package com.gpsonguard.screen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gpsonguard.http.HttpPost;
import com.gpsonguard.util.Constants;
import com.gpsonguard.util.SharedData;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends Activity {
    public static final String TAG = "LoginActivity";
    ImageButton btnLogin;
    Button btnLogin1;
    Button btnLinkToRegister;
    EditText inputGuardID;
    EditText inputPassword;
    TextView loginErrorMsg;
    private String loginDateTime;
    TextView tv1;
    public String guardID = "";
    ImageView imgExit;

    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";


    private static String KEY_ERROR_MSG = "error_msg";

    private ProgressBar progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        addListenerOnEditText();
        handleSSLHandshake();

        inputGuardID = (EditText) findViewById(R.id.guardID);
        //inputPassword = (EditText) findViewById(R.id.guardPassword);
        btnLogin = (ImageButton) findViewById(R.id.button_login);
        imgExit = (ImageView) findViewById(R.id.imgExit);

        addListenerOnButton();
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

    }

    private void showExitPopUp() {
        LayoutInflater layoutinflater = LayoutInflater.from(this);
        final View textEntryView;
        textEntryView = layoutinflater.inflate(R.layout.dialogloginexit, null);
        new AlertDialog.Builder(this)
                .setView(textEntryView)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    //override some buttons
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_CALL)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @Override protected void onStop()
     * {
     * super.onStop();
     * startActivity(new Intent(this, LoginActivity.class));
     * }
     **/

    public void addListenerOnEditText() {

        EditText guardID = (EditText) findViewById(R.id.guardID);
        guardID.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
    }

    public void addListenerOnButton() {
        btnLogin = (ImageButton) findViewById(R.id.button_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                tv1 = (TextView) findViewById(R.id.warning);

                guardID = inputGuardID.getText().toString();

                LoginAsynTask obj = new LoginAsynTask();
                obj.execute(guardID);
            }

        });
    }

    public class LoginAsynTask extends AsyncTask<String, String, JSONObject> {
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(true);
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

                Log.e(TAG, "jsonObject-------------------" + jsonObject.toString());



                HttpPost hp = new HttpPost();
                jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.LOGIN_URL, jsonObject, 1);
                Toast.makeText(getApplicationContext(), "------------------Json---------" + jsonObjectRes, Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                tv1.setTextColor(Color.RED);
                tv1.setTextSize(16);
                tv1.setText("No Internet Connection. Please try again later");
            }
            return jsonObjectRes;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.e("Result",""+jsonObject);
            if (jsonObject == null) {
                return;
            }

            try {
                String guardStatus = "false";
                String guardSchedule = "false";

                Bundle bundle = new Bundle();
                guardStatus = jsonObject.getString("guard");
                String beginTime = jsonObject.getString("startTime");
                String endTime = jsonObject.getString("endTime");
                String companyID = null;
                String siteID = null;
                String supervisor = "N";

                Log.e(TAG, "guardStatus--------------" + guardStatus);

                Date startTime = new Date();
                Date eTime = new Date();
                Date currTime = new Date();

                if (!beginTime.equals("") &&!beginTime.isEmpty()) {

                    companyID = jsonObject.getString("company_id");
                    siteID = jsonObject.getString("siteID");
                    supervisor = jsonObject.getString("supervisor");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    startTime = dateFormat.parse(beginTime);
                    eTime = dateFormat.parse(endTime);

                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    currTime = dateFormat.parse(currentDateandTime);

                    if (currTime.after(startTime) && currTime.before(eTime)) {
                        guardSchedule = "true";
                        bundle.putString("guardName", jsonObject.getString("name"));
                        bundle.putString("startTime", beginTime);
                        bundle.putString("endTime", endTime);
                        bundle.putString("companyID", companyID);
                        bundle.putString("siteId", siteID);
                        bundle.putString("guardId", guardID);
                    }
                }
                if (supervisor.equals("N"))
                    bundle.putString("supervisor", "N");
                else if (supervisor.equals("Y"))
                    bundle.putString("supervisor", "Y");
                //}

                if (guardStatus.equalsIgnoreCase("true") && guardSchedule.equalsIgnoreCase("true")) {
                    SharedData.setSupervisor(false);
                    // Launch Dashboard Screen
                    Intent dashboard = new Intent(getApplicationContext(), ADashboardActivity.class);
                    dashboard.putExtras(bundle);

                    // Close all views before launching Dashboard
                    dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(dashboard);

                    // Close Login Screen
                    finish();
                } else {
                    if (guardStatus.equalsIgnoreCase("false")) {
                        //System.out.println("Invalid");
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


    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }


}