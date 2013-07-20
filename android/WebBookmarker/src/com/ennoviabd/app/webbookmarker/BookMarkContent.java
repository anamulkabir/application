package com.ennoviabd.app.webbookmarker;

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

public class BookMarkContent extends ContentProvider {
	public static final String Provider_Name="com.ennoviabd.content.bookmark";
	public static final Uri Content_Uri=Uri.parse("content://"+Provider_Name+"/web");
	public static final String BOOKMARK_IDN="_id";
	public static final String BOOKMARK_WEB="webaddress";
	public static final String BOOKMARK_LASTVISIT="lastvisit";
	public static final String BOOKMARK_PRIORITY="priority";
		
	private static final int BOOKMARK = 1;
	private static final int BOOKMARK_ID = 2;
	
	private static final UriMatcher uriMatcher;
	static{
	uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	uriMatcher.addURI(Provider_Name, "web", BOOKMARK);
	uriMatcher.addURI(Provider_Name, "web/#", BOOKMARK_ID);	
	}
	
	//---for database use---
	private SQLiteDatabase bookmarkDb;
	
	private static final String DATABASE_NAME = "bookmark";
	private static final String DATABASE_TABLE_BOOKMARK = "bookmark";
		private static final int DATABASE_VERSION = 1;
	private static final String TABLE_BOOKMARK_CREATE =
	"create table " + DATABASE_TABLE_BOOKMARK +
	" (_id integer primary key autoincrement, webaddress text not null,lastvisit text , priority text);";
	
	
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
				db.execSQL(TABLE_BOOKMARK_CREATE);
				
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
		case BOOKMARK:
		count = bookmarkDb.update(DATABASE_TABLE_BOOKMARK,	values,	whereClause,whereArgs);
		break;
		case BOOKMARK_ID:
		count = bookmarkDb.update(DATABASE_TABLE_BOOKMARK,values,"_id=" + uri.getPathSegments().get(1) +
		(!TextUtils.isEmpty(whereClause) ? " AND (" +
		whereClause + ')' : ""),whereArgs);
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
		case BOOKMARK:
		count =bookmarkDb.delete(DATABASE_TABLE_BOOKMARK,whereClause,whereArgs);
		break;
		case BOOKMARK_ID:
			id = uri.getPathSegments().get(1);
			count = bookmarkDb.delete(DATABASE_TABLE_BOOKMARK,"_id " + " = " + id +
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
		case BOOKMARK:
		return "vnd.android.cursor.dir/vnd.bookmarkcontent.bookmark";
		//---get a particular book---
		case BOOKMARK_ID:
		return "vnd.android.cursor.item/vnd.bookmarkcontent.bookmark";
		
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
				case BOOKMARK:
					try
					{
						rowID = bookmarkDb.insert(DATABASE_TABLE_BOOKMARK,"",values);
					}catch(SQLException e)
					{
						e.printStackTrace();
						
					}
				//---if added successfully---
				if (rowID>0)
				{
				Uri _uri = ContentUris.withAppendedId(Content_Uri, rowID);
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
		bookmarkDb = dbm.getWritableDatabase();
		return (bookmarkDb == null)? false:true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Cursor c=null;
			switch (uriMatcher.match(uri)){
			case BOOKMARK:			
			SQLiteQueryBuilder sqlBuilderBookmark = new SQLiteQueryBuilder();
			sqlBuilderBookmark.setTables(DATABASE_TABLE_BOOKMARK);
			if (uriMatcher.match(uri) == BOOKMARK_ID)
			//---if getting a particular book---
				sqlBuilderBookmark.appendWhere("_id = " + uri.getPathSegments().get(1));
			if (sortOrder==null || sortOrder=="")
			sortOrder = "_id";
			 c = sqlBuilderBookmark.query(bookmarkDb,	projection,	selection,selectionArgs,null,null,sortOrder);
			//---register to watch a content URI for changes---
			c.setNotificationUri(getContext().getContentResolver(), uri);
			break;
			case BOOKMARK_ID:			
				sqlBuilderBookmark = new SQLiteQueryBuilder();
				sqlBuilderBookmark.setTables(DATABASE_TABLE_BOOKMARK);
				if (uriMatcher.match(uri) == BOOKMARK_ID)
				//---if getting a particular book---
				sqlBuilderBookmark.appendWhere("_id = " + uri.getPathSegments().get(1));
				if (sortOrder==null || sortOrder=="")
				sortOrder = "_id";
				 c = sqlBuilderBookmark.query(bookmarkDb,	projection,	selection,selectionArgs,null,null,sortOrder);
				//---register to watch a content URI for changes---
				c.setNotificationUri(getContext().getContentResolver(), uri);
				break;
			
		}
		return c;
	}

}
