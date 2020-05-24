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

package com.ktvipin27.easyupdate.sample;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ktvipin27.easyupdate.EasyUpdateManager;
import com.ktvipin27.easyupdate.InstallState;
import com.ktvipin27.easyupdate.SnackbarOptions;
import com.ktvipin27.easyupdate.UpdateListener;
import com.ktvipin27.easyupdate.UpdateOptions;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Vipin KT on 21/05/20
 */
public class JavaActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_main);

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
        options.setCustomNotification(true);
        options.setForceUpdateCancellable(true);

        SnackbarOptions snackbarOptions = new SnackbarOptions();
        snackbarOptions.setActionText("RESTART");

        EasyUpdateManager
                .INSTANCE
                .with(this)
                .setListener(new UpdateListener() {
                    @Override
                    public void onStateUpdate(@NotNull InstallState state) {

                    }
                })
                .setOptions(options)
                .setSnackbar(snackbarOptions)
                .startUpdate();
    }

    private void startImmediateUpdate() {
        EasyUpdateManager
                .INSTANCE
                .with(this)
                .startUpdate();
    }
}
