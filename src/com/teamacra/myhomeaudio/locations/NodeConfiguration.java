package com.teamacra.myhomeaudio.locations;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Intent;

import com.teamacra.myhomeaudio.MHAApplication;
//import com.teamacra.myhomeaudio.bluetooth.BluetoothService;
import com.teamacra.myhomeaudio.manager.NodeManager;
import com.teamacra.myhomeaudio.node.Node;

public class NodeConfiguration {

	private Intent intent_;
	private final MHAApplication app_;
	private Node setupNode_;
	private ArrayList<NodeSignalRange> foundNodes_;
	private static int counter = 0;

	public NodeConfiguration(MHAApplication app, Node node) {
		app_ = app;
		setupNode_ = node;
		foundNodes_ = new ArrayList<NodeSignalRange>();
	}

	public NodeSignalBoundary generateNodeList() {
		NodeSignalBoundary range = new NodeSignalBoundary(setupNode_.id());
		Iterator<NodeSignalRange> i = foundNodes_.iterator();
		while (i.hasNext()) {
			range.addNodeRange(i.next());
		}
		return range;
	}

	public boolean updateNodeList() {
		// BluetoothService discoveryService = new BluetoothService();
		// discoveryService.getApplicationContext().startActivity(new
		// Intent(intent_));

		ArrayList<String> newReadings = new ArrayList<String>();
		if (counter == 0) {
			newReadings.add(String.valueOf(1));
			newReadings.add(String.valueOf(30));
			counter++;
		} else if (counter == 1) {
			newReadings.add(String.valueOf(1));
			newReadings.add(String.valueOf(15));
			counter++;
		} else if (counter == 2) {
			newReadings.add(String.valueOf(1));
			newReadings.add(String.valueOf(35));
		}

		ArrayList<DeviceObject> deviceList = createDeviceList(newReadings);

		updateNodeRange(deviceList);
		return true;
	}

	private void updateNodeRange(ArrayList<DeviceObject> deviceList) {
		Iterator<DeviceObject> i = deviceList.iterator();
		DeviceObject device;
		while (i.hasNext()) {
			device = i.next();
			Iterator<NodeSignalRange> j = foundNodes_.iterator();
			while (j != null) {
				if (j.hasNext()) {
					NodeSignalRange range = j.next();
					if (range.getNodeId() == device.id) {
						if (device.rssi < range.getMin()) {
							range.setMin(device.rssi);
						} else if (device.rssi > range.getMax()) {
							range.setMax(device.rssi);
						}
						j = null;
					}
				} else {
					NodeSignalRange range = new NodeSignalRange(device.id);
					range.setMax(device.rssi);
					range.setMin(device.rssi);
					foundNodes_.add(range);
					j = null;
				}

			}
		}
	}

	private ArrayList<DeviceObject> createDeviceList(
			ArrayList<String> newReadings) {
		ArrayList<DeviceObject> deviceList = new ArrayList<DeviceObject>();

		if (newReadings.size() % 2 == 0) {
			Iterator<String> i = newReadings.iterator();
			while (i.hasNext()) {
				int id = Integer.parseInt(i.next().trim());
				int rssi = Integer.parseInt(i.next().trim());
				deviceList.add(new DeviceObject(id, rssi));
			}
		}

		removeNoneNodes(deviceList);

		return deviceList;
	}

	private void removeNoneNodes(ArrayList<DeviceObject> deviceList) {
		NodeManager nm = NodeManager.getInstance(app_);

		Iterator<DeviceObject> i = deviceList.iterator();
		ArrayList<DeviceObject> activeDeviceList = new ArrayList<DeviceObject>();
		while (i.hasNext()) {
			Node node = nm.getNode(i.next().id);
			if (node == null) {
				i.remove();
			}
		}

	}

	public ArrayList<NodeSignalRange> getFoundNodes() {
		ArrayList<NodeSignalRange> signature = new ArrayList<NodeSignalRange>();
		signature.addAll(foundNodes_);
		return signature;
	}

}
