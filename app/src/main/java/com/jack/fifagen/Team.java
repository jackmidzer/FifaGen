package com.jack.fifagen;

public class Team {

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


    Team(String name, int badge, String league, int logo, String country, int flag, int defence, int midfield, int attack, int overall, Float rating) {
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

    String getName() {
        return this.name;
    }

    int getBadge() {
        return this.badge;
    }

    String getLeague() {
        return this.league;
    }

    int getLogo() {
        return this.logo;
    }

    String getCountry() {
        return this.country;
    }

    int getFlag() {
        return this.flag;
    }

    int getDefence() {
        return this.defence;
    }

    int getMidfield() {
        return this.midfield;
    }

    int getAttack() {
        return this.attack;
    }

    int getOverall() {
        return this.overall;
    }

    Float getRating() {
        return this.rating;
    }
}
