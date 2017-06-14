package com.gpsonguard.screen;

import java.io.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.gpsonguard.http.HttpPost;
import com.gpsonguard.util.Constants;
import com.gpsonguard.util.SharedData;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class DVCameraDescActivity extends Activity {
	
    private Bitmap bitMap;
    private String guardID;
    private String siteID;
    private EditText desc_text;
    private Intent intent;
    private Bundle extras;

	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.camera_preview);
	        
	        extras = getIntent().getExtras();
	        
	        intent = getIntent();
	        
	        // get bitmap
            //bitMap = (Bitmap) intent.getParcelableExtra("data");
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            
            byte[] bytes = intent.getByteArrayExtra("data");
            bitMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            
            guardID = extras.getString("guardID");
            siteID = extras.getString("siteID");
            
	        //System.out.println("Bitmap" + bitMap );
	        desc_text = (EditText) findViewById(R.id.desc_edittext);    
	         
	         
	         //ImageView imagePhoto = (ImageView) dialog.findViewById(R.id.ivThumbnailPhoto);
	         //imagePhoto.setImageBitmap(bitMap);
	         
	         
	         Button sendbutton = (Button) findViewById(R.id.btnTakePic);
	         Button canclebutton = (Button) findViewById(R.id.cancelPic);
	         
	         ImageView imagePhoto = (ImageView) findViewById(R.id.ivThumbnailPhoto);
		     imagePhoto.setImageBitmap(bitMap);
		        
	         canclebutton.setOnClickListener(new View.OnClickListener() {
	         @Override
	             public void onClick(View v) {
	                //dialog.dismiss();
	        	    intent.removeExtra("data");
	        	    intent.replaceExtras(extras);
	                finish();
	             }
	         });
	         
	         sendbutton.setOnClickListener(new View.OnClickListener() {
	         @Override
	             public void onClick(View v) {
	         		JSONObject jsonObject = null;
	         		
	         		//System.out.println("Bitmap4" + bitMap);
	         		String captureBase64string = getBase64String(bitMap);
	         		
	         		//System.out.println(captureBase64string);

	         		try {
	         			
	         			jsonObject = new JSONObject();
	         			jsonObject.put("guardId", "" + guardID);
	         			jsonObject.put("message", "" + desc_text!=null ? desc_text.getText().toString() : "");
	         			jsonObject.put("imagedata", "" + captureBase64string);
	         			jsonObject.put("siteId", "" + siteID);
	         			jsonObject.put("datetime", "" + SharedData.getSimpleDateFormat().format(System.currentTimeMillis()));
	         			//System.out.println("The JsonObject " + jsonObject.toString());
	         			
	         			
	         			try {
	         		          
	         				  //System.out.println ("Captured Image:" + jsonObject.toString());

	                 		  HttpPost hp = new HttpPost();
	                    	  JSONObject jsonObjectRes  = hp.HttpPost(Constants.WEB_SERVER + Constants.IMAGE_POST_URL, jsonObject, 1);
	                 		 //JSONObject jsonObjectRes  = hp.HttpPost("http://192.168.2.11:8081/devices/send_image_data", jsonObject, 1);
	                 		 
	                 		 
	                    	  //System.out.println("Response: " + jsonObjectRes.toString());
	                		} catch (Exception e) {
	                			   //System.out.print("Error while getting JSON:" + e.getMessage());                       
	                	    } 
	         			
	         			//dialog.dismiss();
	         			finish();
	         			
	         		} catch (JSONException e) {
	         			e.printStackTrace();
	         		}
	         }
	         });
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
