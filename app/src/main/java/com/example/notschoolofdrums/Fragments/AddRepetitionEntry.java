package com.example.notschoolofdrums.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.notschoolofdrums.Filters.CustomDateValidator2;
import com.example.notschoolofdrums.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AddRepetitionEntry extends Fragment {

    TextInputLayout dateInput, timeInput;
    AutoCompleteTextView dateAutoInput, timeAutoInput;
    Button finish;
    FrameLayout repProgress;
    SimpleDateFormat dateFormat;
    long selectedValue;
    List<String> availableTimeForAutoInput, lessonTimes;
    List<List<String>> availableTime;
    String date, time, dateTime, chosenClass, StudentUid;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    AddLessonEntry.OnFinishButtonClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_add_repetition_entry, container, false);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        dateInput = view.findViewById(R.id.date_input);
        timeInput = view.findViewById(R.id.time_input);
        dateAutoInput = view.findViewById(R.id.date_auto_input);
        timeAutoInput = view.findViewById(R.id.time_auto_input);
        finish = view.findViewById(R.id.next_button);
        repProgress = view.findViewById(R.id.rep_progress);
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        setupAutoCompleteTextViewArrayList(timeAutoInput, availableTimeForAutoInput);

        dateInput.setEndIconOnClickListener(v -> showDatePickerDialog());
        dateAutoInput.setOnClickListener(v -> showDatePickerDialog());

        finish.setOnClickListener(v -> {
            repProgress.setVisibility(View.VISIBLE);

            date = String.valueOf(dateAutoInput.getText());
            time = String.valueOf(timeAutoInput.getText());

            if (date.isEmpty()){
                setHelper(dateInput, R.string.no_date);
            }
            if (time.isEmpty()) {
                setHelper(timeInput, R.string.no_time);
            }
            if (!date.isEmpty() || !time.isEmpty()){
                dateTime = date + " " + time;

                for (int i=1; i<=10; i++) {
                    String num = String.valueOf(i);
                    db.collection("dates").document(date).collection(String.valueOf(i)).document(time).get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (queryDocumentSnapshots.exists()){
                                    Long places = queryDocumentSnapshots.getLong("places");
                                    if (places != null && places == 4){
                                        if (chosenClass == null || chosenClass.isEmpty()){
                                            chosenClass = num;
                                        }
                                    }
                                }
                                Log.d("chosenClass", chosenClass);
                            }).addOnFailureListener(e -> Log.e("finish", String.valueOf(e)));
                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    if (chosenClass != null){
                        int index = getIndex(requireContext());
                        if (index == 1){
                            FirebaseUser currentUser = fAuth.getCurrentUser();
                            if (currentUser != null) {
                                StudentUid = currentUser.getUid();
                            }
                        } else if (index == 3) {
                            List<String> studentData = getStudentData(requireContext());
                            if (!studentData.isEmpty()) {
                                StudentUid = studentData.get(1);
                                Log.d("Student Data", "Uid: " + StudentUid);
                            } else {
                                Log.d("Student Data", "No data found");
                            }
                        }
                        Map<String, Object> rep = new HashMap<>();
                        rep.put("studentID", StudentUid);
                        rep.put("class", chosenClass);
                        rep.put("type", "Репетиция");
                        rep.put("status", "Активно");

                        db.collection("entries").document(dateTime).set(rep)
                                .addOnSuccessListener(unused -> {
                                    db.collection("dates").document(date).collection(chosenClass).document(time).delete();

                                    if (listener != null){
                                        listener.onFinishButtonClick();
                                    }
                                });
                    } else {
                        Toast.makeText(requireContext(), "Что-то пошло не так!", Toast.LENGTH_LONG).show();
                        repProgress.setVisibility(View.GONE);
                    }
                }, 1000);
            }
        });

        return view;
    }

    public interface OnFinishButtonClickListener {
        void onFinishButtonClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddLessonEntry.OnFinishButtonClickListener) {
            listener = (AddLessonEntry.OnFinishButtonClickListener) context;
        }
    }

    private void setHelper (TextInputLayout item, int text){
        item.setHelperText(getString(text));
        item.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.error_red)));
    }

    private void setupAutoCompleteTextViewArrayList(AutoCompleteTextView item, List<String> value) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.list_for_items, value);
        item.setAdapter(adapter);
        item.setOnItemClickListener((parent, view, position, id) -> {
            selectedValue = parent.getItemIdAtPosition(position);
            if (item == timeAutoInput) {
                if (position == 0){
                    item.setText("");
                }
            }
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
            timeAutoInput.setText("");
            getTimeFromDB();
            setupAutoCompleteTextViewArrayList(timeAutoInput, availableTimeForAutoInput);
        });
        datePicker.addOnNegativeButtonClickListener(v -> {
            dateAutoInput.setText("");
            timeAutoInput.setText("");
            availableTimeForAutoInput = new ArrayList<>();
            availableTimeForAutoInput.add("Нет доступного времени");
            setupAutoCompleteTextViewArrayList(timeAutoInput, availableTimeForAutoInput);
        });
        datePicker.show(getChildFragmentManager(), "datePicker");
        dateInput.setHelperText("");
    }

    private MaterialDatePicker.Builder<Long> getLongBuilder() {
        long today = MaterialDatePicker.todayInUtcMilliseconds();

        Calendar startDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+03:00"));
        startDate.add(Calendar.DAY_OF_MONTH, -1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, 1);

        return MaterialDatePicker.Builder.datePicker()
                .setTitleText("Выберите дату")
                .setSelection(today)
                .setCalendarConstraints(getBuilder(startDate, endDate).build());
    }

    private CalendarConstraints.Builder getBuilder(Calendar startDate, Calendar endDate) {
        return new CalendarConstraints.Builder()
                .setStart(startDate.getTimeInMillis())
                .setEnd(endDate.getTimeInMillis())
                .setValidator(new CustomDateValidator2(startDate.getTimeInMillis(), endDate.getTimeInMillis()));
    }
    private void getTimeFromDB() {
        String chosenDate = String.valueOf(dateAutoInput.getText());
        availableTime = new ArrayList<>();
        availableTimeForAutoInput = new ArrayList<>();
        availableTimeForAutoInput.add("Не выбрано");
        for (int i=1; i<=10; i++) {
            db.collection("dates").document(chosenDate).collection(String.valueOf(i)).whereEqualTo("places", 4).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<String> docTime = new ArrayList<>();
                            lessonTimes = new ArrayList<>();
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                docTime.add(doc.getId());
                            }
                            availableTime.add(docTime);
                            for (List<String> timeList : availableTime) {
                                for (String time : timeList) {
                                    if (!availableTimeForAutoInput.contains(time)) {
                                        availableTimeForAutoInput.add(time);
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("getTimeFromDB", "DocumentSnapshot пустой"));
        }
    }

    public static int getIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AccountID", Context.MODE_PRIVATE);
        return prefs.getInt("Index", 0);
    }

    public static List<String> getStudentData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("StudentData", Context.MODE_PRIVATE);
        String username = prefs.getString("Username", null);
        String uid = prefs.getString("Uid", null);

        List<String> studentData = new ArrayList<>();
        if (username != null && uid != null) {
            studentData.add(username);
            studentData.add(uid);
        }
        return studentData;
    }
}