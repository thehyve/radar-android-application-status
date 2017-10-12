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
import android.support.annotation.NonNull;
import android.util.Pair;
import org.radarcns.android.data.DataCache;
import org.radarcns.android.device.AbstractDeviceManager;
import org.radarcns.android.device.DeviceStatusListener;
import org.radarcns.android.kafka.ServerStatusListener;
import org.radarcns.android.util.OfflineProcessor;
import org.radarcns.kafka.ObservationKey;
import org.radarcns.monitor.application.ApplicationRecordCounts;
import org.radarcns.monitor.application.ApplicationServerStatus;
import org.radarcns.monitor.application.ApplicationUptime;
import org.radarcns.monitor.application.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Set;

import static org.radarcns.android.device.DeviceService.CACHE_RECORDS_SENT_NUMBER;
import static org.radarcns.android.device.DeviceService.CACHE_RECORDS_UNSENT_NUMBER;
import static org.radarcns.android.device.DeviceService.CACHE_TOPIC;
import static org.radarcns.android.device.DeviceService.SERVER_RECORDS_SENT_NUMBER;
import static org.radarcns.android.device.DeviceService.SERVER_RECORDS_SENT_TOPIC;
import static org.radarcns.android.device.DeviceService.SERVER_STATUS_CHANGED;

public class ApplicationStatusManager
        extends AbstractDeviceManager<ApplicationStatusService, ApplicationState>
        implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStatusManager.class);
    private static final long APPLICATION_UPDATE_INTERVAL_DEFAULT = 300_000L; // milliseconds
    private static final Long NUMBER_UNKNOWN = -1L;
    private static final int APPLICATION_PROCESSOR_REQUEST_CODE = 72553575;
    private static final String APPLICATION_PROCESSOR_REQUEST_NAME = ApplicationStatusManager.class.getName();

    private final DataCache<ObservationKey, ApplicationServerStatus> serverStatusTable;
    private final DataCache<ObservationKey, ApplicationUptime> uptimeTable;
    private final DataCache<ObservationKey, ApplicationRecordCounts> recordCountsTable;

    private final OfflineProcessor processor;
    private final long creationTimeStamp;
    private InetAddress previousInetAddress;

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

    public ApplicationStatusManager(ApplicationStatusService service) {
        super(service);
        ApplicationStatusTopics topics = service.getTopics();
        this.serverStatusTable = getCache(topics.getServerTopic());
        this.uptimeTable = getCache(topics.getUptimeTopic());
        this.recordCountsTable = getCache(topics.getRecordCountsTopic());
        this.processor = new OfflineProcessor(service, this, APPLICATION_PROCESSOR_REQUEST_CODE,
                APPLICATION_PROCESSOR_REQUEST_NAME, APPLICATION_UPDATE_INTERVAL_DEFAULT, false);

        setName(getService().getApplicationContext().getApplicationInfo().processName);

        creationTimeStamp = System.currentTimeMillis();
        previousInetAddress = null;
    }

    @Override
    public void start(@NonNull Set<String> acceptableIds) {
        updateStatus(DeviceStatusListener.Status.READY);

        this.processor.start();

        logger.info("Starting ApplicationStatusManager");
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVER_STATUS_CHANGED);
        filter.addAction(SERVER_RECORDS_SENT_TOPIC);
        filter.addAction(CACHE_TOPIC);
        getService().registerReceiver(serverStatusListener, filter);

        updateStatus(DeviceStatusListener.Status.CONNECTED);
    }

    @Override
    public void run() {
        logger.info("Updating application status");
        try {
            processServerStatus();
            if (processor.isDone()) {
                return;
            }
            processUptime();
            if (processor.isDone()) {
                return;
            }
            processRecordsSent();
        } catch (Exception e) {
            logger.error("Failed to update application status", e);
        }
    }

    public void setApplicationStatusUpdateRate(long period) {
        processor.setInterval(period);
    }

    public void processServerStatus() {
        double time = System.currentTimeMillis() / 1_000d;

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

        ApplicationServerStatus value = new ApplicationServerStatus(time, status, ipAddress);

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
        double time = System.currentTimeMillis() / 1_000d;
        double uptime = (System.currentTimeMillis() - creationTimeStamp)/1000d;
        send(uptimeTable, new ApplicationUptime(time, uptime));
    }

    public void processRecordsSent() {
        double time = System.currentTimeMillis() / 1_000d;

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
        send(recordCountsTable, new ApplicationRecordCounts(time,
                recordsCached, recordsSent, recordsCachedUnsent));
    }

    @Override
    public void close() throws IOException {
        logger.info("Closing ApplicationStatusManager");
        this.processor.close();
        getService().unregisterReceiver(serverStatusListener);
        super.close();
    }

    @Override
    protected void registerDeviceAtReady() {
        // custom registration
    }
}
