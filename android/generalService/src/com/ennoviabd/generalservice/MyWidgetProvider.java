package com.ennoviabd.generalservice;

import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MyWidgetProvider extends AppWidgetProvider {
	private static final String ACTION_CLICK = "ACTION_CLICK";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		//super.onUpdate(context, appWidgetManager, appWidgetIds);
		// Get all ids
		
	    ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
	    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	    for (int widgetId : allWidgetIds) {
	      // Create some random data
	      int number = (new Random().nextInt(100));

	      RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
	     // Log.w("Book Bind Widget", String.valueOf(number));
	      // Set the text
	      //remoteViews.setTextViewText(R.id.txtWidgetdt1, "");
	      //remoteViews.setTextViewText(R.id.txtWidgetdt2, "");
	      //remoteViews.setTextViewText(R.id.txtWidgett1, "");	      	      

	      // Register an onClickListener
	      /*
	      Intent intent = new Intent(context, MyWidgetProvider.class);

	      intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

	      PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
	          0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	      remoteViews.setOnClickPendingIntent(R.id.txtWidgetdt1, pendingIntent);
	      */
	      Intent tmpintent = new Intent(context,MainActivity.class);
     	   PendingIntent pendingIntent = PendingIntent.getActivity(context,
   	          0, tmpintent, PendingIntent.FLAG_UPDATE_CURRENT);
   	      remoteViews.setOnClickPendingIntent(R.id.txtAllSchedule, pendingIntent);      	    
          appWidgetManager.updateAppWidget(widgetId, remoteViews);
		
	}
	    
}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Toast.makeText(context,intent.getAction(),Toast.LENGTH_LONG).show();
		  
		 if(intent.getAction().equalsIgnoreCase("ACTION_APPWIDGET_UPDATE_MYSERVICE")){
             //THE CODE BELOW LEADS TO AN EXCEPTION. HOW CAN I UPDATE THE WIDGET HERE?
			 AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			 ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
		   	    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
			//Toast.makeText(context,intent.getStringExtra("d1"),Toast.LENGTH_LONG).show();
			 for (int widgetId : allWidgetIds) {
             RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
           
             //int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
   	      // Register an onClickListener
             //Toast.makeText(context,intent.getAction().toString(),Toast.LENGTH_LONG).show();
             

             String tmpD1,tmpD2,tempT1;
             if(intent.getStringExtra("d1").length()>0)
             {
            	 tmpD1=intent.getStringExtra("d1");
            	 String idn=tmpD1.substring(0, tmpD1.indexOf("@#"));
            	 tmpD1=tmpD1.substring(tmpD1.indexOf("@#")+2);
            	 String tmpCatagory=tmpD1.substring(0, tmpD1.indexOf("@#"));
            	 tmpD1=tmpD1.substring(tmpD1.indexOf("@#")+2);
            	 String tmpEventDT=tmpD1;
            	 remoteViews.setTextViewText(R.id.txtWidgetdt1,tmpCatagory+": "+tmpEventDT);
            	 
            	 Intent tmpintent = new Intent(context,MyEvent.class);
            	 tmpintent.putExtra("_id", idn);
            	 PendingIntent pendingIntent = PendingIntent.getActivity(context,
          	          0, tmpintent, PendingIntent.FLAG_UPDATE_CURRENT);
          	      remoteViews.setOnClickPendingIntent(R.id.txtWidgetdt1, pendingIntent);
            	 
             }
             if(intent.getStringExtra("d2").length()>0)
             {
            	 tmpD1=intent.getStringExtra("d2");
            	 String idn=tmpD1.substring(0, tmpD1.indexOf("@#"));
            	 tmpD1=tmpD1.substring(tmpD1.indexOf("@#")+2);
            	 String tmpCatagory=tmpD1.substring(0, tmpD1.indexOf("@#"));
            	 tmpD1=tmpD1.substring(tmpD1.indexOf("@#")+2);
            	 String tmpEventDT=tmpD1;
            	 remoteViews.setTextViewText(R.id.txtWidgetdt2,tmpCatagory+": "+tmpEventDT);
            	 
            	 Intent tmpintent = new Intent(context,MyEvent.class);
            	 tmpintent.putExtra("_id", idn);
         	      PendingIntent pendingIntent = PendingIntent.getActivity(context,
          	          0, tmpintent, PendingIntent.FLAG_UPDATE_CURRENT);
          	      remoteViews.setOnClickPendingIntent(R.id.txtWidgetdt2, pendingIntent);
            	 
             }
             if(intent.getStringExtra("t1").length()>0)
             {
            	 tmpD1=intent.getStringExtra("t1");
            	 String idn=tmpD1.substring(0, tmpD1.indexOf("@#"));
            	 tmpD1=tmpD1.substring(tmpD1.indexOf("@#")+2);
            	 String tmpCatagory=tmpD1.substring(0, tmpD1.indexOf("@#"));
            	 tmpD1=tmpD1.substring(tmpD1.indexOf("@#")+2);
            	 String tmpEventDT=tmpD1;
            	 remoteViews.setTextViewText(R.id.txtWidgett1, tmpCatagory+": "+tmpEventDT);
            	 Intent tmpintent = new Intent(context,MyEvent.class);
            	 tmpintent.setAction("Call Event");
            	 tmpintent.putExtra("_id", idn);
         	      PendingIntent pendingIntent = PendingIntent.getActivity(context,
          	          0, tmpintent, PendingIntent.FLAG_UPDATE_CURRENT);
          	      remoteViews.setOnClickPendingIntent(R.id.txtWidgett1, pendingIntent);
             }              
              
             Intent tmpintent = new Intent(context,MainActivity.class);
        	 //tmpintent.setAction("Event Call");
        	     PendingIntent pendingIntent = PendingIntent.getActivity(context,
      	          0, tmpintent, PendingIntent.FLAG_UPDATE_CURRENT);
      	      remoteViews.setOnClickPendingIntent(R.id.txtAllSchedule, pendingIntent);      	    
             appWidgetManager.updateAppWidget(widgetId, remoteViews);
			 }
         }

            super.onReceive(context, intent);
	}
}
