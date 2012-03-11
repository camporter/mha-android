package com.teamacra.myhomeaudio.ui;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.teamacra.myhomeaudio.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MyHomeAudioActivity extends SherlockActivity implements OnNavigationListener {
	
	private String[] streams;

	private ViewPager mPager;
	private MainPagerAdapter mAdapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		streams = new String[2];
		streams[0]="hi";
		streams[1]="bye";
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.main);
		
		mPager = (ViewPager) findViewById(R.id.pager);
		
		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(context, R.layout.sherlock_spinner_item, streams);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
		
	}
	
	public void onStart() {
		super.onStart();
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		//TextView tv = (TextView) this.findViewById(R.id.textView1);
		//tv.setText("selected: "+streams[itemPosition]);
		return true;
	}
	
	public static class MainPagerAdapter extends FragmentPagerAdapter {
		public MainPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return null;
		}

		@Override
		public int getCount() {
			return 0;
		}
	}
}
