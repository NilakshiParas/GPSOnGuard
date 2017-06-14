package com.gpsonguard.screen;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.gpsonguard.http.HttpPost;
import com.gpsonguard.util.Constants;
import com.gpsonguard.util.SharedData;
import com.gpsonguard.util.Utilities;

public class MessagingActivity extends Activity {
	
    private Intent intent;
    private Bundle extras;
    public static StringBuffer messages = new StringBuffer();
    private Button sendSMS;
    private String guardID;
    private String loginDateTime;
    private String loginTime;
    private EditText smessage;
    private static TextView smsmessages;
    static boolean isActive = false;
    private ImageButton btnCancel;
	
	public void onCreate(Bundle savedInstanceState) {
		
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.messaging);
	        //setTitle("Messaging with Command Center");
	        
	        extras = getIntent().getExtras();	        
	        intent = getIntent();
	        
	        guardID = extras.get("guardID").toString();
	        
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        
	        //String txt= "Monitoring: How are you?";
	        //messages.append(txt);
	        
	        smsmessages = (TextView) findViewById(R.id.smsmessages);
	        smsmessages.setText(messages.toString());
		/***Send Message Click Listner***/
	        addListenerOnSendButton();

	        addListenerOnButton();
	        
	        
	 }
	 
	public void addListenerOnSendButton() {   
		sendSMS = (Button) findViewById(R.id.sendMessageButton);
		
		sendSMS.setOnClickListener(new View.OnClickListener() {
    		
    		@Override
			public void onClick(View arg0) {

    			smessage = (EditText) findViewById(R.id.smessage);
    			String sms = smessage.getText().toString();

				Utilities.hideKeyBoad(getApplicationContext(), sendSMS);

    			System.out.print("Message" + sms);
    			if (!sms.trim().isEmpty()) {
    				
    				//try {

        				JSONObject jsonobj = sendMessageJSONObject(guardID, sms); 
        				//Log.e("jsonobj","jsonobj------------------"+jsonobj.toString());
    				//} catch (JSONException jex) {
    					
    				//}
    			}
    		}
		});
	}
	
	public static void appendToMessageHistory(String message) {							
			 smsmessages.append(message + "\n");
			 messages.append(message + "\n");
	}
	
	 private JSONObject sendMessageJSONObject(String guardID, String sms) {
	    	
	    	JSONObject jsonObject = null;
	    	JSONObject jsonObjectRes = null;
	        try {	           
	        	
	        	//{"datetime":"2015-01-11 09:06:00","message":"One more test message","guardID":"987654",
				//	"secret":"88a47891227c27f7288543107fd9fd3218067ab1"}

	        	//MessageDigest md = MessageDigest.getInstance("SHA1");
	        	//String finalSecret = guardID.trim() + "d9b4e030600574";
	        	//md.update(finalSecret.getBytes()); 
	        	//byte[] output = md.digest();	        	
	        	
				jsonObject = new JSONObject();
				jsonObject.put("guardID", guardID.trim());
				loginDateTime = SharedData.getSimpleDateFormat().format(System.currentTimeMillis());
				jsonObject.put("message", "" + sms);
				jsonObject.put("datetime", "" + loginDateTime);
				//jsonObject.put("secret", "" + SharedData.bytesToHex(output)); 
				jsonObject.put("secret", "" + SharedData.getSecretCode());

				System.out.print("JSON Object created:" + jsonObject.toString() );
				
			} catch (JSONException e) {
				e.printStackTrace();
				
			} 
	        
	       try {
	    	   
	    	   HttpPost hp = new HttpPost();
	    	   jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.SEND_MESSAGE_URL,jsonObject, 1);
	    	   loginTime = SharedData.getHomeScreenDateFormat().format(System.currentTimeMillis());
	    	   
	    	   System.out.print("JSON Object created:" + jsonObject.toString() );
	    	   
	    	   String txt= "You > " + loginTime + " : " + sms + "\n\n";
		       messages.append(txt);
	    	   
	    	   //smsmessages.append(txt);
		       smsmessages.setText(messages.toString());
	    	   
	    	   InputMethodManager inputManager = (InputMethodManager) getSystemService(getWindow().getContext().INPUT_METHOD_SERVICE); 
               inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

	    	   smessage.setText("");
	    	   //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    	   
			} catch (Exception e) {			
				
	     		e.printStackTrace();
				//System.out.print("Error while getting JSON:" + e.getMessage());
	        
		   } 
	       return jsonObjectRes;
	    }
	 
	 protected void onStart() {
		 super.onStart();
	     isActive = true;
	 }
	 
	 protected void onStop() {
	     super.onStop();
	     isActive = false;
	 }
	 
	public void addListenerOnButton() {
		btnCancel = (ImageButton) findViewById(R.id.cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
	    });
	}
		   	 
	 
	
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

}
