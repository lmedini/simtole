package intersimul.old;

import intersimul.model.experiment.ParamLoader;
import intersimul.model.experiment.Experiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import peersim.config.Configuration;
import peersim.config.ParsedProperties;

public class Simulation_old extends Experiment //A cause des erreurs de compilation...
{
	// définition des noms de paramètres dans les fichiers de configuration (fichiers "*.params")
	private static final String SIM_NAME = "simulName";
	private static final String NB_PEER = "nbPeers";
	//private static final String NB_DOC = "nbDocs";
	private static final String NB_QRY = "nbQueries";
	private static final String NB_CYCLES = "nbCycles";
	private static final String DEGREE = "degree";
	//private static final String NB_THEME = "nbThemes";
	//private static final String NB_PROFILE_DOC = "nbProfileDoc";
	//private static final String NB_PROFILE_QRY = "nbProfileQry";
	private static final String TTL = "ttl";
	private static final String EXP_CYCLE = "experimentByCycle";
	private static final String DYNAMICS = "dynamicNetwork";
	//private static final String Q_BASED_D = "queryBasedDocument";
	//private static final String DOC_PRO_NAME = "docProfileName";
	private static final String QRY_PRO_NAME = "qryProfileName";
	private static final String TH_NAME = "themeName";
	private static final String TOPOLOGY = "topology";
	private static final String MODE = "mode";
	//private static final String DOC_DISTRIB = "docDistrib";
	//private static final String QRY_DISTRIB = "qryDistrib";
	private static final String PROTOCOLES = "protocols";
	/**
	 * Specify strategy for network building or document and query distribution
	 */
	public static final String RANDOM = "Random";
	public static final String CLUSTERED = "Clustered";

	public static final String ZIPF = "Zipf";
	public static final String UNIFORM = "Uniform";

	public static final String NODE = "network.node";

	// Ajout Lionel : int qui sera rajouté au nom de la simulation pour pouvoir en lancer plusieurs
	private static int simulationNb = 0;

	// déclaration des attributs d'une simulation (interne à cette classe)
	String simulName = "exp"; // nom de la simulation
	int nbPeer = 0;          // nombre de pairs
	int nbDocument = 0;      // nombre de documents
	int nbQuery = 0;         // nombre de requêtes
	int nbNeighbors = 0;      // nombre de voisins
	int nbTheme = 0;     	  // nombre de thèmes
	int nbProfileDoc = 0;     // nombre de profils de document
	int nbProfileQuery = 0;   // nombre de profils de requêtes
	int ttl = 0; 			  // ttl des requêtes
	int nbCycle = 0;
	String topology = null;   // topologie du réseau 
	boolean dynNetwork = false;           // pour savoir si la topologie du réseau change pendant la simulation
	boolean experimentsByCycle = false;  // pour savoir si la simulation s'effectue par cycle ou par événement
	boolean qryBasedDoc = false;   		 // pour savoir si les requêtes posées portent sur les documents stockés par le pair	
	boolean sourceQuery = false; // true : profil local (le profil requête dépend des thèmes stockés localement)
	// false : profil généré par rapport aux paramêtres globaux

	String[] topologyStrategies = null;
	String[] distribNbDocByNode = null;
	String docDistrib = null;
	String[] distribNbQueryByNode = null; // utile seulement si sourceQuery = false
	String qryDistrib = null;
	DocumentProfiles docProfiles = null; //ensemble des profiles de document
	QueryProfiles qryProfiles = null;    // ensemble des profiles de requêtes

	String urlFile = null;       // url du fichier de config associé à la simulation
	String urlProtocol = null;
	String protocols = null;
	String scenarioName;
	String mode = "auto";

	String nodeType = null; // Type de noeud à utiliser dans le réseau

	/**
	 * constructors
	 *
	 */
	public Simulation_old(){
		// initialisation des propriétés à choix multiple et limité
		this.topologyStrategies = new String[]{RANDOM, CLUSTERED};
		this.distribNbDocByNode = new String[]{RANDOM, ZIPF, UNIFORM };
		this.distribNbQueryByNode = new String[]{RANDOM, ZIPF, UNIFORM };
	}

