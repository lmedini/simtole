package frame;

import java.io.File;

import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class TreeXML extends JFrame{

	private static final long serialVersionUID = 1L;

	public TreeXML() {
		Document document = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbFactory.newDocumentBuilder();
			document = builder.parse(new File("out\\Stats.xml"));
			document.normalize();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		TreePanel panel = new TreePanel();
		panel.setDocument(document);
		getContentPane().add(panel, "Center");
		pack();
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Result of simulations");
		setLocationRelativeTo(null);
		setVisible(true);
	}
}