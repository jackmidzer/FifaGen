package com.jack.fifagen.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jack.fifagen.ChatActivity;
import com.jack.fifagen.Models.ModelUser;
import com.jack.fifagen.R;
import com.jack.fifagen.TheirProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    private Context context;
    private List<ModelUser> userList;

    //constructor
    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        final String theirUid = userList.get(i).getUid();
        String theirAvatar = userList.get(i).getAvatar();
        String theirName = userList.get(i).getName();
        final String theirEmail = userList.get(i).getEmail();

        //set data
        myHolder.nameTv.setText(theirName);
        myHolder.emailTv.setText(theirEmail);
        try {
            Picasso.get().load(theirAvatar).placeholder(R.drawable.ic_default_img).into(myHolder.avatarIv);
        }
        catch (Exception e) {
            //Toast.makeText(context, "AdapterUsers <1>: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //handle item click
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Profile", "Chat"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //profile clicked, go to their profile
                            Intent intent = new Intent(context, TheirProfileActivity.class);
                            intent.putExtra("theirUid", theirUid);
                            context.startActivity(intent);
                        }
                        if (which ==1) {
                            //chat clicked, go to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("theirUid", theirUid);
                            context.startActivity(intent);
                        }
                    }
                });
                builder.create().show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView avatarIv;
        TextView nameTv, emailTv;

        MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            avatarIv = itemView.findViewById(R.id.avatarId);
            nameTv = itemView.findViewById(R.id.nameId);
            emailTv = itemView.findViewById(R.id.emailId);
        }
    }
}
