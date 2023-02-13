package com.example.startendo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.startendo.Fragments.ChatsFragment;
import com.example.startendo.Fragments.MapFragment;
import com.example.startendo.Fragments.NormalFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new ChatsFragment();
            case 1: return new MapFragment();
            case 2: return new NormalFragment();
            default: return new ChatsFragment();
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title =null;
        if (position==0){
            title ="CHATS";
        }
        if (position==1) {
            title ="MAP";
        }
        if (position==2) {
            title ="NORMAL";
        }

        return title;
    }


}
