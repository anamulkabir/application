package com.ennoviabd.app.webbookmarker;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
 EditText txtBrowseraddress;
 ListView lvbookmark;
 protected static final int TIMER_RUNTIME = 10000; // in ms --> 10s
 protected boolean mbActive;
 protected ProgressBar mProgressBar;
 String[] columns = new String[] {BookMarkContent.BOOKMARK_WEB,BookMarkContent.BOOKMARK_IDN};
	int[] views = new int[] {R.id.txtvWeb,R.id.txtBookMarkid};
  WebView weV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txtBrowseraddress=(EditText)findViewById(R.id.txtBrowseString);
		weV = (WebView) findViewById(R.id.webMyBrowser);
		Button btnBrowse=(Button)findViewById(R.id.btnBrowse);
		Button btnQRcode=(Button)findViewById(R.id.btnQRcode);
		Button btnAddWeb=(Button)findViewById(R.id.btnAddtoBook);
		mProgressBar = (ProgressBar)findViewById(R.id.adprogress_progressBar);
		btnBrowse.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				loadWebAddress();
				
			}
		});
		//final IntentIntegrator integrator = new IntentIntegrator(this);
	  btnQRcode.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {

			    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				//Intent intent=new Intent(getApplicationContext(),CaptureActivity.class);
			    intent.putExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE"); // "PRODUCT_MODE for bar codes			    

			    startActivityForResult(intent, 0);
				//integrator.initiateScan();

			} catch (Exception e) {

			    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
			    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
			    startActivity(marketIntent);

			}			
		}
	});
	  
	  btnAddWeb.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ContentValues cValues = new ContentValues();
			cValues.put(BookMarkContent.BOOKMARK_WEB, txtBrowseraddress.getText().toString());			
			
			try{
			Uri uri ;
			
				uri= getContentResolver().insert(BookMarkContent.Content_Uri, cValues);
				Toast.makeText(getApplicationContext(), "Web address save at your list",Toast.LENGTH_SHORT).show();
			
			}
			catch(Error er)
			{
				
			}
	
			
		}
	});
	  lvbookmark=(ListView)findViewById(R.id.lvWebaddress);
		
		Uri allbookmarks = Uri.parse(BookMarkContent.Content_Uri.toString());
		try{
			// Cursor to get all event information from the event content provider
			// it returns database cursor pointer
			Cursor c = managedQuery(allbookmarks, null, null, null, null);
						
			// set layout, cursor , columns and values of cursoradapter
			CustomCursor bookmarkDataAdapter =new CustomCursor(this, R.layout.listview_row, c, columns, views);
			
			// binding list view
			lvbookmark.setAdapter(bookmarkDataAdapter);
			
		}
		catch(Exception e)
		{
			
		}
		
		lvbookmark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				// TODO Auto-generated method stub
				 Cursor cursor = (Cursor) lvbookmark.getItemAtPosition(position);
				 
				   // Get the web address row from  the database.
				   String webadd =cursor.getString(cursor.getColumnIndexOrThrow(BookMarkContent.BOOKMARK_WEB));
				   txtBrowseraddress.setText(webadd);
				   loadWebAddress();
				   	
				
			}
		});
		lvbookmark.setVisibility(View.GONE);
		Button btnviewlist=(Button)findViewById(R.id.btnlistshow);
		btnviewlist.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				lvbookmark.setVisibility(View.VISIBLE);	
				weV.setVisibility(View.GONE);
			}
		});
		
		weV.setWebViewClient(new WebViewClient(){
			public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) 
			{
				mProgressBar.setVisibility(View.VISIBLE);	
			}
			public void onPageFinished(WebView view, String url) 
			{
				mProgressBar.setVisibility(View.GONE);
			}
			
		}
		);
	  
	}
	

@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
	
			if (resultCode == RESULT_OK) {
			
				txtBrowseraddress.setText(data.getStringExtra("SCAN_RESULT"));
				loadWebAddress();
			} 
			else if (resultCode == RESULT_CANCELED) 
			{
			Toast.makeText(getApplicationContext(), "Press a button to start a scan.Scan cancelled.",Toast.LENGTH_SHORT).show();
			
			}
		}
		
		    
	}
private void loadWebAddress()
{
	String regex="^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	String webadd=webAddressCreate(txtBrowseraddress.getText().toString());
	if(IsMatch(webadd, regex))
	{
				
		lvbookmark.setVisibility(View.GONE);
		weV.setVisibility(View.VISIBLE);
		WebSettings settings = weV.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		settings.setSupportZoom(true);
		settings.setSupportMultipleWindows(true);
		weV.loadUrl(webadd);
		/*
		 if (!URLUtil.isValidUrl(url)) {
			Toast.makeText(this, "Invalid URL specified", Toast.LENGTH_SHORT).show();
			return;
			}
		 */
		
	}
	else
	{
		Toast.makeText(this, "Invalid Web Address", Toast.LENGTH_SHORT).show();
		
	}
}


	private static boolean IsMatch(String s, String pattern) {
	    try {
	        Pattern patt = Pattern.compile(pattern);
	        Matcher matcher = patt.matcher(s);
	        return matcher.matches();
	    } catch (RuntimeException e) {
	    return false;
	    }
	}
	private String webAddressCreate(String webadd)
	{
		String str;
		if(webadd.contains("http://")||webadd.contains("https://"))
		{
			return webadd;
		}
		else
		{
			str="http://"+webadd;
			
		}
		
		return str;
	}
	

}
