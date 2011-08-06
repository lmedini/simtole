package fr.cnrs.liris.simtole.node;

import java.util.ArrayList;
import java.util.HashMap;

import org.mindswap.pellet.PelletOptions;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import peersim.core.GeneralNode;
import sun.reflect.Reflection;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import fr.cnrs.liris.simtole.mapping.EquivalentMapping;
import fr.cnrs.liris.simtole.mapping.SubsomptionMapping;
import fr.cnrs.liris.simtole.node.measurer.Measurer;

//import org.picocontainer.MutablePicoContainer;
//import org.picocontainer.PicoBuilder;

/**
 * Noeud du r�seau qui embarque une ontologie, un moeur d'inf�rences et un outil de mesure de distance entre les ontologies.<br />
 * Attention : le namespace de l'ontologie renvoy� sera : <code>http://</code> suivi du nom du fichier contenant l'ontologie (sans extension).
 * @Author Lionel M�dini
 */
public abstract class SemanticNode extends GeneralNode {

	// Param�tres de l'ontologie
	private IRI physicalIRI;
	private IRI ontologyIRI;
	private String nomFichier;
	// Champ calcul�
	private String nameSpace;

	// Objets de gestion de l'ontologie et du moteur d'inf�rences (pas dans un conteneur, finalement)
	protected OWLOntologyManager manager = null;
	protected OWLOntology localOntology = null;
	protected OWLOntology reasoningOntology = null;
	protected OWLReasoner reasoner = null;

	// Moteur de calcul de la similarit� entre noeuds
	private Measurer measurer = null;

	// Tableaux 
	private HashMap<Integer, Double> tableauDistance;
	private HashMap<Integer, ArrayList<EquivalentMapping>> tableauEquivalences;
	private HashMap<Integer, ArrayList<SubsomptionMapping>> tableauSubsomptions;

	// Contr�le d'initialisation des param�tres de l'ontologie
	private static final int INIT_NOMFICH = 1;
	private static final int INIT_PHYSURI = 2;
	private static final int INIT_ONTOURI = 4;
	private int ontoParamsInit = 0;

	protected String prefix;

	/**
	 * Constructeur "simple" du pair. Initialise le conteneur et les tableaux.
	 * Les composants s�mantiques sont instanci�s dans les fonctions � part, car on ne peut les initialiser qu'une fois la config des param�tres de l'ontologie connus.
	 * @param prefix Pour la compatibilit� avec Node
	 */
	public SemanticNode(String prefix) {
		super(prefix);
		
		tableauDistance = new HashMap<Integer, Double>();
		tableauEquivalences = new HashMap<Integer, ArrayList<EquivalentMapping>>();
		tableauSubsomptions = new HashMap<Integer, ArrayList<SubsomptionMapping>>();

		this.measurer = new Measurer(this);
	}

	/**
	 * Constructeur avec tous les param�tres n�cessaires pour l'initialisation.
	 * Non utilis� car les pairs sont instanci�s par clonage.
	 * @param prefix Pour la compatibilit� avec Node
	 * @param nomFich Nom local de l'ontologie
	 * @param physIri Chemin complet du fichier de l'ontologie
	 * @param ontoIri Nom de l'ontologie dans le r�seau P2P, g�n�r� automatiquement (exemple : network:/12)
	 */
	public SemanticNode(String prefix, String nomFich, IRI physIri, IRI ontoIri) {
		this(prefix);

		setNomFichier(nomFich);
		setPhysicalIri(physIri);
		setOntologyIri(ontoIri);

		semInit();
	}

	/**
	 * Initialise les composants du conteneur s�mantique une fois les URI des
	 * ontologies dispatch�es par OwlDistributionInitializer.
	 */
	public void semInit() throws ExceptionInInitializerError {
		if (isInitialized()) {
			/**
			 * Initialisation de l'ontologie locale.
			 * Cette ontologie n'est plus modifi�e ensuite.
			 */
			//Initialisation du manager de l'ontologie locale. N'est utilis� qu'ici.
			OWLOntologyManager localOntoManager = OWLManager.createOWLOntologyManager();
			OWLOntologyIRIMapper localOntoMapper = new SimpleIRIMapper(getOntologyIri(), getPhysicalIri());
			localOntoManager.addIRIMapper(localOntoMapper);
			//Initialisation de l'ontologie
			try {
				// On cr�e l'ontologie locale.
				this.localOntology = localOntoManager.loadOntology(getPhysicalIri());
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}
			//System.out.println("localOntology : " + localOntology + " ; pyhsicalIri : " + getPhysicalIri().toString());

			/**
			 * Initialisation de l'ontologie du reasoner, qui contiendra l'ontologie locale plus d'autres axiomes �ventuels.
			 * On cr�e l'instance, qu'on rattache � un manager et un reasoner accessibles par des getters.
			 * L'instance reste la m�me, mais le contenu change.
			 */
			//Initialisation du manager de l'ontologie du reasoner
			this.manager = OWLManager.createOWLOntologyManager();
			// On cr�e une deuxi�me ontologie.
			try {
				IRI ontoName = IRI.create(this.nameSpace);
				OWLOntologyID ontoId = new OWLOntologyID(ontoName);
				this.reasoningOntology = this.manager.createOntology(ontoId);
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}
			//Initialisation du reasoner
			PelletReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();

        	PelletOptions.USE_INCREMENTAL_CONSISTENCY = true;
            PelletOptions.USE_COMPLETION_QUEUE = true;

            //PelletReasoner reasoner = reasonerFactory.createReasoner(reasoningOntology);
            PelletReasoner reasoner = reasonerFactory.createNonBufferingReasoner(reasoningOntology);

            // add the reasoner as an ontology change listener
            this.manager.addOntologyChangeListener(reasoner);

            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
/*			reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
			reasoner.precomputeInferences(InferenceType.DATA_PROPERTY_HIERARCHY);
			reasoner.precomputeInferences(InferenceType.DISJOINT_CLASSES);
			reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
			reasoner.precomputeInferences(InferenceType.SAME_INDIVIDUAL);

*/		this.reasoner = reasoner;    
		} else
			throw new ExceptionInInitializerError("Param�tres de l'ontologie non initialis�s ; ontoParamsInit = " + Reflection.getCallerClass(2));

		//initOntoReasoner();
	}

