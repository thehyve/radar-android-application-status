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
import org.radarcns.android.device.DeviceManager;
import org.radarcns.android.device.DeviceService;

import static org.radarcns.application.ApplicationServiceProvider.UPDATE_RATE_KEY;

public class ApplicationStatusService extends DeviceService<ApplicationState> {
    private long updateRate;

    @Override
    protected ApplicationStatusManager createDeviceManager() {
        ApplicationStatusManager manager = new ApplicationStatusManager(this);
        manager.setApplicationStatusUpdateRate(updateRate);
        return manager;
    }

    @Override
    protected ApplicationState getDefaultState() {
        return new ApplicationState();
    }

    @Override
    protected ApplicationStatusTopics getTopics() {
        return ApplicationStatusTopics.getInstance();
    }

    @Override
    protected void onInvocation(Bundle bundle) {
        super.onInvocation(bundle);
        updateRate = bundle.getLong(UPDATE_RATE_KEY) * 1000L;
        DeviceManager manager = getDeviceManager();
        if (manager != null) {
            ((ApplicationStatusManager)manager).setApplicationStatusUpdateRate(updateRate);
        }
    }
}
