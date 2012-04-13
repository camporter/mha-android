package com.teamacra.myhomeaudio.locations;

import java.util.ArrayList;

import android.content.Intent;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.bluetooth.DiscoveryService;
import com.teamacra.myhomeaudio.node.Node;

public class NodeConfiguration {
	private static Intent intent_;
	private static NodeConfiguration nodeConfig;

	private NodeConfiguration(Intent intent) {
		intent_ = intent;
	}

	public static ArrayList<NodeSignalRange> generateNodeList(Node node) {
		// TODO Auto-generated method stub
		ArrayList<NodeSignalRange> foundNodes = new ArrayList<NodeSignalRange>();
		
		DiscoveryService discoveryService = new DiscoveryService();
		discoveryService.getApplicationContext().startActivity(new Intent(intent_));
		
		
		NodeSignalRange r = new NodeSignalRange(0,2,7);
		foundNodes.add(r);
		
		return foundNodes;
	}

	public static NodeConfiguration getInstance(Intent intent) {
		if(nodeConfig == null){
			nodeConfig = new NodeConfiguration(intent);
		}
		return nodeConfig;
	}


}