	public Simulation_old(String name ){
		this();
		setName(name);
		this.urlFile = ParamLoader.getConfigFileURL(this);
		this.protocols = ParamLoader.getProtocolsList(this);
	}



	/**
	 * initialize simulation properties with params stored in the config file specified in url
	 * @param url
	 * @throws IOException
	 */
	public void loadConfig(String url) throws IOException{

		// affichage dans la console de la simulation courante en cours de traitement
		System.out.println("load config : " + url );

		this.urlFile = url ;

		// chargement des paramètres
		Configuration.setConfig(new ParsedProperties(url));

		//// chargement des propriétés principales de la simulation
		// récupération du nom associé à la siulation
		this.setName( Configuration.getString(SIM_NAME));
		// récupération du nombre de pairs souhaité dans le réseau P2P
		this.setPeersNb( Configuration.getInt(NB_PEER) );
		// récupération du nombre de documents disséminés à travers le réseau pdt la simulation 
		//this.setNbDocument( Configuration.getInt(NB_DOC) );
		// récupération du nombre de requêtes exécutées lors de la simulation
		System.out.println("Nombre de requêtes : " + Configuration.getInt(NB_QRY));
		this.setQueriesNb(Configuration.getInt(NB_QRY));
		// récupération du nombre de voisins par pair
		this.setNeighborsNb(Configuration.getInt(DEGREE));
		// récupération du nombre de thèmes considérés lors de la simulation
		//this.setNbTheme( Configuration.getInt(NB_THEME) );
		// récupération du nombre de profils de document définis pour la simulation
		//this.setNbProfileDoc( Configuration.getInt(NB_PROFILE_DOC) );
		// récupération du nombre de profils de requête définis pour la simulation
		//this.setNbProfileQuery( Configuration.getInt(NB_PROFILE_QRY) );
		// récupération du TTL
		this.setTtl( Configuration.getInt(TTL) );
		// récupération de la distribution des documents
		//this.loadDocDistribution( Configuration.getString(DOC_DISTRIB) );
		// récupération de la distribution des requêtes
		//this.loadQryDistribution( Configuration.getString(QRY_DISTRIB) );
		// récupération de la topologie
		this.loadTopologyStrategy(  Configuration.getString(TOPOLOGY)) ;
		// récupération du paramêtre de dynamicité du réseau
		// true : la topologie évolue pendant la simulation
		// false : la topologie est statique lors de la simulation
		this.setDynNetwork( Configuration.getBoolean(DYNAMICS));
		// récupération du protocole de simulation
		// true : simulation par cycle
		// false : simulation par événement
		this.setExperimentsByCycle( Configuration.getBoolean(EXP_CYCLE));
		this.setCyclesNb( Configuration.getInt(NB_CYCLES));
		this.setProtocols(Configuration.getString(PROTOCOLES));
		// récupération du paramètre de lien entre les requêtes et les données stockées
		// true : le pair pose des requêtes sur les données qu'il stocke
		// false : le pair pose des requêtes sur n'importe quel thème
		//this.setQryBasedDoc( Configuration.getBoolean(Q_BASED_D));

		//// chargement des profils de document et de requêtes
		// création d'un ensemble de profils de documents
		//this.docProfiles = new DocumentProfiles(this.nbProfileDoc, this.nbTheme);
		// création d'un ensemble de profils de requêtes
		//this.qryProfiles = new QueryProfiles(this.nbProfileQuery , this.nbTheme);
		
		this.setNodeType(Configuration.getString(NODE));
		//System.out.println("NodeType : " + this.getNodeType());

		// stocke temporairement les noms des profils chargés ou des noms de thème
		String[] tab = null;

		// chargement des noms de profils de documents
		//		tab = ParamLoader.parseConfigValues( Configuration.getString(DOC_PRO_NAME) , this.nbProfileDoc );
		//		this.docProfiles.setProfiles( tab );
		//		
		//		// chargement des noms de profils de requêtes
		//		tab = ParamLoader.parseConfigValues( Configuration.getString(QRY_PRO_NAME) , this.nbProfileQuery );
		//		this.qryProfiles.setProfiles( tab );
		//		
		//		// chargement des noms de thèmes
		//		tab = ParamLoader.parseConfigValues( Configuration.getString(TH_NAME) , this.nbTheme );
		//		this.docProfiles.setThemes( tab );
		//		this.qryProfiles.setThemes( tab );
		//		
		//		try{
		//			// chargement des proportions de thèmes dans les profils de document
		//			this.loadConfigDocProfiles();
		//		}catch(Exception e){System.err.println("No document profile config file !");};
		//		
		//		try{
		//			// chargement des proportions de thèmes dans les profils de requêtes
		//			this.loadConfigQryProfiles();
		//		}catch(Exception e){System.err.println("No query profile config file !");};
	}


