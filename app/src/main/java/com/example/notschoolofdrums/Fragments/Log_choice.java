package com.example.notschoolofdrums.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.notschoolofdrums.R;


public class Log_choice extends Fragment {

    Button Student, Teacher, Admin, Manager, Back_button;
    public int index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_log_choice, container, false);

        Student = view.findViewById(R.id.student_button);
        Teacher = view.findViewById(R.id.teacher_button);
        Admin = view.findViewById(R.id.admin_button);
        Manager = view.findViewById(R.id.manager_button);

        Student.setOnClickListener(v -> {
            index = 1;
            ChangeFragment();
        });

        Teacher.setOnClickListener(v -> {
            index = 2;
            ChangeFragment();
        });

        Admin.setOnClickListener(v -> {
            index = 3;
            ChangeFragment();
        });

        Manager.setOnClickListener(v -> {
            index = 4;
            ChangeFragment();
        });

        return view;
    }

    private void ChangeFragment() {
        Login Login = new Login();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.login_frame, Login).commit();
        Toast.makeText(getActivity(), String.valueOf(index), Toast.LENGTH_LONG).show();
    }


}