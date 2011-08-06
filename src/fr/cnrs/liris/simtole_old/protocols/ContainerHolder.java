package fr.cnrs.liris.simtole_old.protocols;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mindswap.pellet.owlapi.Reasoner;
import org.picocontainer.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import fr.cnrs.liris.simtole.mapping.EquivalentMapping;
import fr.cnrs.liris.simtole.mapping.SubsomptionMapping;



import peersim.core.Protocol;
import sun.reflect.Reflection;

public class ContainerHolder implements Protocol {
	// 2 containers :
	// * 1 pour les objets en rapports avec l'ontologie
	protected MutablePicoContainer semContainer;
	// * 1 pour les tableaux
	protected MutablePicoContainer tabContainer;

	// Paramètres de l'ontologie
	protected IRI physicalIRI;
	protected IRI ontologyIRI;
	protected String nomFichier;

	// Objets de gestion de l'ontologie et du moteur d'inférences
	protected OWLOntologyManager manager;
	protected OWLOntology ontology;
	protected Reasoner reasoner;

	// Tableaux
	protected HashMap<Integer, Integer> tableauMetrique; // Pas compris à quoi
															// ça sert, ça...
															// (Lionel).
	protected ArrayList<EquivalentMapping> tableauEquivalences;
	protected ArrayList<SubsomptionMapping> tableauSubsomptions;

	// Contrôle d'initialisation des paramètres de l'ontologie
	private static final int INIT_NOMFICH = 1;
	private static final int INIT_PHYSURI = 2;
	private static final int INIT_ONTOURI = 4;
	private int ontoParamsInit = 0;
	private String prefix;

	@SuppressWarnings("unchecked")
	/**
	 * Constructeur "simple" du pair. Initialise les conteneurs. Les composants sont instanciés dans les fonctions à part, car on ne peut initialiser l'ontologie qu'une fois la config des paramètres faite. 
	 */
	public ContainerHolder(String prefix) {
		this.prefix = prefix;
		semContainer = new PicoBuilder().withCaching().build();
		tabContainer = new PicoBuilder().withCaching().build();
	}

	public ContainerHolder(String prefix, String nomFich, IRI physIri, IRI ontoIri) {
		this(prefix);

		setNomFichier(nomFich);
		setPhysicalIri(physIri);
		setOntologyIri(ontoIri);
	}

	/**
	 * initialise les composants du conteneur de tableaux.
	 * 
	 * @author L. Médini Topology defined
	 */
	public void tabInit() {
		tabContainer.addComponent("tableauMetrique", new HashMap<Integer, Integer>());
		tabContainer.addComponent("tableauEquivalences", new ArrayList<EquivalentMapping>());
		tabContainer.addComponent("tableauSubsomptions", new ArrayList<SubsomptionMapping>());

		tableauMetrique = this.getTableauMetrique();
		tableauEquivalences = this.getTabEquivalent();
		tableauSubsomptions = this.getTabSubsomption();
	}

	/**
	 * initialise les composants du conteneur sémantique une fois les URI des
	 * ontologies dispatchées par OwlDistributionInitializer.
	 * 
	 * @author L. Médini Topology defined
	 */
	public void semInit() throws ExceptionInInitializerError {
		if (ontoParamsInit == INIT_NOMFICH + INIT_PHYSURI + INIT_ONTOURI) {
			//Initialisation du manager
			OWLOntologyManager man = OWLManager.createOWLOntologyManager();
			OWLOntologyIRIMapper mapper = new SimpleIRIMapper(getOntologyIri(), getPhysicalIri());
			man.addIRIMapper(mapper);
			semContainer.addComponent("manager", man);

			//Initialisation de l'ontologie
			try {
				OWLOntology onto = man.loadOntology(getPhysicalIri());
				semContainer.addComponent("ontology", onto);
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//Initialisation du reasoner
			semContainer.addComponent("reasoner", Reasoner.class);

			//Initialisation des variables globales (à virer)
			manager = this.getManager();
			ontology = this.getOntology();
			reasoner = this.getReasoner();
		} else
			throw new ExceptionInInitializerError("Paramètres de l'ontologie non initialisés ; ontoParamsInit = " + Reflection.getCallerClass(2));
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		ContainerHolder svh = null;
		try {
			svh = (ContainerHolder) super.clone();
/*			svh.setNomFichier(this.getNomFichier());
			svh.setPhysicalUri(this.getPhysicalUri());
			svh.setOntologyUri(this.getOntologyUri());
			svh.tabInit();
			svh.semInit();
*/		} catch (CloneNotSupportedException e) {
			System.err.println("Erreur de clonage du pair");
			e.printStackTrace();
		}
		return svh;
	}

	// Accesseurs des paramètres de l'ontologie
	public String getNomFichier() {
		return nomFichier;
	}

	public IRI getPhysicalIri() {
		return physicalIRI;
	}

	public IRI getOntologyIri() {
		return ontologyIRI;
	}

	public void setPhysicalIri(IRI iri) {
		physicalIRI = iri;
		ontoParamsInit = ontoParamsInit | INIT_PHYSURI;
		//System.out.println("Pair " + this.toString() + " : initialisation de l'URI physique de l'ontologie à " + this.getPhysicalUri());
	}

	public void setOntologyIri(IRI iri) {
		ontologyIRI = iri;
		ontoParamsInit = ontoParamsInit | INIT_ONTOURI;
		//System.out.println("Pair " + this.toString() + " : initialisation de l'URI locale de l'ontologie à " + this.getOntologyUri());
	}

	public void setNomFichier(String nomFichier) {
		this.nomFichier = nomFichier;
		ontoParamsInit = ontoParamsInit | INIT_NOMFICH;
		//System.out.println("Pair " + this.toString() + " : initialisation du nom de fichier de l'ontologie à " + this.getNomFichier());
	}

	// Accesseurs des tableaux
	@SuppressWarnings("unchecked")
	public HashMap<Integer, Integer> getTableauMetrique() {
		return (HashMap<Integer, Integer>) tabContainer.getComponent("tableauMetrique");
	}

	@SuppressWarnings("unchecked")
	public ArrayList<EquivalentMapping> getTabEquivalent() {
		return (ArrayList<EquivalentMapping>) tabContainer.getComponent("tableauEquivalences");
	}

	@SuppressWarnings("unchecked")
	public ArrayList<SubsomptionMapping> getTabSubsomption() {
		return (ArrayList<SubsomptionMapping>) tabContainer.getComponent("tableauSubsomptions");
	}

	// Accesseurs des éléments sémantiques
	public OWLOntologyManager getManager() {
		return (OWLOntologyManager) semContainer.getComponent("manager");
	}

	public OWLOntology getOntology() {
		return (OWLOntology) semContainer.getComponent("ontology");
	}

	public Reasoner getReasoner() {
		return (Reasoner) semContainer.getComponent("reasoner");
	}
}