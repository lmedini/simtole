package intersimul.model.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Class responsible for managing all constant parameters of an experiment.<br />
 * Parameters the vary from one simulation to another (i.e. topology-relative parameters) are stored in class Simulation.
 * @author Lionel Médini
 *
 */
public class Experiment {
	// déclaration des attributs d'une simulation (interne à cette classe)
	private String expName = "exp"; // nom de la simulation
	private int nbPeer = 0;          // nombre de pairs
	private int nbQuery = 0;         // nombre de requêtes
	private int nbNeighbors = 0;      // nombre de voisins
	private int ttl = 0; 			  // ttl des requêtes
	private int nbCycle = 0;
	private String topologyStrategy = null;   // topologie du réseau 
	private boolean dynNetwork = false;           // pour savoir si la topologie du réseau change pendant la simulation
	private boolean experimentsByCycle = false;  // pour savoir si la simulation s'effectue par cycle ou par événement

	private String[] topologyStrategies = null;

	private String urlFile = null;       // url du fichier de config associé à la simulation
	private String protocols = null;
	private String scenarioName;

	private String nodeType = null; // Type de noeud à utiliser dans le réseau

	private ArrayList<Node> nodes = null;
	private Random r;
	private HashMap<String, Simulation> simulations;

	/**
	 * constructors
	 *
	 */
	public Experiment(){
		// initialisation des propriétés à choix multiple et limité
		this.topologyStrategies = new String[]{ParamLoader.RANDOM, ParamLoader.CLUSTERED};
		this.simulations = new HashMap<String, Simulation>();
		this.r = new Random(124);
	}

	public Experiment(String name ){
		this();
		setName(name);
	}

	public void loadNodes(){
		Node cnode = null;
		this.nodes = new ArrayList<Node>(getPeersNb());
		for( int i = 0 ; i < getPeersNb() ; i++ ) {
			cnode = new Node( i );
			this.nodes.add(i , cnode);
		}
		System.out.println(" Nodes created.");
	}

	/**
	 * Creates a new simulation in the current experiment.
	 * @return The new simulation ID, that will be needed to use the getSimulation method.
	 */
	public String createSimulation() {
		String id = this.getName() + this.simulations.size();
		this.simulations.put(id, new Simulation(this, id));
		return id;
	}

	////// GETTERS AND SETTERS
	// <editor-fold desc="accesseurs">
	public String getFileUrl() {
		return urlFile;
	}

	public void setFileUrl(String urlFile) {
		this.urlFile = urlFile;
	}

	public int getNeighborsNb() {
		return nbNeighbors;
	}

	public void setNeighborsNb(int nbNeighbors) {
		this.nbNeighbors = nbNeighbors;
	}

	public int getPeersNb() {
		return nbPeer;
	}
	public void setPeersNb(int nbPeer) {
		this.nbPeer = nbPeer;
	}

	public int getCyclesNb() {
		return nbCycle;
	}
	public void setCyclesNb(int nbCycle) {
		this.nbCycle = nbCycle;
	}

	public int getQueriesNb() {
		return nbQuery;
	}

	public void setQueriesNb(int nbQuery) {
		this.nbQuery = nbQuery;
	}

	public int getTtl() {
		return ttl;
	}


	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public String getName() {
		return expName;
	}

	public void setName(String simulName) {
		this.expName = simulName;
	}

	public String getProtocols() {
		return this.protocols;
	}

	public void setProtocols(String protocols) {
		this.protocols = protocols;
	}

	public boolean isDynNetwork() {
		return dynNetwork;
	}


	public void setDynNetwork(boolean dynNetwork) {
		this.dynNetwork = dynNetwork;
	}


	public boolean isExperimentsByCycle() {
		return experimentsByCycle;
	}


	public void setExperimentsByCycle(boolean experimentsByCycle) {
		this.experimentsByCycle = experimentsByCycle;
	}

	/**
	 * Lists all the known topology strategies
	 * @return An array of topology strategies names
	 */
	public String[] getTopologyStrategies() {
		return topologyStrategies;
	}

	/**
	 * Returns the actual topology strategy of this experiment
	 * @return The topology strategy name
	 */
	public String getTopologyStrategy() {
		return topologyStrategy;
	}

	/**
	 * Sets the actual topology strategy of this experiment.
	 * Only works if the given argument is contained in the list of known topology strategies; does nothing otherwise.
	 * @param topology Name of the topology strategy to be set
	 */
	public void setTopologyStrategy(String topology) {
		for( int i = 0 ; i < this.topologyStrategies.length ; i++ ){
			if( this.topologyStrategies[i].equals(topology)){
				this.topologyStrategy =  this.topologyStrategies[i];
				break;
			}
		}
	}

	public String getScenarioName() {
		return this.scenarioName;
	}

	public void setScenarioName(String name) {
		this.scenarioName = name;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public Node getNode(int i) {
		return this.nodes.get(i);
	}

	public Simulation getSimulation(String id) {
		return this.simulations.get(id);
	}

	public Random getRandom() {
		return r;
	}
	// </editor-fold>
}