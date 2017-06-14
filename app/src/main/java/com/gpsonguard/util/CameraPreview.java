package com.gpsonguard.util;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	 
    //private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    
    private SurfaceHolder previewHolder=null;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    
 
    //Constructor that obtains context and camera
    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
         
        previewHolder = this.getHolder();
        previewHolder.addCallback(this); // we get notified when underlying surface is created and destroyed
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
      //  this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //this is a deprecated method, is not requierd after 3.0
        //this.mSurfaceHolder.
    }
 
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
          // left blank for now
        }
 
    }
     
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
        mCamera.release();
    }
 
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
            int width, int height) {
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            // intentionally left blank for a test
        }
    }
    
    private void initPreview(int width, int height) {
        if (mCamera!=null && previewHolder.getSurface()!=null) {
          try {
            mCamera.setPreviewDisplay(previewHolder);
          }
          catch (Throwable t) {
            //Log.e("PreviewDemo-surfaceCallback",
            //      "Exception in setPreviewDisplay()", t);
            //Toast
            //  .makeText(PreviewDemo.this, t.getMessage(), Toast.LENGTH_LONG)
            //  .show();
          }

          if (!cameraConfigured) {
            Camera.Parameters parameters=mCamera.getParameters();
            Camera.Size size=getBestPreviewSize(width, height,
                                                parameters);
            
            if (size!=null) {
              parameters.setPreviewSize(size.width, size.height);
              mCamera.setParameters(parameters);
              cameraConfigured=true;
            }
          }
        }
      }
    
    private void startPreview() {
        if (cameraConfigured && mCamera!=null) {
          mCamera.startPreview();
          inPreview=true;
        }
      }
    
    private Camera.Size getBestPreviewSize(int width, int height,
            Camera.Parameters parameters) {
    	
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
     
}