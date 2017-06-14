package com.gpsonguard.screen;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.CheckBox;

import android.location.LocationManager; 
import android.location.LocationListener;  
import android.location.Location;  

import android.content.Context;
import android.app.AlertDialog;
import android.widget.Toast;

import com.gpsonguard.listener.MyLocationListener;

public class DashboardSupActivity extends Activity {
	
	ImageButton btnLogout;
	Location currentLocation;
    double currentLatitude,currentLongitude;
    TextView lat;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboardsup);
		lat = (TextView) findViewById(R.id.lat);
		FindLocation();
		addListenerOnButton();
	}
	
	public void FindLocation() {
		System.out.println("Gone Here");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                updateLocation(location);

                //Toast.makeText(
                //		DashboardActivity.this,
                //        String.valueOf(currentLatitude) + "\n"
                //                + String.valueOf(currentLongitude), 5000)
                //        .show();

                }

            public void onStatusChanged(String provider, int status,
                    Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 10, locationListener);

    }

    void updateLocation(Location location) {
            currentLocation = location;
            currentLatitude = currentLocation.getLatitude();
            currentLongitude = currentLocation.getLongitude();
            System.out.println("Latitude:" + String.valueOf(currentLatitude) + "\n"
                    + String.valueOf(currentLongitude));
            lat.setText("Latitude:" + String.valueOf(currentLatitude) + "\n"
                    + "Longitude:" + String.valueOf(currentLongitude));

        }
	
	public void addListenerOnButton() {
   	 
    	btnLogout = (ImageButton) findViewById(R.id.button_logout);
    	
    	btnLogout.setOnClickListener(new View.OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				//btnLogin.setImageResource(R.drawable.login_btn_hover);
				//btnLogin.setMinimumHeight(38);
				//btnLogin.setMinimumWidth(98);
				
	            Intent login = new Intent(getApplicationContext(), LoginActivity.class);	                         
	            // Close all views before launching Dashboard
	            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(login);
	                     
			}
 
		});
 
	}

}
