package com.example.notschoolofdrums.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.notschoolofdrums.R;


public class Account extends Fragment {

    TextView changeBtn, professionTitle, studyTitle;
    CardView professionCard, studyCard;
    private OnChangeBtnClickListener OnChangeBtnClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        professionTitle = view.findViewById(R.id.profession_title);
        studyTitle = view.findViewById(R.id.study_title);
        professionCard = view.findViewById(R.id.profession_card);
        studyCard = view.findViewById(R.id.study_card);

        int index = getIndex(requireContext());
        if(index == 1){
            professionTitle.setVisibility(View.GONE);
            professionCard.setVisibility(View.GONE);
        } else if (index == 2){
            studyTitle.setVisibility(View.GONE);
            studyCard.setVisibility(View.GONE);
        }

        boolean backTextClicked = getValue(requireContext());
        if (backTextClicked){
            Animation slideInLeftAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_left);
            view.startAnimation(slideInLeftAnimation);
        }
        return view;
    }

    public interface OnChangeBtnClickListener {
        void OnChangeBtnClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnChangeBtnClickListener) {
            OnChangeBtnClickListener = (OnChangeBtnClickListener) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeBtn = view.findViewById(R.id.correct_text);
        changeBtn.setOnClickListener(v -> {
            if (OnChangeBtnClickListener != null) {
                OnChangeBtnClickListener.OnChangeBtnClick();
            }
        });
    }

    public static boolean getValue(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("BackTextIsClicked", Context.MODE_PRIVATE);
        return prefs.getBoolean("IsClicked", false);
    }

    public static int getIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AccountID", Context.MODE_PRIVATE);
        return prefs.getInt("Index", 0);
    }
}