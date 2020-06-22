package com.example.dontstop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Score extends AppCompatActivity {

    ImageButton backBtn; // 返回按钮

    Intent menuInt;

    public Player[] players; // 玩家数组

    DBAdapter dbAdapter;

    TextView olgaText;
    TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        backBtn = findViewById(R.id.backBtn);
        olgaText = findViewById(R.id.olga);
        scoreText = findViewById(R.id.score);

        dbAdapter = new DBAdapter(this);

        menuInt = new Intent(Score.this,MainActivity.class);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(menuInt);
            }
        });

        dbAdapter.open();
        players = dbAdapter.getAllData(); // 获取所有玩家
        dbAdapter.close();

        display();
    }

    /**
     * 显示玩家信息
     * */
    private  void display(){
        dbAdapter.open();

        players = dbAdapter.getAllData();

        olgaText.setText(players[0].name);
        scoreText.setText(Integer.toString(players[0].score));
    }
}
