package com.teamacra.myhomeaudio.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.bluetooth.BluetoothService;
import com.teamacra.myhomeaudio.manager.ConfigurationManager;
import com.teamacra.myhomeaudio.manager.NodeManager;
import com.teamacra.myhomeaudio.node.Node;

public class InitialConfigActivity extends SherlockFragmentActivity implements
		OnClickListener {

	private boolean welcomeComplete = false;
	private int nextNodeIndex = 0;

	private ArrayList<Node> mNodeList;
	private ArrayAdapter<Node> mNodeAdapter;

	AsyncTask<Integer, Void, Void> mNodeConfigTask;
	AsyncTask<String, Void, ArrayList<Node>> mUpdateNodesTask;

	private Button mNextButton;
	private Button mCancelButton;
	private Button mRefreshButton;
	private Button mStartButton;

	private TextView mTitleText;
	private TextView mDescriptionText;

	private final String TAG = "InitialConfigActivity";

	private MHAApplication app;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "InitialConfigActivity Started");

		app = (MHAApplication) getApplication();

		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.initialconfig);
		
		// Want to listen to any device updates the BluetoothService broadcasts
		registerReceiver(mReceiver, new IntentFilter(BluetoothService.DEVICE_UPDATE));

		// Build the node list objects
		mNodeList = new ArrayList<Node>();
		mNodeAdapter = new ArrayAdapter<Node>(this,
				android.R.layout.simple_list_item_1, mNodeList);
		ListView nodeListView = (ListView) findViewById(R.id.initialconfig_nodeList);
		nodeListView.setAdapter(mNodeAdapter);

		// Set up buttons
		mNextButton = (Button) findViewById(R.id.initialconfig_nextButton);
		mNextButton.setOnClickListener(this);

		mRefreshButton = (Button) findViewById(R.id.initialconfig_refreshButton);
		mRefreshButton.setOnClickListener(this);
		mRefreshButton.setVisibility(View.GONE);

		mCancelButton = (Button) findViewById(R.id.initialconfig_cancelButton);
		mCancelButton.setOnClickListener(this);

		mStartButton = (Button) findViewById(R.id.initialconfig_startButton);
		mStartButton.setOnClickListener(this);
		mStartButton.setVisibility(View.GONE);

		mTitleText = (TextView) findViewById(R.id.initialconfig_header);
		mDescriptionText = (TextView) findViewById(R.id.initialconfig_description);

		if (savedInstanceState == null) {

		} else {
			welcomeComplete = savedInstanceState.containsKey("welcomeComplete") ? savedInstanceState
					.getBoolean("welcomeComplete") : false;
		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.mNextButton) {
			// Go to the next configuration step
			changeActivityState();

		} else if (v == this.mCancelButton) {
			// Cancel the configuration process
			if (!app.isConfigured()) {
				// User is logged in but canceling config, so log them back out
				app.setLoggedOut();
			}
			finish();

		} else if (v == this.mStartButton) {
			// Start the node configuration scan
			mNodeConfigTask = new NodeConfigTask();
			mNodeConfigTask.execute();

		} else if (v == this.mRefreshButton) {
			// Refresh the list of nodes found
			Log.d(TAG, "Refresh Clicked");
			if (mUpdateNodesTask == null) {
				mUpdateNodesTask = new UpdateNodesTask();
				mUpdateNodesTask.execute();
			} else {
				mUpdateNodesTask.cancel(true);
				mUpdateNodesTask = new UpdateNodesTask();
				mUpdateNodesTask.execute();
			}
		}
	}

	public void onResume() {
		super.onResume();

		// Check to make sure the user is not already configured
		if (app.isConfigured()) {
			// User already configured, move past configuration
			Intent intent = new Intent(this, MyHomeAudioActivity.class);
			this.startActivity(intent);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean("welcomeComplete", this.welcomeComplete);
		// savedInstanceState.putBoolean("confirmNodesComplete",
		// this.confirmNodesComplete);
	}

	@Override
	public void onBackPressed() {

	}

	/**
	 * Handles switching the state of the activity given the current state.
	 * Usually after the next button is pressed.
	 */
	void changeActivityState() {

		Log.d("MyHomeAudio", "ListSize:" + mNodeList.size());

		if (!welcomeComplete) {
			// Just pressed next on the welcome screen
			welcomeComplete = true;

			// Allow the refresh button to be visible
			mRefreshButton.setVisibility(View.VISIBLE);

			// We need to get from the server the nodes that are available
			Log.d(TAG, "Starting Initial Update NodeList");
			new UpdateNodesTask().execute();

		} else if (nextNodeIndex < mNodeList.size()) {
			// Change button visibility
			Log.d(TAG, "Changing Visibility of Buttons");
			mStartButton.setVisibility(View.VISIBLE);
			mNextButton.setVisibility(View.INVISIBLE);
			mRefreshButton.setVisibility(View.GONE);

			// Do individual Node scans
			mTitleText.setText("Node #" + (nextNodeIndex + 1) + " "
					+ mNodeList.get(nextNodeIndex).name());
			mDescriptionText
					.setText("For initializing the node, press start and begin walking the far reaches.");
		} else {
			// Configuration done, send config information to server
			// Exit configuration
		}
	}

	private class UpdateNodesTask extends
			AsyncTask<String, Void, ArrayList<Node>> {

		private final ProgressDialog progressDialog = new ProgressDialog(
				InitialConfigActivity.this);

		protected void onPreExecute() {
			progressDialog.setMessage("Finding nodes...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		protected ArrayList<Node> doInBackground(String... notUsed) {
			NodeManager nm = NodeManager.getInstance(app);
			nm.updateNodes();
			return nm.getNodeList(true);
		}

		protected void onPostExecute(ArrayList<Node> result) {
			progressDialog.dismiss();
			if (result != null) {
				if (result.size() == 0) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
							InitialConfigActivity.this);
					alertBuilder.setTitle("No nodes found!");
					alertBuilder
							.setMessage("Please make sure your hardware is turned on, then try again.");
					alertBuilder.setNegativeButton("Ok",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									InitialConfigActivity.this.finish();
								}
							});
					AlertDialog alertDialog = alertBuilder.create();
					alertDialog.show();
				} else {
					mTitleText.setText("We found these nodes");
					mDescriptionText
							.setText("If there are any missing, make sure they are turned on and connected to the network, then try again.");
					mNodeList.clear();
					mNodeList.addAll(result);
					mNodeAdapter.notifyDataSetChanged();

				}
			}
		}
	}

	/**
	 * Task to send the configuration off to the server.
	 * 
	 * 
	 */
	protected class SendConfigTask extends
			AsyncTask<String, Void, ArrayList<Node>> {

		private final ProgressDialog progressDialog = new ProgressDialog(
				InitialConfigActivity.this);

		protected void onPreExecute() {
			progressDialog.setMessage("Sending your configuration...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		protected ArrayList<Node> doInBackground(String... notUsed) {
			NodeManager nm = NodeManager.getInstance(app);
			nm.updateNodes();
			return nm.getNodeList(true);
		}

		protected void onPostExecute() {
			progressDialog.dismiss();
		}

	}

	/**
	 * Task that runs the scan for signals near a specific node.
	 * 
	 * @author Cameron
	 * 
	 */
	protected class NodeConfigTask extends AsyncTask<Integer, Void, Void> {

		private final ProgressDialog progressDialog = new ProgressDialog(
				InitialConfigActivity.this);
		
		private ConfigurationManager configManager = ConfigurationManager
				.getInstance(app);

		protected void onPreExecute() {
			Log.d(TAG, "NodeConfigTask Setup Started");
			progressDialog.setTitle("Node " + (nextNodeIndex + 1) + " of "
					+ mNodeList.size() + " "
					+ mNodeList.get(nextNodeIndex).name());
			progressDialog.setMessage("Press Back Button to End Scan");
			progressDialog.setCancelable(false);
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Stop",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.d(TAG, "Progress Dialog Stop Pressed ");
							mNodeConfigTask.cancel(true);
							progressDialog.dismiss();
						}
					});
			progressDialog.show();
		}

		protected Void doInBackground(Integer... params) {
			Log.d(TAG, "Starting to Generate List");
			
			// Start the bluetooth service to go find devices
			app.startBluetoothService(app, true);
			
			while (!isCancelled()) {
				// Wait until the task is cancelled
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		protected void onCancelled() {
			// Stop the bluetooth discovery
			app.stopBluetoothService();
			Log.d(TAG, mNodeList.get(nextNodeIndex)
					+ " configuration generated");
			Toast.makeText(InitialConfigActivity.this,
					mNodeList.get(nextNodeIndex) + " configuration generated",
					Toast.LENGTH_SHORT).show();
			Log.i(TAG, configManager.getJSON(mNodeList.get(nextNodeIndex)));
			mNextButton.setVisibility(View.VISIBLE);
			mStartButton.setVisibility(View.INVISIBLE);
			nextNodeIndex++;
			Log.d(TAG, "NodeConfigTask Setup Ending");
		}

		public void receiveDevice(String name, String bluetoothAddress, int rssi) {
			configManager.storeDeviceSignal(mNodeList.get(nextNodeIndex), name,
					bluetoothAddress, rssi);
		}
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothService.DEVICE_UPDATE.equals(action)) {
				// If the nodeConfigTask is executing, then pass the device
				// information off to it
				if (mNodeConfigTask != null) {
					((NodeConfigTask) mNodeConfigTask).receiveDevice(
							intent.getStringExtra("deviceName"),
							intent.getStringExtra("deviceAddress"),
							intent.getIntExtra("rssi", Integer.MIN_VALUE));
				}
				Log.i(TAG, "Name: " + intent.getStringExtra("deviceName"));
			}
		}
	};
}
