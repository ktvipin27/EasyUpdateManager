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

/**
 * Install updates will be delivered through this interface.
 *
 * Created by Vipin KT on 24/05/20
 */
interface UpdateListener {
    /**
     * will be triggered when a state update occurred.
     *
     * @param state [InstallState]
     */
    fun onStateUpdate(state: InstallState)
}