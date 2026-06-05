package com.example.gymtrackmovil.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.gymtrackmovil.LoginActivity;
import com.example.gymtrackmovil.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "gymtrack_channel";
    private static final String CHANNEL_NAME = "GymTrack Notificaciones";

    /**
     * Called when a new FCM message is received (app in foreground or background).
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "GymTrack";
        String body = "Tienes una nueva notificación";

        // Use data payload if present (higher priority)
        if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().containsKey("title")
                    ? remoteMessage.getData().get("title")
                    : title;
            body = remoteMessage.getData().containsKey("body")
                    ? remoteMessage.getData().get("body")
                    : body;
        }

        // Fall back to notification payload
        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null)
                title = remoteMessage.getNotification().getTitle();
            if (remoteMessage.getNotification().getBody() != null)
                body = remoteMessage.getNotification().getBody();
        }

        sendNotification(title, body);
    }

    /**
     * Called when a new FCM registration token is generated.
     * Send this to your server to store it for targeted notifications.
     */
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // TODO: Send this token to your backend for user-targeted notifications.
        // ApiClient.getClient(this).create(ApiService.class).updateFcmToken(token).enqueue(...)
    }

    /**
     * Build and display a local push notification.
     */
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal principal de notificaciones de GymTrack");
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
