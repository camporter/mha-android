package com.teamacra.myhomeaudio.ui;

import java.util.ArrayList;

import org.w3c.dom.NodeList;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
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
import com.teamacra.myhomeaudio.locations.NodeConfiguration;
import com.teamacra.myhomeaudio.locations.NodeSignalRange;
import com.teamacra.myhomeaudio.manager.NodeManager;
import com.teamacra.myhomeaudio.node.Node;

public class InitialConfigActivity extends SherlockFragmentActivity implements OnClickListener {

	private boolean welcomeComplete = false;
	private int nextNodeIndex = 0;

	private ArrayList<Node> mNodeList;
	private ArrayAdapter<Node> mNodeAdapter;

	AsyncTask<Integer, ArrayList<NodeSignalRange>, Void> nodeConfig;
	AsyncTask<String, Void, ArrayList<Node>> updateNodes;

	private Button mNextButton;
	private Button mCancelButton;
	private Button mRefreshButton;
	private Button mStartButton;
	private Button mStopButton;

	private TextView mTitleText;
	private TextView mDescriptionText;

	private final String TAG = "InitialConfigActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "InitialConfigActivity Started");
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.initialconfig);

		// Build the node list objects
		mNodeList = new ArrayList<Node>();
		mNodeAdapter = new ArrayAdapter<Node>(this, android.R.layout.simple_list_item_1, mNodeList);
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

		mStopButton = (Button) findViewById(R.id.initialconfig_stopButton);
		mStopButton.setOnClickListener(this);
		mStopButton.setVisibility(View.GONE);

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
			MHAApplication app = (MHAApplication) getApplication();
			if (!app.isConfigured()) {
				// User is logged in but canceling config, so log them back out
				app.setLoggedOut();
			}
			finish();
			
		} else if (v == this.mStartButton) {
			// Start the node configuration scan
			nodeConfig = new NodeConfig();
			nodeConfig.execute();
			
		} else if (v == this.mStopButton) {
			// Stop the node configuration scan
			nodeConfig.cancel(true);
			if (nodeConfig.getStatus() == AsyncTask.Status.FINISHED) {
				Log.d(TAG, "Stop button clicked");
			}
			
		} else if (v == this.mRefreshButton) {
			// Refresh the list of nodes found
			Log.d(TAG, "Refresh Clicked");
			if (updateNodes == null) {
				updateNodes = new UpdateNodes();
				updateNodes.execute();
			} else {
				updateNodes.cancel(true);
				updateNodes = new UpdateNodes();
				updateNodes.execute();
			}
		}
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
			new UpdateNodes().execute();

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
		}
	}

	private class UpdateNodes extends AsyncTask<String, Void, ArrayList<Node>> {

		MHAApplication app = (MHAApplication) InitialConfigActivity.this.getApplication();
		private final ProgressDialog progressDialog = new ProgressDialog(InitialConfigActivity.this);

		protected void onPreExecute() {
			progressDialog.setMessage("Finding nodes...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		protected ArrayList<Node> doInBackground(String... notUsed) {
			NodeManager nm = NodeManager.getInstance(app);
			nm.updateNodes();
			return nm.getActiveNodeList();
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
					alertBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
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

	protected class SendConfig extends AsyncTask<String, Void, Void> {

		MHAApplication app = (MHAApplication) InitialConfigActivity.this.getApplication();
		private final ProgressDialog progressDialog = new ProgressDialog(InitialConfigActivity.this);

		protected void onPreExecute() {
			progressDialog.setMessage("Sending your configuration...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		protected Void doInBackground(String... notUsed) {
			NodeManager nm = NodeManager.getInstance(app);
			return null;
		}

		protected void onPostExecute() {
			progressDialog.dismiss();
		}

	}

	protected class NodeConfig extends AsyncTask<Integer, ArrayList<NodeSignalRange>, Void> {

		final MHAApplication app = (MHAApplication) InitialConfigActivity.this.getApplication();
		private final ProgressDialog progressDialog = new ProgressDialog(
						InitialConfigActivity.this);
		
			
		protected void onPreExecute() {
			Log.d(TAG, "NodeConfig Started");
			progressDialog.setTitle("Node " + (nextNodeIndex + 1) + " of "
					+ mNodeList.size() + " "+ mNodeList.get(nextNodeIndex).name());
			progressDialog.setMessage("Press Back Button to End Scan");
			progressDialog.setCancelable(false);
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Stop", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.d(TAG, "Progress Dialog onClick Stop");
					nodeConfig.cancel(true);
					progressDialog.dismiss();
				}
			});
			progressDialog.show();
		}

		protected Void doInBackground(Integer... params) {
			Log.d(TAG, "Starting to Generate List");
			ArrayList<NodeSignalRange> foundNodes = new ArrayList<NodeSignalRange>();
			NodeConfiguration config;
		//	config = new NodeConfiguration(app, mNodeList.get(nextNodeIndex));
			while (!isCancelled()) {
				int i = 0;
				while (i < 100000) {
					i++;
				}
				
			//	config.updateNodeList();
			//	foundNodes = config.getFoundNodes();
				if(foundNodes.isEmpty()){
					Log.d(TAG, "No nodes found");
				}else{
					Log.d(TAG, "Generated Found Node List, " + foundNodes.get(0)
							+ " " + isCancelled());
				}
				onPublishedProgress(foundNodes);
			}
			Log.d(TAG, "Back pressed Ending");
			return null;
		}

		protected void onPostExecute() {
			Log.d(TAG, "NodeConfig Ended");
		}

		protected void onCancelled() {
			Log.d(TAG, mNodeList.get(nextNodeIndex) + " configuration generated");
			Toast.makeText(InitialConfigActivity.this,
					mNodeList.get(nextNodeIndex) + " configuration generated", Toast.LENGTH_LONG)
					.show();
			mStopButton.setVisibility(View.INVISIBLE);
			mNextButton.setVisibility(View.VISIBLE);
			nextNodeIndex++;
			// mStartButton.setVisibility(View.VISIBLE);
		}

		protected void onPublishedProgress(ArrayList<NodeSignalRange> foundNodes) {

			Log.d(TAG, "Publishing " + mNodeList.get(nextNodeIndex).name() + " data");
		}

	}
}
