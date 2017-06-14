package com.gpsonguard.screen;

import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.gpsonguard.http.HttpPost;
import com.gpsonguard.util.Constants;
import com.gpsonguard.util.SharedData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

//import com.gpsonguard.util.CameraPreview;

public class DVCameraActivity extends Activity {
    
    //private Camera mCamera;
    //private CameraPreview mCameraPreview;
    static int TAKE_PICTURE = 1;
    static Bitmap bitMap;
    private String siteID;
    private String guardID;
    
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    private Camera camera = null;
    
    private SurfaceView preview=null;
    private SurfaceHolder previewHolder=null;
     
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_display);
        
        Bundle extras = getIntent().getExtras();
        if (!extras.equals("")) {
            siteID = extras.get("siteID").toString();
            guardID = extras.get("guardID").toString();
        }
        //mCamera = getCameraInstance();
        //mCameraPreview = new CameraPreview(this, mCamera);
         
        //FrameLayout preview = (FrameLayout) findViewById(id.camera_preview);
        //preview.addView(mCameraPreview);
                
        SurfaceView preview=(SurfaceView)findViewById(R.id.camera_preview);
        previewHolder=preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        View viewControl = inflater.inflate(R.layout.camera_control, null);
        Button captureButton = (Button) viewControl.findViewById(R.id.takepicture);
        Button closeCButton = (Button) viewControl.findViewById(R.id.closepicture);

        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);
        
        //Button captureButton = (Button) findViewById(id.button_capture);
        captureButton.setOnClickListener(
               new View.OnClickListener() {
                    
                   @Override
                   public void onClick(View v) {
                       camera.takePicture(null, null, mPicture);     
                       //showDialog();        
                      // callDescActivity ();
                   } 
                        
               });
        
        closeCButton.setOnClickListener(
        		 new View.OnClickListener() {
                     
                     @Override
                     public void onClick(View v) {
                         finish();                     
                     } 
        });
        
        // lock home button
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
	    KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
	    lock.disableKeyguard();
	    // end lock home button
         
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
    
    /**
     * Helper method to access the camera returns null if
     * it cannot get the camera or does not exist
     * @return
     */
    private Camera getCameraInstance() {
        
 
        try {
            camera = Camera.open();
            //mCamera.setDisplayOrientation(90);
            //startPreview();
            setCameraDisplayOrientation(DVCameraActivity.this, CameraInfo.CAMERA_FACING_BACK, camera);
            
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }
    
        
    PictureCallback mPicture = new PictureCallback() {		 
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        	
        	//System.out.println("Bitmap 2" + data);
        	
        	
        	bitMap = BitmapFactory.decodeByteArray(data , 0, data .length);
        	
        	Matrix matrix = new Matrix();
        	matrix.postRotate(90);
        	bitMap = Bitmap.createBitmap(bitMap, 0, 0, 
        			bitMap.getWidth(), bitMap.getHeight(), 
        	                              matrix, true);
        	
        	
        	
        	//System.out.println("Bitmap 3" + bitMap);   
        	
        	callDescActivity ();
             
        }
         
    };
    
    private void callDescActivity () {
    	 //Bundle bundle = new Bundle(); 
		 //bundle.putString( "guardID", guardID); 
		 //bundle.putString("siteID", siteID);
		 
		 
		 Intent descIntent = new Intent(getApplicationContext(), DVCameraDescActivity.class);
		 //descIntent.putExtras(bundle);
		 
		 System.out.println("Adding BitMap:" + bitMap);
		 
		 ByteArrayOutputStream stream = new ByteArrayOutputStream();
		 bitMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
	     byte[] bytes = stream.toByteArray(); 
		 
		 descIntent.putExtra("data", bytes);
		 descIntent.putExtra("guardID", guardID); 
		 descIntent.putExtra("siteID", siteID);
		 //startActivityForResult(descIntent, 0);
		 startActivity(descIntent);
		 finish();
    }
    
    
    private void showDialog() {
    	 Context mContext = getApplicationContext();
         final Dialog dialog = new Dialog(DVCameraActivity.this);

         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
         dialog.setContentView(R.layout.camera_preview);
        //dialog.setTitle("Send Report");
         
         //System.out.println("Bitmap" + bitMap);
         final EditText desc_text = (EditText) dialog.findViewById(R.id.desc_edittext);             
         //ImageView imagePhoto = (ImageView) dialog.findViewById(R.id.ivThumbnailPhoto);

         //imagePhoto.setImageBitmap(bitMap);
         
         
         Button sendbutton = (Button) dialog.findViewById(R.id.btnTakePic);
         Button canclebutton = (Button) dialog.findViewById(R.id.cancelPic);
         
         canclebutton.setOnClickListener(new View.OnClickListener() {
         @Override
             public void onClick(View v) {
                dialog.dismiss();
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
         			// Utilities.showMessage("The JsonObject " + jsonObject.toString());
         			
         			
         			try {
         		          
         				  //System.out.println ("Captured Image:" + jsonObject.toString());

                 		  HttpPost hp = new HttpPost();
                    	  JSONObject jsonObjectRes  = hp.HttpPost(Constants.WEB_SERVER + Constants.IMAGE_POST_URL, jsonObject, 1);
                 		 //JSONObject jsonObjectRes  = hp.HttpPost("http://192.168.2.11:8081/devices/send_image_data", jsonObject, 1);
                 		 
                 		 
                    	  //System.out.println("Response: " + jsonObjectRes.toString());
                		} catch (Exception e) {
                			   //System.out.print("Error while getting JSON:" + e.getMessage());                       
                	    } 
         			
         			dialog.dismiss();
         			finish();
         			
         		} catch (JSONException e) {
         			e.printStackTrace();
         		}
     		
             }
         });
         
         
         
         dialog.show();
         dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
         
	        
         //finish();
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
    
    
    @Override
    public void onResume() {
      super.onResume();
      
      camera=Camera.open();
      setCameraDisplayOrientation(DVCameraActivity.this, CameraInfo.CAMERA_FACING_BACK, camera);
      
      Parameters p = camera.getParameters();
      p.setFlashMode(Parameters.FOCUS_MODE_AUTO);
      camera.setParameters(p);
      
      //startPreview();
    }
      
    @Override
    public void onPause() {
      if (inPreview) {
        camera.stopPreview();
        setCameraDisplayOrientation(DVCameraActivity.this, CameraInfo.CAMERA_FACING_BACK, camera);
      }
      
      camera.release();
      camera=null;
      inPreview=false;
            
      super.onPause();
    }
    
    private Camera.Size getBestPreviewSize(int width, int height,
                                           Parameters parameters) {
      Camera.Size result=null;
      
      for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
        if (size.width<=width && size.height<=height) {
          if (result==null) {
            result=size;
          }
          else {
            int resultArea=result.width*result.height;
            int newArea=size.width*size.height;
            
            if (newArea>resultArea) {
              result=size;
            }
          }
        }
      }
      
      return(result);
    }
    
    private void initPreview(int width, int height) {
      if (camera!=null && previewHolder.getSurface()!=null) {
        try {
          camera.setPreviewDisplay(previewHolder);
        }
        catch (Throwable t) {
          //Log.e("PreviewDemo-surfaceCallback",
          //      "Exception in setPreviewDisplay()", t);
          //Toast
          //  .makeText(PreviewDemo.this, t.getMessage(), Toast.LENGTH_LONG)
          //  .show();
        }

        if (!cameraConfigured) {
          Parameters parameters=camera.getParameters();
          Camera.Size size=getBestPreviewSize(width, height,
                                              parameters);
          
          if (size!=null) {
            parameters.setPreviewSize(size.width, size.height);
            camera.setParameters(parameters);
            cameraConfigured=true;
          }
        }
      }
    }
    
    private void startPreview() {
      if (cameraConfigured && camera!=null) {
        camera.startPreview();
        inPreview=true;
      }
    }
    
    public static void setCameraDisplayOrientation(Activity activity,
            int cameraId, Camera camera) {
        CameraInfo info =
                new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
    
    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
      public void surfaceCreated(SurfaceHolder holder) {
        // no-op -- wait until surfaceChanged()
      }
      
      public void surfaceChanged(SurfaceHolder holder,
                                 int format, int width,
                                 int height) {
        initPreview(width, height);
        startPreview();
      }
      
      public void surfaceDestroyed(SurfaceHolder holder) {
        // no-op
      }
    };
    
    

    	     

 
}


