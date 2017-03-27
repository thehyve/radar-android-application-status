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

import org.radarcns.android.device.DeviceTopics;
import org.radarcns.key.MeasurementKey;
import org.radarcns.topic.AvroTopic;

public class ApplicationStatusTopics extends DeviceTopics {

    private final AvroTopic<MeasurementKey, ApplicationServerStatus> serverTopic;
    private final AvroTopic<MeasurementKey, ApplicationRecordCounts> recordstopic;
    private final AvroTopic<MeasurementKey, ApplicationUptime> uptimetopic;
    private final AvroTopic<MeasurementKey, ApplicationExternalTime> ntpTopic;

    private static final Object syncObject = new Object();
    private static ApplicationStatusTopics instance = null;

    public static ApplicationStatusTopics getInstance() {
        synchronized (syncObject) {
            if (instance == null) {
                instance = new ApplicationStatusTopics();
            }
            return instance;
        }
    }

    private ApplicationStatusTopics() {
        serverTopic = createTopic("application_server_status",
                ApplicationServerStatus.getClassSchema(),
                ApplicationServerStatus.class);
        recordstopic = createTopic("application_record_counts",
                ApplicationRecordCounts.getClassSchema(),
                ApplicationRecordCounts.class);
        uptimetopic = createTopic("application_uptime",
                ApplicationUptime.getClassSchema(),
                ApplicationUptime.class);
        ntpTopic = createTopic("application_external_time",
                ApplicationExternalTime.getClassSchema(),
                ApplicationExternalTime.class);
    }

    public AvroTopic<MeasurementKey, ApplicationServerStatus> getServerTopic() {
        return serverTopic;
    }

    public AvroTopic<MeasurementKey, ApplicationRecordCounts> getRecordCountsTopic() {
        return recordstopic;
    }

    public AvroTopic<MeasurementKey, ApplicationUptime> getUptimeTopic() {
        return uptimetopic;
    }

    public AvroTopic<MeasurementKey, ApplicationExternalTime> getExternalTimeTopic() {
        return ntpTopic;
    }
}
