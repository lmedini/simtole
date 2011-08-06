package fr.cnrs.liris.simtole.protocols;

import java.util.ArrayList;
import java.util.HashMap;

import fr.cnrs.liris.simtole.query.SemQuery;

import peersim.cdsim.CDProtocol;
import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.Node;


/**
 * Classe NeedProtocol - Associe a chaque pair une requete constituee d'une valeur souhaitee et du numero de cycle.

 */
public class NeedProtocol extends AbstractQueryProtocol implements CDProtocol {
	// identifiant du protocol QueryFileProtocol
	private final int pidQueryFile;

	// table de hachage avec comme cl� le numero de cycle, et comme valeurs des listes de requetes
	protected HashMap<Integer,ArrayList<SemQuery>> fileRequetes = new HashMap<Integer,ArrayList<SemQuery>>();
	
	public NeedProtocol(String prefix) {
		this.prefix = prefix;
		pidQueryFile = Configuration.getPid(prefix + "." + PAR_PROT_QUEUE);
	}
	
	public ArrayList<SemQuery> getTabRequetes(Integer cycle) {
		return fileRequetes.get(cycle);
	}

	public void nextCycle( Node node, int protocolID ) {
		//System.out.println("-------------- NeedProtocol -----------------------");
		Integer cycle = CDState.getCycle();
		ArrayList<SemQuery> tabRequetes = getTabRequetes(cycle);
		
		QueryQueueManagementProtocol protocolQ = (QueryQueueManagementProtocol) node.getProtocol(pidQueryFile);

		if (tabRequetes != null)
			for (SemQuery query:tabRequetes) {
				if (node.getID() == query.getNodeDepart()){
					protocolQ.addPendingQuery(query);
				}
				System.out.print("Cycle : [" + cycle + "] - Requetes du noeud " + node.getID() + " : ");
				for (int j=0;j<protocolQ.getPendingQueriesCount();j++){
					System.out.print(" query (" + protocolQ.getPendingQuery(j).getQueryID()+")");
				}
				System.out.println();

				this.fileRequetes.remove((Integer) cycle);
			}
		//System.out.println("------------ Fin NeedProtocol ---------------------");
	}

	public Object clone() {
		NeedProtocol cloneObj = (NeedProtocol) super.clone();
			//new NeedProtocol(this.prefix);
		cloneObj.prefix = this.prefix;

		ArrayList<SemQuery> tempCloneList = new ArrayList<SemQuery>();
		for (Integer key:this.fileRequetes.keySet()) {
			tempCloneList.clear();
			
			// clone all Queries from each list for each key
			ArrayList<SemQuery> thisList = this.fileRequetes.get(key);
			for (SemQuery query:thisList) tempCloneList.add((SemQuery) query.clone());
			
			cloneObj.fileRequetes.put(key, tempCloneList);
		}
		return cloneObj;
	}

	public void addQuery(SemQuery query, int noCycle) {
		ArrayList<SemQuery> listeReq = this.fileRequetes.get(noCycle);
		if (listeReq == null)
			listeReq = new ArrayList<SemQuery>();

		listeReq.add(query);
		this.fileRequetes.put(noCycle, listeReq);
	}

	public void removeQuery(SemQuery query, int noCycle) {
		ArrayList<SemQuery> queries = fileRequetes.get((Integer) noCycle);
		System.out.print("Retirage de la query " + query.getQueryID() + " du cycle " + noCycle);
		if(queries != null) 
			for(SemQuery queryTemp: queries)
				if(queryTemp.getQueryID().equals(query.getQueryID()))
					queries.remove(query);
	}
}