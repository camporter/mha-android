package com.teamacra.myhomeaudio.ui;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.http.HttpClient;
import com.teamacra.myhomeaudio.http.HttpNodeClient;
import com.teamacra.myhomeaudio.http.HttpStream;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyHomeAudioActivity extends SherlockActivity implements OnNavigationListener {
	
	private String[] streams;

	private ViewPager mPager;
	private MainPagerAdapter mAdapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		MHAApplication app = (MHAApplication) getApplication();
		HttpStream hs = new HttpStream(app, app.getSessionId());
		
		streams = hs.getStreamList();
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Add Stream").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("Logout").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		return true;
	}
	
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		MHAApplication app = (MHAApplication) this.getApplication();
		HttpNodeClient hnr = new HttpNodeClient(app);
		hnr.getNodes(app.getSessionId());
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
	
	private class AddStream extends AsyncTask<String, Void, String> {

		private final ProgressDialog progressDialog = new ProgressDialog(
				MyHomeAudioActivity.this);
		private AlertDialog failureDialog;
		MHAApplication app = (MHAApplication) MyHomeAudioActivity.this
				.getApplication();

		protected void onPreExecute() {
			
		}

		protected String doInBackground(String... args) {
			String username = args[0];
			String password = args[1];

			// Do actual login here...
			HttpClient client = new HttpClient(app);
			return client.login(username, password);
		}

		protected void onPostExecute(final String sessionId) {

			if (sessionId != null) {
				// Login successful, set our app variables
				String username = ((EditText) MyHomeAudioActivity.this
						.findViewById(R.id.usernameEditText)).getText()
						.toString();
				String password = ((EditText) MyHomeAudioActivity.this
						.findViewById(R.id.passwordEditText)).getText()
						.toString();

				app.setLoggedIn(username, password, sessionId);
				this.progressDialog.dismiss();

				Intent mhaIntent = new Intent(MyHomeAudioActivity.this,
						MyHomeAudioActivity.class);
				MyHomeAudioActivity.this.startActivity(mhaIntent);
			} else {
				// Login failed, let the user know with an AlertDialog
				this.progressDialog.dismiss();

				app.setLoggedOut();

				AlertDialog.Builder failure = new AlertDialog.Builder(
						MyHomeAudioActivity.this);
				failure.setTitle(getText(R.string.login_failed_title));
				failure.setMessage(getText(R.string.login_failed_detail));
				failure.setNeutralButton("Ok",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								failureDialog.dismiss();
							}
						});

				this.failureDialog = failure.create();
				this.failureDialog.show();
			}
		}
	}
}
