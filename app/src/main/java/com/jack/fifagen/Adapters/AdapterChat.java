package com.jack.fifagen.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jack.fifagen.Models.ModelChat;
import com.jack.fifagen.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<ModelChat> chatList;
    private String imageUrl;

    //firebase
    private FirebaseUser user;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layouts
        if (i==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, viewGroup, false);
            return new MyHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, viewGroup, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        String message = chatList.get(i).getMessage();
        String timestamp = chatList.get(i).getTimestamp();

        //convert timestamp to dd/mm/yy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yy hh:mm aa", calendar).toString();

        //set data
        myHolder.messageTv.setText(message);
        myHolder.timestampTv.setText(dateTime);
        try {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_default_img).into(myHolder.avatarIv);
        }
        catch (Exception e ) {
            Picasso.get().load(R.drawable.ic_default_img).into(myHolder.avatarIv);
        }

        //set seen/delivered status of message
        if (i == chatList.size()-1) {
            if (chatList.get(i).isSeen()) {
                myHolder.deliveredTv.setText(R.string.seen);
            }else {
                myHolder.deliveredTv.setText(R.string.delivered);
            }
        }else {
            myHolder.deliveredTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed in user
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(user.getUid())) {
            return MSG_TYPE_RIGHT;
        }else {
            return  MSG_TYPE_LEFT;
        }
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        //views
        ImageView avatarIv;
        TextView messageTv, timestampTv, deliveredTv;

        MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            avatarIv = itemView.findViewById(R.id.avatarId);
            messageTv = itemView.findViewById(R.id.messageId);
            timestampTv = itemView.findViewById(R.id.timestampId);
            deliveredTv = itemView.findViewById(R.id.deliveredId);
        }
    }
}
