package com.jack.fifagen;

public class ModelUser {

    private String name, email, phone, avatar, cover, uid, search;

    public ModelUser() {
    }

    public ModelUser(String name, String email, String phone, String avatar, String cover, String uid, String search) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.cover = cover;
        this.uid = uid;
        this.search = search;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