	/**
	 * Extract and load the document profiles
	 * @throws Exception
	 */
	private void loadConfigDocProfiles()throws Exception {
		int[][] pDoc = ParamLoader.parseProfilesValues( ParamLoader.getUrlDocProfiles(this) , this.nbProfileDoc, this.nbTheme);
		this.docProfiles.setValues( pDoc );
	}

	/**
	 * Extract and load the query profiles
	 * @throws Exception
	 */	
	private void loadConfigQryProfiles()throws Exception {
		int[][] pQry = ParamLoader.parseProfilesValues( ParamLoader.getUrlQryProfiles(this) , this.nbProfileQuery, this.nbTheme) ;;
		this.qryProfiles.setValues( pQry );
	}

	/**
	 * Transform a simulation into the data stored in its config file
	 * @return the contain of the config file related to the current simulation
	 */
	public String getConfig(){
		String res = "";

		res += SIM_NAME + " " + this.getName() + "\n";
		res += NB_PEER + " " +this.getPeersNb()+ "\n";
		//res += NB_DOC + " " +this.getNbDocument( )+ "\n";
		res += NB_QRY + " " + this.getQueriesNb()+ "\n";
		res += DEGREE + " " + this.getNeighborsNb()+ "\n";
		//res += NB_THEME+ " " +this.getNbTheme( )+ "\n";
		//res += NB_PROFILE_DOC + " " +this.getNbProfileDoc( )+ "\n";
		//res += NB_PROFILE_QRY + " " +this.getNbProfileQuery( )+ "\n";
		res += TTL + " " +this.getTtl( )+ "\n";
		res += TOPOLOGY + " " + this.getTopologyStrategy() + "\n";
		//res += DOC_DISTRIB + " " +this.getDocDistrib() + "\n";
		//res += QRY_DISTRIB + " " +this.getQryDistrib() + "\n";
		res += DYNAMICS + " " + this.isDynNetwork()+ "\n";
		res += EXP_CYCLE + " " + this.isExperimentsByCycle( )+ "\n";
		res += NB_CYCLES + " " + this.getCyclesNb()+ "\n";
		res += PROTOCOLES + " " + this.getProtocols()+ "\n";
		//res += Q_BASED_D + " " +this.isQryBasedDoc( )+ "\n";
		//res += this.getConfigDocProfiles() + "\n";
		//res += this.getConfigQryProfiles() + "\n";
		//res += this.getConfigThemes() + "\n";
		res += NODE + " " + this.getNodeType();

		return res;
	}

	public String getConfig1(){
		String res = "";

		res +="networkName " + this.getName() + "\n";
		res +="random.seed 1234567890" +"\n";
		res +="simulation.cycles" + " " + this.getCyclesNb() + "\n";
		res +="network.size " +this.getPeersNb()+ "\n";
		//res +="init.needinit.nbrequetes" + " " +nbQuery+ "\n";
		res +="simulneedinit.needinit.nbrequetes" + " " +this.getQueriesNb()+ "\n";
		res +="ttl" + " " +this.getTtl()+ "\n";
		//Ajout Lionel :
		res += "network.node" + " " + this.getNodeType()+ "\n";

		ArrayList<String> listProtocols = new ArrayList<String>(); 

		BufferedReader buff = null;
		String line = "";

		try{
			buff = new BufferedReader( new FileReader( ParamLoader.getProtocolsList(this) ) );
			while( buff.ready()){
				line = buff.readLine();
				listProtocols.add(  line );
				res += line + "\n";
			}

			buff.close();
		}catch( IOException ioe ){ioe.printStackTrace();}

		return res;
	}
	/**
	 * transform the set of document profiles into a string 
	 * @return the set of document profile name designed for config file insertion
	 */
	//public String getConfigDocProfiles(){
	//		try{
	//		return this.getConfigLabel( this.docProfiles.getProfiles() , DOC_PRO_NAME );
	//		}catch(Exception e){
	//			return "";
	//		}
	//	}

