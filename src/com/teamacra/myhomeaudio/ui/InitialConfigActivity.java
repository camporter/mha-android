package com.teamacra.myhomeaudio.ui;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.manager.NodeManager;
import com.teamacra.myhomeaudio.node.Node;

public class InitialConfigActivity extends SherlockFragmentActivity {

	private boolean welcomeComplete = false;
	private int nextNodeIndex = 0;

	private ArrayList<Node> mNodeList;
	private ArrayAdapter<Node> mNodeAdapter;
	
	private Button mNextButton;
	private Button mCancelButton;
	private TextView mTitleText;
	private TextView mDescriptionText;

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
				changeFragment();
			}
		});

		mCancelButton = (Button) findViewById(R.id.initialconfig_cancelButton);
		mCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mTitleText = (TextView) findViewById(R.id.initialconfig_header);
		mDescriptionText = (TextView) findViewById(R.id.initialconfig_description);

		if (savedInstanceState == null) {
			//Fragment welcomeFragment = WelcomeFragment.newInstance();
			//FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			//ft.add(R.id.initialconfig_fragment, welcomeFragment).commit();
		} else {
			welcomeComplete = savedInstanceState.containsKey("welcomeComplete") ? savedInstanceState
					.getBoolean("welcomeComplete") : false;
			//confirmNodesComplete = savedInstanceState.containsKey("confirmNodesComplete") ? savedInstanceState
			//		.getBoolean("confirmNodesComplete") : false;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean("welcomeComplete", this.welcomeComplete);
		//savedInstanceState.putBoolean("confirmNodesComplete", this.confirmNodesComplete);
	}

	@Override
	public void onBackPressed() {

	}

	void changeFragment() {
		if (!welcomeComplete) {
			// Just pressed next on the welcome screen
			welcomeComplete = true;

			// We need to get from the server the nodes that are available
			new UpdateNodes().execute();

		} else if (nextNodeIndex < mNodeList.size()) {
			// Do individual Node scans
			
			Toast.makeText(InitialConfigActivity.this, "#"+nextNodeIndex, Toast.LENGTH_SHORT).show();
			
			nextNodeIndex++;
		} else {
			
		}

		//FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		//ft.replace(R.id.initialconfig_fragment, fragment);
		//ft.addToBackStack(null);
		//ft.commit();
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
			return nm.getNodeList();
		}

		protected void onPostExecute(ArrayList<Node> result) {
			progressDialog.dismiss();
			if (result != null) {
				mTitleText.setText("We found these nodes");
				mDescriptionText.setText("If there are any missing, make sure they are turned on and connected to the network, then try again.");
				mNodeList.clear();
				mNodeList.addAll(result);
				mNodeAdapter.notifyDataSetChanged();
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
		}
		
		protected void onPostExecute(Void none) {
			progressDialog.dismiss();
		}
		
	}
}

