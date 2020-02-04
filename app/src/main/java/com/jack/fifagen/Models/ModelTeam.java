package com.jack.fifagen.Models;

public class ModelTeam {

    private final String name;
    private final int badge;
    private final String league;
    private final int logo;
    private final String country;
    private final int flag;
    private final int defence;
    private final int midfield;
    private final int attack;
    private final int overall;
    private final Float rating;


    public ModelTeam(String name, int badge, String league, int logo, String country, int flag, int defence, int midfield, int attack, int overall, Float rating) {
        this.name = name;
        this.badge = badge;
        this.league = league;
        this.logo = logo;
        this.country = country;
        this.flag = flag;
        this.defence = defence;
        this.midfield = midfield;
        this.attack = attack;
        this.overall = overall;
        this.rating = rating;
    }

    public String getName() {
        return this.name;
    }

    public int getBadge() {
        return this.badge;
    }

    public String getLeague() {
        return this.league;
    }

    public int getLogo() {
        return this.logo;
    }

    public String getCountry() {
        return this.country;
    }

    public int getFlag() {
        return this.flag;
    }

    public int getDefence() {
        return this.defence;
    }

    public int getMidfield() {
        return this.midfield;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getOverall() {
        return this.overall;
    }

    public Float getRating() {
        return this.rating;
    }
}
