package frame;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class InfoScenario extends JDialog{
	public InfoScenario() {
	    super((Frame) null,"Informations of Current Scenario ", false);
	    
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
