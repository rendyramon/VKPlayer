/*
 * Copyright (C) 2015 IRA-Team
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

package com.irateam.vkplayer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (VKSdk.isLoggedIn()) {
            new VKAccessTokenTracker() {
                @Override
                public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
                    if (newToken == null) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, ListActivity.class));
                    }
                }
            }.startTracking();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }


}