	/**
	 * transform the set of query profiles into a string
	 * @return the set of query profile name designed for config file insertion
	 */

	public String getConfigQryProfiles(){

		try{
			return this.getConfigLabel( this.qryProfiles.getProfiles() , QRY_PRO_NAME );
		}catch(Exception e){
			return "";
		}
	}

	/**
	 * transform the set of themes into a string 
	 * @return the set of themes name designed for config file insertion
	 */
	public String getConfigThemes(){
		try{
			return this.getConfigLabel( this.docProfiles.getThemes() , TH_NAME );
		}catch(Exception e){
			return "";
		}

	}

	/**
	 * transform an array of string into a string preffixed by the amorce
	 * @param tab the array of values
	 * @param amorce the first element of the resulted string
	 * @return
	 */
	private String getConfigLabel( String[] tab , String amorce ){
		String res = "";
		String[] vals = tab;
		res += amorce + " ";
		for( int i = 0 ; i < vals.length ; i ++ ){
			res += vals[i] + ";";
		}
		return res.substring(0, res.length() - 1) ;
	}

	/**
	 * transform the set of document profiles values into a string  
	 * @return the set of document profile values designed for config file insertion
	 */
	public String getValuesDocProfiles(){
		return this.getValuesProfiles( this.docProfiles.getValues());
	}

	/**
	 * transform the set of query profiles values into a string  
	 * @return the set of query profile values designed for config file insertion
	 */
	public String getValuesQryProfiles(){
		return this.getValuesProfiles( this.qryProfiles.getValues());
	}

	/**
	 * transform a matrix into a string 
	 * @param vals matrix values
	 * @return
	 */
	private String getValuesProfiles(int[][] vals){
		String res = "";
		String line = "";

		for( int i = 0 ; i < vals.length ; i ++ ){
			line = "";
			for( int j = 0 ; j < vals[i].length ; j ++ ){
				line += vals[i][j] + ";";
			}
			res += line.substring(0, line.length() - 1) + "\n";	
		}
		//SOP.prt(res);
		return res;
	}

	public void loadTopologyStrategy( String v ){
		for( int i = 0 ; i < this.topologyStrategies.length ; i++ ){
			if( this.topologyStrategies[i].equals(v)){
				this.topology =  this.topologyStrategies[i];
				break;
			}
		}
	}

	private void loadDocDistribution( String v ){
		for( int i = 0 ; i < this.distribNbDocByNode.length ; i++ ){
			if( this.distribNbDocByNode[i].equals(v)){
				this.docDistrib =  this.distribNbDocByNode[i];
				break;
			}
		}
	}

	private void loadQryDistribution( String v ){
		for( int i = 0 ; i < this.distribNbQueryByNode.length ; i++ ){
			if( this.distribNbQueryByNode[i].equals(v)){
				this.qryDistrib =  this.distribNbQueryByNode[i];
				break;
			}
		}
	}

	////// GETTERS AND SETTERS
	// <editor-fold desc="accesseurs">
	public DocumentProfiles getDocProfiles() {
		return docProfiles;
	}

	public void setDocProfiles(DocumentProfiles docProfile) {
		this.docProfiles = docProfile;
	}

	public QueryProfiles getQryProfiles() {
		return qryProfiles;
	}

	public void setQryProfiles(QueryProfiles qryProfiles) {
		this.qryProfiles = qryProfiles;
	}

