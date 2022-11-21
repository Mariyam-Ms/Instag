package com.project1.instagramlite.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.project1.instagramlite.MainActivity;
import com.project1.instagramlite.SubActivity;
import com.project1.instagramlite.databinding.FragmentOtpBinding;


public class OtpFragment extends Fragment {

private FragmentOtpBinding binding;
    private String OTP;
    private FirebaseAuth firebaseAuth;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      binding=FragmentOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        navController= Navigation.findNavController(view);


     OTP = getArguments() .getString("auth");

        binding.verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verification_editText = binding.editText.getText().toString();
                if (!verification_editText.isEmpty()) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP, verification_editText);
                    signIn(credential);
                } else {
                    Toast.makeText(getContext(), "Please Enter otp", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void signIn(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendToHome();
                } else {
                    Toast.makeText(getContext(), "Verfication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
       if(currentUser != null){
            sendToHome();
        }
    }

    private void sendToHome(){
        Intent intent=new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
    }
    }
