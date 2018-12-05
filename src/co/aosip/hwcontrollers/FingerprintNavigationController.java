/*
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

package co.aosip.hwcontrollers;

import android.util.Log;

/*
 * Fingerprint Navigation API
 *
 * A device can enable it's fingerprint navigation methods
 * provided by it's vendor, based on the state of the system's
 * software navigation bar.
 */

public class FingerprintNavigationController {

    /*
     * All HAF classes should export this boolean.
     * Real implementations must, of course, return true
     */
    public static boolean isSupported() {
        return false;
    }

    /*
     * Enable/Disable fingerprint navigation only when navigation
     * bar is disabled. We check that status with [boolean: canUse]
     */
    public static boolean setEnabled(boolean canUse) {
        return false;
    }
}
