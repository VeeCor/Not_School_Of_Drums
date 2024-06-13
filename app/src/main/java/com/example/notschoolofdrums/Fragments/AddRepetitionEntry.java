package com.example.notschoolofdrums.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.example.notschoolofdrums.R;
import com.google.android.material.textfield.TextInputLayout;

public class AddRepetitionEntry extends Fragment {

    TextInputLayout dateInput, timeInput;
    AutoCompleteTextView dateAutoInput, timeAutoInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_add_repetition_entry, container, false);

        dateInput = view.findViewById(R.id.date_input);
        timeInput = view.findViewById(R.id.time_input);
        dateAutoInput = view.findViewById(R.id.date_auto_input);
        timeAutoInput = view.findViewById(R.id.time_auto_input);



        return view;
    }


}