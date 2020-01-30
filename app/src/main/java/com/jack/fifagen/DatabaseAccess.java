package com.jack.fifagen;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c = null;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    //return instance of database
    public static DatabaseAccess getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    //open database
    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    //close database
    public void close() {
        if (db != null) {
            this.db.close();
        }
    }

    //query database
    public Team getRandomTeam() {
        Team team = new Team(null, null, null, null, null, null, null, null, null, null, null);
        c = db.rawQuery("SELECT team_name, team_id, league, league_id, country, country_id, defence, midfield, attack, overall, rating FROM teams WHERE rating > 3.5 ORDER BY RANDOM() LIMIT 1", null);
        if (c.getCount() == 0) {
            Log.d("myTag", "NO DATA");
        }else {Log.d("myTag1", "Hallelujah!!");}
        while (c.moveToNext()) {
            Log.d("myTag2", "RESULT IS " + c.getString(0));
            String name = c.getString(0);
            Integer badge = c.getInt(1);
            String league = c.getString(2);
            Integer logo = c.getInt(3);
            String country = c.getString(4);
            Integer flag = c.getInt(5);
            Integer def = c.getInt(6);
            Integer mid = c.getInt(7);
            Integer att = c.getInt(8);
            Integer overall = c.getInt(9);
            Float rating = c.getFloat(10);
            team = new Team(name, badge, league, logo, country, flag, def, mid, att, overall, rating);
        }
        c.close();
        return team;
    }
}
