package com.teamacra.myhomeaudio.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.bluetooth.BluetoothService;
import com.teamacra.myhomeaudio.http.HttpClient;
import com.teamacra.myhomeaudio.http.StatusCode;
import com.teamacra.myhomeaudio.manager.ConfigurationManager;
import com.teamacra.myhomeaudio.manager.LocationManager;
import com.teamacra.myhomeaudio.manager.NodeManager;
import com.teamacra.myhomeaudio.manager.StreamManager;
import com.teamacra.myhomeaudio.media.MediaDescriptor;
import com.teamacra.myhomeaudio.node.Node;
import com.teamacra.myhomeaudio.source.Source;
import com.teamacra.myhomeaudio.stream.Stream;
import com.teamacra.myhomeaudio.ui.InitialConfigActivity.NodeConfigTask;
import com.teamacra.myhomeaudio.ui.InitialConfigActivity.SendConfigTask;
import com.teamacra.myhomeaudio.ui.fragment.SongFragment;
import com.teamacra.myhomeaudio.ui.fragment.SourceFragment;
import com.teamacra.myhomeaudio.ui.fragment.TestFragment;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

public class MyHomeAudioActivity extends SherlockFragmentActivity implements
		OnNavigationListener, SourceFragment.OnSourceSelectedListener,
		SongFragment.OnSongSelectedListener {

	private MHAApplication app;

	private TabAdapter mAdapter;

	private ViewPager mPager;

	// Add Stream properties
	private EditText mAddStreamEditText;
	private AlertDialog mAddStreamDialog;

	// Stream stuff
	private ArrayList<Stream> mStreamList;
	private ArrayAdapter<Stream> mStreamAdapter;

	// Node stuff
	private ArrayList<Node> mNodeList;
	private ArrayAdapter<Node> mNodeAdapter;
	private ListView mNodeListView;

	// Media stuff
	private SongFragment mSongFragment;
	private SourceFragment mSourceFragment;

	private String TAG = "MyHomeAudioActivity";
	AsyncTask<String, Void, Integer> mSendLocationTask;
	private int counter = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		app = (MHAApplication) getApplication();

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.main);

		// Setup the action bar
		Context context = getSupportActionBar().getThemedContext();
		mStreamList = new ArrayList<Stream>();
		mStreamAdapter = new ArrayAdapter<Stream>(context,
				R.layout.sherlock_spinner_item, mStreamList);
		mStreamAdapter
				.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(mStreamAdapter, this);

		// Bluetooth Receiver Register
		registerReceiver(mReceiver, new IntentFilter(
				BluetoothService.DISCOVERY_START));
		registerReceiver(mReceiver, new IntentFilter(
				BluetoothService.DEVICE_UPDATE));
		registerReceiver(mReceiver, new IntentFilter(
				BluetoothService.DISCOVERY_FINISH));

		mSendLocationTask = new SendLocationTask().execute();
		
		// Setup the node list
		mNodeList = new ArrayList<Node>();

		mNodeAdapter = new ArrayAdapter<Node>(context,
				android.R.layout.simple_list_item_multiple_choice, mNodeList);
		mNodeListView = (ListView) findViewById(R.id.nodeListView);
		mNodeListView.setAdapter(mNodeAdapter);
		mNodeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mNodeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ArrayList<Node> newlyAssignedNodeList = new ArrayList<Node>();

				// Get all the positions that are checked
				SparseBooleanArray checked = mNodeListView
						.getCheckedItemPositions();
				for (int i = 0; i < checked.size(); i++) {
					if (checked.valueAt(i) == true) {
						// Put nodes at the positions that are checked into the
						// new list of assigned nodes for the current stream
						newlyAssignedNodeList.add(mNodeList.get(checked
								.keyAt(i)));
					}
				}

				// Update the assigned nodes
				new AssignNodesTask().execute(newlyAssignedNodeList);
			}
		});

		// Setup the fragments
		mSongFragment = SongFragment.newInstance();
		mSourceFragment = SourceFragment.newInstance();

		// Setup the tabpages
		mAdapter = new TabAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.tabPager);
		mPager.setAdapter(mAdapter);
		mPager.setOffscreenPageLimit(6); // prevent ViewPager from destroying
											// any fragments
		PageIndicator mIndicator = (TabPageIndicator) findViewById(R.id.tabIndicator);
		mIndicator.setViewPager(mPager);

		// Setup the add stream dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getText(R.string.add_stream));
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View addStreamLayout = inflater.inflate(R.layout.add_stream_dialog,
				(ViewGroup) findViewById(R.layout.main));
		mAddStreamEditText = (EditText) addStreamLayout
				.findViewById(R.id.addStreamEditText);
		builder.setView(addStreamLayout);
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				new AddStreamTask().execute(mAddStreamEditText.getText()
						.toString());
			}
		});
		mAddStreamDialog = builder.create();

		// Run the update stuff
		new UpdateStreamsTask().execute("");
		new UpdateNodesTask().execute("");
		new UpdateSourcesTask().execute("");

	}

	public void onStart() {
		super.onStart();
		app.startBluetoothService(this, true);
	}

	public void onDestroy() {
		app.stopBluetoothService();
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, getText(R.string.add_stream)).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0, 2, 0, "Reconfigure").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(0, 3, 0, "Logout").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_NEVER);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			// Show the add stream dialog
			mAddStreamDialog.show();
			return true;

		} else if (item.getItemId() == 2) {
			// Start configuration
			Intent startConfigIntent = new Intent(this,
					InitialConfigActivity.class);
			this.startActivity(startConfigIntent);

		} else if (item.getItemId() == 3) {
			// Log the user out
			app.setLoggedOut();
			finish();
			return true;
		}
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		// Change the active stream
		// Toast.makeText(this, "Selected " + mActiveStream,
		// Toast.LENGTH_LONG).show();

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
	private class AddStreamTask extends
			AsyncTask<String, Void, ArrayList<Stream>> {

		private final ProgressDialog progressDialog = new ProgressDialog(
				MyHomeAudioActivity.this);
		private AlertDialog failureDialog;

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
				Toast.makeText(MyHomeAudioActivity.this,
						"Stream successfully added", Toast.LENGTH_SHORT).show();
			} else {
				// let the user know adding the stream failed
				progressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MyHomeAudioActivity.this);
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
	private class UpdateStreamsTask extends
			AsyncTask<String, Void, ArrayList<Stream>> {

		protected void onPreExecute() {
			Toast.makeText(MyHomeAudioActivity.this, "Updating Streams...",
					Toast.LENGTH_SHORT).show();
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
				Toast.makeText(MyHomeAudioActivity.this,
						"Updating streams failed!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class UpdateNodesTask extends
			AsyncTask<String, Void, ArrayList<Node>> {

		protected void onPreExecute() {
			Toast.makeText(MyHomeAudioActivity.this, "Updating Nodes...",
					Toast.LENGTH_SHORT).show();
		}

		protected ArrayList<Node> doInBackground(String... notUsed) {
			NodeManager nm = NodeManager.getInstance(app);
			nm.updateNodes();
			return nm.getNodeList(true);
		}

		protected void onPostExecute(ArrayList<Node> result) {
			if (result != null) {
				mNodeList.clear();
				// Set a 'follow' option
				mNodeList.add(new Node(-1, "FOLLOW ME", "", true));
				mNodeList.addAll(result);
				mNodeAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * Assigns nodes to the active stream.
	 * 
	 * @author cameron
	 * 
	 */
	private class AssignNodesTask extends
			AsyncTask<ArrayList<Node>, Void, Boolean> {

		protected void onPreExecute() {
		}

		protected Boolean doInBackground(ArrayList<Node>... node) {
			StreamManager sm = StreamManager.getInstance(app);

			int selected = MyHomeAudioActivity.this.getSherlock()
					.getActionBar().getSelectedNavigationIndex();
			Stream activeStream = MyHomeAudioActivity.this.mStreamList
					.get(selected);
			if (activeStream != null) {
				return sm.assignNodes(activeStream.id(), node[0]);
			}
			return null;
		}

		protected void onPostExecute(Boolean result) {

		}
	}

	private class UpdateSourcesTask extends
			AsyncTask<String, Void, ArrayList<Source>> {

		protected void onPreExecute() {
			Toast.makeText(MyHomeAudioActivity.this, "Updating sources...",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected ArrayList<Source> doInBackground(String... params) {
			StreamManager sm = StreamManager.getInstance(app);
			sm.updateSources();

			return sm.getSourceList();
		}

		protected void onPostExecute(ArrayList<Source> result) {
			if (result != null) {
				mSourceFragment.updateSourceList(result);
			}
		}
	}

	private class UpdateSourceMediaTask extends
			AsyncTask<Integer, Void, Source> {

		protected void onPreExecute() {
			Toast.makeText(MyHomeAudioActivity.this,
					"Getting media for source...", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Source doInBackground(Integer... params) {
			int sourceId = params[0];
			StreamManager sm = StreamManager.getInstance(app);
			return sm.updateSourceMedia(sourceId);
		}

		protected void onPostExecute(Source result) {
			if (result != null) {
				// Update worked
				mSongFragment.updateSongList(result.mediaList());
			} else {
				// update failed
			}
		}

	}

	/**
	 * Task to send the configuration off to the server.
	 */
	protected class SendLocationTask extends AsyncTask<String, Void, Integer> {
		LocationManager locationManager = null;
		
		protected void onPreExecute() {
			Log.d(TAG, "Starting SendLocationTask");
		}

		protected Integer doInBackground(String... notUsed) {
			locationManager = LocationManager.getInstance(app);
			while (!isCancelled()) {
				// Wait until the task is cancelled
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			HttpClient client = new HttpClient(app);
			Log.d(TAG, "Location: "
					+ locationManager.getLocationJSONArray().toString());
			return client.location(locationManager.getLocationJSONArray());
		}

		protected void onPostExecute(Integer statusCode) {
			locationManager = LocationManager.getInstance(app);
			locationManager.clear();
			mSendLocationTask = new SendLocationTask().execute();
		}

		public void addDevice(String name, String bluetoothAddress, int rssi) {
			Log.d(TAG,"Adding Device "+ name + " "+ rssi);
			locationManager = LocationManager.getInstance(app);
			if (rssi != Integer.MIN_VALUE) {
				locationManager.storeNode(name, bluetoothAddress, rssi);
			}
		}
	}

	private class TabAdapter extends FragmentPagerAdapter implements
			TitleProvider {

		private final String[] CONTENT = new String[] { "Sources", "Songs",
				"Artists", "Albums", "Genres" };

		public TabAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return mSourceFragment;
			case 1:
				//
				return mSongFragment;
			case 2:
				// Show artist list
				return TestFragment.newInstance("artist");
			case 3:
				return TestFragment.newInstance("album");
			default:
				return TestFragment.newInstance("genre");
			}
			// return TestFragment.newInstance(CONTENT[position]);
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}

		@Override
		public String getTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}
	}

	/**
	 * Captures when a source is selected in the source fragment.
	 */
	@Override
	public void onSourceSelected(Source source) {
		new UpdateSourceMediaTask().execute(source.id());
	}

	@Override
	public void onSongSelected(MediaDescriptor song) {
		// TODO
		// Do something
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothService.DISCOVERY_START.equals(action)) {
				Log.d(TAG,"Starting SendLocationTask");
				mSendLocationTask = new SendLocationTask().execute();
			} else if (BluetoothService.DEVICE_UPDATE.equals(action)) {
				if (mSendLocationTask != null) {
					((SendLocationTask) mSendLocationTask)
							.addDevice(intent.getStringExtra("deviceName"),
									intent.getStringExtra("deviceAddress"),
									intent.getIntExtra("deviceRssi",
											Integer.MIN_VALUE));
				}
				Log.i(TAG, "Device Found Name: " + intent.getStringExtra("deviceName") + " "
						+ intent.getIntExtra("deviceRssi", Integer.MIN_VALUE));
			} else if (BluetoothService.DISCOVERY_FINISH.equals(action)) {
				if(mSendLocationTask != null){
					Log.d(TAG, "SendLocationTask stopping");
					mSendLocationTask.cancel(true);
					mSendLocationTask = null;
				}
			}
		}
	};
}
