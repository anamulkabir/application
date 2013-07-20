package app.ennoviabd.com.umis;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/*
 * This class is responsible to handle loging mechanism
 * Take 2 input username, password and send to validation server using http parameter
 * aspecting 1 if validate else return 0
 * use progress bar between infromation exchange with validation server and application
 * Call CustomHttpClient.java customize class static function to user http request and response
 */
public class LoginActivity extends Activity {
	EditText uname,passwd;
	TextView errmsg;
	Button login;	
    ProgressDialog progDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		uname=(EditText)findViewById(R.id.txt_un);
		passwd=(EditText)findViewById(R.id.txt_upass);
		login=(Button)findViewById(R.id.btn_login);
		errmsg=(TextView)findViewById(R.id.labl_erro);
		//ActionBar actionBar = getActionBar();
		//actionBar.hide();
		login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GlobalSettings.islogin=false;
				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				postParameters.add(new BasicNameValuePair("Username", uname.getText().toString()));
				postParameters.add(new BasicNameValuePair("Password", passwd.getText().toString()));
				String response=null;
				showProgressDialog(1);
				try{
					response=CustomHttpClient.executeHttpPost("http://210.4.76.245/appservice/Default.aspx?",postParameters);
					String res=response.toString();
					String nl=System.getProperty("line.separator");
					res=res.replace("\n", "");
					progDialog.dismiss();
					if(res.equals("1"))
					{
						//errmsg.setText("Correct User Name and Password");
						GlobalSettings.islogin=true;
						GlobalSettings.username=uname.getText().toString();
						Intent intent= new Intent(LoginActivity.this,UMISActivity.class);
						startActivity(intent);
						finish();
					}
					else
					{
						errmsg.setText("Incorrect User Name / Password");
						
					}
							
				}catch(Exception e)
				{
					errmsg.setText(e.toString());
				}
				
			}
			
		});
	}
	
    protected void showProgressDialog(int id) {
        switch(id) {
        case 1:                      // Spinner
            progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setMessage("Loading...");
           // progThread = new ProgressThread(handler,100,40);
           // progThread.start();         		
          //  progDialog.setCancelable(true);
            progDialog.show();


        }
    }	

}
