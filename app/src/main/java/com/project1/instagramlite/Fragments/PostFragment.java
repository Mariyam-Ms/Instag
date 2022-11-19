package com.project1.instagramlite.Fragments;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project1.instagramlite.R;
import com.project1.instagramlite.databinding.FragmentPostBinding;

import java.util.HashMap;


public class PostFragment extends Fragment {

private FragmentPostBinding binding;
private NavController navController;
private FirebaseFirestore firebaseFirestore;
private FirebaseAuth auth;
private StorageReference storageReference;
Uri postImageUri=null;
String currentUserId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentPostBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    navController= Navigation.findNavController(view);
    firebaseFirestore=FirebaseFirestore.getInstance();
    auth=FirebaseAuth.getInstance();
    storageReference= FirebaseStorage.getInstance().getReference();
    binding.progressBar3.setVisibility(View.INVISIBLE);
    binding.imageToPost.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mGetContent.launch("image/*");
        }
    });
    binding.postToPublic.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String About=binding.about.getText().toString();
            if(!About.isEmpty()&& postImageUri != null){
                StorageReference postReff=storageReference.child("PostImages").child(FieldValue.serverTimestamp().toString() + ".jpg");
                postReff.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                        postReff.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap<String,Object> postMap=new HashMap<>();
                                postMap.put("image" ,uri.toString());
                                postMap.put("user" ,currentUserId);
                                postMap.put("about" ,About);
                                postMap.put("time",FieldValue.serverTimestamp());
                                firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
if(task.isSuccessful()){
    Toast.makeText(getContext(), "Post Added Successfully", Toast.LENGTH_SHORT).show();
    navController.navigate(R.id.action_postFragment_to_homeFragment);
}else{
    Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();

}                                    }
                                });
                            }
                        });





                        }else {
                            //Why you wil write few time to String and few times msg
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }else{
                binding.progressBar3.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Please add Image and Write Caption", Toast.LENGTH_SHORT).show();
            }
        }
    });

    }
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    postImageUri = uri;
                    binding.imageToPost.setImageURI(postImageUri);

                }
            });

}