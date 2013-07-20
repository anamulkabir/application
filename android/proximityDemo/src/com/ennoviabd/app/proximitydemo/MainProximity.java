package com.ennoviabd.app.proximitydemo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class MainProximity extends Activity {
	TextView ProximitySensor, ProximityMax, ProximityReading;
	 
	 SensorManager mySensorManager;
	 Sensor myProximitySensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_proximity);
        ProximitySensor = (TextView)findViewById(R.id.proximitySensor);
        ProximityMax = (TextView)findViewById(R.id.proximityMax);
        ProximityReading = (TextView)findViewById(R.id.proximityReading);
        mySensorManager = (SensorManager)getSystemService(
                Context.SENSOR_SERVICE);
              myProximitySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
              
              if (myProximitySensor == null){
               ProximitySensor.setText("No Proximity Sensor!"); 
              }else{
               ProximitySensor.setText(myProximitySensor.getName());
               ProximityMax.setText("Maximum Range: " + String.valueOf(myProximitySensor.getMaximumRange()));
               mySensorManager.registerListener(proximitySensorEventListener,myProximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
              }
    }
    SensorEventListener proximitySensorEventListener=new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if(event.sensor.getType()==Sensor.TYPE_PROXIMITY){
			    ProximityReading.setText("Proximity Sensor Reading:" + String.valueOf(event.values[0]));
			   }
			
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_proximity, menu);
        return true;
    }
    
 
    
}
