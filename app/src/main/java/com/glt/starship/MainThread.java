package com.glt.starship;

import android.graphics.Canvas;
import android.provider.Settings;
import android.view.SurfaceHolder;

/**
 * Created by geo on 3/16/17.
 */

public class MainThread extends Thread {
    //target frames per second 30
    private int FPS = 30;
    //private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean isRunning;

    public static Canvas canvas;

    public MainThread(SurfaceHolder holder, GamePanel gp) {
        super();
        this.surfaceHolder = holder;
        this.gamePanel = gp;
    }

    public void setRunning(boolean b){
        isRunning = b;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long lagTime;
        long totalTime = 0;
        //int frameCount = 0;
        long targetTime = 1000/FPS;

        while(isRunning) {

            startTime = System.nanoTime();
            canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            }catch(Exception e){

            }
            finally {
                if(canvas!=null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            timeMillis = (System.nanoTime() - startTime)/1000000;
            lagTime = targetTime - timeMillis;

            if(lagTime >= 0) {
                try {
                    this.sleep(lagTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            totalTime += System.nanoTime() - startTime;

            ///// ~uncomment to observe avg frames per second for debugging
            //frameCount++;
            //System.out.println("frameCount: " + frameCount);
            //if(frameCount == FPS){
            //    averageFPS = 1000/((totalTime/frameCount)/1000000);
            //    frameCount = 0;
            //    totalTime = 0;
            //    System.out.println(averageFPS);
            }
        }
    }

