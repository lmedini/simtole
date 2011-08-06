package intersimul.util.xml;

import intersimul.model.experiment.SimulationResults;

import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class StatisticsHandler extends DefaultHandler {
	private SimulationResults simRes = null;
	private ArrayList<SimulationResults> listSce;

	public StatisticsHandler(){
		super();
		listSce = new ArrayList<SimulationResults>();
	}

    public void startElement (String uri, String name, String qName, Attributes atts) {
    	//System.out.println(atts.getValue(0));

    	if (qName.equals("scenarioInfo")){
			simRes = new SimulationResults(null);
			simRes.setName(atts.getValue(0));
		}
    	if (qName.equals("nbMessages")){
			simRes.setNbMessages(atts.getValue(0));
		}
		if (qName.equals("nbMappingInit")){
			simRes.setMappingInit(atts.getValue(0));
		}
		if (qName.equals("nbMappingFinal")){
			simRes.setMappingFinal(atts.getValue(0));
		}
	}    

    
    public void endElement (String uri, String name, String qName) {
    	//System.out.println("Qname:" + qName);

    	if (qName.equalsIgnoreCase("scenarioInfo")) {
			this.listSce.add(simRes);
		}
    }

    public ArrayList<SimulationResults> parseXMLStatistics(String urlXML) throws SAXException{
    	XMLReader xr = XMLReaderFactory.createXMLReader();

		xr.setContentHandler(this);
		xr.setErrorHandler(this);

		try {
			xr.parse(urlXML);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return this.listSce;
    }
}