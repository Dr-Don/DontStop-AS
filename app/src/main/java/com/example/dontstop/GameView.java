package com.example.dontstop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class GameView extends SurfaceView implements Callback,Runnable{

    private SurfaceHolder holder;
    private Paint paint;
    private Canvas canvas;
    private Thread thread;
    private GamePlay gamePlay;

    Intent menuInt; // 返回菜单

    private int SDV = -30; // 主角上升的速度
    private int BSDS = 15; // 平台下落的速度
    private int DSDS = 30; // 主角下落的速度
    private int level = 0; // 等级
    private int score = 0; // 分数
    private int springCount = 0;
    private int hFacing = 0;
    private int creationCounter = 0;

    private Olga olga;
    private Platform lastHitPlatform = new Platform(0, 0, 0, 0, 0); // 上一个停靠的平台
    private ArrayList<Character> myGuys = new ArrayList<>(); // 角色队列
    private ArrayList<Bitmap> myBitmaps = new ArrayList<>(); // 图片队列
    private ArrayList<Character> myBullets = new ArrayList<>(); // 子弹队列
    private ArrayList<Character> myPlatforms= new ArrayList<>(); // 平台队列
    private ArrayList<Character> myEnemies = new ArrayList<>(); // 敌人队列

    // 图片资源
    private Bitmap background,overBackground;
    private Bitmap olgaLImg, olgaRImg, olgaSImg;
    private Bitmap enemyImg;
    private Bitmap bulletImg;
    private Bitmap blueP, orangeP, whiteP, dorangeP, blueS0, blueS1;
    private Bitmap brownP0, brownP1, brownP2, brownP3;

    boolean gameOver = false; // 游戏结束
    boolean gameOn = true; // 游戏进行中
    boolean shiftDown = false; // 主角是否下落
    boolean exit = false; // 是否要回到主菜单

    /**
     * 初始化，加载资源
     * */
    public void load(){
        score = 0;
        level = 0;

        // 定位到图片并适当调整大小
        // 背景
        background = BitmapFactory.decodeResource(getResources(),R.drawable.gamebackground);
        overBackground = BitmapFactory.decodeResource(getResources(),R.drawable.over);
        int newWidth = Math.round(background.getWidth()/((float) background.getWidth()/getWidth()));
        int newHeight = Math.round(background.getHeight()/((float) background.getHeight()/getHeight()));
        background = Bitmap.createScaledBitmap(background,newWidth,newHeight,true);
        overBackground = Bitmap.createScaledBitmap(overBackground,newWidth,newHeight,true);
        // 角色
        olgaLImg = BitmapFactory.decodeResource(getResources(),R.mipmap.olgal);
        olgaLImg = Bitmap.createScaledBitmap(olgaLImg,olgaLImg.getWidth()*2,olgaLImg.getHeight()*2,true);
        olgaRImg = BitmapFactory.decodeResource(getResources(),R.mipmap.olgar);
        olgaRImg = Bitmap.createScaledBitmap(olgaRImg,olgaRImg.getWidth()*2,olgaRImg.getHeight()*2,true);
        olgaSImg = BitmapFactory.decodeResource(getResources(),R.mipmap.olgas);
        olgaSImg = Bitmap.createScaledBitmap(olgaSImg,olgaSImg.getWidth()*2,olgaSImg.getHeight()*2,true);
        enemyImg = BitmapFactory.decodeResource(getResources(),R.mipmap.enemy);
        enemyImg = Bitmap.createScaledBitmap(enemyImg,enemyImg.getWidth()*2,enemyImg.getHeight()*2,true);
        // 物品
        bulletImg = BitmapFactory.decodeResource(getResources(),R.mipmap.bullet);
        bulletImg = Bitmap.createScaledBitmap(bulletImg,bulletImg.getWidth()*4,bulletImg.getHeight()*4,true);
        blueP = BitmapFactory.decodeResource(getResources(),R.mipmap.p);
        orangeP = BitmapFactory.decodeResource(getResources(),R.mipmap.porange);
        whiteP = BitmapFactory.decodeResource(getResources(),R.mipmap.pwhite);
        dorangeP = BitmapFactory.decodeResource(getResources(),R.mipmap.pdorange);
        blueS0 = BitmapFactory.decodeResource(getResources(),R.mipmap.platforms0);
        blueS1 = BitmapFactory.decodeResource(getResources(),R.mipmap.platforms1);
        brownP0 = BitmapFactory.decodeResource(getResources(),R.mipmap.pbrown0);
        brownP1 = BitmapFactory.decodeResource(getResources(),R.mipmap.pbrown1);
        brownP2 = BitmapFactory.decodeResource(getResources(),R.mipmap.pbrown2);
        brownP3 = BitmapFactory.decodeResource(getResources(),R.mipmap.pbrown3);

        // 添加资源到队列
        myBitmaps.add(olgaRImg); // 0
        myBitmaps.add(enemyImg); // 1
        myBitmaps.add(blueP); // 2
        myBitmaps.add(orangeP); // 3
        myBitmaps.add(whiteP); // 4
        myBitmaps.add(dorangeP); // 5
        myBitmaps.add(blueS0); // 6
        myBitmaps.add(blueS1); // 7
        myBitmaps.add(brownP0); // 8
        myBitmaps.add(brownP1); // 9
        myBitmaps.add(brownP2); // 10
        myBitmaps.add(brownP3); // 11
        myBitmaps.add(bulletImg); // 12

        // 添加初始角色到队列
        olga = new Olga(0,getWidth()/2-olgaLImg.getWidth()/2,getHeight()-olgaLImg.getHeight()*2,olgaLImg.getWidth(),olgaLImg.getHeight());
        olga.setVelocity(SDV);
        myGuys.add(olga);

        // 先生成部分平台
        for(int i = 0;i < 10;i++){
            Platform platform = randomPlatform();
            myPlatforms.add(platform);
        }

        // 生成游戏一开始时玩家能够立足的平台
        lastHitPlatform = new Platform(2,getWidth()/2-blueP.getWidth()/2,getHeight()-olgaLImg.getHeight(),blueP.getWidth(),blueP.getHeight());
        myPlatforms.add(lastHitPlatform);
    }

    /**
     * 生成一个普通随机平台
     * */
    public Platform randomPlatform(){
        int xp = (int)(Math.random()*(getWidth() - blueP.getWidth()));
        int yp = (int)(Math.random()*getHeight());
        Platform platform = new Platform(2,xp,yp,blueP.getWidth(),blueP.getHeight());
        return platform;
    }

    /**
     * 更新3号平台，在x轴上移动
     * */
    public void updatePlat3(int k, Character plat){
        Platform tempPlat3 = (Platform)plat;
        int tempx1 = tempPlat3.getX();
        int tempv = tempPlat3.getHV();

        if (tempv == 2) {
            tempPlat3.changeX(tempv*10);
            if (tempx1 > getWidth()) {
                tempPlat3.setHV(-2);
            }
        }

        if (tempv == -2) {
            tempPlat3.changeX(tempv*10);
            if (tempx1 < 0) {
                tempPlat3.setHV(2);
            }
        }
        myPlatforms.set(k, tempPlat3);
        canvas.drawBitmap(myBitmaps.get(tempPlat3.getId()),tempPlat3.getX(),tempPlat3.getY(),paint);
    }

    /**
     * 更新破碎的平台，执行平台破碎的动画
     * */
    public void updatePlat8(int k, Character plat) {
        Platform brownPlat = (Platform) plat;

        // 播放动画
        if (brownPlat.getBrownAnimation()) {
            // 动画结束，移除平台
            if (brownPlat.getId() == 11) {
                myPlatforms.remove(k);
            }

            if (brownPlat.getId() < 11) {
                canvas.drawBitmap(myBitmaps.get(brownPlat.getId()),brownPlat.getX(),brownPlat.getY(),paint);
                brownPlat.setId(plat.getId() + 1);
                myPlatforms.set(k, brownPlat);
            }
        }

        // 不播放动画
        if (!brownPlat.getBrownAnimation()) {
            canvas.drawBitmap(myBitmaps.get(brownPlat.getId()),brownPlat.getX(),brownPlat.getY(),paint);
        }
    }

    /**
     * 更新5号平台，在y轴上移动
     * */
    public void updatePlat5(int k, Character plat) {
        Platform tempPlat5 = (Platform) plat;

        int tempy1 = tempPlat5.getY();
        int tempv = tempPlat5.getVV();
        int vcount = tempPlat5.getVcount();

        if (tempv == -1) {
            if (vcount >= 100) {
                tempPlat5.setVV(1);
            }

            if (vcount < 100) {
                tempPlat5.changeY(tempv*10);
                tempPlat5.setVcount(tempPlat5.getVcount() + 1);
            }
        }

        if (tempv == 1) {
            if (vcount <= -100) {
                tempPlat5.setVV(-1);
            }

            if (vcount > -100) {
                tempPlat5.changeY(tempv*10);
                tempPlat5.setVcount(tempPlat5.getVcount() - 1);
            }
        }

        myPlatforms.set(k, tempPlat5);
        canvas.drawBitmap(myBitmaps.get(tempPlat5.getId()),tempPlat5.getX(),tempPlat5.getY(),paint);
    }

    /**
     * 更新敌人
     *
     * */
    public void updateEnemy(int w, Character en){
        Enemy tempEnemy = (Enemy) en;

        int tempy = tempEnemy.getY();
        int tempx = tempEnemy.getX();

        int temph = tempEnemy.getHV();
        int tempv = tempEnemy.getVV();
        int vcount = tempEnemy.getVcount();
        int hcount = tempEnemy.getHcount();

        // 在y轴上移动
        if (tempv == -1) {
            if (vcount >= 25) {
                tempEnemy.setVV(1);
            }

            if (vcount < 25) {
                tempEnemy.changeY(tempv);
                tempEnemy.setVcount(tempEnemy.getVcount() + 1);
            }
        }

        if (tempv == 1) {
            if (vcount <= -25) {
                tempEnemy.setVV(-1);
            }

            if (vcount > -25) {
                tempEnemy.changeY(tempv);
                tempEnemy.setVcount(tempEnemy.getVcount() - 1);
            }
        }

        // 在x轴上移动
        if (temph == -1) {
            if (hcount >= 60) {
                tempEnemy.setHV(1);
            }

            if (hcount < 60) {
                tempEnemy.changeX(temph);
                tempEnemy.setHcount(tempEnemy.getHcount() + 1);
            }
        }

        if (temph == 1) {
            if (hcount <= -60) {
                tempEnemy.setHV(-1);
            }

            if (hcount > -60) {
                tempEnemy.changeX(temph);
                tempEnemy.setHcount(tempEnemy.getHcount() - 1);
            }
        }

        tempEnemy.changeY(BSDS);

        myEnemies.set(w, tempEnemy);
        canvas.drawBitmap(enemyImg,tempEnemy.getX(),tempEnemy.getY(),paint);

        // 掉出屏幕0

        if (tempEnemy.getY() > getHeight()) {
            myEnemies.remove(w);
        }
    }

    /**
     * 平台碰撞检测
     * */
    public void checkPlatformHit() {

        Olga olga1 = (Olga) myGuys.get(0);

        for (int a = 0; a < myPlatforms.size(); a++) {
            // 只检测下落过程中的碰撞
            if (shiftDown) {
                // 如果存在碰撞
                if (olga1.checkHitPlatform(myPlatforms.get(a))) {
                    Platform hitPlat = (Platform) myPlatforms.get(a);

                    // 如果碰到棕色平台，播放动画
                    if ((hitPlat.getId() >= 8) && (hitPlat.getId() < 11)) {
                        Platform newBrown = (Platform) myPlatforms.get(a);
                        newBrown.setBrownAnimation(true);
                        myPlatforms.set(a, newBrown);
                    }
                    // 如果碰到带道具的平台
                    else if (hitPlat.getId() == 6) {
                        Platform launch = (Platform) myPlatforms.get(a);
                        launch.setId(7);
                        myPlatforms.set(a, launch);

                        SDV = -50;
                        BSDS = 45;
                        DSDS = 40;
                        springCount = 50;
                    }
                    // 如果是其它平台,跳跃
                    else {
                        Platform t2 = (Platform) myPlatforms.get(a);

                        Olga temp = (Olga) myGuys.get(0);
                        temp.setVelocity(SDV);
                        temp.setCount(0);
                        myGuys.set(0, temp);

                        // 一次性平台
                        if (hitPlat.getId() == 4) {
                            myPlatforms.remove(a);
                        }
                    }
                }
            }
        }

        for (int r = 0; r < myEnemies.size(); r++) {
            if (olga1.getVelocity() > 0) {
                if (olga1.equals(myEnemies.get(r))) {
                    Olga temp = (Olga) myGuys.get(0);
                    temp.setVelocity(SDV);
                    myGuys.set(0, temp);
                    score = score + 50;
                    myEnemies.remove(r);
                }
            }
        }
    }

    /**
     * 子弹碰撞检测
     * */
    public void checkBulletHit() {
        try{
            // 检查每一颗子弹是否击中敌人
            for (int a = 0; a < myEnemies.size(); a++) {
                if (myEnemies.size() > 0) {
                    for (int k = 0; k < myBullets.size(); k++) {
                        if (myBullets.get(k).equals(myEnemies.get(a))) {
                            myEnemies.remove(a);
                            myBullets.remove(k);
                            score = score + 500;
                        }
                    }
                }
            }
        }catch(IndexOutOfBoundsException e){}
    }

    /**
     * 检查游戏是否结束
     * */
    public void checkDoodleGameOver() {
        Olga olga = (Olga) myGuys.get(0);

        // 检查是否超出屏幕
        if (olga.getY() > getHeight()) {
            gameOver = true;
            gameOn = false;
        }

        // 检查是否碰撞到敌人
        for (int k = 0; k < myEnemies.size(); k++) {
            Character en = myEnemies.get(k);

            if (olga.getVelocity() < 0) {
                if (en.equals(olga)) {
                    gameOver = true;
                    gameOn = false;
                }
            }
        }
    }

    /**
     * 生成随机平台
     * */
    public void generateLiveRandomPlatform(){
        int color = 1;

        // 游戏等级
        if (score >= 25000) {
            level = 5;
        }

        if ((score > 15000) && (score <= 25000)) {
            level = 4;
        }

        if ((score > 7000) && (score <= 15000)) {
            level = 3;
        }

        if ((score > 3000) && (score <= 7000)) {
            level = 2;
        }

        if ((score > 1000) && (score <= 3000)) {
            level = 1;
        }

        if (score <= 1000) {
            level = 0;
        }

        color = (int) (Math.random() * 110) + 1;

        if (level == 0) {
            color = 1;
        }

        int xp = (int) (Math.random() * (getWidth()-blueP.getWidth()));
        int yp = (int) (Math.random() *
                10);
        yp = yp * -1;

        // 蓝色平台
        if ((color > 0) && (color <= 50)) {
            // 提高难度，减少蓝色普通平台数量
            if (level == 2) {
                color = (int) (Math.random() * 99) + 1;
            } else if (level == 3) {
                color = (int) (Math.random() * 70) + 30;
            } else if (level == 4) {
                color = (int) (Math.random() * 63) + 45;
            } else if (level == 5) {
                color = (int) (Math.random() * 63) + 45;
            }

            if ((color > 0) && (color <= 50)) {
                Platform plat1 = new Platform(2, xp, yp, blueP.getWidth(), blueP.getWidth());
                myPlatforms.add(plat1);
            }
        }

        // 橙色平台
        if ((color > 50) && (color <= 60) && (level > 1)) {
            Platform plat3 = new Platform(3, xp, yp, orangeP.getWidth(), orangeP.getHeight());
            myPlatforms.add(plat3);
        }

        if ((color > 50) && (color <= 60) && (level < 2)) {
            color = 62;
        }

        // 棕色平台
        if ((color > 60) && (color <= 70)) {
            Platform plat8 = new Platform(8, xp, yp, brownP0.getWidth(), brownP0.getHeight());
            myPlatforms.add(plat8);
        }

        // 白色平台
        if ((color > 70) && (color <= 80)) {
            if (level == 5) {
                color = (int) (Math.random() * 63) + 45;
            }

            if ((color > 70) && (color <= 80)) {
                Platform plat4 = new Platform(4, xp, yp, whiteP.getWidth(), whiteP.getHeight());
                myPlatforms.add(plat4);
            }
        }

        // 半透明橙色平台
        if ((color > 90) && (color <= 100)) {
            Platform plat5 = new Platform(5, xp, yp, dorangeP.getWidth(), dorangeP.getHeight());
            myPlatforms.add(plat5);
        }

        // 生成敌人
        if ((color > 100) && (color <= 105)) {
            Olga olga = (Olga) myGuys.get(0);

            if (level >= 1) {
                if (springCount < 1) {
                    if (olga.getY() > 200) {
                        if (myEnemies.size() < 1) {
                            generateEnemy();
                        }
                    }
                }
            }
        }

        // 带道具的平台
        if ((color > 105) && (color <= 108)) {
            Platform plat6 = new Platform(6, xp, yp, blueS0.getWidth(), blueS0.getHeight());
            myPlatforms.add(plat6);
        }
    }

    /**
     * 随机生成敌人
     * */
    public void generateEnemy() {
        int monX = (int) (Math.random() * getWidth()-enemyImg.getWidth());
        int monY = (int) (Math.random() * 100);
        if(monX < 0) monX = 0;

        Enemy en = new Enemy(1, monX, monY, enemyImg.getWidth(), enemyImg.getHeight());
        myEnemies.add(en);
    }

    /**
     * 创建子弹
     * */
    public void createBullet(int mx, int my) {
        Olga doodle = (Olga) myGuys.get(0);

        int dx = doodle.getX() + doodle.getWidth() / 2;
        int dy = doodle.getY() - 15;
        int dw = doodle.getWidth() / 2;

        Bullet bnew = new Bullet( dx, dy,12, bulletImg.getWidth(), bulletImg.getHeight());

        /* 创建一个三角形*/
        int triangleLeg = Math.abs(mx) - Math.abs(dx); // 底
        int triangleHeight = Math.abs(my) - Math.abs(dy); // 高

        int hypo = (int) Math.sqrt(Math.pow(triangleLeg, 2) + Math.pow(triangleHeight, 2)); // 斜边

        int numMoves = (int) hypo / 10;
        int legStep = (int) (mx - dx) / 10;
        int heightStep = (int) (my - dy) / 10;

        // 子弹的最小速度是40
        if ((legStep > -40) && (legStep < 0)) {
            legStep = -40;
        }
        if ((legStep < 40) && (legStep > 0)) {
            legStep = 40;
        }

        if ((heightStep > -40) && (heightStep < 0)) {
            heightStep = -40;
        }
        if ((heightStep < 40) && (heightStep > 0)) {
            heightStep = 40;
        }

        // 尽量保持速度一致
        while (Math.abs(legStep) > 10) {
            if (heightStep != 0) {
                heightStep = heightStep / 2;
            }
            if (legStep != 0) {
                legStep = legStep / 2;
            }
        }

        while (Math.abs(heightStep) > 10) {
            if (heightStep != 0) {
                heightStep = heightStep / 2;
            }
            if (legStep != 0) {
                legStep = legStep / 2;
            }
        }

        bnew.setLegStep(legStep);
        bnew.setHeightStep(heightStep);
        myBullets.add(bnew);
    }

    /**
     * 显示分数
     * */
    public void drawScores(){
        // 绘制背景
        canvas.drawBitmap(overBackground, 0, 0, paint);

        // 绘制分数
        paint.setTextSize(100);
        String scoreText = "得分";
        float textWidth = paint.measureText(scoreText); // 文字宽度
        canvas.drawText(scoreText,(getWidth()-textWidth)/2,200,paint);
        paint.setTextSize(200);
        String text = Integer.toString(score);
        textWidth = paint.measureText(text); // 文字宽度
        canvas.drawText(text,(getWidth()-textWidth)/2,500,paint);
        exit = true;
    }

    /**
     * 控制主角左右移动
     * */
    public void overrideLR(int f) {
        Olga olga2 = (Olga) myGuys.get(0);
        olga2.sethFacing(f); // 开始移动

        // 设置对应图片
        if (f == -1) { // 左
            myBitmaps.set(0, olgaLImg);
            olga2.setX(olga2.getX());
        } else if (f == 1) { // 右
            myBitmaps.set(0, olgaRImg);
            olga2.setX(olga2.getX());
        }
        myGuys.set(0, olga2);
    }

    private int fCount = 0;

    /**
     * 游戏更新
     * */
    public void update(){

        if ((gameOver) && (fCount == 0)) {
            fCount = 1;
        }
        // 游戏结束
        if(gameOver){
            drawScores();
        }
        // 游戏进行中
        if(gameOn){
            // 绘制背景
            canvas.drawBitmap(background, 0, 0, paint);

            Olga tempOlga = (Olga) myGuys.get(0);

            if(springCount < 1){
                SDV = -30;
                BSDS = 10;
                DSDS = 30;
            }
            if (springCount > 0) {
                springCount--;
            }

            // 角色向上运动
            shiftDown = false;

            // 角色下落
            if(tempOlga.getVelocity() > 0){
                shiftDown = true;
            }

            for (int k = 0; k < myPlatforms.size(); k++) {
                // 绘制平台
                Character tempPlatform = (Platform) myPlatforms.get(k);

                // 对不同的平台执行操作
                // 橙色平台
                if (tempPlatform.getId() == 3) {
                    updatePlat3(k, tempPlatform);
                }

                // 棕色平台
                if ((tempPlatform.getId() >= 8) && (tempPlatform.getId() <= 11)) {
                    updatePlat8(k, tempPlatform);
                }

                // 透明橙色平台
                if (tempPlatform.getId() == 5) {
                    updatePlat5(k, tempPlatform);
                }
                // 其它平台
                else {
                    canvas.drawBitmap(myBitmaps.get(tempPlatform.getId()),tempPlatform.getX(),tempPlatform.getY(),paint);
                }

                // 角色下落时平台加速下落
                if(shiftDown){
                    tempPlatform.changeY(BSDS+20);
                    score = score + 1;
                }
                //  角色移动时平台向下移动,但是速度更慢
                else{
                    tempPlatform.changeY(BSDS);
                    score = score + 1;
                }

                // 如果平台移出屏幕，生成新的平台
                if (tempPlatform.getY() > getHeight()-200) {
                    // 在区间生成新平台
                    if (creationCounter > ((int) (Math.random() * 7) + 5)) {
                        generateLiveRandomPlatform();
                        creationCounter = 0;
                    }
                }

                if (tempPlatform.getY() > getHeight()) {
                    myPlatforms.remove(k);

                }
            }

            if (myPlatforms.size() < 11) {
                generateLiveRandomPlatform();
            }

            // 找出有没有太大的空隙
            int each = getHeight()/4;
            int dis = 0;
            boolean empty = true;
            while(dis <= getHeight()){
                for (int i = 0; i < myPlatforms.size(); i++){
                    if((myPlatforms.get(i).getY() <= dis + each && myPlatforms.get(i).getY() >= dis) ||
                            myPlatforms.get(i).getId() == 8){
                        empty = false;

                    }
                }
                // 在当前区域存在大空隙
                if(empty){
                    // 生成一个普通平台
                    Platform plat2 = randomPlatform();
                    plat2.setY(dis + each/2);
                    myPlatforms.add(plat2);
                }
                empty = true;
                dis += each;
            }

            // 绘制敌人
            for (int w = 0; w < myEnemies.size(); w++) {
                updateEnemy(w, myEnemies.get(w));
            }

            // 绘制子弹
            for (int h = 0; h < myBullets.size(); h++) {
                Bullet tempBullet = (Bullet) myBullets.get(h);
                tempBullet.move();

                tempBullet.changeY(-BSDS);

                myBullets.set(h, tempBullet);
                canvas.drawBitmap(bulletImg,tempBullet.getX(),tempBullet.getY(),paint);

                if ((tempBullet.getX() > getWidth()) || (tempBullet.getX() < 0) || (tempBullet.getY() < 0) || (tempBullet.getY() > getHeight())) {
                    myBullets.remove(h);
                }

            }

            // 绘制角色
            canvas.drawBitmap(myBitmaps.get(tempOlga.getId()),tempOlga.getX(),tempOlga.getY(),paint);
            tempOlga.move();
            // 下落
            if (shiftDown) {
                tempOlga.changeY(DSDS + 30);
                creationCounter++;
            }

            // 绘制分数
            canvas.drawText("score: "+score,20,60,paint);
            canvas.drawText("level: "+level,500,60,paint);

            // 碰撞检测
            checkPlatformHit();
            checkBulletHit();
            checkDoodleGameOver();
        }
    }

    /**
     * 返回分数
     * */
    public int getScore(){
        return score;
    }

    public GameView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        holder = this.getHolder();
        holder.addCallback(this);
        paint = new Paint();
        paint.setTextSize(60);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
        gamePlay = (GamePlay)context;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false);
        load(); // 加载资源
        thread = new Thread(this); // 实例线程
        thread.start(); // 启动线程
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameOver = true;
    }

    @Override
    public void run() {
        while(!exit){
            try {
                canvas = holder.lockCanvas();
                update();
                // 绘制完成，释放画布，提交修改
                holder.unlockCanvasAndPost(canvas);
                Thread.sleep(30);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            finally {
                invalidate();
            }
        }
    }

    public boolean onTouch(MotionEvent event){
        if(gameOn){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                myBitmaps.set(0,olgaSImg); // 切换射击姿势的角色图片
                if(myBullets.size() < 5){
                    createBullet((int)event.getX(),(int)event.getY());
                }
            }
            if(event.getAction() == MotionEvent.ACTION_UP){
                myBitmaps.set(0,olgaRImg); // 切换默认角色图片
            }
        }
        return true;
    }
}
