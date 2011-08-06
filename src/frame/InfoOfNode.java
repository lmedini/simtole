package frame;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class InfoOfNode extends JDialog {
	public int idNode;
	public String listVoisin =null;
	public String nbMapping;
	public String nbEquivalent;
	public String nbSubsomption;
	public String nbAllignement;
	public String uriPhysique;
	
	
	
	public String getUriPhysique() {
		return uriPhysique;
	}
	public void setUriPhysique(String uriPhysique) {
		this.uriPhysique = uriPhysique;
	}
	public InfoOfNode(int id){
		this.idNode = id;
	}
	public int getId(){
		return this.idNode;
	}
	public void setVoisins(String list){
		this.listVoisin = list;	
	}
	public void setNbMapping(String nbM){
		this.nbMapping = nbM;
	}
	public void setEquivalent(String nbE){
		this.nbEquivalent =nbE;
	}
	public void setNbSubsomption(String nbS){
		this.nbSubsomption = nbS;
	}
	public void setNbAllignement(String nbA){
		this.nbAllignement = nbA;
	}
	  public InfoOfNode() {
		    super((Frame) null, false);
		    
	  }
	  protected JRootPane createRootPane() {
		    JRootPane rootPane = new JRootPane();
		    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		    Action actionListener = new AbstractAction() {
		      public void actionPerformed(ActionEvent actionEvent) {
		        System.out.println("about to disappear");
		        setVisible(false);
		      }
		    };
		    InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		    inputMap.put(stroke, "ESCAPE");
		    rootPane.getActionMap().put("ESCAPE", actionListener);

		    return rootPane;
	  }
}