	/**
	 * (R�)initialise l'ontologie utilis�e par le reasoner � partir de l'ontologie locale. Peut �tre surcharg�e pour faire des modifs sur l'ontologie renvoy�e...
	 */
	protected abstract void initOntoReasoner();

	//M�thode qui permet de d�terminer si un noeud est correctement initialis�
	private boolean isInitialized() {
		return ontoParamsInit == INIT_NOMFICH + INIT_PHYSURI + INIT_ONTOURI;
	}

	// Accesseurs des param�tres de l'ontologie
	public IRI getPhysicalIri() {
		return physicalIRI;
	}

	public IRI getOntologyIri() {
		return ontologyIRI;
	}

	public void setPhysicalIri(IRI iri) {
		if(iri != null) {
			physicalIRI = iri;
			ontoParamsInit = ontoParamsInit | INIT_PHYSURI;
			//System.out.println("Pair " + this.toString() + " : initialisation de l'URI physique de l'ontologie � " + this.getPhysicalIri());
		}
	}

	public void setOntologyIri(IRI iri) {
		if(iri != null) {
			ontologyIRI = iri;
			ontoParamsInit = ontoParamsInit | INIT_ONTOURI;
			//System.out.println("Pair " + this.toString() + " : initialisation de l'URI locale de l'ontologie � " + this.getOntologyIri());
		}
	}

	public String getNomFichier() {
		return nomFichier;
	}

	public void setNomFichier(String nomFichier) {
		if(nomFichier != null) {
			this.nomFichier = nomFichier;
			//Initialisation du champ calcul�
			setNamespace(this.nomFichier);
			ontoParamsInit = ontoParamsInit | INIT_NOMFICH;
			//System.out.println("Pair " + this.toString() + " : initialisation du nom de fichier de l'ontologie � " + this.getNomFichier());
		}
	}

	// Champ calcul� => private
	private void setNamespace(String nomFichier) {
		String [] tmp = nomFichier.split("[.]");
		System.out.println("nomFichier = " + nomFichier + " ; tmp = " + tmp.length);
		this.nameSpace = "http://" + tmp[0];
	}

	public String getNamespace() {
		return this.nameSpace;
	}

	// Accesseurs des tableaux (on masque la structure des tableaux)
	public Double getDistanceAvec(int nodeId) {
		Integer id = new Integer(nodeId);
		Double distance = tableauDistance.get(id);
		if(distance == null) {
			distance = measurer.calcule(nodeId);
			System.out.println("Calcul distance de " + this.getID() + " vers " + nodeId + " : " + tableauDistance.get(id));
		}
		return distance;
	}

	public void setDistanceAvec(int nodeId, double res) {
		Integer id = new Integer(nodeId);
		Double sim = new Double(res);
		tableauDistance.put(id, sim);
	}

	public HashMap<Integer, Double> getTableauDistance() {
		return tableauDistance;
	}

	// Gestion des tableaux de mappings
	public ArrayList<EquivalentMapping> getEquivalentMappingsAvec(int nodeId) {
		if(!aPourVoisin(nodeId))
			initNouveauVoisin(nodeId);
		Integer id = new Integer(nodeId);
		return tableauEquivalences.get(id);
	}

	public void addEquivalentMappingsAvec(int nodeId, EquivalentMapping mapping) {
		if(!aPourVoisin(nodeId))
			initNouveauVoisin(nodeId);
		boolean dejaDedans = false;
		Integer id = new Integer(nodeId);
		for(EquivalentMapping tmp: tableauEquivalences.get(id)) {
			if(tmp.equals(mapping))
				dejaDedans = true;
		}
		if(!dejaDedans)
			tableauEquivalences.get(id).add(mapping);
	}

	public void removeEquivalentMappingsAvec(int nodeId, EquivalentMapping mapping) {
		if(!aPourVoisin(nodeId))
			initNouveauVoisin(nodeId);
		Integer id = new Integer(nodeId);
		tableauEquivalences.get(id).remove(mapping);
	}

