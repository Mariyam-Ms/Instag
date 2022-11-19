package com.project1.instagramlite.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.project1.instagramlite.MainActivity;
import com.project1.instagramlite.R;
import com.project1.instagramlite.SubActivity;
import com.project1.instagramlite.databinding.FragmentSignupBinding;


public class SignupFragment extends Fragment {
    private FragmentSignupBinding binding;
    private FirebaseAuth auth;
    private NavController navController;
    private static final int RC_Sign_In = 2;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentSignupBinding.inflate(inflater , container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth=FirebaseAuth.getInstance();
        navController= Navigation.findNavController(view);
        createRequest();
        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailadd = binding.email.getText().toString().trim();
                String password = binding.passwordUp.getText().toString();
                String Conformpass = binding.confirmPassword.getText().toString();

                if (!emailadd.isEmpty() && !password.isEmpty() && !Conformpass.isEmpty()) {
                    if (password.equals(Conformpass)) {
                        auth.createUserWithEmailAndPassword(emailadd, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                    if(firebaseUser.isEmailVerified()){

                                        Toast.makeText(getContext(), "Successfully Register ", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    }else{
                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(getContext(), "Check your Email to get Verified", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(getContext(), "Please type correct password", Toast.LENGTH_SHORT).show();


                    }

                } else {
                    Toast.makeText(getContext(), "Please give valid email and password", Toast.LENGTH_SHORT).show();
                }
            }

        });
        binding.backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signupFragment_to_signinFragment);
            }
        });
        binding.phoneUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signupFragment_to_phoneauthFragment);
            }
        });
        binding.googleUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }
    private void createRequest() {


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(),gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_Sign_In);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_Sign_In) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else{
            Log.d("TAG", "onActivityResult: "+resultCode+" "+ RC_Sign_In+" "+requestCode);
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getActivity(),MainActivity.class));

                }else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}



