
package com.dk.kingpin;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


import com.dk.kingpin.constant.Constants;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        localNotificationSetup(this);
    }

    private static void localNotificationSetup(Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.SERVICE_NOTIFICATION_CHANNEL, "Service Notification", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(application.getString(R.string.notification_service_description));
            channel.enableLights(false);
            channel.setShowBadge(true);
            channel.enableVibration(false);
            NotificationManager manager = application.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.DEFAULT_NOTIFICATION_CHANNEL, "Default Notification", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(application.getString(R.string.notification_default_description));
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.enableVibration(false);
            NotificationManager manager = application.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

}
