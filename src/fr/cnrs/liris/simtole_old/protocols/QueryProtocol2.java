package fr.cnrs.liris.simtole_old.protocols;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import fr.cnrs.liris.simtole.InfoGlobal;
import fr.cnrs.liris.simtole.protocols.InfoLocalProtocol;
import fr.cnrs.liris.simtole.protocols.QueryQueueManagementProtocol;
import fr.cnrs.liris.simtole.protocols.SemResultProtocol;
import fr.cnrs.liris.simtole.query.SemQuery;
/*
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLSubClassAxiom;
*/
import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;

public class QueryProtocol2 extends QueryProtocolTemplate_old{

	// identifiant du protocol QueryFileProtocol
	private final int pidQueryFile;
	private final int pidResult;
	private final int pidInfoLocal;

	private static final String PAR_PROT_QUERY = "query_protocol";
	private static final String PAR_PROT_RESULT = "result_protocol";
	private static final String PAR_PROT_INFO = "info_protocol";

	public QueryProtocol2(String prefix) {
		super(prefix);

		pidQueryFile = Configuration.getPid(prefix + "." + PAR_PROT_QUERY);
		pidResult = Configuration.getPid(prefix + "." + PAR_PROT_RESULT);
		pidInfoLocal = Configuration.getPid(prefix + "." + PAR_PROT_INFO);
	}

