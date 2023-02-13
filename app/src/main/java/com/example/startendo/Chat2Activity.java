package com.example.startendo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.startendo.databinding.ActivityChat2Binding;

public class Chat2Activity extends AppCompatActivity {
    ActivityChat2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityChat2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.ViewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.TabLayout.setupWithViewPager(binding.ViewPager);

    }
}