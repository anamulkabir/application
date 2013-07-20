package app.ennoviabd.com.umis;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class UmisDbconn {
	private SQLiteDatabase database;
	private umisDao dbconfig;
	private String[] allColumns = { "_id",umisDao.COLUMN_IDN,umisDao.COLUMN_NAME,umisDao.COLUMN_FNAME,umisDao.COLUMN_SEX,
			umisDao.COLUMN_DOB,umisDao.COLUMN_ADDRESS,umisDao.COLUMN_ADMDATE };
	public UmisDbconn(Context context)
	{
	dbconfig=new umisDao(context);	
	}
	public void open() throws SQLException
	{
		database=dbconfig.getWritableDatabase();
	}
	public void close()
	{
		dbconfig.close();
	}
	public boolean createAdmission(String sidn,String name,String fname,String sex,String dob,String address,String admdate)
	{
		ContentValues cntv= new ContentValues();
		cntv.put(umisDao.COLUMN_IDN,sidn);
		cntv.put(umisDao.COLUMN_NAME,name);		
		cntv.put(umisDao.COLUMN_FNAME, fname);
		cntv.put(umisDao.COLUMN_SEX, sex);
		cntv.put(umisDao.COLUMN_DOB, dob);
		cntv.put(umisDao.COLUMN_ADDRESS, address);
		cntv.put(umisDao.COLUMN_ADMDATE, admdate);
		try{
		long id=database.insert(umisDao.TABLE_NAME,null, cntv);
		return true;
		
		/*
		Cursor cursor = database.query(umisDao.TABLE_NAME,	allColumns, "_id" + " = " + id, null,
				null, null, null);
		cursor.moveToFirst();
		newAdmission = cursorToAdmission(cursor);
		cursor.close();
		*/
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	public boolean updateAdmission(String sidn,String name,String fname,String sex,String dob,String address,String admdate)
	{
		ContentValues cntv= new ContentValues();
		//cntv.put(umisDao.COLUMN_IDN,sidn);
		cntv.put(umisDao.COLUMN_NAME,name);		
		cntv.put(umisDao.COLUMN_FNAME, fname);
		cntv.put(umisDao.COLUMN_SEX, sex);
		cntv.put(umisDao.COLUMN_DOB, dob);
		cntv.put(umisDao.COLUMN_ADDRESS, address);
		cntv.put(umisDao.COLUMN_ADMDATE, admdate);
		try{
		return(database.update(umisDao.TABLE_NAME, cntv, umisDao.COLUMN_IDN+"="+sidn,null )>0);
		
		
		/*
		Cursor cursor = database.query(umisDao.TABLE_NAME,	allColumns, "_id" + " = " + id, null,
				null, null, null);
		cursor.moveToFirst();
		newAdmission = cursorToAdmission(cursor);
		cursor.close();
		*/
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
		
	}
	public void deleteComment(MdlAdmission adm) {
		String idn = adm.getIdn();
		System.out.println("Comment deleted with id: " + idn);
		database.delete(umisDao.TABLE_NAME, umisDao.COLUMN_IDN	+ " = " + idn, null);
	}

	public Cursor getAllAdmission() {
		
		return database.query(umisDao.TABLE_NAME,allColumns, null, null, null, null, null);
		/*
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MdlAdmission adm = cursorToAdmission(cursor);
			adms.add(adm);
			cursor.moveToNext();
		}
		*/
		// Make sure to close the cursor
		//cursor.close();
		//return adms;
	}
public Cursor getAdmissionByname(String name) {
		

		return database.query(umisDao.TABLE_NAME,allColumns, umisDao.COLUMN_NAME + " like'%" + name.toString()+"%'", null, null, null, null);
}
public Cursor getAdmissionByname(String idn,String name,String sex,String dob,String admdate) {
	String sqlwhere="";
	if(sex.length()>1)
	{
		sqlwhere+=" and "+umisDao.COLUMN_SEX+" ='" + sex.toString()+"'";
	}
	if(dob.length()>1)
	{
		sqlwhere+=" and "+umisDao.COLUMN_DOB+" ='" + dob.toString()+"'";
	}
	if(admdate.length()>1)
	{
		sqlwhere+=" and "+umisDao.COLUMN_ADMDATE+" ='" + admdate.toString()+"' ";
	}
return database.query(umisDao.TABLE_NAME,allColumns, umisDao.COLUMN_NAME + " like'%" + name.toString()+"%' and "+umisDao.COLUMN_IDN+" like'%" + idn.toString()+"%' "+sqlwhere , null, null, null, null);
}
public MdlAdmission getAdmission(String name) {
		MdlAdmission newAdmission= new MdlAdmission();
		try{
		Cursor cursor = database.query(umisDao.TABLE_NAME,	allColumns, umisDao.COLUMN_NAME + " ='" + name.toString()+"'", null,
				null, null, null);
		if(cursor!=null)
		{
		cursor.moveToFirst();
		newAdmission = cursorToAdmission(cursor);
		}
		cursor.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		return newAdmission;
	}
	private MdlAdmission cursorToAdmission(Cursor cursor) {
		MdlAdmission admn = new MdlAdmission();
		admn.setIdn(String.valueOf(cursor.getLong(1)));
		admn.setName(cursor.getString(2));
		admn.setFname(cursor.getString(3));
		admn.setSex(cursor.getString(4));
		admn.setDob(cursor.getString(5));
		admn.setAddress(cursor.getString(6));
		admn.setAdmissiondate(cursor.getString(7));
		return admn;
	}

}

