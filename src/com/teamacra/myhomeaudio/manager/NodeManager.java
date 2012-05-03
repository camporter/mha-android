package com.teamacra.myhomeaudio.manager;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.http.HttpNode;
import com.teamacra.myhomeaudio.node.Node;

public class NodeManager {

	private ArrayList<Node> nodeList;
	private HttpNode httpNode;

	private static final String TAG = "NodeManager";
	private static NodeManager instance;

	private NodeManager(MHAApplication app) {
		httpNode = new HttpNode(app);
		nodeList = new ArrayList<Node>();
	}

	public synchronized static NodeManager getInstance(MHAApplication app) {
		if (instance == null) {
			instance = new NodeManager(app);
		}
		return instance;
	}

	/**
	 * Updates the list of node objects.
	 * 
	 * @return Whether the node list update succeeded.
	 */
	public synchronized boolean updateNodes() {

		ArrayList<Node> newNodeList = httpNode.getNodes();

		if (newNodeList != null) {
			nodeList = newNodeList;
			return true;
		}

		return false;
	}

	public synchronized boolean updateConfiguration(
			ArrayList<Node> newConfiguration) {
		return false;

	}

	/**
	 * Gets a list of Node objects.
	 * <p>
	 * Note, this doesn't automatically update from the server.
	 * 
	 * @param onlyActive
	 *            Whether to return only nodes that are currently active.
	 * 
	 * @return ArrayList of Node within manager
	 */
	public ArrayList<Node> getNodeList(boolean onlyActive) {
		updateNodes();
		if (onlyActive) {
			ArrayList<Node> activeList = new ArrayList<Node>();
			for (Iterator<Node> i = nodeList.iterator(); i.hasNext();) {
				Node nextNode = i.next();
				if (nextNode.isActive()) {
					activeList.add(nextNode);
				}
			}
			return activeList;
		} else {
			Log.d(TAG, "Returning NodeList, size =" + nodeList.size());
			return new ArrayList<Node>(nodeList);
		}
	}

	/**
	 * Get a Node by its id.
	 * 
	 * @param id
	 *            The id of the Node.
	 * @return The Node object, or null if a matching node is not found.
	 */
	public Node getNode(int id) {
		updateNodes();
		for (Iterator<Node> i = nodeList.iterator(); i.hasNext();) {
			Node nextNode = i.next();
			if (nextNode.id() == id) {
				return nextNode;
			}
		}
		return null;
	}
	
	/**
	 * Get a Node by its name, address, and whether it is active.
	 * 
	 * @param name The name of the node.
	 * @param bluetoothAddress The bluetooth address of the node.
	 * @param isActive Whether the node is active.
	 * @return The Node object, or null if a matching node is not found.
	 */
	public Node getNode(String name, String bluetoothAddress, boolean isActive) {
		updateNodes();
		for (Iterator<Node> i = nodeList.iterator(); i.hasNext();) {
			Node nextNode = i.next();
			if (nextNode.name().equals(name)
					&& nextNode.bluetoothAddress().equals(bluetoothAddress)
					&& (nextNode.isActive() == isActive)) {
				return nextNode;
			}
		}
		return null;
	}
	
	public void forceUpdateNodes(){
		while(!updateNodes());
	}
	
	/**
	 * Makes sure that the exact same Node instance exists in the node manager.
	 * 
	 * @param node 
	 * @return
	 */
	public boolean nodeExists(Node node) {
		for (Iterator<Node> i = nodeList.iterator(); i.hasNext();) {
			if (i.next() == node) {
				return true;
			}
		}
		return false;
	}

}
