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

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

public class CalendarView extends ImageView {
    private static int WEEK_TOP_MARGIN = 74;
    private static int WEEK_LEFT_MARGIN = 40;
    private static int CELL_WIDTH = 58;
    private static int CELL_HEIGH = 53;
    private static int CELL_MARGIN_TOP = 92;
    private static int CELL_MARGIN_LEFT = 39;
    private static float CELL_TEXT_SIZE;
    
	private static final String TAG = "CalendarView"; 
	private Calendar mRightNow = null;
    private Drawable mWeekTitle = null;
    private Drawable cHeader=null;
    private TextView txtHeader=null;
    private Cell mToday = null;
    private Cell[][] mCells = new Cell[6][7];
    private OnCellTouchListener mOnCellTouchListener = null;
    MonthDisplayHelper mHelper;
    Drawable mDecoration = null;
    Drawable mAlertDay=null;
    private int firstd=0;
    String selectedYR="";
    Context ctx;
    
	public interface OnCellTouchListener {
    	public void onTouch(Cell cell);
    }

	public CalendarView(Context context) {
		this(context, null);
		this.ctx=context;
	}
	
	public CalendarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.ctx=context;
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.ctx=context;
		mDecoration = context.getResources().getDrawable(R.drawable.today_select);		
		mAlertDay=context.getResources().getDrawable(R.drawable.ic_alarm_dark);
		String yr=""+(Calendar.getInstance()).get(Calendar.YEAR);
		loadAlertday(yr);
		initCalendarView();
	}
	
	private void initCalendarView() {
		mRightNow = Calendar.getInstance();
		// prepare static vars
		Resources res = getResources();
		WEEK_TOP_MARGIN  = (int) res.getDimension(R.dimen.week_top_margin);
		WEEK_LEFT_MARGIN = (int) res.getDimension(R.dimen.week_left_margin);
		
		CELL_WIDTH = (int) res.getDimension(R.dimen.cell_width);
		CELL_HEIGH = (int) res.getDimension(R.dimen.cell_heigh);
		CELL_MARGIN_TOP = (int) res.getDimension(R.dimen.cell_margin_top);
		CELL_MARGIN_LEFT = (int) res.getDimension(R.dimen.cell_margin_left);
		CELL_TEXT_SIZE = res.getDimension(R.dimen.cell_text_size);
		// set background
		setImageResource(R.drawable.claender_bg);
		mWeekTitle = res.getDrawable(R.drawable.calendar_week);
		mWeekTitle.setBounds(WEEK_LEFT_MARGIN, WEEK_TOP_MARGIN, WEEK_LEFT_MARGIN+mWeekTitle.getMinimumWidth(), WEEK_TOP_MARGIN+mWeekTitle.getMinimumHeight());
		mHelper = new MonthDisplayHelper(mRightNow.get(Calendar.YEAR), mRightNow.get(Calendar.MONTH));
		initCells();
    }

	private void initCells() {
	    class _calendar {
	    	public int day;
	    	public int month;
	    	public int year;
	    	public boolean thisMonth;
	    	public _calendar(int d,int m,int y, boolean b) {
	    		day = d;
	    		month=m;
	    		year=y;
	    		thisMonth = b;
	    	}
	    	public _calendar(int d,int m,int y) {
	    		this(d,m,y, false);
	    	}
	    };
	    _calendar tmp[][] = new _calendar[6][7];
	    String curymd="";
	    
	    
	    for(int i=0; i<tmp.length; i++) {
	    	int n[] = mHelper.getDigitsForRow(i);
	    	for(int d=0; d<n.length; d++) {
	    		if(mHelper.isWithinCurrentMonth(i,d)){
	    			tmp[i][d] = new _calendar(n[d],mHelper.getMonth(),mHelper.getYear() ,true);
	    			firstd=1;
	    		}
	    		else
	    			if(firstd==0)
	    			{
	    			Calendar cl=Calendar.getInstance();
	    			cl.set(Calendar.MONTH, mHelper.getMonth());
	    			cl.set(Calendar.YEAR, mHelper.getYear());
	    			cl.add(Calendar.MONTH, -1);
	    			tmp[i][d] = new _calendar(n[d],cl.get(Calendar.MONTH),cl.get(Calendar.YEAR));
	    			}
	    			else
	    			{
	    				Calendar cl=Calendar.getInstance();
	    				cl.set(Calendar.MONTH, mHelper.getMonth());
		    			cl.set(Calendar.YEAR, mHelper.getYear());
		    			cl.add(Calendar.MONTH, 1);
		    			tmp[i][d] = new _calendar(n[d],cl.get(Calendar.MONTH),cl.get(Calendar.YEAR));
	    				
	    			}	    		
	    	}
	    }

	    Calendar today = Calendar.getInstance();
	    int thisDay = 0;
	    mToday = null;
	    if(mHelper.getYear()==today.get(Calendar.YEAR) && mHelper.getMonth()==today.get(Calendar.MONTH)) {
	    	thisDay = today.get(Calendar.DAY_OF_MONTH);
	    }
		// build cells
		Rect Bound = new Rect(CELL_MARGIN_LEFT, CELL_MARGIN_TOP, CELL_WIDTH+CELL_MARGIN_LEFT, CELL_HEIGH+CELL_MARGIN_TOP);
		for(int week=0; week<mCells.length; week++) {
			for(int day=0; day<mCells[week].length; day++) {
				if(tmp[week][day].thisMonth) {
					if(day==0 || day==6 )
						mCells[week][day] = new RedCell(tmp[week][day].day,tmp[week][day].month,tmp[week][day].year, new Rect(Bound), CELL_TEXT_SIZE);
					else 
						mCells[week][day] = new Cell(tmp[week][day].day,tmp[week][day].month,tmp[week][day].year, new Rect(Bound), CELL_TEXT_SIZE);
					
					if(tmp[week][day].day==thisDay) {
						mToday = mCells[week][day];
						mDecoration.setBounds(mToday.getBound());
					}
					
					
				} else
					mCells[week][day] = new GrayCell(tmp[week][day].day,tmp[week][day].month,tmp[week][day].year, new Rect(Bound), CELL_TEXT_SIZE);
				
				curymd=GlobalSettings.LPad(""+mCells[week][day].mYear,"0",4)+GlobalSettings.LPad(String.valueOf(mCells[week][day].mMonth+1),"0",2)+GlobalSettings.LPad(""+mCells[week][day].mDayOfMonth,"0",2);
				if(GlobalSettings.confiAlertDay.contains(curymd))
				{
					mCells[week][day].mIsScheduleDay=1;						
				}
				
				Bound.offset(CELL_WIDTH, 0); // move to next column 
				
				// get today
				
			}
			Bound.offset(0, CELL_HEIGH); // move to next row and first column
			Bound.left = CELL_MARGIN_LEFT;
			Bound.right = CELL_MARGIN_LEFT+CELL_WIDTH;
		}		
	}

    public void setTimeInMillis(long milliseconds) {
    	mRightNow.setTimeInMillis(milliseconds);
    	initCells();
    	this.invalidate();
    }
        
    public int getYear() {
    	return mHelper.getYear();
    }
    
    public int getMonth() {
    	return mHelper.getMonth();
    }
    
    public void nextMonth() {
    	mHelper.nextMonth();
    	loadAlertday(String.valueOf(mHelper.getYear()));
    	firstd=0;
    	initCells();
    	invalidate();
    }
    
    public void previousMonth() {
    	mHelper.previousMonth();
    	loadAlertday(String.valueOf(mHelper.getYear()));
    	firstd=0;
    	initCells();
    	invalidate();
    }
    
    public boolean firstDay(int day) {
    	return day==1;
    }
    
    public boolean lastDay(int day) {
    	return mHelper.getNumberOfDaysInMonth()==day;
    }
    
    public void goToday() {
    	Calendar cal = Calendar.getInstance();
    	mHelper = new MonthDisplayHelper(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
    	initCells();
    	invalidate();
    }
    
    public Calendar getDate() {
    	return mRightNow;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if(mOnCellTouchListener!=null){
	    	for(Cell[] week : mCells) {
				for(Cell day : week) {
					if(day.hitTest((int)event.getX(), (int)event.getY())) {
						mOnCellTouchListener.onTouch(day);
					}						
				}
			}
    	}
    	return super.onTouchEvent(event);
    }
  
    public void setOnCellTouchListener(OnCellTouchListener p) {
		mOnCellTouchListener = p;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw background
		super.onDraw(canvas);
		mWeekTitle.draw(canvas);
		
		// draw cells
		for(Cell[] week : mCells) {
			for(Cell day : week) {
				day.draw(canvas);	
				if(day.mIsScheduleDay>0) {
					Rect tmpBound =new Rect(day.getBound().left+(CELL_WIDTH/2)+(CELL_WIDTH/5), day.getBound().top+(CELL_HEIGH/2)+(CELL_HEIGH/5), day.getBound().right, day.getBound().bottom);
					mAlertDay.setBounds(tmpBound);
					mAlertDay.draw(canvas);
				}
			}
		}
		
		// draw today
		if(mDecoration!=null && mToday!=null) {
			mDecoration.draw(canvas);
		}
		//
		
	}
	
	private class GrayCell extends Cell {
		public GrayCell(int dayOfMon,int m,int y, Rect rect, float s) {
			super(dayOfMon,m,y, rect, s);
			mPaint.setColor(Color.LTGRAY);
		}			
	}
	
	private class RedCell extends Cell {
		public RedCell(int dayOfMon,int m,int y, Rect rect, float s) {
			super(dayOfMon,m,y, rect, s);
			mPaint.setColor(0xdddd0000);
		}			
		
	}
	private void loadAlertday(String yr)
	{
		if(!selectedYR.equalsIgnoreCase(yr)&& yr.length()>=4)
		{
			selectedYR=yr;
		Uri allEvents = Uri.parse(ChargeMe.Content_Event_Uri.toString());
		try{
			// Cursor to get all event information from the event content provider
			// it returns database cursor pointer
			Cursor c = this.ctx.getContentResolver().query(allEvents, null,"strftime('%Y',"+ChargeMe.EVENT_FROMDT+") = '"+yr+"'", null, null);
			if (c != null) {
			    if (c.moveToFirst()) {
			        do {
			            String str=(c.getString(c.getColumnIndex(ChargeMe.EVENT_FROMDT)));
			            str=str.replace("-", "");
			            GlobalSettings.confiAlertDay+=str+",";
			        } while (c.moveToNext());
			    }
			}
		}catch(Exception ex)
		{
			
		}
	  }
	}


}
