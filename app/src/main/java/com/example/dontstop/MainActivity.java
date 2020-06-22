package com.example.dontstop;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener,View.OnTouchListener {

    private ImageButton beginBtn; // 开始按钮
    private ImageButton helpBtn; // 帮助按钮
    private ImageButton scoreBtn; // 分数按钮

    Intent gameInt; // 进入游戏
    Intent helpInt; // 帮助
    Intent scoreInt; // 分数

    DBAdapter dbAdapter;

    private Player []players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 检查角色是否在数据库中
        // 查找是否存在，不存在就创建角色
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        players = dbAdapter.getAllData();
        if(players == null){
            dbAdapter.insert(new Player("olga",0));
        }
        dbAdapter.close();

        beginBtn = (ImageButton)findViewById(R.id.beginBtn);
        beginBtn.setOnTouchListener(this);
        beginBtn.setOnClickListener(this);
        helpBtn = (ImageButton)findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(this);
        helpBtn.setOnTouchListener(this);
        scoreBtn = (ImageButton)findViewById(R.id.scoreBtn);
        scoreBtn.setOnClickListener(this);
        scoreBtn.setOnTouchListener(this);

        gameInt = new Intent(MainActivity.this,GamePlay.class);
        helpInt = new Intent(MainActivity.this,Help.class);
        scoreInt = new Intent(MainActivity.this,Score.class);
    }

    /**
     * 放大按钮动画
     */
    private void press(View v) {
        float[] vaules = new float[] {
                0.8f, 0.9f, 1.0f
        };
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(v, "scaleX", vaules),
                ObjectAnimator.ofFloat(v, "scaleY", vaules));
        set.setDuration(150);  set.start();
    }

    /**
     * 缩小按钮动画
     */
    private void release(View v) {
        float[] vaules = new float[] {
                1.0f, 0.9f, 0.8f
        };
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(v, "scaleX", vaules),
                ObjectAnimator.ofFloat(v, "scaleY", vaules));
        set.setDuration(150);  set.start();
    }

    /**
     *  按钮按下特效
     *  */
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            press(v);
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            release(v);
        }
        return false;
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.beginBtn:
                startActivity(gameInt);
                break;
            case R.id.helpBtn:
                startActivity(helpInt);
                break;
            case R.id.scoreBtn:
                startActivity(scoreInt);
                break;
        }
    }
}
