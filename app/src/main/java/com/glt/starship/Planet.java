package com.glt.starship;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by geo on 3/16/17.
 */

public class Planet extends GameObject {

    private int score;
    private int speed;
    private Random rand = new Random();
    private Animation animation;
    private Bitmap sprite;

    public Planet(Bitmap bm, int x, int y, int w, int h, int s, int numFrames){

        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;

        animation = new Animation();

        speed = 15 + (int)(rand.nextDouble()*score/20);
        if (speed > 160) {
            speed = 160;
        }

        Bitmap[] image = new Bitmap[numFrames];

        sprite = bm;


        System.out.println("begin planet");
        for(int i = 0; i<image.length; i++){
            try {
                image[i] = Bitmap.createBitmap(sprite, i * width, 0, width, height);
            }catch (Exception e){ e.printStackTrace();}
        }



        animation.setFrames(image);
        animation.setDelay(100 - speed);


        System.out.println("end planet");
    }

    public void update(){
        x -= speed;
        animation.update();

    }
    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
