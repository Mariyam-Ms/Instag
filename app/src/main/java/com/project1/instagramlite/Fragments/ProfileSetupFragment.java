package com.project1.instagramlite.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project1.instagramlite.MainActivity;
import com.project1.instagramlite.R;
import com.project1.instagramlite.databinding.FragmentProfileSetupBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class ProfileSetupFragment extends Fragment {

private FragmentProfileSetupBinding binding;
private FirebaseAuth auth;
private NavController navController;
private StorageReference storageReference;
private FirebaseFirestore firebaseFirestore;
private Uri imageUri=null;
private String Uid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

         binding=FragmentProfileSetupBinding.inflate(inflater,container,false);
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
            binding.progressBar2.setVisibility(View.INVISIBLE);
//        binding.backPs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        firebaseFirestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String imageUrl = task.getResult().getString("image");
                        binding.namePs.setText(name);
                        Picasso.get().load(imageUrl).into(binding.imagePs);
                    }
                }
            }
        });








        binding.setupImageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });
        binding.donePs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =binding.namePs.getText().toString();
//TODO error is coming Crashing if user without any changes try to update

            if(!name.isEmpty() && imageUri!=null){
                binding.progressBar2.setVisibility(View.VISIBLE);
                StorageReference imageReff=storageReference.child("Profilepic").child(Uid+".jpg");
                imageReff.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            binding.progressBar2.setVisibility(View.INVISIBLE);
                            imageReff.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    savetoFireStoreProfile(task,name,uri);

                                }
                            });
                        }else {
                            binding.progressBar2.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else{
                savetoFireStoreProfile(null,name,imageUri);
                Toast.makeText(getContext(), "Please Fill Requirements", Toast.LENGTH_SHORT).show();

            }
            }
        });

    }

    private void savetoFireStoreProfile(Task<UploadTask.TaskSnapshot> task, String name, Uri downloadUri) {
//   downloadUri.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//       @Override
//       public void onSuccess(Uri uri) {
//           downloadUri = uri;

           HashMap<String, Object> map = new HashMap<>();
           map.put("name", name);
           map.put("image", downloadUri.toString());

           firebaseFirestore.collection("Users").document(Uid).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful()) {
                       Toast.makeText(getContext(), "Profile saved", Toast.LENGTH_SHORT).show();
                       navController.navigate(R.id.action_profileSetupFragment_to_homeFragment);
                   } else {
                       Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();

                   }
               }


           });
       }






    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    imageUri = uri;
                    binding.imagePs.setImageURI(imageUri);

                }
            });




}