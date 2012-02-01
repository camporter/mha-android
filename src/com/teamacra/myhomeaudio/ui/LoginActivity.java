package com.teamacra.myhomeaudio.ui;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.R.id;
import com.teamacra.myhomeaudio.R.layout;
import com.teamacra.myhomeaudio.discovery.Discovery;
import com.teamacra.myhomeaudio.http.HttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener {

	private Button loginButton;
	private Button newUserButton;
	
	// Intent request constants
	private static final int REQUEST_ENABLE_BT = 3;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		this.loginButton = (Button) this.findViewById(R.id.loginButton);
		this.loginButton.setOnClickListener(this);
		this.newUserButton = (Button) this.findViewById(R.id.newUserButton);
		this.newUserButton.setOnClickListener(this);

	}
	
	public void onStart() {
		super.onStart();
		MHAApplication app = (MHAApplication) this.getApplication();
		
		checkBluetooth();
		
		//new RunDiscovery().execute();
	}
	
	/**
	 * Checks to see if a user is logged in. If they are, forward the user on to
	 * the MyHomeAudioActivity instead.
	 * 
	 * @param savedInstanceState
	 */
	public void onResume() {
		super.onResume();
		
		MHAApplication app = (MHAApplication) this.getApplication();
		
		// Check to make sure the user is not already logged in
		if (app.isLoggedIn()) {
			// User logged in, so forward them on past the login
			Intent intent = new Intent(this, MyHomeAudioActivity.class);
			this.startActivity(intent);
		}
		
		// Check wifi, update the server connection status
		
		
	}

	@Override
	public void onClick(View view) {
		if (view == this.loginButton) {
			// Begin to log the user in if they press the button

			String username = ((EditText) this.findViewById(R.id.usernameEditText)).getText()
					.toString().trim();
			String password = ((EditText) this.findViewById(R.id.passwordEditText)).getText()
					.toString().trim();
			String server = ((EditText) this.findViewById(R.id.serverAddressEditText)).getText().toString().trim();
			
			// Set the server to use
			MHAApplication app = (MHAApplication) LoginActivity.this.getApplication();
			app.setServerAddress(server);
			
			if (username.length() > 0 && password.length() > 0)
			{
				new LogInUser().execute(username, password);
			} else {
				Toast.makeText(this, "Please fill in your username and password completely!", Toast.LENGTH_SHORT).show();
			}
		} else if (view == this.newUserButton) {
			// Send the user to the RegisterActivity
			Intent registerIntent = new Intent(this, RegisterActivity.class);
			this.startActivity(registerIntent);
		} else { }

	}

	
	/**
	 * Checks if Bluetooth is on. Prompts user to turn it on if it is off.
	 * 
	 * @return Whether bluetooth is on or not.
	 */
	private boolean checkBluetooth() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
			return false;
		}
		return true;
	}
	
	private class RunDiscovery extends AsyncTask<String, Void, String> {
		private final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
		private AlertDialog failureDialog;
		
		protected void onPreExecute() {
			this.progressDialog.setMessage("Finding the My Home Audio server...");
			this.progressDialog.show();
		}
		
		protected String doInBackground(String... args) {
			// Lock multicast access over Wifi
			WifiManager wifi = (WifiManager) LoginActivity.this.getSystemService(android.content.Context.WIFI_SERVICE);
			MulticastLock lock = wifi.createMulticastLock("MHADiscoveryLock");
			lock.setReferenceCounted(true);
			
			Discovery discovery = new Discovery();
			String serverAddress = discovery.run(lock);
			return serverAddress;
		}
		
		protected void onPostExecute(final String serverAddress) {
			MHAApplication app = (MHAApplication) LoginActivity.this.getApplication();
			if (serverAddress == null) {
				// Didn't find the server, don't let the user continue
				// Disable login button
				loginButton.setEnabled(false);
				
				// Show error dialog
				AlertDialog.Builder failure = new AlertDialog.Builder(LoginActivity.this);
				failure.setTitle("Server Not Found");
				failure.setMessage("Unable to find the server.");
				failure.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Close the activity
						failureDialog.dismiss();
						LoginActivity.this.finish();
					}
				});
				failure.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						failureDialog.dismiss();
					}
				});
				failure.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						failureDialog.dismiss();
						new RunDiscovery().execute();
					}
				});
				
				this.failureDialog = failure.create();
				this.failureDialog.show();
			}
			else {
				
			}
			this.progressDialog.dismiss();
		}
	}
	
	/**
	 * Does the actual login, and shows a dialog while the user waits, and if
	 * the login fails.
	 * 
	 * @author Cameron
	 * 
	 */
	private class LogInUser extends AsyncTask<String, Void, String> {

		private final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
		private AlertDialog failureDialog;
		MHAApplication app = (MHAApplication) LoginActivity.this.getApplication();

		protected void onPreExecute() {
			this.progressDialog.setMessage("Logging you in...");
			this.progressDialog.show();
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
				String username = ((EditText) LoginActivity.this
						.findViewById(R.id.usernameEditText)).getText().toString();
				String password = ((EditText) LoginActivity.this
						.findViewById(R.id.passwordEditText)).getText().toString();
				
				app.setLoggedIn(username, password, sessionId);
				this.progressDialog.dismiss();
			} else {
				// Login failed, let the user know with an AlertDialog
				this.progressDialog.dismiss();
				
				app.setLoggedOut();
				
				AlertDialog.Builder failure = new AlertDialog.Builder(LoginActivity.this);
				failure.setTitle("Login Failed");
				failure.setMessage("Check your username and password for errors.");
				failure.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						failureDialog.dismiss();
					}
				});
				
				this.failureDialog = failure.create();
				this.failureDialog.show();
			}
		}

	}
}
