package com.jack.fifagen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //22videos - 8 hours 32 minutes 05 seconds

    //views
    public TextView homeTeam;
    public TextView awayTeam;
    public TextView homeCountry;
    public TextView awayCountry;
    public TextView homeLeague;
    public TextView awayLeague;
    public TextView settings;
    public Button generate;
    public RatingBar homeStars;
    public RatingBar awayStars;
    public ImageView homeBadge;
    public ImageView awayBadge;
    public TextView homeDefence;
    public TextView homeMidfield;
    public TextView homeAttack;
    public TextView awayDefence;
    public TextView awayMidfield;
    public TextView awayAttack;
    public ImageView homeFlag;
    public ImageView awayFlag;
    public ImageView homeLogo;
    public ImageView awayLogo;
    public Button register;
    public Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Generate");

        //init
        homeTeam = findViewById(R.id.homeTeam);
        awayTeam = findViewById(R.id.awayTeam);
        homeCountry = findViewById(R.id.homeCountry);
        awayCountry = findViewById(R.id.awayCountry);
        homeLeague = findViewById(R.id.homeLeague);
        awayLeague = findViewById(R.id.awayLeague);
        settings = findViewById(R.id.settings);
        generate = findViewById(R.id.generate);
        homeStars = findViewById(R.id.homeStars);
        awayStars = findViewById(R.id.awayStars);
        homeBadge = findViewById(R.id.homeBadge);
        awayBadge = findViewById(R.id.awayBadge);
        homeDefence = findViewById(R.id.homeDefence);
        homeMidfield = findViewById(R.id.homeMidfield);
        homeAttack = findViewById(R.id.homeAttack);
        awayDefence = findViewById(R.id.awayDefence);
        awayMidfield = findViewById(R.id.awayMidfield);
        awayAttack = findViewById(R.id.awayAttack);
        homeFlag = findViewById(R.id.homeFlag);
        awayFlag = findViewById(R.id.awayFlag);
        homeLogo = findViewById(R.id.homeLogo);
        awayLogo = findViewById(R.id.awayLogo);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance((getApplicationContext()));
                databaseAccess.open();

                //Home team
                Team home_team = databaseAccess.getRandomTeam();
                if (home_team != null) {
                    Log.d("myTag5", "result is " + home_team);
                    homeTeam.setText(home_team.getName());
                    homeCountry.setText(home_team.getCountry());
                    homeLeague.setText(home_team.getLeague());
                    homeStars.setRating(home_team.getRating());
                    homeDefence.setText(String.valueOf(home_team.getDefence()));
                    homeMidfield.setText(String.valueOf(home_team.getMidfield()));
                    homeAttack.setText(String.valueOf(home_team.getAttack()));
                    String homeImage = "badge_" + home_team.getBadge();
                    int homeResID = getResources().getIdentifier(homeImage, "drawable", getPackageName());
                    homeBadge.setImageResource(homeResID);
                    homeImage = "flag_" + home_team.getFlag();
                    homeResID = getResources().getIdentifier(homeImage, "drawable", getPackageName());
                    homeFlag.setImageResource(homeResID);
                    homeImage = "logo_" + home_team.getLogo();
                    homeResID = getResources().getIdentifier(homeImage, "drawable", getPackageName());
                    homeLogo.setImageResource(homeResID);
                }

                //Away team
                Team away_team = databaseAccess.getRandomTeam();
                if (away_team != null) {
                    Log.d("myTag5", "result is " + away_team);
                    awayTeam.setText(away_team.getName());
                    awayCountry.setText(away_team.getCountry());
                    awayLeague.setText(away_team.getLeague());
                    awayStars.setRating(away_team.getRating());
                    awayDefence.setText(String.valueOf(away_team.getDefence()));
                    awayMidfield.setText(String.valueOf(away_team.getMidfield()));
                    awayAttack.setText(String.valueOf(away_team.getAttack()));
                    String awayImage = "badge_" + away_team.getBadge();
                    int awayResID = getResources().getIdentifier(awayImage, "drawable", getPackageName());
                    awayBadge.setImageResource(awayResID);
                    awayImage = "flag_" + away_team.getFlag();
                    awayResID = getResources().getIdentifier(awayImage, "drawable", getPackageName());
                    awayFlag.setImageResource(awayResID);
                    awayImage = "logo_" + away_team.getLogo();
                    awayResID = getResources().getIdentifier(awayImage, "drawable", getPackageName());
                    awayLogo.setImageResource(awayResID);
                }

                databaseAccess.close();
            }
        });

        //handle register button click
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //handle login button click
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
