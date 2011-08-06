package intersimul.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import frame.GraphSimple;
import frame.SimpleGraphView;

/**
 * Interface containing the network graph.
 *
 */
public class NetworkGraphView extends JPanel {
	private static final long serialVersionUID = 1L;
	private JComboBox lesModes;
	private JPanel graphe;
	private JPanel cmd;
	GraphSimple gs ;
	SimpleGraphView sgv;
	
	public NetworkGraphView() {
		super();
		initGUI();
	}
	
	
	public NetworkGraphView( SimpleGraphView sgv ){
		this();
		String[] modes = new String[3];
		modes[0] = "Transforming";
		modes[1] = "Picking";
		
		ComboBoxModel cbModeModel = new DefaultComboBoxModel(modes);
		lesModes.setModel(cbModeModel);
		
		this.sgv = sgv;
		
		this.graphe.add( sgv.vv );
		
	}
	
	
	
	
	private void initGUI(){
		
		{				
			this.graphe = new JPanel();
			//this.graphe.setBounds(297, 12, 42, 472);
			this.cmd = new JPanel();
			//this.cmd.preferredSize()eferredSize( new Dimension( 492 , 22 ) );
			this.lesModes = new JComboBox();
			
			
			this.cmd.add(this.lesModes);
			
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(this.graphe);
			this.add(this.cmd);
			
			lesModes.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
							sgv.setMode( lesModes.getSelectedIndex() );
				}
			});
		}
	}
}
