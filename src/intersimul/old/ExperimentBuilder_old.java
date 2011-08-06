package intersimul.old;

import intersimul.model.experiment.Document;
import intersimul.model.experiment.DocumentProfile;
import intersimul.model.experiment.Node;
import intersimul.model.experiment.ParamLoader;
import intersimul.model.experiment.Query;
import intersimul.model.experiment.QueryProfile;
import intersimul.model.experiment.Experiment;
import intersimul.model.experiment.StoredTheme;
import intersimul.model.experiment.Theme;
import intersimul.model.experiment.UnknownTopologyException;

import java.util.ArrayList;
import java.util.Random;

public class ExperimentBuilder_old {

	Experiment simulation = null;

	ArrayList<Theme> themes = null;
	ArrayList<DocumentProfile> dprofiles = null;
	ArrayList<QueryProfile> qprofiles = null;
	ArrayList<Node> nodes = null;
	ArrayList<Document> documents = null;
	ArrayList<Query> queries = null; 

	int chConfig = 0;
	Random r;

	public ExperimentBuilder_old(Experiment simul) {
		this.simulation = simul;
		r = new Random(124);
	}

	/**
	 * Main method creating experimentation 
	 *
	 */
	public void build( ){
		System.out.println("\n*** Building experiment.");
		try{	
//			this.loadThemes();
//			SOP.prtln("themes loaded.");
//
//			this.loadDocProfiles();
//			SOP.prtln("Document profiles loaded.");
//
//			this.loadQryProfiles();
//			SOP.prtln("Queries profiles loaded.");
//
//			this.createDocumentAndQueries();
//			SOP.prtln("Documents created.");
//			SOP.prtln("Queries created.");

			this.loadNodes();
			System.out.println(" Nodes created.");

//			this.distributeDocumentsAndQueries();
//			SOP.prtln("Documents loaded.");
//			SOP.prtln("Queries loaded.");

			this.loadTopology();
			System.out.println(" Topology defined.");
		
		} catch(UnknownTopologyException uce){
			uce.printStackTrace();
		}
	}

	/**
	 * Method updating topology
	 */
	public void rebuild(){
		try{
			// TODO Mettre ici l'algo de génération de la topologie	
			this.updateTopology();
			System.out.println("\n*** Topology redefined ***");
		
		} catch(UnknownTopologyException uce){
			uce.printStackTrace();
		}
	}

	/**
	 * Load themes
	 * @param s : current simulation
	 */
	private void loadThemes(   ){
//		String[] pnames = this.simulation.getDocProfiles().getThemes();
//		this.themes = new ArrayList<Theme>(pnames.length);
//		for( int i = 0 ; i < pnames.length ; i++ ){
//			this.themes.add( i ,  new Theme( i , pnames[i] ));
//		}
	}

	private void loadDocProfiles(){
//		String[] pnames = this.simulation.getDocProfiles().getProfiles();
//		int[][] vals = this.simulation.getDocProfiles().getValues();
//		int cval = -1;
//		DocumentProfile cdp = null;
//		this.dprofiles = new ArrayList<DocumentProfile>(pnames.length);
//		for( int i = 0 ; i < pnames.length ; i++ ){
//			cdp = new DocumentProfile( i , pnames[i]);
//			for( int j = 0 ; j < vals[i].length ; j++ ){
//				cval = vals[i][j];
//				if( cval > 0 ){
//					cdp.addThemes( new StoredTheme( this.themes.get(j) , cval ));
//				}
//			}
//			this.dprofiles.add(i , cdp );
//		}
	}

