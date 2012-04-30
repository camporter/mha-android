package com.teamacra.myhomeaudio.ui.fragment;

import java.util.ArrayList;

import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.media.MediaDescriptor;
import com.teamacra.myhomeaudio.source.Source;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SourceFragment extends Fragment {

	private ArrayAdapter mListAdapter;
	private ListView mSourceListView;
	private ArrayList<Source> mSourceList;

	public static SourceFragment newInstance() {
		SourceFragment fragment = new SourceFragment();

		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.song_fragment, container, false);

		mSourceList = new ArrayList<Source>();
		// mSourceList.add(new MediaDescriptor(0, "I like music", "", "", ""));
		mListAdapter = new ArrayAdapter<Source>(this.getActivity(),
				android.R.layout.simple_list_item_1, mSourceList);
		mSourceListView = (ListView) view.findViewById(R.id.songListView);
		mSourceListView.setAdapter(mListAdapter);
		mSourceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getActivity(), mSourceList.get(position).name(),
						Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}

	public void updateSourceList(ArrayList<Source> newSources) {
		if (newSources != null) {
			mSourceList.clear();
			mSourceList.addAll(newSources);
			mListAdapter.notifyDataSetChanged();
		}
	}
}
