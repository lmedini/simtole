package fr.cnrs.liris.simtole_old.protocols;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLClass;

import fr.cnrs.liris.simtole.query.SemQueryInfos;

import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Node;

public abstract class QueryProtocolTemplate_old extends ContainerHolder implements CDProtocol{

	public QueryProtocolTemplate_old(String prefix) {
		super(prefix);
	}
	
	public abstract void nextCycle(Node node, int protocolID);

/*	A homogénéiser entre les différentes sous-classes
	public abstract void sendQueryFlooding(Node node, int protocolID, QueryRout que);
*/
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

	public String findUri(OWLClass c, SemQueryInfos q)
	{
		String[] list=c.getIRI().toString().split("#");
		String result=q.getValeur()+"#"+list[list.length-1];
		return result;
	}

	public String findUri(OWLClass c, long id)
	{
		String[] list=c.getIRI().toString().split("#");
		String result="network:/"+id+"#"+list[list.length-1];
		return result;
	}
}