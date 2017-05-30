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

import org.radarcns.android.RadarConfiguration;
import org.radarcns.android.device.BaseDeviceState;
import org.radarcns.android.device.DeviceManager;
import org.radarcns.android.device.DeviceService;
import org.radarcns.android.device.DeviceTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.radarcns.android.RadarConfiguration.SOURCE_ID_KEY;

public class ApplicationStatusService extends DeviceService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStatusService.class);
    private ApplicationStatusTopics topics;
    private String sourceId;

    @Override
    public void onCreate() {
        logger.info("Creating Application Status service {}", this);
        super.onCreate();

        topics = ApplicationStatusTopics.getInstance();
    }

    @Override
    protected DeviceManager createDeviceManager() {
        return new ApplicationStatusManager(this, getUserId(), getSourceId(), getDataHandler(), topics);
    }

    @Override
    protected BaseDeviceState getDefaultState() {
        return new ApplicationState();
    }

    @Override
    protected DeviceTopics getTopics() {
        return topics;
    }

    public String getSourceId() {
        if (sourceId == null) {
            sourceId = RadarConfiguration.getOrSetUUID(getApplicationContext(), SOURCE_ID_KEY);
        }
        return sourceId;
    }
}
