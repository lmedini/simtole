package fr.cnrs.liris.simtole.protocols;

import java.util.ArrayList;

import fr.cnrs.liris.simtole.query.Query;

import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Node;

public abstract class AbstractQueryFloodingProtocol extends AbstractQueryProtocol{

	public abstract void sendQueryFlooding(Node node, int protocolID, Query que);

	public ArrayList<Node> flooding(Node node, int protocolID) {
		int linkableID = FastConfig.getLinkable(protocolID);
		Linkable linkable = (Linkable) node.getProtocol(linkableID);
		ArrayList<Node> result = new ArrayList<Node>();

		// recuperation des voisins du noeud courant
		for (int i=0; i<linkable.degree(); i++){ 
			result.add(linkable.getNeighbor(i));
		}
		return result;
	}
}