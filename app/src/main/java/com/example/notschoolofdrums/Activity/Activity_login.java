package com.example.notschoolofdrums.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.notschoolofdrums.Fragments.Log_choice;
import com.example.notschoolofdrums.Fragments.Login;
import com.example.notschoolofdrums.R;

public class Activity_login extends AppCompatActivity {

    public Button Back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Log_choice logChoice = new Log_choice();
        FragmentTransaction lc = getSupportFragmentManager().beginTransaction();
        lc.replace(R.id.login_frame, logChoice).commit();

        Back_button = findViewById(R.id.Back_button);
        Back_button.setVisibility(View.INVISIBLE);

    }


}