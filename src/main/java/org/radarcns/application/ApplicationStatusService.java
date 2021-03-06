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

import android.os.Bundle;
import org.radarcns.android.RadarConfiguration;
import org.radarcns.android.device.DeviceManager;
import org.radarcns.android.device.DeviceService;

import static org.radarcns.android.RadarConfiguration.SOURCE_ID_KEY;
import static org.radarcns.application.ApplicationServiceProvider.NTP_SERVER_KEY;
import static org.radarcns.application.ApplicationServiceProvider.UPDATE_RATE_KEY;

public class ApplicationStatusService extends DeviceService<ApplicationState> {
    private String sourceId;
    private String ntpServer;
    private long updateRate;

    @Override
    protected DeviceManager createDeviceManager() {
        if (sourceId == null) {
            sourceId = RadarConfiguration.getOrSetUUID(getApplicationContext(), SOURCE_ID_KEY);
        }
        ApplicationStatusManager manager = new ApplicationStatusManager(
                this, ntpServer, updateRate);
        manager.setApplicationStatusUpdateRate(updateRate);
        return manager;
    }

    @Override
    protected ApplicationState getDefaultState() {
        return new ApplicationState();
    }

    @Override
    protected void onInvocation(Bundle bundle) {
        super.onInvocation(bundle);
        updateRate = bundle.getLong(UPDATE_RATE_KEY) * 1000L;
        ntpServer = bundle.getString(NTP_SERVER_KEY);
        ApplicationStatusManager manager = (ApplicationStatusManager)getDeviceManager();
        if (manager != null) {
            manager.setApplicationStatusUpdateRate(updateRate);
            manager.setNtpServer(ntpServer);
        }
    }
}
