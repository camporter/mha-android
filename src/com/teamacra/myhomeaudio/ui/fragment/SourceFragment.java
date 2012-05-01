package com.teamacra.myhomeaudio.ui.fragment;

import java.util.ArrayList;

import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.media.MediaDescriptor;
import com.teamacra.myhomeaudio.source.Source;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SourceFragment extends Fragment {

	private ArrayAdapter<Source> mListAdapter;
	private ListView mSourceListView;
	private ArrayList<Source> mSourceList;
	private OnSourceSelectedListener mListener;

	private boolean hasInitialSelect;

	public static SourceFragment newInstance() {
		SourceFragment fragment = new SourceFragment();
		return fragment;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		Log.i("SOURCEFRAG", "Creating...");
		super.onCreate(savedInstanceState);
		
		mSourceList = new ArrayList<Source>();
		// mSourceList.add(new MediaDescriptor(0, "I like music", "", "", ""));
		mListAdapter = new ArrayAdapter<Source>(this.getActivity(),
				android.R.layout.simple_list_item_single_choice, mSourceList);
	}
	
	public void onResume() {
		super.onResume();
		Log.i("SOURCEFRAG", "Resuming...");
	}
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("SOURCEFRAG", "Creating View...");
		hasInitialSelect = false;
		View view = inflater.inflate(R.layout.song_fragment, container, false);
		
		mSourceListView = (ListView) view.findViewById(R.id.songListView);
		mSourceListView.setAdapter(mListAdapter);
		mSourceListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mSourceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getActivity(), mSourceList.get(position).name(),
						Toast.LENGTH_SHORT).show();
				mListener.onSourceSelected(mSourceList.get(position));

				selectSource(position);
			}
		});
		
		return view;
	}

	public void updateSourceList(ArrayList<Source> newSources) {
		Log.i("SOURCEFRAG", "Updating sources...");
		if (mSourceList == null) {
			mSourceList = new ArrayList<Source>();
		}
		if (newSources != null) {
			
			mSourceList.clear();
			mSourceList.addAll(newSources);
			mListAdapter.notifyDataSetChanged();

			/*
			 * Check if any source has been set yet. Used so that when the first
			 * source update happens, we choose a default source to list songs
			 * for.
			 */
			if (!hasInitialSelect && mSourceList.size() > 0) {
				selectSource(0);
			}
		}
	}

	/**
	 * Performs selection of a source by calling the listener and indicating
	 * that the source is actually selected in the list view.
	 * 
	 * @param position
	 *            The position in the source list to set as selected.
	 */
	private void selectSource(int position) {
		mListener.onSourceSelected(mSourceList.get(position));
		mSourceListView.setSelection(position);
		View selectedView = mSourceListView.getSelectedView();
		if (selectedView != null) {
			selectedView.setSelected(true);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		Log.i("SOURCEFRAG", "Attaching...");
		super.onAttach(activity);
		try {
			mListener = (OnSourceSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSourceSelectedListener");
		}
		
	}
	
	public void onPause() {
		super.onPause();
		
		Log.i("SOURCEFRAG", "Pausing...");
	}
	
	public void onStop() {
		Log.i("SOURCEFRAG", "Stopping...");
		super.onStop();
	}
	
	public void onDestroy() {
		Log.i("SOURCEFRAG", "Destroying...");
		super.onDestroy();
	}
	
	public interface OnSourceSelectedListener {
		public void onSourceSelected(Source source);
	}
}
