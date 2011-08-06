package intersimul.util.xml;

import intersimul.model.experiment.Document;
import intersimul.model.experiment.DocumentProfile;
import intersimul.model.experiment.Node;
import intersimul.model.experiment.Query;
import intersimul.model.experiment.QueryProfile;
import intersimul.model.experiment.Experiment;
import intersimul.model.experiment.Scenario;
import intersimul.model.experiment.StoredTheme;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class XMLPrinter {
	public static final String SPACE = "   ";
	String spc = "";
	FileWriter fw = null;

	public XMLPrinter( String url ) throws IOException {
		fw = new FileWriter( url );
	}

	public static void printXML(String type, Object o) {
		try {
			XMLPrinter xp = new XMLPrinter(type); // création d'un document
			if(o instanceof Experiment)
				xp.write((Experiment) o); // écriture du fichier avec la topologie
			else if(o instanceof Scenario)
				xp.write((Scenario) o); // écriture du scénario
			xp.close(); // fermeture du fichier XML
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write( Experiment simul ) throws IOException{
		String desc = "";
		String amorce = amorce(0);

		desc = amorce+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n";
		fw.write( desc );

		desc = amorce+ "<experiment name=\""+ simul.getName()  +"\" isbyCycle=\""+ simul.isExperimentsByCycle() +"\" >\n";
		fw.write( desc );

		this.writeNetwork( simul );
		//this.writeDocProfiles(eb);
		//this.writeQryProfiles(eb);

		desc = amorce + "</experiment>\n";
		fw.write( desc );
		//SOP.prt( desc );
	}

	public void write(Scenario sc) throws IOException{
		String desc = "";
		String amorce = amorce(0);

		desc = amorce+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n\n";
		fw.write( desc );
		//SOP.prt( desc );

		desc = amorce+ "<scenario mode=\""+ sc.getMode()  +"\" >\n";
		fw.write( desc );
		//SOP.prt( desc );

		this.writeScenario(sc);		

		desc = amorce + "</scenario>\n";
		fw.write( desc );
		//SOP.prt( desc );
	}

	private void writeScenario(Scenario sc) throws IOException{
		String desc = "";
		String amorce = amorce(1);

		desc = amorce + "<alignement>\n";
		fw.write( desc );

		desc = amorce + "<cycle id=\""+  sc.getIdCycle()  +"\" />\n";
		fw.write( desc );

		desc = amorce + "<source id=\""+  sc.getIdSource()  +"\" />\n";
		fw.write( desc );

		desc = amorce + "<cible id=\""+  sc.getIdCible()  +"\" />\n";
		fw.write( desc );

		desc = amorce + "</alignement>\n";
		fw.write( desc );
	}

	private void writeNetwork( Experiment simul ) throws IOException {

		String desc = "";
		String amorce = amorce(1);

		desc = amorce + "<network isStatic=\"" +  (!simul.isDynNetwork())  + "\" sizeMax=\""+ simul.getPeersNb()  +"\" >\n";
		fw.write( desc );
//		System.out.println("nodes.size() : " + simul.getNbPeer() + "desc : " + desc );

		for( int i = 0 ; i < simul.getPeersNb() ; i++ ){
			write( simul.getNode(i) );
		}

		desc = amorce + "</network>\n";
		fw.write( desc );
	}
//
//	private void writeDocProfiles( ExperimentBuilder eb ) throws IOException {
//		String desc = "";
//		String amorce = amorce(1);
//
//		ArrayList<DocumentProfile> profiles = eb.getDprofiles();
//
//		desc = amorce + "<documentProfiles>\n";
//		fw.write( desc );
//		//SOP.prt( desc );
//
//		for( int i = 0 ; i < profiles.size() ; i++ ){
//			write( profiles.get(i) );
//		}
//
//		desc = amorce + "</documentProfiles>\n";
//		fw.write( desc );
//		//SOP.prt( desc );
//	}
//
//	private void writeQryProfiles( ExperimentBuilder eb ) throws IOException {
//
//		String desc = "";
//		String amorce = amorce(1);
//
//		ArrayList<QueryProfile> profiles = eb.getQprofiles();
//
//		desc = amorce + "<queryProfiles>\n";
//		fw.write( desc );
//		//SOP.prt( desc );
//
//		for( int i = 0 ; i < profiles.size() ; i++ ){
//			write( profiles.get(i) );
//		}
//
//		desc = amorce + "</queryProfiles>\n";
//		fw.write( desc );
//		//SOP.prt( desc );
//	}

	private void write( Node n ) throws IOException {
		String desc = "";
		String amorce = amorce(2);

		desc = amorce+ "<node id=\""+ n.getNid()  +"\" >\n";
		fw.write( desc );
		//SOP.prt( desc );

		//this.writeDocuments( n );

		//this.writeQueries( n );

		this.writeNeighborhood( n );


		desc = amorce + "</node>\n";
		fw.write( desc );
		//SOP.prt( desc );
	}

	private void writeDocuments( Node n ) throws IOException {
		String desc = "";
		String amorce = amorce(3);

		desc = amorce + "<data nbDocuments=\"" + n.getNbDocuments()  + "\" >\n";
		fw.write( desc );
		//SOP.prt( desc );

		ArrayList<Document> docs = n.getDocuments();

		for( int i = 0 ; i < docs.size() ; i++ ){
			write( docs.get(i) );
		}

		desc = amorce + "</data>\n";
		fw.write( desc );
		//SOP.prt( desc );
	}


	private void write( Document d ) throws IOException {
		String desc = "";
		String amorce = amorce(4);

		desc = amorce + "<document id=\""+ d.getDid() +"\" profileid=\"" + d.getProfile().getPid() + "\" />\n";
		fw.write( desc );
		//SOP.prt( desc );
	}

	private void writeQueries( Node n ) throws IOException {
		String desc = "";
		String amorce = amorce(3);

		desc = amorce + "<queries nbQueries=\"" + n.getNbQueries()   + "\" >\n";
		fw.write( desc );
		//SOP.prt( desc );

		ArrayList<Query> qry = n.getQueries();

		for( int i = 0 ; i < qry.size() ; i++ ){
			write( qry.get(i) );
		}

		desc = amorce + "</queries>\n";
		fw.write( desc );
		//SOP.prt( desc );
	}

	private void write( Query q ) throws IOException {
		String desc = "";
		String amorce = amorce(4);

		desc = amorce + "<query id=\""+ q.getDid() +"\" ttl=\""+ q.getTtl() +"\" profileid=\"" + q.getProfile().getPid() + "\" starter=\""+ q.getCycleStart() +"\" isBasedOnLocalDoc=\""+ q.getBasedDocLocal() +"\" qType=\""+ q.getQtype() +"\"/>\n";
		fw.write( desc );
		//SOP.prt( desc );
	}

	private void writeNeighborhood( Node n ) throws IOException {
		String desc = "";
		String amorce = amorce(3);

		desc = amorce + "<neighbors>\n";
		fw.write( desc );
		//SOP.prt( desc );

		ArrayList<Node> neighbors = n.getNeighborhood();

		for( int i = 0 ; i < neighbors.size() ; i++ ){
			writeNeighbor( neighbors.get(i) );
		}

		desc = amorce + "</neighbors>\n";
		fw.write( desc );
		//SOP.prt( desc );
	}

	private void writeNeighbor( Node n ) throws IOException {
		String desc = "";
		String amorce = amorce(4);

		desc = amorce + "<neighbor id=\""+  n.getNid()  +"\" />\n";
		fw.write( desc );
		//SOP.prt( desc );
	}

	private void write( DocumentProfile  dp ) throws IOException {
		String desc = "";
		String amorce = amorce(3);

		desc = amorce + "<docProfile id=\""+ dp.getPid()  +"\" >\n";
		fw.write( desc );
		//SOP.prt( desc );

		ArrayList<StoredTheme> themes = dp.getThemes();

		for( int i = 0 ; i < themes.size() ; i++ ){
			write( themes.get(i) );
			fw.write("</theme>");
		}

		desc = amorce + "</docProfile>\n";
		fw.write( desc );
		//SOP.prt( desc );
	}

	private void write( QueryProfile  qp ) throws IOException {
		String desc = "";
		String amorce = amorce(3);

		desc = amorce + "<qryProfile id=\""+ qp.getPid()  +"\" >\n";
		fw.write( desc );
		//SOP.prt( desc );

		ArrayList<StoredTheme> themes = qp.getThemes();

		for( int i = 0 ; i < themes.size() ; i++ ){
			write( themes.get(i) );
			fw.write("</theme>");
		}

		desc = amorce + "</qryProfile>\n";
		fw.write( desc );
		//SOP.prt( desc );
	}

	private void write( StoredTheme st ) throws IOException {
		String desc = "";
		String amorce = amorce(4);

		desc = amorce + "<theme id=\""+  st.getCtheme().getTid()  +"\" percent=\""+  st.getProportion() +"\" >\n";
		fw.write( desc );
		//SOP.prt( desc );
	}

	public void close() throws IOException {
		fw.close();
	}

	/**
	 * Generates the spaces at the beginning of each line
	 * @param i number of spaces to generate
	 * @return a String containing the desired number of spaces
	 */
	private String amorce( int i){
		switch( i  ){
		case 0 : return "";
		case 1 : return SPACE;
		default : return SPACE + amorce( i-1 );
		}	
	}
}