package com.example.mymusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

/**
 * Created by asus-pc on 2017/5/13.
 */

public class MyService extends Service implements MediaPlayer.OnCompletionListener{

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private boolean isPausing = false;  //用来判断播放器是否处于暂停状态

    private MyBinder myBinder = new MyBinder();

    private LocalBroadcastManager localBroadcastManager;

    public class MyBinder extends Binder {
        /**
         * 获取歌曲总时长
         */
        public int getDuration() {
            int t = 0;
            if (mediaPlayer.isPlaying()) {
                t = mediaPlayer.getDuration();
            }
            return t;
        }

        /**
         * 获取正在播放的歌曲的播放位置
         */
        public int getCurrentPositon() {
            int t = 0;
            if (mediaPlayer.isPlaying()) {
                t = mediaPlayer.getCurrentPosition();
            }
            return t;
        }
        /**
         * 调整播放器的播放进度
         */
        public void seekTo(int position) {
            if (mediaPlayer != null && (isPausing == true || mediaPlayer.isPlaying())) {
                mediaPlayer.seekTo(position);
                mediaPlayer.start();
                isPausing = false;
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {

        String action = intent.getStringExtra("action");
        String path = intent.getStringExtra("path");
        String next = intent.getStringExtra("next");
        switch (action) {
            case"playNew":
                playNew(path);
                isPausing = false;
                break;
            case "play":
                play();
                isPausing = false;
                break;
            case "pause":
                pause();
                isPausing = true;
                break;
            case "before":
                break;
            case "next":
                break;
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = new MediaPlayer();
        }
    }

    /**
     * 播放音乐
     */
    private void play() {
        if (isPausing == true) {
            mediaPlayer.start();
        }
    }
    /**
     * 播放新的歌曲
     */
    private void playNew(String path) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }
    /**
     * 暂停播放
     */
    private void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**
     * 音乐播放完成后的监听事件
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        //发送一条广播
        Intent intent = new Intent("com.example.mymusicplayer.LOCAL_BROADCAST");
        intent.putExtra("play_next_auto", "playNextAuto");
        sendBroadcast(intent);
    }

}