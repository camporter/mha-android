package com.teamacra.myhomeaudio.manager;

import java.util.ArrayList;
import java.util.Iterator;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.http.HttpNode;
import com.teamacra.myhomeaudio.node.Node;

public class NodeManager {
	private ArrayList<Node> nodeList;
	private HttpNode httpNode;

	private static NodeManager instance;

	private NodeManager(MHAApplication app) {
		httpNode = new HttpNode(app);
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

	/**
	 * Gets a list of Node objects.
	 * <p>
	 * Note, this doesn't automatically update from the server.
	 * 
	 * @return
	 */
	public ArrayList<Node> getNodeList() {
		return new ArrayList<Node>(nodeList);
	}

	/**
	 * Get a Node by its id.
	 * 
	 * @param id
	 *            The id of the Node.
	 * @return The Node object, or null if a matching stream object wasn't
	 *         found.
	 */
	public Node getNode(int id) {
		for (Iterator<Node> i = nodeList.iterator(); i.hasNext();) {
			Node nextNode = i.next();
			if (nextNode.id() == id) {
				return new Node(nextNode);
			}
		}
		return null;
	}

}
