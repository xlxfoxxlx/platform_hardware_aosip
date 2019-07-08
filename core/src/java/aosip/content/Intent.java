/*
 * Copyright (C) 2015 The CyanogenMod Project
 * Copyright (C) 2018 CypherOS
 * This code has been modified.  Portions copyright (C) 2010, T-Mobile USA, Inc.
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
 * aosip/hw Intent Class.
 */
public class Intent {

    /**
     * Broadcast action: Perform any initialization required for aosip hardware services.
     * Runs when the service receives the signal the device has booted, but
     * should happen before {@link android.content.Intent#ACTION_BOOT_COMPLETED}.
     *
     * Requires {@link android.Manifest.permission#DEVICE_HARDWARE_ACCESS}.
     * @hide
     */
    public static final String ACTION_INITIALIZE_DEVICE_HARDWARE =
            "android.intent.action.INITIALIZE_DEVICE_HARDWARE";
}
