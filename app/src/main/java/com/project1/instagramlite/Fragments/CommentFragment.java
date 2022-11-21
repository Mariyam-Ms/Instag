package com.project1.instagramlite.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project1.instagramlite.Model.AdapterCmt;
import com.project1.instagramlite.Model.Comments;
import com.project1.instagramlite.Model.PostId;
import com.project1.instagramlite.Model.Users;
import com.project1.instagramlite.R;
import com.project1.instagramlite.databinding.FragmentCommentBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class CommentFragment extends Fragment {

 private FragmentCommentBinding binding;
private NavController navController;
private FirebaseAuth auth;
private FirebaseFirestore firebaseFirestore;
private String post_id;
private String current_user_id;

private AdapterCmt adapterCmt;
private List<Comments>mList;
private List<Users>usersList;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentCommentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mList=new ArrayList<>();
        usersList=new ArrayList<>();
        adapterCmt=new AdapterCmt(requireActivity(),mList,usersList);

        post_id=getArguments().getString("postid");

        current_user_id= Objects.requireNonNull(auth.getCurrentUser()).getUid();
        binding.rcvComments.setHasFixedSize(true);
        binding.rcvComments.setAdapter(adapterCmt);
        firebaseFirestore.collection("Posts/"+post_id+"/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            for(DocumentChange documentChange: value.getDocumentChanges()){
                if(documentChange.getType()==DocumentChange.Type.ADDED){
                    Comments comments=documentChange.getDocument().toObject(Comments.class);
                    String userId=documentChange.getDocument().getString("user");
                    firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(task.isSuccessful()){
                                Users users=task.getResult().toObject(Users.class);
                                usersList.add(users);
                                mList.add(comments);
                                adapterCmt.notifyDataSetChanged();
                            }else{
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }else{
                    adapterCmt.notifyDataSetChanged();
                }
            }
            }
        });




        binding.rcvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.commentpstbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment=binding.commentwr.getText().toString();
                if(!comment.isEmpty()){
                    Map<String,Object> commentMap=new HashMap<>();
                    commentMap.put("comment",comment);
                    commentMap.put("time", FieldValue.serverTimestamp());
                    commentMap.put("user",current_user_id);
                    firebaseFirestore.collection("Posts/"+post_id+"/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                         //   navController.navigate(R.id.action_commentFragment_to_homeFragment);
                            Toast.makeText(getContext(), "Comment Added", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        
                        }
                    });

                }else {
                    Toast.makeText(getContext(), "Please write Comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}