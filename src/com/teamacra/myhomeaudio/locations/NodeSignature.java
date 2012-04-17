package com.teamacra.myhomeaudio.locations;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.node.Node;

/**
 * For every room with a node in it, we have a signature for that particular
 * room. The NodeSignature object comprises of the signal ranges of any nodes
 * found in the room (represented by a NodeSignalRange), and the node that
 * actually serves the room.
 * 
 */
public class NodeSignature {

	private Node node; // The node within the room
	private ArrayList<NodeSignalRange> nodeSignalRanges;

	public NodeSignature(Node node) {
		this.nodeSignalRanges = new ArrayList<NodeSignalRange>();
		this.node = node;
	}

	public NodeSignature(Node node, ArrayList<NodeSignalRange> nodeSignalRanges) {
		this.nodeSignalRanges = nodeSignalRanges;
		this.node = node;
	}

	/**
	 * Stores in this signature that the given node had the given rssi value
	 * during configuration.
	 * 
	 * @param node
	 *            The Node that the signal is for.
	 * @param rssi
	 *            The rssi value that was found.
	 * @return Whether the signal was actually stored.
	 */
	public boolean storeSignal(Node node, int rssi) {
		for (Iterator<NodeSignalRange> i = nodeSignalRanges.iterator(); i
				.hasNext();) {
			NodeSignalRange nextSignalRange = i.next();
			if (nextSignalRange.getNode() == node) {
				// This node already has a signal range in this signature,
				// so just pass the rssi value to the NodeSignalRange.
				nextSignalRange.storeRSSIValue(rssi);
			}
		}
		return false;
	}

	/**
	 * Gets the NodeSignalRange for a given Node within the signature.
	 * 
	 * @param node
	 *            The Node to get the signal range of.
	 * @return The NodeSignalRange, or null if this Node doesn't exist in this
	 *         signature.
	 */
	public NodeSignalRange getNodeRange(Node node) {
		for (Iterator<NodeSignalRange> i = nodeSignalRanges.iterator(); i
				.hasNext();) {
			NodeSignalRange signal = i.next();
			if (signal.getNode() == node) {
				return signal;
			}
		}
		return null;
	}

	/**
	 * Determines if a the given node is in the list of found nodes.
	 * 
	 * @param node
	 *            Node to check
	 * @return True - if found, False - if not
	 */
	private boolean containsNode(Node node) {
		for (Iterator<NodeSignalRange> i = nodeSignalRanges.iterator(); i
				.hasNext();) {
			if (i.next().getNode() == node) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the node representing this NodeSignature.
	 * 
	 * @return The Node itself.
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Return the number of nodes within the signature.
	 * 
	 * @return int Number of found nodes
	 */
	public int size() {
		return nodeSignalRanges.size();
	}

	// {"id":1,"nodeSignalRanges":[{"id":1,"min":1,"max":10},{"id":2,"min":2,"max":14},{"id":3,"min":5,"max":7}]}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			object.put("id", node.id());

			for (Iterator<NodeSignalRange> i = nodeSignalRanges.iterator(); i.hasNext();) {
				array.put(i.next().toJSON());
			}
			object.put("nodeSignalRanges", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return object;
	}
}
