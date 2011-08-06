package fr.cnrs.liris.simtole.owl;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import peersim.core.Network;

import aterm.ATermAppl;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import fr.cnrs.liris.simtole.node.SemanticNode;
import fr.cnrs.liris.simtole.query.SemQueryInfos;

/**
 * Classe qui teste différentes ontologies. Renvoie le nombre de classes, de propriétés et d'instances, et teste la consistance et l'existence d'erreurs.
 * <br />Remarques :
 * <ul>
 * <li>La consistance et la détection d'erreurs sont effectuées sur les ontologies privées de leurs instances.</li>
 * <li>Pour plus de détails sur les contenus des ontologies, décommenter les <code>println</code> à l'intérieur des boucles.
 * <br />Les méthodes qui recréent les ontologies snas les instances sont séparées du reste du code, et rendues publiques et statiques pour pouvoir être réutilisées par ailleurs.
 * @author code original (commentaires) de Christian Halaschek-Wiener (FAQ Pellet), adaptation Lionel Médini
 */

public class OWLUtils {

	/**
	 * Fonction principale. Mettre les noms des ontologies et des fichiers les contenants dans le tableau <code>files</code> pour les tester.
	 * @param args Inopérant
	 * @throws Exception Je l'ai trouvé comme ça dans le code d'origine, alors je l'ai laissé là...
	 */
	public static void main(String[] args) throws Exception {

		String[] files = {
				"network:/0", "file:D:/Projets/Eclipse/simtole/Ontologies/cmt.owl", 
				"network:/1", "file:D:/Projets/Eclipse/simtole/Ontologies/Cocus.owl", 
				"network:/2", "file:D:/Projets/Eclipse/simtole/Ontologies/Conference.owl", 
				"network:/3", "file:D:/Projets/Eclipse/simtole/Ontologies/confious.owl", 
				"network:/4", "file:D:/Projets/Eclipse/simtole/Ontologies/confOf.owl", 
				"network:/5", "file:D:/Projets/Eclipse/simtole/Ontologies/crs_dr.owl", 
				"network:/6", "file:D:/Projets/Eclipse/simtole/Ontologies/edas.owl", 
				"network:/7", "file:D:/Projets/Eclipse/simtole/Ontologies/ekaw.owl", 
				"network:/8", "file:D:/Projets/Eclipse/simtole/Ontologies/iasted.owl", 
				"network:/9", "file:D:/Projets/Eclipse/simtole/Ontologies/linklings.owl", 
				"network:/10", "file:D:/Projets/Eclipse/simtole/Ontologies/MICRO.owl", 
				"network:/11", "file:D:/Projets/Eclipse/simtole/Ontologies/MyReview.owl", 
				"network:/12", "file:D:/Projets/Eclipse/simtole/Ontologies/OpenConf.owl", 
				"network:/13", "file:D:/Projets/Eclipse/simtole/Ontologies/paperdyne.owl", 
				"network:/14", "file:D:/Projets/Eclipse/simtole/Ontologies/PCS.owl", 
				"network:/15", "file:D:/Projets/Eclipse/simtole/Ontologies/sigkdd.owl"};

		PelletOptions.USE_INCREMENTAL_CONSISTENCY = true;
		PelletOptions.USE_COMPLETION_QUEUE = true;

		PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();

		for(int i = 0; i < files.length; i += 2) {
			try {
				String name = files[ i ];
				String file = files[ i+1 ];

				System.out.println( "\nStarting test: "  + file );

				List<String> list = new ArrayList<String>();
				list.add(name);

				//OWLOntology ontology = getOntologySansInstance(IRI.create(file));
				OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
				OWLOntology ontology = manager.loadOntology(IRI.create(file));

				PelletReasoner reasoner = reasonerFactory.createReasoner(ontology);

				// Analyse en fonction de la base de connaissances

				KnowledgeBase kb = reasoner.getKB();

				System.out.println("Ontologie : " + name);
				System.out.println("Expressivité : " + kb.getExpressivity());

				Set<ATermAppl> classes = kb.getClasses();
				System.out.println("Nombre de classes : " + ontology.getClassesInSignature().size() + " ; " + classes.size());
				for(ATermAppl classe: classes) {
					kb.addClass(classe);
					//System.out.println("Classe " + classe.getName() + " ajoutée");
				}

				Set<ATermAppl> proprietes = kb.getProperties();
				System.out.println("Nombre de propriétés : " + proprietes.size());
				for(ATermAppl propriete: proprietes) {
					kb.addProperty(propriete);
					//System.out.println("Propriete " + propriete.getName() + " ajoutée");
				}

				Set<ATermAppl> instances = kb.getIndividuals();
				System.out.println("Nombre d'instances : " + instances.size());
				for(ATermAppl instance: instances) {
					//System.out.println("Instance " + instance.getName() + " non ajoutée");
				}

				kb.timers.getTimer("consistency").start();
				System.out.print("Consistante : " + (reasoner.isConsistent()?"OUI":"NON"));
				kb.timers.getTimer("consistency").stop();
				System.out.println(" (" + kb.timers.getTimer("consistency").getTotal() + " ms)");

				Node<OWLClass> erreurs = reasoner.getUnsatisfiableClasses();
				System.out.println("Erreur(s) : " + erreurs.getSize() + " classe(s) insatisfiable(s)");
				for(OWLClass classe:erreurs) {
					System.out.print("* " + classe.getNNF());
				}
				System.out.println();
				
				Set<OWLAxiom> axiomes = ontology.getAxioms();
				for(OWLAxiom axiome: axiomes) {
					boolean mapping = isMapping(axiome);
					//System.out.println("Résultat : " + mapping);
				}

				/*
					//do not use inc. consistency test at first
					PelletOptions.USE_INCREMENTAL_CONSISTENCY = false;
					PelletOptions.USE_COMPLETION_QUEUE = true;

					reasoner.isConsistent();
					t.stop();
					long iCons = reasoner.getKB().timers.getTimer("consistency").getTotal();
					reasoner.getKB().timers.getTimer("consistency").reset();


					//classify and realize
					t.start();
					reasoner.realise();
					t.stop();

					System.out.println("  Loading incremental reasoner");
					//do the same for inc reasoner
					reader = new PelletLoader();
					Model incModel = reader.read( file );                                   
					OWLReasoner incReasoner = new OWLReasoner();
					incReasoner.load( incModel );
					//do not use inc. consistency test at first
					PelletOptions.USE_INCREMENTAL_CONSISTENCY = false;
					PelletOptions.USE_COMPLETION_QUEUE = true;


					incReasoner.isConsistent();
					incReasoner.getKB().timers.getTimer("consistency").reset();
					incReasoner.realize();


					//get average time for normal consistency checking for a sigle update
					double ITERS = 100.0;
					boolean err = false;
					long e;
					long s;
					long total = 0;
					long largest = 0;                                       
					long incTotal = 0;
					long incLargest = 0;

					boolean isIncCon = true;
					boolean isCon = true;
					ATermAppl ind1 = null;
					ATermAppl ind2 = null;


					//get a random class
					ATermAppl clazz  = null;                
					ATermAppl prop = null;


					System.out.println("  Starting test...");

					for(int j = 0; j < ITERS; j++){                                         


						Random rand = new Random(System.currentTimeMillis());
						boolean type = rand.nextBoolean();

						//randomly select type assertion of property assertion
						if(false){
							clazz = TestUtils.selectRandomConcept(reasoner.getKB());
							//randomly negate the concept
							boolean neg = rand.nextBoolean();                                                       
							if(neg)
								clazz = ATermUtils.makeNot(clazz);

							//if there are more than 10 individuals then add a type to one of the existing inds
							if(reasoner.getKB().getIndividuals().size() > 10){
								ind1 = TestUtils.selectRandomIndividual(reasoner.getKB());
							}else{
								//create a new individuals
								ind1 = term( "http://www.example#ind" + j );
								reasoner.getKB().addIndividual( ind1 );
								incReasoner.getKB().addIndividual( ind1 );                                                              
							}

							//add the ind to the reg reasoner and do consistency check
							reasoner.getKB().timers.getTimer("consistency").reset();
							reasoner.getKB().addType( ind1, clazz);                                 
							PelletOptions.USE_INCREMENTAL_CONSISTENCY = false;      
							PelletOptions.USE_COMPLETION_QUEUE = true;
							isCon = reasoner.isConsistent();
							long cons = reasoner.getKB().timers.getTimer("consistency").getTotal();
							if(cons > largest)
								largest = cons;
							total += cons;

							//                                                      System.out.print("Reg: " + cons);



							//add the ind to the inc reasoner and do consistency check
							incReasoner.getKB().timers.getTimer("consistency").reset();
							PelletOptions.USE_INCREMENTAL_CONSISTENCY = true;               
							PelletOptions.USE_COMPLETION_QUEUE = true;
							incReasoner.getKB().addType( ind1, clazz);                                              
							isIncCon = incReasoner.isConsistent();
							long incCons = incReasoner.getKB().timers.getTimer("consistency").getTotal();
							if(incCons > incLargest)
								incLargest = incCons;
							incTotal += incCons;

							//                                                      System.out.println("  Inc: " + incCons);


							if(isCon != isIncCon){
								System.err.println("Normal consistency check: " + isCon + "   Inc. consistency check: " + isIncCon);
								System.err.println(" Adding type " + clazz + " to node: " + ind1);
								System.err.println("   Ind existing types " + incReasoner.getKB().getABox().getIndividual(ind1).getTypes());
								break;
							}



						}else{


							prop = TestUtils.selectRandomObjectProperty(reasoner.getKB());

							//if there are more than 10 individuals then add a type to one of the existing inds
							if(reasoner.getKB().getIndividuals().size() > 10){
								ind1 = TestUtils.selectRandomIndividual(reasoner.getKB());
								ind2 = TestUtils.selectRandomIndividual(reasoner.getKB());
							}else{
								//create a new individuals
								ind1 = term( "http://www.example#ind" + j );
								ind2 = term( "http://www.example#indb" + j );
								reasoner.getKB().addIndividual( ind1 );
								incReasoner.getKB().addIndividual( ind1 );
								reasoner.getKB().addIndividual( ind2 );
								incReasoner.getKB().addIndividual( ind2 );              
							}

							//add the ind to the reg reasoner and do consistency check
							PelletOptions.USE_INCREMENTAL_CONSISTENCY = false;
							PelletOptions.USE_COMPLETION_QUEUE = true;
							reasoner.getKB().timers.getTimer("consistency").reset();
							reasoner.getKB().addPropertyValue( prop, ind1, ind2);                                                                                                                                                           
							isCon = reasoner.isConsistent();
							long cons = reasoner.getKB().timers.getTimer("consistency").getTotal();
							if(cons > largest)
								largest = cons;
							total += cons;

							//                                                      System.out.print("RegR: " + cons);



							//add the ind to the inc reasoner and do consistency check
							incReasoner.getKB().timers.getTimer("consistency").reset();
							PelletOptions.USE_INCREMENTAL_CONSISTENCY = true;
							PelletOptions.USE_COMPLETION_QUEUE = true;
							incReasoner.getKB().addPropertyValue( prop, ind1, ind2);                                                
							isIncCon = incReasoner.isConsistent();
							long incCons = incReasoner.getKB().timers.getTimer("consistency").getTotal();
							if(incCons > incLargest)
								incLargest = incCons;
							incTotal += incCons;

							//                                                      System.out.println("  IncR: " + incCons);


							if(isCon != isIncCon){
								System.err.println("Normal consistency check: " + isCon + "   Inc. consistency check: " + isIncCon);
								System.err.println(" Adding role " + prop + " to nodes: " + ind1 + ", " + ind2);
								System.err.println("   Subject existing types " + incReasoner.getKB().getABox().getIndividual(ind1).getTypes());
								System.err.println("   Object existing types " + incReasoner.getKB().getABox().getIndividual(ind2).getTypes());


								break;
							}




						}

						System.out.print(".");
					}

					double avg = total/ITERS;
					double incAvg = incTotal/ITERS;




					// do not measure species validation time
					list.add(reasoner.getSpecies().toString());
					list.add(reasoner.getKB().getExpressivity());
					list.add(new Long(model.size()));

					KnowledgeBase kb = reasoner.getKB();
					list.add(new Long(kb.getClasses().size()));
					list.add(new Long(kb.getProperties().size()));
					list.add(new Long(kb.getIndividuals().size()));

					// measure classification and realization time
					t.start();
					//                                      reasoner.realize();
					t.stop();

					Timers timers = reasoner.getKB().timers;
					list.add(new Double((timers.getTimer("Loading").getTotal()+timers.getTimer("preprocessing").getTotal())/1000.0));
					list.add(new Double(timers.getTimer("classify").getTotal()/1000.0));
					list.add(new Double(timers.getTimer("realize").getTotal()/1000.0));
					list.add(new Double(iCons/1000.0));
					list.add(new Double(avg/1000.0));
					list.add(new Double(incAvg/1000.0));
					list.add(new Double(t.getTotal()/1000.0));

					//                              System.err.println( list );

					table.add(list);

					//                              kb.timers.print();
				 */				} catch (Throwable e) {
					 e.printStackTrace();
					 //                                      System.exit(0);
				 }
		}

		System.out.println();
		//table.print(System.out, formatHTML);
	}

