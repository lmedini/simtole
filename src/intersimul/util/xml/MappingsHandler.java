package intersimul.util.xml;

import java.io.IOException;
import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import frame.InfoOfNode;

public class MappingsHandler extends DefaultHandler {
	private int nodeId;
	private Hashtable<Integer, InfoOfNode> listInfoNode ;
	private InfoOfNode infoNode;
	private String cdata;

	public MappingsHandler(){
		super();
		listInfoNode = new Hashtable<Integer, InfoOfNode>();
	}

    public void startElement (String uri, String name,
			      String qName, Attributes atts)
    {
		if (qName.equals("nodeInfo")){
			nodeId = Integer.parseInt(atts.getValue(0));
			infoNode = new InfoOfNode(nodeId);
			
		}
	}    

    public void endElement (String uri, String name, String qName)
    {
    	//System.out.println("Qname:" + qName);

    	if(qName.equals("nodeInfo")){
			listInfoNode.put(infoNode.getId(), infoNode);
		}else if (qName.equalsIgnoreCase("voisins")) {
			this.infoNode.setVoisins(cdata);
		}else if (qName.equalsIgnoreCase("nbMapping")) {
			this.infoNode.setNbMapping(cdata);
		}else if (qName.equalsIgnoreCase("nbEquivalent")) {
			this.infoNode.setEquivalent(cdata);
		}else if (qName.equalsIgnoreCase("nbSubsomption")) {
			this.infoNode.setNbSubsomption(cdata);
		}else if (qName.equalsIgnoreCase("nbAllignement")) {
			this.infoNode.setNbAllignement(cdata);
		}
	}

    public void characters (char ch[], int start, int length)
    {
    	cdata = new String(ch,start,length);
    	
	//System.out.print("Characters:    \"");
//	for (int i = start; i < start + length; i++) {
//	    switch (ch[i]) {
//	    case '\\':
//		//System.out.print("\\\\");
//		break;
//	    case '"':
//		//System.out.print("\\\"");
//		break;
//	    case '\n':
//		//System.out.print("\\n");
//		break;
//	    case '\r':
//		//System.out.print("\\r");
//		break;
//	    case '\t':
//		//System.out.print("\\t");
//		break;
//	    default:
//		//System.out.print(ch[i]);
//		break;
//	    }
//	}
	//System.out.print("\"\n");
    }

    public Hashtable<Integer, InfoOfNode> parseXMLNodesInfo(String urlXML) throws SAXException{
    	XMLReader xr = XMLReaderFactory.createXMLReader();

		xr.setContentHandler(this);
		xr.setErrorHandler(this);

		try {
			xr.parse(urlXML);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return this.listInfoNode;
    }
}