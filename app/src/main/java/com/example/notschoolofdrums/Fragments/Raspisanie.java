package com.example.notschoolofdrums.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notschoolofdrums.Adapters.RaspisanieAdapter;
import com.example.notschoolofdrums.Adapters.RaspisanieForAdapter;
import com.example.notschoolofdrums.R;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Raspisanie extends Fragment {

    RecyclerView recyclerView;
    RaspisanieAdapter raspisanieAdapter;
    List<RaspisanieForAdapter> raspisanieList;
    String date, class1, class2, class3, class4, class5, n1, n2, n3, n4, n5, n6, n7, n8, n9,
            time11, time13, time15, time17, time19, time21,
            class11, class13, class15, class17, class19, class21,
            students11, students13, students15, students17, students19, students21;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_raspisanie, container, false);

        recyclerView = view.findViewById(R.id.recycler_rasp);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        class1 = "1";
        class2 = "2";
        class3 = "3";
        class4 = "4";
        class5 = "5";
        n1 = "Мурашова Александра\nНикольский Дарий\nСоснина Вера";
        n2 = "Подгорный Игорь";
        n3 = "Бобров Алексей\nГорьков Артем";
        n4 = "Маковская Софья\nНемудрякина Мария\nАнищук Анастасия\nЛоницкая Динара";
        n5 = "Макина Вера\nВенедиктова Анастасия";
        n6 = "Дудько Андрей";
        n7 = "Краскина Ангелина\nМитюшин Ильяс\nХандогина Мария";
        n8 = "Тарасова Марина\nТарасов Даниил";
        n9 = "Старикова Наталья";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        raspisanieList = new ArrayList<>();

        for (int i = 0; i <= 11; i++) {
            date = dateFormat.format(calendar.getTime());

            clearFields();

            if (!n1.isEmpty() && !n2.isEmpty() && !n3.isEmpty()) {
                class13 = class2;
                students13 = n1;
                n1 = "";
                class17 = class3;
                students17 = n2;
                n2 = "";
                class19 = class3;
                students19 = n3;
                n3 = "";
                addToRaspisanieList();
            } else if (!n4.isEmpty() && !n5.isEmpty()) {
                class11 = class5;
                students11 = n4;
                n4 = "";
                class15 = class5;
                students15 = n5;
                n5 = "";
                addToRaspisanieList();
            } else if (!n6.isEmpty()) {
                class21 = class4;
                students21 = n6;
                n6 = "";
                addToRaspisanieList();
            } else if (!n7.isEmpty() && !n8.isEmpty()) {
                class11 = class1;
                students11 = n7;
                n7 = "";
                class17 = class1;
                students17 = n8;
                n8 = "";
                addToRaspisanieList();
            } else if (!n9.isEmpty()) {
                class13 = class3;
                students13 = n9;
                n9 = "";
                addToRaspisanieList();
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        raspisanieAdapter = new RaspisanieAdapter(raspisanieList);
        recyclerView.setAdapter(raspisanieAdapter);

        return view;
    }

    private void addToRaspisanieList() {
        RaspisanieForAdapter raspisanieForAdapter = new RaspisanieForAdapter(date,
                class11, class13, class15, class17, class19, class21,
                students11, students13, students15, students17, students19, students21);
        raspisanieList.add(raspisanieForAdapter);
    }

    private void clearFields() {
        class11 = "–";
        class13 = "–";
        class15 = "–";
        class17 = "–";
        class19 = "–";
        class21 = "–";
        students11 = "";
        students13 = "";
        students15 = "";
        students17 = "";
        students19 = "";
        students21 = "";
    }
}