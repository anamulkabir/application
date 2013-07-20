package com.example.wifilayout;

import java.util.Timer;
import java.util.TimerTask;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	boolean wifimenustatus=false,mobilenetworkstatus=false,airplanestatus=false,wifistatus=false;
	Animation animnetwork;
	//AnimationDrawable lightsAnimation;
	AnimationDrawable animnetworksearch;
	int counter=0;
	final int maxcounter=15,responsedelay=1000;
	Runnable mRunnable;// To handle booster animation timer
	Handler mHandler;
	private ShareActionProvider mShareActionProvider;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		mHandler=new Handler();
		
		animnetwork= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animnetworksearch);
		
		// Handling Layout Click Function form ActionPanel
		LinearLayout tmpLayout=(LinearLayout)findViewById(R.id.lyactionbarpane);
		tmpLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				
				
			}
		});
		// Handling Application Logo Click Event Handle. After Pressed It will show Custom Menu
		
		tmpLayout=(LinearLayout)findViewById(R.id.btnlogopane);
		tmpLayout.setOnClickListener(new View.OnClickListener() {	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MenuWifiShow();
				
			}
		});
		
		// Handling Application action bar playsote icon action click event 
		ImageView imgPlayStore=(ImageView)findViewById(R.id.imgplaystoreactionbar);
		imgPlayStore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				//Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.theopen.android"));
				//startActivity(intent);
				LoadShareActionMenu();
				
			}
		});
		//This function is used to handle click event on Advertise Banner Pane
		tmpLayout=(LinearLayout)findViewById(R.id.lyaddbarpane);
		tmpLayout.setOnClickListener(new View.OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				
			}
		});
		
		//This function is used to handle click event on Main Body Pane
				
		tmpLayout=(LinearLayout)findViewById(R.id.lymainbody);
		tmpLayout.setOnClickListener(new View.OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				
			}
		});
		
		// This function handle mobile network active/deactivate operation
		tmpLayout=(LinearLayout)findViewById(R.id.btnmobilenetwork);
		tmpLayout.setOnClickListener(new View.OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				MobileNetworkOnOff(mobilenetworkstatus);
				
				
			}
		});
		
		//This function handle Air Plane active/diactive operation
		tmpLayout=(LinearLayout)findViewById(R.id.btnairplane);
		tmpLayout.setOnClickListener(new View.OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				AirPlaneOnOff(airplanestatus);
			}
		});
		
		// This function handle Wifi active/diactive operation
		tmpLayout=(LinearLayout)findViewById(R.id.btnwifi);
		tmpLayout.setOnClickListener(new View.OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				WiFiOnOff(wifistatus);
				
			}
		});
		
		// This function handle Booster Status Running/Stop operation
		tmpLayout=(LinearLayout)findViewById(R.id.btnbooster);
		tmpLayout.setOnClickListener(new View.OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				BoosterOnOff(true);
				mHandler.postDelayed(mRunnable,1*1000);// push to delay execute runnable function for booster status update
				
			}
		});
		
		// This function handle Bottom Bar		
		tmpLayout=(LinearLayout)findViewById(R.id.lybottombar);
		tmpLayout.setOnClickListener(new View.OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
			}
		});
		tmpLayout=(LinearLayout)findViewById(R.id.btnlike);
		// This function handle Like button operation
		tmpLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				//Toast.makeText(getApplicationContext(), "Like Click Taped", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
	            //intent.setData(Uri.parse("market://details?id=app name"));
				intent.setData(Uri.parse("market://details"));
				//intent = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.theopen.android"));
				startActivity(intent);
				
			}
		});
		
		// This function handle Share Button operation
		tmpLayout=(LinearLayout)findViewById(R.id.btnshare);
		tmpLayout.setOnClickListener(new View.OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				//Toast.makeText(getApplicationContext(), "Share Click Taped", Toast.LENGTH_SHORT).show();
				// Popup to load Share Action Menu
				//create the send intent  
				LoadShareActionMenu();
				
				
			}
		});
		tmpLayout=(LinearLayout)findViewById(R.id.btncomment);
		// This function handle Comment Button operation
		tmpLayout.setOnClickListener(new View.OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				Toast.makeText(getApplicationContext(), "Comment Click Taped", Toast.LENGTH_SHORT).show();
			}
		});
		
		/*
		 * Declare / Handle MenuItem handler
		 */
		// Handle Main Menu List
		tmpLayout=(LinearLayout)findViewById(R.id.menulist);
		tmpLayout.setOnClickListener(new View.OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				
			}
		});
		// This function handle Menu Item1 Button operation		
		tmpLayout=(LinearLayout)findViewById(R.id.btnmenuitem1);
		tmpLayout.setOnClickListener(new View.OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutWifiAdjust();
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.lblmenuitem1)+"Click Taped", Toast.LENGTH_SHORT).show();
			}
		});
		
		// This function handle Menu Item2 Button operation		
				tmpLayout=(LinearLayout)findViewById(R.id.btnmenuitem2);
				tmpLayout.setOnClickListener(new View.OnClickListener() {
			
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LayoutWifiAdjust();
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.lblmenuitem2)+"Click Taped", Toast.LENGTH_SHORT).show();
					}
				});
				
				// This function handle Menu Item3 Button operation		
				tmpLayout=(LinearLayout)findViewById(R.id.btnmenuitem3);
				tmpLayout.setOnClickListener(new View.OnClickListener() {
			
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LayoutWifiAdjust();
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.lblmenuitem3)+"Click Taped", Toast.LENGTH_SHORT).show();
					}
				});
				// This function handle Menu Item4 Button operation		
				tmpLayout=(LinearLayout)findViewById(R.id.btnmenuitem4);
				tmpLayout.setOnClickListener(new View.OnClickListener() {
			
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LayoutWifiAdjust();
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.lblmenuitem4)+"Click Taped", Toast.LENGTH_SHORT).show();
					}
				});
				// This function handle Menu Item5 Button operation		
				tmpLayout=(LinearLayout)findViewById(R.id.btnmenuitem5);
				tmpLayout.setOnClickListener(new View.OnClickListener() {
			
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LayoutWifiAdjust();
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.lblmenuitem5)+"Click Taped", Toast.LENGTH_SHORT).show();
					}
				});
				
				// This function handle Menu Item6 Button operation		
				tmpLayout=(LinearLayout)findViewById(R.id.btnmenuitem6);
				tmpLayout.setOnClickListener(new View.OnClickListener() {
			
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LayoutWifiAdjust();
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.lblmenuitem6)+"Click Taped", Toast.LENGTH_SHORT).show();
					}
				});
				/*
				 * This function handle start scheduler and check animation status and finally stop animation
				 */		
				mRunnable=new Runnable() {

		            @Override
		            public void run() {
		                // TODO Auto-generated method stub
		            	if(counter>=maxcounter)
		            	{
		            	counter=0;
		            	BoosterOnOff(false)    ;
		            	}else
		            	{
		            		mHandler.postDelayed(mRunnable,responsedelay);
		            	}
		            	counter++;
		            }
		        };

	}
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 * This function is used to handle device menu key Press
	 */
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_MENU) {
	        // Do Stuff
			MenuWifiShow();
			return true;
	    } else {
	        return super.onKeyDown(keyCode, event);
	    }
		
	}
	/*
	 * This function display LinearLayout(Create as Own menu) to show Menu and Item List
	 */
	
	private void MenuWifiShow()
	{
		LinearLayout	wifiMenu=(LinearLayout)findViewById(R.id.menulist);// find LinearLayout id
		wifiMenu.setVisibility(View.VISIBLE);
		wifimenustatus=true;
	}
	/*
	 * This function Hide LinearLayout(Create as Own menu) to show Main / Default application view
	 */
	private void MenuWifiHide()
	{
		LinearLayout	wifiMenu=(LinearLayout)findViewById(R.id.menulist);// find LinearLayout id
		wifiMenu.setVisibility(View.GONE);	
		wifimenustatus=false;
	}
	
	/*
	 * This function is used to adjust default value UI state
	 * It handle WiFi Menu visible state
	 */
	private void LayoutWifiAdjust()
	{
		if(wifimenustatus)
		{
			MenuWifiHide();
		}
	}
	/*
	 * Handle logic to On/off mobile network button
	 */
	private void MobileNetworkOnOff(boolean onof)
	{
		if(!onof)
		{
			mobilenetworkstatus=true;
			TextView mobileNetstat= (TextView)findViewById(R.id.txtmobilenetworkstatus);
			mobileNetstat.setText(R.string.lblon);
			mobileNetstat.setTextColor(Color.parseColor( getResources().getString(R.color.green)));
		}
		else
		{
			mobilenetworkstatus=false;
			TextView mobileNetstat= (TextView)findViewById(R.id.txtmobilenetworkstatus);
			mobileNetstat.setText(R.string.lbloff);
			mobileNetstat.setTextColor(Color.parseColor( getResources().getString(R.color.red)));
		}
	}
	
	/*
	 * Handle logic to On/off mobile airplane button
	 */
	private void AirPlaneOnOff(boolean onof)
	{
		if(!onof)
		{
			airplanestatus=true;
			TextView airplanestat= (TextView)findViewById(R.id.txtairplanestatus);
			airplanestat.setText(R.string.lblon);
			airplanestat.setTextColor(Color.parseColor( getResources().getString(R.color.green)));
		}
		else
		{
			airplanestatus=false;
			TextView airplanestat= (TextView)findViewById(R.id.txtairplanestatus);
			airplanestat.setText(R.string.lbloff);
			airplanestat.setTextColor(Color.parseColor( getResources().getString(R.color.red)));
		}
		
	}
	
	/*
	 * Handle logic to On/off mobile WiFi button
	 */
	private void WiFiOnOff(boolean onof)
	{
		if(!onof)
		{
			wifistatus=true;
			TextView wifistat= (TextView)findViewById(R.id.txtwifistatus);
			wifistat.setText(R.string.lblon);
			wifistat.setTextColor(Color.parseColor( getResources().getString(R.color.green)));
		}
		else
		{
			wifistatus=false;
			TextView wifistat= (TextView)findViewById(R.id.txtwifistatus);
			wifistat.setText(R.string.lbloff);
			wifistat.setTextColor(Color.parseColor( getResources().getString(R.color.red)));
			
		}		
	}
	/*
	 * Handle logic to On/off mobile Booster button
	 */
	private void BoosterOnOff(boolean onof)
	{
		
		if(onof)
		{
			LinearLayout	btnBooster=(LinearLayout)findViewById(R.id.btnbooster);
			btnBooster.setVisibility(View.GONE);
			  
			  //animnetwork.reset();
			btnBooster=(LinearLayout)findViewById(R.id.btnboosteranim);
			btnBooster.setVisibility(View.VISIBLE);
			 ImageView imgnetworksearch=(ImageView)findViewById(R.id.imgnetworksearchanim);
			 //imgnetworksearch.startAnimation(animnetwork);
			// lightsAnimation=(AnimationDrawable) imgnetworksearch.getDrawable();
			// imgnetworksearch.setBackgroundResource(R.drawable.animnetworknavigator);
			 animnetworksearch=(AnimationDrawable)imgnetworksearch.getDrawable();
			 animnetworksearch.start();
			 btnBooster=(LinearLayout)findViewById(R.id.btnsearchinfo);
			 btnBooster.setVisibility(View.VISIBLE);
			 
			 // Hide Button of Mobile Network, Airplane, WiFi Network
			 
			 btnBooster=(LinearLayout)findViewById(R.id.lymobairwifi);
			 btnBooster.setVisibility(View.GONE);
			 
		}
		else
		{
			/*
			LinearLayout btnBooster=(LinearLayout)findViewById(R.id.btnbooster);
			btnBooster.setVisibility(View.VISIBLE);
			 animnetwork.cancel();
			 animnetwork.reset();
			 */
			animnetworksearch.stop();
		   LinearLayout	btnBooster=(LinearLayout)findViewById(R.id.lymobairwifi);
		   btnBooster.setVisibility(View.VISIBLE);
		   btnBooster=(LinearLayout)findViewById(R.id.btnbooster);
		   btnBooster.setVisibility(View.VISIBLE);
		   btnBooster=(LinearLayout)findViewById(R.id.btnboosteranim);
		   btnBooster.setVisibility(View.GONE);
		   btnBooster=(LinearLayout)findViewById(R.id.btnsearchinfo);
		   btnBooster.setVisibility(View.GONE);
			
			
		}		
	}
	private void LoadShareActionMenu()
	{
		
		openOptionsMenu();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		 getMenuInflater().inflate(R.menu.shareactionbar, menu);

		 MenuItem item = menu.findItem(R.id.menu_item_share);
		 mShareActionProvider = (ShareActionProvider)item.getActionProvider();
		 mShareActionProvider.setShareHistoryFileName(
	       ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		 mShareActionProvider.setShareIntent(createShareIntent());
	     return true;	    
		
	}
	 private Intent createShareIntent() {
		  Intent shareIntent = new Intent(Intent.ACTION_SEND);
		        shareIntent.setType("text/plain");
		        shareIntent.putExtra(Intent.EXTRA_TEXT, 
		          "http://android-er.blogspot.com/");
		        return shareIntent;
		    }
		 
		
	// Call to update the share intent
	private void setShareIntent(Intent shareIntent) {
	    if (mShareActionProvider != null) {
	        mShareActionProvider.setShareIntent(shareIntent);
	    }
	}
	
}
