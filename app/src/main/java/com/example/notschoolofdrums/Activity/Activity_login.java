package com.example.notschoolofdrums.Activity;

import static android.view.View.*;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.notschoolofdrums.Fragments.Log_choice;
import com.example.notschoolofdrums.Fragments.Login;
import com.example.notschoolofdrums.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Activity_login extends AppCompatActivity implements Login.OnEntryButtonClickListener {

    Button Back_button;
    FrameLayout login_frame;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();

        Back_button = findViewById(R.id.Back_button);
        login_frame = findViewById(R.id.login_frame);

        ChangeFragment();

        Back_button.setOnClickListener(v -> ChangeFragment());
    }

    private void ChangeFragment() {
        Log_choice logChoice = new Log_choice();
        FragmentTransaction FT = getSupportFragmentManager().beginTransaction();
        FT.replace(R.id.login_frame, logChoice).commit();
        Back_button.setVisibility(INVISIBLE);
    }

    @Override
    public void onEntryButtonClick(String login, String password) {
        fAuth.signInWithEmailAndPassword(login, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                finish();
                Intent intent = new Intent(Activity_login.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void setButtonVisibility(boolean isVisible){
        Back_button.setVisibility(VISIBLE);
    }

}