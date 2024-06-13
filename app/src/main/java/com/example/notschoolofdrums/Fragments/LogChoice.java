package com.example.notschoolofdrums.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.notschoolofdrums.R;

public class LogChoice extends Fragment {

    Button Student, Teacher, Admin, Manager;
    public int index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_log_choice, container, false);

        Animation slideInRightAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_slide_in_from_left);
        view.startAnimation(slideInRightAnimation);

        Student = view.findViewById(R.id.student_button);
        Teacher = view.findViewById(R.id.teacher_button);
        Admin = view.findViewById(R.id.admin_button);
        Manager = view.findViewById(R.id.manager_button);

        Student.setOnClickListener(getClickListener(1));
        Teacher.setOnClickListener(getClickListener(2));
        Admin.setOnClickListener(getClickListener(3));
        Manager.setOnClickListener(getClickListener(4));

        return view;
    }

    private View.OnClickListener getClickListener(final int newIndex) {
        return v -> {
            index = newIndex;
            ChangeFragment();
        };
    }

    public void ChangeFragment() {
        if (getActivity() != null) {
            Login loginFragment = new Login();
            if (getContext() != null) {
                saveIndex(getContext(), index);
            }
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.login_frame, loginFragment).commit();
        }
    }

    public static void saveIndex(Context context, int index) {
        SharedPreferences.Editor editor = context.getSharedPreferences("AccountID", Context.MODE_PRIVATE).edit();
        editor.putInt("Index", index);
        editor.apply();
    }
}