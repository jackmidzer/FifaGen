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

import com.jack.fifagen.Activities.ChatActivity;
import com.jack.fifagen.Models.ModelChatlist;
import com.jack.fifagen.Models.ModelUser;
import com.jack.fifagen.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder>{

    Context context;
    List<ModelUser> userList;
    private HashMap<String, String> lastMessageMap;

    public AdapterChatlist(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        this.lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        final String theirUid = userList.get(i).getUid();
        String theirAvatar = userList.get(i).getAvatar();
        String theirName = userList.get(i).getName();
        String theirEmail = userList.get(i).getEmail();
        String lastMessage = lastMessageMap.get(theirUid);

        //set data
        if (!theirName.isEmpty()) {
            myHolder.nameTv.setText(theirName);
        }else {
            myHolder.nameTv.setText(theirEmail);
        }
        if (lastMessage == null || lastMessage.equals("default")){
            myHolder.lastMessageTv.setVisibility(View.GONE);
        }else {
            myHolder.lastMessageTv.setVisibility(View.VISIBLE);
            myHolder.lastMessageTv.setText(lastMessage);
        }
        try {
            Picasso.get().load(theirAvatar).placeholder(R.drawable.ic_default_img).into(myHolder.avatarIv);
        }
        catch (Exception e) {
            //do nothing
        }
        //set online status pf users in chatlist
        if (userList.get(i).getOnlineStatus().equals("online")) {
            //online
            myHolder.onlineStatusIv.setImageResource(R.drawable.circle_online);
        }else {
            //offline
            myHolder.onlineStatusIv.setImageResource(R.drawable.circle_offline);
        }

        //handle user click
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start chat activity with user
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("theirUid", theirUid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessageMap(String theirId, String lastMessage) {
        lastMessageMap.put(theirId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        //views of row_chatlist
        ImageView avatarIv, onlineStatusIv;
        TextView nameTv, lastMessageTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarId);
            onlineStatusIv = itemView.findViewById(R.id.onlineStatusId);
            nameTv = itemView.findViewById(R.id.nameId);
            lastMessageTv =itemView.findViewById(R.id.messageId);
        }
    }
}