	public String getFileUrl() {
		return urlFile;
	}

	public void setFileUrl(String urlFile) {
		this.urlFile = urlFile;
	}

	public int getNbDocument() {
		return nbDocument;
	}

	public void setNbDocument(int nbDocument) {
		this.nbDocument = nbDocument;
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

	public int getNbProfileDoc() {
		return nbProfileDoc;
	}

	public void setNbProfileDoc(int nbProfileDoc) {
		this.nbProfileDoc = nbProfileDoc;
	}

	public int getNbProfileQuery() {
		return nbProfileQuery;
	}

	public void setNbProfileQuery(int nbProfileQuery) {
		this.nbProfileQuery = nbProfileQuery;
	}

	public int getQueriesNb() {
		return nbQuery;
	}

	public void setQueriesNb(int nbQuery) {
		this.nbQuery = nbQuery;
	}

	public int getNbTheme() {
		return nbTheme;
	}

	public void setNbTheme(int nbTheme) {
		this.nbTheme = nbTheme;
	}


	public boolean isSourceQuery() {
		return sourceQuery;
	}


	public void setSourceQuery(boolean sourceQuery) {
		this.sourceQuery = sourceQuery;
	}


	public int getTtl() {
		return ttl;
	}


	public void setTtl(int ttl) {
		this.ttl = ttl;
	}


	public String[] getDistribNbDocByNode() {
		return distribNbDocByNode;
	}

	public void setDistribNbDocByNode(String[] distribNbDocByNode) {
		this.distribNbDocByNode = distribNbDocByNode;
	}

	public String[] getDistribNbQueryByNode() {
		return distribNbQueryByNode;
	}

	public void setDistribNbQueryByNode(String[] distribNbQueryByNode) {
		this.distribNbQueryByNode = distribNbQueryByNode;
	}

	public String[] getTopologyStrategies() {
		return topologyStrategies;
	}

	public void setTopologyStrategies(String[] topologyStrategies) {
		this.topologyStrategies = topologyStrategies;
	}

	public String getName() {
		return simulName;
	}

	public void setName(String simulName) {
		this.simulName = simulName + simulationNb++;
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


	public boolean isQryBasedDoc() {
		return qryBasedDoc;
	}


	public void setQryBasedDoc(boolean qryBasedDoc) {
		this.qryBasedDoc = qryBasedDoc;
	}

	public String getTopologyStrategy() {
		return topology;
	}

	public void setTopologyStrategy(String topology) {
		this.topology = topology;
	}

	public String getDocDistrib() {
		return docDistrib;
	}

	public void setDocDistrib(String docDistrib) {
		this.docDistrib = docDistrib;
	}

	public String getQryDistrib() {
		return qryDistrib;
	}

	public void setQryDistrib(String qryDistrib) {
		this.qryDistrib = qryDistrib;
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
	// </editor-fold>
}

//int[] query_profilQuery=null; // utile seulement si sourceQuery = false
//int[][] profilDoc_theme =null;
//int[] doc_profilDoc = null;

/*
public int[][] getProfilDoc_theme() {
	return profilDoc_theme;
}


public void setProfilDoc_theme(int[][] profilDoc_theme) {
	this.profilDoc_theme = profilDoc_theme;
}


public int[] getQuery_profilQuery() {
	return query_profilQuery;
}


public void setQuery_profilQuery(int[] query_profilQuery) {
	this.query_profilQuery = query_profilQuery;
}
 */

/*
public String[] getDistribNbDocByNode() {
	return distribNbDocByNode;
}


public void setDistribNbDocByNode(String[] distribNbDocByNode) {
	this.distribNbDocByNode = distribNbDocByNode;
}


public String[] getDistribNbQueryByNode() {
	return distribNbQueryByNode;
}


public void setDistribNbQueryByNode(String[] distribNbQueryByNode) {
	this.distribNbQueryByNode = distribNbQueryByNode;
}


public int[] getDoc_profilDoc() {
	return doc_profilDoc;
}


public void setDoc_profilDoc(int[] doc_profilDoc) {
	this.doc_profilDoc = doc_profilDoc;
}
 */