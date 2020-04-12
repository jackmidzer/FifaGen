package com.jack.fifagen.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.jack.fifagen.Activities.MainActivity;
import com.jack.fifagen.Adapters.AdapterMatch;
import com.jack.fifagen.Models.ModelMatch;
import com.jack.fifagen.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class TheirProfileActivity extends AppCompatActivity {

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private RecyclerView recyclerView;
    private AdapterMatch adapterMatch;
    private List<ModelMatch> matchList;

    //views
    private ImageView avatarIv;
    private ImageView coverIv;
    private TextView nameTv, emailTv, phoneTv, noResultsTv;
    private RadioGroup tabBtns;
    private CardView cardViewLayout;

    public String matchStatus = "approved";

    String theirUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_their_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
        storageReference = getInstance().getReference();

        //init views
        avatarIv = findViewById(R.id.avatarId);
        coverIv = findViewById(R.id.coverId);
        nameTv = findViewById(R.id.nameId);
        emailTv = findViewById(R.id.emailId);
        phoneTv = findViewById(R.id.phoneId);
        tabBtns = findViewById(R.id.tabLayoutId);
        noResultsTv = findViewById(R.id.noResultsId);
        cardViewLayout = findViewById(R.id.cardViewId);

        //init recycler view
        recyclerView = findViewById(R.id.matches_recyclerViewId);
        //set its properties
        LinearLayoutManager layoutManager = new LinearLayoutManager(TheirProfileActivity.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //get uid of clicked user
        Intent intent = getIntent();
        theirUid = intent.getStringExtra("theirUid");

        //query firebase database
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(theirUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get data from database
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String avatar = "" + ds.child("avatar").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    //set data to the views
                    if (!name.isEmpty()) {
                        nameTv.setText(name);
                    }
                    emailTv.setText(email);
                    if (!phone.isEmpty()) {
                        phoneTv.setText(phone);
                    }
                    if (!avatar.isEmpty()) {
                        try {
                            //set the profile picture
                            Picasso.get().load(avatar).placeholder(R.drawable.ic_default_img_white).into(avatarIv);
                        } catch (Exception e) {
                            //set a default image
                            Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);
                        }
                    }
                    if (!cover.isEmpty()) {
                        try {
                            //set the cover photo
                            Picasso.get().load(cover).into(coverIv);
                        } catch (Exception e) {
                            //set a default image
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkUserStatus();

        //init match list
        matchList = new ArrayList<>();
        //get all users
        getAllMatches(matchStatus);

        tabBtns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View selectedTab = tabBtns.findViewById(checkedId);
                int index = tabBtns.indexOfChild(selectedTab);

                // Add logic here

                switch (index) {
                    case 0:
                        //approved
                        noResultsTv.setText("No Matches Played");
                        cardViewLayout.setVisibility(View.GONE);
                        matchStatus = "approved";
                        getAllMatches(matchStatus);
                        break;
//                    case 1:
//                        //stats
//                        noResultsTv.setText("No Stats Recorded");
//                        cardViewLayout.setVisibility(View.GONE);
//                        break;
                }
            }
        });
    }

    private void getAllMatches(final String status) {
        //get current user
        DatabaseReference reference = database.getReference("Matches");

        //get all data
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelMatch modelMatch = ds.getValue(ModelMatch.class);

                    //get all approved or pending matches for currently signed in user
                    if ((modelMatch.getHomeUid().equals(theirUid) || modelMatch.getAwayUid().equals(theirUid)) && modelMatch.getIsApproved().equals(status)) {
                        matchList.add(modelMatch);
                    }

                    //adapter
                    adapterMatch = new AdapterMatch(TheirProfileActivity.this, matchList);
                    adapterMatch.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterMatch);
                }
                if (adapterMatch.getItemCount() == 0){
                    cardViewLayout.setVisibility(View.VISIBLE);
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
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //inflate options menu
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        //inflating menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
