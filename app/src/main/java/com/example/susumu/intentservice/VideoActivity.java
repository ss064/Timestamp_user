package com.example.susumu.intentservice;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends AppCompatActivity {

    public VideoView video;
    public TextView counter,t_Xpoint,t_Ypoint;
    public SeekBar seekBar;
    public ImageButton b_start;
    public String androidId;
    private Intent intent;
    private Uri uri;
    public static boolean submit_flg = true; //スタンプ送信完了フラグ
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        video = (VideoView) findViewById(R.id.videoView);
        b_start = (ImageButton) findViewById(R.id.start);
        t_Xpoint = (TextView) findViewById(R.id.pointX);
        t_Ypoint=(TextView) findViewById(R.id.pointY);
        seekBar =(SeekBar) findViewById(R.id.seekBar);
        counter = (TextView) findViewById(R.id.Counter);
        intent = getIntent();
        //動画メディアの指定
        if(null==intent.getData()) {
            //ランチャーから起動した場合、すぐに終了する(デバッグ用)
            Log.d("VideoActivity","finish");
            Toast.makeText(this,"VideoActivity",Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Bundle parameters = getIntent().getExtras();
            androidId = parameters.getString("name");
            uri = Uri.parse(parameters.getString("source"));
            buttonClickListener();
            //uri = Uri.parse(intent.getExtras().getString("source"));
            //Log.d("URL", intent.getExtras().getString("source"));
            VideoChange(uri);

        //再生時間表示に関する処理
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    counter.post(new Runnable() {
                        @Override
                        public void run() {
                            counter.setText(String.valueOf(video.getCurrentPosition() / 1000) + "s");
                            seekBar.setProgress(100 * video.getCurrentPosition() / video.getDuration());
                        }
                    });
                }
            }, 0, 50);


            //次動画自動再生処理
            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mp.getCurrentPosition() != mp.getDuration()) {
                        finish();
                    }
                    VideoChange(uri);
                }
            });

            //動画をタッチしたときの処理
            video.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    if (submit_flg) {
                        submit_flg = false;
                        AsyncHttp post;
                        int i;
                        if (video.isPlaying()) i = 1;
                        else i = 0;
                        post = new AsyncHttp(e.getX() / v.getWidth(), e.getY() / v.getHeight(), video.getCurrentPosition(), uri.getPath().toString().split("/")[1], androidId, i);
                        post.execute();
                        Log.d("post","post.execute()");
                    }
                    t_Xpoint.setText(String.valueOf(e.getX() / v.getWidth()));
                    t_Ypoint.setText(String.valueOf(e.getY() / v.getHeight()));
                    return true;
                }
            });
            //シークバーの処理
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    video.seekTo(video.getDuration() * seekBar.getProgress() / 100);
                    t_Xpoint.setText(String.valueOf(video.getDuration()));
                }
            });
        }
    }

    //ボタンクリックリスナーの設定をまとめる
    public void buttonClickListener(){

        b_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video.isPlaying()) {
                    video.pause();
                } else {
                    video.start();
                }
            }
        });
   }

    private int VideoChange(Uri path){
        video.setVideoURI(path);
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                video.start();
            }
        });
        return 0;
    }

}