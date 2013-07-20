package in.wptrafficanalyzer.actionbarshareactionprovider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ShareActionProvider;

public class MainActivity extends Activity {
	
	private ShareActionProvider mShareActionProvider;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);     
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	/** Inflating the current activity's menu with res/menu/items.xml */
    	getMenuInflater().inflate(R.menu.items, menu);    	
    	
    	/** Getting the actionprovider associated with the menu item whose id is share */
    	mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.share).getActionProvider();
    	
    	/** Setting a share intent */
    	mShareActionProvider.setShareIntent(getDefaultShareIntent());
    	
    	
    	return super.onCreateOptionsMenu(menu);
    	
    }    
    
    /** Returns a share intent */
    private Intent getDefaultShareIntent(){   	
    	Intent intent = new Intent(Intent.ACTION_SEND);
    	intent.setType("text/plain");		
    	intent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
    	intent.putExtra(Intent.EXTRA_TEXT,"Extra Text");    	
    	return intent;
    }
    
}