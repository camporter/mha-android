package com.teamacra.myhomeaudio.manager;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.locations.NodeSignature;
import com.teamacra.myhomeaudio.node.Node;

/**
 * Maintains the entire configuration of nodes for this client.
 * 
 * @author Cameron
 * 
 */
public class ConfigurationManager {

	private ArrayList<NodeSignature> signatures;
	private static ConfigurationManager instance;
	private MHAApplication app;

	private ConfigurationManager(MHAApplication app) {
		this.app = app;
		this.signatures = new ArrayList<NodeSignature>();
	}

	public synchronized static ConfigurationManager getInstance(
			MHAApplication app) {
		if (instance == null) {
			instance = new ConfigurationManager(app);
		}
		return instance;
	}

	/**
	 * Records a device found during the configuration process. It also checks
	 * to make sure that the device is an actual node using the NodeManager,
	 * since we don't care about non-node bluetooth devices.
	 * 
	 * @param currentNode
	 *            The node for the room that the user is currently in. (Not
	 *            necessarily the same as the device being stored!)
	 * @param bluetoothName
	 *            The bluetooth name for the device.
	 * @param bluetoothAddress
	 *            The bluetooth address for the device.
	 * @param rssi
	 *            The rssi value that we get for the device.
	 * @return Whether the device is a node or not.
	 */
	public boolean storeDeviceSignal(Node currentNode, String bluetoothName,
			String bluetoothAddress, int rssi) {

		NodeManager nm = NodeManager.getInstance(app);

		Node device = nm.getNode(bluetoothName, bluetoothAddress, true);
		// Make sure that: The device exists as an active node, the current node
		// can be found in the node manager, and the current node is active.
		if (device != null && nm.nodeExists(currentNode)
				&& currentNode.isActive()) {

			// Try to get the signature for the current node that the signal is
			// being recorded under
			NodeSignature currentNodeSignature = getSignature(currentNode);
			if (currentNodeSignature != null) {
				// The current node does not have a signature yet, so create it
				// and add it to the list of signatures.
				currentNodeSignature = new NodeSignature(currentNode);
				signatures.add(currentNodeSignature);
			}
			
			// Store the device and rssi value in the signature
			currentNodeSignature.storeSignal(device, rssi);
			
			return true;
		}
		return false;
	}

	/**
	 * Get the given node's signature.
	 * 
	 * @param node
	 *            The node to get the signature of, or null if the given Node
	 *            does not have a signature.
	 */
	private NodeSignature getSignature(Node node) {
		for (Iterator<NodeSignature> i = signatures.iterator(); i.hasNext();) {
			NodeSignature nextSignature = i.next();
			if (nextSignature.getNode() == node) {
				return nextSignature;
			}
		}
		return null;
	}
	
	/**
	 * Clears the configuration.
	 * 
	 */
	public void resetSignatures() {
		signatures.clear();
	}
	
	
	public JSONArray getConfigurationJSON() {
		JSONArray array = new JSONArray();
		
		for (Iterator<NodeSignature> i = signatures.iterator(); i.hasNext();) {
			array.put(i.next().toJSON());
		}
		
		return array;
	}
}
