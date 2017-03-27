package com.glt.starship;

import android.content.Context;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Animated portion of screen containing
 * GamePanel has a thread which runs while player is playing
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final float WIDTH = 856;
    public static final float HEIGHT = 480;

    private MainThread mainThread;
    private Player player;
    private Background bg;
    private ArrayList<Laser> lasers;
    private ArrayList<GreenLaser> greenLasers;
    private ArrayList<Planet> planets;
    private long laserStartTime;
    private long planetStartTime;
    private long greenLaserStartTime;
    private Random rand = new Random();
    private boolean newGameCreated;

    private Explosion explosion;
    private boolean started;
    private long startReset;
    private boolean reset ;
    private boolean disappear;
    private int best;


    //public static final int SOUND_BULLET = 0;
    public static final int SOUND_EXPLOSION = 1;
    public static final int SOUND_LASER = 2;
    public static final int SOUND_PLANET = 3;

    private SoundPool soundPool;
    private SparseIntArray soundMap;
    private boolean loaded;

    private Context context;

    public GamePanel(Context context) {
        super(context);
        this.context = context;

        // use the Apps shared preferences to store local high score
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.pref_key), Context.MODE_PRIVATE);
        best = sp.getInt(context.getString(R.string.saved_high_score), 0);
        
        getHolder().addCallback(this);

        //enable event handling
        setFocusable(true);

        configureAudio(context);
    }
    private void configureAudio(Context context) {

        // allow 4 audio streams - 2 lasers , a planet, and an explosion
        soundPool = new SoundPool(4, AudioAttributes.USAGE_GAME, 0);

        // sounds will only be played if flag 'loaded' is true
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        soundMap = new SparseIntArray(3);
        soundMap.put(SOUND_EXPLOSION, soundPool.load(context,R.raw.explosion_g, 1));
        soundMap.put(SOUND_LASER, soundPool.load(context,R.raw.laser_g,1));
        soundMap.put(SOUND_PLANET, soundPool.load(context,R.raw.g_planet, 1));

    }
    // generic sound player
    // convenience method to allow each sound to tune intself
    public void playSound(int soundId, float volume, float speed){
        if(loaded) {
            soundPool.play(soundMap.get(soundId), volume, volume, 1, 0, speed);
        }else
            System.out.println("couldn't  play, wasn't loaded");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.space_background));
        bg.setScrollSpeed(-7);

        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.cleanjet ), 90, 98, 4);

        long obstacleStartTime = System.nanoTime();

        planets = new ArrayList<>();
        planetStartTime = obstacleStartTime;

        greenLasers = new ArrayList<>();
        greenLaserStartTime = obstacleStartTime;

        lasers = new ArrayList<>();
        laserStartTime = obstacleStartTime; //nanoTime();

        mainThread = new MainThread(getHolder(), this);
        mainThread.setRunning(true);
        //System.out.println("started and reset is: " + reset);
        mainThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int retryCounter = 0;

        while(retry && retryCounter < 1000){
            retryCounter++;
            try{
                mainThread.setRunning(false);
                mainThread.join();
                retry = false;
                mainThread = null;
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        System.out.println("touched surface, reset  = " + reset);
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(!player.isPlaying() && newGameCreated && reset){
                System.out.println("in touch - top condition");
                player.setPlaying(true);
                player.setUp(true);
            }

            if(player.isPlaying()){
                System.out.println("in touch - is Playing");
                if(!started)started = true;
                reset = false;
                player.setUp(true);
            }
            return true;

        }

        if(event.getAction() == MotionEvent.ACTION_UP){
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }

    public void update() {

        //System.out.println("in update - reset: " + reset);
        if (player.isPlaying()) {
            //System.out.println("in update - is Playing");
            bg.update();
            player.update();

            //check lasers
            long laserElapsedTime =  (System.nanoTime() - laserStartTime)/1000000;
            if(player.getScore() > 9 && laserElapsedTime > 2000 - player.getScore()/5){

                lasers.add( new Laser(BitmapFactory.decodeResource(getResources(), R.drawable.laser_g),
                        (int) WIDTH + 10, (int) (rand.nextDouble() * HEIGHT), 72 , 20,player.getScore(), 8 ));
                playSound(SOUND_LASER, 0.2f,0.88f);
                laserStartTime = System.nanoTime();
            }

            for(int i = 0; i < lasers.size(); i++){
                lasers.get(i).update();
                if(collision(lasers.get(i), player)){
                    player.setPlaying(false);
                    playSound(SOUND_EXPLOSION, 0.2f,1.5f);
                    break;
                }
                if(lasers.get(i).getX() < - 80){
                    lasers.remove(i);
                    i--;
                }
            }

            // check greenLasers
            long greenLaserElspsedTime = (System.nanoTime() - greenLaserStartTime)/1000000;
            if( player.getScore() > 80 && greenLaserElspsedTime > 3000 - player.getScore()/3){
                System.out.println("try to make a green one");
                greenLasers.add(new GreenLaser(BitmapFactory.decodeResource(getResources(), R.drawable.green_laser_g),
                        (int) WIDTH + 10, (int) (rand.nextDouble() * HEIGHT), 54, 10, player.getScore(), 8 ));

                playSound(SOUND_LASER, 0.3f,1.75f);
                greenLaserStartTime = System.nanoTime();
            }
            for(int i = 0; i < greenLasers.size(); i++){
                greenLasers.get(i).update();
                if(collision(greenLasers.get(i), player)){
                    player.setPlaying(false);
                    playSound(SOUND_EXPLOSION, 0.3f,1.75f);
                    break;
                }
                if(greenLasers.get(i).getX() < - 80){
                    greenLasers.remove(i);
                    i--;
                }
            }
            // check for planets
            long planetElapsedTime= (System.nanoTime() - planetStartTime)/1000000;
            if(player.getScore() > 120 && planetElapsedTime > 4000 - player.getScore()/5) {
                planets.add(new Planet(BitmapFactory.decodeResource(getResources(), R.drawable.g_planet),
                        (int) WIDTH + 10, (int) (rand.nextDouble() * HEIGHT), 100, 100, player.getScore(), 6));

                playSound(SOUND_PLANET, 0.8f, 0.6f);
                planetStartTime = System.nanoTime();
            }
            for(int i = 0; i < planets.size(); i++){
                planets.get(i).update();
                if(collision(planets.get(i),player)){
                    player.setPlaying(false);
                    playSound(SOUND_EXPLOSION, 0.4f,1.0f);
                    break;
                }
                if(planets.get(i).getX() < -100){
                    planets.remove(i);
                }
            }

            //System.out.println("missl len " + missiles.size());
        }else {
            //System.out.println("in update - is Not Playing");
            //System.out.println("in update - value of reset:" + reset);
            player.setDy(0);
            if (!reset) {
                System.out.println("in update - reset2" + reset);
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                disappear = true;

                explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), player.getX(),
                        player.getY() - 30, 100, 100, 24);

            }

            explosion.update();
            long resetElapsed = (System.nanoTime() - startReset) / 1000000;
            //System.out.println("reset time : " +resetElapsed);
            if (resetElapsed > 2000 && !newGameCreated) {
                System.out.println("in update - call new Game");
                newGame();
            }
        }

    }

    public boolean collision(GameObject a, GameObject b){
        if (Rect.intersects(a.getRect(), b.getRect())){
            return true;
        }
        return false;
    }
    @Override
    public void draw(Canvas canvas){
        // scale the canvas according to the dimensions of the scrolling background
        super.draw(canvas);
        final float scaleX = getWidth()/WIDTH;
        final float scaleY = getHeight()/HEIGHT;

        if(canvas != null){
            final int savedState = canvas.save();
            canvas.scale(scaleX, scaleY);
            bg.draw(canvas);
            if(!disappear) {
                player.draw(canvas);
            }


            for(Planet p: planets){
                p.draw(canvas);
            }
            for(GreenLaser g: greenLasers){
                g.draw(canvas);
            }
            for(Laser l: lasers){
                l.draw(canvas);
            }

            if(started){
                explosion.draw(canvas);
            }

            drawText(canvas);
            canvas.restoreToCount(savedState);
        }
    }

    public void newGame(){
        disappear = false;

        //remove all obstacles
        greenLasers.clear();
        lasers.clear();
        planets.clear();

        //restart position and speed
        player.setDy(0);
        player.setY((int)HEIGHT/2 + player.height/2);

        if(player.getScore() > best){
            //reset high score
            best = player.getScore();
            // store high score on disk
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_key),Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(context.getString(R.string.saved_high_score), best);
            editor.commit();
        }
        player.resetScore();

        newGameCreated = true;
    }

    public void drawText(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText("SCORE: " + (player.getScore()), 10, HEIGHT - 10, paint);
        canvas.drawText("BEST: " + best,WIDTH - 215, HEIGHT - 10, paint);

        if(!player.isPlaying() && newGameCreated && reset){
            Paint p = new Paint();
            p.setTextSize(40);
            p.setColor(Color.RED);
            p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START " ,WIDTH/2 - 50, HEIGHT/2, p);

            p.setTextSize(20);
            canvas.drawText("PRESS TO RISE" ,WIDTH/2 - 50, HEIGHT/2 + 20, p);

            Drawable d = getResources().getDrawable(R.drawable.ic_arrow_upward_black_24dp);
            d.setBounds( (int) HEIGHT/2 -100, (int)HEIGHT/2 - 100, (int) HEIGHT/2 -50, (int)HEIGHT/2 +50);
            d.draw(canvas);

            canvas.drawText("RELEASE TO FALL " ,WIDTH/2 - 50, HEIGHT/2 + 40, p);
        }

    }
}
