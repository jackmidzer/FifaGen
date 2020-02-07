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
import androidx.appcompat.app.AlertDialog;
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
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private String timestamp;
    private String matchId;

    public AdapterNotification(Context context, ArrayList<ModelNotification> notificationsList) {
        this.context = context;
        this.notificationsList = notificationsList;
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

        //click notification to accept/reject match
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show accept reject dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Approve Match");
                builder.setMessage("Do you wish to approve this match");
                builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete notification and approve match
                        respondToMatch(true);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete notification and delete match
                        respondToMatch(false);
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

    private void respondToMatch(final boolean isApproved) {
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = firebaseDatabase.getReference("Matches");

        //get all data
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelMatch modelMatch = ds.getValue(ModelMatch.class);

                    //get all matches for currently signed in user
                    if ((modelMatch.getHomeUid().equals(user.getUid()) || modelMatch.getAwayUid().equals(user.getUid())) && modelMatch.getMatchId().equals(matchId)) {

                        if (isApproved) {
                            //approve match
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("isApproved", "approved");
                            reference.child(ds.getKey()).child("isApproved").setValue("approved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Match approved...", Toast.LENGTH_SHORT).show();
                                    deleteNotification(timestamp);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "AdapterNotification <1>: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            //reject match and delete
                            ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Match rejected...", Toast.LENGTH_SHORT).show();
                                    deleteNotification(timestamp);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "AdapterNotification <2>: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
