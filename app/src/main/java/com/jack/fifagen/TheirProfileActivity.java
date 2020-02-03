package com.jack.fifagen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TheirProfileActivity extends AppCompatActivity {

    //firebase
    private FirebaseAuth firebaseAuth;

    //views
    private ImageView avatarIv;
    private ImageView coverIv;
    private TextView nameTv;
    private TextView emailTv;
    private TextView phoneTv;

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

        //init views
        avatarIv = findViewById(R.id.avatarId);
        coverIv = findViewById(R.id.coverId);
        nameTv = findViewById(R.id.nameId);
        emailTv = findViewById(R.id.emailId);
        phoneTv = findViewById(R.id.phoneId);

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

        //load their stats
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
