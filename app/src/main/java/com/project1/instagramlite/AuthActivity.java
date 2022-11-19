package com.project1.instagramlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.project1.instagramlite.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {
   ActivityAuthBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntent();
    }
}