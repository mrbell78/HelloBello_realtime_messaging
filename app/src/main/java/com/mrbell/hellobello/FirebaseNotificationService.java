package com.mrbell.hellobello;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseNotificationService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

       String notificationtitle= remoteMessage.getNotification().getTitle();
       String message= remoteMessage.getNotification().getBody();
       String ClickAction = remoteMessage.getNotification().getClickAction();
        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationtitle)
                .setContentText(message);

        Intent resultintent = new Intent(ClickAction);

        PendingIntent resultpendingintent = PendingIntent.getActivity(this,0,resultintent,PendingIntent.FLAG_UPDATE_CURRENT);

        mbuilder.setContentIntent(resultpendingintent);


        int mNotificationid = (int) System.currentTimeMillis();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(mNotificationid,mbuilder.build());


    }

}
