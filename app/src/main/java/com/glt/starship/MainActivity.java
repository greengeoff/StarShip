package com.glt.starship;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {


    private boolean isBound = false;
    private MusicService mService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MusicService.ServiceBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }
    void doUnBindService(){
        if(isBound){
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // start the bg music service
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mService != null) {
            mService.stopMusic();
        }
        // stop music service
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // turn off title bar get full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        doBindService();
        setContentView(new GamePanel(this));
    }

    @Override
    protected void onDestroy() {
        doUnBindService();
        super.onDestroy();
    }
}
