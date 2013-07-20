package com.ennoviabd.app.earthquakedemo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class Earthquake extends Activity {
	EarthquakeReceiver receiver;
	ListView earthquakeListView;
	ArrayAdapter<Quake> aa;
	ArrayList<Quake> earthquakes = new ArrayList<Quake>();
	static final private int QUAKE_DIALOG = 1;
	Quake selectedQuake;
	int minimumMagnitude = 0;
	boolean autoUpdate = false;
	int updateFreq = 0;
	public static final int NOTIFICATION_ID = 1;
	NotificationManager ntmf;
	// service binding
	private EarthQuakeService serviceBinder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		earthquakeListView =(ListView)findViewById(R.id.earthquakeListView);
		earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int _index,
					long arg3) {
				// TODO Auto-generated method stub
				selectedQuake = earthquakes.get(_index);
				showDialog(QUAKE_DIALOG);
				
			}
		});
		//loadQuakesFromProvider();
		
		int layoutID = android.R.layout.simple_list_item_1;
		aa = new ArrayAdapter<Quake>(this, layoutID , earthquakes);
		earthquakeListView.setAdapter(aa);
		Intent bindIntent = new Intent(Earthquake.this, EarthQuakeService.class);
		//bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
		//startService(bindIntent);
	  
	  //NotificationManager ntm=new Notific
	  
	 
	 //NotificationManager.notify(notificationRef, ntf);
	 ImageButton imb=(ImageButton)findViewById(R.id.imbNotificationTest);
	 imb.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			  int icon=R.drawable.logo;
			  String txtNotify="New Contact Number";
			  long when = 6000;
			  
			  		  
			  
			  Notification ntf=new Notification(icon,txtNotify,when);			   
			  Context context = getApplicationContext();
			  /*
			  NotificationCompat.Builder builder =new NotificationCompat.Builder(context)
	            .setSmallIcon(icon).setContentTitle("Notifications Example").setContentText(txtNotify);
			  
			  */
			  
			 // Text to display in the extended status window
			 String expandedText = "Extended status text";
			 // Title for the expanded status
			 String expandedTitle = "Notification Title";
			 int notificationRef = 1;
			 
			 // Intent to launch an activity when the extended text is clicked	
			// Intent intent=new Intent(Earthquake.this, Earthquake.class);
			 Intent intent = new Intent();
			 
			 intent.setComponent(new ComponentName("com.ennoviabd.generalservice","com.ennoviabd.generalservice.MyEvent"));
			// Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(myBlog));
			 PendingIntent pendingIntent= PendingIntent.getActivity(Earthquake.this,     0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
			 //builder.setContentIntent(pendingIntent);
			 
			 //Intent intent = new Intent(context, "com.ennoviabd.myservices");
			// PendingIntent launchIntent = PendingIntent.getActivity(context, 0, intent, 0);
			 //ntf.setLatestEventInfo(context,expandedTitle, expandedText, launchIntent);
			/* ntf.contentIntent = pendingIntent;
		        // Now set up notification flags
			 ntf.flags |= Notification.FLAG_NO_CLEAR;
			 ntf.flags |= Notification.FLAG_ONGOING_EVENT;
			 ntf.flags |= Notification.FLAG_FOREGROUND_SERVICE;
			 ntf.number++;
			 */
			// ntf.contentIntent = pendingIntent;
		        // Now set up notification flags
			 ntf.flags = Notification.FLAG_AUTO_CANCEL;
			ntf.setLatestEventInfo(context,expandedTitle, expandedText, pendingIntent);
			ntmf =    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			//ntmf.notify(0,ntf);
			ntmf.notify(0, ntf);
			//startActivity(intent);
			
			
			
		}
	});
		
		
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch(id) {
		case (QUAKE_DIALOG) :
		LayoutInflater li = LayoutInflater.from(this);
		View quakeDetailsView = li.inflate(R.layout.quake_details, null);
		AlertDialog.Builder quakeDialog = new AlertDialog.Builder(this);
		quakeDialog.setTitle("Quake Time");
		quakeDialog.setView(quakeDetailsView);
		return quakeDialog.create();
		}
		return null;
	}
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		switch(id) {
		case (QUAKE_DIALOG) :
		SimpleDateFormat sdf;
		sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateString = sdf.format(selectedQuake.getDate());
		String quakeText = "Mangitude " + selectedQuake.getMagnitude() +
		"\n" + selectedQuake.getDetails() + "\n" +
		selectedQuake.getLink();
		AlertDialog quakeDialog = (AlertDialog)dialog;
		quakeDialog.setTitle(dateString);
		TextView tv =(TextView)quakeDialog.findViewById(R.id.quakeDetailsTextView);
		tv.setText(quakeText);
		break;
		}
	}
	public class EarthquakeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
		loadQuakesFromProvider();
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		IntentFilter filter;
		filter = new IntentFilter(EarthQuakeService.NEW_EARTHQUAKE_FOUND);
		receiver = new EarthquakeReceiver();
		registerReceiver(receiver, filter);
		loadQuakesFromProvider();
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onPause();
	}
	private void addQuakeToArray(Quake _quake) {
		if (_quake.getMagnitude() > minimumMagnitude) {
		// Add the new quake to our list of earthquakes.
		earthquakes.add(_quake);
		// Notify the array adapter of a change.
		aa.notifyDataSetChanged();
		}
	}
	
	private void loadQuakesFromProvider() {
		// Clear the existing earthquake array
		earthquakes.clear();
		ContentResolver cr = getContentResolver();
		// Return all the saved earthquakes
		Cursor c = cr.query(EarthquakeProvider.CONTENT_URI,
		null, null, null, null);
		if (c.moveToFirst())
		{
			do 
			{
			// Extract the quake details.
			Long datems = c.getLong(EarthquakeProvider.DATE_COLUMN);
			String details;
			details = c.getString(EarthquakeProvider.DETAILS_COLUMN);
			Float lat = c.getFloat(EarthquakeProvider.LATITUDE_COLUMN);
			Float lng = c.getFloat(EarthquakeProvider.LONGITUDE_COLUMN);
			Double mag = c.getDouble(EarthquakeProvider.MAGNITUDE_COLUMN);
			String link = c.getString(EarthquakeProvider.LINK_COLUMN);
			Location location = new Location("dummy");
			location.setLongitude(lng);
			location.setLatitude(lat);
			Date date = new Date(datems);
			Quake q = new Quake(date, details, location, mag, link);
			addQuakeToArray(q);
			} while(c.moveToNext());
		}
		c.close();
		}
	private ServiceConnection mConnection= new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			serviceBinder = null;			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			// Called when the connection is made.
			serviceBinder = ((EarthQuakeService.MyBinder)service).getService();
		}
	};
	

}
