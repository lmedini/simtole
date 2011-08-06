package fr.cnrs.liris.simtole.protocols;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import fr.cnrs.liris.simtole.node.SemanticNode;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;

/**
 * Protocole de redéfinition de la topologie du réseau ?
 *
 */
public class SemClusterProtocol extends AbstractQueryProtocol implements CDProtocol{

	private final int pidQuery;

	public SemClusterProtocol(String prefix) {
		this.prefix=prefix;
		pidQuery = Configuration.getPid(prefix + "." + PAR_PROT_QUERY);
	}

	public void nextCycle(Node nod, int protocolID) {
		SemanticNode node = (SemanticNode) nod;
		//QueryProtocolTemplate protocolQ1 = (QueryProtocolTemplate) node.getProtocol(pidQuery);

		int linkableID = FastConfig.getLinkable(pidQuery);
		Linkable linkable = (Linkable) node.getProtocol(linkableID);
//		System.out.println("<<<<<<<<<<< nb voisins >>>>>>>>>>"+linkable.degree());
//		for(int j=0;j<linkable.degree();j++)
//		{
//			System.out.println(linkable.getNeighbor(j).getID());
//		}

		HashMap<Integer,Double> tabMet = new HashMap<Integer,Double>();
		HashMap<Integer,Double> tabVoisin = new HashMap<Integer,Double>();
		//HashMap<Integer,Integer> tabDistant=new HashMap<Integer,Integer>();

		for(int j=0;j<linkable.degree();j++)
		{
			int k=(int)linkable.getNeighbor(j).getID();
			if(tabMet.containsKey(k))
			{
				tabMet.put(new Integer(k), (Double) node.getTableauDistance().get(k));

				tabVoisin.put(new Integer(k), (Double) tabMet.get(k));
				tabMet.remove(k);
			}
		}
		//donc tabVoisin contient tous les voisins et tabMet contient tous les similarites des noeuds distants
		int compteur=0;
		for(Iterator<?> it = tabMet.entrySet().iterator();it.hasNext();)
		{

			Entry<?, ?> entry=(Entry<?, ?>)it.next();
			for(Iterator<?> it2 = tabVoisin.entrySet().iterator();it2.hasNext();)
			{
				Entry<?, ?> entry2=(Entry<?, ?>)it2.next();
				int i=(Integer)entry.getValue();
				int j=(Integer)entry2.getValue();

				if(i>j)
				{
					if(compteur<linkable.degree()-2)
					{
						int index=0;
						//look for the index of current voisin to replace
						for(int p=0;p<linkable.degree();p++)
						{
							//long == Integer?
							if (linkable.getNeighbor(p).getID() == (Integer)entry2.getKey())
							{
								index=(Integer)entry2.getKey();
								break;
							}
						}
						//replace edge
						linkable.setNeighbor(index,Network.get((Integer)entry.getKey()));

						compteur++;
					}
				}
			}
		}
	}
	
	public Object clone(){
		SemClusterProtocol temp = (SemClusterProtocol) super.clone();
			//new SemClusterProtocol(this.prefix);
		temp.prefix = this.prefix;
		return temp;
	}
}