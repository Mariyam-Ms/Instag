package com.project1.instagramlite.Fragments;

import android.content.Context;
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

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.PhoneAuthCredential;

import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.project1.instagramlite.AuthActivity;
import com.project1.instagramlite.R;
import com.project1.instagramlite.SubActivity;
import com.project1.instagramlite.databinding.FragmentPhoneauthBinding;


import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


public class PhoneauthFragment extends Fragment {

private FragmentPhoneauthBinding binding;
private NavController navController;

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentPhoneauthBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        navController= Navigation.findNavController(view);
binding.progressBar.setVisibility(View.INVISIBLE);
        binding.sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = binding.phonenumber.getText().toString().trim();
                String country_code = binding.countrycode.getText().toString();
                        String phoneNumber =  "+"  + country_code + ""+ phone;
                        if ( !country_code.isEmpty() ||!phone.isEmpty()) {
                                binding.progressBar.setVisibility(View.VISIBLE);
//                            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
//                                    .setPhoneNumber(phoneNumber).
//                                    setTimeout(60L, TimeUnit.SECONDS)
//                                    .getActivity();
//                                    .setCallbacks(mCallBacks);
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    phoneNumber,        // Phone number to verify
                                    60,                   // Timeout duration
                                    TimeUnit.SECONDS,     // Unit of timeout
                                    getActivity(),
                                    mCallBacks);         // OnVerificationStateChangedCallbacks


                        } else {

                            Toast.makeText(getContext(), " Please Enter Valid Number", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                       binding. progressBar.setVisibility(View.INVISIBLE);

                        PhoneauthFragment phoneauthFragment=new PhoneauthFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("auth",s);
                        phoneauthFragment.setArguments(bundle);

                        navController.navigate(R.id.action_phoneauthFragment_to_otpFragment);


                        Toast.makeText(getContext(), "Otp has been sent", Toast.LENGTH_SHORT).show();


                    }
                };
            }



//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if(user != null ){
//            sentToHome();
//        }
//
//    }

    private void sentToHome() {
        Intent intent=new Intent(getActivity(), SubActivity.class);
        startActivity(intent);
            }




            private void signIn(PhoneAuthCredential credential){
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){


                            sentToHome();
                        }else{
                            Toast.makeText(getContext(), task .getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
            }
