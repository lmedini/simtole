package fr.cnrs.liris.simtole_old.protocols;

import java.util.ArrayList;
import java.util.Iterator;

import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.Node;
import fr.cnrs.liris.simtole.protocols.InfoLocalProtocol;
import fr.cnrs.liris.simtole.protocols.QueryQueueManagementProtocol;
import fr.cnrs.liris.simtole.protocols.SemResultProtocol;
import fr.cnrs.liris.simtole.query.SemQuery;

public class QueryProtocol extends QueryProtocolTemplate_old{

	// identifiant du protocol QueryFileProtocol
	private final int pidQueryFile;
	private final int pidResult;
	private final int pidInfoLocal;
	
	private static final String PAR_PROT_QUERY = "query_protocol";
	private static final String PAR_PROT_RESULT = "result_protocol";
	private static final String PAR_PROT_INFO = "info_protocol";
	
	public QueryProtocol(String prefix) {
		super(prefix);
		
		pidQueryFile = Configuration.getPid(prefix + "." + PAR_PROT_QUERY);
		pidResult = Configuration.getPid(prefix + "." + PAR_PROT_RESULT);
		pidInfoLocal = Configuration.getPid(prefix + "." + PAR_PROT_INFO);
		

	}
	
	public void nextCycle(Node node, int protocolID) {
		System.out.println("je suis bien dans queryProtocol");
		// recuperation du protocol QueryFileProtocol associe au noeud courant
		QueryQueueManagementProtocol protocolQ = (QueryQueueManagementProtocol) node.getProtocol(pidQueryFile);
		QueryProtocol self = (QueryProtocol)node.getProtocol(protocolID);
		SemResultProtocol protocolR=(SemResultProtocol)node.getProtocol(pidResult);
		InfoLocalProtocol protocolInfo =(InfoLocalProtocol) node.getProtocol(pidInfoLocal); 
		
		System.out.println("cycle "+CDState.getCycle()+" sur noeud "+node.getID()+" size of listmapping "+protocolInfo.getMappingsCount());
		
		
		/*int linkableID = FastConfig.getLinkable(protocolID);
		Linkable linkable = (Linkable) node.getProtocol(linkableID);
		ArrayList<Node> neighbour = new ArrayList<Node>();
		for (int i=0; i<linkable.degree(); i++){ 
			neighbour.add(linkable.getNeighbor(i));
		}
		System.out.println("noeud "+ node.getID()+" son voisin est "+neighbour.get(0).getID());*/
		
		//int cyc=Configuration.getInt(CDSimulator.PAR_CYCLES);
		
		if (protocolQ.getPendingQueriesCount() != 0) {
			if(tableauMetrique!=null)
			{
			    Iterator iterator = tableauMetrique.keySet().iterator(); 
			    //System.out.println("dans le pair "+node.getID());
			    if(iterator.hasNext()) System.out.println("nodeID is======= "+node.getID());
		        while(iterator.hasNext()) {  
		            int s = (Integer)iterator.next();  
		             System.out.print(s+"...");  
		             int temp = tableauMetrique.get(s);  
		             System.out.println(temp);  
		        }
			}
			/*if(protocolQ.getListMapping()!=null)
			{
				Iterator it = protocolQ.getListMapping().iterator();
				
				while(it.hasNext())
				{
					Mapping mapping=(Mapping)it.next();
					if(mapping.getType().equals("equivalent"))
					{
						EquivalentMapping equi=(EquivalentMapping)mapping;
						System.out.println("my class "+equi.uriMyClass+" mapping type "+equi.getType());
					}
					if(mapping.getType().equals("subsomption"))
					{
						SubsomptionMapping sub=(SubsomptionMapping)mapping;
						System.out.println("my class "+sub.uriMyClass+" mapping type "+sub.getType());
					}
					
				}
				
			}*/
		
			//System.out.println("fin sur nodeID========= "+node.getID());
			return;
		}
		else {
			try{
				for(int i=0; i<protocolQ.getPendingQueriesCount(); i++){
					SemQuery query = (SemQuery) protocolQ.getPendingQuery(i);
					System.out.println("--------------------------- Cycle " + CDState.getCycle() + " du noeud "+ node.getID() +" ---------------------");
					System.out.println("requete num " + query.getQueryID());
					//test si on est au node de depart
/*					if(node.getID()==query.getNodeDepart())
					{
						if(query.getType()==1)
						{
//							query.setOntologyTemp(self.ontology);
						}
					}
					else
					{
						switch(query.getType())
						{
						case 0:
							if ( query.getInfosTraitement().getValeur().equals(self.getNomFichier()) )
							{	
								System.out.println("find similar domain!!!!!!!!!!!"+query.getInfosTraitement().getValeur()+" sur noeud "+ node.getID());
								// creation d'un resultat
//								AbstractResult result = new AbstractResult(node.getID(),1,query.getPath());
//								
//								ArrayList<AbstractResult> listeRes = protocolR.getTabResultat();
//								
//								if (listeRes == null) listeRes = new ArrayList<AbstractResult>();
//								 
//								listeRes.add(result);
//								protocolR.setTabResultat(listeRes);
//								System.out.println("");
								
							}
							break;
							
						case 1:
							if(!query.getTraite())
							//if(query.getNoCycle() < CDState.getCycle())
							{
								int degreSubclasse=0;
								int degreEquivalent=0;
								String uriLocal;
								String uriDistant;
								Reasoner reasoner = new Reasoner( manager );
								Set <OWLOntology> setOfOntologies = new HashSet <OWLOntology> ();
								setOfOntologies.add(query.getOntologie());
								setOfOntologies.add(self.ontology);
								reasoner.loadOntologies( setOfOntologies );
								
								//ArrayList<EquivalentMapping> tmpEquivalent = new ArrayList<EquivalentMapping>();
								//ArrayList<SubsomptionMapping> tmpSubsomption = new ArrayList<SubsomptionMapping>();
								ArrayList<Mapping> tmpListMapping = new ArrayList<Mapping>();
								for ( OWLClass classe1: query.getOntologie().getReferencedClasses() ) {
									for ( OWLClass classe2: self.ontology.getReferencedClasses() ){
										if ( reasoner.isSubClassOf(classe1,classe2) ) {
											degreSubclasse++;
											uriLocal=findUri(classe1,query);
											uriDistant=findUri(classe2,query);
											SubsomptionMapping sub = new SubsomptionMapping(uriLocal, uriDistant,0);
											tmpListMapping.add(sub);
											protocolQ.getListMapping().add(sub);
										}
										if ( reasoner.isSubClassOf(classe2,classe1) ) {
											degreSubclasse++;
											uriLocal=findUri(classe1,query);
											uriDistant=findUri(classe2,query);
											SubsomptionMapping sub = new SubsomptionMapping(uriLocal, uriDistant,1);
											tmpListMapping.add(sub);
											protocolQ.getListMapping().add(sub);
										}
										if ( reasoner.isEquivalentClass(classe1,classe2)){
											degreEquivalent++;
											uriLocal=findUri(classe1,query);
											uriDistant=findUri(classe2,query);
											EquivalentMapping equi = new EquivalentMapping(uriLocal, uriDistant); 
											tmpListMapping.add(equi);
											protocolQ.getListMapping().add(equi);
										}
									}
								}
								System.out.println("nb equivalent est "+degreEquivalent);
								System.out.println("nb sub est "+degreSubclasse);
								//protocolInfo.addNbEquivalent(degreEquivalent);
								//protocolInfo.addNbSubsomption(degreSubclasse);
								int similarite=ComputeSimilarite.calcule(degreEquivalent, degreSubclasse);
								
								if(similarite > query.seuil)
								{
									Result result = new Result(node.getID(),1,query.getPath(),similarite);
									result.setListMapping(tmpListMapping);
									ArrayList<Result> listeRes = protocolR.getTabResultat();
									if (listeRes == null) listeRes = new ArrayList<Result>();
									listeRes.add(result);
									protocolR.setTabResultat(listeRes);
								}
								query.setTraite(true);
							}
							break;
						}
						//interrogation.traiterRequete(query, node, pidResult);
					}


					// decrementation du TTL de chaque requete
					query.decreaseTTL();
					// verification de la valeur de la requete par rapport au tableau de donnees du DataProtocol
					
					if (query.getTtl() > 0){
						// inondation des requetes du noeud courant vers ses voisins
						sendQueryFlooding(node,protocolID,query);
					}

*/				}
			}
			catch(Exception e){
				
			}
		}
	}
	
