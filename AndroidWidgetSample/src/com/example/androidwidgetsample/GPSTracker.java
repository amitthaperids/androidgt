package com.example.androidwidgetsample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker implements LocationListener{

	private Context mContext;
	private LocationManager locationManager;
	private boolean isGPSEnabled = false;
	private boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	private Location location;
	Double latitude;
	Double longitude;
	private SharedPreferences geoPointPref;
	
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute 
	
	public GPSTracker(Context ctx){
		this.mContext = ctx;
		getLocation();
	}
	@Override
	public void onLocationChanged(Location location) {
		getLocation();
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Function to get the user's current location
	 * @return
	 */
	public Location getLocation() {
	    try {
	        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
	        geoPointPref = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
	        
	        // getting GPS status
	        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

	        Log.v("isGPSEnabled", "=" + isGPSEnabled);

	        // getting network status
	        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

	        Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

	        if (isGPSEnabled == false && isNetworkEnabled == false) {
	            // no network provider is enabled
	        	Log.v("NO PROVIDER", "no network provider is enabled");
	        	latitude = null;
	        	longitude = null;
	        } else {
	            this.canGetLocation = true;
	            if (isNetworkEnabled) {
	                locationManager.requestLocationUpdates(
	                        LocationManager.NETWORK_PROVIDER,
	                        MIN_TIME_BW_UPDATES,
	                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                Log.d("Network", "Network");
	                if (locationManager != null) {
	                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                    if (location != null) {
	                        latitude = location.getLatitude();
	                        longitude = location.getLongitude();
	                    }
	                }
	            }
	            // if GPS Enabled get lat/long using GPS Services
	            if (isGPSEnabled) {
	                if (location == null) {
	                    locationManager.requestLocationUpdates(
	                            LocationManager.GPS_PROVIDER,
	                            MIN_TIME_BW_UPDATES,
	                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                    Log.d("GPS Enabled", "GPS Enabled");
	                    if (locationManager != null) {
	                        location = locationManager
	                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                        if (location != null) {
	                            latitude = location.getLatitude();
	                            longitude = location.getLongitude();
	                        }
	                    }
	                }
	            }
	           
	        }
	        Editor geoPointEdit = geoPointPref.edit();
            geoPointEdit.putString("Latitude", ""+latitude);
            geoPointEdit.putString("Longitude", ""+longitude);
            geoPointEdit.commit();
            Log.v("Latitude", "=" + latitude);
            Log.v("Longitude", "=" + longitude);
//            stopUsingGPS();
            SwitchOFFGPS(mContext);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
	    if (locationManager != null) {
	        locationManager.removeUpdates(GPSTracker.this);
	    }
	}
	
	private void SwitchOFFGPS(Context context){
		String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		
		if(provider.contains("gps")){ //if gps is enabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3")); 
			context.sendBroadcast(poke);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
	    if (location != null) {
	        latitude = location.getLatitude();
	    }
	    else{
	    	latitude = 0.0;
	    }

	    // return latitude
	    return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
	    if (location != null) {
	        longitude = location.getLongitude();
	    }
	    else{
	    	longitude = 0.0;
	    }

	    // return longitude
	    return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
	    return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert() {
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

	    // Setting Dialog Title
	    alertDialog.setTitle("GPS is settings");

	    // Setting Dialog Message
	    alertDialog
	            .setMessage("GPS is not enabled. Do you want to go to settings menu?");

	    // On pressing Settings button
	    alertDialog.setPositiveButton("Settings",
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    Intent intent = new Intent(
	                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                    mContext.startActivity(intent);
	                }
	            });

	    // on pressing cancel button
	    alertDialog.setNegativeButton("Cancel",
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    dialog.cancel();
	                }
	            });

	    // Showing Alert Message
	    alertDialog.show();
	}

}
