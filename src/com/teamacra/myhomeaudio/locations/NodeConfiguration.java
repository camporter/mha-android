package com.teamacra.myhomeaudio.locations;

import java.util.ArrayList;

import android.content.Intent;

import com.teamacra.myhomeaudio.MHAApplication;
//import com.teamacra.myhomeaudio.bluetooth.BluetoothService;
import com.teamacra.myhomeaudio.node.Node;

/*
 * Create a node Configuration object
 * -stores node being configured
 * -array of nodeSignalRanges
 * 
 * method
 * -generate NSB
 * 
 * process
 * init nodeConfig
 * call bluetooth dscoveryservice
 * create device list (string)
 * convert to deviceobject
 * 
 * 
 * 
 * 
 * 
 */




public class NodeConfiguration {

	private Intent intent_;
	private final MHAApplication app_;
	private Node setupNode_;
	private ArrayList<Node> foundNodes_;

	public NodeConfiguration(MHAApplication app, Node node) {
		app_ = app;
		setupNode_ = node;
		foundNodes_ = new ArrayList<Node>();
	}

	public NodeSignalBoundary generateNodeList() {
		return new NodeSignalBoundary(0);
	}

	public boolean updateNodeList() {
		//BluetoothService discoveryService = new BluetoothService();
		//discoveryService.getApplicationContext().startActivity(new Intent(intent_));
		return true;
	}

	public ArrayList<NodeSignalRange> getFoundNodes() {
		return new ArrayList<NodeSignalRange>();
	}

}
