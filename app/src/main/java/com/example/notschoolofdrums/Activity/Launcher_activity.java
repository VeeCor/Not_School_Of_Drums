package com.example.notschoolofdrums.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notschoolofdrums.R;

import java.util.Timer;
import java.util.TimerTask;

public class Launcher_activity extends AppCompatActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Launcher_activity.this, Activity_login.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}