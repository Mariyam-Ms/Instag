package com.project1.instagramlite.Model;


import java.util.Date;

public class Data extends PostId{
    String user,image,about;
    Date time;
    public Data (){

    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public String getImage() {
        return image;
    }

    public String getAbout() {
        return about;
    }

    public Date getTime() {
        return  time;
    }
}
