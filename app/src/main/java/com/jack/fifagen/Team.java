package com.jack.fifagen;

public class Team {

    private final String name;
    private final Integer badge;
    private final String league;
    private final Integer logo;
    private final String country;
    private final Integer flag;
    private final Integer defence;
    private final Integer midfield;
    private final Integer attack;
    private final Integer overall;
    private final Float rating;


    public Team(String name, Integer badge, String league,Integer logo, String country, Integer flag, Integer defence, Integer midfield, Integer attack, Integer overall, Float rating) {
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

    public Integer getBadge() {
        return this.badge;
    }

    public String getLeague() {
        return this.league;
    }

    public Integer getLogo() {
        return this.logo;
    }

    public String getCountry() {
        return this.country;
    }

    public Integer getFlag() {
        return this.flag;
    }

    public Integer getDefence() {
        return this.defence;
    }

    public Integer getMidfield() {
        return this.midfield;
    }

    public Integer getAttack() {
        return this.attack;
    }

    public Integer getOverall() {
        return this.overall;
    }

    public Float getRating() {
        return this.rating;
    }
}
