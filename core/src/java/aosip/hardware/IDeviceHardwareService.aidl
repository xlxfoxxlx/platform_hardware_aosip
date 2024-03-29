/**
 * Copyright (c) 2015-2016 The CyanogenMod Project
 *               2017-2018 The LineageOS Project
 *               2018 CypherOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aosip.hardware;

import android.view.KeyEvent;

import aosip.hardware.DisplayMode;
import aosip.hardware.TouchscreenGesture;

/** @hide */
interface IDeviceHardwareService {

    int getSupportedFeatures();
    boolean get(int feature);
    boolean set(int feature, boolean enable);

    DisplayMode[] getDisplayModes();
    DisplayMode getCurrentDisplayMode();
    DisplayMode getDefaultDisplayMode();
    boolean setDisplayMode(in DisplayMode mode, boolean makeDefault);

    boolean setFingerprintNavigation(in boolean canUse);

    boolean triStateReady();
    KeyEvent handleTriStateEvent(in KeyEvent event);

    TouchscreenGesture[] getTouchscreenGestures();
    boolean setTouchscreenGestureEnabled(in TouchscreenGesture gesture, boolean state);
}
