package com.teamacra.myhomeaudio;

import com.teamacra.myhomeaudio.http.StatusCode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

public class RegisterActivity extends Activity implements View.OnClickListener {

	private Button registerButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		this.registerButton = (Button) this.findViewById(R.id.registerButton);
		this.registerButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (view == this.registerButton) {
			String username = ((EditText) this.findViewById(R.id.registerUsernameEditText))
					.getText().toString();
			String password = ((EditText) this.findViewById(R.id.registerPasswordEditText))
					.getText().toString();
			
			if (username.length() > 0 && password.length() > 0) {
				new RegisterUser().execute(username, password);
			} else {
				Toast.makeText(this, "Please fill in your username and password completely!", Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	
	private class RegisterUser extends AsyncTask<String, Void, Integer> {
		private final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
		private AlertDialog completionDialog;
		
		protected void onPreExecute() {
			this.progressDialog.setMessage("Registering a new account for you...");
			this.progressDialog.show();
		}
		
		protected Integer doInBackground(String... args) {
			String username = args[0];
			String password = args[1];
			
			return StatusCode.STATUS_FAILED;
		}
		
		protected void onPostExecute(final Integer statusCode) {
			AlertDialog.Builder completion = new AlertDialog.Builder(RegisterActivity.this);
			
			switch(statusCode) {
			case StatusCode.STATUS_OK:
				completion.setTitle("Now Registered");
				completion.setMessage("You can now log in with your username and password.");
				completion.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						completionDialog.dismiss();	
						// TODO: Go back to the login activity!
					}
				});
				break;
			case StatusCode.STATUS_REG_DUPLICATE:
				completion.setTitle("Duplicate Username");
				completion.setMessage("Your username has already been registered, please try another one.");
				completion.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						completionDialog.dismiss();	
					}
				});
				break;
			default:
				completion.setTitle("Registration Failed");
				completion.setMessage("An error occured while trying to register.");
				completion.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						completionDialog.dismiss();	
					}
				});
			}
			this.progressDialog.dismiss();
			this.completionDialog = completion.create();
			this.completionDialog.show();
			
		}
	}
}
