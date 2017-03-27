package com.glt.starship;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Base class for objects in game
 */

public abstract class GameObject {

    protected int x;
    protected int y;
    protected int dx;
    protected int dy;
    protected int width;

    /**
     * Provides enclosing rectangle for detecting collisions and placement
     * @return framing rectacle
     */
    public Rect getRect(){
        return new Rect(x, y, x+width,y+height);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {

        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    protected int height;

    // To keep up with the game thread, game objects must update and draw themselves.
    abstract void update();
    abstract void draw(Canvas canvas);
}
