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

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.irateam.vkplayer.R;
import com.irateam.vkplayer.api.SimpleCallback;
import com.irateam.vkplayer.api.service.AudioService;
import com.irateam.vkplayer.controllers.ActivityPlayerController;
import com.irateam.vkplayer.controllers.PlayerController;
import com.irateam.vkplayer.models.Audio;
import com.irateam.vkplayer.player.Player;
import com.irateam.vkplayer.receivers.DownloadFinishedReceiver;
import com.irateam.vkplayer.services.DownloadService;
import com.irateam.vkplayer.services.PlayerService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioActivity extends AppCompatActivity implements ServiceConnection {

    private final Player player = Player.getInstance();
    private final EventBus eventBus = EventBus.getDefault();

    private Toolbar toolbar;

    private PlayerController playerController;
    private PlayerService playerService;
    private AudioService audioService = new AudioService(this);
    private DownloadFinishedReceiver downloadFinishedReceiver;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        toolbar = (Toolbar) findViewById(R.id.toolbar_transparent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        playerController = new ActivityPlayerController(this, findViewById(R.id.activity_player_panel));
        playerController.initialize();
        eventBus.register(playerController);
        playerController.setFabOnClickListener(v -> finish());
        downloadFinishedReceiver = new DownloadFinishedReceiver() {
            @Override
            public void onDownloadFinished(Audio audio) {
                setCacheAction(audio.isCached());
                player.getPlayingAudio().setCacheFile(audio.getCacheFile());
            }
        };
        registerReceiver(downloadFinishedReceiver, new IntentFilter(DownloadService.DOWNLOAD_FINISHED));
        startService(new Intent(this, PlayerService.class));
        bindService(new Intent(this, PlayerService.class), this, BIND_AUTO_CREATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_audio, menu);
        Audio audio = player.getPlayingAudio();
        if (audio != null) {
            setCacheAction(audio.isCached());
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
        unregisterReceiver(downloadFinishedReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_cache:
                ArrayList<Audio> list = new ArrayList<>();
                list.add(player.getPlayingAudio());

                Intent intent = new Intent(this, DownloadService.class)
                        .setAction(DownloadService.START_DOWNLOADING)
                        .putExtra(DownloadService.AUDIO_LIST, list);

                startService(intent);
                break;
            case R.id.action_remove_from_cache:
                List<Audio> removeList = Collections.singletonList(player.getPlayingAudio());
                audioService.removeFromCache(removeList).execute(SimpleCallback.success(audios -> {
                    setCacheAction(false);
                }));
                break;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        Log.i("Service", "Connected");
        playerService = ((PlayerService.PlayerBinder) binder).getPlayerService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i("Service", "Disconnected");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_up_close, R.anim.slide_in_up_close);
    }

    public void setCacheAction(boolean isCached) {
        if (menu != null) {
            if (isCached) {
                menu.findItem(R.id.action_remove_from_cache).setVisible(true);
                menu.findItem(R.id.action_cache).setVisible(false);
            } else {
                menu.findItem(R.id.action_remove_from_cache).setVisible(false);
                menu.findItem(R.id.action_cache).setVisible(true);
            }
        }
    }
}
