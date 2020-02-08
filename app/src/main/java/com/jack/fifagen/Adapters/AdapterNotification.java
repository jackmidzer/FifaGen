package com.jack.fifagen.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jack.fifagen.DashboardActivity;
import com.jack.fifagen.Fragments.ProfileFragment;
import com.jack.fifagen.Models.ModelMatch;
import com.jack.fifagen.Models.ModelNotification;
import com.jack.fifagen.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.MyHolder> {

    private Context context;
    private ArrayList<ModelNotification> notificationsList;
    private FragmentManager manager;
    private DashboardActivity dashboardActivity;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private String timestamp;
    private String matchId;

    public AdapterNotification(Context context, ArrayList<ModelNotification> notificationsList, FragmentManager manager, DashboardActivity dashboardActivity) {
        this.context = context;
        this.notificationsList = notificationsList;
        this.manager = manager;
        this.dashboardActivity = dashboardActivity;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_notification, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int i) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //get data
        final ModelNotification modelNotification = notificationsList.get(i);
        matchId = modelNotification.getMatchId();
        String message = modelNotification.getMessage();
        timestamp = modelNotification.getTimestamp();
        String senderUid = modelNotification.getSenderUid();

        //convert timestamp to dd/mm/yy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yy hh:mm aa", calendar).toString();

        //get name, email and image of sender
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(senderUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    String senderName = ""+ds.child("name").getValue();
                    String senderImage = ""+ds.child("avatar").getValue();
                    String senderEmail = ""+ds.child("email").getValue();

                    //add to model
                    modelNotification.setSenderName(senderName);
                    modelNotification.setSenderImage(senderImage);
                    modelNotification.setSenderEmail(senderEmail);

                    //set to views
                    if (!senderName.isEmpty()) {
                        holder.nameTv.setText(senderName);
                    }else {
                        holder.nameTv.setText(senderEmail);
                    }
                    try {
                        Picasso.get().load(senderImage).placeholder(R.drawable.ic_default_img).into(holder.avatarIv);
                    }catch (Exception e) {
                        holder.avatarIv.setImageResource(R.drawable.ic_default_img);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show view pending dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Approve Match");
                builder.setMessage("Do you wish to view your matches to approve this match?");
                builder.setPositiveButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //profile fragment transaction
                        dashboardActivity.actionBar.setTitle("Profile");
                        dashboardActivity.navigationView.setSelectedItemId(R.id.profileId);
                        ProfileFragment fragment1 = new ProfileFragment();
                        FragmentTransaction ft1 = manager.beginTransaction();
                        ft1.replace(R.id.contentId, fragment1, "");
                        ft1.commit();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Delete notification", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNotification(timestamp);
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        //set to views
        holder.messageTv.setText(message);
        holder.timestampTv.setText(dateTime);
    }

    private void deleteNotification(String timestamp) {
        //delete notification and approve match
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Notifications").child(timestamp).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //deleted
                Toast.makeText(context, "Notification deleted...", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed
                Toast.makeText(context, "AdapterNotification <3>: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    //holder class for views of row_notifications
    class MyHolder extends RecyclerView.ViewHolder {
        //declare views
        ImageView avatarIv;
        TextView nameTv, messageTv, timestampTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            avatarIv = itemView.findViewById(R.id.avatarId);
            nameTv = itemView.findViewById(R.id.nameId);
            messageTv = itemView.findViewById(R.id.messageId);
            timestampTv = itemView.findViewById(R.id.timestampId);
        }
    }
}
