package com.jack.fifagen.Models;

public class ModelMatch {

    private String homeTeam, awayTeam, homeBadge, awayBadge, homePlayer, awayPlayer, homeScore, awayScore, isApproved, timestamp, winner, homeUid, awayUid;

    public ModelMatch() {
    }

    public ModelMatch(String homeTeam, String awayTeam, String homeBadge, String awayBadge, String homePlayer, String awayPlayer, String homeScore, String awayScore, String isApproved, String timestamp, String winner, String homeUid, String awayUid) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeBadge = homeBadge;
        this.awayBadge = awayBadge;
        this.homePlayer = homePlayer;
        this.awayPlayer = awayPlayer;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.isApproved = isApproved;
        this.timestamp = timestamp;
        this.winner = winner;
        this.homeUid = homeUid;
        this.awayUid = awayUid;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getHomeBadge() {
        return homeBadge;
    }

    public void setHomeBadge(String homeBadge) {
        this.homeBadge = homeBadge;
    }

    public String getAwayBadge() {
        return awayBadge;
    }

    public void setAwayBadge(String awayBadge) {
        this.awayBadge = awayBadge;
    }

    public String getHomePlayer() {
        return homePlayer;
    }

    public void setHomePlayer(String homePlayer) {
        this.homePlayer = homePlayer;
    }

    public String getAwayPlayer() {
        return awayPlayer;
    }

    public void setAwayPlayer(String awayPlayer) {
        this.awayPlayer = awayPlayer;
    }

    public String getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(String homeScore) {
        this.homeScore = homeScore;
    }

    public String getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(String awayScore) {
        this.awayScore = awayScore;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getHomeUid() {
        return homeUid;
    }

    public void setHomeUid(String homeUid) {
        this.homeUid = homeUid;
    }

    public String getAwayUid() {
        return awayUid;
    }

    public void setAwayUid(String awayUid) {
        this.awayUid = awayUid;
    }
}
