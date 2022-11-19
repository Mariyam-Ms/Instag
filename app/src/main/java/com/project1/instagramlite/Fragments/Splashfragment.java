package com.project1.instagramlite.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project1.instagramlite.R;
import com.project1.instagramlite.databinding.FragmentSplashfragmentBinding;


public class Splashfragment extends Fragment {

  private FragmentSplashfragmentBinding binding;
  private NavController navController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentSplashfragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /*here just firebase auth na how abiut phone and google signin
                what logic do i need to use if else
                 */

                navController.navigate(R.id.action_splashfragment_to_signinFragment);
            }
        },3000);
    }
}