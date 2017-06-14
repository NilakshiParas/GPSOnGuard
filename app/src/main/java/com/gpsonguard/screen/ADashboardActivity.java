package com.gpsonguard.screen;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gpsonguard.http.HttpPost;
import com.gpsonguard.listener.PhoneCallListener;
import com.gpsonguard.listener.ProximityIntentReceiver;
import com.gpsonguard.listener.ProximityService;
import com.gpsonguard.util.CameraPreview;
import com.gpsonguard.util.CapturePOI;
import com.gpsonguard.util.Constants;
import com.gpsonguard.util.KeyValue;
import com.gpsonguard.util.LoginData;
import com.gpsonguard.util.SharedData;
import com.gpsonguard.util.SitePOI;
import com.gpsonguard.util.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class ADashboardActivity extends AppCompatActivity {
    public static final String TAG = "ADashboardActivity";
    ImageButton btnLogin;
    EditText inputGuardID;
    private String loginDateTime;
    private String lastMessageTime;

    TextView tv1;

    private ImageButton btnLogout;
    private ImageButton sendReport;
    private ImageButton callSupervisor;
    private ImageButton scanQRCode;
    private ImageButton standingOrder;
    private ImageButton sms;
    private ImageButton e911;
    private ImageButton poi;
    private Location currentLocation;
    double currentLatitude, currentLongitude;
    private TextView lat;
    private TextView sitevID;
    TextView nameshift;
    TextView shiftB;
    TextView shiftE;
    Context mContext = ADashboardActivity.this;
    private PopupWindow pwindo;
    Button btnClosePopup;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    static int TAKE_PICTURE = 1;
    ImageView ivThumbnailPhoto;
    Bitmap bitMap;
    LocationManager mlocManager = null;
    LocationListener mlocListener;
    Intent startMyService;
    ProximityIntentReceiver proxmityRec;
    private static String guardName;
    private static String startTime;
    private static String endTime;
    private static String companyID;
    private static String siteID;
    private static String patrolType;
    private static String phoneNumber;
    private static String guardID = null;
    private final static Map<String, String> emergency_contacts = new HashMap<String, String>();
    private static final String PROX_ALERT_INTENT = "com.gpsonguard.ProximityAlert";
    private long POINT_RADIUS = 1; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1; // It will never
    // expire
    private static Vector standingOrderVector = null;
    public static final int SCANNER_RESULT = 111;
    public static JSONArray standingOrderArray = null;
    private Handler customHandler = new Handler();
    private Handler customHandler2 = new Handler();
    private Handler customHandler3 = new Handler();
    SlidingDrawer slidingDrawer;
    private android.hardware.Camera mCamera;
    private CameraPreview mCameraPreview;
    private static String supervisor = "N";
    private TextView batteryPercent;
    private Vector poiVector = new Vector();
    private Vector primaryPOIVector = new Vector();
    private Map timeForPOIs = new HashMap();
    private int batteryHealth;
    public static List siteStatus;
    private NotificationManager mNM;

    public final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;

    ImageView imgExit;

    /*Create POI Dialog @EditText as Public*/
    EditText poilat;
    EditText poilon;
    EditText poiName;
    EditText poiDesc;
    EditText radius;
    EditText poiAddr;
    TextView poiwarning;
    Button savePoi;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = Utilities.getSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        // mContext = this;

        if (guardID == null) {
            if (Build.VERSION.SDK_INT >= 23) {
                insertDummyContactWrapper();
            } else {
                initializeLogin();
            }

        } else {
            initializeDashboard();
        }

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public void initializeLogin() {
        setContentView(R.layout.login);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        /*********addListenerOnloginEditText************/
        addListenerOnloginEditText();

        inputGuardID = (EditText) findViewById(R.id.guardID);
        btnLogin = (ImageButton) findViewById(R.id.button_login);

        /***********addListenerOnLoginButton*************/
        addListenerOnLoginButton();
    }

    public void initializeDashboard() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            insertDummyContactWrapper();
//        }

        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null && this.getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            Toast.makeText(this, "GPS Enabled", Toast.LENGTH_LONG);
        }

        proxmityRec = new ProximityIntentReceiver();

        // Initialize the vector
        standingOrderVector = new Vector();

        PhoneCallListener phoneCallListener = new PhoneCallListener();
        TelephonyManager telManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(phoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);

        phoneNumber = telManager.getLine1Number();
        Log.e("phoneNumber", "+++++++++++++++++++++" + phoneNumber);

//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        String deviceNumber = telephonyManager.getDeviceId();
//        Log.e("deviceNumber", "+++++++++++++++++++++++++" + deviceNumber);

        Bundle extras = getIntent().getExtras();

        if (supervisor.equals("Y")) {
            // System.out.println("Gone supervisor..");
            setContentView(R.layout.sndashboard);

            nameshift = (TextView) findViewById(R.id.nameshift);
            nameshift.setText(guardName);

            addListenerOnPOI();

            customHandler2.postDelayed(updateTimerLocationThread, 60000);

            imgExit = (ImageView) findViewById(R.id.imgExit);
            imgExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setToExitGuard();
                }
            });

        } else {

            setContentView(R.layout.gndashboard);

            nameshift = (TextView) findViewById(R.id.nameshift);
            shiftB = (TextView) findViewById(R.id.shiftB);
            shiftE = (TextView) findViewById(R.id.shiftE);
            imgExit = (ImageView) findViewById(R.id.imgExit);

            // System.out.println("Name" + guardName + companyID);
            nameshift.setText(guardName);
            shiftB.setText("Shift Start: " + startTime);
            shiftE.setText("Shift End: " + endTime);

            batteryPercent = (TextView) this.findViewById(R.id.batteryLevel);

            this.registerReceiver(this.batteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

            // getBatteryPercentage();
            SharedData.setSecretCode(prefs.getString(Constants.GUARD_ID, ""));
            addListenerOnCall();
            customHandler.postDelayed(updateTimerThread, 600000);
            customHandler2.postDelayed(updateTimerLocationThread, 60000);
            customHandler3.postDelayed(messageTimerThread, 60000);

            imgExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setToExitGuard();
                }
            });


        }

        /*********FindLocation**********/
        FindLocation();

        /*********addProximityAlert**********/
