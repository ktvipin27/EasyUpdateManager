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

package com.ktvipin.easyupdate.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ktvipin.easyupdate.EasyUpdateManager
import com.ktvipin.easyupdate.UpdateType
import com.ktvipin.easyupdate.logD
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

        textView2.text = getString(R.string.label_version, BuildConfig.VERSION_CODE)
    }

    private fun startFlexibleUpdate() {
        EasyUpdateManager
            .with(this)
            .options {
                updateType = UpdateType.FLEXIBLE
            }
            .snackbar {
                actionText = "Install"
                actionTextColor = ContextCompat.getColor(this@KotlinActivity, R.color.colorAccent)
            }
            .listener {
                logD("listening FlexibleUpdate")
            }
            .startUpdate()
    }

    private fun startImmediateUpdate() {
        EasyUpdateManager
            .with(this)
            .options {
                updateType = UpdateType.IMMEDIATE
            }
            .listener {
                logD("listening ImmediateUpdate")
            }
            .startUpdate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        logD("requestCode = $requestCode, resultCode = $resultCode")
    }
}
