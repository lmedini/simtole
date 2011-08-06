package fr.cnrs.liris.simtole.owl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import fr.cnrs.liris.simtole.InfoGlobal;
import fr.cnrs.liris.simtole.mapping.EquivalentMapping;
import fr.cnrs.liris.simtole.mapping.Mapping;
import fr.cnrs.liris.simtole.mapping.SubsomptionMapping;
import fr.cnrs.liris.simtole.protocols.InfoLocalProtocol;

import peersim.core.Linkable;
import peersim.core.Node;

/**
 * Classe à reprendre avec un vrai parser XML et un constructeur contenant le nom de l'élément XML root.
 *
 */
public class WriteXMLfunctions {
	
	public static final String SPACE = "   ";
	private FileWriter fw = null;
	
	public WriteXMLfunctions(String url) throws IOException{
		fw = new FileWriter( url );
	}
	
	public void writeHead(String s) throws IOException{
		String desc = "";
		String amorce = amorce(0);
		
		desc = amorce+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n\n";
		desc = desc+ amorce + "<"+s+">\n";
		fw.write( desc );
	}
	
	public void writeFin(String s) throws IOException{
		String desc = "";
		String amorce = amorce(0);
		
		//desc = amorce+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n\n";
		desc = amorce + "</"+s+">\n";
		fw.write( desc );
	}
	
	public void write(Node node, InfoLocalProtocol protocolInfo, Linkable linkable) throws IOException
	{
		String desc = "";
		String amorce = amorce(0);
		
		
		desc = amorce + "<nodeInfo id=\""+  node.getID()  +"\" >\n";
		fw.write( desc );
		
		this.write(linkable);
		this.write(protocolInfo);
		
		desc = amorce + "</nodeInfo>\n";
		
		fw.write( desc );
	}
	
	public void write(Linkable linkable) throws IOException
	{
		String desc = "";
		String amorce = amorce(1);
		
		desc = amorce + "<voisins> ";
		for(int i=0;i<linkable.degree();i++)
		{
			desc=desc+linkable.getNeighbor(i).getID()+" ";
		}
		desc = desc+"</voisins>\n";
		fw.write( desc );
	}
	
	public void write(Node node, InfoLocalProtocol infoProtocol) throws IOException
	{	if(infoProtocol.getMappingsCount()!=0)
		{
			String desc = "";
			String amorce = amorce(0);
			
			desc = amorce + "<node id=\""+  node.getID()  +"\" >\n";
			fw.write( desc );
			
			this.write(infoProtocol);
			
			desc = amorce + "</node>\n";
			fw.write( desc );
		}
	}
	
	public void write(InfoLocalProtocol protocolInfo) throws IOException
	{
		String desc = "";
		String amorce = amorce(1);
		
		desc = amorce + "<nbMapping>"+  protocolInfo.getNbMapping()  +"</nbMapping>\n";
		fw.write( desc );
		
		desc = amorce + "<nbEquivalent>"+  protocolInfo.getNbEquivalent()  +"</nbEquivalent>\n";
		fw.write( desc );
		
		desc = amorce + "<nbSubsomption>"+  protocolInfo.getNbSubsomption()  +"</nbSubsomption>\n";
		fw.write( desc );
		
		desc = amorce + "<nbAllignement>"+  protocolInfo.getNbAlignements()  +"</nbAllignement>\n";
		fw.write( desc );
		
		desc = amorce + "<Mapping>\n";
		fw.write( desc );

		for(int i=0; i<protocolInfo.getMappingsCount(); i++) {
			write(protocolInfo.getMapping(i));
		}

		desc = amorce + "</Mapping>\n";
		fw.write( desc );
	}
	
	public void write(Mapping mapping) throws IOException {	
		String desc = "";
		String amorce = amorce(2);
		String desc2 = "";
		String amorce2 = amorce(3);
		if(mapping instanceof EquivalentMapping) {
			desc = amorce + "<EquivalentMapping>\n";
			fw.write( desc );
			desc2 = amorce2 + "<uriMyClass>"+  mapping.getLocalOntologyName()  +"</uriMyClass>\n";
			fw.write( desc2 );
			desc2 = amorce2 + "<uriDistantClass>"+  mapping.getDistantOntologyName()  +"</uriDistantClass>\n";
			fw.write( desc2 );

			desc = amorce + "</EquivalentMapping>\n";
			fw.write( desc );		
		} else if(mapping instanceof SubsomptionMapping) {
			desc = amorce + "<SubsomptionMapping>\n";
			fw.write( desc );

			desc2 = amorce2 + "<uriMyClass>"+  mapping.getLocalOntologyName()  +"</uriMyClass>\n";
			fw.write( desc2 );
			desc2 = amorce2 + "<uriDistantClass>"+  mapping.getDistantOntologyName()  +"</uriDistantClass>\n";
			fw.write( desc2 );
			desc2 = amorce2 + "<relation>"+  ((SubsomptionMapping) mapping).getRelation()  +"</relation>\n";
			fw.write( desc2 );

			desc = amorce + "</SubsomptionMapping>\n";
			fw.write( desc );		
		}
	}
	
	public void writeChemin(ArrayList<Node> chemin) throws IOException
	{
		String desc = "";
		String amorce = amorce(1);
		
		desc = amorce + "<chemin>\n";
		fw.write( desc );

		String ss=""+amorce(2);
		for(int i=0;i<chemin.size();i++)
		{
			ss = ss + chemin.get(i).getID()+" ";
		}
		ss=ss+"\n";
		fw.write(ss);
		desc = amorce + "</chemin>\n";
		fw.write( desc );

	}
	
	public void writeInfoGlobal(String nameSce) throws IOException
	{
		
		String desc = "";
		String amorce = amorce(1);
		
		desc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n\n";
		fw.write( desc );
		desc = amorce + "<scenarioInfo name=\"" + nameSce + "\" >\n";
		desc = desc + amorce+ "<nbMessages value=\"" + InfoGlobal.nbMessage + "\" />\n";
		fw.write( desc );

//		String ss=""+amorce(2);
//		ss=ss+InfoGlobal.nbMessage+"\n";
//		fw.write(ss);
//		desc = amorce + "</nbMessages>\n";
//		fw.write( desc );
		
		desc = amorce+ "<nbMappingInit value=\"" + InfoGlobal.nbMappingInit + "\" />\n";
		fw.write(desc);
		
//		desc = amorce+"</nbMappingInit>\n";
//		fw.write(desc);
		
		desc = amorce+ "<nbMappingFinal value=\"" + InfoGlobal.nbMapping + "\" />\n";
		fw.write(desc);
		
		desc = amorce+"</scenarioInfo>\n";
		fw.write(desc);
		
	}
	
	public void writeOntoInfo(int i, String uri) throws IOException
	{
		String desc = "";
		String amorce = amorce(1);
		
		desc = amorce + "<ontoInfo>\n";
		
		desc = desc + amorce(2) + "<nodeID>"+  i  +"</nodeID>\n";
		desc = desc + amorce(2) + "<ontoUri>"+  uri  +"</ontoUri>\n";
		desc = desc + amorce(1) + "</ontoInfo>\n";
		fw.write( desc );
		
	}
	
	private String amorce( int i){
		
		switch( i  ){
		case 0 : return "";
		case 1 : return SPACE;
		default : return SPACE + amorce( i-1 );
		}	
	}

	public void close() {
		try {
			fw.close();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}
