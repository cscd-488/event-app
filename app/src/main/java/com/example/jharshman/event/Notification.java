package com.example.jharshman.event;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

/**
 * Created by Aaron on 2/4/2016.
 */
public class Notification {
    private static int color = Color.CYAN;
    private static int colorDuration = 1500;

    public static void create(Context context, int id, String name, String description, long ... vibrateTime){
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(name)
                .setContentText(description);

        mBuilder.setVibrate(vibrateTime);
        mBuilder.setLights(color, colorDuration, colorDuration);

        Intent notIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());
    }
}
