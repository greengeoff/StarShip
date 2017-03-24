package com.glt.starship;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.glt.starship.GameObject;

/**
 * Created by geo on 3/22/17.
 */

public class GreenLaser extends GameObject { // laser metrics specs determined by player score
    private int score;
    private int speed;
    private Animation animation;
    private Bitmap sprite;

    public GreenLaser(Bitmap bm, int x, int y, int w, int h, int s, int numFrames){

        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;
        int row = 0;

        animation = new Animation();

        speed = 35 + score/ 30 ;
        if (speed > 90){
            speed = 90;
        }
        System.out.println("first laser");
        Bitmap[] image = new Bitmap[numFrames];

        sprite = bm;

        for(int i = 0; i<image.length; i++) {
            if (i % 4 == 0 && i > 0) row++;
            try {
                image[i] = Bitmap.createBitmap(sprite, width * (i - (4 * row)), row * height, width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("middle laser");
            animation.setFrames(image);
            //higher delay for faster start speed compare to Laser
            animation.setDelay(100 - speed);

        }


    }

    @Override
    void update() {
        x -= speed;
        animation.update();

    }

    @Override
    void draw(Canvas canvas) {
        try{
            canvas.drawBitmap(animation.getImage(),x,y,null);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}