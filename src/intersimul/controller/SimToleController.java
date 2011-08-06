package intersimul.controller;

import intersimul.model.SimToleModel;
import intersimul.model.experiment.ParamLoader;
import intersimul.view.SFrame;
import intersimul.view.SPanel;

import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * Controller of the application main layer: initializes the model and the main window.
 * @author Lionel Médini
 *
 */
public class SimToleController {
	private static SimToleModel model;
	private static SFrame view;

	/**
	 * Launches the controller between the main SFrame (window containing all the experiments) and its model.
	 */
	public SimToleController() {
        // Initialisation de la fenêtre principale
	    System.out.println("Initialisation de la fenêtre principale de l'application.");
        view = initMainInterface();

        // Chargement des simulations
	    System.out.println("Chargement des expérimentations : " + ParamLoader.getBasePath());
        model = new SimToleModel(this);
        model.run();
	}

	/**
	 * Creates the main SFrame containing all experiments
	 * @return a new SFrame (application window) object 
	 */
	private SFrame initMainInterface(){
		// création de la Frame principale
		SFrame f = new SFrame(this);

		// paramétrisation de la Frame
		f.setTitle("SimTole");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setPreferredSize(new Dimension(1000, 700));
        f.setLocationRelativeTo(null);      

        // Affichage de la Frame
	    f.pack();
	    f.setVisible(true);

        return f;
	}

	/**
	 * Method called by the model for each line (path) of the config file. Creates a delegate controller to manage a new experiment.
	 * @param path Path of the experiment to be created
	 */
	public void createExperimentFromConfigFile(String path) {
		// Create and init the delegate controller
		ExperimentController delegate = new ExperimentController(this);
		delegate.createExperimentFromPath(path);
		model.addExperimentController(delegate);

		// Get the configured SPanel to add it to the main window
		SPanel s = delegate.getSPanel(); // Récupération de la simulation correspondante
		if(s != null) {
			view.addExperimentPanel(s); // ajout d'un nouveau SPanel
		}
	}

// Methods called by the interface after user actions

	/**
	 * Creates a new experiment
	 */
	public void newExperiment() {
		System.out.println("New experiment !");

		// récupération du nom de la simulation
		String name = view.getNewExperimentName();

		if (name == null)
			return;

		// Create and init the delegate controller
		ExperimentController delegate = new ExperimentController(this);
		delegate.createExperimentFromName(name);
		model.addExperimentController(delegate);

		// Get the configured SPanel to add it to the main window
		SPanel s = delegate.getSPanel(); // Récupération de la simulation correspondante
		if(s != null) {
			view.addExperimentPanel(s); // ajout d'un nouveau SPanel
		}
	}	

	/**
	 * Exits the application
	 */
	public void exit() {
		System.exit(0);
	}
}
