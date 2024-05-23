package com.example.notschoolofdrums.Fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.notschoolofdrums.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class Account extends Fragment {

    TextView changeBtn, professionTitle, studyTitle, accountTypeValue, accountNameValue, professionValue, levelValue, lessonValue, statusValue, birthdayValue, genderValue, phoneValue, emailValue, aboutMeValue;
    CardView professionCard, studyCard;
    ImageView accountPhoto;
    private OnChangeBtnClickListener OnChangeBtnClickListener;
    FirebaseAuth fAuth;
    FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getDataFromDB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        professionTitle = view.findViewById(R.id.profession_title);
        studyTitle = view.findViewById(R.id.study_title);
        accountTypeValue = view.findViewById(R.id.account_type);
        accountNameValue = view.findViewById(R.id.fio_account);
        professionValue = view.findViewById(R.id.database_text_profession);
        levelValue = view.findViewById(R.id.database_text_level);
        lessonValue = view.findViewById(R.id.database_text_lesson);
        statusValue = view.findViewById(R.id.database_text_status);
        birthdayValue = view.findViewById(R.id.database_text_birthday);
        genderValue = view.findViewById(R.id.database_text_gender);
        phoneValue = view.findViewById(R.id.database_text_phone);
        emailValue = view.findViewById(R.id.database_text_email);
        aboutMeValue = view.findViewById(R.id.database_text_about_me);

        accountPhoto = view.findViewById(R.id.photo_profile);

        professionCard = view.findViewById(R.id.profession_card);
        studyCard = view.findViewById(R.id.study_card);


        int index = getIndex(requireContext());
        if (index == 1) {
            professionTitle.setVisibility(View.GONE);
            professionCard.setVisibility(View.GONE);
        } else if (index == 2) {
            studyTitle.setVisibility(View.GONE);
            studyCard.setVisibility(View.GONE);
        } else {
            professionTitle.setVisibility(View.GONE);
            professionCard.setVisibility(View.GONE);
            studyTitle.setVisibility(View.GONE);
            studyCard.setVisibility(View.GONE);
        }

        boolean backTextClicked = getValue(requireContext());
        if (backTextClicked) {
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

    private void getDataFromDB() {
        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String accType = document.getString("accType");
                    String profession = document.getString("profession");
                    String lastName = document.getString("lastName");
                    String name = document.getString("name");
                    String birthday = document.getString("birthday");
                    String gender = document.getString("gender");
                    String phone = document.getString("phone");
                    String email = document.getString("email");
                    String level = document.getString("level");
                    String lesson = document.getString("lesson");
                    String status = document.getString("status");
                    String aboutMe = document.getString("aboutMe");

                    String accountName = name + " " + lastName;
                    String phoneNumber = "+7" + phone;

                    accountTypeValue.setText(accType);
                    accountNameValue.setText(accountName);
                    professionValue.setText(profession);
                    levelValue.setText(level);
                    lessonValue.setText(lesson);
                    statusValue.setText(status);
                    birthdayValue.setText(birthday);
                    genderValue.setText(gender);
                    phoneValue.setText(phoneNumber);
                    emailValue.setText(email);
                    aboutMeValue.setText(aboutMe);

                    Log.d(TAG, "OK");
                } else {
                    Log.d(TAG, "Document doesn't exist");
                }
            } else {
                Log.d(TAG, "Get failed with ", task.getException());
            }
        });

    }
}