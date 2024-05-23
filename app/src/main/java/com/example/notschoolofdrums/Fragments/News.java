package com.example.notschoolofdrums.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notschoolofdrums.Adapters.NewsAdapter;
import com.example.notschoolofdrums.Adapters.NewsForAdapter;
import com.example.notschoolofdrums.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class News extends Fragment {

    RecyclerView newsRecycler;
    ArrayList<NewsForAdapter> newsArrayList = new ArrayList<>();
    NewsAdapter newsAdapter;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        db = FirebaseFirestore.getInstance();

        newsRecycler = view.findViewById(R.id.recycler_news);
        newsRecycler.setHasFixedSize(true);
        newsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }
}