package com.glt.starship;

import android.graphics.Bitmap;

/**
 * A component of graphical object that handles animation logic, updating a current frame among
 * an array of frames based on time-measured updates.
 */

public class Animation {

    private Bitmap[] frames;
    private int currentFrame;
    private long startTime;

    // length between animations
    private long delay;

    // some graphics, such as explosions, should only be animated through one cycle
    private boolean playedOnce;

    /**
     * Starts t
     * @param frames Bitmap array provided by Owner's constructor (Laser, Planet, etc)
     */
    public void setFrames(Bitmap[] frames){
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
    }
    public void setDelay(long d){
        this.delay = d;
    }

    public void update(){
        long elapsedTime = (System.nanoTime() - startTime)/1000000;
        if (elapsedTime > delay){
            currentFrame ++;
            //reset timer
            startTime = System.nanoTime();
        }
        if(currentFrame == frames.length){
            currentFrame = 0;
            playedOnce = true;
        }
    }

    /**
     *
     * @return the current frame that will be drawn for the animation
     */
    public Bitmap getImage(){
        return  frames[currentFrame];
    }

    /**
     *
     * @return whether the animation has already played through once
     */
    public boolean playedOnce(){
        return playedOnce;
    }

}
