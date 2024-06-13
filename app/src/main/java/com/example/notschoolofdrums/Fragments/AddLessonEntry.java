package com.example.notschoolofdrums.Fragments;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.notschoolofdrums.Filters.CustomDateValidator;
import com.example.notschoolofdrums.Filters.CustomDateValidator2;
import com.example.notschoolofdrums.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class AddLessonEntry extends Fragment {
    String[] consist = {"Группа", "Индивидуально"};
    List<String> teacherNames, teacherUid;
    static List<String> schedule;
    TextInputLayout consistInput, teacherInput, dateInput, timeInput;
    AutoCompleteTextView consistAutoInput;
    static AutoCompleteTextView teacherAutoInput;
    AutoCompleteTextView dateAutoInput;
    AutoCompleteTextView timeAutoInput;
    Button finish;
    SimpleDateFormat dateFormat;
    private boolean isFormatting;
    private int prevLength;
    FirebaseFirestore db;
    String Uid;
    long selectedValue;

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

        dateInput.setEndIconOnClickListener(v -> showDatePickerDialog());
        dateAutoInput.setOnClickListener(v -> showDatePickerDialog());

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

    private void setupAutoCompleteTextViewArrayList(AutoCompleteTextView item, List<String> value) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.list_for_items, value);
        item.setAdapter(adapter);
        item.setOnItemClickListener((parent, view, position, id) -> {
            selectedValue = parent.getItemIdAtPosition(position);
            getScheduleDataFromDB();
            cleanFocus();
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+03:00"));
        calendar.clear();

        MaterialDatePicker.Builder<Long> builder = getLongBuilder();
        MaterialDatePicker<Long> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            dateAutoInput.setText(dateFormat.format(calendar.getTime()));
        });

        datePicker.show(getChildFragmentManager(), "datePicker");
    }

    @NonNull
    private static MaterialDatePicker.Builder<Long> getLongBuilder() {
        long today = MaterialDatePicker.todayInUtcMilliseconds();

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Выберите дату");
        builder.setSelection(today);

        Calendar startDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+03:00"));
        startDate.add(Calendar.DAY_OF_MONTH, -1);
        CalendarConstraints.Builder constraintsBuilder = getBuilder(startDate);

        builder.setCalendarConstraints(constraintsBuilder.build());
        return builder;
    }

    @NonNull
    private static CalendarConstraints.Builder getBuilder(Calendar startDate) {
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, 21);

        List<Integer> blockedDays = getBlockedDays();


        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setStart(startDate.getTimeInMillis());
        constraintsBuilder.setEnd(endDate.getTimeInMillis());
        if (!blockedDays.isEmpty()) {
            constraintsBuilder.setValidator(new CustomDateValidator(startDate.getTimeInMillis(), endDate.getTimeInMillis(), blockedDays));
        } else {
            constraintsBuilder.setValidator(new CustomDateValidator2(startDate.getTimeInMillis(), endDate.getTimeInMillis()));
        }
        return constraintsBuilder;
    }

    private static List<Integer> getBlockedDays() {
        List<Integer> blockedDays = new ArrayList<>();
        if (teacherAutoInput.getText().toString().isEmpty()) {
            return Collections.emptyList();
        }
        for (String day : schedule) {
            switch (day) {
                case "Пн":
                    blockedDays.add(Calendar.MONDAY);
                    break;
                case "Вт":
                    blockedDays.add(Calendar.TUESDAY);
                    break;
                case "Ср":
                    blockedDays.add(Calendar.WEDNESDAY);
                    break;
                case "Чт":
                    blockedDays.add(Calendar.THURSDAY);
                    break;
                case "Пт":
                    blockedDays.add(Calendar.FRIDAY);
                    break;
                case "Сб":
                    blockedDays.add(Calendar.SATURDAY);
                    break;
                case "Вс":
                    blockedDays.add(Calendar.SUNDAY);
                    break;
            }
        }
        return blockedDays;
    }

    private void getDataFromDB() {
        teacherNames = new ArrayList<>();
        teacherUid = new ArrayList<>();
        List<String> accTypeValues = Collections.singletonList("Преподаватель");
        Query query = db.collection("users")
                .whereIn("accType", accTypeValues);

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Uid = document.getId();
                            String lastName = document.getString("lastName");
                            String name = document.getString("name");

                            if (lastName != null && name != null ) {
                                String username = lastName + " " + name;
                                teacherNames.add(username);
                                teacherUid.add(Uid);
                                Log.i("Firestore", "Данные получены");
                            }
                        }
                        setupAutoCompleteTextViewArrayList(teacherAutoInput, teacherNames);
                    } else {
                        Log.e("Firestore", String.valueOf(task.getException()));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Не удалось получить данные"));

    }

    private void getScheduleDataFromDB() {
        Uid = teacherUid.get((int) selectedValue);
        DocumentReference docRef = db.collection("users").document(Uid);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Object scheduleObj = documentSnapshot.get("schedule");

                if (scheduleObj instanceof List<?>) {
                    List<?> scheduleList = (List<?>) scheduleObj;
                    List<String> schedule = new ArrayList<>();

                    for (Object item : scheduleList) {
                        if (item instanceof String) {
                            schedule.add((String) item);
                        } else {
                            Log.e("Firestore", "Element in schedule is not a String");
                        }
                    }
                    this.schedule = schedule;
                } else {
                    Log.e("Firestore", "Schedule is not a List");
                }
            } else {
                Log.d("Firestore", "No such document");
            }
        }).addOnFailureListener(e -> Log.d("Firestore", "get failed with ", e));
    }

    private void cleanFocus() {
        consistInput.clearFocus();
        teacherInput.clearFocus();
        dateInput.clearFocus();
        timeInput.clearFocus();
    }
}
