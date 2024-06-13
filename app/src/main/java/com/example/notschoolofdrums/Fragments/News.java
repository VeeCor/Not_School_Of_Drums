package com.example.notschoolofdrums.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.example.notschoolofdrums.Adapters.NewsAdapter;
import com.example.notschoolofdrums.Adapters.NewsForAdapter;
import com.example.notschoolofdrums.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class News extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView newsRecycler;
    ArrayList<NewsForAdapter> newsArrayList = new ArrayList<>();
    NewsAdapter newsAdapter;
    FirebaseFirestore db;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        db = FirebaseFirestore.getInstance();

        swipeRefreshLayout = view.findViewById(R.id.fragment_news);
        newsRecycler = view.findViewById(R.id.recycler_news);

        newsRecycler.setHasFixedSize(true);
        newsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        newsAdapter = new NewsAdapter(requireContext(), newsArrayList);
        newsRecycler.setAdapter(newsAdapter);

        sharedPreferences = requireActivity().getSharedPreferences("news_cache", Context.MODE_PRIVATE);

        loadCachedData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            newsArrayList.clear();
            fetchDataFromFirestore();
            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    private void loadCachedData() {
        sharedPreferences = requireActivity().getSharedPreferences("news_cache", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        if (sharedPreferences != null){
            String cachedNewsJson = sharedPreferences.getString("cached_news", null);
            if (cachedNewsJson != null) {
                try {
                    NewsForAdapter[] cachedNewsArray = gson.fromJson(cachedNewsJson, NewsForAdapter[].class);
                    newsArrayList.addAll(Arrays.asList(cachedNewsArray));
                    newsAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    fetchDataFromFirestore();
                    Log.w("CacheError", "Error deserializing cached data", e);
                }
            } else {
                fetchDataFromFirestore();
            }
        } else {
            fetchDataFromFirestore();
        }
    }

    private void fetchDataFromFirestore() {
        db.collection("news")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<NewsForAdapter> fetchedNews = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String text = document.getString("text");
                            String dateTime = document.getString("datetime");
                            String imageUriString = document.getString("imageURI");

                            if (dateTime != null && (text != null || imageUriString != null)) {
                                Uri imageUri = Uri.parse(imageUriString);

                                NewsForAdapter newsItem = new NewsForAdapter(text, dateTime, imageUri);
                                fetchedNews.add(newsItem);
                            } else {
                                Log.w("FirestoreData", "Document contains null fields");
                            }
                        }
                        fetchedNews.sort(Comparator.comparing(NewsForAdapter::getDateTime).reversed());

                        newsArrayList.clear();
                        newsArrayList.addAll(fetchedNews);
                        newsAdapter.notifyDataSetChanged();
                        cacheFetchedData();
                    } else {
                        Log.w("FirestoreError", "Error getting documents.", task.getException());
                    }
                });
    }

    private void cacheFetchedData() {
        Gson gson = new Gson();
        String newsJson = gson.toJson(newsArrayList);
        sharedPreferences.edit().putString("cached_news", newsJson).apply();
    }
}