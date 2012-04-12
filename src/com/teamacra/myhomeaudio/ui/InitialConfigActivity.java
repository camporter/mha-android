package com.teamacra.myhomeaudio.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.R;


public class InitialConfigActivity extends SherlockFragmentActivity {
	
	private Button nextButton;
	private Button cancelButton;
	private boolean welcomeComplete = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final MHAApplication app = (MHAApplication) getApplication();
		
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.initialconfig);
		
		nextButton = (Button) findViewById(R.id.initialconfig_nextButton);
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeFragment();
			}
		});
		
		cancelButton = (Button) findViewById(R.id.initialconfig_cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Fragment welcomeFragment = WelcomeFragment.newInstance();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.initialconfig_fragment, welcomeFragment).commit();
		
	}
	
	public void onResume() {
		super.onResume();

		MHAApplication app = (MHAApplication) this.getApplication();

		// Check to make sure the user is not already configured
		if (app.isConfigured()) {
			// User already configured, move past configuration
			Intent intent = new Intent(this, MyHomeAudioActivity.class);
			this.startActivity(intent);
		}
	}
	
	public void onClick(View view){
		Log.d("MyHomeAudio", "Button Clicked");
		if (view == this.nextButton) {
			//User pressed next button
			Log.d("MyHomeAudio", "Next Button Clicked");
			
		} else if (view == this.cancelButton) {
			//User pressed cancel, exit to login?
			Intent intent = new Intent(this, LoginActivity.class);
			this.startActivity(intent);
		}
		
	}
	
	@Override
	public void onBackPressed() {
		
	}
	
	void changeFragment() {
		if (!welcomeComplete) {
			// Just finished the welcome fragment, now on to all the 
			welcomeComplete = true;
			Fragment nodeFragment = NodeAreaFragment.newInstance();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.initialconfig_fragment, nodeFragment);
			ft.addToBackStack(null);
			ft.commit();
			Log.d("MyHomeAudio", "Welcome Frag Complete");
		}
	}
	
	public static class WelcomeFragment extends SherlockFragment {
		static WelcomeFragment newInstance() {
			WelcomeFragment f = new WelcomeFragment();
			
			Bundle args = new Bundle();
			f.setArguments(args);
			return f;
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.initialconfig_welcome, container, false);
			return v;
		}
	}
	
	public static class ConfirmNodesFragment extends SherlockFragment {
		static ConfirmNodesFragment newInstance() {
			ConfirmNodesFragment f = new ConfirmNodesFragment();
			Bundle args = new Bundle();
			f.setArguments(args);
			return f;
		}
	}
	
	public static class NodeAreaFragment extends SherlockFragment {
		static NodeAreaFragment newInstance() {
			NodeAreaFragment f = new NodeAreaFragment();
			
			Bundle args = new Bundle();
			f.setArguments(args);
			return f;
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.initialconfig_welcome, container, false);
			return v;
		}
	}
}


