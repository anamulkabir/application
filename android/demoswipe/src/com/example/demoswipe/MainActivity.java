package com.example.demoswipe;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class MainActivity extends FragmentActivity {
	SectionPagerAdapter mSectionPagerAdapter;
	ViewPager mViewPager;
	Fragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSectionPagerAdapter=new SectionPagerAdapter(getSupportFragmentManager());
		mViewPager=(ViewPager)findViewById(R.id.viewpager);
		mViewPager.setAdapter(mSectionPagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public class SectionPagerAdapter extends FragmentPagerAdapter
	{
		public SectionPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}
		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			switch(position)
			{
			case 0:
				mFragment = new Frag1();
				mFragment.setArguments(null);
				return mFragment;
			case 1:
				mFragment=new Frag2();
				mFragment.setArguments(null);
				return mFragment;
			case 2:
				mFragment=new Frag3();
				mFragment.setArguments(null);
				return mFragment;
				
			default :
				return null;
			}
			
		}
		
	}

}
