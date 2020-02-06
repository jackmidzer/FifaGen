package com.jack.fifagen.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jack.fifagen.Adapters.AdapterOpponents;
import com.jack.fifagen.Models.ModelUser;
import com.jack.fifagen.R;

import java.util.ArrayList;
import java.util.List;

public class OpponentsActivity extends AppCompatActivity {

    //firebase auth
    private FirebaseAuth firebaseAuth;

    private RecyclerView recyclerView;
    private AdapterOpponents adapterOpponents;
    private List<ModelUser> userList;

    ActionBar actionBar;

    Intent teamInfo;

    //team info
    String homeTeam;
    String awayTeam;
    String homeBadge;
    String awayBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opponent);

        //init
        firebaseAuth = FirebaseAuth.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setTitle("Select Opponent");
        //enable back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //init recycler view
        recyclerView = findViewById(R.id.users_recyclerViewId);
        //set its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(OpponentsActivity.this));

        //init user list
        userList = new ArrayList<>();

        //get uid of clicked user
        teamInfo = getIntent();
        homeTeam = teamInfo.getStringExtra("homeTeam");
        awayTeam = teamInfo.getStringExtra("awayTeam");
        homeBadge = teamInfo.getStringExtra("homeBadge");
        awayBadge = teamInfo.getStringExtra("awayBadge");
        
        //get all users
        getAllUsers();
    }

    private void getAllUsers() {
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        //get all data
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //get all users except currently signed in user
                    if (!modelUser.getUid().equals(user.getUid())) {
                        userList.add(modelUser);
                    }

                    //adapter
                    adapterOpponents = new AdapterOpponents(OpponentsActivity.this, userList, teamInfo);
                    recyclerView.setAdapter(adapterOpponents);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUsers(final String query) {
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        //get all data
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //get all searched users except currently signed in user
                    if (!modelUser.getUid().equals(user.getUid()) && modelUser != null) {
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) || modelUser.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            userList.add(modelUser);
                        }
                    }

                    //adapter
                    adapterOpponents = new AdapterOpponents(OpponentsActivity.this, userList, teamInfo);
                    //refresh adapter
                    adapterOpponents.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterOpponents);
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
            startActivity(new Intent(OpponentsActivity.this, MainActivity.class));
            finish();
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

    //inflate options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflating menu
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //search view
        MenuItem item = menu.findItem(R.id.searchId);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //if search query not empty then search
                if (!TextUtils.isEmpty(s.trim())) {
                    searchUsers(s);
                }else {
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //if search query not empty then search
                if (!TextUtils.isEmpty(s.trim())) {
                    searchUsers(s);
                }else {
                    getAllUsers();
                }
                return false;
            }
        });
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
