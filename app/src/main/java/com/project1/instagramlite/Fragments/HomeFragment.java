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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project1.instagramlite.MainActivity;
import com.project1.instagramlite.Model.AdapterRcv;
import com.project1.instagramlite.Model.Data;
import com.project1.instagramlite.Model.Users;
import com.project1.instagramlite.R;
import com.project1.instagramlite.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements AdapterRcv.CommentsScreen {

    private FragmentHomeBinding binding;
    private FirebaseAuth auth;
    private NavController navController;
    private FirebaseFirestore firebaseFirestore;
    private AdapterRcv adapterRcv;
    private List<Data> list;

    Uri postImageUri = null;
    private Query query;
    private ListenerRegistration listenerRegistration;
    private List<Users> usersList1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        usersList1 = new ArrayList<>();
        adapterRcv = new AdapterRcv(requireActivity(), list, usersList1,this::moveToCommentFrag);
//        adapterRcv=new AdapterRcv(this);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerview.setAdapter(adapterRcv);

        binding.addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //how can I directely open gallery and selected image set in another fragment bot this
                mGetContent.launch("image/*");
            }
        });
        if (auth.getCurrentUser() != null) {
            binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    //Why do we writen 1
                    Boolean isbottom = !binding.recyclerview.canScrollVertically(1);
                    if (isbottom) {
                        Toast.makeText(getContext(), "Reached bottom", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            query = firebaseFirestore.collection("Posts").orderBy("time", Query.Direction.DESCENDING);

            if (requireActivity() != null) {


                listenerRegistration = query.addSnapshotListener(requireActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange doc : value.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String postId = doc.getDocument().getId();
                                Data post = doc.getDocument().toObject(Data.class).withId(postId);
                                String postUseId = doc.getDocument().getString("user");
                                if(postUseId != null){
                                    firebaseFirestore.collection("Users").document(postUseId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Users users = task.getResult().toObject(Users.class);
                                                usersList1.add(users);
                                                list.add(post);
                                                adapterRcv.notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }


                            } else {
                                adapterRcv.notifyDataSetChanged();
                            }
                        }
                        listenerRegistration.remove();
                    }
                });


            }

        }
    }
    //Ehy do I always get error in OnAttech and onStart

    @Override
    public void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listenerRegistration.remove();
    }

    //
    //TODO CHECK IF USERS DATA IS ALREADY UPLOADED OR NOT
    //
    @Override
    public void onStart() {
        super.onStart();
//       if (auth.getCurrentUser() != null) {
//           Intent intent=new Intent(requireActivity(), MainActivity.class);
//           startActivity(intent);
//        }

        firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().exists()){
                      navController.navigate(R.id.action_homeFragment_to_profileSetupFragment);
                    }
                }
            }
        });

    }
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                     postImageUri = uri;

                    Bundle bundle=new Bundle();
                    bundle.putString("postUri",postImageUri.toString());

                    navController.navigate(R.id.action_homeFragment_to_postFragment,bundle);
                }
            });

    @Override
    public void moveToCommentFrag(Data post) {
        String postId = post.PostId;
         Bundle bundle = new Bundle();
         bundle.putString("postid", postId);
        //Can we pass bundle in nav controller if not then how
        navController.navigate(R.id.action_homeFragment_to_commentFragment,bundle);

    }


}