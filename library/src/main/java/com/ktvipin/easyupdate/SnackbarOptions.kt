/*
 * Copyright 2020 Vipin KT
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

package com.ktvipin.easyupdate

import android.graphics.Color
import com.google.android.material.snackbar.Snackbar

/**
 * A set of customization options for [Snackbar].
 *
 * Created by Vipin KT on 18/05/20
 */
data class SnackbarOptions(
    /**
     * [Snackbar] message
     */
    var text: String = DEFAULT_TEXT,
    /**
     * [Snackbar] message text color
     */
    var textColor: Int = DEFAULT_TEXT_COLOR,
    /**
     * Text of [Snackbar] action
     */
    var actionText: String = DEFAULT_ACTION,
    /**
     * Color [Snackbar] action
     */
    var actionTextColor: Int = DEFAULT_ACTION_COLOR
) {
    companion object {
        internal const val DEFAULT_TEXT = "An update has just been downloaded."
        internal const val DEFAULT_ACTION = "RESTART"
        internal const val DEFAULT_TEXT_COLOR = Color.WHITE
        internal const val DEFAULT_ACTION_COLOR = Color.RED
    }
}