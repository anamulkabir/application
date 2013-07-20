package com.ennoviabd.generalservice;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.text.Editable;
import android.text.GetChars;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.content.ContentProvider;
import android.content.Context;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;


public class MainActivity extends  Activity {

	CustomCursorAdapter eventDataAdapter;
	ListView lvEvent;
	String[] columns = new String[] {ChargeMe.EVENT_NOTE,ChargeMe.EVENT_FROMDT,ChargeMe.EVENT_IDN};
	int[] views = new int[] {R.id.lstAlertnote,R.id.lstAlertDT,R.id.lstEventid};
	EditText txtDTFilter,txtEventFilter;
	String filterType="0";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		txtDTFilter=(EditText)findViewById(R.id.txtDateFilter);
		txtEventFilter=(EditText)findViewById(R.id.txtEventFilter);
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		txtDTFilter.setText(sdf.format(new Date()));
		Intent myIntent= getIntent(); // gets the previously created intent
		if(myIntent.getStringExtra(ChargeMe.EVENT_FROMDT)!=null) 
		{
			txtDTFilter.setText(GlobalSettings.stringToDateDbToPr(myIntent.getStringExtra(ChargeMe.EVENT_FROMDT)));
			loadFilterType();
			
		}
		Button btn= (Button)findViewById(R.id.btnAddEvent);
		btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				startActivity(new Intent(MainActivity.this,MyEvent.class));
				
				
			}
		});
		
				
		btn=(Button)findViewById(R.id.btnClose);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//stopService(new Intent(getBaseContext(),MyService.class));
				finish();
			}
		});
		btn=(Button)findViewById(R.id.btnDT);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadFilterType();								
			}
		});
		btn=(Button)findViewById(R.id.btnSearch);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(filterType.equalsIgnoreCase("1"))
				{
					if(GlobalSettings.stringToDatePrToDb(txtDTFilter.getText().toString()).length()>2)
					{
						Uri allEvents = Uri.parse(ChargeMe.Content_Event_Uri.toString());
						//lvEvent.clearTextFilter();
						 Cursor c= managedQuery(allEvents, null,ChargeMe.EVENT_FROMDT+" = '"+GlobalSettings.stringToDatePrToDb(txtDTFilter.getText().toString())+"'", null, null);
						 eventDataAdapter.setCursor(c);
						 eventDataAdapter.changeCursor(c);
						 eventDataAdapter.notifyDataSetChanged();						 
						
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Invalid Date formation, Use[dd-mm-yyyy]", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Uri allEvents = Uri.parse(ChargeMe.Content_Event_Uri.toString());
					 Cursor c= managedQuery(allEvents, null,ChargeMe.EVENT_NOTE+" like '%"+txtEventFilter.getText().toString()+"%'", null, null);
					 eventDataAdapter.setCursor(c);
					 eventDataAdapter.changeCursor(c);
					 eventDataAdapter.notifyDataSetChanged();
					
				}
				
				
				
			}
		});
		
		
		lvEvent=(ListView)findViewById(R.id.lstEvent);
		
		Uri allEvents = Uri.parse(ChargeMe.Content_Event_Uri.toString());
		try{
			// Cursor to get all event information from the event content provider
			// it returns database cursor pointer
			Cursor c=null;
			if(filterType.equalsIgnoreCase("1"))
			{
				c= managedQuery(allEvents, null,ChargeMe.EVENT_FROMDT+" = '"+GlobalSettings.stringToDatePrToDb(txtDTFilter.getText().toString())+"'", null, null);
			}
			else
			{
				c= managedQuery(allEvents, null, null, null, null);	
			}
						
			// set layout, cursor , columns and values of cursoradapter
			eventDataAdapter =new CustomCursorAdapter(this, R.layout.list_main, c, columns, views);
			
			// binding list view
			lvEvent.setAdapter(eventDataAdapter);
			
			
			eventDataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
				
				@Override
				public Cursor runQuery(CharSequence constraint) {
					// TODO Auto-generated method stub  
					Uri allEvents = Uri.parse(ChargeMe.Content_Event_Uri.toString());					
									
					 Cursor c= managedQuery(allEvents, null,ChargeMe.EVENT_NOTE+" like '%"+constraint+"%'", null, null);
					 eventDataAdapter.setCursor(c);
					  return c;
					
				}
			});
			
			lvEvent.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> listView, View arg1,
						int position, long id) {
					// TODO Auto-generated method stub
					 // Get the cursor, positioned to the corresponding row in the result set
					   Cursor cursor = (Cursor) listView.getItemAtPosition(position);
					 
					   // Get the state's capital from this row in the database.
					   String eventIdn =cursor.getString(cursor.getColumnIndexOrThrow(ChargeMe.EVENT_IDN));
					   Intent eventIntent = new Intent(MainActivity.this,MyEvent.class);
					   eventIntent.putExtra(ChargeMe.EVENT_IDN,eventIdn);					   
					   startActivity(eventIntent);					
				}
			});
			
			EditText myFilter = (EditText) findViewById(R.id.txtEventFilter);
			myFilter.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
					eventDataAdapter.getFilter().filter(s.toString());					
					
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
			
			
		}catch(Exception ex)
		{
			
		}
		
	}
	private void loadFilterType()
	{
		if(filterType.equalsIgnoreCase("0"))
		{
			txtDTFilter.setVisibility(View.VISIBLE);
			txtEventFilter.setVisibility(View.GONE);	
			((Button)findViewById(R.id.btnDT)).setBackgroundResource(R.drawable.ic_filterevent);
			filterType="1";

			
		}else
		{
			txtDTFilter.setVisibility(View.GONE);
			txtEventFilter.setVisibility(View.VISIBLE);					
			((Button)findViewById(R.id.btnDT)).setBackgroundResource(R.drawable.ic_dt);
			filterType="0";
		}

	}
	
		
}
