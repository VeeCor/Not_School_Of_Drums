package com.example.notschoolofdrums.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.notschoolofdrums.Fragments.ActiveEntry;
import com.example.notschoolofdrums.Fragments.History_entry;

public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ActiveEntry();
            default:
                return new History_entry();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
