package com.example.placeremindermap;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // In this method, the class receives the broadcast intent when a geofencing event occurs.

        // Create an instance of NotificationHelper for managing notifications.
        NotificationHelper notificationHelper = new NotificationHelper(context);

        // Extract the geofencing event from the received intent.
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Extract the list of geofences involved in the event.
        assert geofencingEvent != null;
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        geofencingEvent.getTriggeringLocation(); // This is a Location object representing the location associated with the event.

        // Iterate through the geofences involved in the event.
        assert geofenceList != null;
        for (Geofence geofence : geofenceList) {
            int transitionType = geofencingEvent.getGeofenceTransition();

            // Check the type of transition and send corresponding notifications.
            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    if (controlNotification(context, "You've entered the placeholder " + geofence.getRequestId())) {
                        notificationHelper.sendHighPriorityNotification("Enter", "GEOFENCE TRANSITION ENTER", "You've entered the placeholder " + geofence.getRequestId(), MainActivity.class);
                    }
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    if (controlNotification(context, "You've exited the placeholder " + geofence.getRequestId())) {
                        notificationHelper.sendHighPriorityNotification("Exit", "GEOFENCE TRANSITION EXIT", "You've exited the placeholder " + geofence.getRequestId(), MainActivity.class);
                    }
                    break;
            }
        }
    }

    public boolean controlNotification(Context context, String geofenceString) {
        // This method checks if an identical notification is already active in the system.

        // Obtain the NotificationManager service.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Get a list of active notifications in the system.
        StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();

        // Iterate through the active notifications.
        for (StatusBarNotification notification : activeNotifications) {
            String packageName = notification.getPackageName();

            // Check if the notification belongs to your application.
            if (packageName.equals(context.getPackageName())) {

                // Extract the Notification object from the active notification.
                Notification notificationObject = notification.getNotification();

                // Now you can access the content of the notification.
                CharSequence notificationText = notificationObject.extras.getCharSequence(Notification.EXTRA_TEXT);

                if (notificationText != null) {
                    String body = notificationText.toString();

                    // Compare the notification's content with the geofence message.
                    if (geofenceString.equals(body)) {
                        // Return false if an identical notification is already active.
                        return false;
                    }
                }
            }
        }
        // Return true if no similar active notifications were found.
        return true;
    }
}
