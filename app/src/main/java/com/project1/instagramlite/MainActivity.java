package com.project1.instagramlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.project1.instagramlite.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
        private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntent();


        NavController navController= Navigation.findNavController(this,R.id.main_container);
        NavigationUI.setupWithNavController(binding.bottomNavigation,navController);

    }
}