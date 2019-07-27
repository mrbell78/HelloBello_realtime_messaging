package com.mrbell.hellobello;

public class Frind_reqdata {

    private String Currentuser,otheruser;

    public Frind_reqdata() {
    }

    public Frind_reqdata(String currentuser, String otheruser) {
        Currentuser = currentuser;
        this.otheruser = otheruser;
    }

    public String getCurrentuser() {
        return Currentuser;
    }

    public void setCurrentuser(String currentuser) {
        Currentuser = currentuser;
    }

    public String getOtheruser() {
        return otheruser;
    }

    public void setOtheruser(String otheruser) {
        this.otheruser = otheruser;
    }
}
