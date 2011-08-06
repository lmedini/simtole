package intersimul.view;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import javax.swing.WindowConstants;

import intersimul.SimToleMain;
import intersimul.controller.SimToleController;
import intersimul.model.experiment.Experiment;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 * 
 * @author lumineau
 */

public class SFrame extends javax.swing.JFrame {
	private SimToleController controller;

	private JMenuBar jMenuBar1;
	private JMenu jMenu1;
	private JMenuItem jMenuItem1;
	private JMenuItem jMenuItem2;
	private JTabbedPane jTabSimulations;
	private JSeparator jSeparator1;

	/**
	 * Constructor
	 * 
	 */
	public SFrame(SimToleController controller) {
		super();
		this.controller = controller;
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
				jTabSimulations = new JTabbedPane();
				getContentPane().add(jTabSimulations, BorderLayout.CENTER);
			}
			{
				jMenuBar1 = new JMenuBar();
				setJMenuBar(jMenuBar1);
				{
					jMenu1 = new JMenu();
					jMenuBar1.add(jMenu1);
					jMenu1.setText("Simulation");
					{
						jMenuItem1 = new JMenuItem();

						jMenuItem1.setText("Exit");
						jMenuItem1.setBounds(0, 0, 53, 18);
						java.awt.event.ActionListener action = new java.awt.event.ActionListener() {
							public void actionPerformed(java.awt.event.ActionEvent e) {
								controller.exit();
							}
						};

						jMenuItem1.addActionListener(action);

					}

					{
						jMenuItem2 = new JMenuItem();
						jMenuItem2.setText("New experiment");
						jMenuItem2.setBounds(0, 0, 53, 20);
						java.awt.event.ActionListener action = new java.awt.event.ActionListener() {
							public void actionPerformed(
									java.awt.event.ActionEvent e) {
								controller.newExperiment();
							}
						};

						jMenuItem2.addActionListener(action);
					}
					{
						jMenu1.add(jMenuItem2);
						jSeparator1 = new JSeparator();
						jMenu1.add(jSeparator1);
						jMenu1.add(jMenuItem1);
					}
				}
			}
			pack();
			this.setSize(683, 454);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a new SPanel interface for simulation s
	 * @param s simulation instance to be added to the main SFrame
	 */
	public void addExperimentPanel(SPanel expeView) {
		this.jTabSimulations.add(expeView);
		expeView.setSf(this);
	}

	/**
	 * Delete the current experiment
	 * 
	 * @param sname Spanel to be removed from the main SFrame
	 */
	public void removeSimulationPanel(SPanel sPanel) {
		this.jTabSimulations.remove(sPanel);
	}

	/**
	 * Shows a dialog box and asks for the name of a new simulation
	 * @return the name entered in the box or null if nothing entered
	 */
	public String getNewExperimentName() {
		String name = null;
		name = JOptionPane.showInputDialog(this, "Experimentation name", "New experiment", JOptionPane.OK_CANCEL_OPTION);
		return name;
	}
}
