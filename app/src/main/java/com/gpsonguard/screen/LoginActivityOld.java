package com.gpsonguard.screen;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.gpsonguard.http.HttpPost;
import com.gpsonguard.util.Constants;
import com.gpsonguard.util.SharedData;
 
public class LoginActivityOld extends Activity {
	public static final String TAG = "LoginActivityOld";
	ImageButton btnLogin;
	Button btnLogin1;
    Button btnLinkToRegister;
    EditText inputGuardID;
    EditText inputPassword;
    TextView loginErrorMsg;
    private String loginDateTime;
    TextView tv1;
    
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    
    private ProgressBar progress;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.login);
        addListenerOnEditText ();
        
        inputGuardID = (EditText) findViewById(R.id.guardID);
        //inputPassword = (EditText) findViewById(R.id.guardPassword);
        btnLogin = (ImageButton) findViewById(R.id.button_login);
       // btnLogin1 = (Button) findViewById(R.id.btnLogin);
        System.out.println("input Guard: " + inputGuardID);
        
       addListenerOnButton();   
       
       //progress = (ProgressBar) findViewById(R.id.progressBar);
       //progress.setVisibility(View.GONE);
       
       KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
       KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
       lock.disableKeyguard();
       
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
    
    /**
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    } **/
    
    
    //draw the menu - we use this menu to exit the app
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    
  //menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
    	LayoutInflater layoutinflater = LayoutInflater.from(this);
        final View textEntryView;
        switch (item.getItemId()) {
        case R.id.item_exit:
            textEntryView = layoutinflater.inflate(R.layout.exitdialog, null);
            new AlertDialog.Builder(this)
            	.setView(textEntryView)
            	.setIcon(android.R.drawable.ic_dialog_alert)
            	.setTitle("Exit")
            	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            		@Override
            		public void onClick(DialogInterface dialog, int which) {
            			//if the password is correct we can exit!
            			if(
            				((EditText)textEntryView.findViewById(R.id.exitpassword)).getText().toString()
            				.compareTo(getString(R.string.exitpassword)) == 0)
            			{
            				//prompt the user for the preferred home
            				Intent selector = new Intent("android.intent.action.MAIN");
            				selector.addCategory("android.intent.category.HOME");
            				selector.setComponent(new ComponentName("android", "com.android.internal.app.ResolverActivity"));
            				startActivity(selector);
            				
            				//finish this activity
            				finish();
            			}
            		}
            	})
            	.setNegativeButton("CANCEL", null)
            	.show();
            return true;
        }
		return false;
    }
    
    //override some buttons
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	//Log.d("button", new Integer(keyCode).toString());
    	
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return true;
        }
        else if ((keyCode == KeyEvent.KEYCODE_CALL)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    
    public void addListenerOnEditText () {
    	
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
    			
    			//new GPSAsyncTask(LoginActivity.this).execute();

    		    tv1 = (TextView) findViewById(R.id.warning);
    		    
    		    //progress.setVisibility(View.VISIBLE);
    		    
    			
    			String guardID = inputGuardID.getText().toString();
    			System.out.print("GUARD ID:" + guardID);
    			Log.e(TAG,"guardID-----------------------"+guardID);
    			
    			try {
    				
    				System.out.println("Gone Page 1");
    				tv1.setTextColor(Color.GREEN);
             		tv1.setTextSize(16);
    				tv1.setText("Loading.....");
    				progress.setVisibility(View.VISIBLE);
    				SystemClock.sleep(7000);
    				System.out.println("Gone Page 2");
    				
    				//{"guard":true,"datetime":1396750873,"startTime":"2014-04-05 11:00:00","endTime":"2014-04-05 20:00:00",
    				//"name":"David Anderson","company_id":"118"}

    				JSONObject jsonobj = getJSONObject(guardID);  
    				System.out.println("Gone Page 3");
    			    System.out.print("Response:" + jsonobj.toString());
    			    String guardStatus = "false";
    			    String guardSchedule = "false";
    			    Log.e(TAG,"jsonobj-----------------------"+jsonobj.toString());
    			    Bundle bundle = new Bundle(); 
    			    
    			   // if (null != guardID && !guardID.isEmpty() && guardID.equals("sup001")) {
    			   // 	guardStatus = "true";
    			   // 	System.out.println("Set Supervisor:" + SharedData.isSupervisor());
    			    	  
    	           //     bundle.putString( "supervisor","Y");  
 
    			    //} else {
    			         guardStatus =	jsonobj.getString("guard");
    			         String beginTime = jsonobj.getString("startTime");
    			         String endTime = jsonobj.getString("endTime");
    			         String companyID = null;
    			         String siteID = null;
    			         String supervisor = "N";
    			         
    			         Date startTime = new Date();
    			         Date eTime = new Date();
    			         Date currTime = new Date();
    			         
    			         if (!beginTime.isEmpty()) {
    			        	 
    			             companyID = jsonobj.getString("company_id");
        			         siteID = jsonobj.getString("siteID");
        			         supervisor = jsonobj.getString("supervisor");
        			         
    			             SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    			             startTime = dateFormat.parse(beginTime);
    			             eTime = dateFormat.parse(endTime);
    			             
    			             String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    			             currTime = dateFormat.parse(currentDateandTime);
    			             
    			             System.out.println("Start Time:" + startTime);
    			             System.out.println("End Time:" + eTime);
    			             System.out.println("Current Time:"  + currTime);
    			             
    			             System.out.println("Current Time after:" + currTime.after(startTime));
    			             System.out.println("Current Time Before:" + currTime.before(eTime));
    			             
    			             if (currTime.after(startTime) && currTime.before(eTime)) {
    			            	 guardSchedule = "true";
    			            	 bundle.putString("guardName", jsonobj.getString("name"));
    			            	 bundle.putString("startTime", beginTime);
    			            	 bundle.putString("endTime", endTime);
    			            	 bundle.putString("companyID", companyID);
    			            	 bundle.putString("siteId", siteID);
    			            	 bundle.putString("guardId", guardID);
    			             
    			             }    			                			             
    			         }
    			         if (supervisor.equals("N"))
    			            bundle.putString( "supervisor","N"); 
    			         else if (supervisor.equals("Y"))
    			        	 bundle.putString( "supervisor","Y"); 
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
    			 }  else {
    				 if (guardStatus.equalsIgnoreCase("false")) {
    				    //System.out.println("Invalid");
                 		tv1.setTextColor(Color.RED);
                 		tv1.setTextSize(16);
                 		tv1.setText("Invalid Guard ID!! Please try again");
    				 } else if (guardSchedule.equalsIgnoreCase("false")) {
                  		tv1.setTextColor(Color.RED);
                  		tv1.setTextSize(16);
                  		tv1.setText("You are NOT scheduled to logon at this time. Please try again later!");
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
    			}   finally {
    				progress.setVisibility(View.GONE);
    			}
			}
 
		});
 
	}
    
    private JSONObject getJSONObject(String guardID) {
    	
    	JSONObject jsonObject = null;
    	JSONObject jsonObjectRes = null;
        System.out.print("Gone Here");
        try {	           
        	
			jsonObject = new JSONObject();
			jsonObject.put("guardId", guardID.trim());
			loginDateTime = SharedData.getSimpleDateFormat().format(System.currentTimeMillis());
			jsonObject.put("datetime", "" + loginDateTime);
			jsonObject.put("trackingtype", "login"); 
			
			System.out.print("JSON Object created:" + jsonObject.toString());
			Log.e(TAG, "JSON Object created:" + jsonObject.toString());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
       try {
    	   
			/** final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "", "Loading please wait...", true);
			new Thread(new Runnable() {
		        @Override
		        public void run() {
		        	try {
		        		while (progressDialog.getProgress() <= progressDialog.getMax()) {
		        			Thread.sleep(200);
		        			//handle.sendMessage(handle.obtainMessage());
		        			if (progressDialog.getProgress() == progressDialog.getMax()) {
		        				progressDialog.dismiss();
		        			}
		        		}
		        	} catch (Exception e) {
		        		     e.printStackTrace();
		        	}
		        }
		     }).start(); **/
    	  
    	   
    	   HttpPost hp = new HttpPost();
    	   jsonObjectRes = hp.HttpPost(Constants.WEB_SERVER + Constants.LOGIN_URL,jsonObject, 1);
    	   Log.e(TAG,"jsonObjectRes---------------------------"+jsonObjectRes);
    	   
		} catch (Exception e) {			
			tv1.setTextColor(Color.RED);
     		tv1.setTextSize(16);
     		tv1.setText("No Internet Connection. Please try again later");
     		
			System.out.print("Error while getting JSON:" + e.getMessage());
        
	       } 
       return jsonObjectRes;
    }
}