package com.jack.fifagen.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jack.fifagen.DatabaseAccess;
import com.jack.fifagen.Activities.MainActivity;
import com.jack.fifagen.Models.ModelTeam;
import com.jack.fifagen.Activities.OpponentsActivity;
import com.jack.fifagen.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    //init progress dialog
    private ProgressDialog progressDialog;

    //views
    private TextView homeTeam, homeCountry, homeLeague, homeDefence, homeMidfield, homeAttack;
    private TextView awayTeam, awayCountry, awayLeague, awayDefence, awayMidfield, awayAttack;
    private ImageView homeBadge, homeFlag, homeLogo;
    private ImageView awayBadge, awayFlag, awayLogo;
    private RatingBar homeStars, awayStars;
    private Button generate, save;
    private TextView settings;

    //teams
    private ModelTeam home_team;
    private ModelTeam away_team;

    boolean isGenerated = false;

    String myUid;
    String theirUid;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        myUid = firebaseAuth.getCurrentUser().getUid();

        //init progress dialog
        progressDialog = new ProgressDialog(getActivity());

        //init
        homeTeam = view.findViewById(R.id.homeTeam);
        awayTeam = view.findViewById(R.id.awayTeam);
        homeCountry = view.findViewById(R.id.homeCountry);
        awayCountry = view.findViewById(R.id.awayCountry);
        homeLeague = view.findViewById(R.id.homeLeague);
        awayLeague = view.findViewById(R.id.awayLeague);
        settings = view.findViewById(R.id.settings);
        generate = view.findViewById(R.id.generate);
        save = view.findViewById(R.id.save);
        homeStars = view.findViewById(R.id.homeStars);
        awayStars = view.findViewById(R.id.awayStars);
        homeBadge = view.findViewById(R.id.homeBadge);
        awayBadge = view.findViewById(R.id.awayBadge);
        homeDefence = view.findViewById(R.id.homeDefence);
        homeMidfield = view.findViewById(R.id.homeMidfield);
        homeAttack = view.findViewById(R.id.homeAttack);
        awayDefence = view.findViewById(R.id.awayDefence);
        awayMidfield = view.findViewById(R.id.awayMidfield);
        awayAttack = view.findViewById(R.id.awayAttack);
        homeFlag = view.findViewById(R.id.homeFlag);
        awayFlag = view.findViewById(R.id.awayFlag);
        homeLogo = view.findViewById(R.id.homeLogo);
        awayLogo = view.findViewById(R.id.awayLogo);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance((getActivity().getApplicationContext()));
                databaseAccess.open();

                //Home team
                home_team = databaseAccess.getRandomTeam();
                if (home_team != null) {
                    homeTeam.setText(home_team.getName());
                    homeCountry.setText(home_team.getCountry());
                    homeLeague.setText(home_team.getLeague());
                    homeStars.setRating(home_team.getRating());
                    homeDefence.setText(String.valueOf(home_team.getDefence()));
                    homeMidfield.setText(String.valueOf(home_team.getMidfield()));
                    homeAttack.setText(String.valueOf(home_team.getAttack()));
                    String homeImage = "badge_" + home_team.getBadge();
                    int homeResID = getResources().getIdentifier(homeImage, "drawable", getActivity().getPackageName());
                    homeBadge.setImageResource(homeResID);
                    homeImage = "flag_" + home_team.getFlag();
                    homeResID = getResources().getIdentifier(homeImage, "drawable", getActivity().getPackageName());
                    homeFlag.setImageResource(homeResID);
                    homeImage = "logo_" + home_team.getLogo();
                    homeResID = getResources().getIdentifier(homeImage, "drawable", getActivity().getPackageName());
                    homeLogo.setImageResource(homeResID);
                }

                //Away team
                away_team = databaseAccess.getRandomTeam();
                while (away_team == home_team) {
                    away_team = databaseAccess.getRandomTeam();
                }
                if (away_team != null) {
                    awayTeam.setText(away_team.getName());
                    awayCountry.setText(away_team.getCountry());
                    awayLeague.setText(away_team.getLeague());
                    awayStars.setRating(away_team.getRating());
                    awayDefence.setText(String.valueOf(away_team.getDefence()));
                    awayMidfield.setText(String.valueOf(away_team.getMidfield()));
                    awayAttack.setText(String.valueOf(away_team.getAttack()));
                    String awayImage = "badge_" + away_team.getBadge();
                    int awayResID = getResources().getIdentifier(awayImage, "drawable", getActivity().getPackageName());
                    awayBadge.setImageResource(awayResID);
                    awayImage = "flag_" + away_team.getFlag();
                    awayResID = getResources().getIdentifier(awayImage, "drawable", getActivity().getPackageName());
                    awayFlag.setImageResource(awayResID);
                    awayImage = "logo_" + away_team.getLogo();
                    awayResID = getResources().getIdentifier(awayImage, "drawable", getActivity().getPackageName());
                    awayLogo.setImageResource(awayResID);
                }

                isGenerated = true;
                databaseAccess.close();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGenerated && homeTeam != null && awayTeam != null) {
                    String homeImage = "badge_" + home_team.getBadge();
                    String awayImage = "badge_" + away_team.getBadge();
                    Intent intent = new Intent(getActivity(), OpponentsActivity.class);
                    intent.putExtra("homeTeam", home_team.getName());
                    intent.putExtra("awayTeam", away_team.getName());
                    intent.putExtra("homeBadge", homeImage);
                    intent.putExtra("awayBadge", awayImage);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "Generate a match first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            //user not signed in, go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // to show menu options in fragment
        super.onCreate(savedInstanceState);
    }

    //inflate options menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
