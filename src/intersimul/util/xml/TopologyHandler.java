package intersimul.util.xml;

import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class TopologyHandler extends DefaultHandler {
	private ArrayList<ArrayList<Integer>> tableNode; 
	private int nodeId;
	private ArrayList<Integer> nodeNeighbor;

	public TopologyHandler(){
		super();
		tableNode = new ArrayList<ArrayList<Integer>>();
	}

    public void startElement (String uri, String name,
			      String qName, Attributes atts)
    {
		if (qName.equals("node")){
//			System.out.println("Start element: " + qName + " Attribute " 
//					+atts.getQName(0) + " Value " + atts.getValue(0));
			nodeId = Integer.parseInt(atts.getValue(0));
			nodeNeighbor = new ArrayList<Integer>();

		}
		if (qName.equals("neighbor")){
//			System.out.println("Start element: " + qName + " Attribute " 
//					+atts.getQName(0) + " Value " + atts.getValue(0));
			nodeNeighbor.add(Integer.parseInt(atts.getValue(0)));			
		}
	}    

    
    public void endElement (String uri, String name, String qName)
    {
    	//System.out.println("Qname:" + qName);
		if (qName.equals("node")){
			//System.out.println("test" +  nodeId);
			tableNode.add(nodeId, nodeNeighbor);
			//System.out.println("End element: " + qName );
			nodeNeighbor = null;
		}
	}

    public ArrayList<ArrayList<Integer>> parseXMLTopology(String urlXML) throws SAXException{
    	XMLReader xr = XMLReaderFactory.createXMLReader();
		
		xr.setContentHandler(this);
		xr.setErrorHandler(this);
		
		try {
			xr.parse(urlXML);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.tableNode;
    }
}