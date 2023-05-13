package com.my.mw_music_notification.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.my.mw_music_notification.MainActivity;
import com.my.mw_music_notification.R;
import com.my.mw_music_notification.models.Music;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicService extends Service {

    private List<Music> musicList;
    private int selectedMusicId = -1;
    private Notification notification;
    private boolean isResume = false;
    private static final String CHANNEL_ID = "music_channel";
    private static final int NOTIFICATION_CODE = 435;
    public static final String ACTION_PAUSE = "com.example.music.PAUSE";
    public static final String ACTION_RESUME = "com.example.music.RESUME";
    public static final String ACTION_PREV = "com.example.music.PREV";
    public static final String ACTION_NEXT = "com.example.music.NEXT";
    public static final String ACTION_STOP = "com.example.music.STOP";
    public static final String ACTION_FAV = "com.example.music.FAV";
    public static final String ACTION_UNFAV = "com.example.music.UNFAV";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("app", "work");

        musicList = getMusicList();

        notification = createNotification();

        startForeground(NOTIFICATION_CODE, notification);
        stopForeground (true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();
        Log.i("app", "onStartCommand");
        Log.i("app", "" + action.equals(ACTION_RESUME));
        Log.i("app", "" + action);

        if (selectedMusicId == -1){
            return START_NOT_STICKY;
        }

        if (action.equals(ACTION_RESUME)){
            isResume = false;
        } else if (action.equals(ACTION_STOP)) {
            isResume = true;
        } else if (action.equals(ACTION_PREV)) {
            if (selectedMusicId == 0){
                selectedMusicId = musicList.size()-1;
            } else {
                selectedMusicId--;
            }
        } else if (action.equals(ACTION_NEXT)) {
            if (selectedMusicId == musicList.size()-1){
                selectedMusicId = 0;
            } else {
                selectedMusicId++;
            }
        } else if (action.equals(ACTION_FAV)){
            musicList.get(selectedMusicId).setFavorite(true);
        } else if (action.equals(ACTION_UNFAV)) {
            musicList.get(selectedMusicId).setFavorite(false);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        PendingIntent mainAction = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        RemoteViews remoteViews = getView(musicList.get(selectedMusicId), isResume);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(mainAction)
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setContent(remoteViews)
                .setPriority(Notification.PRIORITY_LOW)
                .setAutoCancel(true)
                .setSound(null);

        Notification notification = builder.build();

        notificationManager.notify(NOTIFICATION_CODE, notification);

        return START_NOT_STICKY;
    }

    public Notification createNotification(){
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.deleteNotificationChannel(CHANNEL_ID);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Music channel", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Mw channel");
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        }
        else {
            builder = new NotificationCompat.Builder(context);
        }

        RemoteViews remoteViews;
        if (musicList.size() > 0){
            selectedMusicId = 0;
            remoteViews = getView(musicList.get(0), isResume);
        }
        else{
            remoteViews = getView(new Music("Song name", "Singer", R.mipmap.ic_launcher), isResume);
        }

        PendingIntent mainAction = PendingIntent.getActivity(this, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(mainAction)
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setContent(remoteViews)
                .setPriority(Notification.PRIORITY_LOW)
                .setAutoCancel(true)
                .setSound(null);

        builder.setContentIntent(mainAction);

        Notification notification = builder.build();

        return notification;
    }

    public RemoteViews getView(Music music, boolean isResume){
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.music_big);

        if (selectedMusicId == -1){
            return remoteViews;
        }

        Intent prevIntent = new Intent(this, MusicService.class);
        prevIntent.setAction(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getService(this,
                0,
                prevIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent playIntent;
        if (isResume){
            playIntent = new Intent(this, MusicService.class);
            playIntent.setAction(ACTION_RESUME);
            remoteViews.setImageViewResource(R.id.btn_play, R.drawable.baseline_play_arrow_24);
        } else {
            playIntent = new Intent(this, MusicService.class);
            playIntent.setAction(ACTION_STOP);
            remoteViews.setImageViewResource(R.id.btn_play, R.drawable.baseline_pause_circle_outline_24);
        }
        PendingIntent resumePendingIntent = PendingIntent.getService(this,
                0,
                playIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(this,
                0,
                nextIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent favIntent;
        if (music.isFavorite()){
            favIntent = new Intent(this, MusicService.class);
            favIntent.setAction(ACTION_UNFAV);
            remoteViews.setImageViewResource(R.id.btn_fav, R.drawable.baseline_favorite_24);
        }
        else {
            favIntent = new Intent(this, MusicService.class);
            favIntent.setAction(ACTION_FAV);
            remoteViews.setImageViewResource(R.id.btn_fav, R.drawable.baseline_favorite_border_24);
        }

        PendingIntent favPendingIntent = PendingIntent.getService(this,
                0,
                favIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        remoteViews.setTextViewText(R.id.text_music_name, music.getTitle());
        remoteViews.setTextViewText(R.id.text_music_singer, music.getSinger());
        remoteViews.setImageViewResource(R.id.image_music, music.getImageId());

        remoteViews.setOnClickPendingIntent(R.id.btn_prev, prevPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.btn_play, resumePendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.btn_next, nextPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.btn_fav, favPendingIntent);

        return remoteViews;
    }

    public List<Music> getMusicList(){
        List<Integer> images = new ArrayList<>(Arrays.asList(R.drawable.music_picture,
                R.drawable.cat_picture,
                R.drawable.cat_programmer_picture));
        List<Music> music = new ArrayList<>();
        for (int i = 0, j = 0; i < 10; i++, j++){
            if (j == images.size()){
                j = 0;
            }
            music.add(new Music("Title " + i, "Singer " + i, images.get(j)));
        }
        return music;
    }
}
