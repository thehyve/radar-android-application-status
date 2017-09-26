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
import org.radarcns.kafka.ObservationKey;
import org.radarcns.monitor.application.ApplicationRecordCounts;
import org.radarcns.monitor.application.ApplicationServerStatus;
import org.radarcns.monitor.application.ApplicationUptime;
import org.radarcns.topic.AvroTopic;

public class ApplicationStatusTopics extends DeviceTopics {

    private final AvroTopic<ObservationKey, ApplicationServerStatus> serverTopic;
    private final AvroTopic<ObservationKey, ApplicationRecordCounts> recordstopic;
    private final AvroTopic<ObservationKey, ApplicationUptime> uptimetopic;

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
    }

    public AvroTopic<ObservationKey, ApplicationServerStatus> getServerTopic() {
        return serverTopic;
    }

    public AvroTopic<ObservationKey, ApplicationRecordCounts> getRecordCountsTopic() {
        return recordstopic;
    }

    public AvroTopic<ObservationKey, ApplicationUptime> getUptimeTopic() {
        return uptimetopic;
    }

}
