package com.example.notschoolofdrums.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.notschoolofdrums.Fragments.Activity_entry;
import com.example.notschoolofdrums.Fragments.History_entry;

public class Pager_adapter extends FragmentStateAdapter {

    public Pager_adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new Activity_entry();
            default:
                return new History_entry();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
