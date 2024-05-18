package com.example.notschoolofdrums.Fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.notschoolofdrums.R;

public class Add_news extends Fragment {

    ImageButton close;
    private float dY, initialY, screenHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_news, container, false);

        Animation slideInBottomAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom);
        view.startAnimation(slideInBottomAnimation);

        close = view.findViewById(R.id.close_item);
        close.setOnClickListener(v -> CloseFragment());

        screenHeight = getResources().getDisplayMetrics().heightPixels;

        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    dY = v.getY() - event.getRawY();
                    initialY = v.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float newY = event.getRawY() + dY;
                    if (newY >= initialY) {
                        v.setY(newY);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if ((v.getY() / screenHeight) < 0.35) {
                        v.animate().y(initialY).setDuration(400);
                    } else {
                        CloseFragment();
                    }
                    break;

                default:
                    return false;
            }
            view.performClick();
            return true;
        });

        return view;
    }

    private void CloseFragment() {
        Animation slideOutBottomAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom);
        requireView().startAnimation(slideOutBottomAnimation);
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }
}
