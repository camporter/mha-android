package com.teamacra.myhomeaudio;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener {

	private Button loginButton;
	private Button newUserButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		this.loginButton = (Button) this.findViewById(R.id.loginButton);
		this.loginButton.setOnClickListener(this);
		this.newUserButton = (Button) this.findViewById(R.id.newUserButton);
		this.newUserButton.setOnClickListener(this);

	}

	/**
	 * Checks to see if a user is logged in. If they are, forward the user on to
	 * the MyHomeAudioActivity instead.
	 * 
	 * @param savedInstanceState
	 */
	public void onResume(Bundle savedInstanceState) {
		MHAApplication app = (MHAApplication) this.getApplication();
		if (app.isLoggedIn()) {
			// User logged in, so forward them on past the login
			Intent intent = new Intent(this, MyHomeAudioActivity.class);
			this.startActivity(intent);
		}
	}

	@Override
	public void onClick(View view) {
		if (view == this.loginButton) {
			// Begin to log the user in if they press the button

			String username = ((EditText) this.findViewById(R.id.usernameEditText)).getText()
					.toString().trim();
			String password = ((EditText) this.findViewById(R.id.passwordEditText)).getText()
					.toString().trim();
			
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
	 * Does the actual login, and shows a dialog while the user waits, and if
	 * the login fails.
	 * 
	 * @author Cameron
	 * 
	 */
	private class LogInUser extends AsyncTask<String, Void, String> {

		private final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
		private AlertDialog failureDialog;

		protected void onPreExecute() {
			this.progressDialog.setMessage("Logging you in...");
			this.progressDialog.show();
		}

		protected String doInBackground(String... args) {
			String username = args[0];
			String password = args[1];

			// Do actual login here...
			return "FAKESESSIONID";
		}

		protected void onPostExecute(final String sessionId) {
			MHAApplication app = (MHAApplication) LoginActivity.this.getApplication();
			
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
