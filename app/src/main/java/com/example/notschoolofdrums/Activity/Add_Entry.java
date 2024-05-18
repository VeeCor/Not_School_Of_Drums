package com.example.notschoolofdrums.Activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.notschoolofdrums.Fragments.Add_Entry_step1;
import com.example.notschoolofdrums.R;

public class Add_Entry extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        toolbar = findViewById(R.id.new_entry_toolbar);
        ChangeFragment();

        toolbar.setNavigationOnClickListener(v -> {
            setToolbarTitle(getString(R.string.title_new_entry));
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void ChangeFragment(){
        FragmentTransaction FT = getSupportFragmentManager().beginTransaction();
        FT.replace(R.id.frame_for_fragments_add_entry, new Add_Entry_step1()).commit();
    }

    public void setToolbarTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }
}