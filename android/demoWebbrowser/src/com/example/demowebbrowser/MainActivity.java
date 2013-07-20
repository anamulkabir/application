package com.example.demowebbrowser;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		WebView weV = (WebView) findViewById(R.id.webMybrowser);
		WebSettings settings = weV.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		settings.setSupportZoom(true);
		settings.setSupportMultipleWindows(true);
		weV.loadUrl("http://www.ennoviabd.com");
		//Uri uri = Uri.parse("https://www.google.com");
        //startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
