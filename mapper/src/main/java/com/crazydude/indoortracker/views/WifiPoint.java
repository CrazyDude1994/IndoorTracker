package com.crazydude.indoortracker.views;

/**
 * Created by Crazy on 27.10.2016.
 */
public class WifiPoint {

    private float x;
    private float y;

    public WifiPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }
}
