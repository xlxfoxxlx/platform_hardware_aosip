/*
 * Copyright (C) 2015 The CyanogenMod Project
 * Copyright (C) 2018 CypherOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aosip.content;

import android.Manifest;

/**
 * @hide
 * CypherOS Hardware Context Class.
 */
public final class HardwareContext {

    /**
     * @hide
     */
    private HardwareContext() {
        // Empty constructor
    }

    /**
     * Use with {@link android.content.Context#getSystemService} to retrieve a
     * {@link aosip.hardware.DeviceHardwareManager} to manage the extended
     * hardware features of the device.
     *
     * @see android.content.Context#getSystemService
     * @see aosip.hardware.DeviceHardwareManager
     *
     * @hide
     */
    public static final String DEVICE_HARDWARE_SERVICE = "devicehardware";

    /**
     * Hardware Features.
     */
    public static class Features {

        /**
         * Feature for {@link PackageManager#getSystemAvailableFeatures} and
         * {@link PackageManager#hasSystemFeature}: The device includes the
         * hardware feature framework service.
         */
        public static final String HARDWARE_AOSIP = "hardware.aosip";

    }
}
