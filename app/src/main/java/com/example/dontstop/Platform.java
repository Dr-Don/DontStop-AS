package com.example.dontstop;

import android.graphics.Bitmap;
import android.view.SurfaceView;

/**
 * Platform类，平台类
 * */

public class Platform extends Character{
    private int id, hv, vv, vcount;
    private boolean brownAnimation = false;

    /**
     * 默认构造函数
     */
    public Platform(int id, int x, int y, int w, int h)
    {
        super(x,y,id,w,h);

        int hv1 = (int) (Math.random()*2)+1;
        if(hv1==1)
            hv=-2;
        if(hv1==2)
            hv=2;

        int vv1 = (int) (Math.random()*2)+1;
        if(vv1==1)
            vv=-1;
        if(vv1==2)
            vv=1;

        vcount=0;
    }

    public int getVcount() {
        return vcount;
    }

    public void setVcount(int v) {
        vcount = v;
    }

    public boolean getBrownAnimation() {
        return brownAnimation;
    }

    public void setBrownAnimation(boolean b) {
        brownAnimation=b;
    }

    public void setHV(int h2) {
        hv = h2;
    }

    public int getHV() {
        return hv;
    }

    public int getVV() {
        return vv;
    }

    public void setVV(int v) {
        vv =v;
    }

    public void move()
    {

    }
}
