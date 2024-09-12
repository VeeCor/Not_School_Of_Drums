package com.example.notschoolofdrums.Fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.notschoolofdrums.Adapters.EntriesForAdapter;
import com.example.notschoolofdrums.Adapters.EntryAdapter;
import com.example.notschoolofdrums.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ActiveEntry extends Fragment {

    RecyclerView myRecycler;
    TextView noEntries, lessonTitle;
    SwipeRefreshLayout refresh;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    ArrayList<EntriesForAdapter> entryList;
    EntryAdapter entryAdapter;
    String StudentUid, time, date, consist, level, lesson, teacherID, type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_entry, container, false);

        myRecycler = view.findViewById(R.id.recycler_my_entry);
        noEntries = view.findViewById(R.id.no_entries);
        refresh = view.findViewById(R.id.refresh);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        entryList = new ArrayList<>();
        entryAdapter = new EntryAdapter(entryList);
        myRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        myRecycler.setAdapter(entryAdapter);

        loadEntries();
        
        refresh.setOnRefreshListener(() -> {
            entryList.clear();
            loadEntries();
            refresh.setRefreshing(false);
        });
        return view;
    }

    private void loadEntries() {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            StudentUid = currentUser.getUid();
        }
        db.collection("entries")
                .whereEqualTo("studentID", StudentUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            type = document.getString("type");
                            if (type != null && type.equals("Занятие")){
                                consist = document.getString("consist");
                                level = document.getString("level");
                                lesson = document.getString("lesson");
                                teacherID = document.getString("teacherID");
                            }
                            if (type != null && type.equals("Репетиция")){

                            }
                            String chosenClass = document.getString("class");
                            String dateTime = document.getId();

                            Locale locale = new Locale("ru", "RU");
                            SimpleDateFormat timeFormat = new SimpleDateFormat("EEEE HH:mm", locale);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", locale);
                            SimpleDateFormat originalFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", locale);

                            try {
                                Date dateObject = originalFormat.parse(dateTime);
                                time = timeFormat.format(dateObject);
                                date = dateFormat.format(dateObject);
                            } catch (ParseException e){
                                Log.e(TAG, "Error: " + e);
                            }

                            if (type != null && type.equals("Занятие") && teacherID != null){
                                db.collection("users").document(teacherID).get().addOnSuccessListener(userDocument -> {
                                    String lastName = userDocument.getString("lastName");
                                    String name = userDocument.getString("name");
                                    String username = lastName + " " + name;

                                    EntriesForAdapter EntriesForAdapter = new EntriesForAdapter(type, username, chosenClass, consist, level, lesson, time, date);
                                    entryList.add(EntriesForAdapter);
                                    entryAdapter.notifyDataSetChanged();
                                    noEntries.setVisibility(View.GONE);
                                });
                            }  else if (type != null && type.equals("Репетиция")){
                                EntriesForAdapter EntriesForAdapter = new EntriesForAdapter(type, "", chosenClass, "", "", "", time, date);
                                entryList.add(EntriesForAdapter);
                                entryAdapter.notifyDataSetChanged();
                                noEntries.setVisibility(View.GONE);
                            }

                        }
                    }
                });
    }
}