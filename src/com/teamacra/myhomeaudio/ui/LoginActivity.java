package com.teamacra.myhomeaudio.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.discovery.DiscoverySearch;
import com.teamacra.myhomeaudio.http.HttpClient;
import com.teamacra.myhomeaudio.ui.actionbar.ActionBarActivity;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

	private Button loginButton;
	private Button newUserButton;
	private EditText serverAddressEditText;

	/**
	 * Creates the LoginActivity UI, sets up the click listener.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// Register the click listeners for the buttons
		this.loginButton = (Button) this.findViewById(R.id.loginButton);
		this.loginButton.setOnClickListener(this);
		this.newUserButton = (Button) this.findViewById(R.id.newUserButton);
		this.newUserButton.setOnClickListener(this);
	}

	public void onStart() {
		super.onStart();
		this.getApplication();

		checkBluetooth();

		new RunDiscovery().execute();
	}

	/**
	 * Runs when the LoginActivity resumes. Checks to see if a user is logged
	 * in. If they are, forward the user on to the MyHomeAudioActivity instead.
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

			String username = ((EditText) this
					.findViewById(R.id.usernameEditText)).getText().toString()
					.trim();
			String password = ((EditText) this
					.findViewById(R.id.passwordEditText)).getText().toString()
					.trim();

			if (username.length() > 0 && password.length() > 0) {
				new LogInUser().execute(username, password);
			} else {
				Toast.makeText(
						this,
						getText(R.string.complete_username_password),
						Toast.LENGTH_SHORT).show();
			}
		} else if (view == this.newUserButton) {
			// Send the user to the RegisterActivity
			Intent registerIntent = new Intent(this, RegisterActivity.class);
			this.startActivity(registerIntent);
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_refresh:
                Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT).show();
                getActionBarHelper().setRefreshActionItemState(true);
                getWindow().getDecorView().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                getActionBarHelper().setRefreshActionItemState(false);
                            }
                        }, 1000);
                break;

            case R.id.menu_search:
                Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_share:
                Toast.makeText(this, "Tapped share", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
	
	/**
	 * Checks if Bluetooth is on. Prompts user to turn it on when off.
	 * 
	 * @return Whether bluetooth is on or not.
	 */
	private boolean checkBluetooth() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBTIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBTIntent, 3);
			return false;
		}
		return true;
	}

	private class RunDiscovery extends AsyncTask<String, Void, String> {

		private ProgressDialog progressDialog;
		private AlertDialog failureDialog;

		protected void onPreExecute() {
			progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.setMessage(getText(R.string.server_find));
			progressDialog.show();
		}

		protected String doInBackground(String... args) {

			WifiManager wifi = (WifiManager) LoginActivity.this
					.getSystemService(android.content.Context.WIFI_SERVICE);

			DhcpInfo dhcp = wifi.getDhcpInfo();
			DiscoverySearch discovery = new DiscoverySearch("myhomeaudio", dhcp);

			String result = discovery.run();
			discovery.end();
			return result;

		}

		protected void onPostExecute(final String serverAddress) {
			MHAApplication app = (MHAApplication) LoginActivity.this
					.getApplication();
			if (serverAddress == null) {
				// Didn't find the server, don't let the user continue
				// Disable login button
				loginButton.setEnabled(false);

				// Show error dialog
				AlertDialog.Builder failure = new AlertDialog.Builder(
						LoginActivity.this);
				failure.setTitle(getText(R.string.server_find_failed_title));
				failure.setMessage(getText(R.string.server_find_failed_detail));
				failure.setNegativeButton(getText(R.string.quit),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// Close the activity
								failureDialog.dismiss();
								LoginActivity.this.finish();
							}
						});
				/*
				 * failure.setNeutralButton("Ok", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * public void onClick(DialogInterface dialog, int which) {
				 * failureDialog.dismiss(); } });
				 */
				failure.setPositiveButton(getText(R.string.retry),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// Run the discovery process again
								failureDialog.dismiss();
								new RunDiscovery().execute();
							}
						});

				this.failureDialog = failure.create();
				this.failureDialog.show();
			} else {
				// Found the server, save the address and let the user continue
				app.setServerAddress(serverAddress);

				// Update the address text view for debug purposes
				TextView addrTextView = (TextView) LoginActivity.this
						.findViewById(R.id.serverAddressTextView);
				addrTextView.setText(getText(R.string.login_server_address) + " " + serverAddress);
				// Enable the login button
				LoginActivity.this.loginButton.setEnabled(true);
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

		private final ProgressDialog progressDialog = new ProgressDialog(
				LoginActivity.this);
		private AlertDialog failureDialog;
		MHAApplication app = (MHAApplication) LoginActivity.this
				.getApplication();

		protected void onPreExecute() {
			this.progressDialog.setMessage(getText(R.string.logging_in_wait));
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
						.findViewById(R.id.usernameEditText)).getText()
						.toString();
				String password = ((EditText) LoginActivity.this
						.findViewById(R.id.passwordEditText)).getText()
						.toString();

				app.setLoggedIn(username, password, sessionId);
				this.progressDialog.dismiss();

				Intent mhaIntent = new Intent(LoginActivity.this,
						MyHomeAudioActivity.class);
				LoginActivity.this.startActivity(mhaIntent);
			} else {
				// Login failed, let the user know with an AlertDialog
				this.progressDialog.dismiss();

				app.setLoggedOut();

				AlertDialog.Builder failure = new AlertDialog.Builder(
						LoginActivity.this);
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
