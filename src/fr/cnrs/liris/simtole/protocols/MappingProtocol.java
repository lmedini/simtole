package fr.cnrs.liris.simtole.protocols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import peersim.cdsim.CDProtocol;
import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.Node;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import fr.cnrs.liris.simtole.InfoGlobal;
import fr.cnrs.liris.simtole.mapping.AbstractMapping;
import fr.cnrs.liris.simtole.mapping.EquivalentMapping;
import fr.cnrs.liris.simtole.mapping.Mapping;
import fr.cnrs.liris.simtole.mapping.MappingFactory;
import fr.cnrs.liris.simtole.mapping.SubsomptionMapping;
import fr.cnrs.liris.simtole.node.SemanticNode;
import fr.cnrs.liris.simtole.owl.OWLUtils;
import fr.cnrs.liris.simtole.query.SemQuery;
import fr.cnrs.liris.simtole.query.SemQueryInfos;
import fr.cnrs.liris.simtole.result.SemResult;

/**
 * Protocole de retour d'une requête de mapping.
 * Est activé une fois qu'une requête de mapping a été envoyée, qu'elle est arrivée à destination, et que le plus court chemin entre les noeuds source et destination a été déterminé.
 * C'est pendant le retour de la requête qu'est effectuée la recherche de mappings.<br />
 * Le fonctionnement de ce protocole est le suivant :
 * <ul> 
 * <li>Le noeud destination se contente de rajouter son ontologie locale et ses mappings dans les paramètres de la requête et de la transmettre au noeud précédent.</li>
 * <li>Les autres noeuds alignent leur ontologie locale et celle contenue dans la requête et transfèrent au noeud suivant en rajoutant leur ontologie locale et les mappings trouvés dans la reupete.</li>
 * <li>Le noeud source effectue le dernier alignement et affiche les mappings entre les deux noeuds.</li>
 * </ul>
 * @author Lionel Médini
 */
public class MappingProtocol extends AbstractQueryProtocol implements CDProtocol{

	private final int pidQueryQueue;
	private final int pidResult;
	private final int pidInfoLocal;

	public MappingProtocol(String prefix) {
		this.prefix=prefix;
		pidQueryQueue = Configuration.getPid(prefix + "." + PAR_PROT_QUEUE);
		pidResult = Configuration.getPid(prefix + "." + PAR_PROT_RESULT);
		pidInfoLocal = Configuration.getPid(prefix + "." + PAR_PROT_INFO);
	}

