package com.example.dontstop;

import android.content.ContentValues;

/**
 * 玩家类
 * */
public class Player {
    public String name; // 姓名
    public int score; // 分数，最高分

    public Player(){
        name = "olga";
        score = 0;
    }

    public Player(String n, int s){
        name = n;
        score = s;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
