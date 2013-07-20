package com.ennoviabd.generalservice;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;



public class MyService extends Service {
	int counter=0;
	static private int restart=0,updateTimer=0;
	static private int UPDATE_INTERVAL=5000;
	
	private Timer tm=new Timer();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		//Toast.makeText(this,"AK Service has been started",Toast.LENGTH_LONG).show();
		restart=0;
		startSchedular();
		
		return START_STICKY;
	}
	public void doCheckList()
	{
		tm.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//Log.d("MyService", String.valueOf(++counter)+" last Update:"+String.valueOf(UPDATE_INTERVAL));
				if(restart==0)
				{
				doCheckSchedule();
				
				}
				else if(restart==1)
				{
				restart=0;
				}
				
			}
		}, 0, UPDATE_INTERVAL);
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(tm!=null)
		{
			tm.cancel();
		}
		
		Toast.makeText(this, "Service destroy", Toast.LENGTH_LONG).show();
		
	}
/*
 * This function search database and check schedule and send the widget by broadcasting message
 */
	private void doCheckSchedule()
	{
		String daytimealert1="",daytimealert2="",olddaytimealert1="",olddaytimealert2="",
		timealert1="",timealert2="",oldtimealert1="",oldtimealert2="";
		updateTimer=0;
		Uri allEventsDateTime = Uri.parse(ChargeMe.Content_V_DateTimeList_Uri.toString());
		Uri allEventsTime = Uri.parse(ChargeMe.Content_V_TimeList_Uri.toString());
		Cursor cDateTime= getContentResolver().query(allEventsDateTime, null,null, null, null);
		Cursor cTime= getContentResolver().query(allEventsTime, null,null, null, null);
		
		if (cDateTime != null ) {
		    if  (cDateTime.moveToFirst()) {
		        do {
		        	int i=Integer.parseInt(cDateTime.getString(cDateTime.getColumnIndex("d4")));
		        	if(i<=0)
		        	{
		        		updateTimer=1;
		        		UPDATE_INTERVAL=(java.lang.Math.abs(i)*60*990)>0?(java.lang.Math.abs(i)*60*990):59000;
		        		restart=1;
		        		
		        		daytimealert2=daytimealert1;
		        		daytimealert1 = cDateTime.getString(cDateTime.getColumnIndex(ChargeMe.EVENT_IDN))+"@#"+cDateTime.getString(cDateTime.getColumnIndex(ChargeMe.EVENT_CATAGORY))+"@#"+
		        				GlobalSettings.stringToDateDbToPr(cDateTime.getString(cDateTime.getColumnIndex(ChargeMe.EVENT_FROMDT)));		        		
		        	}
		        	else
		        	{
		        		if(updateTimer==0)
		        		{
		        			UPDATE_INTERVAL=(java.lang.Math.abs(i)*60*990)>0?(java.lang.Math.abs(i)*60*990):59000;
		        			updateTimer=1;
		        			restart=1;
		        		}
		        		olddaytimealert2=olddaytimealert1;
		        		olddaytimealert1 = cDateTime.getString(cDateTime.getColumnIndex(ChargeMe.EVENT_IDN))+"@#"+cDateTime.getString(cDateTime.getColumnIndex(ChargeMe.EVENT_CATAGORY))+"@#"+
		        				GlobalSettings.stringToDateDbToPr(cDateTime.getString(cDateTime.getColumnIndex(ChargeMe.EVENT_FROMDT)));
		        		
		        	}
		        			          
		            
		        }while (cDateTime.moveToNext());
		    }
		}
		
		cDateTime.close();
		if (cTime != null ) {
		    if  (cTime.moveToFirst()) {
		        do {
		        	int i=Integer.parseInt(cTime.getString(cTime.getColumnIndex("d4")));
		        	if(i<=0)
		        	{
		        		if((UPDATE_INTERVAL>(java.lang.Math.abs(i)*60*1000))&&updateTimer==1)
		        		{
		        			UPDATE_INTERVAL=(java.lang.Math.abs(i)*60*990)>0?(java.lang.Math.abs(i)*60*990):59000;
		        			restart=1;
		        			
		        		}
		        		else if(updateTimer==0)
		        		{
		        			UPDATE_INTERVAL=(java.lang.Math.abs(i)*60*990)>0?(java.lang.Math.abs(i)*60*990):59000;
		        			updateTimer=1;
		        			restart=1;
		        		}
		        		timealert2=timealert1;
		        		timealert1 = cTime.getString(cTime.getColumnIndex(ChargeMe.EVENT_IDN))+"@#"+cTime.getString(cTime.getColumnIndex(ChargeMe.EVENT_CATAGORY))+"@#"+
		        				GlobalSettings.stringToTimeStr(cTime.getString(cTime.getColumnIndex(ChargeMe.EVENT_FROMTM)));		        		
		        	}
		        	else
		        	{
		        		if(updateTimer==0)
		        		{
		        			UPDATE_INTERVAL=(java.lang.Math.abs(i)*60*990)>0?(java.lang.Math.abs(i)*60*990):59000;
		        			updateTimer=1;
		        			restart=1;
		        		}
		        		oldtimealert2=oldtimealert1;
		        		oldtimealert1 = cTime.getString(cTime.getColumnIndex(ChargeMe.EVENT_IDN))+"@#"+cTime.getString(cTime.getColumnIndex(ChargeMe.EVENT_CATAGORY))+"@#"+
		        				GlobalSettings.stringToTimeStr(cTime.getString(cTime.getColumnIndex(ChargeMe.EVENT_FROMTM)));
		        		
		        	}
		        			          
		            
		        }while (cTime.moveToNext());
		    }
		}
		
		cTime.close();
		//Toast.makeText(this,"Next Running Time:"+UPDATE_INTERVAL,Toast.LENGTH_LONG).show();
		if(daytimealert1.length()>0 || timealert1.length()>0||olddaytimealert1.length()>0 || oldtimealert1.length()>0)
		{
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.ennoviabd.generalservice","com.ennoviabd.generalservice.MyWidgetProvider"));
		intent.setAction("ACTION_APPWIDGET_UPDATE_MYSERVICE");
	    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, 2);
	    intent.putExtra("d1", daytimealert1);
	    intent.putExtra("d2", daytimealert2);
	    intent.putExtra("oldd1", olddaytimealert1);
	    intent.putExtra("oldd2", olddaytimealert2);
	    intent.putExtra("t1", timealert1);
	    intent.putExtra("t2", timealert2);
	    intent.putExtra("oldt1", oldtimealert1);
	    intent.putExtra("oldt2", oldtimealert2);
	    sendBroadcast(intent);	    
	    reStartTimer();
	    
		}		
	}
	/*
	 * This function handle service restart logic. it restart if time is not available
	 */
	private void reStartTimer()
	{
		if(restart>0)
		{
			
			if(tm!=null)
			{
				tm.cancel();
			}
			tm=new Timer();
			doCheckList();// check schedule and update widget			
		}
	}
	/*
	 * This function call initially and each time when event information is saved. It handles service start mechanism
	 */
	private void startSchedular()
	{
		if(tm!=null)
		{
			tm.cancel();
		}
		tm=new Timer();
		doCheckList();
	}
}