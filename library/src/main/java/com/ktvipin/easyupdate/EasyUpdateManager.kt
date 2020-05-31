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

import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

/**
 * A wrapper for Android In-App Update API.
 * (https://developer.android.com/guide/playcore/in-app-updates)
 *
 * Created by Vipin KT on 29/04/20
 */
object EasyUpdateManager {

    /**
     * Log tag.
     */
    internal const val TAG = "EasyUpdateManager"

    /**
     * Request code for UpdateFlow.
     */
    internal const val REQ_CODE_APP_UPDATE = 54321

    /**
     * Creates an instance of [UpdateManager].
     *
     * @param activity Reference of the activity from where the [EasyUpdateManager] is called.
     * @return An instance of [UpdateManager].
     */
    fun with(activity: FragmentActivity) = UpdateManager.getInstance(WeakReference(activity))
}