package fr.cnrs.liris.simtole_old.protocols;

import fr.cnrs.liris.simtole.InfoGlobal;
import fr.cnrs.liris.simtole.mapping.EquivalentMapping;
import fr.cnrs.liris.simtole.mapping.Mapping;
import fr.cnrs.liris.simtole.mapping.SubsomptionMapping;
import fr.cnrs.liris.simtole.protocols.InfoLocalProtocol;
import fr.cnrs.liris.simtole.protocols.QueryQueueManagementProtocol;
import fr.cnrs.liris.simtole.protocols.SemResultProtocol;
import fr.cnrs.liris.simtole.query.SemQuery;
import intersimul.util.xml.XMLLogWriter;

import java.util.ArrayList;

import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.Node;

public class QueryProtocol1 extends QueryProtocolTemplate_old{

	// identifiant du protocol QueryFileProtocol
	private final int pidQueryFile;
	private final int pidResult;
	private final int pidInfoLocal;
	
	private static final String PAR_PROT_QUERY = "query_protocol";
	private static final String PAR_PROT_RESULT = "result_protocol";
	private static final String PAR_PROT_INFO = "info_protocol";
	
	public QueryProtocol1(String prefix) {
		super(prefix);
		
		pidQueryFile = Configuration.getPid(prefix + "." + PAR_PROT_QUERY);
		pidResult = Configuration.getPid(prefix + "." + PAR_PROT_RESULT);
		pidInfoLocal = Configuration.getPid(prefix + "." + PAR_PROT_INFO);
		

	}
	