	public ArrayList<SubsomptionMapping> getSubsomptionMappingsAvec(int nodeId) {
		if(!aPourVoisin(nodeId))
			initNouveauVoisin(nodeId);
		Integer id = new Integer(nodeId);
		return tableauSubsomptions.get(id);
	}

	public void addSubsomptionMappingsAvec(int nodeId, SubsomptionMapping mapping) {
		if(!aPourVoisin(nodeId))
			initNouveauVoisin(nodeId);
		boolean dejaDedans = false;
		Integer id = new Integer(nodeId);
		for(SubsomptionMapping tmp: tableauSubsomptions.get(id)) {
			if(tmp.equals(mapping))
				dejaDedans = true;
		}
		if(!dejaDedans)
			tableauSubsomptions.get(id).add(mapping);
	}

	public void removeSubsomptionMappingsAvec(int nodeId, SubsomptionMapping mapping) {
		if(!aPourVoisin(nodeId))
			initNouveauVoisin(nodeId);
		Integer id = new Integer(nodeId);
		tableauSubsomptions.get(id).remove(mapping);
	}

	public int getEquivalentMappingsCount() {
		int nbEqu = 0;
		for(Integer nodeId: tableauEquivalences.keySet()) {
			nbEqu += tableauEquivalences.get(nodeId).size();
		}
		return nbEqu;
	}

	public int getSubsomptionMappingsCount() {
		int nbSub = 0;
		for(Integer nodeId: tableauSubsomptions.keySet()) {
			nbSub += tableauSubsomptions.get(nodeId).size();
		}
		return nbSub;
	}

	// Accesseurs des �l�ments s�mantiques
	/**
	 * Renvoie l'ontologyManager qui a en charge l'ontologie du pair. Notamment n�cessaire pour y ajouter des axiomes. 
	 */
	public OWLOntologyManager getManager() {
		return manager;
	}

	/**
	 * Renvoie l'ontologie associ�e au reasoner, avec uniquement l'ontologie locale dedans. Il est possible d'y rajouter des axiomes pour effectuer des inf�rences.
	 * @return L'instance de l'ontologie associ�e au reasoner (ind�pendante de l'ontologie locale).
	 */
	public OWLOntology getOntology() {
		initOntoReasoner();
		return reasoningOntology;
	}

	/**
	 * Renvoie le moteur d'inf�rences associ� � l'ontologie obtenue avec getOntology.
	 * Le reasoner est param�tr� pour �tre un "non buffering reasoner" :
	 * il est capable d'inf�rer sur les connaissances rajout�es dans l'ontologie d�s qu'elles sont ajout�es.
	 * @return Une instance de OWLReasoner associ� � l'ontologie locale du pair.
	 */
	public OWLReasoner getReasoner() {
		return reasoner;
	}

	/**
	 * Renvoie le moteur de calcul de similarit� construit avec le pair.
	 * @return un SimilarityMeasurer capable de mesurer la similarit� du pair courant avec un autre pair du r�seau.
	 */
	public Measurer getMeasurer() {
		return measurer;
	}

	/**
	 * M�thode n�cessaire pour la mesure de distance entre ontologies
	 */
	public int getOntologyAxiomCount() {
		return getOntology().getLogicalAxiomCount();
	}

	/**
	 * Mis en private par d�faut. Pas de probl�me � le mettre en public a priori.
	 * @param nodeId
	 * @return
	 */
	private boolean aPourVoisin(int nodeId) {
		Integer id = new Integer(nodeId);
		return tableauSubsomptions.keySet().contains(id);
	}

	private void initNouveauVoisin(int nodeId) {
		// Faudra faire un truc plus intelligent, genre d�finir une classe "InfoVoisin" avec les tableaux dedans
		Integer id = new Integer(nodeId);
		tableauSubsomptions.put(id, new ArrayList<SubsomptionMapping>());
		tableauEquivalences.put(id, new ArrayList<EquivalentMapping>());
	}

	public void affiche() {	 
		OWLOntology ontology = getOntology();
		PelletReasoner reasoner = (PelletReasoner) this.reasoner;
        System.out.println("Ontologie : " + getNomFichier());
    	System.out.println("Nombre d'axiomes : " + ontology.getAxiomCount());
    	System.out.println("Nombre de classes : " + reasoner.getKB().getClasses().size());
    	System.out.println("Nombre de propri�t�s : " + reasoner.getKB().getProperties().size());
    	System.out.println("Nombre d'instances : " + reasoner.getKB().getIndividuals().size());
    	System.out.println("Expressivit� : " + reasoner.getKB().getExpressivity());
        System.out.println("Consistante : " + (reasoner.isConsistent()?"OUI":"NON"));
        System.out.println("Nombre de classes insatisfiables : " + reasoner.getUnsatisfiableClasses().getSize());
	}

	/**
	 * M�thode qui retransmet au noeud la notification de modification de l'ontologie.
	 */
	public void ontologyChangeNotify() {
		this.manager.notify();
	}
}