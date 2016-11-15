package com.crazydude.indoortracker.models;

/**
 * Created by Crazy on 11.11.2016.
 */

public class Position {

    private float x;
    private float y;

    public Position() {
    }

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
