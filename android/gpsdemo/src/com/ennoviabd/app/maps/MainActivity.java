package com.ennoviabd.app.maps;



import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBoundsCreator;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	Button btnShowLocation;
    
    // GPSTracker class
    GpsHandler gps;
   //static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static  LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;
    private LatLngBounds bound;  
    
    
   // private GoogleMap  gmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		/*
		btnShowLocation=(Button)findViewById(R.id.btngps);
		btnShowLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 gps = new GpsHandler(MainActivity.this);
				 
	                // check if GPS enabled    
	                if(gps.canGetLocation()){
	                     
	                    double latitude = gps.getLatitude();
	                    double longitude = gps.getLongitude();
	                    Toast.makeText(getApplicationContext(), "Your Location is \nLat: " + latitude+ "\nLong: " + longitude, Toast.LENGTH_LONG).show();   
	                }else{
	                    // can't get location
	                    // GPS or Network is not enabled
	                    // Ask user to enable GPS/network in settings
	                    gps.showSettingsAlert();
	                }
				
			}
		});
		*/
		// Getting reference to the SupportMapFragment of activity_main.xml
		
		GoogleMapOptions options = new GoogleMapOptions();
		options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
	    .compassEnabled(false)
	    .rotateGesturesEnabled(false)
	    .tiltGesturesEnabled(false);
		
		SupportMapFragment fm = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).newInstance(options);
		
					
					// Getting GoogleMap object from the fragment
					map = fm.getMap();
		
		    
		    if (map!=null){
		    	Toast.makeText(getApplicationContext(), "Map found", Toast.LENGTH_SHORT).show();
		    	map.setMyLocationEnabled(true);
		    	/*
		      Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
		          .title("Hamburg"));
		      Marker kiel = map.addMarker(new MarkerOptions()
		          .position(KIEL)
		          .title("Kiel")
		          .snippet("Kiel is cool")
		          .icon(BitmapDescriptorFactory
		              .fromResource(R.drawable.ic_launcher)));
		      */
		      //map.moveCamera(CameraUpdateFactory.newLatLng(HAMBURG));
				
				// Zoom in the Google Map
		     // map.animateCamera(CameraUpdateFactory.zoomTo(15));
		    //	bound= map.getProjection().getVisibleRegion().latLngBounds;
		    			
		    	//

		      // Move the camera instantly to hamburg with a zoom of 15.
		    	gps = new GpsHandler(MainActivity.this);
		    	Toast.makeText(getApplicationContext(), "Lat:"+gps.getLatitude()+ " long:"+gps.getLongitude(), Toast.LENGTH_LONG).show();
		    	//KIEL=new LatLng(gps.getLatitude(), gps.getLongitude());
				 map.moveCamera(CameraUpdateFactory.newLatLng(KIEL));

			      // Zoom in, animating the camera.
			      map.animateCamera(CameraUpdateFactory.zoomTo(15));
			      bound.including(KIEL);
			     
			      GroundOverlay groundOverlay = map.addGroundOverlay(new GroundOverlayOptions()
			      .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
			      .positionFromBounds(bound)
			      .transparency((float) 0.5));
			     
			      
			      map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			      
			      
			      
		     
		    }
		
		
	}
	
}
