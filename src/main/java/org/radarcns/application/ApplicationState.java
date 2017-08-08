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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import org.radarcns.android.device.BaseDeviceState;
import org.radarcns.android.device.DeviceStateCreator;
import org.radarcns.android.kafka.ServerStatusListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ApplicationState extends BaseDeviceState {
    private ServerStatusListener.Status serverStatus;
    private final AtomicInteger recordsSent = new AtomicInteger(0);
    private final Map<String, Pair<Long, Long>> cachedRecords = new ConcurrentHashMap<>();

    public static final Parcelable.Creator<ApplicationState> CREATOR = new DeviceStateCreator<>(ApplicationState.class);

    public void updateFromParcel(Parcel in) {
        super.updateFromParcel(in);
        serverStatus = ServerStatusListener.Status.values()[in.readInt()];
        recordsSent.set(in.readInt());
        cachedRecords.clear();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            cachedRecords.put(in.readString(), new Pair<>(in.readLong(), in.readLong()));
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(getServerStatus().ordinal());
        dest.writeInt(getRecordsSent());
        dest.writeInt(cachedRecords.size());
        for (Map.Entry<String, Pair<Long, Long>> record : cachedRecords.entrySet()) {
            dest.writeString(record.getKey());
            dest.writeLong(record.getValue().first);
            dest.writeLong(record.getValue().second);
        }
    }

    public synchronized void setServerStatus(ServerStatusListener.Status status) {
        serverStatus = status;
    }

    public void addRecordsSent(int nRecords) {
        recordsSent.addAndGet(nRecords);
    }

    public void putCachedRecords(String topic, Pair<Long, Long> numberOfRecords) {
        cachedRecords.put(topic, numberOfRecords);
    }

    public Map<String, Pair<Long, Long>> getCachedRecords() {
        return cachedRecords;
    }

    public synchronized ServerStatusListener.Status getServerStatus() {
        if (serverStatus == null) {
            return ServerStatusListener.Status.DISCONNECTED;
        }
        return serverStatus;
    }

    public int getRecordsSent() {
        return recordsSent.get();
    }
}
