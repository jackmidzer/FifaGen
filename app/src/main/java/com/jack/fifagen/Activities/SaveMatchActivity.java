package com.jack.fifagen.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jack.fifagen.DashboardActivity;
import com.jack.fifagen.Fragments.HomeFragment;
import com.jack.fifagen.Models.ModelUser;
import com.jack.fifagen.R;
import com.jack.fifagen.notifications.Data;
import com.jack.fifagen.notifications.Sender;
import com.jack.fifagen.notifications.Token;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SaveMatchActivity extends AppCompatActivity {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ActionBar actionBar;
    ProgressDialog progressDialog;

    //views
    public TextView homeTeamTv, awayTeamTv, homePlayerTv, awayPlayerTv;
    public ImageView homeBadgeIv, awayBadgeIv;
    public EditText homeScoreEt;
    public EditText awayScoreEt;
    public Button saveBtn;
    public ImageButton swapBtn;

    //team info
    String homeTeam;
    String awayTeam;
    String homeBadge;
    String awayBadge;
    String homePlayer;
    String awayPlayer;

    String myUid;
    String theirUid;
    String theirName;

    //volley request queue for notifications
    private RequestQueue requestQueue;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_match);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Save Match");
        //enable back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getCurrentUser().getUid();

        progressDialog = new ProgressDialog(this);

        //init
        homeTeamTv = findViewById(R.id.homeTeamId);
        awayTeamTv = findViewById(R.id.awayTeamId);
        homeBadgeIv = findViewById(R.id.homeBadgeId);
        awayBadgeIv = findViewById(R.id.awayBadgeId);
        homePlayerTv = findViewById(R.id.homePlayerId);
        awayPlayerTv = findViewById(R.id.awayPlayerId);
        homeScoreEt = findViewById(R.id.homeScoreId);
        awayScoreEt = findViewById(R.id.awayScoreId);
        saveBtn = findViewById(R.id.saveId);
        swapBtn = findViewById(R.id.swapId);

        //get uid and team info
        final Intent intent = getIntent();
        homeTeam = intent.getStringExtra("homeTeam");
        awayTeam = intent.getStringExtra("awayTeam");
        homeBadge = intent.getStringExtra("homeBadge");
        awayBadge = intent.getStringExtra("awayBadge");
        theirUid = intent.getStringExtra("theirUid");
        theirName = intent.getStringExtra("theirName");
        awayPlayer = theirName;

        //volley
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //query firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = databaseReference.orderByChild("uid").equalTo(myUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get data from database
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    if (!name.isEmpty()) {
                        homePlayer = name;
                    }else {
                        homePlayer = email;
                    }
                    homePlayerTv.setText(homePlayer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //put info into views
        homeTeamTv.setText(homeTeam);
        awayTeamTv.setText(awayTeam);
        int homeResId = getResources().getIdentifier(homeBadge, "drawable", getPackageName());
        homeBadgeIv.setImageResource(homeResId);
        int awayResID = getResources().getIdentifier(awayBadge, "drawable", getPackageName());
        awayBadgeIv.setImageResource(awayResID);
        awayPlayerTv.setText(awayPlayer);

        //handle swap button click
        swapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence temp;
                temp = homePlayerTv.getText();
                homePlayerTv.setText(awayPlayerTv.getText());
                awayPlayerTv.setText(temp);
            }
        });

        //handle send button click tp send message
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                //get text from edit text
                String homeScore = homeScoreEt.getText().toString().trim();
                String awayScore = awayScoreEt.getText().toString().trim();
                //check if text is empty or not
                if (TextUtils.isEmpty(homeScore) || TextUtils.isEmpty(awayScore)) {
                    //at least one score is empty
                    Toast.makeText(SaveMatchActivity.this, "Cannot save without both scores...", Toast.LENGTH_SHORT).show();
                }else {
                    //scores are not empty
                    saveMatch(homeScore, awayScore);
                    //go to home after saving match
                    homeScoreEt.setText("");
                    awayScoreEt.setText("");
                    Intent intent1 = new Intent(SaveMatchActivity.this, DashboardActivity.class);
                    startActivity(intent1);
                }
            }
        });

    }

    private void saveMatch(String homeScore, String awayScore) {
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();

        String timestamp = String.valueOf(System.currentTimeMillis());
        String winner;
        String homeUid;
        String awayUid;
        homePlayer = homePlayerTv.getText().toString();
        awayPlayer = awayPlayerTv.getText().toString();

        //store chat info in database using hashmap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("homeTeam", homeTeam);
        hashMap.put("awayTeam", awayTeam);
        hashMap.put("homeBadge", homeBadge);
        hashMap.put("awayBadge", awayBadge);
        Integer hScore = Integer.parseInt(homeScore);
        Integer aScore = Integer.parseInt(awayScore);
        if (homePlayer.trim().equals(theirName.trim())) {
            homeUid = theirUid;
            awayUid = myUid;
            if (hScore > aScore) {
                winner  = theirUid;
            }else if (aScore > hScore) {
                winner = myUid;
            }else {
                winner = "none";
            }
        }else {
            homeUid = myUid;
            awayUid = theirUid;
            if (hScore > aScore) {
                winner  = myUid;
            }else if (aScore > hScore) {
                winner = theirUid;
            }else {
                winner = "none";
            }
        }
        final String matchId = ""+homeUid+awayUid+timestamp;
        hashMap.put("matchId", matchId);
        hashMap.put("homePlayer", "You");
        hashMap.put("awayPlayer", awayPlayer);
        hashMap.put("homeUid", homeUid);
        hashMap.put("awayUid", awayUid);
        hashMap.put("homeScore", homeScore);
        hashMap.put("awayScore", awayScore);
        hashMap.put("isApproved", "pending");
        hashMap.put("timestamp", timestamp);
        hashMap.put("winner", winner);
        databaseReference.child("Matches").push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addToTheirNotifications(theirUid, matchId, "Created a match. Accept/Reject?");
                progressDialog.dismiss();
                Toast.makeText(SaveMatchActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SaveMatchActivity.this, "SavedMatchActivity <1>: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (notify) {
                    sendNotification(theirUid, user.getName(), "Created a new match. Accept/Reject?");
                }
                notify = false;
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void addToTheirNotifications(String theirUid, String matchId, String message) {
        String timestamp = ""+ System.currentTimeMillis();
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("matchId", matchId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("theirUid", theirUid);
        hashMap.put("message", message);
        hashMap.put("senderUid", myUid);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(theirUid).child("Notifications").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //added successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed
            }
        });
    }

    private void sendNotification(final String theirUid, final String name, final String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(theirUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUid, name+": "+message, "New Message", theirUid, R.drawable.ic_default_img);

                    Sender sender = new Sender(data, token.getToken());

                    //fcm json object request
                    try {
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //response of request
                                Log.d("JSON_RESPONSE", "onResponse: "+response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse: "+error.toString());
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                //put params
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAADHXhgfE:APA91bHNigKFUaRM3jFseSbo5OyWeO5KUB0mM_eJ2zwUzoLF7pjhFeIt8b7XtwsYiB3sjkBLMSE0OiOkQVP33rjFDx9IDt2AQ2nWUnQomKJtktCw6LdY5jFcPiUPF4kOPY0_h8A7e1P0");

                                return headers;
                            }
                        };

                        //add this request to the queue
                        requestQueue.add(jsonObjectRequest);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            //user not signed in, go to main activity
            startActivity(new Intent(SaveMatchActivity.this, MainActivity.class));
            finish();
        }else {
            myUid = user.getUid(); //currently signed in users uid
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //hide search view
        menu.findItem(R.id.searchId).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }
}
