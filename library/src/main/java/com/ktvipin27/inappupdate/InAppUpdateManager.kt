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

package com.ktvipin27.inappupdate

import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

/**
 * A wrapper for Android In-App Update API.
 * (https://developer.android.com/guide/playcore/in-app-updates)
 *
 * Created by Vipin KT on 29/04/20
 */
object InAppUpdateManager {

    internal const val REQ_CODE_APP_UPDATE = 54321

    fun with(activity: AppCompatActivity) = InAppUpdateManagerImpl(WeakReference(activity))
}