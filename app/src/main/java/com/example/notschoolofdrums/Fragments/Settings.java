package com.example.notschoolofdrums.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.notschoolofdrums.R;

public class Settings extends Fragment {

    private OnExitButtonClickListener Listener;
    Button accountBtn, exitBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Animation slideInLeftAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_right);
        view.startAnimation(slideInLeftAnimation);

        accountBtn = view.findViewById(R.id.profile_button);

        //TODO: Сделать всплывающее окно с подтверждением выхода

        return view;
    }

    public interface OnExitButtonClickListener {
        void OnExitButtonClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnExitButtonClickListener) {
            Listener = (OnExitButtonClickListener) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exitBtn = view.findViewById(R.id.exit_button);
        exitBtn.setOnClickListener(v -> {
            if (Listener != null) {
                Listener.OnExitButtonClick();
            }
        });
    }
}