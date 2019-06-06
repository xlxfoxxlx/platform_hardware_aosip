/*
 * Copyright (C) 2019 CypherOS
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

import android.content.Context;
import android.view.KeyEvent;

/*
 * Alert Slider API
 *
 * A device can enable utilize it's tri-state key events
 * provided by it's assigned keycode code. User's can swap
 * between zen modes via Alert Slider when this is in use.
 */

public class AlertSliderController {

    /*
     * All HAF classes should export this boolean.
     * Real implementations must, of course, return true
     */
    public static boolean isSupported() {
        return false;
    }

    /*
     * Notifies the handler that the tri-state handler
     * can be used after systemReady() is called
     */
    public static boolean triStateReady(Context context) {
        return false;
    }

    /*
     * The primary handler that changes zen mode based
     * on the trigger tri-state keycode
     */
    public static KeyEvent handleTriStateEvent(KeyEvent event) {
        return null;
    }
}
