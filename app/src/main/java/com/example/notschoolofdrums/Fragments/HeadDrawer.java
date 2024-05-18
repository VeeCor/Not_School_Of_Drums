package com.example.notschoolofdrums.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.notschoolofdrums.R;

public class HeadDrawer extends Fragment {

    ImageView photo;
    TextView username;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_head_drawer, container, false);

        photo = view.findViewById(R.id.photo_profile_drawer);
        username = view.findViewById(R.id.username_drawer);



        return view;
    }
}