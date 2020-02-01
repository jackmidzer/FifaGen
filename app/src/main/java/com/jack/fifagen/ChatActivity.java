package com.jack.fifagen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    //views
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView avatarIv;
    TextView nameTv, statusTv;
    EditText inputEt;
    ImageButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //init views
        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_recyclerViewId);
        avatarIv = findViewById(R.id.avatarId);
        nameTv = findViewById(R.id.nameId);
        statusTv = findViewById(R.id.statusId);
        inputEt = findViewById(R.id.inputId);
        sendBtn = findViewById(R.id.sendId);
    }
}
