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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.notschoolofdrums.Filters.CustomDateValidator;
import com.example.notschoolofdrums.Filters.CustomDateValidator2;
import com.example.notschoolofdrums.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class AddLessonEntry extends Fragment {
    String[] consist = {"Группа", "Индивидуально"};
    List<String> teacherNames, teacherUid, lessonTimes, availableTimeForAutoInput;
    List<List<String>> availableTime;
    static List<String> schedule;
    TextInputLayout consistInput, teacherInput, dateInput, timeInput;
    AutoCompleteTextView consistAutoInput, teacherAutoInput, dateAutoInput, timeAutoInput;
    Button finish;
    FrameLayout entryProgress;
    SimpleDateFormat dateFormat;
    private boolean isFormatting;
    private int prevLength;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    String Uid, StudentUid, teachUid, level, lesson, chosenClass, cons, teach, date, time, dateTime;
    long selectedValue;
    OnFinishButtonClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_add_lesson_entry, container, false);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        finish = view.findViewById(R.id.next_button);
        consistInput = view.findViewById(R.id.consist_input);
        teacherInput = view.findViewById(R.id.teacher_input);
        dateInput = view.findViewById(R.id.date_input);
        timeInput = view.findViewById(R.id.time_input);
        consistAutoInput = view.findViewById(R.id.consist_auto_input);
        teacherAutoInput = view.findViewById(R.id.teacher_auto_input);
        dateAutoInput = view.findViewById(R.id.date_auto_input);
        timeAutoInput = view.findViewById(R.id.time_auto_input);
        entryProgress = view.findViewById(R.id.entry_progress);
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        setupAutoCompleteTextView(consistAutoInput, consist);

        availableTimeForAutoInput = new ArrayList<>();
        availableTimeForAutoInput.add("Нет доступного времени");
        setupAutoCompleteTextViewArrayList(timeAutoInput, availableTimeForAutoInput);

        getTeachersFromDB();

        consistAutoInput.setOnClickListener(v -> consistInput.setHelperText(""));
        teacherAutoInput.setOnClickListener(v -> teacherInput.setHelperText(""));
        timeAutoInput.setOnClickListener(v -> timeInput.setHelperText(""));

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

        finish.setOnClickListener(v -> {
            entryProgress.setVisibility(View.VISIBLE);

            cons = String.valueOf(consistAutoInput.getText());
            teach = String.valueOf(teacherAutoInput.getText());
            date = String.valueOf(dateAutoInput.getText());
            time = String.valueOf(timeAutoInput.getText());

            if (date.isEmpty()){
                setHelper(dateInput, R.string.no_date);
            }
            if (time.isEmpty()) {
                setHelper(timeInput, R.string.no_time);
            }
            if (teach.isEmpty()) {
                setHelper(teacherInput, R.string.no_teacher);
            }
            if (cons.isEmpty()) {
                setHelper(consistInput, R.string.no_consist);
            }
            if (!date.isEmpty() || !time.isEmpty() || !teach.isEmpty() || !cons.isEmpty()){
                dateTime = date + " " + time;

                for (int g=1; g<teacherNames.size(); g++){
                    if (teacherNames.get(g).equals(teach)){
                        teachUid = teacherUid.get(g);
                        break;
                    }
                }

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
                        Map<String, Object> entry = new HashMap<>();
                        entry.put("studentID", StudentUid);
                        entry.put("teacherID", teachUid);
                        entry.put("class", chosenClass);
                        entry.put("type", "Занятие");
                        entry.put("consist", cons);
                        entry.put("level", level);
                        entry.put("lesson", lesson);
                        entry.put("status", "Активно");

                        db.collection("entries").document(dateTime).set(entry)
                                .addOnSuccessListener(unused -> {
                                    Map<String, Object> updateClass = new HashMap<>();
                                    updateClass.put("places", 3);
                                    updateClass.put("type", "Занятие");
                                    updateClass.put("level", level);
                                    updateClass.put("lesson", lesson);
                                    updateClass.put("teacherID", teachUid);
                                    updateClass.put("consist", cons);

                                    db.collection("dates").document(date).collection(chosenClass).document(time).set(updateClass);

                                    char[] charTime = time.toCharArray();
                                    charTime[1] = (char) (charTime[1] + 1);
                                    time = new String(charTime);

                                    db.collection("dates").document(date).collection(chosenClass).document(time).delete();

                                    if (listener != null){
                                        listener.onFinishButtonClick();
                                    }
                                }).addOnFailureListener(e -> {

                                });
                    } else {
                        Toast.makeText(requireContext(), "Что-то пошло не так!", Toast.LENGTH_LONG).show();
                        entryProgress.setVisibility(View.GONE);
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
        if (context instanceof OnFinishButtonClickListener) {
            listener = (OnFinishButtonClickListener) context;
        }
    }

    private void setHelper (TextInputLayout item, int text){
        item.setHelperText(getString(text));
        item.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.error_red)));
    }

    private void setupAutoCompleteTextView(AutoCompleteTextView item, String[] value) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.list_for_items, value);
        item.setAdapter(adapter);
    }

    private void setupAutoCompleteTextViewArrayList(AutoCompleteTextView item, List<String> value) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.list_for_items, value);
        item.setAdapter(adapter);
        item.setOnItemClickListener((parent, view, position, id) -> {
            selectedValue = parent.getItemIdAtPosition(position);
            if (item == teacherAutoInput){
                if (position == 0){
                    item.setText("");
                    getTeachersFromDB();
                } else {
                    getScheduleDataFromDB();
                }
            } else if (item == timeAutoInput) {
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
            getTeachersFromDBIfDateIsChosen();
            getTimeFromDB();
            setupAutoCompleteTextViewArrayList(timeAutoInput, availableTimeForAutoInput);
        });
        datePicker.addOnNegativeButtonClickListener(v -> {
            dateAutoInput.setText("");
            getTeachersFromDB();
            availableTimeForAutoInput = new ArrayList<>();
            availableTimeForAutoInput.add("Нет доступного времени");
            setupAutoCompleteTextViewArrayList(timeAutoInput, availableTimeForAutoInput);
        });
        datePicker.show(getChildFragmentManager(), "datePicker");
        dateInput.setHelperText("");
    }

    private void getTimeFromDB() {
        getLevelAndLesson();
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
                            for (int j=0; j<docTime.size(); j++){
                                if(docTime.get(j).equals("11:00") && docTime.get(j+1).equals("12:00")){
                                    lessonTimes.add("11:00");
                                }
                                if(docTime.get(j).equals("13:00") && docTime.get(j+1).equals("14:00")){
                                    lessonTimes.add("13:00");
                                }
                                if(docTime.get(j).equals("15:00") && docTime.get(j+1).equals("16:00")){
                                    lessonTimes.add("15:00");
                                }
                                if(docTime.get(j).equals("17:00") && docTime.get(j+1).equals("18:00")){
                                    lessonTimes.add("17:00");
                                }
                                if(docTime.get(j).equals("19:00") && docTime.get(j+1).equals("20:00")){
                                    lessonTimes.add("19:00");
                                }
                                if(docTime.get(j).equals("21:00") && docTime.get(j+1).equals("22:00")){
                                    lessonTimes.add("21:00");
                                }
                            }
                            availableTime.add(lessonTimes);
                            for (List<String> timeList : availableTime) {
                                if (timeList.contains("11:00") && !availableTimeForAutoInput.contains("11:00")) {
                                    availableTimeForAutoInput.add("11:00");
                                }
                                if (timeList.contains("13:00") && !availableTimeForAutoInput.contains("13:00")) {
                                    availableTimeForAutoInput.add("13:00");
                                }
                                if (timeList.contains("15:00") && !availableTimeForAutoInput.contains("15:00")) {
                                    availableTimeForAutoInput.add("15:00");
                                }
                                if (timeList.contains("17:00") && !availableTimeForAutoInput.contains("17:00")) {
                                    availableTimeForAutoInput.add("17:00");
                                }
                                if (timeList.contains("19:00") && !availableTimeForAutoInput.contains("19:00")) {
                                    availableTimeForAutoInput.add("19:00");
                                }
                                if (timeList.contains("21:00") && !availableTimeForAutoInput.contains("21:00")) {
                                    availableTimeForAutoInput.add("21:00");
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("getTimeFromDB", "DocumentSnapshot пустой"));
        }
    }

    private void getLevelAndLesson() {
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
        db.collection("users").document(StudentUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        level = documentSnapshot.getString("level");
                        lesson = documentSnapshot.getString("lesson");
                        Log.d("Firestore", "Данные из документа получены");
                    } else {
                        Log.e("Firestore", "Данные из документа не получены");
                    }
                }).addOnFailureListener(e -> Log.e("Firestore", "Документ не получен. Ошибка " + e));
    }

    @NonNull
    private MaterialDatePicker.Builder<Long> getLongBuilder() {
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
    private CalendarConstraints.Builder getBuilder(Calendar startDate) {
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

    private List<Integer> getBlockedDays() {
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

    private void getTeachersFromDB() {
        teacherNames = new ArrayList<>();
        teacherUid = new ArrayList<>();
        teacherNames.add("Не выбрано");
        teacherUid.add("Не выбрано");
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
                                Log.i("Firestore", "Данные получены. teacherNames: " + teacherNames + "teacherUid: " + teacherUid);
                            }
                        }
                        setupAutoCompleteTextViewArrayList(teacherAutoInput, teacherNames);
                    } else {
                        Log.e("Firestore", String.valueOf(task.getException()));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Не удалось получить данные"));
    }

    private void getTeachersFromDBIfDateIsChosen() {
        String chosenDate = dateAutoInput.getText().toString();
        String dayOfWeek;
        try {
            Date date = dateFormat.parse(chosenDate);
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+03:00"));
            calendar.setTime(date);
            int dayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK);

            switch (dayOfWeekInt) {
                case Calendar.MONDAY:
                    dayOfWeek = "Пн";
                    break;
                case Calendar.TUESDAY:
                    dayOfWeek = "Вт";
                    break;
                case Calendar.WEDNESDAY:
                    dayOfWeek = "Ср";
                    break;
                case Calendar.THURSDAY:
                    dayOfWeek = "Чт";
                    break;
                case Calendar.FRIDAY:
                    dayOfWeek = "Пт";
                    break;
                case Calendar.SATURDAY:
                    dayOfWeek = "Сб";
                    break;
                case Calendar.SUNDAY:
                    dayOfWeek = "Вс";
                    break;
                default:
                    dayOfWeek = null;
            }

            Log.i("DayOfWeek", "Выбранный день недели: " + dayOfWeek);

        } catch (ParseException e) {
            Log.e("DateError", "Неверный формат даты", e);
            return;
        }

        if (dayOfWeek != null){
            teacherNames = new ArrayList<>();
            teacherUid = new ArrayList<>();
            teacherNames.add("Не выбрано");
            teacherUid.add("Не выбрано");
            List<String> accTypeValues = Collections.singletonList("Преподаватель");
            Query query = db.collection("users")
                    .whereIn("accType", accTypeValues)
                    .whereArrayContains("schedule", dayOfWeek);

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
                                    Log.i("Firestore2", "Данные получены. teacherNames: " + teacherNames + "teacherUid: " + teacherUid);
                                }
                            }
                            setupAutoCompleteTextViewArrayList(teacherAutoInput, teacherNames);
                        } else {
                            Log.e("Firestore", String.valueOf(task.getException()));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Не удалось получить данные"));
        }
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
                            Log.e("Firestore", "Тип элемента в schedule не String");
                        }
                    }
                    AddLessonEntry.schedule = schedule;
                } else {
                    Log.e("Firestore", "Schedule не является List");
                }
            } else {
                Log.d("Firestore", "Документ не найден");
            }
        }).addOnFailureListener(e -> Log.d("Firestore", "Завершено с ошибкой: ", e));
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
