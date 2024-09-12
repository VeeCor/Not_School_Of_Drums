package com.example.notschoolofdrums.Activity;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.notschoolofdrums.Fragments.AddEntryStep1;
import com.example.notschoolofdrums.Fragments.AddLessonEntry;
import com.example.notschoolofdrums.Fragments.AddRepetitionEntry;
import com.example.notschoolofdrums.R;

public class AddEntryActivity extends AppCompatActivity implements AddLessonEntry.OnFinishButtonClickListener, AddRepetitionEntry.OnFinishButtonClickListener {

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
        FT.replace(R.id.frame_for_fragments_add_entry, new AddEntryStep1()).commit();
    }

    public void setToolbarTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @Override
    public void onFinishButtonClick() {
        finish();
    }
}