	public static ATermAppl term(String s) {
		return ATermUtils.makeTermAppl(s);
	}

	/**
	 * Lecture de l'ontologie et création d'une ontologie sans instance.
	 * @param iri String contenant le nom complet du fichier permettant d'accéder à l'ontologie
	 * @return Une nouvelle instance de OWLOntologie contenant les classes et les propriétés mais pas les instances
	 * @throws OWLOntologyCreationException
	 */
	public static OWLOntology getOntologySansInstance(IRI iri) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(iri);
		return getOntologySansInstance(ontology);
	}

	/**
	 * Création d'une ontologie sans instance.
	 * @param ontology Ontologie contenant éventuellement des instances
	 * @return Une nouvelle instance de OWLOntologie contenant les classes et les propriétés mais pas les instances
	 * @throws OWLOntologyCreationException
	 */
	public static OWLOntology getOntologySansInstance(OWLOntology ontology) throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology2 = manager.createOntology();

		Set<OWLAxiom> axiomes = ontology.getAxioms();
		int instanceCount = 0;
		for(OWLAxiom axtmp:axiomes) {
			//System.out.println("Axiome\n nom : " + axtmp.toString() + "\n type : " + axtmp.getAxiomType().toString());
			if(axtmp.getAxiomType() == AxiomType.CLASS_ASSERTION)
				instanceCount++;
			else {
				manager.addAxiom(ontology2, axtmp);
			}
		}
