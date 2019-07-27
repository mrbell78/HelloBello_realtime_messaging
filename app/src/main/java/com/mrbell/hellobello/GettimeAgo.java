package com.mrbell.hellobello;

import android.app.Application;
import android.content.Context;

public class GettimeAgo extends Application {

    public static final int SECONDS_MILLIS=1000;
    public static final int MINUTE_MILLIS=60*SECONDS_MILLIS;
    public static final int HOURS_MILLIS=60*MINUTE_MILLIS;
    public static final int DAY_MILLIS=24*HOURS_MILLIS;

    public  static String getTimeago(long time , Context context){
        if(time<1000000000000L){
            time*=100;
        }
        long now = System.currentTimeMillis();
        if(time>now || time<=0){
            return null;
        }

        final long diff = now - time;
        if(diff<=MINUTE_MILLIS){
            return "just a moment ago";
        }else if(diff<=2*MINUTE_MILLIS)
            return "a minute ago";
        else if(diff<=50*MINUTE_MILLIS)
            return diff/MINUTE_MILLIS +"minutes ago";
        else if(diff<=70*MINUTE_MILLIS)
            return "an hour ago";
        else if(diff<=24*HOURS_MILLIS)
            return diff/HOURS_MILLIS+"hour ago";
        else if(diff<=48 * HOURS_MILLIS)
            return "Yesterday";
        else {
            return diff/DAY_MILLIS+"days ago";
        }
    }
}
