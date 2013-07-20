package com.ennoviabd.app.maps;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivityGPS extends Activity implements LocationListener{
	private TextView lastLatitude;
	private TextView lastLongitude;
	private TextView txtGpsProvider;
	private String gpsProvider;
	private LocationManager lManager;
	double latitude; // latitude
	double longitude; // longitude
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_gps);
        lastLatitude = (TextView) findViewById(R.id.txtlatitude);
        lastLongitude = (TextView) findViewById(R.id.txtlongitude);
        txtGpsProvider=(TextView)findViewById(R.id.txtgpsprovider);

        // Get the location manager
        lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
       // gpsProvider = lManager.getBestProvider(criteria, false);
        gpsProvider=LocationManager.GPS_PROVIDER;
        Location location = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
     // Initialize the location fields
        if (location != null) {
          Toast.makeText(this, "Provider :" + gpsProvider + " has been selected.",Toast.LENGTH_SHORT).show();
          //gpsProvider=LocationManager.GPS_PROVIDER;
          onLocationChanged(location);
        } else {
          lastLatitude.setText("Location not available");
          lastLongitude.setText("Location not available");
        }
        
        
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	 lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 1, this);
    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	lManager.removeUpdates(this);
    }
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		 location.getLatitude();
	    latitude = location.getLatitude();
	    longitude=location.getLongitude();
	    lastLatitude.setText(String.valueOf(latitude));
	    lastLongitude.setText(String.valueOf(longitude));
	    txtGpsProvider.setText(gpsProvider);
	    Toast.makeText(getApplicationContext(), "Location Changed", Toast.LENGTH_SHORT).show();
	    
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Status :"+status, Toast.LENGTH_SHORT).show();
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Enabled new provider " + gpsProvider,  Toast.LENGTH_SHORT).show();
		/* bring up the GPS settings */
		Intent intent = new Intent(	android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disabled provider " + gpsProvider,Toast.LENGTH_SHORT).show();	
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_g, menu);
        return true;
    }
    
    
}
