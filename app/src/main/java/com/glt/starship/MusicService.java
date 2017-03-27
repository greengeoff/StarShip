package com.glt.starship;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Background music player, coupled with Main Activity's onPause() and onResume
 */

public class MusicService extends Service implements MediaPlayer.OnCompletionListener,
MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{

    private MediaPlayer mPlayer;

    // for activity to bind
    private final IBinder mBinder = new ServiceBinder();
    public class ServiceBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    public MusicService(){};
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // constructor
    @Override
    public void onCreate(){
        super.onCreate();
        mPlayer = MediaPlayer.create(this, R.raw.opentrack);

        if(mPlayer != null){
            mPlayer.setLooping(true);
            mPlayer.setVolume(0.09f,0.09f);

            initPlayer();
        }

    }

    public void initPlayer(){
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }
    // observe life cycle of MediaPlayer
    public void stopMusic(){
        if(mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
        mPlayer = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mPlayer != null)
        {
            mPlayer.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mPlayer != null){
            try{
                mPlayer.stop();
                mPlayer.release();
            }finally{
                mPlayer = null;
            }
        }
    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        System.out.println("Music player fail");
        if(mPlayer != null){
            try{
                mPlayer.stop();
                mPlayer.release();
            }finally{
                mPlayer = null;
            }
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }



    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
