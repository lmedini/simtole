package frame;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLTreeNode {
	Element element;
	
	public XMLTreeNode(Element element) {
		this.element = element;
	}
	public Element getElement() {
		return element;
	}
	public String toString() {
		String result = null;
		if (element.getNodeName().compareTo("scenarios")==0){
			result = element.getNodeName();
		}else if(element.getNodeName().compareTo("scenarioInfo") == 0){
			result = "Scenario : " + element.getAttribute("name"); 
		}else  {
			result = element.getNodeName() + " value is " + element.getAttribute("value");
		}
		return result;
		//return element.getAttribute("value");
	}
	public String getText() {
		NodeList list = element.getChildNodes();
		for (int i=0 ; i<list.getLength() ; i++) {
			if (list.item(i) instanceof Text) {
				System.out.println(((Text)list.item(i)).getTextContent());
				return ((Text)list.item(i)).getTextContent();
			}
		}
		return "";
	}
}
