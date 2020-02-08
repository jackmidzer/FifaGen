package com.jack.fifagen.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jack.fifagen.Adapters.AdapterNotification;
import com.jack.fifagen.DashboardActivity;
import com.jack.fifagen.Models.ModelNotification;
import com.jack.fifagen.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelNotification> notificationsList;

    private AdapterNotification adapterNotification;

    public DashboardActivity dashboardActivity;

    //recyclerview
    RecyclerView recyclerView;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        //init recyclerview
        recyclerView = view.findViewById(R.id.notifications_recyclerViewId);

        firebaseAuth = FirebaseAuth.getInstance();

        getAllNotifications();

        return view;
    }

    private void getAllNotifications() {
        notificationsList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    //get data
                    ModelNotification modelNotification = ds.getValue(ModelNotification.class);

                    //add to list
                    notificationsList.add(modelNotification);
                }
                //adapter
                dashboardActivity = (DashboardActivity) getActivity();
                adapterNotification = new AdapterNotification(getActivity(), notificationsList, getFragmentManager(), dashboardActivity);
                recyclerView.setAdapter(adapterNotification);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
