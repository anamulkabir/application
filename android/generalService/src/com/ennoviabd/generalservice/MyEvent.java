package com.ennoviabd.generalservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MyEvent extends Activity {
	 int hour, minute, mYear,mMonth, mDay;
	 String eventNote,eventCatagory,eventType="",eventFromDT,eventToDT,eventFromTM,eventToTM,
			 eventDesc,eventLocation,eventRemind,eventNotification="",eventStatus,eventDuration,assignPerson;
	 String eventidn="",event_type="";
	 Cursor catagory;
	 
	 boolean uiiputs=false;
	 char activityUIStatus='I';
	    static final int TIME_DIALOG_ID = 0;
	    static final int DATE_DIALOG_ID = 1;
	    static final int TIME_DIALOG_ID_T = 2;
	    private EditText txtAlertFD;
		private EditText txtAlertTD;
		private AutoCompleteTextView txtFromTM,txtToTM;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_main);
		loadDDLCatagory();
		initUIComponent();
		updateUIComponent();
		 	
	}
		// This is a custom function to handle left padding and fill with 0
	private static String LPad(String schar, String spad, int len) {
				String sret = schar;
				for (int i = sret.length(); i < len; i++) {
					sret = spad + sret;
				}
				return new String(sret);
			}	
			
	// this funciton initialize all the component of event activity and 
	private void initUIComponent()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat stf=new SimpleDateFormat("HH:mm");
		
		txtFromTM=(AutoCompleteTextView)findViewById(R.id.txtFromTime);
		txtToTM=(AutoCompleteTextView)findViewById(R.id.txtToTime);
		txtAlertFD =(EditText)findViewById(R.id.txtFromDate);
		 txtAlertTD =(EditText)findViewById(R.id.txtToDate);		
		 txtFromTM.setThreshold(0);
		 loadDDLReminder();
		 CheckBox chkDuration=(CheckBox)findViewById(R.id.chkAllDay);
			
		    Resources rs=getResources();
		    String [] ftdt=rs.getStringArray(R.array.fromtotime);
		    ArrayAdapter<String> adapterfttm=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,ftdt);
		    txtFromTM.setAdapter(adapterfttm);
		    txtToTM.setAdapter(adapterfttm);
		    
		    txtAlertFD.setText(sdf.format(new Date()));
		    txtAlertTD.setText(sdf.format(new Date()));
		    txtFromTM.setText(stf.format(new Date()));
		    txtToTM.setText(stf.format(new Date()));
		    
		    txtAlertFD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(!hasFocus && txtAlertFD.getText().toString().length()>0)
					{
						String tmpdtc=GlobalSettings.stringToDateStrPr(txtAlertFD.getText().toString());
						if(tmpdtc.length()<=0)
						{
							txtAlertFD.setTextColor(Color.RED);
						}
						else
						{
							txtAlertFD.setText(tmpdtc);
							txtAlertFD.setTextColor(Color.DKGRAY);
						}
					}
				}
			});
		    txtAlertTD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(!hasFocus&& txtAlertTD.getText().toString().length()>0)
					{
						String tmpdtc=GlobalSettings.stringToDateStrPr(txtAlertTD.getText().toString());
						if(tmpdtc.length()<=0)
						{
							
							txtAlertTD.setTextColor(Color.RED);
						}
						else
						{
							txtAlertTD.setText(tmpdtc);
							txtAlertTD.setTextColor(Color.DKGRAY);
						}
					}
					
				}
			});
		    
		    txtFromTM.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(hasFocus) {
			            txtFromTM.showDropDown();
			        }
					
				}
			});
		    txtToTM.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(hasFocus) {
			            txtToTM.showDropDown();
			        }
				}
			});
		    
		    
		    
		// function to handle save button
		    CheckBox chkAllday=(CheckBox)findViewById(R.id.chkAllDay);
		    chkAllday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					updateUIComponent();
					
				}
			});
		Button btnSave=(Button)findViewById(R.id.eventsave);
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getAllUIValues(); // get all UI inputs and validate 
				if(uiiputs)
				{
					ContentValues cValues = new ContentValues();
					cValues.put(ChargeMe.EVENT_NOTE, eventNote);
					cValues.put(ChargeMe.EVENT_CATAGORY, eventCatagory);
					cValues.put(ChargeMe.EVENT_EVENT_TYPE,eventType);
					cValues.put(ChargeMe.EVENT_NOTIFICATION,eventNotification);
					cValues.put(ChargeMe.EVENT_FROMDT,eventFromDT);
					cValues.put(ChargeMe.EVENT_TODT, eventToDT);
					cValues.put(ChargeMe.EVENT_FROMTM,eventFromTM);
					cValues.put(ChargeMe.EVENT_TOTM, eventToTM);
					cValues.put(ChargeMe.EVENT_DESCRIPTION, eventDesc);
					cValues.put(ChargeMe.EVENT_LOCATION, eventLocation);
					cValues.put(ChargeMe.EVENT_REMIND,eventRemind);
					cValues.put(ChargeMe.EVENT_ATTACH_PERSON,assignPerson);
					cValues.put(ChargeMe.EVENT_DURATION,eventDuration);					
					cValues.put(ChargeMe.EVENT_STATUS,GlobalSettings.appstatus[0].toString());
					try{
					Uri uri ;
						if(activityUIStatus=='I')
						{
						uri= getContentResolver().insert(ChargeMe.Content_Event_Uri, cValues);
						}
						else if(activityUIStatus=='U')
						{
						int rs= getContentResolver().update(ChargeMe.Content_Event_Uri, cValues, ChargeMe.EVENT_IDN+"="+eventidn, null);
						
						}
						if(true)
						{
							setAllUIValues("0");
							Intent intent = new Intent();
							Toast.makeText(getApplicationContext(),"Event Save Successfully!",Toast.LENGTH_LONG).show();
							//intent.setComponent(new ComponentName("com.ennoviabd.app.widget","com.ennoviabd.app.widget.MyWidgetProvider"));
							intent.setAction("SERVICE_RESTART");						   
						    sendBroadcast(intent);
						}
					}
					catch(Error er)
					{
						
					}
				
				}
			}
		});
		Button btnClose=(Button)findViewById(R.id.eventClose);
		btnClose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				finish();
			}
		});
		Intent myIntent= getIntent(); // gets the previously created intent
		String eventIDN ="";
		eventIDN= myIntent.getStringExtra(ChargeMe.EVENT_IDN); 
		if(eventIDN!=null && !eventIDN.isEmpty())
		{
			loadUIData(eventIDN);
			//updateUIComponent();			
		}
		else if(myIntent.getStringExtra(ChargeMe.EVENT_FROMDT)!=null) 
		{
			txtAlertFD.setText(GlobalSettings.stringToDateDbToPr(myIntent.getStringExtra(ChargeMe.EVENT_FROMDT)));
			txtAlertTD.setText(GlobalSettings.stringToDateDbToPr(myIntent.getStringExtra(ChargeMe.EVENT_FROMDT)));
			
		}		
	}
	// this function implement logic to show UI on activity[hide, view depend on logc]
	private void updateUIComponent()
	{
		txtAlertFD.setVisibility(View.VISIBLE);
		txtAlertTD.setVisibility(View.VISIBLE);
		((Spinner)findViewById(R.id.ddlReminder)).setVisibility(View.VISIBLE);
		txtFromTM.setVisibility(View.VISIBLE);
		txtToTM.setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblFromDate)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblToDate)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblReminder)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblFromTime)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblToTime)).setVisibility(View.VISIBLE);
		((LinearLayout)findViewById(R.id.loLblFTTime)).setVisibility(View.VISIBLE);
		((LinearLayout)findViewById(R.id.loTxtFTTime)).setVisibility(View.VISIBLE);
		((LinearLayout)findViewById(R.id.loLblftdt)).setVisibility(View.VISIBLE);
		((LinearLayout)findViewById(R.id.loTxtftdt)).setVisibility(View.VISIBLE);
		
		((CheckBox)findViewById(R.id.chkAllDay)).setVisibility(View.VISIBLE);
		if(event_type.equalsIgnoreCase("day"))
		{
			//txtAlertD;
			//txtRememind;
			txtAlertTD.setVisibility(View.INVISIBLE);
			txtFromTM.setVisibility(View.INVISIBLE);
			txtToTM.setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.lblToDate)).setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.lblFromTime)).setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.lblToTime)).setVisibility(View.INVISIBLE);
			((CheckBox)findViewById(R.id.chkAllDay)).setVisibility(View.INVISIBLE);
			((LinearLayout)findViewById(R.id.loLblFTTime)).setVisibility(View.GONE);
			((LinearLayout)findViewById(R.id.loTxtFTTime)).setVisibility(View.GONE);
			
		}
		else if(event_type.equalsIgnoreCase("datetime"))
		{
			CheckBox chkDuration=(CheckBox)findViewById(R.id.chkAllDay);
			if(chkDuration.isChecked())
			{
				txtAlertTD.setVisibility(View.GONE);
				txtFromTM.setVisibility(View.GONE);
				txtToTM.setVisibility(View.GONE);
				((TextView)findViewById(R.id.lblToDate)).setVisibility(View.GONE);
				((TextView)findViewById(R.id.lblFromTime)).setVisibility(View.GONE);
				((TextView)findViewById(R.id.lblToTime)).setVisibility(View.GONE);
			
			}			
		}
		else if(event_type.equalsIgnoreCase("time"))
		{
			txtAlertFD.setVisibility(View.INVISIBLE);
			txtAlertTD.setVisibility(View.INVISIBLE);
			((Spinner)findViewById(R.id.ddlReminder)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.lblFromDate)).setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.lblToDate)).setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.lblReminder)).setVisibility(View.INVISIBLE);
			((CheckBox)findViewById(R.id.chkAllDay)).setVisibility(View.INVISIBLE);
			((LinearLayout)findViewById(R.id.loLblftdt)).setVisibility(View.GONE);
			((LinearLayout)findViewById(R.id.loTxtftdt)).setVisibility(View.GONE);
			
		}
		
	}
	// this function load data passed by parameter/ load previous saved entry data
	private void loadUIData(String idn)
	{
		Uri allEvents = Uri.parse(ChargeMe.Content_Event_Uri.toString()+"/"+idn);
		Cursor cEvent = managedQuery(allEvents, null, null, null, null);
		if(cEvent.moveToLast())
		{
			cEvent.moveToFirst();
			((EditText)findViewById(R.id.txt_note)).setText(cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_NOTE)));
			Spinner spType=(Spinner)findViewById(R.id.ddlCatagory); 
			// select spinner with save data for catagory
			SelectSpinnerItemByValue(spType,cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_CATAGORY)).toString());
			CheckBox chkDuration=(CheckBox)findViewById(R.id.chkAllDay);
			if(cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_DURATION)).length()>1)
			{
			 chkDuration.setChecked(true);
			}
			else
			{
				chkDuration.setChecked(false);
			}
			txtAlertFD.setText(GlobalSettings.stringToDateDbToPr(cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_FROMDT))));
			txtAlertTD.setText(GlobalSettings.stringToDateDbToPr(cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_TODT))));
			txtFromTM.setText(cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_FROMTM)));
			txtToTM.setText(cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_TOTM)));
			((EditText)findViewById(R.id.txtEventLocation)).setText(cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_LOCATION)));
			((EditText)findViewById(R.id.txtEventDesc)).setText(cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_DESCRIPTION)));
			//txtRememind.setText(cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_REMIND)));
			Spinner spRemind=(Spinner)findViewById(R.id.ddlReminder);
			SelectedSpinnerByValue(spRemind,cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_REMIND)).toString());
			if(cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_NOTIFICATION)).length()>0)
			{
				String str=cEvent.getString(cEvent.getColumnIndex(ChargeMe.EVENT_NOTIFICATION));
				if(str.contains("1"))
					((CheckBox)findViewById(R.id.chkMsg)).setChecked(true);
				else
					((CheckBox)findViewById(R.id.chkMsg)).setChecked(false);
				
				if(str.contains("2"))
					((CheckBox)findViewById(R.id.chkSms)).setChecked(true);
				else
					((CheckBox)findViewById(R.id.chkSms)).setChecked(false);
				
				if(str.contains("3"))
					((CheckBox)findViewById(R.id.chkRing)).setChecked(true);
				else
					((CheckBox)findViewById(R.id.chkRing)).setChecked(false);
				
			 //chkDuration.setChecked(true);
			}
			
			activityUIStatus='U';
			eventidn=idn;
		}
		cEvent.close();
	}
	private void loadDDLCatagory()
	 {
		 
		 String[] columns = new String[] { ChargeMe.CATAGORY_NAME };
		 int [] to=new int[] {R.id.lblspinnergeneral};
			Uri allCatagory = Uri.parse("content://com.ennoviabd.services.chargeMe/eventcatagory");
					catagory = managedQuery(allCatagory, null, null, null,"");
					if(catagory.moveToFirst())
					{
					SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,R.layout.layout_spinner , catagory, columns, to);
					mAdapter.setDropDownViewResource(R.layout.layout_spinner );
					Spinner spCatagory = (Spinner) findViewById(R.id.ddlCatagory);
					spCatagory.setAdapter(mAdapter);
					catagory.moveToPosition(0);
					eventCatagory=catagory.getString(1);
					event_type=catagory.getString(2);
					
					spCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int position, long id) {
							// TODO Auto-generated method stub
							catagory.moveToPosition(position);
			                event_type=catagory.getString(2);
			                eventCatagory=catagory.getString(1);
			                updateUIComponent();
							
							
						}
						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							
						}
					});
					}
					
		 
	 }
	/*
	 * This function load reminder dropdown list box with predefine array values to make proper alerm
	 */
	private void loadDDLReminder()
	{
		ArrayAdapter<CharSequence> adapterremind = ArrayAdapter.createFromResource(this,  R.array.riminder, R.layout.layout_spinner_text);
	    adapterremind.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
	    ((Spinner)findViewById(R.id.ddlReminder)).setAdapter(adapterremind);
	    
		
	}
	private void getAllUIValues()
	{
		uiiputs=true;
		EditText txtTmp= (EditText)findViewById(R.id.txt_note);
		eventNote=txtTmp.getText().toString();
		if(eventNote.isEmpty())
		{
			uiiputs=false;
			txtTmp.setBackgroundColor(Color.RED);
		}
		else
		{
			txtTmp.setBackgroundResource(R.drawable.textfield_bg_default_holo_dark);
		}
		eventType=event_type;
		CheckBox chkDuration=(CheckBox)findViewById(R.id.chkAllDay);
		if(chkDuration.isChecked())
		{
			eventDuration="allday";
		}
		else
		{
			eventDuration="";
		}
		
		
		if(event_type.equalsIgnoreCase("day")||event_type.equalsIgnoreCase("datetime"))
		{
			txtTmp= (EditText)findViewById(R.id.txtFromDate);
			eventFromDT=GlobalSettings.stringToDateStrPr(txtTmp.getText().toString());
			String tmpDT=GlobalSettings.stringToDatePrToDb(eventFromDT);
			
			if(tmpDT.length()<=0)
			{
				uiiputs=false;
				txtTmp.setBackgroundColor(Color.RED);
			}
			else
			{
				txtTmp.setText(eventFromDT);
				txtTmp.setBackgroundResource(R.drawable.textfield_bg_default_holo_dark);
				eventFromDT=tmpDT;
			}
		}
		if(event_type.equalsIgnoreCase("datetime"))
		{
			txtTmp= (EditText)findViewById(R.id.txtToDate);
			eventToDT=GlobalSettings.stringToDateStrPr(txtTmp.getText().toString());
			String tmpDT=GlobalSettings.stringToDatePrToDb(eventToDT);
			if(tmpDT.length()<=0)
			{
				uiiputs=false;
				txtTmp.setBackgroundColor(Color.RED);
			}
			else
			{
				txtTmp.setText(eventToDT);
				txtTmp.setBackgroundResource(R.drawable.textfield_bg_default_holo_dark);
				eventToDT=tmpDT;
			}
		}
		if(event_type.equalsIgnoreCase("datetime")||event_type.equalsIgnoreCase("time"))
		{
			txtTmp= (AutoCompleteTextView)findViewById(R.id.txtFromTime);
			eventFromTM=GlobalSettings.stringToTimeStr(txtTmp.getText().toString());
			if(eventFromTM.length()<=0)
			{
				uiiputs=false;
				txtTmp.setBackgroundColor(Color.RED);
			}
			else
			{
				txtTmp.setText(eventFromTM);
				txtTmp.setBackgroundResource(R.drawable.textfield_bg_default_holo_dark);
			}
			txtTmp= (AutoCompleteTextView)findViewById(R.id.txtToTime);
			eventToTM=GlobalSettings.stringToTimeStr(txtTmp.getText().toString());
			if(eventToTM.length()<=0)
			{
				uiiputs=false;
				txtTmp.setBackgroundColor(Color.RED);
			}
			else
			{
				txtTmp.setText(eventToTM);
				txtTmp.setBackgroundResource(R.drawable.textfield_bg_default_holo_dark);
			}
		}
		txtTmp= (EditText)findViewById(R.id.txtEventLocation);
		eventLocation=txtTmp.getText().toString();
		txtTmp= (EditText)findViewById(R.id.txtEventDesc);
		eventDesc=txtTmp.getText().toString();
		Spinner ddlTmp= (Spinner)findViewById(R.id.ddlReminder);
		eventRemind=ddlTmp.getSelectedItem().toString();//GlobalSettings.stringToTimeStr(((TextView)ddlTmp.getSelectedView()).getText().toString());
		
		CheckBox chkTmp=(CheckBox)findViewById(R.id.chkMsg);
		if(chkTmp.isChecked())
		{
			eventNotification+="1,";
		}
		chkTmp=(CheckBox)findViewById(R.id.chkSms);
		if(chkTmp.isChecked())
		{
			eventNotification+="2,";
		}
		chkTmp=(CheckBox)findViewById(R.id.chkRing);
		if(chkTmp.isChecked())
		{
			eventNotification+="3";
		}
			
		
	}
	private void setAllUIValues(String val)
	{
		if(val.equals("0"))
		{
			EditText txtTmp= (EditText)findViewById(R.id.txt_note);
			txtTmp.setText("");
			txtTmp= (EditText)findViewById(R.id.txtFromDate);
			txtTmp.setText((new SimpleDateFormat("dd-MM-yyyy")).format(new Date()));
			txtTmp= (EditText)findViewById(R.id.txtToDate);
			txtTmp.setText((new SimpleDateFormat("dd-MM-yyyy")).format(new Date()));
			txtTmp= (EditText)findViewById(R.id.txtFromTime);
			txtTmp.setText((new SimpleDateFormat("hh:mm")).format(new Date()));
			txtTmp= (EditText)findViewById(R.id.txtToTime);
			txtTmp.setText((new SimpleDateFormat("hh:mm")).format(new Date()));
			txtTmp= (EditText)findViewById(R.id.txtEventLocation);
			txtTmp.setText("");
			txtTmp= (EditText)findViewById(R.id.txtEventDesc);
			txtTmp.setText("");
			((Spinner)findViewById(R.id.ddlReminder)).setSelection(0);
			eventNotification="";
			eventidn="";
			uiiputs=false;
			activityUIStatus='I';			
		
		}
	}
	public  void SelectSpinnerItemByValue(Spinner spnr, String value)
	{
	    SimpleCursorAdapter adapter = (SimpleCursorAdapter) spnr.getAdapter();
	    for (int position = 0; position < adapter.getCount(); position++)
	    {
	    	 catagory = (Cursor)adapter.getItem(position);

	    	  String tmpval = catagory.getString(1);
	    	if(tmpval.equalsIgnoreCase(value))
	        {
	            spnr.setSelection(position);
	            event_type=catagory.getString(2);
	        }
	    }
	}
	public void SelectedSpinnerByValue(Spinner sp,String val)
	{
		ArrayAdapter myAdap = (ArrayAdapter) sp.getAdapter(); //cast to an ArrayAdapter

		int spinnerPosition = myAdap.getPosition(val);

		//set the default according to value
		sp.setSelection(spinnerPosition);
	}
}
