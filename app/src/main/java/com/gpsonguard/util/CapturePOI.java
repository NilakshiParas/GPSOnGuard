package com.gpsonguard.util;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;

import com.gpsonguard.http.HttpPost;
import com.gpsonguard.screen.ADashboardActivity;
import com.gpsonguard.screen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class CapturePOI {

    private static final int NOTIFICATION_ID = 1000;

    public static void alertandSendPOI(Context context, String poiName, String guardID, String siteID, double latitude, double longitude, String trackingstatus) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //Log.d(getClass().getSimpleName(), "entering");
        //Toast.makeText(context,"You are approaching point of interest marker.",Toast.LENGTH_LONG).show();

        builder.setMessage("You are near POI : " + poiName);

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

        Intent notificationIntent = new Intent(context, ADashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification notification = createNotification();
        //notification.setLatestEventInfo(context, "Proximity Alert!", "You are near your point of interest.", pendingIntent);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builderNoti = new NotificationCompat.Builder(context);
        notification = builderNoti.setContentIntent(pendingIntent)
                .setTicker("Proximity Alert!")
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(true).setContentTitle("Proximity Alert!")
                .setContentText("You are near your point of interest.")
                .setSound(soundUri).build();

        notificationManager.notify(NOTIFICATION_ID, notification);

        JSONObject jsonObject = null;

        try {

            // guardId, siteId, date, time, lattitude, longitude
            jsonObject = new JSONObject();
            jsonObject.put("guardId", "" + guardID + "");
            jsonObject.put("latitude", "" + latitude + "");
            jsonObject.put("longitude", "" + longitude + "");
            jsonObject.put("trackingtype", "gps");
            jsonObject.put("poiname", "" + poiName + "");
            SimpleDateFormat dateFormat = dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            jsonObject.put("trackingstatus", "" + trackingstatus + "");
            jsonObject.put("datetime", "" + dateFormat.format(System.currentTimeMillis()));

            //jsonObject.put("trackingstatus", "" + trackingstatus + "");
            // Utilities.showMessage("Date "
            // + dateFormat.formatLocal(System.currentTimeMillis()));
            jsonObject.put("siteId", "" + siteID + "");

            System.out.println("JSON Object : " + jsonObject);
            try {
                HttpPost hp = new HttpPost();
                JSONObject jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.LAT_LONG_INFO_URL, jsonObject, 1);
            } catch (Exception e) {
                System.out.print("Error while getting JSON:" + e.getMessage());
            }

        } catch (JSONException jex) {
            jex.printStackTrace();
        }

    }

    private static Notification createNotification() {
        Notification notification = new Notification();
        //notification.icon = R.drawable.ic_media_next;
        //notification.when = System.currentTimeMillis();
        //notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        //notification.ledARGB = Color.WHITE;
        //notification.ledOnMS = 1500;
        //notification.ledOffMS = 1500;

        return notification;
    }

    public static void ringtone(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
