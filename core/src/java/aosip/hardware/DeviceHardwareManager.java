/*
 * Copyright (C) 2015-2016 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
 *               2018-2019 CypherOS
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
package aosip.hardware;

import android.content.Context;
import android.hidl.base.V1_0.IBase;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;

import aosip.content.HardwareContext;

import com.android.internal.annotations.VisibleForTesting;

import java.lang.IllegalArgumentException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import vendor.aosip.displayengine.V1_0.IDisplayModes;
import vendor.aosip.touch.V1_0.IFingerprintNavigation;
import vendor.aosip.touch.V1_0.ITouchscreenGesture;

/**
 * Manages access to device hardware extensions
 *
 *  <p>
 *  This manager requires the DEVICE_HARDWARE_ACCESS permission.
 *  <p>
 *  To get the instance of this class, utilize DeviceHardwareManager#getInstance(Context context)
 */
public final class DeviceHardwareManager {

    private static final String TAG = "DeviceHardwareManager";

    private final ArrayMap<String, String> mDisplayModeMappings = new ArrayMap<String, String>();
    private final boolean mFilterDisplayModes;

    private Context mContext;
    private static IDeviceHardwareService sService;
    private static DeviceHardwareManager sDeviceHardwareManagerInstance;

    // HIDL hals
    private HashMap<Integer, IBase> mHIDLMap = new HashMap<Integer, IBase>();

    /**
     * DisplayEngine (DisplayModes)
     */
    @VisibleForTesting
    public static final int FEATURE_DISPLAY_MODES = 0x1;

    /**
     * Fingerprint Navigation
     */
    @VisibleForTesting
    public static final int FEATURE_FINGERPRINT_NAVIGATION = 0x2;

    /**
     * Alert Slider
     */
    @VisibleForTesting
    public static final int FEATURE_ALERT_SLIDER = 0x3;

    /**
     * Touchscreen gesture
     */
    @VisibleForTesting
    public static final int FEATURE_TOUCHSCREEN_GESTURES = 0x4;

    private static final List<Integer> BOOLEAN_FEATURES = Arrays.asList(
        FEATURE_FINGERPRINT_NAVIGATION
    );

