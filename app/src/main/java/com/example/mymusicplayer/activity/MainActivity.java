package com.example.mymusicplayer.activity;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.adapter.MyAdapter;
import com.example.mymusicplayer.po.Song;
import com.example.mymusicplayer.service.MyService;
import com.example.mymusicplayer.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView musicListView;

    private MyAdapter adapter;

    private List<Song> musicList = new ArrayList<Song>();

    private ImageButton star;

    private ImageButton before;

    private ImageButton next;

    private TextView time;

    public Song song;

    private TextView titleTV;

    private TextView artistTV;

    private TextView allDuration;

    private TextView nowDuation;

    private SeekBar seekBar;

    private int mposition;

    private boolean isPlaying = false;

    private MyService.MyBinder myBinder;

    public static final int UPDATE = 1;

    private static int i = 0;

    private MyReceiver myReceiver;

    private IntentFilter intentFilter;

    private Handler handler = new Handler() {

        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE:
                    if (myBinder != null && myBinder.getDuration() != 0) {
                        nowDuation.setText(getTime(myBinder.getCurrentPositon()));
                        seekBar.setProgress(100*myBinder.getCurrentPositon()/myBinder.getDuration());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        musicListView = (ListView) findViewById(R.id.musiclist);
        time = (TextView) findViewById(R.id.time);
        star = (ImageButton) findViewById(R.id.star);
        before = (ImageButton) findViewById(R.id.before);
        next = (ImageButton) findViewById(R.id.next);
        titleTV = (TextView) findViewById(R.id.title_textview);
        artistTV = (TextView) findViewById(R.id.artist_textview);
        allDuration = (TextView) findViewById(R.id.all_duration);
        nowDuation = (TextView) findViewById(R.id.now_duration);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        allDuration.setText(getTime(0));
        nowDuation.setText(getTime(0));
        star.setOnClickListener(this);
        before.setOnClickListener(this);
        next.setOnClickListener(this);
        musicList = MusicUtils.getMusicData(this);
        adapter = new MyAdapter(this, musicList);
        musicListView.setAdapter(adapter);
        song = musicList.get(0);
        myReceiver = new MyReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.mymusicplayer.LOCAL_BROADCAST");
        registerReceiver(myReceiver, intentFilter);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                song = musicList.get(position);
                mposition = position;
                play_onClick(song, true);
                isPlaying = true;
                star.setBackgroundResource(R.drawable.pause);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.star:

                if (isPlaying == false && i == 0) {
                    play_onClick(song, true);
                    star.setBackgroundResource(R.drawable.pause);
                    i++;//判断是否在进入播放器后，直接点击播放按钮
                } else if (isPlaying == false) {
                    play_onClick(song, false);
                    star.setBackgroundResource(R.drawable.pause);
                } else {
                    //暂停播放
                    pause_onClick();
                    star.setBackgroundResource(R.drawable.star);
                }

              break;
            case R.id.before:
                int p1 = (musicList.size()+mposition-1)%musicList.size();
                mposition--;
                song = musicList.get(p1);
                before_onClick(song);
                break;
            case R.id.next:
                int p2 = (musicList.size()+mposition+1)%musicList.size();
                mposition++;
                song = musicList.get(p2);
                next_onClick(song);
                break;
            default:
                break;
        }
    }

    /**
     *播放，调用服务
     */
    private void play_onClick(Song song, boolean isNew) {
        Intent intent = new Intent(this, MyService.class);
        if (isNew == true) {
            intent.putExtra("action", "playNew");
        } else {
            intent.putExtra("action", "play");
        }
        intent.putExtra("path", song.getPath());
        startService(intent);
        titleTV.setText(song.getTitle());
        artistTV.setText(song.getArtist());
        isPlaying = true;
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        allDuration.setText(getTime(song.getDuration()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int dest = seekBar.getProgress();
                    int mMax = myBinder.getDuration();
                    int sMax = seekBar.getMax();
                    myBinder.seekTo(mMax*dest/sMax);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message message = new Message();
                    message.what = UPDATE;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    /**
     * 暂停，调用服务
     */
    private void pause_onClick() {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action", "pause");
        startService(intent);
        isPlaying = false;
    }

    /**
     * 上一首
     */
    private void before_onClick(Song song) {
        play_onClick(song, true);
        star.setBackgroundResource(R.drawable.pause);
    }

    /**
     * 下一首
     */
    private void next_onClick(Song song) {
        play_onClick(song, true);
        star.setBackgroundResource(R.drawable.pause);
    }


    /**
     * 将duration转化为hh：mm：ss的格式
     */
    private String getTime(int duration) {
        duration /= 1000;
        int hour = duration/3600;       //时
        int minute = (duration-hour*3600)/60;   //分
        int seconds = duration-hour*3600-minute*60;  //秒

        if(hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, seconds);
        }
        return String.format("%02d:%02d", minute, seconds);
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String nextAutos = intent.getStringExtra("play_next_auto");
            Toast.makeText(MainActivity.this, "received", Toast.LENGTH_SHORT).show();
            if ("playNextAuto".equals(nextAutos)) {
                int p3 = (musicList.size()+mposition+1)%musicList.size();
                mposition++;
                song = musicList.get(mposition);
                next_onClick(song);
            }
        }
    }
}
