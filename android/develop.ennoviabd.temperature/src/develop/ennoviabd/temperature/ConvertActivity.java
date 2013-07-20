package develop.ennoviabd.temperature;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.app.ActionBar;
import android.util.Log;
import android.view.Window;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.view.KeyEvent;
import android.content.Intent;

public class ConvertActivity extends Activity {
    /** Called when the activity is first created. */
	private EditText text;
	//String tag ="Events";
	public String tag="Events";
	CharSequence[] items = { "Google", "Apple", "Microsoft"};
	boolean[] itemsChecked = new boolean [items.length];
	int request_Code = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
       // text=(EditText) findViewById(R.id.editText1);
        Log.d(tag, "In the onCreate() event");
       // ActionBar actionBar = getActionBar();
    	// add the custom view to the action bar
    	//actionBar.setCustomView(R.layout.main);
    	//actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
    	// | ActionBar.DISPLAY_SHOW_HOME);
        Button btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        showDialog(0);
        }
        });
    }
    public void myClickHandler(View view){
    	switch (view.getId()) {
		case R.id.button1:
			RadioButton celsiusButton = (RadioButton) findViewById(R.id.radio0);
			RadioButton fahrenheitButton = (RadioButton) findViewById(R.id.radio1);
			if (text.getText().length() == 0) {
				Toast.makeText(this, "Please enter a valid number",
						Toast.LENGTH_LONG).show();
				return;
			}

			float inputValue = Float.parseFloat(text.getText().toString());
			if (celsiusButton.isChecked()) {
				text.setText(String
						.valueOf(convertFahrenheitToCelsius(inputValue)));
				celsiusButton.setChecked(false);
				fahrenheitButton.setChecked(true);
			} else {
				text.setText(String
						.valueOf(convertCelsiusToFahrenheit(inputValue)));
				fahrenheitButton.setChecked(false);
				celsiusButton.setChecked(true);
			}
			break;
		}
    }
 // Converts to celsius
 	private float convertFahrenheitToCelsius(float fahrenheit) {
 		return ((fahrenheit - 32) * 5 / 9);
 	}

 	// Converts to fahrenheit
 	private float convertCelsiusToFahrenheit(float celsius) {
 		return ((celsius * 9) / 5) + 32;
 	}
 	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menueitem1:
			Toast.makeText(this, "Menu Item 1 selected", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.menueitem2:
			Toast.makeText(this, "Menu item 2 selected", Toast.LENGTH_SHORT)
					.show();
			break;

		default:
			break;
		}

		return true;
	}
	public void onStart()
	{
	super.onStart();
	Log.d(tag, "In the onStart() event");
	}
	public void onRestart()
	{
	super.onRestart();
	Log.d(tag, "In the onRestart() event");
	}
	public void onResume()
	{
	super.onResume();
	Log.d(tag, "In the onResume() event");
	}
	public void onPause()
	{
	super.onPause();
	Log.d(tag, "In the onPause() event");
	}
	public void onStop()
	{
	super.onStop();
	Log.d(tag, "In the onStop() event");
	}
	public void onDestroy(){
	super.onDestroy();
	Log.d(tag, "In the onDestroy() event");
	}
	
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
		return new AlertDialog.Builder(this)
		.setIcon(R.drawable.drawablebk)
		.setTitle("This is a dialog with some simple text...")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,	int whichButton)
		{
		Toast.makeText(getBaseContext(),"OK clicked!", Toast.LENGTH_SHORT).show();
		}
		}).setNegativeButton("Cancel", new	DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,	int whichButton)
		{
		Toast.makeText(getBaseContext(),"Cancel clicked!", Toast.LENGTH_SHORT).show();
		}
		}).setMultiChoiceItems(items, itemsChecked, new		DialogInterface.OnMultiChoiceClickListener() {
			//@Override
			public void onClick(DialogInterface dialog, int which,boolean isChecked) {
			Toast.makeText(getBaseContext(),items[which] + (isChecked ? " checked!":" unchecked!"),	Toast.LENGTH_SHORT).show();
			}
			}
			).create();
			}
			return null;
			}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
	{
	//startActivity(new Intent("develop.ennoviabd.ACTIVITY2"));
		startActivityForResult(new Intent("develop.ennoviabd.ACTIVITY2"),request_Code);
	}
	return false;
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	if (requestCode == request_Code) {
	if (resultCode == RESULT_OK) {
	Toast.makeText(this,data.getData().toString(),
	Toast.LENGTH_SHORT).show();
	}
	}
	}
}