    /**
     * @hide to prevent subclassing from outside of the framework
     */
    private DeviceHardwareManager(Context context) {
        Context appContext = context.getApplicationContext();
        if (appContext != null) {
            mContext = appContext;
        } else {
            mContext = context;
        }
        sService = getService();

        if (context.getPackageManager().hasSystemFeature(
                HardwareContext.Features.HARDWARE_AOSIP) && !checkService()) {
            Log.wtf(TAG, "Unable to get DeviceHardwareService. The service either" +
                    " crashed, was not started, or the interface has been called too early in" +
                    " SystemServer init");
        }

        final String[] mappings = mContext.getResources().getStringArray(
                com.android.internal.R.array.config_displayModeMappings);
        if (mappings != null && mappings.length > 0) {
            for (String mapping : mappings) {
                String[] split = mapping.split(":");
                if (split.length == 2) {
                    mDisplayModeMappings.put(split[0], split[1]);
                }
            }
        }
        mFilterDisplayModes = mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_filterDisplayModes);
    }

    /**
     * Get or create an instance of the {@link aosip.hardware.DeviceHardwareManager}
     * @param context
     * @return {@link DeviceHardwareManager}
     */
    public static DeviceHardwareManager getInstance(Context context) {
        if (sDeviceHardwareManagerInstance == null) {
            sDeviceHardwareManagerInstance = new DeviceHardwareManager(context);
        }
        return sDeviceHardwareManagerInstance;
    }

    /** @hide */
    public static IDeviceHardwareService getService() {
        if (sService != null) {
            return sService;
        }
        IBinder b = ServiceManager.getService(HardwareContext.DEVICE_HARDWARE_SERVICE);
        if (b != null) {
            sService = IDeviceHardwareService.Stub.asInterface(b);
            return sService;
        }
        return null;
    }

    /**
     * Determine if a Device Hardware feature is supported
     *
     * @param feature The Device Hardware feature to query
     *
     * @return true if the feature is supported, false otherwise.
     */
    public boolean isSupported(int feature) {
        return isSupportedHIDL(feature) || isSupportedLegacy(feature);
    }

    private boolean isSupportedHIDL(int feature) {
        if (!mHIDLMap.containsKey(feature)) {
            mHIDLMap.put(feature, getHIDLService(feature));
        }
        return mHIDLMap.get(feature) != null;
    }

    private boolean isSupportedLegacy(int feature) {
        try {
            if (checkService()) {
                return feature == (sService.getSupportedFeatures() & feature);
            }
        } catch (RemoteException e) {
        }
        return false;
    }

    /**
     * String version for preference constraints
     *
     * @hide
     */
    public boolean isSupported(String feature) {
        if (!feature.startsWith("FEATURE_")) {
            return false;
        }
        try {
            Field f = getClass().getField(feature);
            if (f != null) {
                return isSupported((int) f.get(null));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.d(TAG, e.getMessage(), e);
        }

        return false;
    }

    private IBase getHIDLService(int feature) {
        try {
            switch (feature) {
                case FEATURE_FINGERPRINT_NAVIGATION:
                    return IFingerprintNavigation.getService(true);
                case FEATURE_DISPLAY_MODES:
                    return IDisplayModes.getService(true);
                case FEATURE_TOUCHSCREEN_GESTURES:
                    return ITouchscreenGesture.getService(true);
            }
        } catch (NoSuchElementException | RemoteException e) {
        }
        return null;
    }

    /**
     * Determine if the given feature is enabled or disabled.
     *
     * Only used for features which have simple enable/disable controls.
     *
     * @param feature the Device Hardware feature to query
     *
     * @return true if the feature is enabled, false otherwise.
     */
    public boolean get(int feature) {
        if (!BOOLEAN_FEATURES.contains(feature)) {
            throw new IllegalArgumentException(feature + " is not a boolean");
        }

        try {
            if (isSupportedHIDL(feature)) {
                IBase obj = mHIDLMap.get(feature);
                switch (feature) {
                    case FEATURE_FINGERPRINT_NAVIGATION:
                        IFingerprintNavigation fingerprintNav = (IFingerprintNavigation) obj;
                        return fingerprintNav.isSupported();
                }
            } else if (checkService()) {
                return sService.get(feature);
            }
        } catch (RemoteException e) {
        }
        return false;
    }

    /**
     * Enable or disable the given feature
     *
     * Only used for features which have simple enable/disable controls.
     *
     * @param feature the Device Hardware feature to set
     * @param enable true to enable, false to disale
     *
     * @return true if the feature is enabled, false otherwise.
     */
    public boolean set(int feature, boolean enable) {
        if (!BOOLEAN_FEATURES.contains(feature)) {
            throw new IllegalArgumentException(feature + " is not a boolean");
        }

        try {
            if (isSupportedHIDL(feature)) {
                IBase obj = mHIDLMap.get(feature);
                switch (feature) {
                    case FEATURE_FINGERPRINT_NAVIGATION:
                        IFingerprintNavigation fingerprintNav = (IFingerprintNavigation) obj;
                        return fingerprintNav.setEnabled(enable);
                }
            } else if (checkService()) {
                return sService.set(feature, enable);
            }
        } catch (RemoteException e) {
        }
        return false;
    }

    /**
     * @return a list of available display modes on the devices
     */
    public DisplayMode[] getDisplayModes() {
        DisplayMode[] modes = null;
        try {
            if (isSupportedHIDL(FEATURE_DISPLAY_MODES)) {
                IDisplayModes dm = (IDisplayModes) mHIDLMap.get(FEATURE_DISPLAY_MODES);
                modes = HIDLHelper.fromHIDLModes(dm.getDisplayModes());
            } else if (checkService()) {
                modes = sService.getDisplayModes();
            }
        } catch (RemoteException e) {
        } finally {
            if (modes == null) {
                return null;
            }
            final ArrayList<DisplayMode> remapped = new ArrayList<DisplayMode>();
            for (DisplayMode mode : modes) {
                DisplayMode r = remapDisplayMode(mode);
                if (r != null) {
                    remapped.add(r);
                }
            }
            return remapped.toArray(new DisplayMode[0]);
        }
    }

    /**
     * @return the currently active display mode
     */
    public DisplayMode getCurrentDisplayMode() {
        DisplayMode mode = null;
        try {
            if (isSupportedHIDL(FEATURE_DISPLAY_MODES)) {
                IDisplayModes dm = (IDisplayModes) mHIDLMap.get(FEATURE_DISPLAY_MODES);
                mode = HIDLHelper.fromHIDLMode(dm.getCurrentDisplayMode());
            } else if (checkService()) {
                mode = sService.getCurrentDisplayMode();
            }
        } catch (RemoteException e) {
        } finally {
            return mode != null ? remapDisplayMode(mode) : null;
        }
    }

    /**
     * @return the default display mode to be set on boot
     */
    public DisplayMode getDefaultDisplayMode() {
        DisplayMode mode = null;
        try {
            if (isSupportedHIDL(FEATURE_DISPLAY_MODES)) {
                IDisplayModes dm = (IDisplayModes) mHIDLMap.get(FEATURE_DISPLAY_MODES);
                mode = HIDLHelper.fromHIDLMode(dm.getDefaultDisplayMode());
            } else if (checkService()) {
                mode = sService.getDefaultDisplayMode();
            }
        } catch (RemoteException e) {
        } finally {
            return mode != null ? remapDisplayMode(mode) : null;
        }
    }

    /**
     * @return true if setting the mode was successful
     */
    public boolean setDisplayMode(DisplayMode mode, boolean makeDefault) {
        try {
            if (isSupportedHIDL(FEATURE_DISPLAY_MODES)) {
                IDisplayModes dm = (IDisplayModes)
                        mHIDLMap.get(FEATURE_DISPLAY_MODES);
                return dm.setDisplayMode(mode.id, makeDefault);
            } else if (checkService()) {
                return sService.setDisplayMode(mode, makeDefault);
            }
        } catch (RemoteException e) {
        }
        return false;
    }

    private DisplayMode remapDisplayMode(DisplayMode dm) {
        if (dm == null) {
            return null;
        }
        if (mDisplayModeMappings.containsKey(dm.name)) {
            return new DisplayMode(dm.id, mDisplayModeMappings.get(dm.name));
        }
        if (!mFilterDisplayModes) {
            return dm;
        }
        return null;
    }

    /**
     * @return the status of the fingerprint navigation
     */
    public boolean setFingerprintNavigation(boolean canUse) {
        try {
            if (isSupportedHIDL(FEATURE_FINGERPRINT_NAVIGATION)) {
                IFingerprintNavigation fingerprintNav = (IFingerprintNavigation)
                        mHIDLMap.get(FEATURE_FINGERPRINT_NAVIGATION);
                return fingerprintNav.setEnabled(canUse);
            } else if (checkService()) {
                return sService.setFingerprintNavigation(canUse);
            }
        } catch (RemoteException e) {
        }
        return false;
    }

    /**
     * @return notifies that the alert slider can be used
     */
    public boolean triStateReady() {
        try {
            if (checkService()) {
                return sService.triStateReady();
            }
        } catch (RemoteException e) {
        }
        return false;
    }

    /**
     * @return handles the key event of the alert slider
     */
    public KeyEvent handleTriStateEvent(KeyEvent event) {
        try {
            if (checkService()) {
                return sService.handleTriStateEvent(event);
            }
        } catch (RemoteException e) {
        }
        return null;
    }

    /**
     * @return a list of available touchscreen gestures on the devices
     */
    public TouchscreenGesture[] getTouchscreenGestures() {
        try {
            if (isSupportedHIDL(FEATURE_TOUCHSCREEN_GESTURES)) {
                ITouchscreenGesture touchscreenGesture = (ITouchscreenGesture)
                        mHIDLMap.get(FEATURE_TOUCHSCREEN_GESTURES);
                return HIDLHelper.fromHIDLGestures(touchscreenGesture.getSupportedGestures());
            } else if (checkService()) {
                return sService.getTouchscreenGestures();
            }
        } catch (RemoteException e) {
        }
        return null;
    }

    /**
     * @return true if setting the activation status was successful
     */
    public boolean setTouchscreenGestureEnabled(
            TouchscreenGesture gesture, boolean state) {
        try {
            if (isSupportedHIDL(FEATURE_TOUCHSCREEN_GESTURES)) {
                ITouchscreenGesture touchscreenGesture = (ITouchscreenGesture)
                        mHIDLMap.get(FEATURE_TOUCHSCREEN_GESTURES);
                return touchscreenGesture.setGestureEnabled(
                        HIDLHelper.toHIDLGesture(gesture), state);
            } else if (checkService()) {
                return sService.setTouchscreenGestureEnabled(gesture, state);
            }
        } catch (RemoteException e) {
        }
        return false;
    }

    /**
     * @return true if service is valid
     */
    private boolean checkService() {
        if (sService == null) {
            Log.w(TAG, "not connected to DeviceHardwareManagerService");
            return false;
        }
        return true;
    }
}
