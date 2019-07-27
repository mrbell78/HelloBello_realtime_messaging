package com.mrbell.hellobello;

public class Modeluserclass {

    String name;
    String image;
    String status;
    String thumb_img;
    Boolean onlinestate;



    public Modeluserclass() {
    }

    public Modeluserclass(String name, String image, String status,String thumb_img,Boolean onlinestate) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumb_img=thumb_img;
        this.onlinestate=onlinestate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumimg() {
        return thumb_img;
    }

    public void setThumimg(String thumimg) {
        this.thumb_img = thumimg;
    }

    public Boolean getOnlinestate() {
        return onlinestate;
    }

    public void setOnlinestate(Boolean onlinestate) {
        this.onlinestate = onlinestate;
    }
}
