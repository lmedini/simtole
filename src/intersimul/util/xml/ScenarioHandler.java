package intersimul.util.xml;

import intersimul.model.experiment.Scenario;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


public class ScenarioHandler extends DefaultHandler {
	private Scenario sc = null;
	private Scenario sc1 = null;

	public ScenarioHandler(){
		super();
		sc = new Scenario(null, -1, -1 , -1);
	}

    public Scenario parseXMLScenario(String urlXML) throws SAXException{
    	XMLReader xr = XMLReaderFactory.createXMLReader();
		
		xr.setContentHandler(this);
		xr.setErrorHandler(this);
		
		try {
			xr.parse(urlXML);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return this.sc;
    }

//  public ArrayList<Scenario> parseXMLScenarioInfo(String urlXML) throws SAXException{
//	XMLReader xr = XMLReaderFactory.createXMLReader();
//
//	xr.setContentHandler(this);
//	xr.setErrorHandler(this);
//
//	try {
//		xr.parse(urlXML);
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
//	return listSce;
//}

    public void startElement (String uri, String name, String qName, Attributes atts)
    {
		if (qName.equals("scenario")){
			//System.out.println(atts.getValue(0));
			sc.setMode(atts.getValue(0));
		}		if (qName.equals("scenarioInfo")){
			sc1 = new Scenario(null, -1, -1 , -1);
			//System.out.println(atts.getValue(0));

			sc1.setName(atts.getValue(0));
		}
		if (qName.equals("cycle")){
			sc.setIdCycle(Integer.parseInt(atts.getValue(0)));
		}
		if (qName.equals("source")){
			sc.setIdSource(Integer.parseInt(atts.getValue(0)));
		}
		if (qName.equals("cible")){
			sc.setIdCible(Integer.parseInt(atts.getValue(0)));
		}
	}
}