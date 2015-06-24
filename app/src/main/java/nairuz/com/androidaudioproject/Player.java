package nairuz.com.androidaudioproject;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class Player extends Activity {

    int position;
    ArrayList<File> mySong;
    static MediaPlayer  mediaPlayer;
    Uri uri;

    private ImageView playlist;
    private ImageView btn_play;
    private ImageView btn_forward;
    private ImageView btn_backward;
    private ImageView btn_previous;
    private ImageView btn_next;

    //Seekbar and its timing
    private SeekBar seekBar ;
    private int seekForwardTime = 5000;
    private int seekBackwardTime = 5000;
    private LinearLayout timeframe;
    private TextView time_possition, time_duration;
    private Thread updateSeekbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        //initiate variables
        playlist = (ImageView) findViewById(R.id.btnPlaylist);
        btn_play = (ImageView) findViewById(R.id.btnPlay);
        btn_backward = (ImageView) findViewById(R.id.btnBackward);
        btn_forward = (ImageView) findViewById(R.id.btnForward);
        btn_previous = (ImageView) findViewById(R.id.btnPrevious);
        btn_next = (ImageView) findViewById(R.id.btnNext);

        seekBar = (SeekBar) findViewById(R.id.songProgressBar);
        time_possition = (TextView) findViewById(R.id.time_position);
        time_duration = (TextView) findViewById(R.id.time_duration);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = bundle.getInt("pos", 0);
        mySong = (ArrayList) bundle.getParcelableArrayList("songlist");


        //Start music automatically when an item clicked from the list view
        if (mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.release();
        }

        //Get the  position of the song in the list and cast to string using (  toString() )
        uri = Uri.parse(mySong.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);//plug in the uri in the media player to work



        //Update the seekbar
        updateSeekbar =  new Thread(){
            @Override
            public void run () {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                seekBar.setMax(totalDuration);
                while (currentPosition < totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //super.run();
            }
        };

        mediaPlayer.start(); //start mediaplayer first
        updateSeekbar.start();// then update seekbar



        //Action Listeners
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the playlist icon clicked, would take back to the second listview (new activity)
                if (v == playlist) {
                    startActivity(new Intent(getApplicationContext(), AnotherList.class).putExtra("pos", position));
                }
            }
        });


        //Play button
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                        // Changing button image to play button
                        btn_play.setImageResource(R.drawable.btn_play);
                    }
                } else {
                    // Resume song
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        // Changing button image to pause button
                        btn_play.setImageResource(R.drawable.btn_pause);
                    }
                }
            }
        });

        //Backward Button:
        btn_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // get current song position
                    int currentPosition = mediaPlayer.getCurrentPosition();

                    // check if seekBackward time is greater than 0 sec
                    if(currentPosition - seekBackwardTime >= 0){
                        // forward song
                        mediaPlayer.seekTo(currentPosition - seekBackwardTime);
                    }else{
                        // backward to starting position
                        mediaPlayer.seekTo(0);
                    }
                }
        });

        //Previous button:
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btn_previous){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    position = (position - 1 < 0)? mySong.size()-1 : position -1 ;
                    uri = Uri.parse(mySong.get(position).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    mediaPlayer.start();
                }

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btn_next) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    position = (position + 1) % mySong.size();
                    uri = Uri.parse(mySong.get(position).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    mediaPlayer.start();
                }
            }
        });

        btn_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current song position
                int currentPosition = mediaPlayer.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= mediaPlayer.getDuration()){
                    // forward song
                    mediaPlayer.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
