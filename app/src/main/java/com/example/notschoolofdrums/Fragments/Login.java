package com.example.notschoolofdrums.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.notschoolofdrums.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class Login extends Fragment {
    String login, password;
    Button entry;
    ProgressBar progressBar;
    TextInputLayout loginInput, passwordInput;
    TextInputEditText loginEditText, passwordEditText;
    private OnEntryButtonClickListener listener;
    FirebaseAuth fAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Animation slideInLeftAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_slide_in_from_right);
        view.startAnimation(slideInLeftAnimation);

        loginInput = view.findViewById(R.id.login_log_input);
        passwordInput = view.findViewById(R.id.login_password_input);
        loginEditText = view.findViewById(R.id.login_log_editText);
        passwordEditText = view.findViewById(R.id.login_password_editText);
        progressBar = view.findViewById(R.id.login_progress);

        fAuth = FirebaseAuth.getInstance();

        loginInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                singOutError();
            }
        });

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                passwordEditText.clearFocus();
            }
            return false;
        });

        return view;
    }

    public interface OnEntryButtonClickListener {
        void onEntryButtonClick(String login, String password);
        void setButtonVisibility(boolean isVisible);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnEntryButtonClickListener) {
            listener = (OnEntryButtonClickListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) {
            listener.setButtonVisibility(true);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        entry = view.findViewById(R.id.entry_button);
        entry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            login = Objects.requireNonNull(loginEditText.getText()).toString();
            password = Objects.requireNonNull(passwordEditText.getText()).toString();
            int index = getIndex(requireContext());
            if(TextUtils.isEmpty(login) || TextUtils.isEmpty(password)){
                singInError();
            } else if (listener != null) {
                if(index == 4){
                    saveAuth(requireContext(), login,password);
                }
                listener.onEntryButtonClick(login, password);
            }
        });
    }

    public static void saveAuth(Context context, String login, String password) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Auth", Context.MODE_PRIVATE).edit();
        editor.putString("Login", login);
        editor.putString("Password", password);
        editor.apply();
    }

    public void singInError (){
        passwordInput.setHelperText(getString(R.string.error_login_password));
        passwordInput.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.error_red)));
    }

    public void singOutError (){
        passwordInput.setHelperText("");
    }

    public static int getIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AccountID", Context.MODE_PRIVATE);
        return prefs.getInt("Index", 0);
    }
    public ProgressBar getProgressBar() {
        return progressBar;
    }
}