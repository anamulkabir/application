package com.ennoviabd.generalservice;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class CustomCursorAdapter extends SimpleCursorAdapter {
	private Cursor cursor;
    private Context context;
    
	public CustomCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.cursor = c;
        this.context=context;
	}
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	// TODO Auto-generated method stub
    	View v =convertView; //super.getView(position, convertView, parent);
    	  	
    	 ViewHolder vh=null;
     	  if (v == null) {
               LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               v = vi.inflate(R.layout.list_main, null);
           	
               vh = new ViewHolder();
               vh.idn = (TextView)v.findViewById(R.id.lstEventid);                                
               v.setTag(vh);
           }
           else
           {
               vh = (ViewHolder)v.getTag();
           }
    	  
        if (this.cursor != null && !this.cursor.isClosed()) {
       
        	
        this.cursor.moveToPosition(position);	
        
        
        vh.idn=(TextView) v.findViewById(R.id.lstEventid);
    	vh.idn.setText(cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_IDN)));
    	vh.id=cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_IDN));
    	vh.position=position;
    	
    	final TextView ltnote = (TextView) v.findViewById(R.id.lstAlertnote);
    	final   TextView ltdt = (TextView) v.findViewById(R.id.lstAlertDT);
    	final   TextView ltidn = (TextView) v.findViewById(R.id.lstEventid);
    	final   Button btnDel = (Button) v.findViewById(R.id.btnDdlDel);
    	ltnote.setText(cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_IDN)));
    	ltnote.setText(cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_CATAGORY))+" : "+cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_NOTE)));
    	ltdt.setText(GlobalSettings.stringToDateDbToPr(cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_FROMDT)))+"    "+GlobalSettings.stringToTimeStr(cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_FROMTM))));
    	
    	//final TextView txteventHandler = (TextView) v.findViewById(R.id.btnDdlDel);
    	btnDel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ViewHolder vh = getViewHolder(v);
				//MainActivity.setOnDelHandle(vh.id.toString());// call mainactivity to perform delete function
				int rs=context.getContentResolver().delete(ChargeMe.Content_Event_Uri, ChargeMe.EVENT_IDN+"="+vh.id.toString(), null);
				//int rs= getContentResolver().update(ChargeMe.Content_Event_Uri, cValues, ChargeMe.EVENT_IDN+"="+eventidn, null);
				
			}
		});
        String  event_type = this.cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_EVENT_TYPE));
    	String  eventDate = this.cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_FROMDT));
    	String  eventTime = cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_FROMTM));
    	String  remindMe = cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_REMIND));
    	String  eventRept = cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_IS_REPEAT));
    	String  eventStatus = cursor.getString(cursor.getColumnIndex(ChargeMe.EVENT_STATUS));
    	//GEN: general; AAC:alert activate;DNH: deadline not handle,DHN: deadline handle;ARC: archive;DEP: depricated;ERR:error;
    	// Color generate on status logic and date time duration
    	int color=new GlobalSettings().getEventBehaviour(eventDate,eventTime,remindMe,eventRept,event_type,eventStatus);
    	
	    	if(color !=0)
	    	{
	    		//1:Green;2:Red;3:Yellow;4:Gray;5:Blue
	    		if(color==1)
	    			((ImageView)v.findViewById(R.id.lstimgStatus)).setImageResource(R.drawable.circle_green);
	    		else if(color==2)
	    			((ImageView)v.findViewById(R.id.lstimgStatus)).setImageResource(R.drawable.circle_red_light);
	    		else if(color==3)
	    			((ImageView)v.findViewById(R.id.lstimgStatus)).setImageResource(R.drawable.circle_yellow);
	    		else if(color==4)
	    			((ImageView)v.findViewById(R.id.lstimgStatus)).setImageResource(R.drawable.circle_gray);
	    		else if(color==5)
	    			((ImageView)v.findViewById(R.id.lstimgStatus)).setImageResource(R.drawable.circle_maroon);
	    	}
	    	else
	    	{
	    		((ImageView)v.findViewById(R.id.lstimgStatus)).setImageResource(R.drawable.transparent);
	    	}
        }
       
    	return v;
    }    
    public static class ViewHolder
    {
        private TextView idn;
        public int position;
        public String id;
        
    }
    public  ViewHolder getViewHolder(View v)
    {
        if(v.getTag() == null)
        {
            return getViewHolder((View)v.getParent());
        }
        return (ViewHolder)v.getTag();
    }
    public void setCursor(Cursor c)
    {
    	this.cursor=c;
    }

}