	/**
	 * Extraction des mappings initiaux depuis l'ontologie du pair et par rapport à l'ontologie contenue dans la requête.
	 */
	public void nextCycle(Node nod, int protocolID)
	{
		SemanticNode node = (SemanticNode) nod;
		QueryQueueManagementProtocol protocolQ = (QueryQueueManagementProtocol) node.getProtocol(pidQueryQueue);
		SemResultProtocol protocolR = (SemResultProtocol) node.getProtocol(pidResult);

		try{
			for(int i=0; i < protocolQ.getMappingQueriesCount(); i++) //Pour chaque requête de la file
			{
				SemQuery query = (SemQuery) protocolQ.getMappingQuery(i);
				String queryId = query.getQueryID();
				SemResult result = (SemResult) protocolR.getQueryShortestResult(queryId);

				System.out.println("\n*** MappingProtocol ***");
				System.out.println(query.toString());
				System.out.print("Noeud : " + node.getID());

				// Renvoi de la requête au noeud précédent dans le path
				for(int j= result.getPath().size() - 1; j >= 0; j--) {
					if(result.getPath().get(j).equals(node)) {
						OWLOntology nodeOntology = node.getOntology();
						if(j == result.getPath().size() - 1) // Noeud destinataire : on se contente de stocker l'ontologie et de passer au noeud suivant
						{
							System.out.println(" (noeud destination)");
							System.out.println("Ontologie : " + node.getNomFichier() + "");
							Node suivant = result.getPath().get(j-1);
							addOntologyToQuery(query, nodeOntology, (SemanticNode) node, (SemanticNode) suivant);
							passeAuNoeudSuivant(node, suivant, result, query);
						} else if(j>0) // Noeuds intermédiaires : alignement + stockage ontologie + passage au noeud suivant
						{
							System.out.println(" (noeud intermédiaire)");
							System.out.println("Ontologie : " + node.getNomFichier() + "");
							Node suivant = result.getPath().get(j-1);
							traiteMappings(query, nodeOntology, node, (SemanticNode) result.getPath().get(j+1), result);
							addOntologyToQuery(query, nodeOntology, (SemanticNode) node, (SemanticNode) suivant);
							passeAuNoeudSuivant(node, suivant, result, query);
						} else // j=0 ; noeud source : on ne fait que l'alignement
						{
							System.out.println(" (noeud source)");
							System.out.println("Ontologie : " + node.getNomFichier() + "");
							traiteMappings(query, nodeOntology, node, (SemanticNode) result.getPath().get(1), result);

							// Affichage des résultats
							int cibleId = (int) result.getPath().get(result.getPath().size() - 1).getID();
							System.out.println("\n**** Résultats de la requête d'alignement du noeud " + node.getID() + " au noeud " + cibleId + " ****");
							System.out.println("**** Equivalences : ");
							for(Mapping equ: node.getEquivalentMappingsAvec(cibleId)) {
								System.out.println(equ.getAxiom().toString());								
							}
							System.out.println("**** Subsompbions : ");
							for(Mapping sub: node.getSubsomptionMappingsAvec(cibleId)) {
								System.out.println(sub.getAxiom().toString());								
							}
						}
						// Dans tous les cas, on retire la requête de la file
						protocolQ.removeMappingQuery(query);
					}
				}
				System.out.println("*** Fin MappingProtocol ***");
			}
		} catch(Exception e){
			System.err.print("Erreur pendant traitement retour - noeud " + node.getID() + " ; cycle " + CDState.getCycle() + " : ");
			e.printStackTrace();
		}
	}

