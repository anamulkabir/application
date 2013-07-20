package com.ennoviabd.generalservice;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ChargeMe extends ContentProvider {
	public static final String Provider_Name="com.ennoviabd.services.chargeMe";
	public static final Uri Content_Event_Uri=Uri.parse("content://"+Provider_Name+"/event");
	public static final Uri Content_Catagory_Uri=Uri.parse("content://"+Provider_Name+"/eventcatagory");
	public static final Uri Content_V_DateTimeList_Uri=Uri.parse("content://"+Provider_Name+"/v_daytime_alert");
	public static final Uri Content_V_TimeList_Uri=Uri.parse("content://"+Provider_Name+"/v_time_alert");
	public static final String EVENT_IDN="_id";
	public static final String EVENT_NOTE="alertnote";
	public static final String EVENT_CATAGORY="catagory";
	public static final String EVENT_EVENT_TYPE="event_type";
	public static final String EVENT_NOTIFICATION="eventnotification";
	public static final String EVENT_DESCRIPTION="eventdesc";
	public static final String EVENT_LOCATION="eventlocation";
	public static final String EVENT_PRIORITY="priority";	
	public static final String EVENT_ALERT_DURATION="alert_duration";
	public static final String EVENT_FROMDT="eventfromdt";
	public static final String EVENT_FROMTM="eventfromtm";
	public static final String EVENT_TODT="eventtodt";
	public static final String EVENT_TOTM="eventtotm";
	public static final String EVENT_REMIND="eventremind";
	public static final String EVENT_ATTACH_PERSON="assignperson";
	public static final String EVENT_DURATION="eventduration";
	public static final String EVENT_IS_REPEAT="is_repeat";
	public static final String EVENT_STATUS="status";
	public static final String CATAGORY_IDN="_id";
	public static final String CATAGORY_NAME="name";
	public static final String CATAGORY_ALERT_TYPE="alert_type";
	
	private static final int EVENTS = 1;
	private static final int EVENT_ID = 2;
	private static final int CATAGORY=3;
	private static final int CATAGORY_ID=4;
	private static final int V_DAYTIME_ALERT = 5;
	private static final int V_TIME_ALERT = 6;
	private static final UriMatcher uriMatcher;
	static{
	uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	uriMatcher.addURI(Provider_Name, "event", EVENTS);
	uriMatcher.addURI(Provider_Name, "event/#", EVENT_ID);
	uriMatcher.addURI(Provider_Name, "eventcatagory", CATAGORY);
	uriMatcher.addURI(Provider_Name, "eventcatagory/#", CATAGORY_ID);	
	uriMatcher.addURI(Provider_Name, "v_daytime_alert", V_DAYTIME_ALERT);
	uriMatcher.addURI(Provider_Name, "v_time_alert", V_TIME_ALERT);
	}
	
	//---for database use---
	private SQLiteDatabase chargeMeDb;
	
	private static final String DATABASE_NAME = "chargeMe";
	private static final String DATABASE_TABLE_EVENT = "event";
	private static final String DATABASE_TABLE_CATAGORY = "catagory";
	private static final String DATABASE_VIEW_DAYTIME = "v_daytime_alert";
	private static final String DATABASE_VIEW_TIME = "v_time_alert";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_EVENT_CREATE =
	"create table " + DATABASE_TABLE_EVENT +
	" (_id integer primary key autoincrement, alertnote text not null, catagory text not null,event_type text,priority text, alert_duration text," +
	"eventnotification text, eventfromdt text,eventfromtm text,eventtodt text,eventtotm text,eventdesc text,eventlocation text,assignperson text,eventduration text,eventremind text,is_repeat text, status text);";
	
	private static final String TABLE_CATAGORY_CREATE =
			"create table " + DATABASE_TABLE_CATAGORY +
			" (_id integer primary key autoincrement, name text not null, event_type text not null);";
	private static final String INSCATAGORY1="INSERT INTO catagory (name,event_type) VALUES ('Birth Day/Anniversary', 'DAY');";
	private static final String INSCATAGORY2="INSERT INTO catagory (name,event_type) VALUES ('Meeting', 'DATETIME');";
	private static final String INSCATAGORY3="INSERT INTO catagory (name,event_type) VALUES ('Regular Timer', 'TIME');";
	
	private static final String VIEW_DATETIMELIST=
			"create view v_daytime_alert as "+ 
"select*,(strftime('%s',datetime('now','+'|| replace(eventremind,'s','')),'localtime')-strftime('%s', ifnull(eventfromdt,'')||'  '||ifnull(eventfromtm,'00:00')) )/60 d4"+
" from event where lower(event_type)<>'time'and d4 between -2880 and 2880 order by d4 ;";
	private static final String VIEW_TIMELIST="create view v_time_alert as "+ 
"select*,(strftime('%s','now','localtime')-strftime('%s',datetime(date('now','localtime')||' '|| ifnull(eventfromtm,'00:00'))) )/60 d4"+
" from event where lower(event_type)='time' order by d4;";
	
	
	
	private static class DbManager extends SQLiteOpenHelper
	{
		DbManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.beginTransaction();
			try
			{
				db.execSQL(TABLE_CATAGORY_CREATE);
				db.execSQL(TABLE_EVENT_CREATE);
				db.execSQL(INSCATAGORY1);
				db.execSQL(INSCATAGORY2);
				db.execSQL(INSCATAGORY3);
				db.execSQL(VIEW_DATETIMELIST);
				db.execSQL(VIEW_TIMELIST);
				db.setTransactionSuccessful();
			}catch (SQLException e) {
				Log.e("Error creating tables and debug data", e.toString());
				throw e;
			}
			finally
			{
				db.endTransaction();
			}
		// script for default master data entry
		
		// end of script
		
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
		int newVersion) {
		Log.w("Content provider database","Upgrading database from version " +
		oldVersion + " to " + newVersion +", which will destroy all old data");
		db.beginTransaction();
		try
		{
		db.execSQL("DROP TABLE IF EXISTS event");
		db.execSQL("DROP TABLE IF EXISTS catagory");
		db.setTransactionSuccessful();
		
		}catch(SQLException e)
		{
			Log.e("Error upgrading tables and debug data", e.toString());
			throw e;
		}
		finally
		{
			db.endTransaction();
		}
		onCreate(db);
		}		
	}
	@Override
	public int update(Uri uri, ContentValues values, String whereClause,
			String[] whereArgs) {
		// TODO Auto-generated method stub
		int count = 0;
		switch (uriMatcher.match(uri)){
		case EVENTS:
		count = chargeMeDb.update(DATABASE_TABLE_EVENT,	values,	whereClause,whereArgs);
		break;
		case EVENT_ID:
		count = chargeMeDb.update(DATABASE_TABLE_EVENT,values,"_id=" + uri.getPathSegments().get(1) +
		(!TextUtils.isEmpty(whereClause) ? " AND (" +
		whereClause + ')' : ""),whereArgs);
		break;
		case CATAGORY:
		count = chargeMeDb.update(DATABASE_TABLE_CATAGORY,	values,	whereClause,whereArgs);
		break;
			
		case CATAGORY_ID:
		count = chargeMeDb.update(DATABASE_TABLE_CATAGORY,values,"_id=" + uri.getPathSegments().get(1) +
		(!TextUtils.isEmpty(whereClause) ? " AND (" +	whereClause + ')' : ""),whereArgs);
		break;
		default: throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		// TODO Auto-generated method stub
		int count=0;
		String id;
		switch (uriMatcher.match(uri)){
		case EVENTS:
		count =chargeMeDb.delete(DATABASE_TABLE_EVENT,whereClause,whereArgs);
		break;
		case EVENT_ID:
			id = uri.getPathSegments().get(1);
			count = chargeMeDb.delete(DATABASE_TABLE_EVENT,"_id " + " = " + id +
			(!TextUtils.isEmpty(whereClause) ? " AND (" +whereClause + ')' : ""),whereArgs);
		break;
		case CATAGORY:
			count =chargeMeDb.delete(DATABASE_TABLE_CATAGORY,whereClause,whereArgs);
			break;
		case CATAGORY_ID:
			 id = uri.getPathSegments().get(1);
			 count = chargeMeDb.delete(DATABASE_TABLE_CATAGORY,"_id" + "=" + id +
			(!TextUtils.isEmpty(whereClause) ? " AND (" +whereClause + ')' : ""),whereArgs);
		break;
		default: throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (uriMatcher.match(uri)){
		//---get all books---
		case EVENTS:
		return "vnd.android.cursor.dir/vnd.chargeme.events";
		//---get a particular book---
		case EVENT_ID:
		return "vnd.android.cursor.item/vnd.charge.events";
		case CATAGORY:
		return "vnd.android.cursor.dir/vnd.chargeme.catagory";
			//---get a particular book---
		case CATAGORY_ID:
		return "vnd.android.cursor.item/vnd.charge.catagory";
		default:
		throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		//---add a new book---
		long rowID=0;
		switch (uriMatcher.match(uri)){
				case EVENTS:
					try
					{
						rowID = chargeMeDb.insert(DATABASE_TABLE_EVENT,"",values);
					}catch(SQLException e)
					{
						e.printStackTrace();
						
					}
				//---if added successfully---
				if (rowID>0)
				{
				Uri _uri = ContentUris.withAppendedId(Content_Event_Uri, rowID);
				getContext().getContentResolver().notifyChange(_uri, null);
				return _uri;
				}
				break;
				case CATAGORY:
				 rowID = chargeMeDb.insert(	DATABASE_TABLE_CATAGORY,"",values);
							//---if added successfully---
				if (rowID>0)
				{
				Uri _uri = ContentUris.withAppendedId(Content_Catagory_Uri, rowID);
				getContext().getContentResolver().notifyChange(_uri, null);
				return _uri;
				}
				break;
				default:
					throw new IllegalArgumentException("Unsupported URI: " + uri);
					
			}
		throw new SQLException("Failed to insert row into " + uri);
	}
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Context context = getContext();
		DbManager dbm = new DbManager(context);
		chargeMeDb = dbm.getWritableDatabase();
		return (chargeMeDb == null)? false:true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Cursor c=null;
			switch (uriMatcher.match(uri)){
			case EVENTS:			
			SQLiteQueryBuilder sqlBuilderEvent = new SQLiteQueryBuilder();
			sqlBuilderEvent.setTables(DATABASE_TABLE_EVENT);
			if (uriMatcher.match(uri) == EVENT_ID)
			//---if getting a particular book---
			sqlBuilderEvent.appendWhere("_id = " + uri.getPathSegments().get(1));
			if (sortOrder==null || sortOrder=="")
			sortOrder = "_id";
			 c = sqlBuilderEvent.query(chargeMeDb,	projection,	selection,selectionArgs,null,null,sortOrder);
			//---register to watch a content URI for changes---
			c.setNotificationUri(getContext().getContentResolver(), uri);
			break;
			case EVENT_ID:			
				sqlBuilderEvent = new SQLiteQueryBuilder();
				sqlBuilderEvent.setTables(DATABASE_TABLE_EVENT);
				if (uriMatcher.match(uri) == EVENT_ID)
				//---if getting a particular book---
				sqlBuilderEvent.appendWhere("_id = " + uri.getPathSegments().get(1));
				if (sortOrder==null || sortOrder=="")
				sortOrder = "_id";
				 c = sqlBuilderEvent.query(chargeMeDb,	projection,	selection,selectionArgs,null,null,sortOrder);
				//---register to watch a content URI for changes---
				c.setNotificationUri(getContext().getContentResolver(), uri);
				break;
			case CATAGORY:
				
				SQLiteQueryBuilder sqlBuilderCat = new SQLiteQueryBuilder();
				sqlBuilderCat.setTables(DATABASE_TABLE_CATAGORY);
				if (uriMatcher.match(uri) == EVENT_ID)
				//---if getting a particular book---
				sqlBuilderCat.appendWhere("_id = " + uri.getPathSegments().get(1));
				if (sortOrder==null || sortOrder=="")
				sortOrder = "_id";
				 c = sqlBuilderCat.query(chargeMeDb,	projection,	selection,selectionArgs,null,null,sortOrder);
				//---register to watch a content URI for changes---
				c.setNotificationUri(getContext().getContentResolver(), uri);
				break;
			case V_DAYTIME_ALERT:			
				SQLiteQueryBuilder sqlBuilderDayTime = new SQLiteQueryBuilder();
				sqlBuilderDayTime.setTables(DATABASE_VIEW_DAYTIME);
				
				 c = sqlBuilderDayTime.query(chargeMeDb,	projection,	selection,selectionArgs,null,null,sortOrder);
				//---register to watch a content URI for changes---
				c.setNotificationUri(getContext().getContentResolver(), uri);
				break;
			case V_TIME_ALERT:			
				SQLiteQueryBuilder sqlBuilderTime = new SQLiteQueryBuilder();
				sqlBuilderTime.setTables(DATABASE_VIEW_TIME);
				
				 c = sqlBuilderTime.query(chargeMeDb,	projection,	selection,selectionArgs,null,null,sortOrder);
				//---register to watch a content URI for changes---
				c.setNotificationUri(getContext().getContentResolver(), uri);
				break;
			
		}
		return c;
	}

}
