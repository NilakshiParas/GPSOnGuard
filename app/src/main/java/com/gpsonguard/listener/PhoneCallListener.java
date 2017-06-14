package com.gpsonguard.listener;


import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

//monitor phone call states
 public class PhoneCallListener extends PhoneStateListener {

     String TAG = "LOGGING PHONE CALL";
     private boolean phoneCalling = false;

     @Override
     public void onCallStateChanged(int state, String incomingNumber) {
         if (TelephonyManager.CALL_STATE_RINGING == state) {
             // phone ringing
             Log.i(TAG, "RINGING, number: " + incomingNumber);
         }

         if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
             // active
             Log.i(TAG, "OFFHOOK");
             phoneCalling = true;
         }

         // When the call ends launch the main activity again

         if (TelephonyManager.CALL_STATE_IDLE == state) {
             Log.i(TAG, "IDLE");
             if (phoneCalling) {
                 Log.i(TAG, "restart app");

                 // restart app
                 //Intent i = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                 //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 
                 //startActivity(i);
                 phoneCalling = false;
             }

         }

     }
 }

