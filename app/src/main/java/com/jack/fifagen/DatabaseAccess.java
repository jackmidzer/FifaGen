package com.jack.fifagen;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    //return instance of database
    static DatabaseAccess getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    //open database
    void open() {
        this.db = openHelper.getWritableDatabase();
    }

    //close database
    void close() {
        if (db != null) {
            this.db.close();
        }
    }

    //query database
    Team getRandomTeam() {
        Team team = null;
        Cursor c = db.rawQuery("SELECT team_name, team_id, league, league_id, country, country_id, defence, midfield, attack, overall, rating FROM teams WHERE rating > 3.5 ORDER BY RANDOM() LIMIT 1", null);
        if (c.getCount() == 0) {
            Log.d("myTag", "NO DATA");
        }else {
            Log.d("myTag1", "Hallelujah!!");
            while (c.moveToNext()) {
                Log.d("myTag2", "RESULT IS " + c.getString(0));
                String name = c.getString(0);
                int badge = c.getInt(1);
                String league = c.getString(2);
                int logo = c.getInt(3);
                String country = c.getString(4);
                int flag = c.getInt(5);
                int def = c.getInt(6);
                int mid = c.getInt(7);
                int att = c.getInt(8);
                int overall = c.getInt(9);
                Float rating = c.getFloat(10);
                team = new Team(name, badge, league, logo, country, flag, def, mid, att, overall, rating);
            }
        }
        c.close();
        return team;
    }
}
