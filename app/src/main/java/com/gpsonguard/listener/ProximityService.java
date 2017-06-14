package com.gpsonguard.listener;

import android.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class ProximityService extends IntentService {

    String proximitysd = "com.gpsonguard.listener.ProximityService";

    private Location currentLocation;
    double currentLatitude, currentLongitude;
    private TextView lat;

    int n = 0;
    private BroadcastReceiver mybroadcast;
    private LocationManager locationManager;
    MyLocationListener locationListenerp;
    LocationListener mlocListener;

    public ProximityService() {
        super("Proximity Service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        mybroadcast = new ProximityIntentReceiver();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        FindLocation();

        double lat;
        double lng;
        float radius = 5;
        long expiration = -1;
        //MyDBAdapter db = new MyDBAdapter(this);
        //   Cursor cursor;
        //   db.read();
        //   cursor = db.getAllEntries();
        //   boolean go = cursor.moveToFirst();
        //   while(cursor.isAfterLast() != true){
        //lat = cursor.getInt(MyDBAdapter.LATITUDE_COLUMN)/1E6;
        //lng = cursor.getInt(MyDBAdapter.LONGITUDE_COLUMN)/1E6;
        //String what = cursor.getString(MyDBAdapter.ICON_COLUMN);
        //String how = cursor.getString(MyDBAdapter.FISH_COLUMN);

        String what = "Back Door";
        String how = "Walking";

        lat = 47.8053107310275;
        lng = -122.180154025555;

        String proximitys = "com.gpsonguard.listener.ProximityService" + n;
        IntentFilter filter = new IntentFilter(proximitys);
        registerReceiver(mybroadcast, filter);

        Intent intent = new Intent(proximitys);

        intent.putExtra("alert", what);
        intent.putExtra("type", how);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, n, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //  locationManager.addProximityAlert(lat, lng, radius, expiration, proximityIntent);

        //sendBroadcast(new Intent(intent));

        n++;
        //  cursor.moveToNext();
        //}
        // db.close();
        // cursor.close();
    }

    @Override
    public void onDestroy() {

        try {
            /*Change Here
            * */
            locationManager.removeUpdates(mlocListener);
            mlocListener = null;

            Toast.makeText(this, "Proximity Service Stopped", Toast.LENGTH_LONG).show();
            unregisterReceiver(mybroadcast);


        } catch (Exception e) {
            Log.d("reciever", e.toString());
        }
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Proximity Service Started", Toast.LENGTH_LONG).show();
        //IntentFilter filter = new IntentFilter(proximitys);
        //registerReceiver(mybroadcast,filter);
    }

    public class ProximityIntentReceiver extends BroadcastReceiver {
        private static final int NOTIFICATION_ID = 1000;

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String key = LocationManager.KEY_PROXIMITY_ENTERING;

            Boolean entering = arg1.getBooleanExtra(key, false);
            String here = arg1.getExtras().getString("alert");
            String happy = arg1.getExtras().getString("type");

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(arg0, 0, arg1, 0);
            Toast.makeText(getApplicationContext(), "You are approaching " + here + " marker.", Toast.LENGTH_LONG).show();
            Notification notification = createNotification();
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builderNoti = new NotificationCompat.Builder(getApplicationContext());
            notification = builderNoti.setContentIntent(pendingIntent)
                    .setTicker("Entering Proximity!")
                    .setSmallIcon(com.gpsonguard.screen.R.drawable.icon)
                    .setAutoCancel(true).setContentTitle("Entering Proximity!")
                    .setContentText("You are approaching a " + here + " marker.").setSound(soundUri).build();

            notificationManager.notify(NOTIFICATION_ID, notification);
        }


    }

    private Notification createNotification() {
        Notification notification = new Notification();

        notification.icon = R.drawable.ic_media_next;
        notification.when = System.currentTimeMillis();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        notification.ledARGB = Color.WHITE;
        notification.ledOnMS = 1500;
        notification.ledOffMS = 1500;


        return notification;
    }
    //make actions
    public void FindLocation() {
        System.out.println("Gone Here");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mlocListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                updateLocation(location);

                Toast.makeText(
                        getApplicationContext(),
                        String.valueOf(currentLatitude) + "\n"
                                + String.valueOf(currentLongitude), Toast.LENGTH_SHORT)
                        .show();

                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        try {
            // 100 meter and 1000 millisecond
            /*Change Here
            * */
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, mlocListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public class MyLocationListener implements LocationListener {
        //public void onLocationChanged(Location location) {
        //    Toast.makeText(getApplicationContext(), "I was here", Toast.LENGTH_LONG).show();
        //}

        public void onLocationChanged(Location location) {
            updateLocation(location);

            Toast.makeText(
                    getApplicationContext(),
                    String.valueOf(currentLatitude) + "\n"
                            + String.valueOf(currentLongitude), Toast.LENGTH_LONG)
                    .show();

            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

        }

        public void onProviderDisabled(String s) {
        }

        public void onProviderEnabled(String s) {
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }
    }


    void updateLocation(Location location) {
        currentLocation = location;
        currentLatitude = currentLocation.getLatitude();
        currentLongitude = currentLocation.getLongitude();
        System.out.println("Latitude:" + String.valueOf(currentLatitude) + "\n"
                + String.valueOf(currentLongitude));
        //lat.setText("Latitude:-" + String.valueOf(currentLatitude)
        //        + " Longitude:-" + String.valueOf(currentLongitude));


    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        // TODO Auto-generated method stub

    }

}