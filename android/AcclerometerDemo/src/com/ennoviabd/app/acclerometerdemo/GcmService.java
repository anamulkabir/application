package com.ennoviabd.app.acclerometerdemo;


import com.google.android.gcm.GCMBaseIntentService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;


public class GcmService extends GCMBaseIntentService {
	@Override
	protected void onRegistered(Context context, String registrationId) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "Registration Success",Toast.LENGTH_SHORT).show();
		ServerUtilities.register(context, registrationId);
		
	}
	public GcmService()
	{
		super("614716004374");
		Toast.makeText(getApplicationContext(), "614716004374",Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "Unregistration",Toast.LENGTH_SHORT).show();
		ServerUtilities.unregister(context, registrationId);		
		
	}
	@Override
	protected void onMessage(Context arg0, Intent intent) {
		// TODO Auto-generated method stub
		 String message = "Message From GCM Server:";
	        message+="Original Message: "+intent.getStringExtra("message").toString();
	        
	        // notifies user
	        generateNotification(getApplication(), message);
		Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
		
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
	 /**
     * Issues a notification to inform the user that server has sent a message.
     */
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
