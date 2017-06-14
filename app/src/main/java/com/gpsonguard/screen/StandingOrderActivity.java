package com.gpsonguard.screen;

import java.io.ByteArrayOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.gpsonguard.http.HttpPost;
import com.gpsonguard.util.Constants;
import com.gpsonguard.util.KeyValue;
import com.gpsonguard.util.SharedData;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class StandingOrderActivity extends Activity {
	
    private Bitmap bitMap;
    private String guardID;
    private String siteID;
    private EditText desc_text;
    private Intent intent;
    private Bundle extras;

	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.standingorder);
	        
	        extras = getIntent().getExtras();
	        
	        intent = getIntent();
	        
	        // get bitmap
            //bitMap = (Bitmap) intent.getParcelableExtra("data");
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        
	        String standingOrderS = intent.getStringExtra("standingorder");

		    Button btnCancel = (Button) findViewById(R.id.btn_close_popup);
		        //System.out.println("Cancel Button" + btnCancel);
		        btnCancel.setOnClickListener(new View.OnClickListener() {
		    		
		    		@Override
					public void onClick(View arg0) {
		    			finish();
		    		}
		    		
		    	});
	         
	         	try {
	         			
	         		JSONArray  standingOrderArray = new JSONArray(standingOrderS);
	         		
	         		TextView sitename = (TextView) findViewById(R.id.site_name);
	        		TextView siteaddr = (TextView) findViewById(R.id.site_addr);
	        		TextView sitehrs = (TextView)  findViewById(R.id.site_hrs);
	        		TextView dailyReport = (TextView) findViewById(R.id.daily_report);
	        		TextView siteKeys = (TextView) findViewById(R.id.keys);
	        		TextView lock = (TextView) findViewById(R.id.lock);
	        		TextView site_auth = (TextView) findViewById(R.id.site_auth);
	        		TextView spec_req = (TextView) findViewById(R.id.spec_req);
	        		TextView patrol_point = (TextView) findViewById(R.id.patrol_point);
	        		TextView barcode = (TextView) findViewById(R.id.barcode);
	        			
	        		String hrs = "";
	        			
	        		for (int j = 0; j < standingOrderArray.length(); j++) {
	        			
	        			JSONObject jsonObject2 = standingOrderArray.getJSONObject(j);
	        			KeyValue keyValue = new KeyValue();
	        			String key = jsonObject2.getString("key");
	        			keyValue.setKey(key);
	        			String value = jsonObject2.getString("value");
	        			keyValue.setValue(value);
	        				
	        				//System.out.println(key + value);
	        			
	        			if (key.equalsIgnoreCase("site_name")) {
	        				//sitename.setText( Html.fromHtml("<b>Site Name:</b>") + "\n" + value);
	        				//sitename.setText(Html.fromHtml(getString(R.string.site_name)));
	        				Spannable WordtoSpan = new SpannableString("Site Name: \n" + value);
	        				WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        				sitename.setText(WordtoSpan);
	        			}
	        				
	        			if (key.equalsIgnoreCase("site_addr")) {
	        				Spannable WordtoSpan = new SpannableString("Site Address: \n" + value);
	        				WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        				siteaddr.setText(WordtoSpan);
	        				//siteaddr.setText("Site Address: \n" + value);	        					
	        			}
	        				
	        				if (key.equalsIgnoreCase("mon_fri_hrs")) {
	        					hrs = hrs + "\nMon-Fri: " + value;
	        				}
	        				if (key.equalsIgnoreCase("sat_sun_hrs")) {
	        					hrs = hrs + "\nSat-Sun: " + value;
	        				}
	        				
	        				if (key.equalsIgnoreCase("daily_report")) {
	        					//dailyReport.setText("Daily Reports: \n" + value);
	        					Spannable WordtoSpan = new SpannableString("Daily Reports: \n" + value);
	        					WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        					dailyReport.setText(WordtoSpan);
	        				}
	        				if (key.equalsIgnoreCase("site_keys")) {
	        					//siteKeys.setText("Site Keys: \n" + value);
	        					
	        					Spannable WordtoSpan = new SpannableString("Site Keys: \n" + value);
	        					WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        					siteKeys.setText(WordtoSpan);
	        					
	        				}
	        				if (key.equalsIgnoreCase("locak_unlock_order")) {
	        					//lock.setText("Lock/Unlock Order: \n" + value);
	        					
	        					Spannable WordtoSpan = new SpannableString("Lock/Unlock Order: \n" + value);
	        					WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        					lock.setText(WordtoSpan);
	        					
	        				}
	        				if (key.equalsIgnoreCase("site_auth")) {
	        					//site_auth.setText("Site Authorization: \n" + value);
	        					Spannable WordtoSpan = new SpannableString("Site Authorization: \n" + value);
	        					WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        					site_auth.setText(WordtoSpan);
	        				}
	        				if (key.equalsIgnoreCase("site_requirements")) {
	        					//spec_req.setText("Special Requirements: \n" + value);
	        					Spannable WordtoSpan = new SpannableString("Special Requirements: \n" + value);
	        					WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        					spec_req.setText(WordtoSpan);
	        				}
	        				
	        				if (key.equalsIgnoreCase("security_patrol_point")) {
	        					JSONArray jsonPP = new JSONArray(value);
	        					
	        					StringBuilder pp = new StringBuilder("Security Patrol Points: \n");
	        					
	        					for (int z = 0; z < jsonPP.length(); z++) {
	        						JSONObject jsonObjectPP = jsonPP.getJSONObject(z);
	                            	JSONObject jsonObjectPP2= jsonObjectPP.getJSONObject("Location");
	                            	String ppName1 = jsonObjectPP2.getString("name");
	                            	//System.out.println("Location Object" + ppName1);
	                            	pp.append(ppName1 + "\n");
	        					}
	        					
	        					Spannable WordtoSpan = new SpannableString(pp.toString());
	        					WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        					patrol_point.setText(WordtoSpan);
	        					
	        					//patrol_point.setText(pp.toString());
	        				}
	        				
	        				if (key.equalsIgnoreCase("bar_codes")) {
	        					JSONArray jsonPP = new JSONArray(value);
	        					
	        					StringBuilder bc = new StringBuilder("Barcode: \n");
	        					
	        					for (int z = 0; z < jsonPP.length(); z++) {
	        						JSONObject jsonObjectBC = jsonPP.getJSONObject(z);
	                            	JSONObject jsonObjectBC2= jsonObjectBC.getJSONObject("Barcode");
	                            	String bcName1 = jsonObjectBC2.getString("description");
	                            	//System.out.println("Location Object" + bcName1);
	                            	bc.append(bcName1 + "\n");
	        					}
	        					
	        					Spannable WordtoSpan = new SpannableString(bc.toString());
	        					WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        					barcode.setText(WordtoSpan);
	        					
	        					//barcode.setText(bc.toString());
	        				}

	        				//standingOrderVector.addElement(keyValue);
	        			}
	        			
	        			sitehrs.setText("Site Hours: " + hrs);
	        			
	        			Spannable WordtoSpan = new SpannableString("Site Hours: " + hrs);
	        			WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(244, 98, 0)), 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        			sitehrs.setText(WordtoSpan);
	         			
	         		} catch (JSONException e) {
	         			e.printStackTrace();
	         		}
	 }
	 
	 
	   
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	//Log.d("button", new Integer(keyCode).toString());
	    	//if (keyCode == KeyEvent.KEYCODE_HOME) {
	            //Log.i("TESTE", "BOTAO HOME");
	        //    return true;
	        //}
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	            return true;
	        }
	        else if ((keyCode == KeyEvent.KEYCODE_CALL)) {
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	    }

	    public void onWindowFocusChanged(boolean hasFocus) {
	    	super.onWindowFocusChanged(hasFocus);
	    
	            //Log.d("Focus debug", "Focus changed !");
	     
	    	if(!hasFocus) {
	    		//Log.d("Focus debug", "Lost focus !");
	     
	    		Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	    		sendBroadcast(closeDialog);
	    	}
	    }
	    
	    private String getBase64String(Bitmap bitMap) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] captureImageByteData = stream.toByteArray();
			
			//String captureBase64string = Base64OutputStream .encodeAsString(captureImageByteData, 0, captureImageByteData.length,false, false);
			
			String captureBase64string = Base64.encodeToString(captureImageByteData, Base64.DEFAULT);
			
			// Utilities.showMessage("The Encoded String is "
			// + captureBase64string);
			return captureBase64string;

		}
}
