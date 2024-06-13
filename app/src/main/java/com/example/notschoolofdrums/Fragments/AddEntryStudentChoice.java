package com.example.notschoolofdrums.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notschoolofdrums.Activity.AddEntryActivity;
import com.example.notschoolofdrums.Adapters.StudentsAdapter;
import com.example.notschoolofdrums.R;
import com.example.notschoolofdrums.Adapters.Students;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEntryStudentChoice extends Fragment implements StudentsAdapter.OnItemClickListener {
    RecyclerView recyclerView;
    SearchView searchStudent;
    TextView NothingIsFound;
    List<List<String>> StudentsNameAndUid;
    ArrayList<Students> studentsArrayList;
    String username, uid;
    StudentsAdapter studentsAdapter;
    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_entry_student_choice, container, false);

        NothingIsFound = view.findViewById(R.id.nothing_is_found);
        searchStudent = view.findViewById(R.id.search_view_entry);
        recyclerView = view.findViewById(R.id.students_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        studentsArrayList = new ArrayList<>();
        studentsAdapter = new StudentsAdapter(requireContext(), studentsArrayList, this);
        recyclerView.setAdapter(studentsAdapter);

        getDateFromDB();

        searchStudent.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null){
                    filterList(newText);
                } else {
                    getDateFromDB();
                }

                return true;
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Students selectedStudent = studentsArrayList.get(position);
        username = selectedStudent.getUsername();

        for (List<String> data : StudentsNameAndUid) {
            if (!data.isEmpty() && data.get(0).equals(username)) {
                uid = data.get(1);
                break;
            }
        }

        if (!TextUtils.isEmpty(uid)) {
            saveStudentData(requireContext(), username, uid);
            Intent intent = new Intent(requireContext(), AddEntryActivity.class);
            startActivity(intent);
            Log.d("Student Clicked", "Username: " + username + ", Uid: " + uid);
        } else Log.d("Student Clicked", "No matching data found for Username: " + username);
        Log.d("Student Clicked", "Username: " + username);
    }

    private void getDateFromDB() {
        List<String> accTypeValues = Arrays.asList("Ученик", "ученик");
        StudentsNameAndUid = new ArrayList<>();
        Query query = db.collection("users")
                .whereIn("accType", accTypeValues);

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String Uid = document.getId();
                            String lastName = document.getString("lastName");
                            String name = document.getString("name");
                            String surname = document.getString("surname");

                            if (lastName != null && name != null && surname != null) {
                                String username = lastName + " " + name + " " + surname;
                                List<String> studentData = new ArrayList<>();
                                studentData.add(username);
                                studentData.add(Uid);
                                StudentsNameAndUid.add(studentData);
                                Students student = new Students(username);
                                studentsAdapter.addStudent(student);
                                NothingIsFound.setVisibility(View.GONE);
                                Log.i("Firestore", "Данные получены");
                            }
                        }
                    } else {
                        Log.e("Firestore", String.valueOf(task.getException()));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Не удалось получить данные"));

    }
// TODO: Добавить поиск. Сейчас он не обновляет список при поиске.
    private void filterList(String text) {
        ArrayList<Students> filteredList = new ArrayList<>();
        for(Students student: studentsArrayList){
            if (student.getUsername().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(student);
            }
        }
        if (filteredList.isEmpty()){
            NothingIsFound.setVisibility(View.VISIBLE);
        } else {
            studentsAdapter.setFilteredList(filteredList);
        }
    }

    public static void saveStudentData(Context context, String username, String uid) {
        SharedPreferences.Editor editor = context.getSharedPreferences("StudentData", Context.MODE_PRIVATE).edit();
        editor.putString("Username", username);
        editor.putString("Uid", uid);
        editor.apply();
        Log.d("SharedPreferences", "Username: " + username + ", Uid: " + uid);
    }
}