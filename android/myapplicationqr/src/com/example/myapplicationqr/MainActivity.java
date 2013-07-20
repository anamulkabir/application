package com.example.myapplicationqr;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView txtresultscan;
	String companyurl="localite-inc.com";//"http://dev1.localite-inc.com";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txtresultscan=(TextView)findViewById(R.id.txtresultqrscan);
		
		Button btnScanQR=(Button)findViewById(R.id.btnsearchqrcode);// find reference of search/scan QR button
		// handle click event that call QR application
		btnScanQR.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				try {

				    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
					//Intent intent=new Intent(getApplicationContext(),CaptureActivity.class);
				    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes			    

				    startActivityForResult(intent, 0);
					//integrator.initiateScan();

				} catch (Exception e) {

				    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
				    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
				    startActivity(marketIntent);

				}
				
			}
		});
	}
	// handle QR code data read by scanner
	
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	if (requestCode == 0) {
		
		if (resultCode == RESULT_OK) {
		String tmpUrl=data.getStringExtra("SCAN_RESULT");
		if(checkValidURL(tmpUrl))
		{
			if(checkCompanyURL(tmpUrl))
			{
				txtresultscan.setText(tmpUrl);
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(tmpUrl));
				startActivity(i);
				
			}
			else
			{
				Toast.makeText(getApplicationContext(), "The url not permited by this application", Toast.LENGTH_SHORT);
				txtresultscan.setText("Not permited URL");
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_SHORT);
			txtresultscan.setText("Invalid URL");
		}
		
		
		} 
		else if (resultCode == RESULT_CANCELED) 
		{
		//Toast.makeText(getApplicationContext(), "Press a button to start a scan.Scan cancelled.",Toast.LENGTH_SHORT).show();
			txtresultscan.setText("Scan could not read properly");
		
		}
	}
}
// this function is capable to validate URL/URI of specific
private boolean checkValidURL(String url)
{
	if (URLUtil.isValidUrl(url)) {
		return true;
		}
	return false;
}
private boolean checkCompanyURL(String url)
{
	if(url.contains(companyurl)){
		return true;
	}
	return false;
}
	

}
