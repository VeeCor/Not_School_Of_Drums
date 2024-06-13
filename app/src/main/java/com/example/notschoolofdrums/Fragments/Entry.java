package com.example.notschoolofdrums.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notschoolofdrums.Adapters.PagerAdapter;
import com.example.notschoolofdrums.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class Entry extends Fragment {

    PagerAdapter pagerAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_entry, container, false);

        viewPager = view.findViewById(R.id.pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        pagerAdapter = new PagerAdapter(getChildFragmentManager(), getLifecycle());
        viewPager.setAdapter(pagerAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText(R.string.my_entries);
                    break;
                default:
                    tab.setText(R.string.history);
                    break;
            }
        });
        tabLayoutMediator.attach();

        return view;
    }
}