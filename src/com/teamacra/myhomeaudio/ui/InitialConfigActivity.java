package com.teamacra.myhomeaudio.ui;

import java.util.ArrayList;

import org.w3c.dom.NodeList;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class InitialConfigActivity extends SherlockFragmentActivity {

	private boolean welcomeComplete = false;
	private int nextNodeIndex = 0;

	private ArrayList<Node> mNodeList;
	private ArrayAdapter<Node> mNodeAdapter;
	private ArrayAdapter<NodeSignalRange> mNodeSignalAdapter;
	
	AsyncTask<Integer, ArrayList<NodeSignalRange>, Void>  nodeConfig;
	AsyncTask<String, Void, ArrayList<Node>> updateNodes;
	
	private Button mNextButton;
	private Button mCancelButton;
	private Button mRefreshButton;
	private Button mStartButton;
	private Button mStopButton;

	private TextView mTitleText;
	private TextView mDescriptionText;

	private final String TAG = "MyHomeAudio";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		final MHAApplication app = (MHAApplication) getApplication();

		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.initialconfig);

		mNodeList = new ArrayList<Node>();
		mNodeAdapter = new ArrayAdapter<Node>(this, android.R.layout.simple_list_item_1, mNodeList);
		ListView nodeListView = (ListView) findViewById(R.id.initialconfig_nodeList);
		nodeListView.setAdapter(mNodeAdapter);

		mNextButton = (Button) findViewById(R.id.initialconfig_nextButton);
		mNextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeActivityState();
			}
		});

		mRefreshButton = (Button) findViewById(R.id.initialconfig_refreshButton);
		mRefreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Refresh Clicked");
				if(updateNodes == null){
					updateNodes = new UpdateNodes();
					updateNodes.execute();
				}else{
					updateNodes.cancel(true);
					updateNodes = new UpdateNodes();
					updateNodes.execute();
				}
				
			}
		});
		mRefreshButton.setVisibility(View.GONE);

		mCancelButton = (Button) findViewById(R.id.initialconfig_cancelButton);
		mCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!app.isConfigured()) {
					app.setLoggedOut();
				}
				finish();
			}
		});

		mStartButton = (Button) findViewById(R.id.initialconfig_startButton);
		mStartButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nodeConfig = new NodeConfig();
				nodeConfig.execute();
			}
		});
		mStartButton.setVisibility(View.GONE);

		mStopButton = (Button) findViewById(R.id.initialconfig_stopButton);
		mStopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nodeConfig.cancel(true);
				if(nodeConfig.getStatus() == AsyncTask.Status.FINISHED){
					Log.d(TAG, "Stop button clicked");
				}
			}
		});
		mStopButton.setVisibility(View.GONE);

		mTitleText = (TextView) findViewById(R.id.initialconfig_header);
		mDescriptionText = (TextView) findViewById(R.id.initialconfig_description);

		if (savedInstanceState == null) {
			
		} else {
			welcomeComplete = savedInstanceState.containsKey("welcomeComplete") ? savedInstanceState
					.getBoolean("welcomeComplete") : false;
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

	void updateNodeList(){
		final MHAApplication app = (MHAApplication) getApplication();
		NodeManager nm = NodeManager.getInstance(app);
		nm.updateNodes();
		mNodeList.clear();
		mNodeList.addAll(nm.getActiveNodeList());
		mNodeAdapter.notifyDataSetChanged();
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
			mStartButton.setVisibility(View.VISIBLE);
			mNextButton.setVisibility(View.INVISIBLE);
			mRefreshButton.setVisibility(View.GONE);
		
			mNodeSignalAdapter = new ArrayAdapter<NodeSignalRange>(this, android.R.layout.simple_list_item_1, 
					new ArrayList<NodeSignalRange>());
			ListView nodeListView = (ListView) findViewById(R.id.initialconfig_nodeList);
			nodeListView.setAdapter(mNodeSignalAdapter);
			
			// Do individual Node scans
			mTitleText
					.setText("Node #" + nextNodeIndex + " " + mNodeList.get(nextNodeIndex).name());
			mDescriptionText
					.setText("For initializing the node, press start and begin walking the far reaches.");

			nextNodeIndex++;
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
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(InitialConfigActivity.this);
					alertBuilder.setTitle("No nodes found!");
					alertBuilder.setMessage("Please make sure your hardware is turned on, then try again.");
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
		MHAApplication app = (MHAApplication) InitialConfigActivity.this.getApplication();

		protected void onPreExecute() {
			mStartButton.setVisibility(View.INVISIBLE);
			mStopButton.setVisibility(View.VISIBLE);
			Log.d(TAG,"NodeConfig Started");
			mTitleText.setText("Node " + nextNodeIndex + " of " + mNodeList.size() +
					" " + mNodeList.get(nextNodeIndex).name());
			mDescriptionText.setText("For initializing the node, press start and begin walking the far reaches.");
		}

		protected Void doInBackground(Integer... params) {
			Log.d(TAG, "Starting to Generate List");
			ArrayList<NodeSignalRange> foundNodes = new ArrayList<NodeSignalRange>();
			while(!isCancelled()){
				foundNodes = NodeConfiguration.generateNodeList(mNodeList.get(nextNodeIndex));
				Log.d(TAG, "Generated Found Node List");
				onPublishedProgress(foundNodes);
			}
			return null;
		}

		protected void onPostExecute() {
			Log.d(TAG, "NodeConfig Ended");
		}
		
		protected void onCancelled(){
			Log.d(TAG, mNodeList.get(nextNodeIndex) + " configuration generated");
			Toast.makeText(InitialConfigActivity.this, mNodeList.get(nextNodeIndex) + " configuration generated",
					Toast.LENGTH_LONG).show();
			mStopButton.setVisibility(View.INVISIBLE);
			mNextButton.setVisibility(View.VISIBLE);
			mStartButton.setVisibility(View.VISIBLE);
		}
		
		protected void onPublishedProgress(ArrayList<NodeSignalRange> foundNodes){
			Log.d(TAG, "Publishing " + mNodeList.get(nextNodeIndex).name() + " data");
		}

	}
}
