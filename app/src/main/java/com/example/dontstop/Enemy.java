package com.example.dontstop;

import android.graphics.Bitmap;
import android.view.SurfaceView;

/**
 * Enemy类，敌人
 * */

public class Enemy extends Character{
    private int id,hv,vv;
    private int hcount,vcount;

    public Enemy(int id, int x, int y, int w, int h)
    {
        super(x,y,id,w,h);
        hv=1;
        vv=1;
		hcount=0;
		vcount=0;
    }

    public void move() {
        changeY(vv);
        changeX(hv);
    }

    public void setHV(int h) {
        hv=h;
    }

    public void setVV(int v) {
        vv=v;
    }

    public int getHV() {
        return hv;
    }

    public int getVV() {
        return vv;
    }

    public void setVcount(int v) {
        vcount=v;
    }

    public void setHcount(int h) {
        hcount=h;
    }

    public int getVcount() {
        return vcount;
    }

    public int getHcount() {
        return hcount;
    }
}
