package com.ennoviabd.app.earthquakedemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.net.ParseException;
import android.os.Binder;
import android.os.IBinder;

public class EarthQuakeService extends Service {
	private Timer updateTimer;
	private float minimumMagnitude;
	public static final String NEW_EARTHQUAKE_FOUND = "New_Earthquake_Found";
	private final IBinder binder = new MyBinder();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		SharedPreferences prefs = getSharedPreferences("USER_PREFERENCE",Activity.MODE_PRIVATE);
				int minMagIndex = prefs.getInt("PREF_MIN_MAG", 0);
				if (minMagIndex < 0)
				minMagIndex = 0;
				int freqIndex = prefs.getInt("PREF_UPDATE_FREQ", 0);
				if (freqIndex < 0)
				freqIndex = 0;
				boolean autoUpdate = prefs.getBoolean("PREF_AUTO_UPDATE", true);
				Resources r = getResources();
				int[] minMagValues = r.getIntArray(R.array.magnitude);
				int[] freqValues = r.getIntArray(R.array.update_freq_values);
				minimumMagnitude = minMagValues[minMagIndex];
				int updateFreq = freqValues[freqIndex];
				updateTimer.cancel();
				if (autoUpdate) {
					updateTimer = new Timer("earthquakeUpdates");
					updateTimer.scheduleAtFixedRate(doRefresh, 0, updateFreq*6*1000);
					}
					else
					refreshEarthquakes();
	}
	private TimerTask doRefresh = new TimerTask() {
		public void run() {
		refreshEarthquakes();
		}
		};
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		updateTimer = new Timer("earthquakeUpdates");
		
		
	}
	
	private void addNewQuake(Quake _quake) {
		ContentResolver cr = getContentResolver();
		// Construct a where clause to make sure we don�t already have this
		// earthquake in the provider.
		String w = ""+_quake.getDate().getTime();
		// If the earthquake is new, insert it into the provider.
		Cursor c = cr.query(EarthquakeProvider.CONTENT_URI, null, w, null, null);
		if (c.getCount()==0){
		ContentValues values = new ContentValues();
		values.put(EarthquakeProvider.KEY_DATE, _quake.getDate().getTime());
		values.put(EarthquakeProvider.KEY_DETAILS, _quake.getDetails());
		double lat = _quake.getLocation().getLatitude();
		double lng = _quake.getLocation().getLongitude();
		values.put(EarthquakeProvider.KEY_LOCATION_LAT, lat);
		values.put(EarthquakeProvider.KEY_LOCATION_LNG, lng);
		values.put(EarthquakeProvider.KEY_LINK, _quake.getLink());
		values.put(EarthquakeProvider.KEY_MAGNITUDE, _quake.getMagnitude());
		cr.insert(EarthquakeProvider.CONTENT_URI, values);
		announceNewQuake(_quake);
		}
		c.close();
	}
	private void announceNewQuake(Quake quake) {
		Intent intent = new Intent(NEW_EARTHQUAKE_FOUND);
		intent.putExtra("date", quake.getDate().getTime());
		intent.putExtra("details", quake.getDetails());
		intent.putExtra("longitude", quake.getLocation().getLongitude());
		intent.putExtra("latitude", quake.getLocation().getLatitude());
		intent.putExtra("magnitude", quake.getMagnitude());
		sendBroadcast(intent);
		}
	private void refreshEarthquakes() {
		// Get the XML
		URL url;
		try {
		String quakeFeed = getString(R.string.quake_feed);
		url = new URL(quakeFeed);
		URLConnection connection;
		connection = url.openConnection();
		HttpURLConnection httpConnection = (HttpURLConnection)connection;
		int responseCode = httpConnection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
		InputStream in = httpConnection.getInputStream();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		// Parse the earthquake feed.
		Document dom = db.parse(in);
		Element docEle = dom.getDocumentElement();
		// Get a list of each earthquake entry.
		NodeList nl = docEle.getElementsByTagName("entry");
		if (nl != null && nl.getLength() > 0) {
			
			for (int i = 0 ; i < nl.getLength(); i++) {
				Element entry = (Element)nl.item(i);
				Element title;
				title = (Element)entry.getElementsByTagName("title").item(0);
				Element g;
				g = (Element)entry.getElementsByTagName("georss:point").item(0);
				Element when;
				when = (Element)entry.getElementsByTagName("updated").item(0);
				Element link = (Element)entry.getElementsByTagName("link").item(0);
				String details = title.getFirstChild().getNodeValue();
				String hostname = "http://earthquake.usgs.gov";
				String linkString = hostname + link.getAttribute("href");
				String point = g.getFirstChild().getNodeValue();
				String dt = when.getFirstChild().getNodeValue();
				SimpleDateFormat sdf;
				sdf = new SimpleDateFormat("yyyy-MM-dd�T�hh:mm:ss�Z�");
				Date qdate = new GregorianCalendar(0,0,0).getTime();
				try {
				qdate = sdf.parse(dt);
				} catch (Exception e) {
				e.printStackTrace();
				}
				String[] location = point.split(" ");
				Location l = new Location("dummyGPS");
				l.setLatitude(Double.parseDouble(location[0]));
				l.setLongitude(Double.parseDouble(location[1]));
				String magnitudeString = details.split(" ")[1];
				int end = magnitudeString.length()-1;
				double magnitude;
				magnitude = Double.parseDouble(magnitudeString.substring(0, end));
				details = details.split(",")[1].trim();
				Quake quake = new Quake(qdate, details, l, magnitude, linkString);
				// Process a newly found earthquake
				addNewQuake(quake);
				}
				}
				}
				} catch (MalformedURLException e) {
				e.printStackTrace();
				} catch (IOException e) {
				e.printStackTrace();
				} catch (ParserConfigurationException e) {
				e.printStackTrace();
				} catch (SAXException e) {
				e.printStackTrace();
				}
				finally {
				}
		}
	public class MyBinder extends Binder {
		EarthQuakeService getService() {
		return EarthQuakeService.this;
		}
	}
	

}
