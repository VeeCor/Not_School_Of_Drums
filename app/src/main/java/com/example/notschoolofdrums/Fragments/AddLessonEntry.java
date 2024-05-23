package com.example.notschoolofdrums.Fragments;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.notschoolofdrums.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class AddLessonEntry extends Fragment {
    String[] consist = {"Группа", "Индивидуально"};
    TextInputLayout consistInput, teacherInput, dateInput, timeInput;
    AutoCompleteTextView consistAutoInput, teacherAutoInput, dateAutoInput, timeAutoInput;
    Button finish;
    SimpleDateFormat dateFormat;
    private boolean isFormatting, hasErrors;
    private int prevLength;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_add_lesson_entry, container, false);

        db = FirebaseFirestore.getInstance();

        finish = view.findViewById(R.id.next_button);
        consistInput = view.findViewById(R.id.consist_input);
        teacherInput = view.findViewById(R.id.teacher_input);
        dateInput = view.findViewById(R.id.date_input);
        timeInput = view.findViewById(R.id.time_input);
        consistAutoInput = view.findViewById(R.id.consist_auto_input);
        teacherAutoInput = view.findViewById(R.id.teacher_auto_input);
        dateAutoInput = view.findViewById(R.id.date_auto_input);
        timeAutoInput = view.findViewById(R.id.time_auto_input);
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        setupAutoCompleteTextView(consistAutoInput, consist);
        getDataFromDB();

        dateInput.setEndIconOnClickListener(v -> {
            showDatePickerDialog();
            cleanFocus();
        });

        dateAutoInput.addTextChangedListener(new TextWatcher() {

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

        return view;
    }

    private void setupAutoCompleteTextView(AutoCompleteTextView item, String[] value) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.list_for_items, value);
        item.setAdapter(adapter);
        item.setOnItemClickListener((parent, view, position, id) -> cleanFocus());
    }

    private void showDatePickerDialog() {
        setLocale();
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    calendar.set(year1, monthOfYear, dayOfMonth);
                    dateAutoInput.setText(dateFormat.format(calendar.getTime()));
                },
                year, month, day);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        Calendar maxDateCalendar = Calendar.getInstance();
        maxDateCalendar.add(Calendar.DAY_OF_MONTH, 21);
        datePickerDialog.getDatePicker().setMaxDate(maxDateCalendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void getDataFromDB() {
        List<String> teacherNames = new ArrayList<>();
        List<String> accTypeValues = Collections.singletonList("Преподаватель");
        Query query = db.collection("users")
                .whereIn("accType", accTypeValues);

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String Uid = document.getId();
                            String lastName = document.getString("lastName");
                            String name = document.getString("name");

                            if (lastName != null && name != null ) {
                                String username = lastName + " " + name;
                                teacherNames.clear();
                                teacherNames.add(username);
                                setupAutoCompleteTextView(teacherAutoInput, teacherNames.toArray(new String[0]));
                                Log.i("Firestore", "Данные получены");
                            }
                        }

                    } else {
                        Log.e("Firestore", String.valueOf(task.getException()));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Не удалось получить данные"));

    }

    private void getScheduleFromDB() {
        List<String> workDays = new ArrayList<>();
        List<String> accTypeValues = Arrays.asList("Преподаватель", "преподаватель");
        Query query = db.collection("users")
                .whereIn("accType", accTypeValues);

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String Uid = document.getId();
                            DocumentReference workDaysRef = db.collection("users").document(Uid)
                                    .collection("schedule").document("WorkDays");

                            Boolean Mon = document.getBoolean("Mon");
                            Boolean Tue = document.getBoolean("Tue");
                            Boolean Wed = document.getBoolean("Wed");
                            Boolean Thu = document.getBoolean("Thu");
                            Boolean Fri = document.getBoolean("Fri");
                            Boolean Sat = document.getBoolean("Sat");
                            Boolean Sun = document.getBoolean("Sun");

                        }

                    } else {
                        Log.e("Firestore", String.valueOf(task.getException()));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Не удалось получить данные"));

    }

    private void cleanFocus() {
        consistInput.clearFocus();
        teacherInput.clearFocus();
        dateInput.clearFocus();
        timeInput.clearFocus();
    }

    private void setLocale() {
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}