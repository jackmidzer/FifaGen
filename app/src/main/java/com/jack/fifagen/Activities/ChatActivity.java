package com.jack.fifagen.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jack.fifagen.Adapters.AdapterChat;
import com.jack.fifagen.Models.ModelChat;
import com.jack.fifagen.Models.ModelUser;
import com.jack.fifagen.R;
import com.jack.fifagen.notifications.Data;
import com.jack.fifagen.notifications.Sender;
import com.jack.fifagen.notifications.Token;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    //firebase instances
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    //views
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView avatarIv;
    TextView nameTv, statusTv;
    EditText inputEt;
    ImageButton sendBtn;

    //for checking if user has seen message or not
    ValueEventListener seenListener;
    DatabaseReference refForSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;

    String theirUid;
    String myUid;
    String theirAvatar;

    //volley request queue for notifications
    private RequestQueue requestQueue;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //init views
        toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_recyclerViewId);
        avatarIv = findViewById(R.id.avatarId);
        nameTv = findViewById(R.id.nameId);
        statusTv = findViewById(R.id.statusId);
        inputEt = findViewById(R.id.inputId);
        sendBtn = findViewById(R.id.sendId);

        //volley
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //layout for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        //recycler view properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //get uid of user to be chatted with
        Intent intent = getIntent();
        theirUid = intent.getStringExtra("theirUid");

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Users");

        //user query
        Query userQuery = reference.orderByChild("uid").equalTo(theirUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required info is received
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    //get data
                    String name = ""+ ds.child("name").getValue();
                    theirAvatar = ""+ ds.child("avatar").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String onlineStatus = ""+ ds.child("onlineStatus").getValue();
                    String typingStatus = ""+ ds.child("typingTo").getValue();

                    //set data
                    //check typing status
                    if (typingStatus.equals(myUid)) {
                        statusTv.setText(R.string.typing);
                    }else {
                        //get value of online status
                        if (onlineStatus.equals("online")) {
                            statusTv.setText(onlineStatus);
                        }else {
                            //convert timestamp to dd/mm/yy hh:mm am/pm
                            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                            calendar.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime = "Last seen at: " + DateFormat.format("dd/MM/yy hh:mm aa", calendar).toString();
                            statusTv.setText(dateTime);
                        }
                    }
                    if (!name.isEmpty()) {
                        //user hasn't added their name yet
                        nameTv.setText(name);
                    }else {
                        nameTv.setText(email);
                    }
                    if (!theirAvatar.isEmpty()) {
                        //user hasn't added a profile picture yet
                        try {
                            //image received, set to image view in toolbar
                            Picasso.get().load(theirAvatar).placeholder(R.drawable.ic_default_img_white).into(avatarIv);
                        } catch (Exception e) {
                            //exception getting picture, set default image
                            Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //handle send button click tp send message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                //get text from edit text
                String message = inputEt.getText().toString().trim();
                //check if text is empty or not
                if (TextUtils.isEmpty(message)) {
                    //text empty
                    Toast.makeText(ChatActivity.this, "Cannot send empty message...", Toast.LENGTH_SHORT).show();
                }else {
                    //text not empty
                    sendMessage(message);
                }
                //reset edit text after sending message
                inputEt.setText("");
            }
        });

        //check edit text change listener
        inputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    checkTypingStatus("noOne");
                }else {
                    checkTypingStatus(theirUid); //uid of receiver
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        readMessage();

        seenMessage();

    }

    private void seenMessage() {
        refForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = refForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(theirUid)) {
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(theirUid) || chat.getReceiver().equals(theirUid) && chat.getSender().equals(myUid)) {
                        chatList.add(chat);
                    }

                    //adapter
                    adapterChat = new AdapterChat(ChatActivity.this, chatList, theirAvatar);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());
        //store chat info in database using hashmap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", theirUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeen", false);
        databaseReference.child("Chats").push().setValue(hashMap);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (notify) {
                    sendNotification(theirUid, user.getName(), message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //create chatlist node/child in firebase database
        final DatabaseReference chatReference1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(myUid).child(theirUid);
        chatReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatReference1.child("id").setValue(theirUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatReference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(theirUid).child(myUid);
        chatReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatReference2.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
        }else {
            myUid = user.getUid(); //currently signed in users uid
        }
    }

    private void checkOnlineStatus(String status) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        //update value of online status of user
        dbRef.updateChildren(hashMap);
    }

    private void checkTypingStatus(String typing) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        //update value of online status of user
        dbRef.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        //set online
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkUserStatus();

        //get timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());

        //set offline with timestamp
        checkOnlineStatus(timestamp);
        //set typing status off
        checkTypingStatus("noOne");
        refForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        //set online
        checkOnlineStatus("online");
        super.onResume();
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