//		System.out.println(axiomes.size() + " axiomes trouvés dont " + instanceCount + " instances.");

		return ontology2;
	}

	/**
	 * Méthode qui teste si un axiome est un mapping (i.e. s'il associe des classes d'ontologies différentes).
	 * Pour l'instant, seuls deux types de mappings sont pris en compte : subsomption et équivalence.
	 * @param axiome axiome OWL contenant les classes à tester
	 * @return <code>true</code> si l'axiome est de type OWLSubClassOfAxiom ou OWLEquivalentClassesAxiom ET si deux classes contenues dans sa signature sont issues d'ontologies différentes ; <code>false</code> sinon.
	 */
	public static boolean isMapping(OWLAxiom axiome) {
		String ontologyCurrentName = null;
		if(axiome instanceof OWLSubClassOfAxiom || axiome instanceof OWLEquivalentClassesAxiom) {
			Set<OWLClass> classes = axiome.getClassesInSignature();
			//System.out.print("isMapping de l'axiome : " + axiome.getClass().getSimpleName() + "; classes : " + classes);
			for(OWLClass classe: classes) {
				if(ontologyCurrentName == null)
					ontologyCurrentName = getOntologyNameFromClass(classe);
				else {
					String tmp = getOntologyNameFromClass(classe);
					if(!ontologyCurrentName.equals(tmp))
						//System.out.println("\nVRAI car 2 ontologies différentes trouvées (" + ontologyCurrentName + " et " + tmp);
					return true;
				}
			}
			//System.out.println("\nFAUX uniquement des classes de " + ontologyCurrentName);
		}
		return false;
	}

	/**
	 * Méthode qui renvoie les ontologies de référence d'un mapping (à utiliser après <code>isMapping()</code>).
	 * Pour l'instant, seuls deux types de mappings sont pris en compte : subsomption et équivalence.
	 * @param axiome axiome OWL contenant les classes à tester
	 * @return un tableau de deux OWLOntology contenant des ontologies différentes si l'axiome est un mapping de type connu ou <code>null</code> sinon.
	 */
	public static String[] getOntologiesFromMapping(OWLAxiom axiome) {
		String ontologyCurrentName = null;
		if(axiome instanceof OWLSubClassOfAxiom || axiome instanceof OWLEquivalentClassesAxiom) {
			//System.out.print("isMapping de l'axiome : " + axiome.toString() + "; classes : ");
			Set<OWLClass> classes = axiome.getClassesInSignature();
			for(OWLClass classe: classes) {
				if(ontologyCurrentName == null)
					ontologyCurrentName = getOntologyNameFromClass(classe);
				else {
					String tmp = getOntologyNameFromClass(classe);
					if(!ontologyCurrentName.equals(tmp)) {
						//System.out.println("\nVRAI car 2 ontologies différentes trouvées (" + ontologyCurrentName + " et " + tmp);
						String [] res = {ontologyCurrentName, tmp};

						return res;
					}
				}
			}
			//System.out.println("\nFAUX uniquement des classes de " + ontologyCurrentName);
		}
		return null;
	}

	/**
	 * Méthode qui renvoie le type d'un mapping (à utiliser après <code>isMapping()</code>).
	 * Pour l'instant, seuls deux types de mappings sont pris en compte : subsomption et équivalence.
	 * @param axiome axiome OWL contenant les classes à tester
	 * @param localOntology ontologie OWL considérée comme locale (pour le sens des mappings de subsomption)
	 * @return	0 si les deux ontologies sont équivalentes,
	 * 			1 si le mapping définit une subsomption impliquant une super-classe d'une classe de l'ontologie locale externe à cette ontologie,
	 * 			2 si le mapping définit une subsomption impliquant une sous-classe d'une classe de l'ontologie locale externe à cette ontologie,
	 * 			-1 sinon.
	 */
	public static int getMappingType(OWLAxiom axiome, String localOntologyName) {
		if(axiome instanceof OWLEquivalentClassesAxiom) {
			return 0;
		} else if(axiome instanceof OWLSubClassOfAxiom) {
			OWLClass subClass = ((OWLSubClassOfAxiom) axiome).getSubClass().asOWLClass();
			OWLClass superClass = ((OWLSubClassOfAxiom) axiome).getSuperClass().asOWLClass();
			if(getOntologyNameFromClass(subClass).equals(localOntologyName) && !getOntologyNameFromClass(superClass).equals(localOntologyName))
				return 1;
			else if(getOntologyNameFromClass(superClass).equals(localOntologyName) && !getOntologyNameFromClass(subClass).equals(localOntologyName))
				return 2;
		}
		return -1;
	}

	/**
	 * Renvoie null si la classe est owl:Thing...
	 * @param classe
	 * @return
	 */
	public static String getOntologyNameFromClass(OWLClass classe) {
		String tab [] = classe.toString().split("#");
		//System.out.println(" [getOntologyNameFromClass] classe : " + classe.toStringID() + " ; ontologie : " + tab[0].substring(1));
		if(tab[0].equals("owl:Thing") || tab[0].equals("owl:Nothing")) {
			return null;
		}
		return tab[0].substring(1); // pour supprimer le '<' du début.
	}


	/**
	 * Le coeur du problème...
	 * @param manager Manager de l'ontologie source
	 * @param reasoner Reasoner lié à l'ontologie source (doit être un nonBufferingReasoner
	 * @param ontoSource Ontologie du pair où seront effectuées les inférences. <strong>Sera modifiée par cette méthode.</strong>
	 * @param ontoCible Ontologie à aligner avec l'ontologie source. Ne sera pas modifiée par cette méthode.
	 * @param fromSource Booléen permettant d'indiquer comment seront classés les mappings résultants.
	 * <ul>
	 * <li>Si <code>true</code>, les clés des 3 HashMap retournées seront des classes OWL de l'ontologie source</li>
	 * <li>Si <code>false</code>, les clés des 3 HashMap retournées seront des classes OWL de l'ontologie cible</li>
	 * </ul>
	 * @return un tableau de 3 HashMap contenant respectivement les mappings de sous-classes, de classes équivalentes et de super-classes, de chaque classe de l'ontologie source.
	 * Dans les 3 cas, les Hashmaps ont pour clé une classe de l'ontologie source et pour valeur une ArrayList de classes d'autres ontologies correspondant au type de mapping. 
	 */
	public static ArrayList<HashMap<OWLClass, ArrayList<OWLClass>>> detectMappings(OWLOntologyManager manager, OWLReasoner reasoner, OWLOntology ontoSource, OWLOntology ontoCible, boolean fromSource) {
		//System.out.println("Recherche de mappings entre " + ontoSource.toString() + " (" + ontoSource.getAxiomCount() + " axiomes) et " + ontoCible.toString() + " (" + ontoCible.getAxiomCount() + " axiomes)...");

		OWLOntology reference;
		if(fromSource) {
			reference = ontoSource;
		} else {
			reference = ontoCible;
		}
		HashMap<OWLClass, ArrayList<OWLClass>> queryAxiomsSubClasses = new HashMap<OWLClass, ArrayList<OWLClass>> (); 
		HashMap<OWLClass, ArrayList<OWLClass>> queryAxiomsEquClasses = new HashMap<OWLClass, ArrayList<OWLClass>> (); 
		HashMap<OWLClass, ArrayList<OWLClass>> queryAxiomsSupClasses = new HashMap<OWLClass, ArrayList<OWLClass>> (); 

		Set<OWLAxiom> axiomes = ontoCible.getAxioms();

		for(OWLAxiom axiome:axiomes) {
			//System.out.println("Ajout de l'axiome : " + axiome.toString());
			manager.addAxiom(ontoSource, axiome);
			//manager.saveOntology(ontoSource);
			//ontoSource.notify();
			//reasoner.flush();
			//Test de consistance light...
			if (!reasoner.isConsistent()) {
				System.err.println("Problème d'inconsistance à l'ajout de l'axiome " + axiome.toString() + ". Axiome retiré.");
				manager.removeAxiom(ontoSource, axiome);
			}
//			reasoner.flush();
		}
		for(OWLClass classe:ontoSource.getClassesInSignature()) {
			//On regarde si une classe dedans provient de l'ontologie référence
			if(isFromSameOntology(reference, classe)) {
				NodeSet<OWLClass> subClasses = reasoner.getSubClasses(classe, false);
				ArrayList<OWLClass> subs = queryAxiomsSubClasses.get(classe);
				if(subs == null)
					subs = new ArrayList<OWLClass>();
				for(org.semanticweb.owlapi.reasoner.Node<OWLClass> subClassNode:subClasses) {
					OWLClass subClass = subClassNode.getRepresentativeElement();
					//On ne rajoute la sous-classe que si elle ne fait pas partie de la même ontolgie
					if(!subClass.toString().equals("owl:Thing") && !subClass.toString().equals("owl:Nothing") && !isFromSameOntology(reference, subClass))
						subs.add(subClass);
				}
				queryAxiomsSubClasses.put(classe, subs);

				org.semanticweb.owlapi.reasoner.Node<OWLClass> equClasses = reasoner.getEquivalentClasses(classe);
				ArrayList<OWLClass> equs = queryAxiomsEquClasses.get(classe);
				if(equs == null)
					equs = new ArrayList<OWLClass>(); 
				for(OWLClass equClass:equClasses) {
					//On ne rajoute la sous-classe que si elle ne fait pas partie de la même ontolgie
					if(!equClass.toString().equals("owl:Thing") && !equClass.toString().equals("owl:Nothing") && !isFromSameOntology(reference, equClass)) {
						//System.out.println("Trouvé equ class de " + classe + " : " + equClass);
						equs.add(equClass);
					}
				}
				queryAxiomsEquClasses.put(classe, equs);

				NodeSet<OWLClass> supClasses = reasoner.getSuperClasses(classe, false);
				ArrayList<OWLClass> sups = queryAxiomsSupClasses.get(classe);
				if(sups == null)
					sups = new ArrayList<OWLClass>();
				for(org.semanticweb.owlapi.reasoner.Node<OWLClass> supClassNode:supClasses) {
					OWLClass supClass = supClassNode.getRepresentativeElement();
					//On ne rajoute la sous-classe que si elle ne fait pas partie de la même ontolgie
					if(!supClass.toString().equals("owl:Thing") && !supClass.toString().equals("owl:Nothing") && !isFromSameOntology(reference, supClass))
						sups.add(supClass);
				}
				queryAxiomsSupClasses.put(classe, sups);
			}
		}

		ArrayList<HashMap<OWLClass, ArrayList<OWLClass>>> mappings = new ArrayList<HashMap<OWLClass, ArrayList<OWLClass>>> ();
		mappings.add(queryAxiomsSubClasses);
		mappings.add(queryAxiomsEquClasses);
		mappings.add(queryAxiomsSupClasses);

		return mappings;
	}

	public static void afficheMappings(ArrayList<HashMap<OWLClass, ArrayList<OWLClass>>> listesMappings) {

		// Affichage complet des contenus du retour de detectMappings
		System.out.println("Résultats detectMappings : ");
		HashMap<OWLClass, ArrayList<OWLClass>> map;
		int indexArray = 0;
		map = listesMappings.get(0);
		System.out.println(" Nb de super-classes : " + map.size());
		for(OWLClass cleArray: map.keySet()) {
			ArrayList<OWLClass> array = map.get(cleArray);
			if(array.size()>0) {
				System.out.print("  classe locale[" + indexArray + "] : " + cleArray.toString());
				System.out.print(" ; superClasses - nombre : " + array.size() + " ; classes : ");
				for(OWLClass classeMappee: array) {
					System.out.print(classeMappee.toString() + " ");							
				}
				System.out.println();
			}
			indexArray++;
		}
		map = listesMappings.get(1);
		System.out.println(" Nb de classes équivalentes : " + map.size());
		for(OWLClass cleArray: map.keySet()) {
			ArrayList<OWLClass> array = map.get(cleArray);
			if(array.size()>0) {
				System.out.print("  classe locale[" + indexArray + "] : " + cleArray.toString());
				System.out.print(" ; equivalentClasses - nombre : " + array.size() + " ; classes : ");
				for(OWLClass classeMappee: array) {
					System.out.print(classeMappee.toString() + " ");							
				}
				System.out.println();
			}
			indexArray++;
		}
		map = listesMappings.get(2);
		System.out.println(" Nb de sous-Classes : " + map.size());
		for(OWLClass cleArray: map.keySet()) {
			ArrayList<OWLClass> array = map.get(cleArray);
			if(array.size()>0) {
				System.out.print("  classe locale[" + indexArray + "] : " + cleArray.toString());
				System.out.print(" ; sousClasses - nombre : " + array.size() + " ; classes : ");
				for(OWLClass classeMappee: array) {
					System.out.print(classeMappee.toString() + " ");							
				}
				System.out.println();
			}
			indexArray++;
		}
	}

	public static String findIri(OWLClass c, SemQueryInfos q)
	{
		String[] list=c.getIRI().toString().split("#");
		String result=q.getValeur()+"#"+list[list.length-1];
		return result;
	}

	public static String findIri(OWLClass c, long id)
	{
		String[] list=c.getIRI().toString().split("#");
		String result="network:/"+id+"#"+list[list.length-1];
		return result;
	}

	public static boolean isFromSameOntology(OWLOntology ontology, OWLClass classe) {
		URI ontoURI = ontology.getOntologyID().getOntologyIRI().toURI();
		URI classeURI = classe.getIRI().toURI();
		return ontoURI.getAuthority().equals(classeURI.getAuthority());
	}

	public static SemanticNode getNodeFromOntologyName(String name) {
		SemanticNode res = null;
		for(int i=0; i< Network.size(); i++) {
			SemanticNode tmp = (SemanticNode) Network.get(i);
			//System.out.println("Comparaison de " + name + " avec " + tmp.getNamespace());
			if(tmp.getNamespace().equals(name))
				res = tmp;
		}
		return res;
	}
}