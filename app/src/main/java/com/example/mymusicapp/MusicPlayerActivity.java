package com.example.mymusicapp;

//import android.icu.util.TimeUnit;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleB, currentTimeB, totalTimeB;
    SeekBar seeker;
    ImageView pausePlay, nxtB, prvB, PlayIc;
    ArrayList<AudioMode> songsList;
    AudioMode currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int i= 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_player);

        titleB = findViewById(R.id.song_t);
        currentTimeB = findViewById(R.id.current_time);
        totalTimeB = findViewById(R.id.total_time);
        seeker = findViewById(R.id.seek_bar);

        pausePlay = findViewById(R.id.pause);
        nxtB = findViewById(R.id.next);
        prvB = findViewById(R.id.pre);
        PlayIc = findViewById(R.id.display_icon);

        songsList = (ArrayList<AudioMode>) getIntent().getSerializableExtra("LIST");

        setMusicData();
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seeker.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeB.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));
                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.baseline_pause_circle_24);
                        PlayIc.setRotation(i++);

                    }else{
                        pausePlay.setImageResource(R.drawable.baseline_arrow_right_24);
                        PlayIc.setRotation(0);
                    }
                }
                new Handler().postDelayed(this,100);
            }
        });
        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null &&  fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    void setMusicData(){
        currentSong = songsList.get(MyMediaPlayer.currentIndex);
        titleB.setText(currentSong.getTitle());
        totalTimeB.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v-> pausePlay());
        nxtB.setOnClickListener(v-> playNext());
        prvB.setOnClickListener(v-> playPrev());

        playMusic();
    }

    private void  playMusic(){
       mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seeker.setProgress(0);
            seeker.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void playNext(){

        if(MyMediaPlayer.currentIndex==songsList.size()-1)
            return;
        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setMusicData();
    }
    private void  playPrev(){

        if(MyMediaPlayer.currentIndex==0)
            return;
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setMusicData();
    }
    private void  pausePlay(){
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
       return  String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis)% TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis)% TimeUnit.MINUTES.toSeconds(1));
    }

}





