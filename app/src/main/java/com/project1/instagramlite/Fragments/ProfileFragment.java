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
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project1.instagramlite.R;

import com.project1.instagramlite.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {

private FragmentProfileBinding binding;
private NavController navController;
private FirebaseAuth auth;
private FirebaseFirestore firebaseFirestore;
private StorageReference storageReference;
    private String Uid;
    private BottomSheetDialog bottomSheetDialog;
private TextView editProfile,signOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentProfileBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth=FirebaseAuth.getInstance();
        navController= Navigation.findNavController(view);
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        Uid=auth.getCurrentUser().getUid();

        binding.btmSheetProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDailog();
            }
        });


        firebaseFirestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String imageUrl = task.getResult().getString("image");
                        binding.profilename.setText(name);
                        Picasso.get().load(imageUrl).into(binding.profilepic);
                    }
                }
            }
        });
    }

    private void bottomDailog() {

        bottomSheetDialog  =new BottomSheetDialog(getContext());
        View view=getLayoutInflater().from(getContext()).inflate(R.layout.bottomsheet,null);
       // BottomSheetDialog binding = bottomSheetDialog.getLayoutInflater((LayoutInflater.from(getContext()));
      //  bottomSheetDialog.setContentView(binding,getRoot());
       editProfile=view.findViewById(R.id.editprofile);
        signOut=view.findViewById(R.id.signout);
        bottomSheetDialog.show();

       // View view=getLayoutInflater().from(getContext()).inflate(R.layout.bottomsheet,null);
       // editProfile= view.findViewById(R.id.edit_profile);
       // sign_out=view.findViewById(R.id.signOut);
       bottomSheetDialog.setContentView(view);
//        bottomSheetDialog.show();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileFragment_to_profileSetupFragment);
                bottomSheetDialog.dismiss();

            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth.signOut();
                bottomSheetDialog.dismiss();
                Intent intent=new Intent( getActivity(), SigninFragment.class);
                startActivity(intent);


            }
        });


    }
}



