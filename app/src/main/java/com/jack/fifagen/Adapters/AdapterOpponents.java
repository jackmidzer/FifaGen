package com.jack.fifagen.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jack.fifagen.Models.ModelUser;
import com.jack.fifagen.R;
import com.jack.fifagen.Activities.SaveMatchActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterOpponents extends RecyclerView.Adapter<AdapterOpponents.MyHolder> {
    private Context context;
    private List<ModelUser> userList;
    private Intent teamInfo;

    //constructor
    public AdapterOpponents(Context context, List<ModelUser> userList, Intent teamInfo) {
        this.context = context;
        this.userList = userList;
        this.teamInfo = teamInfo;
    }

    @NonNull
    @Override
    public AdapterOpponents.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup, false);
        return new AdapterOpponents.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOpponents.MyHolder myHolder, int i) {
        //get data
        final String theirUid = userList.get(i).getUid();
        String theirAvatar = userList.get(i).getAvatar();
        final String theirName = userList.get(i).getName();
        final String theirEmail = userList.get(i).getEmail();

        //set data
        myHolder.nameTv.setText(theirName);
        myHolder.emailTv.setText(theirEmail);
        try {
            Picasso.get().load(theirAvatar).placeholder(R.drawable.ic_default_img).into(myHolder.avatarIv);
        }
        catch (Exception e) {
            //Toast.makeText(context, "AdapterOpponents <1>: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //handle item click
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user chosen, go to save match activity
                Intent intent = new Intent(context, SaveMatchActivity.class);
                intent.putExtra("theirUid", theirUid);
                if (!theirName.isEmpty()) {
                    intent.putExtra("theirName", theirName);
                }else {
                    intent.putExtra("theirName", theirEmail);
                }
                intent.putExtra("homeTeam", teamInfo.getStringExtra("homeTeam"));
                intent.putExtra("awayTeam", teamInfo.getStringExtra("awayTeam"));
                intent.putExtra("homeBadge", teamInfo.getStringExtra("homeBadge"));
                intent.putExtra("awayBadge", teamInfo.getStringExtra("awayBadge"));
                context.startActivity(intent);
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
