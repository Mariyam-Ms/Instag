package com.project1.instagramlite.Model;

import com.google.type.Date;

public class Data extends PostId{
    String user,image,about;
    Date   time;

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