	public void nextCycle(Node node, int protocolID) {
		
		// recuperation du protocol QueryFileProtocol associe au noeud courant
		QueryQueueManagementProtocol protocolQ = (QueryQueueManagementProtocol) node.getProtocol(pidQueryFile);
		QueryProtocol1 self = (QueryProtocol1)node.getProtocol(protocolID);
		SemResultProtocol protocolR=(SemResultProtocol)node.getProtocol(pidResult);
		InfoLocalProtocol protocolInfo =(InfoLocalProtocol) node.getProtocol(pidInfoLocal); 
		
		XMLLogWriter xlw = new XMLLogWriter( "C" + CDState.getCycle() );
		
		// Si l'ontologie locale a des axiomes
		for(int i=0; i<protocolInfo.getMappingsCount(); i++)
		{
			Mapping mapping=protocolInfo.getMapping(i);
			if(mapping.getType().equals("equivalent"))
			{
				EquivalentMapping equi=(EquivalentMapping)mapping;
				System.out.println("my class "+equi.getLocalOntologyName() + " mapping type "+equi.getType());
				xlw.write("<analyse node='"+node.getID() + "' mapping='equivalent' from='" + equi.getLocalOntologyName() + "' />");
			}
			if(mapping.getType().equals("subsomption"))
			{
				SubsomptionMapping sub=(SubsomptionMapping)mapping;
				System.out.println("my class "+sub.getLocalOntologyName() + " mapping type "+sub.getType());
				xlw.write("<analyse node='"+node.getID() + "' mapping='subsomption' from='" + sub.getLocalOntologyName() + "' />");
			}
		}
/*		
		//extrair les mappings initiaux
		if(CDState.getCycle()==0)
		{
			Set<OWLAxiom> axioms=ontology.getAxioms();
			Random random = new Random();
			if(axioms!=null)
			{
				for(OWLAxiom oa : axioms)
				{
					String uriLocal=null;
					String uriDistant=null;
					if(oa instanceof OWLSubClassAxiom)
					{
						System.out.println(oa.toString());
						Set<OWLEntity> oe=oa.getReferencedEntities();
						int compt = 0;
						for(OWLEntity oc: oe)
						{
							//URI s=oc.getURI();
							//System.out.println(s.toString());
							if(compt==0)
							{uriLocal= "network:/"+node.getID()+"#"+oc.toString();}
							else
							{uriDistant="network:/"+random.nextInt(Network.size())+"#"+oc.toString();}
							compt++;
							
						}
						SubsomptionMapping sub = new SubsomptionMapping(uriLocal, uriDistant,0);
						protocolQ.getListMapping().add(sub);
						InfoGlobal.nbMapping++;
						InfoGlobal.nbMappingInit++;
					}
					System.out.println();
				}
			}
			
			if(protocolQ.getListMapping()!=null)
			{
				Iterator it = protocolQ.getListMapping().iterator();
				
				while(it.hasNext())
				{
					Mapping mapping=(Mapping)it.next();
					if(mapping.getType().equals("subsomption"))
					{
						SubsomptionMapping sub=(SubsomptionMapping)mapping;
						System.out.println("my class "+sub.uriMyClass);
					}
					
				}
				
			}
		}
				
		//partie query router

		if(!protocolQ.getQueryRoutList().isEmpty())
		{
			try{
				
				for(QueryRout queryRout:protocolQ.getQueryRoutList())
				{
					
					if(node.getID()==queryRout.getNodeDestinatair())
					{
						if(!queryRout.getTraite())
						{
							Result result = new Result(node.getID(),0,queryRout.getPath());
							//rajoute noeud courant dans la pathRout
							result.getPathRout().add(node);
	
							ArrayList<Result> listeRes = protocolR.getTabResultat();
							if (listeRes == null) listeRes = new ArrayList<Result>();
							listeRes.add(result);
							protocolR.setTabResultat(listeRes);
							queryRout.setTraite(true);
							InfoGlobal.nbMessage=InfoGlobal.nbMessage+(result.getPathRout().size()-1)*3;
						}
					}
					else
					{
						if (queryRout.getTtl() > 0){
							
							// inondation des requetes du noeud courant vers ses voisins
							sendQueryFlooding(node,protocolID,queryRout, xlw);
							
						}
					}
				}
			}
			catch(Exception e){}
		}
		
		//partie query
		if (protocolQ.getQueryList().isEmpty()) {
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
			return;
		}
		else {
			try{
				for(Query query:protocolQ.getQueryList()){
					System.out.println("--------------------------- Cycle " + CDState.getCycle() + " du noeud "+ node.getID() +" ---------------------");
					System.out.println("requete num " + query.getQueryID());
					//test si on est au node de depart
					if(node.getID()==query.getNodeDepart())
					{
						if(query.getType()==1)
						{
							query.setOntologie(self.ontology);
						}
					}

					if(node.getID()==query.getNodeDestinatair())
					{

						if(!query.getTraite())
						{
							
							int nbAllignement=protocolInfo.getNbAlignements();
							nbAllignement++;
							protocolInfo.setNbAlignements(nbAllignement);
							int degreSubclasse=0;
							int degreEquivalent=0;
							String uriLocal;
							String uriDistant;
							Reasoner reasoner = new Reasoner( manager );
							Set <OWLOntology> setOfOntologies = new HashSet <OWLOntology> ();
							setOfOntologies.add(query.getOntologie());
							setOfOntologies.add(self.ontology);
							reasoner.loadOntologies( setOfOntologies );
							
							ArrayList<Mapping> tmpListMapping = new ArrayList<Mapping>();
							for ( OWLClass classe1: query.getOntologie().getReferencedClasses() ) {
								for ( OWLClass classe2: self.ontology.getReferencedClasses() ){
									if ( reasoner.isSubClassOf(classe1,classe2) ) {
										degreSubclasse++;
										uriLocal=findIri(classe1,node.getID());
										uriDistant=findIri(classe2,query);
										SubsomptionMapping sub = new SubsomptionMapping(uriLocal, uriDistant,0);
										tmpListMapping.add(sub);
										protocolQ.getListMapping().add(sub);
										InfoGlobal.nbMapping++;
										xlw.write("<action type='reasoner' class1='"+ classe1.getClass().getName()+"' relation='isSubClassOf' class2='"+ classe2.getClass().getName()+"'>");
									}
									if ( reasoner.isSubClassOf(classe2,classe1) ) {
										degreSubclasse++;
										uriLocal=findIri(classe1,node.getID());
										uriDistant=findIri(classe2,query);
										SubsomptionMapping sub = new SubsomptionMapping(uriLocal, uriDistant,1);
										tmpListMapping.add(sub);
										protocolQ.getListMapping().add(sub);
										InfoGlobal.nbMapping++;
										xlw.write("<action type='reasoner' class1='"+ classe2.getClass().getName()+"' relation='isSubClassOf' class2='"+ classe1.getClass().getName()+"'>");
										
									}
									if ( reasoner.isEquivalentClass(classe1,classe2)){
										degreEquivalent++;
										uriLocal=findIri(classe1,node.getID());
										uriDistant=findIri(classe2,query);
										EquivalentMapping equi = new EquivalentMapping(uriLocal, uriDistant); 
										tmpListMapping.add(equi);
										protocolQ.getListMapping().add(equi);
										InfoGlobal.nbMapping++;
										xlw.write("<action type='reasoner' class1='"+ classe1.getClass().getName()+"' relation='isEquivalent' class2='"+ classe2.getClass().getName()+"'>");
										
									}
								}
							}
							int similarite=ComputeSimilarite.calcule(degreEquivalent, degreSubclasse);

							Result result = new Result(node.getID(),1,query.getPath(),similarite);
							result.getPath().remove(query.getPath().size()-1);
							result.setListMapping(tmpListMapping);
							ArrayList<Result> listeRes = protocolR.getTabResultat();
							if (listeRes == null) listeRes = new ArrayList<Result>();
							listeRes.add(result);
							protocolR.setTabResultat(listeRes);
							
							query.setTraite(true);
						}
					}
					else
					{
						//Query que=(Query)query.clone();
						ArrayList<Node> path=query.getPath();
						//recuperer le noeud prochain et son protocol
						Node prochain=path.get(query.getCompteur());
						QueryFileProtocol protocolQNext=(QueryFileProtocol)prochain.getProtocol(pidQueryFile);
						int cc=query.getCompteur()+1;
						query.setCompteur(cc);
						protocolQNext.getQueryList().add(query);
						protocolQ.getQueryList().remove( protocolQ.getQueryList().indexOf(query) );
					}
					
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		xlw.close();
*/	}
	
