package com.ennoviabd.generalservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastreceiver  extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction() != null)
    	{
    		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
    		{
    			context.startService(new Intent(context, MyService.class));
    		}
    		if(intent.getAction().equals("SERVICE_RESTART"))
    		{
    			context.startService(new Intent(context, MyService.class));
    		}
    		
    	}
		
	}

}
