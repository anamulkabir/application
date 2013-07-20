package com.ennoviabd.app.gcmtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {
	public GCMIntentService()
	{
		super("614716004374");
	}
	@Override
	protected void onRegistered(Context context, String registerId) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "Registering to server...", Toast.LENGTH_SHORT).show();
		ServerUtilities.register(context, registerId);
				
	}
	@Override
	protected void onUnregistered(Context context, String registrerId) {
		// TODO Auto-generated method stub
		if(GCMRegistrar.isRegistered(context))
		{
			Toast.makeText(context, "Server Unregistration...", Toast.LENGTH_SHORT).show();
			ServerUtilities.unregister(context, registrerId);
		}
		else
		{
			Toast.makeText(context, "Ignore Unreg", Toast.LENGTH_SHORT).show();
		}
		
	}
	@Override
	protected void onMessage(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 String message = "Message From GCM Server:";
	        message+="Original Message: "+intent.getStringExtra("message").toString();
	        
	        // notifies user
	        generateNotification(getApplication(), message);
		
	}
	@Override
	protected void onDeletedMessages(Context context, int total) {
		// TODO Auto-generated method stub
		//super.onDeletedMessages(context, total);
		String message="GCM Server Deleted"+total;
		generateNotification(context, message);
	}
	
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// TODO Auto-generated method stub
		return super.onRecoverableError(context, errorId);
	}
	 private static void generateNotification(Context context, String message) {
	        int icon = R.drawable.ic_launcher;
	        
	        long when = System.currentTimeMillis();
	        NotificationManager notificationManager = (NotificationManager)
	                context.getSystemService(Context.NOTIFICATION_SERVICE);
	        Notification notification = new Notification(icon, message, when);
	        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	        String title = context.getString(R.string.app_name);
	        Intent notificationIntent = new Intent(context, MainActivity.class);
	        // set intent so it does not start a new activity
	        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        PendingIntent intent =
	                PendingIntent.getActivity(context, 0, notificationIntent, 0);
	        notification.setLatestEventInfo(context, title, message, intent);
	        notification.flags |= Notification.FLAG_AUTO_CANCEL;
	        notification.sound = soundUri;
	        //notification.defaults |= Notification.DEFAULT_SOUND;       
	        notificationManager.notify(0, notification);
	    }

}
