package com.gpsonguard.listener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gpsonguard.http.HttpPost;
import com.gpsonguard.screen.R;
import com.gpsonguard.util.SharedData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class ProximityIntentReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1000;

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {

        Boolean entering = false;

        //Toast.makeText(context,"You are approaching point of interest marker.",Toast.LENGTH_LONG).show();

        if (intent.getData() != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                entering = extras.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING);

                if (entering) {

                    Location loc1 = new Location("");
                    loc1.setLatitude(intent.getDoubleExtra("lat", -1L));
                    loc1.setLongitude(intent.getDoubleExtra("lon", -1L));

                    Location loc2 = new Location("");
                    loc2.setLatitude(SharedData.getCurrentLatitude());
                    loc2.setLongitude(SharedData.getCurrentLongitude());

                    float distanceInMeters = loc2.distanceTo(loc1);

                    //Toast.makeText(context,"Distance to " + intent.getStringExtra("alert") + " " + distanceInMeters + " meters" ,Toast.LENGTH_LONG).show();

                    if (distanceInMeters <= 2.0)
                        entering = true;
                    else
                        entering = false;

                }
            }
        }


        //String key = LocationManager.KEY_PROXIMITY_ENTERING;
        //Boolean entering = intent.getBooleanExtra(key, false);


        if (entering) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Log.d(getClass().getSimpleName(), "entering");
            //Toast.makeText(context,"You are approaching point of interest marker.",Toast.LENGTH_LONG).show();

            builder.setMessage("You are near POI : " + intent.getStringExtra("alert"));

            final AlertDialog alert = builder.create();

            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Add your code for the button here.
                }
            });
            alert.show();

            new Handler().postDelayed(new Runnable() {

                public void run() {
                    alert.dismiss();
                }
            }, 20000);


            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            //Define sound URI
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification notification = createNotification();
            //notification.setLatestEventInfo(context, "Proximity Alert!", "You are near your point of interest.", pendingIntent);
            NotificationCompat.Builder builderNoti = new NotificationCompat.Builder(context);
            notification = builderNoti.setContentIntent(pendingIntent)
                    .setTicker("Proximity Alert!")
                    .setSmallIcon(com.gpsonguard.screen.R.drawable.icon)
                    .setAutoCancel(true).setContentTitle("Proximity Alert!")
                    .setContentText("You are near your point of interest.")
                    .setSound(soundUri).build();

            notificationManager.notify(NOTIFICATION_ID, notification);

            JSONObject jsonObject = null;

            try {
                // guardId, siteId, date, time, lattitude, longitude
                jsonObject = new JSONObject();
                jsonObject.put("guardId", intent.getStringExtra("guardID"));
                jsonObject.put("latitude", "" + intent.getDoubleExtra("lat", -1L));
                jsonObject.put("longitude", "" + intent.getDoubleExtra("lon", -1L));
                jsonObject.put("trackingtype", "gps");
                jsonObject.put("poiname", "" + intent.getStringExtra("alert"));
                SimpleDateFormat dateFormat = dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                jsonObject.put("datetime", "" + dateFormat.format(System.currentTimeMillis()));
                // Utilities.showMessage("Date "
                // + dateFormat.formatLocal(System.currentTimeMillis()));
                jsonObject.put("siteId", "" + intent.getStringExtra("siteID"));

                // System.out.println("JSON Object : " + jsonObject);
                try {
                    HttpPost hp = new HttpPost();
                    //JSONObject jsonObjectRes  = hp.HttpPost(Constants.WEB_SERVER + Constants.LAT_LONG_INFO_URL, jsonObject, 1);
                } catch (Exception e) {
                    System.out.print("Error while getting JSON:" + e.getMessage());
                }

            } catch (JSONException jex) {
                jex.printStackTrace();
            }
        } else {
            Log.d(getClass().getSimpleName(), "exiting");
        }

    }


    private Notification createNotification() {
        Notification notification = new Notification();
        notification.icon = R.drawable.icon;
        notification.when = System.currentTimeMillis();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        //notification.ledARGB = Color.WHITE;
        //notification.ledOnMS = 1500;
        //notification.ledOffMS = 1500;
        return notification;
    }
}
