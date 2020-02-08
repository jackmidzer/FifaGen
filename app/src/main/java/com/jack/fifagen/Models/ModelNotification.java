package com.jack.fifagen.Models;

public class ModelNotification {

    String matchId, timestamp, theirUid, message, senderUid, senderName, senderEmail, senderImage;

    public ModelNotification() {
    }

    public ModelNotification(String matchId, String timestamp, String theirUid, String message, String senderUid, String senderName, String senderEmail, String senderImage) {
        this.matchId = matchId;
        this.timestamp = timestamp;
        this.theirUid = theirUid;
        this.message = message;
        this.senderUid = senderUid;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.senderImage = senderImage;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String gettheirUid() {
        return theirUid;
    }

    public void settheirUid(String theirUid) {
        this.theirUid = theirUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }
}
