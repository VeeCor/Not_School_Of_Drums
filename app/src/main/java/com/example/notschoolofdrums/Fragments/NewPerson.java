package com.example.notschoolofdrums.Fragments;


import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.notschoolofdrums.EmailChooserActivity;
import com.example.notschoolofdrums.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class NewPerson extends Fragment {

    String[] accountTypes = {"Ученик", "Преподаватель", "Администратор", "Менеджер"};
    String[] genders = {"Мужской", "Женский"};
    String[] professions = {"Барабанщик", "Барабанщик/Гитарист", "Барабанщик/Гитарист/Вокалист"};
    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();
    private boolean isFormatting, hasErrors;
    private int prevLength;
    AutoCompleteTextView AutoCompleteInput, professionAutoInput, birthdayAutoInput, genderAutoInput;
    TextInputEditText lastNameEditText, nameEditText, surnameEditText, phoneEditText, emailEditText;
    TextInputLayout accountInput, professionInput, lastNameInput, nameInput, surnameInput, birthdayInput, genderInput, phoneInput, emailInput;
    SimpleDateFormat dateFormat;
    Button AddPersonBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_person, container, false);

        AutoCompleteInput = view.findViewById(R.id.autocomplete_input);
        professionAutoInput = view.findViewById(R.id.profession_auto_input);
        birthdayAutoInput = view.findViewById(R.id.birthday_auto_input);
        genderAutoInput = view.findViewById(R.id.gender_auto_input);

        accountInput = view.findViewById(R.id.account_type_input);
        professionInput = view.findViewById(R.id.profession_input);
        lastNameInput = view.findViewById(R.id.last_name_input);
        nameInput = view.findViewById(R.id.name_input);
        surnameInput = view.findViewById(R.id.surname_input);
        birthdayInput = view.findViewById(R.id.birthday_input);
        genderInput = view.findViewById(R.id.gender_input);
        phoneInput = view.findViewById(R.id.number_input);
        emailInput = view.findViewById(R.id.email_input);

        lastNameEditText = view.findViewById(R.id.last_name_editText);
        nameEditText = view.findViewById(R.id.name_editText);
        surnameEditText = view.findViewById(R.id.surname_editText);
        phoneEditText = view.findViewById(R.id.number_editText);
        emailEditText = view.findViewById(R.id.email_editText);

        AddPersonBtn = view.findViewById(R.id.add_person_button);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        setupAutoCompleteTextView(AutoCompleteInput, accountTypes);
        setupAutoCompleteTextView(genderAutoInput, genders);
        setupAutoCompleteTextView(professionAutoInput, professions);

        OnFocusEditText(lastNameInput, lastNameEditText);
        OnFocusEditText(nameInput, nameEditText);
        OnFocusEditText(phoneInput, phoneEditText);
        OnFocusEditText(emailInput, emailEditText);
        OnFocusAutoCompleteText(accountInput, AutoCompleteInput);
        OnFocusAutoCompleteText(birthdayInput, birthdayAutoInput);
        OnFocusAutoCompleteText(genderInput, genderAutoInput);

        emailEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                emailEditText.clearFocus();
            }
            return false;
        });

        birthdayInput.setEndIconOnClickListener(v -> showDatePickerDialog());

        AutoCompleteInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String accType = AutoCompleteInput.getText().toString();
                if (accType.equals("Преподаватель") || accType.equals("преподаватель")){
                    professionInput.setVisibility(View.VISIBLE);
                } else {
                    professionAutoInput.setText("");
                    professionInput.setVisibility(View.GONE);
                }
            }
        });

        birthdayAutoInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isFormatting) {
                    prevLength = s.length();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) {
                    return;
                }

                int length = s.length();
                if (prevLength < length && length == 2) {
                    isFormatting = true;
                    s.append(".");
                } else if (prevLength < length && length == 5) {
                    isFormatting = true;
                    s.insert(length, ".");
                }

                isFormatting = false;
            }
        });

        AddPersonBtn.setOnClickListener(v -> {
            String accType = AutoCompleteInput.getText().toString();
            String profession = professionAutoInput.getText().toString();
            String lastName = Objects.requireNonNull(lastNameEditText.getText()).toString();
            String name = Objects.requireNonNull(nameEditText.getText()).toString();
            String surname = Objects.requireNonNull(surnameEditText.getText()).toString();
            String birthday = birthdayAutoInput.getText().toString();
            String gender = genderAutoInput.getText().toString();
            String phone = Objects.requireNonNull(phoneEditText.getText()).toString();
            String email = Objects.requireNonNull(emailEditText.getText()).toString();

            if(TextUtils.isEmpty(accType)){
                setHelper(accountInput, R.string.account_type_helper);
            } else if (!(accType.equals("Ученик") || accType.equals("ученик") ||
                    accType.equals("Преподаватель")|| accType.equals("преподаватель")||
                    accType.equals("Администратор")||accType.equals("администратор")||
                    accType.equals("Менеджер") || accType.equals("менеджер"))) {
                setHelper(accountInput, R.string.wrong_account_type_helper);
            }
            if (professionAutoInput.getVisibility() == View.VISIBLE || TextUtils.isEmpty(profession)) {
                setHelper(professionInput, R.string.no_profession);
            } else if (!(accType.equals("Барабанщик") || accType.equals("Барабанщик/Гитарист") || accType.equals("Барабанщик/Гитарист/Вокалист"))){
                setHelper(professionInput, R.string.wrong_profession);
            }
            if (TextUtils.isEmpty(lastName)) {
                setHelper(lastNameInput, R.string.no_last_name);
            }
            if (TextUtils.isEmpty(name)) {
                setHelper(nameInput, R.string.no_name);
            }
            if (TextUtils.isEmpty(birthday)) {
                setHelper(birthdayInput, R.string.no_birthday);
            }
            if (TextUtils.isEmpty(gender)) {
                setHelper(genderInput, R.string.no_gender);
            } else if (!(gender.equals("Мужской") || gender.equals("Женский"))) {
                setHelper(accountInput, R.string.wrong_gender_helper);
            }
            if (TextUtils.isEmpty(phone)) {
                setHelper(phoneInput, R.string.no_phone);
            }
            if (TextUtils.isEmpty(email)) {
                setHelper(emailInput, R.string.no_email);
            } else if (!(email.endsWith("@gmail.com") || email.endsWith("@mail.ru") || email.endsWith("@yandex.ru") || email.endsWith("@icloud.com"))) {
                setHelper(emailInput, R.string.wrong_email);
            }

            if(!hasErrors){
                String password = generateRandomPassword();
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String body = String.format(Locale.getDefault(),
                                "%s, мы рады видеть тебя в наших рядах!\n" +
                                        "Скорее скачивай приложение \"Не школа барабанов\" и записывайся на первое занятие. Данные для входа в твой аккаунт:\n\n\n" +
                                        "Логин: %s\n" +
                                        "Пароль: %s\n\n\n" +
                                        "Удачи!", name, email, password);

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Добро пожаловать в Не школу барабанов!");
                        intent.putExtra(Intent.EXTRA_TEXT, body);
                        startActivity(intent);

                        Map<String, Object> user = new HashMap<>();
                        user.put("accType", accType);
                        if (!TextUtils.isEmpty(profession)){
                            user.put("profession", profession);
                        }
                        user.put("lastName", lastName);
                        user.put("name", name);
                        if(!TextUtils.isEmpty(surname)){
                            user.put("surname", surname);
                        }
                        user.put("birthday", birthday);
                        user.put("gender", gender);
                        user.put("phone", phone);
                        user.put("email", email);

                        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                        CollectionReference collRef = db.collection("users");
                        collRef.document(uid).set(user)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document added successfully!"))
                                .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));

                        Toast.makeText(requireContext(), "Аккаунт успешно создан", Toast.LENGTH_LONG).show();
                    }else {
                            Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return view;
    }

    private void setupAutoCompleteTextView(AutoCompleteTextView item, String[] value) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.list_for_items, value);
        item.setAdapter(adapter);
        item.setOnItemClickListener((parent, view, position, id) -> cleanFocus(item));
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    calendar.set(year1, monthOfYear, dayOfMonth);
                    birthdayAutoInput.setText(dateFormat.format(calendar.getTime()));
                },
                year, month, day);

        datePickerDialog.show();
        cleanFocus(birthdayAutoInput);
    }

    private void cleanFocus(AutoCompleteTextView item){
        item.clearFocus();
    }

    private void setHelper (TextInputLayout item, int text){
        item.setHelperText(getString(text));
        item.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.error_red)));
        hasErrors = true;
    }

    private void OnFocusEditText (TextInputLayout item, TextInputEditText editText){
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                item.setHelperText("");
                hasErrors = false;
            }
        });
    }

    private void OnFocusAutoCompleteText (TextInputLayout item, AutoCompleteTextView editText){
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                item.setHelperText("");
                hasErrors = false;
            }
        });
    }

    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = random.nextInt(CHAR_SET.length());
            password.append(CHAR_SET.charAt(randomIndex));
        }
        return password.toString();
    }
}