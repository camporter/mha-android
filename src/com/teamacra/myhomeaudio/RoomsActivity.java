package com.teamacra.myhomeaudio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RoomsActivity extends Activity {

	private Button finder;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Adds a button to check for nodes
		// TODO: add node-checking code
		this.setContentView(R.layout.rooms);
		this.finder = (Button) this.findViewById(R.id.finder);
		this.finder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//finish();
			}
		});
	}
}