	public void nextCycle(Node node, int protocolID) {
		// Récupération du QueryFileProtocol associé au noeud courant
		QueryQueueManagementProtocol protocolQ = (QueryQueueManagementProtocol) node.getProtocol(pidQueryFile);
		QueryProtocol2 self = (QueryProtocol2)node.getProtocol(protocolID);
		SemResultProtocol protocolR=(SemResultProtocol)node.getProtocol(pidResult);
		InfoLocalProtocol protocolInfo =(InfoLocalProtocol) node.getProtocol(pidInfoLocal); 

		System.out.println("\n-------------------------------------------------------------------");
		System.out.println("--------------------------- Cycle " + CDState.getCycle() + " du noeud "+ node.getID() +" ---------------------");
		System.out.println("-------------------------------------------------------------------\n");

		System.out.println("Noeud : " + node.getID() + "");
		System.out.println("Cycle : " + CDState.getCycle() + "");
		System.out.println("id Protocole : " + pidQueryFile + "");
		System.out.println("Protocole query : " + protocolQ.hashCode()+ "");
		System.out.println("Queries : " + (protocolQ.getPendingQueriesCount() != 0) + "\n");

		// Extraire les mappings initiaux

		System.out.println("\n*** Ajout des mappings initiaux : ***");
/*
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
						System.out.println("Mapping : " + oa.toString());

						Set<OWLEntity> oe=oa.getReferencedEntities();
						int compt = 0;
						for(OWLEntity oc: oe)
						{
							//URI s=oc.getURI();
							//System.out.println(s.toString());
							if(compt==0)
							{
								switch ( (int)node.getID()){
									case 0 : uriLocal= "network:/0#"+oc.toString(); break; 
									case 1 : uriLocal= "network:/1#"+oc.toString(); break; 
									case 2 : uriLocal= "network:/2#"+oc.toString(); break; 
									case 4 : uriLocal= "network:/4#"+oc.toString(); break; 
									default : uriLocal= "network:/"+node.getID()+"#"+oc.toString();
								}
							}
							else {
								switch ( (int)node.getID()){
									case 0 : uriDistant= "network:/2#"+oc.toString(); break; 
									case 1 : uriDistant= "network:/4#"+oc.toString(); break; 
									case 2 : uriDistant= "network:/1#"+oc.toString(); break; 
									case 4 : uriDistant= "network:/6#"+oc.toString(); break; 
									default : uriDistant="network:/"+random.nextInt(Network.size())+"#"+oc.toString();}
								}
							compt++;
						}
						SubsomptionMapping sub = new SubsomptionMapping(uriLocal, uriDistant,0);
						protocolQ.getListMapping().add(sub);
						InfoGlobal.nbMapping++;
						InfoGlobal.nbMappingInit++;
					}
					//System.out.println();
				}
			}			
		}
		if(node.getID()==8){
			System.out.print("");
		}
		System.out.println("*** Fin ajout des mappings initiaux ***");

		//partie query router

		System.out.println("\n*** Routage : ***");

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
							sendQueryFlooding(node,protocolID,queryRout);
						}
					}
				}
			}
			catch(Exception e){}
		}
		System.out.println("*** Fin routage ***");

		//partie query

		System.out.println("\n*** Traitement des requêtes : ***");

		if (protocolQ.getQueryList().isEmpty()) {
			if(tableauMetrique!=null) {
				System.out.println("\nTableau métrique :");
			    Iterator<Integer> iterator = tableauMetrique.keySet().iterator();
		        while(iterator.hasNext()) {
		            int s = (Integer)iterator.next();
		            int temp = tableauMetrique.get(s);
		            System.out.println("(" + s + " ; " + temp + ")");
		        }
				System.out.println("Fin tableau métrique\n");
			}
			return;
		}
		else {
			try{
				for(Query query:protocolQ.getQueryList()) {
					System.out.println("Requête numéro : " + query.getQueryID());
					System.out.println("Noeud source : " + query.getNodeDepart());
					System.out.println("Noeud destination : " + query.getNodeDestinatair());
					System.out.println("Ontologie : " + query.getOntologie());
					
					//test si on est au node de depart
					if(node.getID()==query.getNodeDepart())
					{
						if(query.getType()==1) {
							query.setOntologie(self.ontology);
						}
					}

					if(node.getID()==query.getNodeDestinatair()) {
						if(!query.getTraite()) {

							int nbAlignements=protocolInfo.getNbAlignements();
							nbAlignements++;
							protocolInfo.setNbAlignements(nbAlignements);
							int degreSubclasse=0;
							int degreEquivalent=0;
							String uriLocal;
							String uriDistant;
							Set <OWLOntology> setOfOntologies = new HashSet <OWLOntology> ();
							setOfOntologies.add(query.getOntologie());
							setOfOntologies.add(self.ontology);
							reasoner.loadOntologies( setOfOntologies );
							//Test de consistance light...
							if (reasoner.isConsistent(query.getOntologie()))
								System.out.println("Ontologie consistante");
							else
								System.out.println("Ontologie inconsistante");

							// Test des axiomes dans la query
							Set<OWLAxiom> ax=query.getOntologie().getAxioms();
							for(OWLAxiom oa : ax) {
								if(oa instanceof OWLSubClassAxiom) {
									Set<OWLEntity> oe=oa.getReferencedEntities();
									System.out.println("Axiome : " + oa.toString());
									
								}
							}
*/							// Fin de test axiomes

/*							//Test inconsistences (rajout Lionel 20 mai 2011)
							System.out.println("\n**** DETECTION DES INCONSISTENCES ****");
							System.out.println("* Début du test : ");
							Set<OWLClass> problems = reasoner.getInconsistentClasses();
							System.out.println("* Fin d'initialisation de la liste des classes inconsistantes");
							if (!problems.isEmpty()) {
								System.out.println("* " + problems.size() + " inconsistences détectées");
								for (Iterator<OWLClass> i = reasoner.getInconsistentClasses().iterator(); i.hasNext();) {
									OWLClass iClass = (OWLClass) (i.next());
									System.out.println("* INCONSISTENCE : " + iClass.toString());
								}
							}
							System.out.println("**** FIN DE DETECTION DES INCONSISTENCES ****\n");
*/
/*							ArrayList<Mapping> tmpListMapping = new ArrayList<Mapping>();
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
									}
									if ( reasoner.isSubClassOf(classe2,classe1) ) {
										degreSubclasse++;
										uriLocal=findIri(classe1,node.getID());
										uriDistant=findIri(classe2,query);
										SubsomptionMapping sub = new SubsomptionMapping(uriLocal, uriDistant,1);
										tmpListMapping.add(sub);
										protocolQ.getListMapping().add(sub);
										InfoGlobal.nbMapping++;
									}
									if ( reasoner.isEquivalentClass(classe1,classe2)){
										degreEquivalent++;
										uriLocal=findIri(classe1,node.getID());
										uriDistant=findIri(classe2,query);
										EquivalentMapping equi = new EquivalentMapping(uriLocal, uriDistant); 
										tmpListMapping.add(equi);
										protocolQ.getListMapping().add(equi);
										InfoGlobal.nbMapping++;
									}
								}
							}
							int similarite=ComputeSimilarite.calcule(degreEquivalent, degreSubclasse);
							
							System.out.println("degreEquivalent: "+degreEquivalent);
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
					//collecter informations intermediares
					{
						if(node.getID()!=query.getNodeDepart())
						{
						//rajouter axioms locaux dans l'ontologie qui est dans une requete
						OWLDataFactory factory = manager.getOWLDataFactory();
						OWLClass clsA;
						OWLClass clsB;
						if(protocolQ.getListMapping()!=null)
						{
							Iterator<Mapping> it = protocolQ.getListMapping().iterator();
							
							while(it.hasNext())
							{
								Mapping mapping=(Mapping)it.next();
								if(mapping.getType().equals("equivalent"))
								{
									//EquivalentMapping equi=(EquivalentMapping)mapping;
									//System.out.println("my class "+equi.uriMyClass+" mapping type "+equi.getType());
								}
								if(mapping.getType().equals("subsomption"))
								{
									SubsomptionMapping sub=(SubsomptionMapping)mapping;
									
									String[] tempA=sub.getUriMyClass().split("#");
									String tempAA=tempA[tempA.length-2];
									char atmp=tempAA.charAt(tempAA.length()-1);
									String aa=""+atmp;
									int a=Integer.parseInt(aa);
									String[] tempB=sub.getUriDistantClass().split("#");
									String tempBB=tempB[tempB.length-2];
									char btmp=tempBB.charAt(tempBB.length()-1);
									String bb=""+btmp;
									int b=Integer.parseInt(bb);
									//String tempAA=tempA[tempA.length-1];
									//String tempBB=tempB[tempB.length-1];
									
									
									if( (a==(query.getPath().get(query.getCompteur()-2).getID())&& b==(node.getID()))
										 ||(b==(int)query.getPath().get(query.getCompteur()-2).getID()&& a==node.getID()) )
										{
											if(sub.getRelation()==0)
											{
												clsA = factory.getOWLClass(URI.create(sub.getUriMyClass()));
												clsB = factory.getOWLClass(URI.create(sub.getUriDistantClass()));											
											}
											else
											{
												clsB = factory.getOWLClass(URI.create(sub.getUriDistantClass()));
												clsA = factory.getOWLClass(URI.create(sub.getUriMyClass()));		
											}
											
											OWLAxiom axiom = factory.getOWLSubClassAxiom(clsA, clsB);
											AddAxiom addAxiom = new AddAxiom(query.getOntologie(), axiom);
											this.manager.applyChange(addAxiom);
										}
									}
								
								}
							
							}
						}
						
						//this.manager.saveOntology(query.getOntologie());
						
						//on commence propager la requete
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
				
			}
		}
*/		System.out.println("*** Fin de traitement des requêtes ***");
	}
	
	public void sendQueryFlooding(Node node, int protocolID, SemQuery que) {
		boolean inside=false;
		// liste des voisins
		ArrayList <Node> peers = flooding(node, protocolID);
		
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
				
				//Vérifier si la requête est déjà traitée(flooding)
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
					InfoGlobal.nbMessage++;
					inside=true;
					System.out.print("Node "+ node.getID() +  " propage vers les voisins : ");
					System.out.println(peer.getID());
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