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

package com.ktvipin.easyupdate.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ktvipin.easyupdate.EasyUpdateManager;
import com.ktvipin.easyupdate.InstallState;
import com.ktvipin.easyupdate.UpdateListener;
import com.ktvipin.easyupdate.UpdateOptions;
import com.ktvipin.easyupdate.UpdateType;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Vipin KT on 21/05/20
 */
public class JavaActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSwitch = findViewById(R.id.btn_switch);
        btnSwitch.setText("Switch to Kotlin");
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFlexibleUpdate();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImmediateUpdate();
            }
        });
    }

    private void startFlexibleUpdate() {
        UpdateOptions options = new UpdateOptions();
        options.setUpdateType(UpdateType.FLEXIBLE);

        EasyUpdateManager
                .INSTANCE
                .with(this)
                .setOptions(options)
                .setListener(new UpdateListener() {
                    @Override
                    public void onStateUpdate(@NotNull InstallState state) {
                        Log.d("EasyUpdateManager", "onStateUpdate: " + state.toString());
                    }
                })
                .startUpdate();
    }

    private void startImmediateUpdate() {
        UpdateOptions options = new UpdateOptions();
        options.setCustomNotification(true);
        options.setForceUpdate(true);
        options.setUpdateType(UpdateType.IMMEDIATE);

        EasyUpdateManager
                .INSTANCE
                .with(this)
                .setOptions(options)
                .startUpdate();
    }
}
