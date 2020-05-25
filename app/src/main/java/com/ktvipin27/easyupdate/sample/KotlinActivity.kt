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

package com.ktvipin27.easyupdate.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktvipin27.easyupdate.EasyUpdateManager
import com.ktvipin27.easyupdate.InstallState
import com.ktvipin27.easyupdate.UpdateListener
import com.ktvipin27.easyupdate.UpdateType
import kotlinx.android.synthetic.main.activity_main.*

class KotlinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_switch.text = "Switch to Java"
        btn_switch.setOnClickListener {
            startActivity(Intent(this, JavaActivity::class.java))
        }

        button1.setOnClickListener {
            startFlexibleUpdate()
        }

        button2.setOnClickListener {
            startImmediateUpdate()
        }
    }

    private fun startFlexibleUpdate() {
        EasyUpdateManager
            .with(this)
            .startUpdate()
    }

    private fun startImmediateUpdate() {
        EasyUpdateManager
            .with(this)
            .options {
                updateType = UpdateType.IMMEDIATE
            }
            .snackbar {

            }
            .listener {

            }
            .setListener(object : UpdateListener {
                override fun onStateUpdate(state: InstallState) {

                }
            })
            .startUpdate()
    }
}