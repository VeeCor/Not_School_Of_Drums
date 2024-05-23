package com.example.notschoolofdrums.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.notschoolofdrums.Activity.AddEntry;
import com.example.notschoolofdrums.R;


public class AddEntryStep1 extends Fragment {
    CardView lesson, repetition;
    FrameLayout less, rep;
    Button next;
    TextView name;
    FrameLayout line;
    int isChecked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add__entry_step1, container, false);
        lesson = view.findViewById(R.id.card_lesson_chose);
        repetition = view.findViewById(R.id.card_repetition_chose);
        next = view.findViewById(R.id.next_button);
        less = lesson.findViewById(R.id.less_frame);
        rep = repetition.findViewById(R.id.rep_frame);
        name = view.findViewById(R.id.student_name_text);
        line = view.findViewById(R.id.line);

        int index = getIndex(requireContext());
        if (index == 1){
            name.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
//            TODO: добавить вставку имени из базы данных
        }
        else {
            name.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }

        lesson.setOnClickListener(v -> {
            isChecked = 1;
            ChangeAlpha(isChecked);
        });

        repetition.setOnClickListener(v -> {
            isChecked = 2;
            ChangeAlpha(isChecked);
        });

        next.setOnClickListener(v -> ChangeFragment());

        return view;
    }

    private void ChangeFragment() {
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        if (isChecked == 1){
            ft.replace(R.id.frame_for_fragments_add_entry, new AddLessonEntry());
            ((AddEntry) requireActivity()).setToolbarTitle(getString(R.string.lesson));
        } else if (isChecked == 2) {
            ft.replace(R.id.frame_for_fragments_add_entry, new AddRepetitionEntry());
            ((AddEntry) requireActivity()).setToolbarTitle(getString(R.string.repetition));
        }
        ft.addToBackStack(null).commit();
    }

    private void ChangeAlpha(int isChecked) {
        if(isChecked == 1){
            less.setBackgroundResource(R.color.card_chosen_foreground_alpha);
            rep.setBackgroundResource(R.color.card_not_chosen_foreground_alpha);
            lesson.setCardElevation(30);
            repetition.setCardElevation(0);
        }else if (isChecked == 2){
            rep.setBackgroundResource(R.color.card_chosen_foreground_alpha);
            less.setBackgroundResource(R.color.card_not_chosen_foreground_alpha);
            repetition.setCardElevation(30);
            lesson.setCardElevation(0);
        }
    }

    public static int getIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AccountID", Context.MODE_PRIVATE);
        return prefs.getInt("Index", 0);
    }
}