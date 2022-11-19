package com.project1.instagramlite.Model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project1.instagramlite.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRcv extends RecyclerView.Adapter<AdapterRcv.PostViewHolder> {
    private List<Data> mList;
    private List<Users>usersList1;
    private Activity context;
FirebaseFirestore firebaseFirestore;
FirebaseAuth auth;
NavController navController;
    public AdapterRcv(Activity context, List<Data> mList,List<Users> usersList1) {
        this.mList = mList;
        this.context = context;
        this.usersList1=usersList1;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.each_item, parent, false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        navController= Navigation.findNavController(v);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
       Data post=mList.get(position);
       holder.setPostPic(post.getImage());
       holder.setCaption(post.getAbout());


//        long milliseconds=post.getTime();
//                String date= DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
//                holder.setTime(date);



                    String username=usersList1.get(position).getName();
                    String image=usersList1.get(position).getImage();
                    holder.setpNameabove(username);
                    holder.setpNameBelow(username);
                    holder.setProfilePic(image);


        //Like Count
        String postId=post.PostId;
        String currentUserId=auth.getCurrentUser().getUid();
        holder.likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/"+postId+"/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String,Object>likesMap=new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/"+postId+"/Likes").document(currentUserId).set(likesMap);
                        }else{
                            firebaseFirestore.collection("Posts/"+postId+"/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });
        //like colour change
        firebaseFirestore.collection("Posts/"+postId+"/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error==null){
                    if(value.exists()){
holder.likeImg.setImageDrawable(context.getDrawable(R.drawable.lovecolour));
                    }else {
                        holder.likeImg.setImageDrawable(context.getDrawable(R.drawable.favourite));
                    }
                }
            }
        });
        //Like Count
        firebaseFirestore.collection("Posts/"+postId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error==null){
                    if(!value.isEmpty()){
                        int count=value.size();
                        holder.setLikeCount(count);
                    }else{
                        holder.setLikeCount(0);
                    }
                }
            }
        });
        //Comments
        holder.cmtImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("postid",postId);

                navController.navigate(R.id.action_homeFragment_to_commentFragment,bundle);

            }
        });

        if(currentUserId.equals(post.getUser())){
            holder.delete_btn.setVisibility(View.VISIBLE);
            holder.delete_btn.setClickable(true);
            holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Delete").setMessage("Are You Sure").setNegativeButton("No", null).
                            setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    firebaseFirestore.collection("Posts/"+postId+"/Comments").get().
                                            addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for (QueryDocumentSnapshot snapshot :task.getResult()){
                                                    firebaseFirestore.collection("Posts/"+postId+"/Comments").document(snapshot.getId()).delete();
                                                }
                                                }
                                            });
                                    firebaseFirestore.collection("Posts/"+postId+"/Likes").get().
                                            addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    for (QueryDocumentSnapshot snapshot :task.getResult()){
                                                        firebaseFirestore.collection("Posts/"+postId+"/Likes").document(snapshot.getId()).delete();
                                                    }
                                                }
                                            });
                                firebaseFirestore.collection("Posts").document(postId).delete();
                                mList.remove(position);
                                notifyDataSetChanged();
                                }
                            });
                    alert.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postPic, likeImg, cmtImg;
        CircleImageView profilePic;
        TextView pNameabove, pNameBelow, caption, time, likeCount;
        View mView;
        ImageButton delete_btn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            likeImg=mView.findViewById(R.id.likeImg);
            cmtImg=mView.findViewById(R.id.comment);
            delete_btn=mView.findViewById(R.id.delete);
        }
        public void setLikeCount(int count){
            likeCount=mView.findViewById(R.id.likeImg);
            likeCount.setText(count + "  Likes");
        }

        public void setPostPic(String urlPost) {
            postPic = mView.findViewById(R.id.pic_post);
            Picasso.get().load(urlPost).into(postPic);
        }

        public void setProfilePic(String urlProfilePic) {
            profilePic = mView.findViewById(R.id.profilepic_rcv);
            Picasso.get().load(urlProfilePic).into(profilePic);
        }

        public void setpNameabove(String pNameabove1) {
            pNameabove = mView.findViewById(R.id.username_aboveP);
            // pNameBelow=mView.findViewById(R.id.username_belowP);
            pNameabove.setText(pNameabove1);
            //   pNameBelow.setText(b);
        }

        public void setpNameBelow(String pNameBelow1) {
            pNameBelow = mView.findViewById(R.id.username_belowP);
            pNameBelow.setText(pNameBelow1);
        }

        public void setTime(String date) {
            time = mView.findViewById(R.id.time_of_post);
            time.setText(date);

        }

        public void setCaption(String caption1 ){
        caption=mView.findViewById(R.id.about_rcv);
        caption.setText(caption1);
        }
    }
}