	public void sendQueryFlooding(Node node, int protocolID, SemQuery que) {
		boolean inside=false;
		// liste des voisins
		ArrayList <Node> peers = flooding(node, protocolID);
		System.out.print("nombre de voisins est "+peers.size()+" qui est noeud ");
		//System.out.print("Propagation vers les voisins : ");
		for (int i=0; i<peers.size()-1;i++){
			System.out.print(" "+peers.get(i).getID() + " - ");
		}
		System.out.println(" "+peers.get(peers.size()-1).getID());
		
		// recuperation du protocol QueryFileProtocol associe au noeud courant
		QueryQueueManagementProtocol nprotocol = (QueryQueueManagementProtocol)node.getProtocol(pidQueryFile);
		
		Node peer = null;
		QueryQueueManagementProtocol vprotocol = null;
		
		
		//ArrayList<Integer> queriesToRemove = new ArrayList<Integer>();
		for (int i = 0; i < peers.size(); i++) {
			peer = peers.get(i);
			if (!peer.isUp()) { continue; }
			// recuperation du protocol QueryFileProtocol associe au voisin
			vprotocol = (QueryQueueManagementProtocol)peer.getProtocol(pidQueryFile);
			//for (int j = 0; j < nprotocol.getQueryList().size(); j++) {
				SemQuery query = (SemQuery) que.clone();
				
				//verifier si la requete est deja traiter(flooding)
				/*String valeur=query.getValeur();
				boolean contains=false;
				if(nprotocol.getQueryListSent()!=null)
				{
					for(int kk=0;kk<nprotocol.getQueryListSent().size();kk++)
					{
						if(nprotocol.getQueryListSent().get(kk).getValeur().equals(valeur))
							{
							  contains=true;
							  break;
							}
						
					}
				}*/
				String queryId=query.getQueryID();
				boolean contains=false;
				if(nprotocol.getSentQueriesCount() != 0)
				{
					
					for(int kk=0;kk<vprotocol.getSentQueriesCount();kk++)
					{
						if(vprotocol.getSentQuery(kk).getQueryID().equals(queryId))
							{
							  contains=true;
							  break;
							}
						
					}
				}
				if (!query.endTTL() && !contains &&!vprotocol.containsPendingQuery(query) && peer.getID()!= query.getNodeDepart() && query.getNoCycle() < CDState.getCycle())
				//if (!query.endTTL() && !contains &&!vprotocol.getQueryList().contains(query) && peer.getID()!= query.getNodeID())
				{
					inside=true;
					System.out.print("Propagation vers les voisins : ");
					System.out.println(peer.getID());
					// ajout du  noeud dans le chemin de la requete
					query.getPath().add(node);
					query.setNoCycle(CDState.getCycle());
					query.setTraite(false);
					vprotocol.addPendingQuery(query);

					nprotocol.addSentQuery(query);
					//nprotocol.getQueryList().remove(j);
				}
			
		}
		if(inside)
		{
			nprotocol.removePendingQuery(que);
		}
	}
}