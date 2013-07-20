package com.ennoviabd.generalservice;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;


public class Tabtest extends TabActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabtest);
		TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);  // The activity TabHost
		TabSpec tabsp1=tabHost.newTabSpec("m1");
		TabSpec tabsp2=tabHost.newTabSpec("m2");
		/* TabSpec setIndicator() is used to set name for the tab. */
        /* TabSpec setContent() is used to set content for a particular tab. */
        tabsp1.setIndicator("",getResources().getDrawable(R.drawable.tab_calendar_ico)).setContent(new Intent(this,CalendarActivity.class));
        tabsp2.setIndicator("",getResources().getDrawable(R.drawable.tab_schedular_list_ico)).setContent(new Intent(this,MainActivity.class));
        
        /* Add tabSpec to the TabHost to display. */
        tabHost.addTab(tabsp1);
       tabHost.addTab(tabsp2);	
        
        //int totalTabs = tabHost.getTabWidget().getChildCount();
       
        //tabHost.getTabWidget().getChildAt(totalTabs-1).getLayoutParams().height = 40;
        //tabHost.getTabWidget().getChildAt(totalTabs-2).getLayoutParams().height = 40;
        
        
        }
	

}
