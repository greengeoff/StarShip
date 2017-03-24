package com.glt.starship;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by geo on 3/16/17.
 */

class Explosion extends GameObject{

    private int x;
    private int y;
    private int width;
    private int height;
    private int row;
    private Animation animation = new Animation();
    Bitmap sprite;




    public Explosion(Bitmap bm, int x, int y, int w, int h, int numFrames){
       // System.out.println("Explosion - start");
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;

        // row = 0;

        Bitmap image[] = new Bitmap[numFrames];

        sprite = bm;

        for(int i = 0; i<image.length; i++){
            if(i%5==0 && i >0)row++;
            try {
                image[i] = Bitmap.createBitmap(sprite, (i - (5*row))* width, row * height, width, height);
            }catch(Exception e){e.printStackTrace();}
        }

        animation.setFrames(image);
        animation.setDelay(10);

    }
    @Override
    void update() {
        if(!animation.playedOnce()){
            animation.update();
        }

    }

    @Override
    void draw(Canvas canvas) {
        if(!animation.playedOnce()){
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }

    }
    public int getHeight(){return height;}
}
