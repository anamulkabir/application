package develop.ennoviabd.temperature;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Activity2 extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity2);
	Button btn = (Button) findViewById(R.id.button2);
	btn.setOnClickListener(new View.OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent data = new Intent();
			//---get the EditText view---
			EditText txt_username =	(EditText) findViewById(R.id.editText2);
			//---set the data to pass back---
			data.setData(Uri.parse(	txt_username.getText().toString()));
			setResult(RESULT_OK, data);
			//---closes the activity---
			finish();
			
		}
	});
	}

}
