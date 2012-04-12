package com.teamacra.myhomeaudio.ui;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.http.HttpClient;
import com.teamacra.myhomeaudio.http.HttpNode;
import com.teamacra.myhomeaudio.http.HttpStream;
import com.teamacra.myhomeaudio.manager.NodeManager;
import com.teamacra.myhomeaudio.manager.StreamManager;
import com.teamacra.myhomeaudio.node.Node;
import com.teamacra.myhomeaudio.stream.Stream;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyHomeAudioActivity extends SherlockFragmentActivity implements OnNavigationListener {
	
	private TabAdapter mAdapter;

	private ViewPager mPager;

	// Add Stream properties
	private EditText mAddStreamEditText;
	private AlertDialog mAddStreamDialog;

	// Stream stuff
	private ArrayList<Stream> mStreamList;
	private ArrayAdapter<Stream> mStreamAdapter;
	private Stream mActiveStream;

	// Node stuff
	private ArrayList<Node> mNodeList;
	private ArrayAdapter<Node> mNodeAdapter;
	private ListView mNodeListView;

	private static final String[] CONTENT = new String[] { "Test", "Test 2", "Test 3", "Test 4" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		final MHAApplication app = (MHAApplication) getApplication();

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.main);

		// Setup the action bar
		Context context = getSupportActionBar().getThemedContext();
		mStreamList = new ArrayList<Stream>();
		mStreamAdapter = new ArrayAdapter<Stream>(context, R.layout.sherlock_spinner_item,
				mStreamList);
		mStreamAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(mStreamAdapter, this);
		
		// Setup the node list
		mNodeList = new ArrayList<Node>();

		mNodeAdapter = new ArrayAdapter<Node>(context,
				android.R.layout.simple_list_item_multiple_choice, mNodeList);
		mNodeListView = (ListView) findViewById(R.id.nodeListView);
		mNodeListView.setAdapter(mNodeAdapter);
		mNodeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mNodeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ArrayList<Node> newlyAssignedNodeList = new ArrayList<Node>();

				// Get all the positions that are checked
				SparseBooleanArray checked = mNodeListView.getCheckedItemPositions();
				for (int i = 0; i < checked.size(); i++) {
					if (checked.valueAt(i) == true) {
						// Put nodes at the positions that are checked into the
						// new list of assigned nodes for the current stream
						newlyAssignedNodeList.add(mNodeList.get(checked.keyAt(i)));
					}
				}
				
				// Update the assigned nodes
				new AssignNodes().execute(newlyAssignedNodeList);
			}
		});

		// Setup the tabpages
		mAdapter = new TabAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.tabPager);
		mPager.setAdapter(mAdapter);
		PageIndicator mIndicator = (TabPageIndicator) findViewById(R.id.tabIndicator);
		mIndicator.setViewPager(mPager);

		// Setup the add stream dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getText(R.string.add_stream));
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View addStreamLayout = inflater.inflate(R.layout.add_stream_dialog,
				(ViewGroup) findViewById(R.layout.main));
		mAddStreamEditText = (EditText) addStreamLayout.findViewById(R.id.addStreamEditText);
		builder.setView(addStreamLayout);
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				new AddStream().execute(mAddStreamEditText.getText().toString());
			}
		});
		mAddStreamDialog = builder.create();
		
		// Run the update stuff
		new UpdateStreams().execute("");
		new UpdateNodes().execute("");

	}

	public void onStart() {
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, getText(R.string.add_stream)).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0, 2, 0, "Reconfigure").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(0, 3, 0, "Logout").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			// Show the add stream dialog
			mAddStreamDialog.show();
			return true;
			
		} else if (item.getItemId() == 2) {
			Intent startConfigIntent = new Intent(this, InitialConfigActivity.class);
			this.startActivity(startConfigIntent);
			
		} else if (item.getItemId() == 3) {
			// Log the user out
			((MHAApplication) getApplication()).setLoggedOut();
			finish();
			return true;
		}
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		MHAApplication app = (MHAApplication) this.getApplication();

		// Change the active stream
		mActiveStream = mStreamList.get(itemPosition);
		Toast.makeText(this, "Selected " + mActiveStream, Toast.LENGTH_LONG).show();

		return true;
	}

	public static class MainPagerAdapter extends FragmentPagerAdapter {

		public MainPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return null;
		}

		@Override
		public int getCount() {
			return 0;
		}
	}

	/**
	 * Add the given stream to the server.
	 * 
	 * @author cameron
	 * 
	 */
	private class AddStream extends AsyncTask<String, Void, ArrayList<Stream>> {

		private final ProgressDialog progressDialog = new ProgressDialog(MyHomeAudioActivity.this);
		private AlertDialog failureDialog;
		MHAApplication app = (MHAApplication) MyHomeAudioActivity.this.getApplication();

		protected void onPreExecute() {
			progressDialog.show();
		}

		protected ArrayList<Stream> doInBackground(String... args) {

			StreamManager sm = StreamManager.getInstance(app);

			if (sm.addStream(args[0])) {
				return sm.getStreamList();
			}
			return null;
		}

		protected void onPostExecute(final ArrayList<Stream> result) {
			if (result != null) {
				// Update the list of streams since we added one
				mStreamList.clear();
				mStreamList.addAll(result);
				mStreamAdapter.notifyDataSetChanged();

				progressDialog.dismiss();
				Toast.makeText(MyHomeAudioActivity.this, "Stream successfully added",
						Toast.LENGTH_SHORT).show();
			} else {
				// let the user know adding the stream failed
				progressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(MyHomeAudioActivity.this);
				builder.setTitle("Add Stream failed");
				failureDialog = builder.create();
				failureDialog.show();

			}
		}
	}

	/**
	 * Update the streams for the activity.
	 * 
	 * @author cameron
	 * 
	 */
	private class UpdateStreams extends AsyncTask<String, Void, ArrayList<Stream>> {

		MHAApplication app = (MHAApplication) MyHomeAudioActivity.this.getApplication();

		protected void onPreExecute() {
			Toast.makeText(MyHomeAudioActivity.this, "Updating Streams...", Toast.LENGTH_SHORT)
					.show();
		}

		protected ArrayList<Stream> doInBackground(String... notUsed) {

			StreamManager sm = StreamManager.getInstance(app);
			sm.updateStreams();
			return sm.getStreamList();
		}

		protected void onPostExecute(ArrayList<Stream> result) {
			if (result != null) {
				mStreamList.clear();
				mStreamList.addAll(result);
				mStreamAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(MyHomeAudioActivity.this, "Updating streams failed!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class UpdateNodes extends AsyncTask<String, Void, ArrayList<Node>> {

		MHAApplication app = (MHAApplication) MyHomeAudioActivity.this.getApplication();

		protected void onPreExecute() {
			Toast.makeText(MyHomeAudioActivity.this, "Updating Nodes...", Toast.LENGTH_SHORT);
		}

		protected ArrayList<Node> doInBackground(String... notUsed) {
			NodeManager nm = NodeManager.getInstance(app);
			nm.updateNodes();
			return nm.getNodeList();
		}

		protected void onPostExecute(ArrayList<Node> result) {
			if (result != null) {
				mNodeList.clear();
				// Set a 'follow' option
				mNodeList.add(new Node(-1, "FOLLOW ME", ""));
				mNodeList.addAll(result);
				mNodeAdapter.notifyDataSetChanged();
			}
		}
	}

	private class AssignNodes extends AsyncTask<ArrayList<Node>, Void, Boolean> {

		MHAApplication app = (MHAApplication) MyHomeAudioActivity.this.getApplication();

		protected void onPreExecute() {
		}

		protected Boolean doInBackground(ArrayList<Node>... node) {
			StreamManager sm = StreamManager.getInstance(app);
			
			return sm.assignNodes(mActiveStream.id(), node[0]);
		}

		protected void onPostExecute(Boolean result) {
			
		}
	}

	private class TabAdapter extends TestFragmentAdapter implements TitleProvider {

		public TabAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return TestFragment.newInstance(MyHomeAudioActivity.CONTENT[position]);
		}

		@Override
		public int getCount() {
			return MyHomeAudioActivity.CONTENT.length;
		}

		@Override
		public String getTitle(int position) {
			return MyHomeAudioActivity.CONTENT[position % MyHomeAudioActivity.CONTENT.length]
					.toUpperCase();
		}
	}
}
