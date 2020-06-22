package com.example.dontstop;

/**
 * Bullet子弹类
 * */

public class Bullet extends Character{
    private int id;
    private int legStep,heightStep;

    public Bullet(int x, int y, int id, int w, int h) {
        super(x,y,id,w,h);
    }

    public void move() {
        changeX(legStep);
        changeY(heightStep);
    }

    public void setLegStep(int ls) {
        legStep=ls;
    }

    public void setHeightStep(int hs) {
        heightStep=hs;
    }

    public int getHeightStep() {
        return heightStep;
    }

    public int getLegStep() {
        return legStep;
    }

}
