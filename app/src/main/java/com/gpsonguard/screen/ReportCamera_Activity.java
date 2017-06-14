package com.gpsonguard.screen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gpsonguard.http.HttpPost;
import com.gpsonguard.util.Constants;
import com.gpsonguard.util.SharedData;
import com.gpsonguard.util.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class ReportCamera_Activity extends AppCompatActivity {
    Context context = ReportCamera_Activity.this;
    public static final String TAG = "ReportCamera_Activity";
    TextView txtCancel, txtSendReport;
    EditText editDescription;
    ImageView imgReport;
    String strDescription = "", strSiteID = "", strBase64Image = "", strDateTime = "", strGuardID = "";

    public final int CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_camera_);

        gettingIntents();

        openCamera();

        setWidgetsIDs();

        setClickListner();
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void gettingIntents() {
        Intent intent = getIntent();
        strSiteID = intent.getStringExtra("siteID");
        strGuardID = intent.getStringExtra("guardID");
        strDateTime = intent.getStringExtra("dateTime");
    }

    private void setWidgetsIDs() {
        txtCancel = (TextView) findViewById(R.id.txtCancel);
        txtSendReport = (TextView) findViewById(R.id.txtSendReport);
        editDescription = (EditText) findViewById(R.id.editDescription);
        imgReport = (ImageView) findViewById(R.id.imgReport);
    }


    private void setClickListner() {
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
        txtSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strDescription = editDescription.getText().toString();
                if (strDescription.equals("")) {
                    showAlertDialog("GPS ON Guard", "Please enter the report Description.");
                } else {
                    /***Call to API ****/
                    sendReportImageDatatoServer();
                }
            }
        });
    }

    private void sendReportImageDatatoServer() {
        JSONObject jsonObject = null;
        try {

            jsonObject = new JSONObject();
            jsonObject.put("guardId", strGuardID);
            jsonObject.put("message", strDescription);
            jsonObject.put("imagedata", strBase64Image);
            jsonObject.put("siteId", strSiteID);
            jsonObject.put("datetime", strDateTime);
            Log.e(TAG, "@params:+++++ " + jsonObject.toString());
            try {
                HttpPost hp = new HttpPost();
                JSONObject jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.IMAGE_POST_URL,
                        jsonObject, 1);
                Log.e(TAG, "Response:+++++ " + jsonObjectRes.toString());
                boolean apiResponse = jsonObjectRes.getBoolean("response");
                if (apiResponse = true) {
                    Toast.makeText(getApplicationContext(), "Upload Report Successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Sever not respond.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "API Exception++++++" + e);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ReportCamera_Activity.this.CAMERA_REQUEST_CODE == requestCode && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgReport.setImageBitmap(bitmap);
            strBase64Image = Utilities.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
