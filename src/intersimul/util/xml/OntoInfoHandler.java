package intersimul.util.xml;

import java.io.IOException;
import java.util.Hashtable;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import frame.InfoOfNode;

public class OntoInfoHandler extends DefaultHandler {
	private InfoOfNode infoNodeOWL;
	private String cdata;
	private Hashtable<Integer, InfoOfNode> listInfoNodeOWL;

	public OntoInfoHandler(){
		super();
		listInfoNodeOWL = new Hashtable<Integer, InfoOfNode>();
	}

    public void endElement (String uri, String name, String qName)
    {
    	//System.out.println("Qname:" + qName);

    	if (qName.equalsIgnoreCase("ontoUri")){
			this.infoNodeOWL.setUriPhysique(cdata);
			//System.out.println("===>" + this.infoNodeOWL.getUriPhysique());
		}
		else if (qName.equalsIgnoreCase("nodeID")){
			this.infoNodeOWL = new InfoOfNode(Integer.parseInt(cdata));
			//System.out.println("===>" + this.infoNodeOWL.idNode);
			this.listInfoNodeOWL.put(this.infoNodeOWL.idNode,this.infoNodeOWL);
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

    public Hashtable<Integer, InfoOfNode> parseInfoNodeOWL(String urlXML) throws SAXException{
    	XMLReader xr = XMLReaderFactory.createXMLReader();

		xr.setContentHandler(this);
		xr.setErrorHandler(this);

		try {
			xr.parse(urlXML);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return this.listInfoNodeOWL;
    }
}