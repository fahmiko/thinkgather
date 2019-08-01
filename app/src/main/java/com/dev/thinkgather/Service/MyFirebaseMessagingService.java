package com.dev.thinkgather.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.dev.thinkgather.Activity.DetailPost;
import com.dev.thinkgather.Activity.Home;
import com.dev.thinkgather.Activity.Register;
import com.dev.thinkgather.Activity.TambahPublikasi;
import com.dev.thinkgather.Fragment.HomeFragment;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification().getTitle().equals("Register")){
            Register.register.finish();
        }else{
            try {
                TambahPublikasi.tambahPublikasi.progress.dismiss();
                TambahPublikasi.tambahPublikasi.finish();
                (HomeFragment.homeFragment).loadData();
                (DetailPost.detailPost).loadData();
            }catch (Exception e){

            }
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.dev.thinkgather";
        Intent resultIntent = new Intent(getApplicationContext() , Home.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Thinkgather Channel");
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.um)
                .setContentIntent(resultPendingIntent);
        notificationManager.notify((int)System.currentTimeMillis(),builder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Session session = Application.getSession();
        session.saveDeviceToken(s);
    }
}
