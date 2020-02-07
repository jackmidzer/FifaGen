package com.jack.fifagen.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    private Context context;
    private List<ModelMatch> matchList;

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
