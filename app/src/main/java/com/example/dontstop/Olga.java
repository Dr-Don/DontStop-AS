package com.example.dontstop;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.math.BigInteger;

/**
 * 主角类Olga，奥尔加团长
 * */
public class Olga extends Character{
    private int id;
    private int velocity; // 速度，y方向
    private int hVelocity; // 速度，x方向
    private int count; // 已运行帧数
    private int hFacing; // 朝向


    /**
     * 默认构造函数
     * */
    public Olga(int id, int x, int y, int w, int h)
    {
        super(x,y,id,w,h);
        velocity=0;
        hVelocity=80;
        count=0;
    }
    /**
     * 获得速度
     */
    public int getVelocity()
    {
        return velocity;
    }

    /**
     * 设置速度
     * */
    public void setVelocity(int v){
        velocity = v;
    }

    /**
     * 返回朝向
     * */
    public int gethFacing(){
        return hFacing;
    }

    /**
     * 设置朝向
     * */
    public void sethFacing(int hf){
        hFacing = hf;
    }

    public void setCount(int count){
        this.count = count;
    }

    /**
     * 移动函数
     * */
    public void move(){
        /* y方向 */
        int acceleration = 15; // 加速度

        if(velocity != 0){
            if(count > 7){
                if(velocity < 20){
                    if(velocity + acceleration == 0){
                        velocity = 0;
                    }
                    velocity = velocity + acceleration;
                }
                count = 0;
            }
            count++;
            changeY(velocity);
        }

        /* x方向*/
        switch (hFacing){
            case -1:
                changeX(-hVelocity);
                hFacing = 0;
                break;
            case 0:
                changeX(0);
                break;
            case 1:
                changeX(hVelocity);
                hFacing = 0;
                break;
        }
    }

    /**
     * checkHitPlatform函数，检测角色是否踩在了平台上
     * */
    public boolean checkHitPlatform(Object obj)
    {
        Platform other = (Platform) obj;

        if(getX()+getWidth()>=other.getX()&&
                getX()<=other.getX()+other.getWidth() &&
                getY()+getHeight() >=other.getY() &&
                getY()+getHeight() <= other.getY()+other.getHeight())
            return true;
        return false;
    }
}
