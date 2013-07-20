package app.ennoviabd.com.umis;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class umisDao extends SQLiteOpenHelper {
	
	public static final String TABLE_NAME = "admission";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_IDN = "idn";
	public static final String COLUMN_NAME = "studname";
	public static final String COLUMN_FNAME = "fname";
	public static final String COLUMN_SEX = "sex";
	public static final String COLUMN_DOB = "dob";
	public static final String COLUMN_ADDRESS = "studaddress";
	public static final String COLUMN_ADMDATE = "admdate";
	

	private static final String DATABASE_NAME = "umis.db";
	private static final int DATABASE_VERSION = 1;
	// Database creation sql statement with table
		private static final String DATABASE_CREATE = "create table "
				+ TABLE_NAME + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " +COLUMN_IDN+" text not null, " +COLUMN_NAME
				+ " text not null,"+COLUMN_FNAME+","+COLUMN_SEX+","+COLUMN_DOB+","+COLUMN_ADDRESS+","+COLUMN_ADMDATE+");";
	public umisDao(Context context) {
		// TODO Auto-generated constructor stub
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(DATABASE_CREATE);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(umisDao.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
		
	}
	

}
