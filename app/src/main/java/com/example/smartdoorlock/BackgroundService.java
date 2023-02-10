package com.example.smartdoorlock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BackgroundService extends Service {
    int count=0;
    DatabaseReference databaseReference;
    private static final String CHANNEL_ID = "Doorbell";
    private static final int NOTIFICATION_ID = 100;
    private static final int REQ_CODE = 100;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        NotificationManager fm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification foreground;

        Intent iNotify = new Intent(getApplicationContext(),MainActivity.class);
        //iNotify.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        iNotify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, REQ_CODE, iNotify, PendingIntent.FLAG_UPDATE_CURRENT);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.logo)
                    .setContentText("Someone is at your door. Tap to View")
                    .setContentIntent(pi)
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true)
                    .build();
            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "New Channel", NotificationManager.IMPORTANCE_HIGH));

            foreground = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.logo)
                    .setContentText("The App is Running")
                    .setContentIntent(pi)
                    .setChannelId("Foreground")
                    .build();
            fm.createNotificationChannel(new NotificationChannel("Foreground", "Service", NotificationManager.IMPORTANCE_NONE));

        }else {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.logo)
                    .setContentText("Someone is at your door. Tap to View")
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            foreground = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.logo)
                    .setContentText("The App is Running")
                    .setContentIntent(pi)
                    .build();
        }

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Smart Bell");
        DatabaseReference getNotificationStatus = databaseReference.child("Notify");

        getNotificationStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(count!=0) {
                    nm.notify(NOTIFICATION_ID, notification);
                }else
                    count++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        startForeground(1, foreground);




        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }
}
