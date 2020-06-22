package com.example.dontstop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.usage.UsageEvents;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;

public class GamePlay extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {

    Intent bgmInt;
    Intent menuInt; // 返回菜单

    public ImageButton leftBtn,rightBtn; // 左右移动按钮
    ImageButton shotBtn; // 射击按钮
    ImageButton backBtn; // 返回按钮

    DBAdapter dbAdapter;
    private Player olga;

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        dbAdapter = new DBAdapter(this);
        olga = new Player();
        olga.name = "olga";
        olga.score = 0;

        // 定位到按钮
        leftBtn = findViewById(R.id.leftBtn);
        leftBtn.setOnClickListener(this);
        rightBtn = findViewById(R.id.rightBtn);
        rightBtn.setOnClickListener(this);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        shotBtn = findViewById(R.id.shotBtn);
        shotBtn.setOnTouchListener(this);
        menuInt = new Intent(GamePlay.this,MainActivity.class);
        gameView = findViewById(R.id.gameView);

        // 启动服务播放背景音乐
        bgmInt = new Intent(GamePlay.this,MusicIntentService.class);
        String action = MusicIntentService.ACTION_MUSIC;
        // 设置action
        bgmInt.setAction(action);
        startService(bgmInt);
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    /**
     * 显示消息框
     * */
    private void showDialog(){
        gameView.gameOn = false; // 先暂停游戏
        final AlertDialog.Builder dialog = new AlertDialog.Builder(GamePlay.this);
        dialog.setTitle("提示");
        dialog.setMessage("确定要停下来吗?");
        dialog.setPositiveButton("停下",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface,int which){
                        gameView.exit = true;
                        startActivity(menuInt);
                    }
                });
        dialog.setNegativeButton("不要停下",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface,int which){
                        gameView.gameOn = true;
                    }
                });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.leftBtn:
                gameView.overrideLR(-1);
                break;
            case R.id.rightBtn:
                gameView.overrideLR(1);
                break;
            case R.id.backBtn:
                // 若游戏结束直接返回
                if(gameView.gameOver){
                    this.stopService(bgmInt);
                    olga.score = gameView.getScore();

                    dbAdapter.open();
                    // 更新数据库
                    if(dbAdapter.getDate("olga").score < olga.score) {
                        dbAdapter.updateOneData("olga", olga);
                    }
                    dbAdapter.close();

                    startActivity(menuInt);
                }
                else{
                    showDialog();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v,MotionEvent event) {
        if(v.getId() == R.id.shotBtn){
            gameView.onTouch(event);
            gameView.postInvalidate();
        }
        return super.onTouchEvent(event);
    }
}
