package com.my.mw_music_notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import com.my.mw_music_notification.models.Music;
import com.my.mw_music_notification.services.MusicService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Music> musicList;
    private static final String CHANNEL_ID = "music_channel";
    Button buttonMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicList = getMusicList();

        buttonMusic = findViewById(R.id.btn_music);
        buttonMusic.setOnClickListener(view -> {
            Log.i("app", "onClick");
            Intent service = new Intent(this, MusicService.class);
            service.setAction("Resume");
            startService(service);
        });
    }

    public List<Music> getMusicList(){
        List<Music> music = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            music.add(new Music("Title " + i, "Singer " + i, R.drawable.ic_launcher_foreground));
        }
        return music;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent service = new Intent(this, MusicService.class);
        stopService(service);
    }
}