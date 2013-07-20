package com.ennoviabd.app.acclerometerdemo;

import com.ennoviabd.app.acclerometerdemo.ShakeDetector.OnShakeListener;

import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
	SensorManager cSensorManager;
	Sensor cAccelerometer;
	ShakeDetector sd;
	boolean loaded = false;
	SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	int sound;
	private final float NOISE = (float) 2.0;
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	float currentVolume=0.0f,maxVolume=0.0f;
	 AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*
		mInitialized = false;
		cSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		cAccelerometer = cSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		cSensorManager.registerListener(mySensorListener, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
		*/
		
		initVolume();
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		sound=soundPool.load(getApplicationContext(), R.raw.dr1, 1);
		((TextView)findViewById(R.id.txtDemoAccelerometer)).setText("My Name is Speedometer");
		cSensorManager= (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		cAccelerometer=cSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sd=	new ShakeDetector(new OnShakeListener() {
			
			@Override
			public void onShake() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Shake 11d",Toast.LENGTH_SHORT).show();
				 AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		            float actualVolume = (float) audioManager
		                    .getStreamVolume(AudioManager.STREAM_MUSIC);
		            float maxVolume = (float) audioManager
		                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		            currentVolume = actualVolume / maxVolume;
		            // Is the sound loaded already?
		            if (loaded) {
		                soundPool.play(sound, currentVolume, currentVolume, 1, 0, 1f);
		               
		            }
				
			}
		});
		//sm.registerListener(mySensorListener, SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_FASTEST);
		//sm.registerListener(myOrientationListener, SensorManager.SENSOR_ORIENTATION,SensorManager.SENSOR_DELAY_NORMAL);
		
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				// TODO Auto-generated method stub
				loaded=true;
				
			}
		});
		
		Button btnPlay=(Button)findViewById(R.id.btnPlaySound);
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
		            //currentVolume = actualVolume / maxVolume;
		            // Is the sound loaded already?
		            if (loaded) {
		                soundPool.play(sound, currentVolume/maxVolume,currentVolume/maxVolume , 1, 0, 1f);
		               
		            }
				
			}
		});
		
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
		  GCMRegistrar.register(this, "614716004374");
		  Toast.makeText(getApplicationContext(), "registering...", Toast.LENGTH_SHORT).show();
		} 
		else 
		{
			if (GCMRegistrar.isRegisteredOnServer(this)) 
			{
                // Skips registration.
                Toast.makeText(getApplicationContext(), "Already Registered", Toast.LENGTH_SHORT).show();
            } 
			else {
		  //Log.v("GVM Service", "Already registered");
				// Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = getApplicationContext();
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerUtilities.register(context, regId);
                        Toast.makeText(context, regId, Toast.LENGTH_SHORT).show();
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            
            }
		}
		
		
	}
	
	protected void onResume() {
		 super.onResume();
	     cSensorManager.registerListener(sd, cAccelerometer, SensorManager.SENSOR_DELAY_UI);
	     GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);
			final String regId = GCMRegistrar.getRegistrationId(this);
			if (regId.equals("")) {
			  GCMRegistrar.register(this, "614716004374");
			  Toast.makeText(getApplicationContext(), "registering...", Toast.LENGTH_SHORT).show();
			}
		// cSensorManager.registerListener(mySensorListener, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL); 
		
	};
	protected void onPause() {
		
	        super.onPause();
	        cSensorManager.unregisterListener(sd);
	       // finish();
	        //cSensorManager.unregisterListener(mySensorListener);
		
	};
	SensorListener mySensorListener=new SensorListener() {
		
		@Override
		public void onSensorChanged(int sensor, float[] values) {
			// TODO Auto-generated method stub
			if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
				/*
				float xAxis = values[SensorManager.DATA_X];
				float yAxis = values[SensorManager.DATA_Y];
				float zAxis = values[SensorManager.DATA_Z];
				float raw_xAxis = values[SensorManager.RAW_DATA_X];
				float raw_yAxis = values[SensorManager.RAW_DATA_Y];
				float raw_zAxis = values[SensorManager.RAW_DATA_Z];
				// TODO apply the acceleration changes to your application.
				((TextView)findViewById(R.id.txtDemoAccelerometer)).setText("x:"+xAxis+" y:"+yAxis+" z:"+zAxis);
				((TextView)findViewById(R.id.txtDemoAccelerometer2)).setText("xx:"+raw_xAxis+" yy:"+raw_yAxis+" zz:"+raw_zAxis);
				*/
				TextView tvX= (TextView)findViewById(R.id.x_axis);
				TextView tvY= (TextView)findViewById(R.id.y_axis);
				TextView tvZ= (TextView) findViewById(R.id.z_axis);
				ImageView iv = (ImageView)findViewById(R.id.imgCustom);
				float x = values[SensorManager.DATA_X];
				float y = values[SensorManager.DATA_Y];
				float z = values[SensorManager.DATA_Z];
				if (!mInitialized) {
				mLastX = x;
				mLastY = y;
				mLastZ = z;
				tvX.setText("0.0");
				tvY.setText("0.0");
				tvZ.setText("0.0");
				mInitialized = true;
				} else {
				float deltaX = Math.abs(mLastX - x);
				float deltaY = Math.abs(mLastY - y);
				float deltaZ = Math.abs(mLastZ - z);
				if (deltaX < NOISE) deltaX = (float)0.0;
				if (deltaY < NOISE) deltaY = (float)0.0;
				if (deltaZ < NOISE) deltaZ = (float)0.0;
				mLastX = x;
				mLastY = y;
				mLastZ = z;
				tvX.setText(Float.toString(deltaX));
				tvY.setText(Float.toString(deltaY));
				tvZ.setText(Float.toString(deltaZ));
				iv.setVisibility(View.VISIBLE);
				if (deltaX > deltaY) {
				iv.setImageResource(R.drawable.shaker_hr);
				} else if (deltaY > deltaX) {
				iv.setImageResource(R.drawable.shaker_vr);
				} else {
				iv.setVisibility(View.INVISIBLE);
				}
				}
				
				}
			
		}		
		
		@Override
		public void onAccuracyChanged(int sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	SensorListener myOrientationListener=new SensorListener() {
		
		@Override
		public void onSensorChanged(int sensor, float[] values) {
			// TODO Auto-generated method stub
			if (sensor == SensorManager.SENSOR_ORIENTATION) {
				float rollAngle = values[SensorManager.DATA_X];
				float pitchAngle = values[SensorManager.DATA_Y];
				float headingAngle = values[SensorManager.DATA_Z];
				float raw_rollAngle = values[SensorManager.RAW_DATA_X];
				float raw_pitchAngle= values[SensorManager.RAW_DATA_Y];
				float raw_headingAngle = values[SensorManager.RAW_DATA_Z];
				// TODO apply the orientation changes to your application.
				((TextView)findViewById(R.id.txtDemoAccelerometer3)).setText("x:"+rollAngle+" y:"+pitchAngle+" z:"+headingAngle);
				
				}
			
		}
		
		@Override
		public void onAccuracyChanged(int sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//return super.onKeyDown(keyCode, event);
		switch (keyCode) {
	    case KeyEvent.KEYCODE_VOLUME_UP:
	       currentVolume++;
	        Toast.makeText(getApplicationContext()," Current Volume:"+ currentVolume+" Max Volume:"+maxVolume, Toast.LENGTH_SHORT).show();
	        if(currentVolume>=maxVolume)
	        {
	        	currentVolume=maxVolume;
	        }
	        return true;
	    case KeyEvent.KEYCODE_VOLUME_DOWN:
	    	currentVolume--;
	    	Toast.makeText(getApplicationContext()," Current Volume:"+ currentVolume+" Max Volume:"+maxVolume, Toast.LENGTH_SHORT).show();
	        if(currentVolume<1)
	        {
	        	currentVolume=1;
	        }
	        return true;
	    default:
	    	return super.onKeyDown(keyCode, event);
	    	
	    }
	}

	private void initVolume()
	{
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		 float actualVolume = (float) audioManager
                   .getStreamVolume(AudioManager.STREAM_MUSIC);
           maxVolume = (float) audioManager
                   .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
           currentVolume=actualVolume;
           Toast.makeText(getApplicationContext()," Current Volume:"+ currentVolume+" Mamimum Volume:"+maxVolume+" acutal/max: "+actualVolume/maxVolume, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
    	{
    	case R.id.action_settings:
    		finish();
    		return true;
    	case R.id.action_clear:
    		GCMRegistrar.unregister(getApplicationContext());
    		return true;
    	default:
    	return super.onOptionsItemSelected(item);
    	}
	}
}
