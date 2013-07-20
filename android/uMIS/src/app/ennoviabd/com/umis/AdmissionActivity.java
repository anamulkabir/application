package app.ennoviabd.com.umis;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import app.ennoviabd.com.umis.UmisCursor.ViewHolder;

public class AdmissionActivity extends Activity  {
	
	private int mhour;
	private int mminute;
	private int mYear;
	private int mMonth;
	private int mDay;
	private EditText txtbox;
	static final int TIME_DIALOG_ID = 1;
	static final int DATE_DIALOG_ID = 0;
	static int operationstatus=0;
	public int READ_CODE=1;
	private UmisDbconn ds;
	private final static String SERVICE_URI = "http://192.168.0.116/demoservice/demoservice.svc";
	private Button btnOpenPopup;
	PopupWindow popupWindow;
	ListView gridview;
	 LayoutInflater layoutInflater;  
     View popupView ;
     private UmisCursor dataSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admission);
		 Spinner spinner = (Spinner) findViewById(R.id.ddlsex);
		 txtbox=(EditText)findViewById(R.id.txt_dpdob);
		    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		            this, R.array.sex, android.R.layout.simple_spinner_item);
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    spinner.setAdapter(adapter);
	
		    txtbox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Calendar c = Calendar.getInstance();					
					txtbox=(EditText)findViewById(R.id.txt_dpdob);
					showDialog(DATE_DIALOG_ID);
					
				}
			});
		    EditText admdt=(EditText)findViewById(R.id.txt_admdate);
		    admdt.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					txtbox=(EditText)findViewById(R.id.txt_admdate);
					// TODO Auto-generated method stub
					showDialog(DATE_DIALOG_ID);					
				}
			});
		   		   
		    
		  // txtbox.setText(LoginActivity.islogin);
		    btnOpenPopup=(Button)findViewById(R.id.admsearch);
		    ds = new UmisDbconn(this);
			ds.open();
			//row= View.inflate(this, R.layout.fillgridview, null);
	}
	//-------------------------------------------update date----------------------------------------//    
	private void updateDate() {

	    txtbox.setText(
	        new StringBuilder()
	                // Month is 0 based so add 1
	                .append(mDay).append("/")
	                .append(mMonth + 1).append("/")
	                .append(mYear).append(" "));
	    //showDialog(TIME_DIALOG_ID);

	     }
	public void updatetime()
	{
	    txtbox.setText(
	            new StringBuilder()
	                    .append(pad(mhour)).append(":")
	                    .append(pad(mminute))); 
	}

	private static String pad(int c) {
	    if (c >= 10)
	    	return String.valueOf(c);
	    else
	        return "0" + String.valueOf(c);
	}
	private DatePickerDialog.OnDateSetListener mDateSetListener =
    new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, 
                              int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDate();
        }
    };
 // Timepicker dialog generation
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mhour = hourOfDay;
                mminute = minute;
                updatetime();
            }
        };
   @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);

        case TIME_DIALOG_ID:
            return new TimePickerDialog(this,
                    mTimeSetListener, mhour, mminute, false);

        }
        return null;
    }
   // this function is used when user press save button.
   // this will create either new entry or update previoud entry
   public void onClick(View view) 
   {	   
		@SuppressWarnings("unchecked")
		// Save the new comment to the database
		Calendar c = Calendar.getInstance();
		EditText	edtxt=(EditText)findViewById(R.id.txt_studname);
		String studname=edtxt.getText().toString();
		edtxt=(EditText)findViewById(R.id.txt_fname);
		String fname=edtxt.getText().toString();
		Spinner ddlsex=(Spinner)findViewById(R.id.ddlsex);
		String sex=ddlsex.getSelectedItem().toString();
		edtxt=(EditText)findViewById(R.id.txt_dpdob);
		String dob=edtxt.getText().toString();
		edtxt=(EditText)findViewById(R.id.txt_studaddress);
		String studaddress=edtxt.getText().toString();
		edtxt=(EditText)findViewById(R.id.txt_admdate);
		String admdate=edtxt.getText().toString();
		TextView errmsg= (TextView)findViewById(R.id.lbl_erro);		
	if(operationstatus==0)
	{
		String idn=""+c.get(Calendar.YEAR)+""+c.get(Calendar.MONTH)+""+c.get(Calendar.DAY_OF_MONTH)+""+c.get(Calendar.HOUR_OF_DAY)+""+c.get(Calendar.MINUTE);
		createAdmission(idn, studname, fname, sex, dob, studaddress, admdate);
	}
	else if(operationstatus==1)
	{
		String idn=((EditText)findViewById(R.id.txt_studid)).getText().toString();
		updateAdmission(idn, studname, fname, sex, dob, studaddress, admdate);
	}		
   }
   private boolean inputValidation(String pl)
   {
	   if(pl.equals("main"))
	   {
	   TextView errmsg= (TextView)findViewById(R.id.lbl_erro);
	   errmsg.setText("");
	   errmsg.setTextColor(Color.BLACK);
	   EditText	edtxt=(EditText)findViewById(R.id.txt_studname);
	   edtxt.setTextColor(Color.BLACK);
	   if(edtxt.getText().length()<1)
	   {
		   edtxt.setTextColor(Color.RED);
		   return false;
	   }
	  edtxt=(EditText)findViewById(R.id.txt_dpdob);
	  edtxt.setTextColor(Color.BLACK);
	   if(!ApplicationValidation.isvalidDate(edtxt.getText().toString()))
	   {
		   edtxt.setTextColor(Color.RED);
		   return false;
	   }
	   edtxt=(EditText)findViewById(R.id.txt_admdate);
	   edtxt.setTextColor(Color.BLACK);
	   if(!ApplicationValidation.isvalidDate(edtxt.getText().toString()))
	   {
		   edtxt.setTextColor(Color.RED);
		   return false;
	   }
	  }
	   else if(pl.equals("popup"))
	   {
		   TextView errmsg= (TextView)popupView.findViewById(R.id.lbl_erropop);
		   errmsg.setText("");
		   errmsg.setTextColor(Color.BLACK);
		   EditText	edtxt=(EditText)popupView.findViewById(R.id.txt_dpdobpop);
			  edtxt.setTextColor(Color.BLACK);
			   if(!ApplicationValidation.isvalidDate(edtxt.getText().toString()))
			   {
				   edtxt.setTextColor(Color.RED);
				   return false;
			   }
			   edtxt=(EditText)popupView.findViewById(R.id.txt_admdatepop);
			   edtxt.setTextColor(Color.BLACK);
			   if(!ApplicationValidation.isvalidDate(edtxt.getText().toString()))
			   {
				   edtxt.setTextColor(Color.RED);
				   return false;
			   }		   
	   }
	   return true;
   }
   private void createAdmission(String idn,String studname,String fname,String sex,String dob,String studaddress,String admdate)
   {
	   TextView errmsg= (TextView)findViewById(R.id.lbl_erro);
	   if(inputValidation("main"))
		{		
		   if(ds.createAdmission(idn,studname,fname,sex,dob,studaddress,admdate))
			{
			   	freshAll();
			   	errmsg.setText("Save successfully");			
			}
			else
			{
				errmsg.setText("Error Occur");
			}
		}	   
   }
   private void updateAdmission(String idn,String studname,String fname,String sex,String dob,String studaddress,String admdate)
   {
	   TextView errmsg= (TextView)findViewById(R.id.lbl_erro);
	   if(inputValidation("main") && idn.length()>0)
		{
		   if(ds.updateAdmission(idn,studname,fname,sex,dob,studaddress,admdate))
			{
			freshAll();
			operationstatus=0;
			errmsg.setText("Save successfully");			
			}
			else
			{
				errmsg.setText("Error Occur");
			}
		}
  }
   public void onSearchClick(View view) {
		//ArrayAdapter<MdlAdmission> adapter =new ArrayAdapter<MdlAdmission>(this,R.id.txt_studid);		
		//Save the new comment to the database	 
		popUpLoad();	
}
   public void onClearClick(View view) {		
	   freshAll();
	   operationstatus=0;	
}
   public void onSearchClickpop(View view) {
		//ArrayAdapter<MdlAdmission> adapter =new ArrayAdapter<MdlAdmission>(this,R.id.txt_studid);		
		// Save the new comment to the database
	   //final LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
	    //View popupView = layoutInflater.inflate(R.layout.searchadmission,null);
	EditText	studname=(EditText)popupView.findViewById(R.id.txt_studnamepop);	
	EditText	studid=(EditText)popupView.findViewById(R.id.txt_studidpop);	
	Spinner ddlsex=(Spinner)popupView.findViewById(R.id.ddlsexpop);
	String sex=ddlsex.getSelectedItem().toString();
	EditText	dob=(EditText)popupView.findViewById(R.id.txt_dpdobpop);	
	EditText	admdt=(EditText)popupView.findViewById(R.id.txt_admdatepop);	
	if(inputValidation("popup"))
	fillAdmissionResult(searchAdmission(studid.getText().toString(),studname.getText().toString(),sex,dob.getText().toString(),admdt.getText().toString()));				
}
   private Cursor searchAdmission(String idn,String name,String sex,String dob,String admdate)
   {
	  	 Cursor cursor=ds.getAdmissionByname(idn,name,sex,dob,admdate);	    
		 return cursor;	   
   }
   private void fillAdmissionResult(Cursor cursor)
   {	  
		String[] cols = new String[] { umisDao.COLUMN_IDN,umisDao.COLUMN_NAME};
	    int[]   views = new int[] { R.id.txt_gv_studid, R.id.txt_gv_studname };
		if(cursor!=null){		
			//	 ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
				 try{
					// adapter = new UmisCursor(this, R.layout.fillgridview, cursor, cols, views);
					 //dataSource = new SimpleCursorAdapter(this, R.layout.fillgridview, cursor, cols, views);
					 dataSource = new UmisCursor(this, R.layout.fillgridview, cursor, cols, views);
					 gridview.setAdapter(dataSource);
				//Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				//intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				//startActivityForResult(intent, READ_CODE); 	
				// popupWindow.showAsDropDown(btnOpenPopup,10,10);
			  	 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }				 
			}	   
   }
   
   private MdlAdmission AdmissionEntityFill(Cursor cursor)
   {
	   MdlAdmission mdl=new MdlAdmission();
		
		if(cursor!=null){	
			 cursor.moveToPosition(0);			
				 try{
					 mdl.setIdn(cursor.getString(1));
					 mdl.setName(cursor.getString(2));
					 mdl.setFname(cursor.getString(3));
					 mdl.setSex(cursor.getString(4));
					 mdl.setDob(cursor.getString(5));
					 mdl.setAddress(cursor.getString(6));
					 mdl.setAdmissiondate(cursor.getString(7));
			  	 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }				 
			}
		return mdl;
  }
   private void popUpLoad()
   {
	   View v=View.inflate(this,R.layout.lvheader,null);
	   layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
	    popupView = layoutInflater.inflate(R.layout.searchadmission, null);
	    //popupWindow = new PopupWindow(this);
	    popupWindow = new PopupWindow(popupView,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,true);
	    //popupWindow = new PopupWindow(this);
	    //popupWindow = new PopupWindow(popupView,  LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,    true); 
	    popupWindow.setBackgroundDrawable(new BitmapDrawable());
	    popupWindow.setOutsideTouchable(false);
	    popupWindow.showAtLocation(popupView, Gravity.TOP, 10, 20);
	    gridview = (ListView) popupView.findViewById(R.id.lv_admission);
		gridview.addHeaderView(v);	
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getBaseContext(), R.array.sex, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    Spinner popsex = (Spinner)popupView.findViewById(R.id.ddlsexpop);
	    popsex.setAdapter(adapter);
	    /*PhoneNumberFormattingTextWatcher() EditText inputField = (EditText) popupView.findViewById(R.id.txt_dpdobpop);
	    inputField.addTextChangedListener(new Numberfo);
		gridview.setOnItemClickListener(new OnItemClickListener() {
		    	@Override
		    	public void onItemClick(AdapterView<?> arg0, View arg1,
		    			int arg2, long arg3) {
		    		// TODO Auto-generated method stub
		    		TextView col1 = (TextView) arg1.findViewById(R.id.txt_gv_studid);
		    		Toast.makeText(getApplicationContext(),col1.getText(), Toast.LENGTH_LONG).show();		    		
		    	}
			});*/
 }
   private void getAdmissionsrv() {
	    try {
	        // Send GET request to <service>/GetPlates
	        HttpGet request = new HttpGet(SERVICE_URI + "/GetAllAdmission");
	        request.setHeader("Accept", "application/json");
	        request.setHeader("Content-type", "application/json");
	        DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpResponse response = httpClient.execute(request);
	        HttpEntity responseEntity = response.getEntity();
	        // Read response data into buffer
	        char[] buffer = new char[(int)responseEntity.getContentLength()];
	        InputStream stream = responseEntity.getContent();
	        InputStreamReader reader = new InputStreamReader(stream);
	        reader.read(buffer);
	        stream.close();
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	        //JSONObject plates = new JSONObject(new String(buffer));
	        JSONArray plates = new JSONArray(new String(buffer));
	        for(Integer i=0; i< plates.length(); i++){
	            try{
	                //Get My JSONObject and grab the String Value that I want.
	                String obj = plates.getJSONObject(i).getString("Studname");
	                //Add the string to the list
	                adapter.add(obj);
	            }catch(JSONException e){

	            }
	        }
	        	        
	        // Reset plate spinner
	       // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	       Spinner spinner = (Spinner) findViewById(R.id.ddlsex);
	       spinner.setAdapter(adapter);
	       // plateSpinner.setAdapter(adapter);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
  
   public void fillUIds(MdlAdmission adm)
   {
	   if(adm!=null)
	   {
	   EditText idn= (EditText)findViewById(R.id.txt_studid);
	   idn.setText(adm.getIdn());
	   EditText edtxt= (EditText)findViewById(R.id.txt_studname);
	   edtxt.setText(adm.getName());
	   edtxt= (EditText)findViewById(R.id.txt_fname);
	   edtxt.setText(adm.getFname());
	   Spinner spinner = (Spinner) findViewById(R.id.ddlsex);
	   ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.sex, android.R.layout.simple_spinner_item);
	   int pos=adapter.getPosition(adm.getSex());
	   spinner.setSelection(pos);
	   edtxt= (EditText)findViewById(R.id.txt_dpdob);
	   edtxt.setText(adm.getDob());
	   edtxt= (EditText)findViewById(R.id.txt_studaddress);
	   edtxt.setText(adm.getAddress());
	   edtxt= (EditText)findViewById(R.id.txt_admdate);
	   edtxt.setText(adm.getAdmissiondate());
	   }	   
   }
   public void freshAll()
   {
	   EditText idn= (EditText)findViewById(R.id.txt_studid);
	   idn.setText("");
	   EditText edtxt= (EditText)findViewById(R.id.txt_studname);
	   edtxt.setText("");
	   edtxt= (EditText)findViewById(R.id.txt_fname);
	   edtxt.setText("");
	   Spinner spinner = (Spinner) findViewById(R.id.ddlsex);
	   spinner.setSelection(0);
	   edtxt= (EditText)findViewById(R.id.txt_dpdob);
	   edtxt.setText("");
	   edtxt= (EditText)findViewById(R.id.txt_studaddress);
	   edtxt.setText("");
	   edtxt= (EditText)findViewById(R.id.txt_admdate);
	   edtxt.setText("");	   
   }
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
	if (requestCode == READ_CODE)
		if (resultCode == Activity.RESULT_OK) {
		String contents = data.getStringExtra("SCAN_RESULT");
		String format = data.getStringExtra("SCAN_RESULT_FORMAT");
		EditText	fname=(EditText)findViewById(R.id.txt_fname);
		fname.setText(contents);
		}
}
public void onUpdateClick(View view) {
	@SuppressWarnings("unchecked")
	//ArrayAdapter<MdlAdmission> adapter =new ArrayAdapter<MdlAdmission>(this,R.id.txt_studid);
	ViewHolder vh = getViewHolder(view);
	popUPUnload();
	fillUIds(AdmissionEntityFill(searchAdmission("",vh.id , "", "","")));
	operationstatus=1;
	//Toast.makeText(view.getContext(),String.valueOf(vh.position), Toast.LENGTH_LONG).show();
	
}
public  void onRecordSelectClick(View view) {
	
	//ArrayAdapter<MdlAdmission> adapter =new ArrayAdapter<MdlAdmission>(this,R.id.txt_studid);
	//popUPUnload();
	ViewHolder vh = getViewHolder(view);
	//Toast.makeText(context,String.valueOf(vh.position), Toast.LENGTH_LONG).show();
	Toast.makeText(view.getContext(),String.valueOf(vh.position), Toast.LENGTH_LONG).show();	
}
protected void popUPUnload()
{
	popupWindow.dismiss();
}
public ViewHolder getViewHolder(View v)
{
    if(v.getTag() == null)
    {
        return getViewHolder((View)v.getParent());
    }
    return (ViewHolder)v.getTag();
}
  
}

