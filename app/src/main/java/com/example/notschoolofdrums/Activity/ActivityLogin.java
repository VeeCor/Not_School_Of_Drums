package com.example.notschoolofdrums.Activity;

import static android.content.ContentValues.TAG;
import static android.view.View.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.notschoolofdrums.Fragments.LogChoice;
import com.example.notschoolofdrums.Fragments.Login;
import com.example.notschoolofdrums.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ActivityLogin extends AppCompatActivity implements Login.OnEntryButtonClickListener {

    Button Back_button;
    FrameLayout login_frame;
    FirebaseAuth fAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Back_button = findViewById(R.id.Back_button);
        login_frame = findViewById(R.id.login_frame);

        ChangeFragment();

        Back_button.setOnClickListener(v -> ChangeFragment());
    }

    private void ChangeFragment() {
        LogChoice logChoice = new LogChoice();
        FragmentTransaction FT = getSupportFragmentManager().beginTransaction();
        FT.replace(R.id.login_frame, logChoice).commit();
        Back_button.setVisibility(INVISIBLE);
    }

    @Override
    public void onEntryButtonClick(String login, String password) {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if (networkCapabilities != null) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                fAuth.signInWithEmailAndPassword(login, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CheckAccount();
                    } else {
                        Login loginFragment = new Login();
                        loginFragment.singInError();
                    }
                });
            }
        } else {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            Login loginFragment = (Login) getSupportFragmentManager().findFragmentById(R.id.login_relative);
            assert loginFragment != null;
            ProgressBar progressBar = loginFragment.getProgressBar();
            progressBar.setVisibility(GONE);
        }
    }

    private void CheckAccount(){
        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    int index = getIndex(this);
                    String accType = document.getString("accType");
                    assert accType != null;
                    if ((accType.equals("Ученик") && index == 1) || (accType.equals("Преподаватель") && index == 2) || (accType.equals("Администратор") && index == 3) || (accType.equals("Менеджер") && index == 4)){
                        finish();
                        Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Login loginFragment = new Login();
                        loginFragment.singInError();
                    }
                }
            } else {
                Log.d(TAG, "Get failed with ", task.getException());
            }
        });
    }

    @Override
    public void setButtonVisibility(boolean isVisible){
        Back_button.setVisibility(VISIBLE);
    }

    public static int getIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AccountID", Context.MODE_PRIVATE);
        return prefs.getInt("Index", 0);
    }
}