/*
 * Copyright (C) 2011 Chris Gao <chris@exina.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ennoviabd.generalservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class CalendarActivity extends Activity  implements CalendarView.OnCellTouchListener{
	public static final String MIME_TYPE = "vnd.android.cursor.dir/vnd.exina.android.calendar.date";
	CalendarView mView = null;	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_main);
        
        mView = (CalendarView)findViewById(R.id.calendar);
        mView.setOnCellTouchListener(this);
        TextView txtHeader=(TextView)findViewById(R.id.txtMonYear);
        txtHeader.setText(DateUtils.getMonthString(mView.getMonth(), DateUtils.LENGTH_LONG) + " ,"+mView.getYear());
        ImageButton imbPrevious=(ImageButton)findViewById(R.id.btnPrevious);
        ImageButton imbNext=(ImageButton)findViewById(R.id.btnNext);
        imbPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView txtHeader=(TextView)findViewById(R.id.txtMonYear);
				mView.previousMonth();
				txtHeader.setText(DateUtils.getMonthString(mView.getMonth(), DateUtils.LENGTH_LONG) + " ,"+mView.getYear());
				
				
			}
		});
        
        imbNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView txtHeader=(TextView)findViewById(R.id.txtMonYear);
				mView.nextMonth();
				txtHeader.setText(DateUtils.getMonthString(mView.getMonth(), DateUtils.LENGTH_LONG) + " ,"+mView.getYear());
				
			}
		});
        
        Button btn= (Button)findViewById(R.id.btnAddEvent);
		btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				startActivity(new Intent(CalendarActivity.this,MyEvent.class));
				
				
			}
		});
		
				
		btn=(Button)findViewById(R.id.btnClose);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					finish();
			}
		});
                
    }

	public void onTouch(Cell cell) {
		
		Intent intent ;
		if(cell.mIsScheduleDay>0)
		{
			intent=new Intent(CalendarActivity.this,MainActivity.class);
		}
		else
		{
			intent=new Intent(CalendarActivity.this,MyEvent.class);
		}
		intent.putExtra(ChargeMe.EVENT_FROMDT, GlobalSettings.stringToDatePrToDb(cell.getDayOfMonth()+"-"+(cell.mMonth+1)+"-"+cell.mYear));
		intent.putExtra("month", cell.mMonth);
		intent.putExtra("day", cell.getDayOfMonth());
	   startActivity(intent);	
				
	}
	
    
}