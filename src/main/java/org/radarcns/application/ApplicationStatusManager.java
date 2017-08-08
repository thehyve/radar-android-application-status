/*
 * Copyright 2017 The Hyve
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.radarcns.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Pair;
import org.radarcns.android.data.DataCache;
import org.radarcns.android.data.TableDataHandler;
import org.radarcns.android.device.AbstractDeviceManager;
import org.radarcns.android.device.DeviceStatusListener;
import org.radarcns.android.kafka.ServerStatusListener;
import org.radarcns.key.MeasurementKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Set;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static org.radarcns.android.device.DeviceService.CACHE_RECORDS_SENT_NUMBER;
import static org.radarcns.android.device.DeviceService.CACHE_RECORDS_UNSENT_NUMBER;
import static org.radarcns.android.device.DeviceService.CACHE_TOPIC;
import static org.radarcns.android.device.DeviceService.SERVER_RECORDS_SENT_NUMBER;
import static org.radarcns.android.device.DeviceService.SERVER_RECORDS_SENT_TOPIC;
import static org.radarcns.android.device.DeviceService.SERVER_STATUS_CHANGED;

public class ApplicationStatusManager extends AbstractDeviceManager<ApplicationStatusService,
        ApplicationState> implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStatusManager.class);
    private static final Long NUMBER_UNKNOWN = -1L;

    private final DataCache<MeasurementKey, ApplicationServerStatus> serverStatusTable;
    private final DataCache<MeasurementKey, ApplicationUptime> uptimeTable;
    private final DataCache<MeasurementKey, ApplicationExternalTime> ntpTimeTable;
    private final DataCache<MeasurementKey, ApplicationRecordCounts> recordCountsTable;
    private final HandlerThread mHandlerThread;

    private final long creationTimeStamp;
    private final SntpClient sntpClient;
    private String ntpServer;

    private InetAddress previousInetAddress;
    private long updateRate;
    private Handler mHandler;

    private final BroadcastReceiver serverStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SERVER_STATUS_CHANGED)) {
                final ServerStatusListener.Status status = ServerStatusListener.Status.values()[
                        intent.getIntExtra(SERVER_STATUS_CHANGED, 0)];
                getState().setServerStatus(status);
            } else if (intent.getAction().equals(SERVER_RECORDS_SENT_TOPIC)) {
                int numberOfRecordsSent = intent.getIntExtra(SERVER_RECORDS_SENT_NUMBER, 0);
                if (numberOfRecordsSent != -1) {
                    getState().addRecordsSent(numberOfRecordsSent);
                }
            } else if (intent.getAction().equals(CACHE_TOPIC)) {
                String topic = intent.getStringExtra(CACHE_TOPIC);
                Pair<Long, Long> numberOfRecords = new Pair<>(
                        intent.getLongExtra(CACHE_RECORDS_UNSENT_NUMBER, NUMBER_UNKNOWN),
                        intent.getLongExtra(CACHE_RECORDS_SENT_NUMBER, NUMBER_UNKNOWN));
                getState().putCachedRecords(topic, numberOfRecords);
            }
        }
    };

    public ApplicationStatusManager(ApplicationStatusService applicationStatusService,
                                    String userId, String sourceId, TableDataHandler dataHandler,
                                    ApplicationStatusTopics topics, String ntpServer, long updateRate) {
        super(applicationStatusService, new ApplicationState(), dataHandler, userId, sourceId);
        this.serverStatusTable = dataHandler.getCache(topics.getServerTopic());
        this.uptimeTable = dataHandler.getCache(topics.getUptimeTopic());
        this.recordCountsTable = dataHandler.getCache(topics.getRecordCountsTopic());
        this.ntpTimeTable = dataHandler.getCache(topics.getExternalTimeTopic());

        sntpClient = new SntpClient();
        setNtpServer(ntpServer);

        setName(getService().getApplicationContext().getApplicationInfo().processName);
        creationTimeStamp = System.currentTimeMillis();

        mHandlerThread = new HandlerThread("ApplicationStatus",
                THREAD_PRIORITY_BACKGROUND);

        previousInetAddress = null;
        this.updateRate = updateRate;
    }

    @Override
    public void start(@NonNull Set<String> acceptableIds) {
        mHandlerThread.start();
        synchronized (this) {
            mHandler = new Handler(mHandlerThread.getLooper());
        }

        logger.info("Starting ApplicationStatusManager");
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVER_STATUS_CHANGED);
        filter.addAction(SERVER_RECORDS_SENT_TOPIC);
        filter.addAction(CACHE_TOPIC);
        getService().registerReceiver(serverStatusListener, filter);

        schedule();

        updateStatus(DeviceStatusListener.Status.CONNECTED);
    }

    private synchronized void schedule() {
        if (mHandler == null) {
            return;
        }

        mHandler.removeCallbacks(this);
        mHandler.postDelayed(this, updateRate / 3);
        logger.info("App status updater: listener activated and set to a period of {}", updateRate);
    }

    public final synchronized void setNtpServer(String server) {
        if (server == null || server.trim().isEmpty()) {
            this.ntpServer = null;
        } else {
            this.ntpServer = server.trim();
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            if (mHandler == null) {
                return;
            }
        }
        logger.info("Updating application status");
        try {
            processServerStatus();
            processUptime();
            processRecordsSent();
            processReferenceTime();
        } catch (Exception e) {
            logger.error("Failed to update application status", e);
        }
        synchronized (this) {
            if (mHandler != null) {
                mHandler.postDelayed(this, updateRate);
            }
        }
    }

    public final synchronized void setApplicationStatusUpdateRate(final long period) {
        if (period == updateRate) {
            return;
        }
        updateRate = period;
        schedule();
    }

    public void processReferenceTime() {
        String localServer;
        synchronized (this) {
            localServer = ntpServer;
        }
        if (localServer != null) {
            if (sntpClient.requestTime(localServer, 5000)) {
                double delay = sntpClient.getRoundTripTime() / 1000d;
                double time = System.currentTimeMillis() / 1000d;
                double ntpTime =  (sntpClient.getNtpTime() + SystemClock.elapsedRealtime()
                        - sntpClient.getNtpTimeReference()) / 1000d;

                send(ntpTimeTable, new ApplicationExternalTime(time, time, ntpTime,
                        localServer, ExternalTimeProtocol.SNTP, delay));
            }
        }
    }

    public void processServerStatus() {
        double timeReceived = System.currentTimeMillis() / 1_000d;

        ServerStatus status;
        switch (getState().getServerStatus()) {
            case CONNECTED:
            case READY:
            case UPLOADING:
                status = ServerStatus.CONNECTED;
                break;
            case DISCONNECTED:
            case DISABLED:
            case UPLOADING_FAILED:
                status = ServerStatus.DISCONNECTED;
                break;
            default:
                status = ServerStatus.UNKNOWN;
        }
        String ipAddress = getIpAddress();
        logger.info("Server Status: {}; Device IP: {}", status, ipAddress);

        ApplicationServerStatus value = new ApplicationServerStatus(timeReceived, timeReceived, status, ipAddress);

        send(serverStatusTable, value);
    }

    private String getIpAddress() {
        // Find Ip via NetworkInterfaces. Works via wifi, ethernet and mobile network
        try {
            if (previousInetAddress == null ||
                    NetworkInterface.getByInetAddress(previousInetAddress) == null) {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                            // This finds both xx.xx.xx ip and rmnet. Last one is always ip.
                            previousInetAddress = inetAddress;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            logger.warn("No IP Address could be determined", ex);
            previousInetAddress = null;
        }
        if (previousInetAddress == null) {
            return null;
        } else {
            return previousInetAddress.getHostAddress();
        }
    }

    public void processUptime() {
        double timeReceived = System.currentTimeMillis() / 1_000d;
        double uptime = (System.currentTimeMillis() - creationTimeStamp)/1000d;
        send(uptimeTable, new ApplicationUptime(timeReceived, timeReceived, uptime));
    }

    public void processRecordsSent() {
        double timeReceived = System.currentTimeMillis() / 1_000d;

        int recordsCachedUnsent = 0;
        int recordsCachedSent = 0;

        for (Pair<Long, Long> records : getState().getCachedRecords().values()) {
            if (!records.first.equals(NUMBER_UNKNOWN)) {
                recordsCachedUnsent += records.first.intValue();
            }
            if (!records.second.equals(NUMBER_UNKNOWN)) {
                recordsCachedSent += records.second.intValue();
            }
        }
        int recordsCached = recordsCachedUnsent + recordsCachedSent;
        int recordsSent = getState().getRecordsSent();

        logger.info("Number of records: {sent: {}, unsent: {}, cached: {}}",
                recordsSent, recordsCachedUnsent, recordsCached);
        send(recordCountsTable, new ApplicationRecordCounts(timeReceived, timeReceived,
                recordsCached, recordsSent, recordsCachedUnsent));
    }

    @Override
    public void close() throws IOException {
        logger.info("Closing ApplicationStatusManager");
        synchronized (this) {
            mHandler = null;
        }
        mHandlerThread.quitSafely();
        getService().unregisterReceiver(serverStatusListener);
        super.close();
    }
}
