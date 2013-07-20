package com.ennoviabd.app.gcmtest;


import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.google.android.gcm.GCMRegistrar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends SherlockActivity {
	AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);		
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);		
		setContentView(R.layout.activity_main);
		((LinearLayout)findViewById(R.id.custom_buttom)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Click Taped", Toast.LENGTH_SHORT).toString();
				
			}
		});
		registerReceiver(myreceiver, new IntentFilter("com.ennoviabd.app.gcmtest.message"));
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
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		 if (mRegisterTask != null) {
	            mRegisterTask.cancel(true);
	        }
	        unregisterReceiver(myreceiver);
	        GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}
	

@Override
public boolean onCreateOptionsMenu(Menu menu) {
	// TODO Auto-generated method stub
	MenuInflater inflater=getSupportMenuInflater();
	inflater.inflate(R.menu.main, menu);
	return true;
}
	private final BroadcastReceiver myreceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			String newMessage = intent.getExtras().getString("message");
			Toast.makeText(context, newMessage, Toast.LENGTH_SHORT).show();
		}
	};

}
