package com.project1.instagramlite.Model;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project1.instagramlite.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCmt extends RecyclerView.Adapter<AdapterCmt.CommentViewHolder> {
    private Activity context;
    List<Comments> commentsList;
    List<Users>usersList;
    public AdapterCmt(Activity context,List<Comments> commentsList,List<Users> usersList){
        this.context=context;
        this.commentsList=commentsList;
        this.usersList=usersList;
    }
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view= LayoutInflater.from(context).inflate(R.layout.each_comment,parent,false);
       return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comments comment= commentsList.get(position);
        holder.setmComment(comment.getComment());
        Users users=usersList.get(position);
        holder.setmUsername(users.getName());
        holder.setmCircleImageView(users.getImage());
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView mComment,mUsername;
        View view;
        CircleImageView mCircleImageView;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }
   public void setmComment(String comment){
            mComment = view.findViewById(R.id.comments_cmtfrag);
            mComment.setText(comment);
        }
public void setmUsername(String username){
            mUsername=view.findViewById(R.id.username_cmt);
            mUsername.setText(username);
}
public void setmCircleImageView(String circleImageView){
            mCircleImageView=view.findViewById(R.id.profilepic_cmt);
    Picasso.get().load(circleImageView).into(mCircleImageView);

}




    }
}
