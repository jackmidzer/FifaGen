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

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    String getPhone() {
        return phone;
    }

    void setPhone(String phone) {
        this.phone = phone;
    }

    String getAvatar() {
        return avatar;
    }

    void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    String getCover() {
        return cover;
    }

    void setCover(String cover) {
        this.cover = cover;
    }

    String getUid() {
        return uid;
    }

    void setUid(String uid) {
        this.uid = uid;
    }

    String getSearch() {
        return search;
    }

    void setSearch(String search) {
        this.search = search;
    }
}
