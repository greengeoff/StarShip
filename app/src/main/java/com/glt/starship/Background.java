package com.glt.starship;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Object representation of the background image
 */

class Background {


    private Bitmap image;

    private int x, y, scrollSpeed;

    /**
     * Constructor
     * @param bp the image for the background
     */
    public Background(Bitmap bp){
        this.image = bp;


    }

    /**
     * Scrolls the background , and moves image back to origin when totally scrolled of screen
     */
    public void update(){
        x += scrollSpeed;
        if(x < -GamePanel.WIDTH){
            x = 0;
        }
    }

    /**
     * Draws background once, or twice if image has scrolled (which is always the case after an update)
     * @param canvas the canvas object
     */
    public void draw(Canvas canvas){
        canvas.drawBitmap(image, x, y, null);
        if(x < 0){
            canvas.drawBitmap(image, x + GamePanel.WIDTH, y, null);
        }
    }

    /**
     *
     * @param speed rate of desired background scrolling
     */
    public void setScrollSpeed(int speed) {
        this.scrollSpeed = speed;
    }
}