	/**
	 * Génère les profiles de requêtes à partir de la simulation courante
	 *
	 */
	private void loadQryProfiles(){
//		// récupération des nom de profiles
//		String[] pnames = this.simulation.getQryProfiles().getProfiles();
//		// récupérations des proportions de thèmes dans les profils
//		int[][] vals = this.simulation.getQryProfiles().getValues();
//		// pour référencer la valeur courante
//		int cval = -1;
//		// pour référencer le profil requête courant
//		QueryProfile cdp = null;
//		
//		// instranciation de la liste de profile de requêtes
//		this.qprofiles = new ArrayList<QueryProfile>(pnames.length);
//		this.qprofiles.clear();
//
//		// création d'un profil de requête pour chaque nom stocké dans pnames
//		for( int i = 0 ; i < pnames.length ; i++ ){
//			// instanciation du ième profile de requête 
//			cdp = new QueryProfile( i , pnames[i]);
//			
//			// affectation des thèmes et de leuir proportions dans le profil de requête
//			for( int j = 0 ; j < vals[0].length ; j++ ){
//				
//				cval = vals[i][j]; // proportion du jème thème dans le ième profile de requête
//				
//				if( cval > 0 ){
//					
//					// association du jème thème avec une proportion de 'cval'% dans le profil 
//					// de requête couorant
//					cdp.addThemes( new StoredTheme( this.themes.get(j) , cval ));
//				}
//			}
//			// ajout du profil de requête courant dans la liste des profils de requêtes
//			this.qprofiles.add(i , cdp );
//		}
	}

	public void createDocumentAndQueries() throws UnknownTopologyException{
//		this.createDocuments( this.getProfileByDocument( ) );
		this.createQueries( this.getProfileByQuery() );
	}

	private void createDocuments( int[] dProfileByDoc ){
		Document cDocument = null;
		this.documents = new ArrayList<Document>();
		for( int i = 0 ; i < dProfileByDoc.length ; i++ ){
			cDocument = new Document( i  ,  this.dprofiles.get( dProfileByDoc[i]) );
			this.documents.add(i , cDocument );
			}
		}

	private void createQueries( int[] qProfileByQry ){
		Query cQuery = null;
		this.queries = new ArrayList<Query>();
		for( int i = 0 ; i < qProfileByQry.length ; i++ ){
			cQuery = new Query( i  ,  this.qprofiles.get( qProfileByQry[i]) , "simple" , "true" , this.simulation.getTtl());
			this.queries.add(i , cQuery );
			}
		}

	private void createQueries( int[] qProfileByQry, String[] typeByQry , String[] isLocalSourceByQry , int[] cycleStarterByQry){
		Query cQuery = null;
		this.queries = new ArrayList<Query>();
		for( int i = 0 ; i < qProfileByQry.length ; i++ ){
			cQuery = new Query(i, this.qprofiles.get(qProfileByQry[i]), typeByQry[i], isLocalSourceByQry[i], cycleStarterByQry[i]);
			this.queries.add(i , cQuery );	
		}
	}

	private void loadNodes(){
		Node cnode = null;
		this.nodes = new ArrayList<Node>(this.simulation.getPeersNb());
		for( int i = 0 ; i < this.simulation.getPeersNb() ; i++ ) {
			cnode = new Node( i );
			this.nodes.add(i , cnode);
		}
	}

	private void loadTopology() throws UnknownTopologyException {
		String cTopology = 	this.simulation.getTopologyStrategy();
		if(cTopology.equals(ParamLoader.RANDOM)){
			this.randomTopology();
		} else{
			throw new UnknownTopologyException();
		}
	}

	private void updateTopology() throws UnknownTopologyException {
		String cTopology = 	this.simulation.getTopologyStrategy();
		if(cTopology.equals(ParamLoader.RANDOM)){
			this.randomTopology();
		} else{
			throw new UnknownTopologyException();
		}
	}

	public String getName(){
		return this.simulation.getName();
	}

	public String isByCycle(){
		return "" + this.simulation.isExperimentsByCycle();
	}

	public String isStatic(){
		return "" + (!this.simulation.isDynNetwork());
	}

