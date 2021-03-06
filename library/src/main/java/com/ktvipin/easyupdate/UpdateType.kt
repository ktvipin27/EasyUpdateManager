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

import com.google.android.play.core.install.model.AppUpdateType

/**
 * Identifiers for the different types of developer triggered updates.
 *
 * Created by Vipin KT on 07/05/20
 */
enum class UpdateType(val value: Int) {
    /**
     * background download and installation
     */
    FLEXIBLE(AppUpdateType.FLEXIBLE),

    /**
     * update is critical for continued use of the app
     */
    IMMEDIATE(AppUpdateType.IMMEDIATE)
}