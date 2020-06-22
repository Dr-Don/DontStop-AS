package com.example.dontstop;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 抽象类Character，用来描述一个运动角色
 * */
public abstract class Character {
    protected int x, y, id; // 角色坐标和id（所用图片索引
    protected int width, height; // 角色高度和宽度

    /**
     * 默认构造函数，属性初始值为0
     * **/
    public Character() {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
        id = 0;
    }

    /**
     * 参数构造函数
     * */
    public Character(int xx, int yy, int i, int w, int h) {
        x = xx;
        y = yy;
        id = i;
        width = w;
        height = h;
    }

    /**
     * 移动函数move
     * */
    public abstract void move();

    /**
     * 获得角色的x坐标
     * */
    public int getX() {
        return x;
    }

    /**
     * 设置角色的x坐标
     * */
    public void setX(int xx) {
        x = xx;
    }

    /**
     * 获得角色的y坐标
     * */
    public int getY() {
        return y;
    }

    /**
     * 设置角色的y坐标
     * */
    public void setY(int yy) {
        y = yy;
    }

    /**
     * 获得角色的id
     * */
    public int getId() {
        return id;
    }

    /**
     * 设置角色的id
     * */
    public void setId(int i) {
        id = i;
    }

    /**
     * 获得角色的宽度
     * */
    public int getWidth() {
        return width;
    }

    /**
     * 设置角色的宽度
     */
    public void setWidth(int w) {
        width = w;
    }

    /**
     * 获得角色的高度
     * */
    public int getHeight() {
        return height;
    }

    /**
     * 设置角色的高度
     * */
    public void setHeight(int h) {
        height = h;
    }

    /**
     * 改变角色的x坐标
     * */
    public void changeX(int k) {
        x += k;
    }

    /**
     * 改变角色的y坐标
     * */
    public void changeY(int k) {
        y += k;
    }
	
    /**
     * 检测两个角色是否产生碰撞
     * */
    public boolean equals(Character ch) {
        Character other = ch;
        if(getX() + getWidth() >= other.getX() && getX() <= other.getX() + other.getWidth()
        && getY() + getHeight() >= other.getY() && getY() <= other.getY() + other.getHeight())
        {
            return true;
        }
        return false;
    }
}
