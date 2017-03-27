package com.glt.starship;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 *  Ship that the player conducts. User presses on screen to toggle "up", causing craft to rise,
 *  and releasing touch causes ship to fall.
 *
 *  Player has a score that accumulates with time (and.. ) and influences appearence and
 *  speed of obstacles.
 */

class Player extends GameObject {

    private Bitmap sprite;
    private int score;
    private boolean up;
    //change in y value, ie vertical velocity
    private int dy;
    private boolean playing;
    private Animation animation;
    private long startTime;


    // bitmap information injected by client
    public Player(Bitmap bm, int w, int h, int numFrames){

        // arbitrary size, currently scaling due to the X,Y scaling on canvas
        x = 100;
        // start ship in the vertical middle
        y = (int)GamePanel.HEIGHT /2;


        animation = new Animation();

        dy = 0;
        score = 0;
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];

        sprite = bm;
        for (int i = 0; i < image.length; i++){
            image[i] = Bitmap.createBitmap(sprite, i*width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }

    public void setUp(boolean b){up = b;}

    @Override
    void update() {
        long elapsedTime = (System.nanoTime()-startTime)/1000000;
        if (elapsedTime > 100){
            score ++;
            startTime = System.nanoTime();
            //System.out.println("score " + score);
        }

        // get correct sprite frame
        animation.update();

        if (up){
            dy -= 4;
        }else{
            dy += 4;
        }
        if(dy > 6) dy = 6;
        if(dy < -6) dy =-6;

        y += dy;
        dy /= 2;

        //// makes sure the ship stays on screen
        if(y < 0) y = 1;
        if(y + height > GamePanel.HEIGHT) y = (int)GamePanel.HEIGHT-height;

    }

    @Override
    void draw(Canvas canvas) {

        canvas.drawBitmap(animation.getImage(), x, y, null);
    }

    public int getScore() {
        return score;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void resetScore() {
        this.score = 0;
    }
}
