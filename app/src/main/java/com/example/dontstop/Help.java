package com.example.dontstop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Help extends AppCompatActivity {

    ImageButton backBtn; // 返回按钮

    Intent menuInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        backBtn = findViewById(R.id.backBtn);
        menuInt = new Intent(Help.this,MainActivity.class);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(menuInt);
            }
        });
    }
}
