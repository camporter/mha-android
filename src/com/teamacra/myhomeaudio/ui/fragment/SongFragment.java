package com.teamacra.myhomeaudio.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.media.MediaDescriptor;

public class SongFragment extends Fragment {

	private ArrayAdapter mListAdapter;
	private ListView mMediaListView;
	private ArrayList<MediaDescriptor> mMediaList;

	public static SongFragment newInstance() {
		SongFragment fragment = new SongFragment();

		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.song_fragment, container, false);

		mMediaList = new ArrayList<MediaDescriptor>();
		mMediaList.add(new MediaDescriptor(0, "I like music", "", "", ""));
		mListAdapter = new ArrayAdapter<MediaDescriptor>(this.getActivity(),
				android.R.layout.simple_list_item_1, mMediaList);
		mMediaListView = (ListView) view.findViewById(R.id.songListView);
		mMediaListView.setAdapter(mListAdapter);
		mMediaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getActivity(), mMediaList.get(position).title(),
						Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}

	/**
	 * Clears any previous songs out of the list, adds a new song
	 * 
	 * @param newMedia
	 */
	public void updateSongList(ArrayList<MediaDescriptor> newMedia) {
		if (newMedia != null) {
			mMediaList.clear();
			mMediaList.addAll(newMedia);
			mListAdapter.notifyDataSetChanged();
		}
	}

	private void playMedia(MediaDescriptor descriptor) {
		this.getActivity();
	}
}