	private void addOntologyToQuery(SemQuery query, OWLOntology courantOntology, SemanticNode courant, SemanticNode suivant) {
		OWLOntology queryOntology = query.getInfosTraitement().getOntologyTemp();

		// Initialisation de l'OntologyManager
		OWLOntologyManager manager;
		if(queryOntology == null) {
			manager = OWLManager.createOWLOntologyManager();
			try {
				queryOntology = manager.createOntology();
				query.getInfosTraitement().setOntologyTemp(queryOntology);
			} catch (OWLOntologyCreationException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			manager = queryOntology.getOWLOntologyManager();
		}

		// Ajout de l'ontologie du noeud, axiome par axiome
		Set<OWLAxiom> nodeOntologyAxioms = courantOntology.getAxioms();
		for(OWLAxiom axiom: nodeOntologyAxioms) {
			/**
			 * A VERIFIER : que la méthode containsAxiom() détecte les axiomes identiques...
			 */
			if(!queryOntology.containsAxiom(axiom))
				manager.addAxiom(queryOntology, axiom);
		}
		//System.out.println("Nb d'axiomes dans l'ontologie avant ajout mappings : " + queryOntology.getAxiomCount());

		// Ajout des mappings de subsomption connus par le noeud avec l'ontologie du suivant
		ArrayList<SubsomptionMapping> nodeSubMappings = courant.getSubsomptionMappingsAvec((int) suivant.getID());
		for(AbstractMapping mapping: nodeSubMappings) {
			OWLAxiom axiom = mapping.getAxiom();
			if(!queryOntology.containsAxiom(axiom))
				manager.addAxiom(queryOntology, axiom);
		}
		// Ajout des mappings de subsomption connus par le noeud avec l'ontologie du suivant
		ArrayList<EquivalentMapping> nodeEquMappings = courant.getEquivalentMappingsAvec((int) suivant.getID());
		for(AbstractMapping mapping: nodeEquMappings) {
			OWLAxiom axiom = mapping.getAxiom();
			if(!queryOntology.containsAxiom(axiom))
				manager.addAxiom(queryOntology, axiom);
		}
		//System.out.println("Nb d'axiomes dans l'ontologie après ajout mappings : " + queryOntology.getAxiomCount());
	}

	private void passeAuNoeudSuivant(Node courant, Node suivant, SemResult result, SemQuery query) {
		// On rajoute la query à traiter dans la file du noeud suivant
		QueryQueueManagementProtocol suivantProtocolQ = (QueryQueueManagementProtocol) suivant.getProtocol(pidQueryQueue);
		suivantProtocolQ.addMappingQuery(query);
		// On rajoute le résultat au protocole résultat du noeud suivant
		SemResultProtocol suivantProtocolR = (SemResultProtocol) suivant.getProtocol(pidResult);
		suivantProtocolR.setQueryShortestResult(query.getQueryID(), (SemResult) result.clone());

		QueryQueueManagementProtocol courantProtocolQ = (QueryQueueManagementProtocol) courant.getProtocol(pidQueryQueue);
		courantProtocolQ.removeMappingQuery(query);

		System.out.println("Requête mapping transférée au noeud : " + suivant.getID() + " avec " + query.getInfosTraitement().getOntologyTemp().getAxiomCount() + " axiomes.");
	}

	/**
	 * Réalise le traitement des requêtes. Ici, traitement sémantique qui consiste à :
	 * <ol>
	 * <li>décider s'il faut aligner les ontologies</li>
	 * <li>lancer l'alignement (à l'aide de OWLUtil.detectMappings)</li>
	 * <li>récupérer les résultats et les stocker dans les tableaux appropriés</li>
	 * </ol>
	 * @param localNode Noeud courant
	 * @param protocolID Identifiant du QueryProtocol courant
	 * @param protocolQ QueryFileProtocol du noeud courant
	 */
	private void traiteMappings(SemQuery query, OWLOntology ontology, SemanticNode localNode, SemanticNode nextNode, SemResult result) {
		SemQueryInfos queryInfos = query.getInfosTraitement();
		// Récupération des protocoles associés au noeud courant
		InfoLocalProtocol protocolInfo =(InfoLocalProtocol) localNode.getProtocol(pidInfoLocal);

		//Récupération des données du noeud
		OWLOntologyManager manager = localNode.getManager();
		OWLReasoner reasoner = localNode.getReasoner();
		OWLDataFactory factory = manager.getOWLDataFactory();

		System.out.println("* Recherche mappings entre noeud " + localNode.getID() + " (" + localNode.getNomFichier() + ") et noeud " + nextNode.getID() + " (" + nextNode.getNomFichier() + ") : ");

		int degreSubclasse=0;
		int degreEquivalent=0;

		if(queryInfos.getOntologyTemp() != null) {
			if(!query.getTraite()) {
				// Récupération de l'ensemble des mappings entre les 2 ontologies
				ArrayList<HashMap<OWLClass, ArrayList<OWLClass>>> listesMappings = OWLUtils.detectMappings(manager, reasoner, ontology, queryInfos.getOntologyTemp(), true);
				//OWLUtils.afficheMappings(listesMappings);

				// Stockage des nouveaux mappings
				ArrayList<Mapping> tmpListMapping = new ArrayList<Mapping> ();

				//Traitement des sous-classes
				Set<OWLClass> sourceClasses = listesMappings.get(0).keySet();
				for(OWLClass sourceClasse: sourceClasses) {
					for(OWLClass sousClasse: listesMappings.get(0).get(sourceClasse)) {
						degreSubclasse++;
						OWLSubClassOfAxiom subAxiom = new OWLSubClassOfAxiomImpl(factory, sousClasse, sourceClasse, new ArrayList<OWLAnnotation>());
						SubsomptionMapping sub = (SubsomptionMapping) MappingFactory.createMapping(localNode, subAxiom);
						if(sub != null) {
							tmpListMapping.add(sub);
							SemanticNode otherNode = OWLUtils.getNodeFromOntologyName(OWLUtils.getOntologyNameFromClass(sousClasse));
							if(otherNode != null) {
								System.out.println("Ajout mapping sous-classe : SubClassOfAxiom = " + subAxiom.toString());
								localNode.addSubsomptionMappingsAvec((int) otherNode.getID(), sub);
								/**
							 	 * A FAIRE : transformer ça en un autre type de requête
							 	 */
								otherNode.addSubsomptionMappingsAvec((int) localNode.getID(), sub);
								protocolInfo.addMapping(sub);
								InfoGlobal.nbMapping++;
							}
						}
					}
				}

				//Traitement des équivalences
				sourceClasses = listesMappings.get(1).keySet();
				for(OWLClass sourceClasse: sourceClasses) {
					for(OWLClass equClasse: listesMappings.get(1).get(sourceClasse)) {
						degreEquivalent++;
						Set<OWLClass> equClasses= new HashSet<OWLClass>();
						equClasses.add(sourceClasse);
						equClasses.add(equClasse);
						OWLEquivalentClassesAxiom equAxiom = new OWLEquivalentClassesAxiomImpl(factory, equClasses, new ArrayList<OWLAnnotation>());
						EquivalentMapping equ = (EquivalentMapping) MappingFactory.createMapping(localNode, equAxiom);

						System.out.println("Ajout mapping équivalence : EquivalentClassesAxiom = " + equAxiom.toString());
						if(equ != null) {
							tmpListMapping.add(equ);
							SemanticNode otherNode = OWLUtils.getNodeFromOntologyName(OWLUtils.getOntologyNameFromClass(equClasse));
							localNode.addEquivalentMappingsAvec((int) otherNode.getID(), equ);
							/**
							 * A FAIRE : transformer ça en un autre type de requête
							 */
							otherNode.addEquivalentMappingsAvec((int) localNode.getID(), equ);
							protocolInfo.addMapping(equ);
							InfoGlobal.nbMapping++;
						}
					}
				}

				//Traitement des super-classes
				sourceClasses = listesMappings.get(2).keySet();
				for(OWLClass sourceClasse: sourceClasses) {
					for(OWLClass superClasse: listesMappings.get(2).get(sourceClasse)) {
						degreSubclasse++;
						OWLSubClassOfAxiom supAxiom = new OWLSubClassOfAxiomImpl(factory, sourceClasse, superClasse, new ArrayList<OWLAnnotation>());
						SubsomptionMapping sup = (SubsomptionMapping) MappingFactory.createMapping(localNode, supAxiom);
						System.out.println("Ajout mapping super-classe : SubClassOfAxiom = " + supAxiom.toString());
						if(sup != null) {
							tmpListMapping.add(sup);
							SemanticNode otherNode = OWLUtils.getNodeFromOntologyName(OWLUtils.getOntologyNameFromClass(superClasse));
							localNode.addSubsomptionMappingsAvec((int) otherNode.getID(), sup);
							/**
							 * A FAIRE : transformer ça en un autre type de requête
							 */
							otherNode.addSubsomptionMappingsAvec((int) localNode.getID(), sup);
							protocolInfo.addMapping(sup);
							InfoGlobal.nbMapping++;
						}
					}
				}

				result.getResultInfos().setListMapping(tmpListMapping);
				//						listeRes.add(result);
				//						this.setTabResultat(listeRes);

				// Mise à jour du nombre d'alignements effectués
				int nbAlignements=protocolInfo.getNbAlignements();
				nbAlignements++;
				protocolInfo.setNbAlignements(nbAlignements);
			}
		}
		System.out.println("subsomptions : " + degreSubclasse + " ; équivalences : " + degreEquivalent);
	}

	@Override
	public Object clone()
	{
		MappingProtocol tmp = (MappingProtocol) super.clone();
		tmp.prefix = this.prefix;
		return tmp;
	}
}