	public void sendQueryFlooding(Node node, int protocolID, SemQuery que, XMLLogWriter xlw) {
		boolean inside=false;
		// liste des voisins
		ArrayList <Node> peers = flooding(node, protocolID);
		System.out.print("nombre de voisins de noeud "+node.getID()+" est "+peers.size()+" qui est noeud ");
		//System.out.print("Propagation vers les voisins : ");
		for (int i=0; i<peers.size()-1;i++){
			System.out.print(" "+peers.get(i).getID() + " - ");
		}
		System.out.println(" "+peers.get(peers.size()-1).getID());
		
		// recuperation du protocol QueryFileProtocol associe au noeud courant
		QueryQueueManagementProtocol nprotocol = (QueryQueueManagementProtocol)node.getProtocol(pidQueryFile);
		
		Node peer = null;
		QueryQueueManagementProtocol vprotocol = null;
		
		for (int i = 0; i < peers.size(); i++) {
		
			peer = peers.get(i);
			
			if (!peer.isUp()) { continue; }
			// recuperation du protocol QueryFileProtocol associe au voisin
			vprotocol = (QueryQueueManagementProtocol)peer.getProtocol(pidQueryFile);
			
				SemQuery query = (SemQuery)que.clone();
				
				//verifier si la requete est deja traiter(flooding)
				String queryId=query.getQueryID();
				boolean contains=false;
				
				if(nprotocol.getPendingQueriesCount()!=0)
				{
					
					for(int kk=0;kk<vprotocol.getSentQueriesCount();kk++)
					{
						if(vprotocol.getSentQuery(kk).getQueryID().equals(queryId))
							{
							  contains=true;
							  break;
							}				
					}
					for(int kk=0;kk<vprotocol.getPendingQueriesCount();kk++)
					{
						if(vprotocol.getPendingQuery(kk).getQueryID()==queryId)
						{
							  contains=true;
							  break;
						}
					}
				}
				
				if (!query.endTTL() && !contains && peer.getID()!= query.getNodeDepart() && query.getNoCycle() < CDState.getCycle())
				{
					
					inside=true;
					System.out.print("Propagation vers les voisins : ");
					System.out.println(peer.getID());
					xlw.write("<action type='send' from='"+ node.getID() + "' to='"+ peer.getID() +"' />");
					//nb message transit incremente
					InfoGlobal.nbMessage++;
					// ajout du  noeud dans le chemin de la requete
					query.getPath().add(node);
					query.setNoCycle(CDState.getCycle());
					query.setTraite(false);
					// decrementation du TTL de chaque requete
					query.decreaseTTL();
					vprotocol.addPendingQuery(query);

					nprotocol.addSentQuery(query);
				}
			
		}
		if(inside)
		{
			nprotocol.removePendingQuery(que);
		}
	}
}