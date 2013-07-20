package app.ennoviabd.com.umis;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

public class RegistrationActivity extends Activity {
	
	ArrayList<HashMap<String, String>> courselist = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> map = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		 Spinner spinner = (Spinner) findViewById(R.id.ddl_semester);
		 ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.semesters, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			//adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);
			AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocom_course);
			// Get the string array
			String[] cources = getResources().getStringArray(R.array.course);
			// Create the adapter and set it to the AutoCompleteTextView 
			ArrayAdapter<String> courseadapter = 
			        new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, cources);
			textView.setThreshold(1); 
			textView.setAdapter(courseadapter);
	}
	
	   public void onCourseAddClick(View view) {
			//ArrayAdapter<MdlAdmission> adapter =new ArrayAdapter<MdlAdmission>(this,R.id.txt_studid);		
			//Save the new comment to the database
		   
		   map = new HashMap<String, String>();
		   map.put("crsname", "101");
			map.put("crscode", "");
			courselist.add(map);
		
	}

}
