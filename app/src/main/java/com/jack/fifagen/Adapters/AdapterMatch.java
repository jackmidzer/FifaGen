package com.jack.fifagen.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import com.jack.fifagen.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterMatch extends RecyclerView.Adapter<AdapterMatch.MyHolder>{

    //Firebase
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;

    private Context context;
    private List<ModelMatch> matchList;

    private String matchId, approved;

    //constructor
    public AdapterMatch(Context context, List<ModelMatch> matchList) {
        this.context = context;
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_match, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //firebase
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String myUid = auth.getCurrentUser().getUid();

        //get data
        String homeTeam = matchList.get(i).getHomeTeam();
        String awayTeam = matchList.get(i).getAwayTeam();
        String homePlayer = matchList.get(i).getHomePlayer();
        String awayPlayer = matchList.get(i).getAwayPlayer();
        String homeScore = matchList.get(i).getHomeScore();
        String awayScore = matchList.get(i).getAwayScore();
        String homeBadge = matchList.get(i).getHomeBadge();
        String awayBadge = matchList.get(i).getAwayBadge();
        String timestamp = matchList.get(i).getTimestamp();
        String winner = matchList.get(i).getWinner();
        approved = matchList.get(i).getIsApproved();
        matchId = matchList.get(i).getMatchId();

        //convert timestamp to dd/mm/yy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd MMMM yyyy", calendar).toString();

        //set data
        myHolder.homeTeamTv.setText(homeTeam);
        myHolder.awayTeamTv.setText(awayTeam);
        myHolder.homePlayerTv.setText(homePlayer);
        myHolder.awayPlayerTv.setText(awayPlayer);
        myHolder.homeScoreTv.setText(homeScore);
        myHolder.awayScoreTv.setText(awayScore);
        if (winner.equals(myUid)) {
            myHolder.homeScoreTv.setTextColor(Color.parseColor("#2fbf15"));
            myHolder.awayScoreTv.setTextColor(Color.parseColor("#2fbf15"));
            myHolder.scoreTv.setTextColor(Color.parseColor("#2fbf15"));
        }else if (winner.equals("none")) {

        }else {
            myHolder.homeScoreTv.setTextColor(Color.parseColor("#eb1a1a"));
            myHolder.awayScoreTv.setTextColor(Color.parseColor("#eb1a1a"));
            myHolder.scoreTv.setTextColor(Color.parseColor("#eb1a1a"));
        }
        int homeResID = context.getResources().getIdentifier(homeBadge, "drawable", context.getPackageName());
        myHolder.homeBadgeIv.setImageResource(homeResID);
        int awayResID = context.getResources().getIdentifier(awayBadge, "drawable", context.getPackageName());
        myHolder.awayBadgeIv.setImageResource(awayResID);
        myHolder.timestampTv.setText(dateTime);

        //click notification to accept/reject match
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (approved.equals("pending")) {
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
                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //delete notification and delete match
                            respondToMatch(false);
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            }
        });
    }

    private void respondToMatch(final boolean approve) {
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

                        if (approve) {
                            //approve match
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("isApproved", "approved");
                            reference.child(ds.getKey()).child("isApproved").setValue("approved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Match approved...", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView homeBadgeIv, awayBadgeIv;
        TextView homeTeamTv, homePlayerTv, homeScoreTv, scoreTv, awayTeamTv, awayPlayerTv, awayScoreTv, timestampTv;

        MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            homeBadgeIv = itemView.findViewById(R.id.homeBadgeId);
            awayBadgeIv = itemView.findViewById(R.id.awayBadgeId);
            homeTeamTv = itemView.findViewById(R.id.homeTeamId);
            awayTeamTv = itemView.findViewById(R.id.awayTeamId);
            homePlayerTv = itemView.findViewById(R.id.homePlayerId);
            awayPlayerTv = itemView.findViewById(R.id.awayPlayerId);
            homeScoreTv = itemView.findViewById(R.id.homeScoreId);
            scoreTv = itemView.findViewById(R.id.scoreId);
            awayScoreTv = itemView.findViewById(R.id.awayScoreId);
            timestampTv = itemView.findViewById(R.id.timestampId);
        }
    }
}
