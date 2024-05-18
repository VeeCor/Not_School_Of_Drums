package com.example.notschoolofdrums.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.notschoolofdrums.R;

public class Account_change extends Fragment {

    TextView backText;
    Boolean backTextClicked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_change, container, false);

        Animation slideInLeftAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_right);
        view.startAnimation(slideInLeftAnimation);

        backText = view.findViewById(R.id.back_text);
        backText.setOnClickListener(v -> {
            backTextClicked = true;
            saveValue(requireContext(), backTextClicked);
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        return view;
    }

    public static void saveValue(Context context, boolean backTextClicked) {
        SharedPreferences.Editor editor = context.getSharedPreferences("BackTextIsClicked", Context.MODE_PRIVATE).edit();
        editor.putBoolean("IsClicked", backTextClicked);
        editor.apply();
    }
}