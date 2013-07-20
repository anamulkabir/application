package app.ennoviabd.com.umis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class UMISActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	 MenuInflater inflater = getMenuInflater();
    	    inflater.inflate(R.menu.application_menu, menu);
    	    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch(item.getItemId())
    	{
    	case R.id.admission:
    		Intent intent= new Intent(UMISActivity.this,AdmissionActivity.class);
			startActivity(intent);
    		return true;
    	case R.id.registration:
    		 
			startActivity(new Intent(UMISActivity.this,RegistrationActivity.class));
    		return true;
    	case R.id.Collection:
    		//Intent intent= new Intent(UMISActivity.this,"");
			//startActivity(intent);
    		return true;
    	default:
    	return super.onOptionsItemSelected(item);
    	}
    }
}