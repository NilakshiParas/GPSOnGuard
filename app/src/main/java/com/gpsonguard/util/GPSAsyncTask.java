package com.gpsonguard.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;


public class GPSAsyncTask extends AsyncTask <Void, Void, String> {
	private ProgressDialog mDialog;
	Context mCntxt;
	
	public GPSAsyncTask(Context context) {
		mCntxt = context;
    }
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		new ProgressDialog(mCntxt);
		//mDialog.setCancelable(false);
		mDialog = ProgressDialog.show(mCntxt, "", "Loading please wait...");
		
	}

	@Override
	protected String doInBackground(Void... params) {
         //code for writing excel sheet
		//try {
		//      Thread.sleep(4000);  // Do your real work here
		//} catch (InterruptedException e) {
		//      e.printStackTrace();
	//	}
		return "success";
	}

	@Override
	protected void onPostExecute(String result) {
		if(mDialog!=null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		
		/**
		if (result.length()>0) {
			new AlertDialog.Builder(mCntxt).setTitle("")
		    .setMessage("")
		    .setIcon(R.drawable.ic_launcher)
		    .setCancelable(false)
		    .setPositiveButton("OK",
		      new DialogInterface.OnClickListener()  {
		        public void onClick(DialogInterface dialog, int whichButton)
		        {
		            dialog.dismiss();
		        }
		      })
		    .create().show();
		    } **/
		
          super.onPostExecute(result);
       } 
}