	public ArrayList<DocumentProfile> getDprofiles() {
		return dprofiles;
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public ArrayList<QueryProfile> getQprofiles() {
		return qprofiles;
	}

	public Experiment getSimulation() {
		return simulation;
	}

	public ArrayList<Theme> getThemes() {
		return themes;
	}

	public ArrayList<Document> getDocuments() {
		return documents;
	}

	public ArrayList<Query> getQueries() {
		return queries;
	}

	//////////////////////////// A IMPLENTER //////////////////////////////

//	public int[] getProfileByDocument()throws UnknownTopologyException {
//		switch( this.chConfig ){
//		case 0 : return testingGetProfileByDocument(); 
//		case 1 : 
//		default : throw new UnknownTopologyException();  
//		}
//	}

	public int[] getProfileByQuery() throws UnknownTopologyException {
		switch( this.chConfig ){
		case 0 : return testingGetProfileByQuery(); 
		case 1 : 
		default : throw new UnknownTopologyException();  
		}
	}

	private void distributeDocumentsAndQueries() throws UnknownTopologyException {
		switch( this.chConfig ){
		case 0 : testingDistributionOfDocumentsAndQueries(); return;
		case 1 : 
		default : throw new UnknownTopologyException();  
		}
	}

	/////////////////////////////////// TOPOLOGY ///////////////////////////

	private void randomTopology(){
		int n = this.nodes.size();
		int k = this.simulation.getNeighborsNb();
		ArrayList<Node> voisins = null;
		if( n < 2 )
			return;

		if( n <= k )
			k=n-1;

		int[] nodes = new int[n];
		for(int i=0; i<nodes.length; ++i)
			nodes[i]=i;

		int j=0;
		int newedge = 0;
		int tmp = 0;
		for(int i=0; i<n; ++i) {
			j=0;
			voisins = new ArrayList<Node>(k);
			voisins.clear();
			while(j<k) {
				newedge = j+r.nextInt(n-j);
				tmp = nodes[j];
				nodes[j] = nodes[newedge];
				nodes[newedge] = tmp;
				if( nodes[j] != i ) {
				//	SOP.prtln("N" + this.nodes.get(i).getNid() + " --> N" + this.nodes.get(nodes[j]).getNid());
					voisins.add( this.nodes.get(nodes[j]));
					j++;
				}
			}
			this.nodes.get(i).setNeighborhood( voisins );
		}
	}
	
	////////////////////////////////// Pour tests //////////////////////////

	private void testingDistributionOfDocumentsAndQueries(){
		//attribution des documents
		Node cnode = null;
		ArrayList<Document> hisDoc = null;
		ArrayList<Query> hisQry = null;
		int nbDocByNode =  this.documents.size() / this.nodes.size();
		int cpt = 0;
		for( int i = 0 ; i < this.nodes.size() ; i++ ){
			
			cnode = this.nodes.get(i);
			hisDoc = new ArrayList<Document>();
			hisDoc.clear();
			
			if( i == this.nodes.size() - 1 ){
				nbDocByNode = this.documents.size() - cpt;
			}
			
			for( int j = 0 ; j < nbDocByNode ; j++ ){
				hisDoc.add( this.documents.get(cpt) );
				cpt++;
			}
			//SOP.prtln( "N" + cnode.getNid() + " : #doc = " + hisDoc.size()  );
			cnode.loadDocument( hisDoc );
		}

		//attribution des requêtes
		cpt = 0;
		int nbQryByNode =  this.queries.size() / this.nodes.size();
		for( int i = 0 ; i < this.nodes.size() ; i++ ){
		
			cnode = this.nodes.get(i);
			hisQry = new ArrayList<Query>();
			hisQry.clear();
			
			if( i == this.nodes.size() - 1 ){
				nbQryByNode = this.queries.size() - cpt;
			}
			
			for( int j = 0 ; j < nbQryByNode ; j++ ){
				hisQry.add( this.queries.get(cpt) );
				cpt++;
			}
		
			//SOP.prtln( "N" + cnode.getNid() + " : #qry = " + hisQry.size()  );
			cnode.loadQuery( hisQry );
		}
	}

//	private int[] testingGetProfileByDocument(){
//		
//		int[] tab = new int[ this.simulation.getNbDocument() ];
//		int nbProfile = this.dprofiles.size();
//		
//		for( int i = 0 ; i < tab.length ; i ++ ){
//			tab[i] = i % nbProfile; 
//		}
//		return tab;
//	}

private int[] testingGetProfileByQuery(){
		
		int[] tab = new int[ this.simulation.getQueriesNb() ];
		int nbProfile = this.qprofiles.size();
		
		for( int i = 0 ; i < tab.length ; i ++ ){
			tab[i] = i % nbProfile; 
		}
		return tab;
	}
}