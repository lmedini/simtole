package intersimul.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import frame.GraphSimple;
import frame.SimpleGraphView;

public class DPanel extends JPanel {
	
	String[] infos = null;
	JList lesData = null;
	
	public DPanel() {
		super();
		initGUI();
	}
	
	
	public DPanel( String[] lst ){
		this();
		this.infos = lst;
		
	}
	
	
	
	
	private void initGUI(){
		
		{				
			
			this.lesData = new JList(this.infos);
			
			
			
			this.lesData.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					
					//TODO
					System.out.println(">" + infos[lesData.getSelectedIndex()]);
							
				}

			});

			
			
			
		}
	}

}