//        addProximityAlert(siteID);
        if(Utilities.isNetworkAvailable(ADashboardActivity.this)){
            AddProximityAlertAsyn obj = new AddProximityAlertAsyn();
            obj.execute(siteID);
        }else{
            Toast.makeText(ADashboardActivity.this, "InternetConnection Error!\nPlease connect the valid connection.", Toast.LENGTH_SHORT).show();
        }


        lat = (TextView) findViewById(R.id.lat);
        sitevID = (TextView) findViewById(R.id.siteId);

        /*********getEmergencyContacts**********/
        // getEmergencyContacts(companyID);
        if (Utilities.isNetworkAvailable(ADashboardActivity.this))
            new GetEmergencyContacts().execute(companyID);
        else
            Toast.makeText(ADashboardActivity.this, "InternetConnection Error!\nPlease connect the valid connection.", Toast.LENGTH_SHORT).show();

        /**********addListenerOnButton***********/
        addListenerOnButton();

        /*********addListenerOn911********/
        addListenerOn911();

        /**********BarCodeScanner***********/
        addScanQRCode();

        /***********addStandingOrder***********/
        addStandingOrder();
        /*********addListenerOnCamera***********/
        addListenerOnCamera();

        /***********addMessaging***********/
        addMessaging();

        final ImageView slideButton = (ImageView) findViewById(R.id.drawerHandle);
        slidingDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer);
        slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            public void onDrawerOpened() {
                slideButton.setImageResource(R.drawable.close_slide);
            }
        });

        slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                slideButton.setImageResource(R.drawable.help);
            }
        });

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

    }

    // lock home hold button
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    // lock back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // System.out.println("KEYCODE" + keyCode);

        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            // return true;

            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // if the password is correct we can exit!

                        }
                    }).setNegativeButton("CANCEL", null).show();
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK) && event.isLongPress()) {
            System.out.println("I have emergency!!!");

            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("guardID", guardID.trim());
                loginDateTime = SharedData.getSimpleDateFormat().format(System.currentTimeMillis());
                jsonObject.put("datetime", "" + loginDateTime);
                jsonObject.put("secret", "" + SharedData.getSecretCode());

                HttpPost hp = new HttpPost();

                JSONObject jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.SEND_RED_MESSAGE_URL,
                        jsonObject, 1);
                Log.e("Sending Emergency::::::", "jsonObject:::::::::::::::::" + jsonObject);
                Log.e("Api Response::::::", "Response:::::::::::::::::" + jsonObjectRes);
            } catch (JSONException jex) {
                jex.printStackTrace();
            }

        }

        return false;
    }

    public void addListenerOnloginEditText() {

        EditText editTextguardID = (EditText) findViewById(R.id.guardID);
        editTextguardID.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
    }

    private Runnable updateTimerLocationThread = new Runnable() {
        public void run() {
            try {
                if (currentLocation == null) {
                    customHandler2.postDelayed(this, 60000);
                } else {

                    if (patrolType.equals("Mobile Patrol")) {

                        siteStatus = new ArrayList();

                        for (int i = 0; i < primaryPOIVector.size(); i++) {
                            SitePOI psitePOI = (SitePOI) primaryPOIVector.get(i);
                            boolean distStatus = distanceToPOI(100, psitePOI.getLatitude(), psitePOI.getLongitude(),
                                    currentLatitude, currentLongitude);

                            if (distStatus && currentLocation.getAccuracy() < 50) {

                                // at some site
                                siteStatus.add("Y");

                                if (!ADashboardActivity.siteID.equals(psitePOI.getSiteID())) {

                                    // Toast.makeText(getApplicationContext(),"Before"
                                    // + siteID ,Toast.LENGTH_LONG).show();
                                    siteID = psitePOI.getSiteID();
                                    Toast.makeText(getApplicationContext(), "Entering site - " + siteID, Toast.LENGTH_LONG)
                                            .show();
                                    CapturePOI.ringtone(mContext);
//                                    addProximityAlert(siteID);
                                    if (Utilities.isNetworkAvailable(ADashboardActivity.this)) {
                                        AddProximityAlertAsyn obj = new AddProximityAlertAsyn();
                                        obj.execute(siteID);
                                    }else{
                                        Toast.makeText(ADashboardActivity.this, "InternetConnection Error!\nPlease connect the valid connection.", Toast.LENGTH_SHORT).show();
                                    }


                                    standingOrderArray = null;

                                    timeForPOIs.clear();
                                }
                            }
                        }
                    }

                    if (Utilities.isNetworkAvailable(ADashboardActivity.this)) {
                        LAT_LONG_INFO_URLAsyc obj = new LAT_LONG_INFO_URLAsyc();
                        obj.execute();
                    }else{
                        Toast.makeText(ADashboardActivity.this, "InternetConnection Error!\nPlease connect the valid connection.", Toast.LENGTH_SHORT).show();
                    }


                    customHandler2.postDelayed(this, 60000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /*************
     * LAT_LONG_INFO_URL API
     *********************/
    public class LAT_LONG_INFO_URLAsyc extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            JSONObject jsonObjectRes = null;

            try {
                // guardId, siteId, date, time, lattitude, longitude
                jsonObject = new JSONObject();
                jsonObject.put("guardId", "" + guardID + "");
                jsonObject.put("latitude", "" + currentLocation.getLatitude() + "");
                jsonObject.put("longitude", "" + currentLocation.getLongitude() + "");
                jsonObject.put("trackingtype", "gps");
                jsonObject.put("poiname", "");
                SimpleDateFormat dateFormat = dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                jsonObject.put("datetime", "" + dateFormat.format(System.currentTimeMillis()));

                if (patrolType.equals("Mobile Patrol")) {
                    if (!siteStatus.isEmpty()) {
                        jsonObject.put("siteId", siteID);
                    } else {
                        jsonObject.put("siteId", "");
                    }
                } else {
                    jsonObject.put("siteId", siteID);
                }

                JSONObject extraJSON = new JSONObject();
                extraJSON.put("battery_life", batteryHealth);
                extraJSON.put("guard_phone", phoneNumber);
                jsonObject.put("additional_data", extraJSON);

                HttpPost hp = new HttpPost();
                jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.LAT_LONG_INFO_URL,
                        jsonObject, 1);
                Log.e(TAG, "Response+++++" + jsonObjectRes.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObjectRes;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

        }
    }

    private Runnable messageTimerThread = new Runnable() {

        public void run() {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("guardID", prefs.getString(Constants.GUARD_ID, ""));
                loginDateTime = SharedData.getSimpleDateFormat().format(System.currentTimeMillis() - 10000);

                // if (lastMessageTime == null) {
                lastMessageTime = loginDateTime;
                // }

                // lastMessageTime = loginDateTime + " :10:10";
                jsonObject.put("datetime", "" + lastMessageTime);
                // jsonObject.put("secret", "" + SharedData.bytesToHex(output));
                jsonObject.put("secret", "" + SharedData.getSecretCode());
                Log.e("****************", "*******************************" + jsonObject.toString());
                HttpPost hp = new HttpPost();
                JSONObject jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.FETCH_MESSAGE_URL, jsonObject,
                        1);

                System.out.println("Sending Message Array:" + jsonObject);

                if (null != jsonObjectRes) {
                    JSONArray cmessages = jsonObjectRes.getJSONArray("messages");
                    Log.e("Message Array:", "********************************" + cmessages);

                    if (cmessages.length() > 0) {
                        for (int i = 0; i < cmessages.length(); ++i) {
                            JSONObject locationObj = cmessages.getJSONObject(i);
                            String messageID = locationObj.getString("messageID");
                            String realMessage = locationObj.getString("message");
                            String datetime = locationObj.getString("datetime");

                            // The message window is open
                            if (MessagingActivity.isActive) {
                                MessagingActivity.appendToMessageHistory(
                                        "Monitoring > " + datetime + " : " + realMessage + "\n\n");
                                MessagingActivity.messages
                                        .append("Monitoring >" + datetime + " : " + realMessage + "\n\n");
                            } else {
                                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                                toneG.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 200);
                                toneG.release();
                                MessagingActivity.messages
                                        .append("Monitoring > " + datetime + " : " + realMessage + "\n\n");
                            }

                            JSONObject readjsonObject = new JSONObject();
                            readjsonObject.put("guardID", guardID.trim());
                            loginDateTime = SharedData.getSimpleDateFormat().format(System.currentTimeMillis());

                            readjsonObject.put("datetime", "" + loginDateTime);
                            // readjsonObject.put("secret", "" +
                            // SharedData.bytesToHex(output));
                            readjsonObject.put("secret", "" + SharedData.getSecretCode());
                            readjsonObject.put("messageID", "" + messageID);

                            JSONObject readjsonObjectRes = hp
                                    .HttpPost(Constants.WEB_SERVER + Constants.READ_MESSAGE_URL, readjsonObject, 1);

                        }
                    }
                }

                customHandler3.postDelayed(this, 30000);

            } catch (JSONException jex) {
                jex.printStackTrace();
            }
        }
    };

    private void showNotification(String msg) {
        // Set the icon, scrolling text and TIMESTAMP
        String title = "You got a new Message!";

        String text = ((msg.length() < 5) ? msg : msg.substring(0, 5) + "...");
        //Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.stat_sample)
                .setContentTitle(title)
                .setContentText(text)
                .setSound(soundUri);

        Intent i = new Intent(this, MessagingActivity.class);
        // i.putExtra(FriendInfo.USERNAME, username);
        i.putExtra("textMessage", msg);

        // The PendingIntent to launch our activity if the user selects this
        // notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);

        // Set the info for the views that show in the notification panel.
        // msg.length()>15 ? MSG : msg.substring(0, 15);
        mBuilder.setContentIntent(contentIntent);

        mBuilder.setContentText("New message: " + msg);

        // TODO: it can be improved, for instance message coming from same user
        // may be concatenated
        // next version

        // Send the notification.
        // We use a layout id because it is a unique number. We use it later to
        // cancel.
        mNM.notify((msg).hashCode(), mBuilder.build());
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date eTime = dateFormat.parse(endTime);

                String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Date currTime = dateFormat.parse(currentDateandTime);

                if (currTime.before(eTime))
                    customHandler.postDelayed(this, 600000);

                if (currTime.after(eTime)) {
                    customHandler.removeCallbacks(updateTimerThread);

                    if (ActivityCompat.checkSelfPermission(ADashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ADashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mlocManager.removeUpdates(mlocListener);
                    // mlocManager.removeProximityAlert(proximityIntent);

                    mlocListener = null;

                    unregisterReceiver(proxmityRec);
                    // mContext.stopService (startMyService );

                    if (Utilities.isNetworkAvailable(ADashboardActivity.this)){
                        /*********Execute Logout API********/
                        LogoutApiAsync obj = new LogoutApiAsync();
                        obj.execute();

                        initializeLogin();
                    }else{
                        Toast.makeText(ADashboardActivity.this, "InternetConnection Error!\nPlease connect the valid connection.", Toast.LENGTH_SHORT).show();
                    }

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    };

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /****************************/
    public class AddProximityAlertAsyn extends AsyncTask<String, String, JSONObject> {
        ProgressDialog progressDialog = new ProgressDialog(ADashboardActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            JSONObject jsonObjectRes = null;

            try {
                poiVector.clear();
                jsonObject = new JSONObject();
                jsonObject.put("siteId", params[0]);

                HttpPost hp = new HttpPost();
                jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.GUARD_PATROL_POINTS, jsonObject, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObjectRes;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            if (jsonObject == null) {
                return;
            }

            try {
                JSONArray lineItems = jsonObject.getJSONArray("poiinfo");

                for (int i = 0; i < lineItems.length(); ++i) {
                    JSONObject locationObj = lineItems.getJSONObject(i);

                    JSONObject poiObject = locationObj.getJSONObject("Location");
                    // System.out.println(poiObject);

                    double latitude = Double.parseDouble(poiObject.getString("latitude"));
                    double longitude = Double.parseDouble(poiObject.getString("longitude"));
                    try {
                        POINT_RADIUS = Long.parseLong(poiObject.getString("radius_ft"));
                    } catch (NumberFormatException e) {
                    }
                    POINT_RADIUS = 5;

                    String what = poiObject.getString("name");
                    String proximitys = "com.gpsonguard.ProximityAlert" + i;

                    String geo = "geo:" + latitude + "," + longitude;

                    Intent intent = new Intent("com.gpsonguard.ProximityAlert", Uri.parse(geo));
                    intent.putExtra("alert", what);
                    intent.putExtra("lat", latitude);
                    intent.putExtra("lon", longitude);
                    intent.putExtra("siteID", siteID);
                    intent.putExtra("guardID", guardID);

                    SitePOI sPOI = new SitePOI();
                    sPOI.setLatitude(latitude);
                    sPOI.setLongitude(longitude);
                    sPOI.setGuardID(guardID);
                    sPOI.setSiteID(siteID);
                    sPOI.setRadius(5);
                    sPOI.setPoiName(what);
                    poiVector.add(sPOI);

                    //**********POI Added Message***************//*
                    //  Toast.makeText(getApplicationContext(), what + " POI added", Toast.LENGTH_LONG).show();

                }

                IntentFilter filter = new IntentFilter("com.gpsonguard.ProximityAlert");
                filter.addDataScheme("geo");
                registerReceiver(proxmityRec, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addStandingOrder() {
        standingOrder = (ImageButton) findViewById(R.id.standorder);
        standingOrder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // initiateStandingOrder();
                        if (Utilities.isNetworkAvailable(ADashboardActivity.this)){
                            initiateStandingOrderDialog();
                        }else{
                            Toast.makeText(ADashboardActivity.this, "InternetConnection Error!\nPlease connect the valid connection.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 5000);

            }
        });
    }

    public void addMessaging() {
        sms = (ImageButton) findViewById(R.id.smsbut);
        sms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("guardID", guardID);

                Intent messagingIntent = new Intent(getApplicationContext(), MessagingActivity.class);
                messagingIntent.putExtras(bundle);
                startActivity(messagingIntent);
            }
        });
    }

    private void initiateLoginPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) ADashboardActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.login, (ViewGroup) findViewById(R.id.loginb));
        pwindo = new PopupWindow(layout, 450, 600, true);
        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

    }

    private void initiateStandingOrder() {

        try {

            if (standingOrderArray == null) {
                // Get standing order data
                JSONObject jsonObject = null;
                JSONObject jsonObjectRes = null;

                jsonObject = new JSONObject();
                jsonObject.put("siteId", siteID);
                String loginDateTime = SharedData.getSimpleDateFormat().format(System.currentTimeMillis());
                jsonObject.put("datetime", "" + loginDateTime);
                jsonObject.put("guardId", guardID);
                Log.e(TAG, "standing order api @parms+++++++" + jsonObject.toString());

                // Vector has no data.. get it from server
                HttpPost hp = new HttpPost();

                jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.STANDING_ORDER, jsonObject, 1);
                Log.e(TAG, "standing order api Response+++++++" + jsonObjectRes.toString());

                if (jsonObjectRes != null)
                    standingOrderArray = jsonObjectRes.getJSONArray("Standingorder");
            }
            if (standingOrderArray != null) {
                try {
                    // invoke Standing order Activity
                    Intent standingIntent = new Intent(getApplicationContext(), StandingOrderActivity.class);
                    standingIntent.putExtra("standingorder", standingOrderArray.toString());
                    startActivity(standingIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO
        }
    }

    private void initiateStandingOrderDialog() {
        AlertDialog.Builder standingO = new AlertDialog.Builder(mContext);
        standingO.setCancelable(true);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.standingorder, null);
        standingO.setCustomTitle(view);

        final AlertDialog alert = standingO.create();

        Button btnCancel = (Button) view.findViewById(R.id.btn_close_popup);
        // System.out.println("Cancel Button" + btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alert.dismiss();
            }

        });

        try {

            if (standingOrderArray == null) {

                // Get standing order data
                JSONObject jsonObject = null;
                JSONObject jsonObjectRes = null;

                jsonObject = new JSONObject();
                jsonObject.put("siteId", siteID);
                String loginDateTime = SharedData.getSimpleDateFormat().format(System.currentTimeMillis());
                jsonObject.put("datetime", "" + loginDateTime);
                jsonObject.put("guardId", guardID);

                // Vector has no data.. get it from server
                HttpPost hp = new HttpPost();

                jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.STANDING_ORDER, jsonObject, 1);

                if (jsonObjectRes != null)
                    standingOrderArray = jsonObjectRes.getJSONArray("Standingorder");
            }

            standingOrderVector = new Vector();

            TextView sitename = (TextView) view.findViewById(R.id.site_name);
            TextView siteaddr = (TextView) view.findViewById(R.id.site_addr);
            TextView sitehrs = (TextView) view.findViewById(R.id.site_hrs);
            TextView dailyReport = (TextView) view.findViewById(R.id.daily_report);
            TextView siteKeys = (TextView) view.findViewById(R.id.keys);
            TextView lock = (TextView) view.findViewById(R.id.lock);
            TextView site_auth = (TextView) view.findViewById(R.id.site_auth);
            TextView spec_req = (TextView) view.findViewById(R.id.spec_req);
            TextView patrol_point = (TextView) view.findViewById(R.id.patrol_point);
            TextView barcode = (TextView) view.findViewById(R.id.barcode);


            String hrs = "";

            for (int j = 0; j < standingOrderArray.length(); j++) {
                JSONObject jsonObject2 = standingOrderArray.getJSONObject(j);
                KeyValue keyValue = new KeyValue();
                String key = jsonObject2.getString("key");
                keyValue.setKey(key);
                String value = jsonObject2.getString("value");
                keyValue.setValue(value);

                // System.out.println(key + value);

                if (key.equalsIgnoreCase("site_name")) {
                    Spannable WordtoSpan = new SpannableString("Site Name: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 9,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sitename.setText(WordtoSpan);
                }

                if (key.equalsIgnoreCase("site_addr")) {

                    Spannable WordtoSpan = new SpannableString("Site Address: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 13,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    siteaddr.setText(WordtoSpan);
                    // siteaddr.setText("Site Address: \n" + value);

                }

                if (key.equalsIgnoreCase("mon_fri_hrs")) {
                    hrs = hrs + "\nMon-Fri: " + value;
                }
                if (key.equalsIgnoreCase("sat_sun_hrs")) {
                    hrs = hrs + "\nSat-Sun: " + value;
                }

                if (key.equalsIgnoreCase("daily_report")) {
                    // dailyReport.setText("Daily Reports: \n" + value);
                    Spannable WordtoSpan = new SpannableString("Daily Reports: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 13,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    dailyReport.setText(WordtoSpan);
                }
                if (key.equalsIgnoreCase("site_keys")) {
                    // siteKeys.setText("Site Keys: \n" + value);

                    Spannable WordtoSpan = new SpannableString("Site Keys: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 9,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    siteKeys.setText(WordtoSpan);

                }
                if (key.equalsIgnoreCase("locak_unlock_order")) {
                    // lock.setText("Lock/Unlock Order: \n" + value);

                    Spannable WordtoSpan = new SpannableString("Lock/Unlock Order: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 17,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    lock.setText(WordtoSpan);

                }
                if (key.equalsIgnoreCase("site_auth")) {
                    // site_auth.setText("Site Authorization: \n" + value);
                    Spannable WordtoSpan = new SpannableString("Site Authorization: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 18,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    site_auth.setText(WordtoSpan);
                }
                if (key.equalsIgnoreCase("site_requirements")) {
                    // spec_req.setText("Special Requirements: \n" + value);
                    Spannable WordtoSpan = new SpannableString("Special Requirements: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 20,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spec_req.setText(WordtoSpan);
                }

                if (key.equalsIgnoreCase("security_patrol_point")) {
                    JSONArray jsonPP = new JSONArray(value);

                    StringBuilder pp = new StringBuilder("Security Patrol Points: \n");

                    for (int z = 0; z < jsonPP.length(); z++) {
                        JSONObject jsonObjectPP = jsonPP.getJSONObject(z);
                        JSONObject jsonObjectPP2 = jsonObjectPP.getJSONObject("Location");
                        String ppName1 = jsonObjectPP2.getString("name");
                        // System.out.println("Location Object" + ppName1);
                        pp.append(ppName1 + "\n");
                    }

                    Spannable WordtoSpan = new SpannableString(pp.toString());
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 22,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    patrol_point.setText(WordtoSpan);

                    // patrol_point.setText(pp.toString());
                }

                if (key.equalsIgnoreCase("bar_codes")) {
                    JSONArray jsonPP = new JSONArray(value);

                    StringBuilder bc = new StringBuilder("Barcode: \n");

                    for (int z = 0; z < jsonPP.length(); z++) {
                        JSONObject jsonObjectBC = jsonPP.getJSONObject(z);
                        JSONObject jsonObjectBC2 = jsonObjectBC.getJSONObject("Barcode");
                        String bcName1 = jsonObjectBC2.getString("description");
                        // System.out.println("Location Object" + bcName1);
                        bc.append(bcName1 + "\n");
                    }

                    Spannable WordtoSpan = new SpannableString(bc.toString());
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 8,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    barcode.setText(WordtoSpan);

                    // barcode.setText(bc.toString());
                }

                // standingOrderVector.addElement(keyValue);
            }

            sitehrs.setText("Site Hours: " + hrs);

            Spannable WordtoSpan = new SpannableString("Site Hours: " + hrs);
            WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 10,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sitehrs.setText(WordtoSpan);

            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initiatePopupWindow() {
        try {
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) ADashboardActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.standingorder, (ViewGroup) findViewById(R.id.sorder));

            pwindo = new PopupWindow(layout, 450, 600, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            // btnClosePopup = (Button)
            // layout.findViewById(R.id.btn_close_popup);
            btnClosePopup.setOnClickListener(cancel_button_click_listener);

            if (standingOrderArray == null) {

                // Get standing order data
                JSONObject jsonObject = null;
                JSONObject jsonObjectRes = null;

                jsonObject = new JSONObject();
                jsonObject.put("siteId", siteID);
                String loginDateTime = SharedData.getSimpleDateFormat().format(System.currentTimeMillis());
                jsonObject.put("datetime", "" + loginDateTime);
                jsonObject.put("guardId", guardID);

                // Vector has no data.. get it from server
                HttpPost hp = new HttpPost();
                jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.STANDING_ORDER, jsonObject, 1);
                standingOrderArray = jsonObjectRes.getJSONArray("Standingorder");
            }

            standingOrderVector = new Vector();

            TextView sitename = (TextView) layout.findViewById(R.id.site_name);
            TextView siteaddr = (TextView) layout.findViewById(R.id.site_addr);
            TextView sitehrs = (TextView) layout.findViewById(R.id.site_hrs);
            TextView dailyReport = (TextView) layout.findViewById(R.id.daily_report);
            TextView siteKeys = (TextView) layout.findViewById(R.id.keys);
            TextView lock = (TextView) layout.findViewById(R.id.lock);
            TextView site_auth = (TextView) layout.findViewById(R.id.site_auth);
            TextView spec_req = (TextView) layout.findViewById(R.id.spec_req);
            TextView patrol_point = (TextView) layout.findViewById(R.id.patrol_point);
            TextView barcode = (TextView) layout.findViewById(R.id.barcode);

            String hrs = "";

            for (int j = 0; j < standingOrderArray.length(); j++) {
                JSONObject jsonObject2 = standingOrderArray.getJSONObject(j);
                KeyValue keyValue = new KeyValue();
                String key = jsonObject2.getString("key");
                keyValue.setKey(key);
                String value = jsonObject2.getString("value");
                keyValue.setValue(value);

                // System.out.println(key + value);

                if (key.equalsIgnoreCase("site_name")) {
                    // sitename.setText( Html.fromHtml("<b>Site Name:</b>") +
                    // "\n" + value);
                    // sitename.setText(Html.fromHtml(getString(R.string.site_name)));
                    Spannable WordtoSpan = new SpannableString("Site Name: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 9,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sitename.setText(WordtoSpan);
                }

                if (key.equalsIgnoreCase("site_addr")) {

                    Spannable WordtoSpan = new SpannableString("Site Address: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 13,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    siteaddr.setText(WordtoSpan);
                    // siteaddr.setText("Site Address: \n" + value);

                }

                if (key.equalsIgnoreCase("mon_fri_hrs")) {
                    hrs = hrs + "\nMon-Fri: " + value;
                }
                if (key.equalsIgnoreCase("sat_sun_hrs")) {
                    hrs = hrs + "\nSat-Sun: " + value;
                }

                if (key.equalsIgnoreCase("daily_report")) {
                    // dailyReport.setText("Daily Reports: \n" + value);
                    Spannable WordtoSpan = new SpannableString("Daily Reports: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 13,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    dailyReport.setText(WordtoSpan);
                }
                if (key.equalsIgnoreCase("site_keys")) {
                    // siteKeys.setText("Site Keys: \n" + value);

                    Spannable WordtoSpan = new SpannableString("Site Keys: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 9,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    siteKeys.setText(WordtoSpan);

                }
                if (key.equalsIgnoreCase("locak_unlock_order")) {
                    // lock.setText("Lock/Unlock Order: \n" + value);

                    Spannable WordtoSpan = new SpannableString("Lock/Unlock Order: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 17,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    lock.setText(WordtoSpan);

                }
                if (key.equalsIgnoreCase("site_auth")) {
                    // site_auth.setText("Site Authorization: \n" + value);
                    Spannable WordtoSpan = new SpannableString("Site Authorization: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 18,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    site_auth.setText(WordtoSpan);
                }
                if (key.equalsIgnoreCase("site_requirements")) {
                    // spec_req.setText("Special Requirements: \n" + value);
                    Spannable WordtoSpan = new SpannableString("Special Requirements: \n" + value);
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 20,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spec_req.setText(WordtoSpan);
                }

                if (key.equalsIgnoreCase("security_patrol_point")) {
                    JSONArray jsonPP = new JSONArray(value);

                    StringBuilder pp = new StringBuilder("Security Patrol Points: \n");

                    for (int z = 0; z < jsonPP.length(); z++) {
                        JSONObject jsonObjectPP = jsonPP.getJSONObject(z);
                        JSONObject jsonObjectPP2 = jsonObjectPP.getJSONObject("Location");
                        String ppName1 = jsonObjectPP2.getString("name");
                        // System.out.println("Location Object" + ppName1);
                        pp.append(ppName1 + "\n");
                    }

                    Spannable WordtoSpan = new SpannableString(pp.toString());
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 22,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    patrol_point.setText(WordtoSpan);

                    // patrol_point.setText(pp.toString());
                }

                if (key.equalsIgnoreCase("bar_codes")) {
                    JSONArray jsonPP = new JSONArray(value);

                    StringBuilder bc = new StringBuilder("Barcode: \n");

                    for (int z = 0; z < jsonPP.length(); z++) {
                        JSONObject jsonObjectBC = jsonPP.getJSONObject(z);
                        JSONObject jsonObjectBC2 = jsonObjectBC.getJSONObject("Barcode");
                        String bcName1 = jsonObjectBC2.getString("description");
                        // System.out.println("Location Object" + bcName1);
                        bc.append(bcName1 + "\n");
                    }

                    Spannable WordtoSpan = new SpannableString(bc.toString());
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 8,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    barcode.setText(WordtoSpan);

                }
            }

            sitehrs.setText("Site Hours: " + hrs);

            Spannable WordtoSpan = new SpannableString("Site Hours: " + hrs);
            WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 10,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sitehrs.setText(WordtoSpan);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Scanned:+++++++" + result.getContents());
                try {
                    JSONObject jsonObj = new JSONObject(result.getContents());
                    JSONObject jsonObj1 = jsonObj.getJSONObject("Barcode");
                    String barcode = jsonObj1.getString("barcode");
                    String barcodeDesc = jsonObj1.getString("description");
                    showDialog(R.string.result_succeeded, barcode, barcodeDesc);
                    Log.e(TAG, "SuccessFull Working");
                } catch (JSONException jex) {
                    jex.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void showDialog(int title, final CharSequence barcode, final CharSequence barcodeDesc) {

        Toast.makeText(getApplicationContext(), "Barcode: " + barcode + "\n Description: " + barcodeDesc,
                Toast.LENGTH_SHORT).show();

        JSONObject jsonObject = null;
        String serverData = "";

        try {
            jsonObject = new JSONObject();
            jsonObject.put("guardId", "" + guardID);
            jsonObject.put("siteId", "" + siteID);
            jsonObject.put("latitude", "30.71445");
            jsonObject.put("longitude", "76.75444");
            jsonObject.put("trackingtype", "barcode");
            jsonObject.put("datetime", "" + SharedData.getSimpleDateFormat().format(System.currentTimeMillis()));
            // jsonObject.put("qrdata", message.toString());
            jsonObject.put("barcode", barcode.toString());
            jsonObject.put("barcodeDesc", barcodeDesc.toString());
            serverData = jsonObject.toString();
            Log.e("Json @params......", "serverData..............." + serverData);

            try {
                HttpPost hp = new HttpPost();
                JSONObject jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.SCAN_QRCODE_URL, jsonObject, 1);

                Log.e("SCAN_QRCODE_URL", "Response SCAN_QRCODE_URL-----------" + jsonObjectRes);
            } catch (Exception e) {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Used To retrieve The Guard Information
    private void getGuardInformation() {

        JSONObject jsonObject = null;
        JSONObject jsonObjectRes = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("guardId", "ABC001");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String requiredDate = dateFormat.format(new Date(System.currentTimeMillis()));

            // Utilities.showMessage("The Date Time "+requiredDate);
            jsonObject.put("date", "" + requiredDate);
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
            String requiredDate2 = dateFormat2.format(new Date(System.currentTimeMillis()));
            jsonObject.put("time", requiredDate2);
            // System.out.println("==="+jsonObject.toString());

            HttpPost hp = new HttpPost();
            jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.GUARD_PROFILE_INFO_URL, jsonObject, 1);

            JSONObject guardObj = jsonObjectRes.getJSONObject("guard");
            JSONObject userObject = guardObj.getJSONObject("User");

            String guardName = userObject.getString("name");

            JSONObject scheduleArray = guardObj.getJSONObject("Schedule");
            Vector guardSiteInfoVector = null;
            if (scheduleArray.length() > 0) {
                guardSiteInfoVector = SharedData.getGuardSiteInfoVectorInstance();
            }

            JSONObject indexObject = scheduleArray.getJSONObject("CurrentShift");
            StringBuffer start_Time_Buffer = new StringBuffer(indexObject.getString("start_time"));
            StringBuffer end_Time_Buffer = new StringBuffer(indexObject.getString("end_time"));

            String nmst = "Guard:- " + guardName + "  Shift:- " + start_Time_Buffer + "-" + end_Time_Buffer;
            // System.out.println("Name" + nmst);
            nameshift.setText(nmst);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Used to retrieve the Emergency Contacts from the Web-Server

    /*****************
     * GetEmergencyContacts API
     *****************/
    class GetEmergencyContacts extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            JSONObject jsonObjectRes = null;

            try {
                jsonObject = new JSONObject();
                // jsonObject.put("siteId", ""+SharedData.getSiteId());
                jsonObject.put("siteId", params[0]);

                HttpPost hp = new HttpPost();
                jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.EMERGENCY_NUMBER_URL, jsonObject, 1);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonObjectRes;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject == null) {
                return;
            }
            try {
                JSONArray lineItems = jsonObject.getJSONArray("contacts");

                for (int i = 0; i < lineItems.length(); ++i) {
                    JSONObject contactsObj = lineItems.getJSONObject(i);
                    emergency_contacts.put(contactsObj.getString("contact_number"), contactsObj.getString("contact_name"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void FindLocation() {
        // System.out.println("Gone Location");

        mlocListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                if (location != null)
                    updateLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // 25 meter and 1000 millisecond
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, mlocListener);
    }

    void updateLocation(Location location) {
        currentLocation = location;
        currentLatitude = currentLocation.getLatitude();
        currentLongitude = currentLocation.getLongitude();

        SharedData.setCurrentLatitude(currentLatitude);
        SharedData.setCurrentLongitude(currentLongitude);

        sitevID.setText("Site ID: " + siteID);
        lat.setText("Latitude:" + String.valueOf(currentLatitude) + "\nLongitude:" + String.valueOf(currentLongitude));

        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < poiVector.size(); i++) {
            SitePOI sitePOI = (SitePOI) poiVector.get(i);
            boolean distStatus = distanceToPOI(sitePOI.getRadius(), sitePOI.getLatitude(), sitePOI.getLongitude(),
                    currentLatitude, currentLongitude);

            if (distStatus && location.getAccuracy() < 40) {

                // Check if Guard visited the POI before or not. We are not
                // allowed to capture POIs if the guard is
                // standing at the same POI for 5 min.
                if (timeForPOIs.containsKey(sitePOI.getPoiName())) {

                    long lastTime = Long.valueOf(timeForPOIs.get(sitePOI.getPoiName()).toString());

                    // if visited before, was it before 5 min
                    if ((currentTime - lastTime) > 300000) {
                        CapturePOI.alertandSendPOI(mContext, sitePOI.getPoiName(), sitePOI.getGuardID(),
                                sitePOI.getSiteID(), currentLatitude, currentLongitude, "entered");
                        timeForPOIs.put(sitePOI.getPoiName(), currentTime);
                    }
                    // if Not, capture the POI
                } else {
                    // Toast.makeText(getApplicationContext(),"Close to POI" +
                    // sitePOI.getPoiName() ,Toast.LENGTH_LONG).show();
                    CapturePOI.alertandSendPOI(mContext, sitePOI.getPoiName(), sitePOI.getGuardID(),
                            sitePOI.getSiteID(), currentLatitude, currentLongitude, "entered");
                    timeForPOIs.put(sitePOI.getPoiName(), currentTime);
                }

            }
        }
    }

    public boolean distanceToPOI(double radius, double latCenter, double lonCenter, double latCurrent,
                                 double lonCurrent) {
        boolean status = false;

        float[] results = new float[1];
        Location.distanceBetween(latCenter, lonCenter, latCurrent, lonCurrent, results);
        float distanceInMeters = results[0];

        if (distanceInMeters <= radius) {
            status = true;
        } else
            status = false;

        return status;

    }

    public void addListenerOnButton() {

        btnLogout = (ImageButton) findViewById(R.id.button_logout);

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (ActivityCompat.checkSelfPermission(ADashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ADashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mlocManager.removeUpdates(mlocListener);
                // mlocManager.removeProximityAlert(proximityIntent);

                mlocListener = null;

                unregisterReceiver(proxmityRec);
                // mContext.stopService (startMyService );
                standingOrderArray = null;
                emergency_contacts.clear();

                customHandler2.removeCallbacks(updateTimerLocationThread);
                customHandler.removeCallbacks(updateTimerThread);
                customHandler3.removeCallbacks(messageTimerThread);

                /*********Execute Logout API********/
                if(Utilities.isNetworkAvailable(ADashboardActivity.this)) {
                    LogoutApiAsync obj = new LogoutApiAsync();
                    obj.execute();
                }else{
                    Toast.makeText(ADashboardActivity.this, "InternetConnection Error!\nPlease connect the valid connection.", Toast.LENGTH_SHORT).show();
                }


                guardID = null;

                initializeLogin();

            }

        });
    }

    public void addListenerOnCamera() {

        sendReport = (ImageButton) findViewById(R.id.sendreport);
        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Build.VERSION.SDK_INT >= 23) {
                    insertDummyContactWrapper();
                }
                String loginDateTime = SharedData.getSimpleDateFormat().format(System.currentTimeMillis());
                Intent bundle = new Intent(getApplicationContext(), ReportCamera_Activity.class);
                bundle.putExtra("guardID", guardID);
                bundle.putExtra("siteID", siteID);
                bundle.putExtra("dateTime", loginDateTime);
                startActivity(bundle);
            }
        });
    }

    private android.hardware.Camera getCameraInstance() {

        android.hardware.Camera camera = null;

        try {
            camera = android.hardware.Camera.open();

        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    public void addListenerOnPOI() {

        poi = (ImageButton) findViewById(R.id.poi);

        poi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final Dialog dialog = new Dialog(ADashboardActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.addpoi);

                poilat = (EditText) dialog.findViewById(R.id.poilat);
                poilat.setText(String.valueOf(currentLatitude));

                poilon = (EditText) dialog.findViewById(R.id.poilon);
                poilon.setText(String.valueOf(currentLongitude));

                poiName = (EditText) dialog.findViewById(R.id.poiname);
                poiDesc = (EditText) dialog.findViewById(R.id.poidesc);
                radius = (EditText) dialog.findViewById(R.id.poirad);
                poiAddr = (EditText) dialog.findViewById(R.id.poiaddr);
                poiwarning = (TextView) dialog.findViewById(R.id.poiwarning);

                // dialog.setTitle("Send Report");

                Button button = (Button) dialog.findViewById(R.id.close_poi);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                savePoi = (Button) dialog.findViewById(R.id.save_poi);
                savePoi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isNetworkAvailable(ADashboardActivity.this)){
                            /********Add POI Asyn**********/
                            AddPOIAsynTask obj = new AddPOIAsynTask();
                            obj.execute();
                        }else{
                            Toast.makeText(ADashboardActivity.this, "InternetConnection Error!\nPlease connect the valid connection.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                dialog.show();

            }

        });

    }

    /********
     * Add POI Asyn
     **********/
    public class AddPOIAsynTask extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObjectRes = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("siteId", siteID);
                jsonObject.put("latitude", poilat.getText());
                jsonObject.put("longitude", poilon.getText());
                jsonObject.put("longitude", poilon.getText());
                jsonObject.put("poiname", poiName.getText());
                jsonObject.put("desc", poiDesc.getText());
                jsonObject.put("radius", radius.getText());
                jsonObject.put("address", poiAddr.getText());
                HttpPost hp = new HttpPost();
                jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.ADD_POI_URL,
                        jsonObject, 1);
                Log.e(TAG, jsonObjectRes.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonObjectRes;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject == null) {
                return;
            }
            try {
                String status = jsonObject.getString("status");
                if (status.equals("The POI has been saved sucessfully"))
                    savePoi.setVisibility(View.GONE);
                poiwarning.setText(status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void addScanQRCode() {
        scanQRCode = (ImageButton) findViewById(R.id.scanqr);

        scanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                IntentIntegrator integrator = new IntentIntegrator(ADashboardActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });

    }

    public void addListenerOn911() {
        e911 = (ImageButton) findViewById(R.id.e911);

        e911.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please long press the 9-1-1", Toast.LENGTH_LONG).show();
            }
        });
        e911.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
                phoneCallIntent.setData(Uri.parse("tel:6047218063"));
                // phoneCallIntent.setData(Uri.parse(telephone));
                startActivity(phoneCallIntent);
                return false;
            }
        });
    }

    public void addListenerOnCall() {

        callSupervisor = (ImageButton) findViewById(R.id.callsup);

        callSupervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List item = new ArrayList();
                final List phone = new ArrayList();
                Iterator it = emergency_contacts.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    item.add(pairs.getValue());
                    phone.add(pairs.getKey());
                }

                ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.contacts, item) {

                    ViewHolder holder;
                    Drawable icon;

                    class ViewHolder {
                        ImageView icon;
                        TextView title;
                    }

                    public View getView(int position, View convertView, ViewGroup parent) {
                        final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        if (convertView == null) {
                            convertView = inflater.inflate(R.layout.contacts, null);

                            holder = new ViewHolder();
                            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                            holder.title = (TextView) convertView.findViewById(R.id.title);
                            convertView.setTag(holder);
                        } else {
                            // view already defined, retrieve view holder
                            holder = (ViewHolder) convertView.getTag();
                        }

                        Drawable drawable = getResources().getDrawable(R.drawable.callsel); // this
                        // is
                        // an
                        // image
                        // from
                        // the
                        // drawables
                        // folder

                        holder.title.setText(item.get(position).toString());
                        holder.icon.setImageDrawable(drawable);

                        return convertView;
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_custom_title, null);
                TextView title = (TextView) view.findViewById(R.id.myTitle);
                title.setText("Call Supervisor");
                builder.setCustomTitle(view);
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });

                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        // mDoneButton.setText(items[item]);

                        String telephone = "tel:" + phone.get(item);
                        // System.out.println("Know contact" +
                        // telephone);

                        Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
                        // phoneCallIntent.setData(Uri.parse("tel:4252412568"));
                        phoneCallIntent.setData(Uri.parse(telephone));
                        startActivity(phoneCallIntent);

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

    }

    public void addListenerOnLoginButton() {
        btnLogin = (ImageButton) findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                tv1 = (TextView) findViewById(R.id.warning);

                guardID = inputGuardID.getText().toString();
                if (Utilities.isNetworkAvailable(ADashboardActivity.this)) {
                    LoginAPIAsyn obj = new LoginAPIAsyn();
                    obj.execute(guardID);
                } else {
                    tv1.setTextColor(Color.RED);
                    tv1.setTextSize(16);
                    tv1.setText("No Internet Connection. Please try again later");
                }

            }

        });

    }

    /***************
     * Login Asyn API
     ****************/
    public class LoginAPIAsyn extends AsyncTask<String, String, JSONObject> {
        ProgressDialog progressDialog = new ProgressDialog(ADashboardActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            JSONObject jsonObjectRes = null;
            try {

                jsonObject = new JSONObject();
                jsonObject.put("guardId", params[0]);
                loginDateTime = SharedData.getSimpleDateFormat().format(System.currentTimeMillis());
                jsonObject.put("datetime", "" + loginDateTime);
                jsonObject.put("trackingtype", "login");

                Log.e(TAG, "JSON Object created:++++++++" + jsonObject.toString());

                HttpPost hp = new HttpPost();

                jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.LOGIN_URL, jsonObject, 1);

                Log.e(TAG, "jsonObjectRes+++++++++" + jsonObjectRes.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                tv1.setTextColor(Color.RED);
                tv1.setTextSize(16);
                tv1.setText("No Internet Connection. Please try again later");
            }
            return jsonObjectRes;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            if (jsonObject == null) {
                return;
            }
            try {

                editor.putString(Constants.GUARD_ID, inputGuardID.getText().toString());
                editor.commit();

                String guardStatus = "false";
                String guardSchedule = "false";

                Bundle bundle = new Bundle();

                guardStatus = jsonObject.getString("guard");
                String beginTime = jsonObject.getString("startTime");
                String endTime = jsonObject.getString("endTime");
                String patrolT = jsonObject.getString("patrolType");

                String companyID = null;
                String siteID = null;
                String lsupervisor = "N";

                Date startTime = new Date();
                Date eTime = new Date();
                Date currTime = new Date();

                if (!beginTime.isEmpty()) {

                    companyID = jsonObject.getString("company_id");
                    JSONArray sites = jsonObject.getJSONArray("sites");

                    for (int i = 0; i < sites.length(); i++) {
                        JSONObject site = sites.getJSONObject(i);
                        // siteID = site.getString("siteid");
                        siteID = site.getString("siteID");

                        SitePOI sPrimaryPOI = new SitePOI();
                        sPrimaryPOI.setLatitude(Double.parseDouble(site.getString("latitude")));
                        sPrimaryPOI.setLongitude(Double.parseDouble(site.getString("longitude")));
                        sPrimaryPOI.setSiteID(siteID);
                        sPrimaryPOI.setRadius(100);

                        primaryPOIVector.add(sPrimaryPOI);

                    }

                    lsupervisor = jsonObject.getString("supervisor");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    startTime = dateFormat.parse(beginTime);
                    eTime = dateFormat.parse(endTime);

                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    currTime = dateFormat.parse(currentDateandTime);

                    if (currTime.after(startTime) && currTime.before(eTime)) {
                        guardSchedule = "true";
                        // bundle.putString("guardName",
                        // jsonobj.getString("name"));
                        ADashboardActivity.guardName = jsonObject.getString("name");
                        // bundle.putString("startTime", beginTime);
                        ADashboardActivity.startTime = beginTime;
                        // bundle.putString("endTime", endTime);
                        ADashboardActivity.endTime = endTime;

                        // bundle.putString("companyID", companyID);
                        ADashboardActivity.companyID = companyID;

                        // bundle.putString("siteId", siteID);
                        ADashboardActivity.siteID = siteID;

                        ADashboardActivity.patrolType = patrolT;

                        // bundle.putString("guardId", guardID);
                        ADashboardActivity.guardID = guardID;
                    }
                }

                if (lsupervisor.equals("N"))
                    // bundle.putString( "supervisor","N");
                    ADashboardActivity.supervisor = "N";
                else if (lsupervisor.equals("Y"))
                    // bundle.putString( "supervisor","Y");
                    ADashboardActivity.supervisor = "Y";
                // }

                if (guardStatus.equalsIgnoreCase("true") && guardSchedule.equalsIgnoreCase("true")) {
                    SharedData.setSupervisor(false);
                    // Launch Dashboard Screen
                    Intent dashboard = new Intent(getApplicationContext(), ADashboardActivity.class);
                    dashboard.putExtras(bundle);

                    initializeDashboard();

                } else {
                    if (guardStatus.equalsIgnoreCase("false")) {
                        // System.out.println("Invalid");
                        tv1.setTextColor(Color.RED);
                        tv1.setTextSize(16);
                        tv1.setText("Invalid Guard ID!! Please try again");
                    } else if (guardSchedule.equalsIgnoreCase("false")) {
                        tv1.setTextColor(Color.RED);
                        tv1.setTextSize(16);
                        tv1.setText("You are NOT scheduled to logon at this time. Please contact your supervisor");
                    }

                }
            } catch (JSONException jex) {
                tv1.setTextColor(Color.RED);
                tv1.setTextSize(16);
                tv1.setText("Invalid Guard ID!! Please try again.");
            } catch (Exception ex) {
                tv1.setTextColor(Color.RED);
                tv1.setTextSize(16);
                tv1.setText("Failed to connect to GPS ON GUARD!");
                ex.printStackTrace();
            } finally {

            }
        }
    }

    private void setToExitGuard() {
        LayoutInflater layoutinflater = LayoutInflater.from(this);
        final View textEntryView;
        textEntryView = layoutinflater.inflate(R.layout.exitdialog, null);
        new AlertDialog.Builder(this).setView(textEntryView).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if the password is correct we can exit!
                if (((EditText) textEntryView.findViewById(R.id.exitpassword)).getText().toString()
                        .compareTo(getString(R.string.exitpassword)) == 0) {

//                    if (((EditText) textEntryView.findViewById(R.id.exitpassword)).getText().toString()
//                            .equals(guardID)) {
                    if (mlocManager != null && mlocListener != null) {
                        if (ActivityCompat.checkSelfPermission(ADashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ADashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mlocManager.removeUpdates(mlocListener);
                        // mlocManager.removeProximityAlert(proximityIntent);
                        mlocListener = null;

                        if (proxmityRec != null)
                            unregisterReceiver(proxmityRec);
                    }
                    guardID = null;

                    stopService(new Intent(ADashboardActivity.this, ProximityService.class));
                    // finish this activity
                    ADashboardActivity.this.finish();
//                    System.exit(0);
                } else {
                    Toast.makeText(getApplicationContext(), "Password should be wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("CANCEL", null).show();

    }

    private BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

            batteryHealth = level;
            batteryPercent.setText("Battery Health:" + level + "%");
        }
    };


    /*********************
     * Marshmallows Permissions
     ***************************/
    /*********
     * GRANT PERMISSION FOR TAKEING IMAGE
     * 1) CAMERA PERMISSION
     * 2) WRITE_EXTERNAL_STORAGE PERMISSION
     * 3) READ_EXTERNAL_STORAGE PERMISSION
     **********/

    @TargetApi(Build.VERSION_CODES.M)
    public boolean insertDummyContactWrapper() {

//        listpermissionsNeeded = new ArrayList<String>();
        List<String> permissionsNeeded = new ArrayList<String>();
        int permissionCAMERA = ContextCompat.checkSelfPermission(ADashboardActivity.this,
                Manifest.permission.CAMERA);

        int permissionWRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(ADashboardActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int permissionREAD_SMS = ContextCompat.checkSelfPermission(ADashboardActivity.this,
                Manifest.permission.READ_SMS);
        int permissionACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(ADashboardActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionREAD_PHONE_STATE = ContextCompat.checkSelfPermission(ADashboardActivity.this,
                Manifest.permission.READ_PHONE_STATE);

        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionREAD_SMS != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionREAD_PHONE_STATE != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }

//        if (!listpermissionsNeeded.isEmpty()) {
//            requestPermissions(listpermissionsNeeded.toArray(new String[listpermissionsNeeded.size()]), REQUEST_PERMISSION_CODE);
//            return false;
//        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                        /******** process the normal flow
                         *else any one or both the permissions are not granted
                         *Perform Tasks That you Want******************/

                        /*openCameraGalleryDialog();*/

                        initializeLogin();

                    } else {
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {

                            showDialogOK("CAMERA,READ,WRITE and Location, Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    insertDummyContactWrapper();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            initializeLogin();

                            Toast.makeText(ADashboardActivity.this, "Now You can select the permissions manually from your settings.", Toast.LENGTH_LONG);

                        }
                    }
                } else {
                    initializeLogin();
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

   /* @TargetApi(Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, android.Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write External Storage");
        if (!addPermission(permissionsList, android.Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS Location");
        if (!addPermission(permissionsList, android.Manifest.permission.READ_PHONE_STATE))
            permissionsList.add("Read Phone Contact");
        if (!addPermission(permissionsList, android.Manifest.permission.READ_SMS))
            permissionsList.add("Read SMS");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = this.getResources().getString(R.string.yourneedmarshmallow);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    showMessageOKCancel(message + permissionsNeeded.get(i),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

		*//*Set Camera PoPUP*//*
        Bundle bundle = new Bundle();
        bundle.putString("guardID", guardID);
        bundle.putString("siteID", siteID);
        Intent cameraIntent = new Intent(mContext, DVCameraActivity.class);
        cameraIntent.putExtras(bundle);
        startActivityForResult(cameraIntent, 0);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    *//*Set Camera PoPUP*//*
                    Bundle bundle = new Bundle();
                    bundle.putString("guardID", guardID);
                    bundle.putString("siteID", siteID);
                    Intent cameraIntent = new Intent(mContext, DVCameraActivity.class);
                    cameraIntent.putExtras(bundle);
                    startActivityForResult(cameraIntent, 0);

                } else {
                    // Permission Denied
                    showMessageOKCancel(this.getResources().getString(R.string.permissiondenied),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }*/

    public class LogoutApiAsync extends AsyncTask<String, String, JSONObject> {
        ProgressDialog progressDialog = new ProgressDialog(ADashboardActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObjectRes = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("siteId", siteID);
                jsonObject.put("guardId", prefs.getString(Constants.GUARD_ID, ""));
                jsonObject.put("trackingtype", "logout");
                String loginDateTime = SharedData.getSimpleDateFormat().format(System.currentTimeMillis());
                jsonObject.put("datetime", "" + loginDateTime);
                Log.e(TAG, "Logout JSON Object : " + jsonObject.toString());
                HttpPost hp = new HttpPost();
                jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.LOGIN_URL, jsonObject, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObjectRes;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            if (jsonObject == null) {
                return;
            }
            try {
                String status = jsonObject.getString("status");
                siteID = null;
                guardID = null;
                patrolType = null;
                LoginData.setGuardStatus(false);
                primaryPOIVector.clear();
                MessagingActivity.messages.setLength(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
