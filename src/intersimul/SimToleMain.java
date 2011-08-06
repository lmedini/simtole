package intersimul;

import intersimul.controller.SimToleController;
import intersimul.model.experiment.ParamLoader;

/**
 * Main class to run SimTole Application
 * 
 * @author Nicolas Lumineau, Lionel Médini
 */
public class SimToleMain {

	/**
	 * Main entry point of the SimTole application.
	 * @param args Path of the project directory
	 */
	public static void main(String[] args) {

        // Mémorisation du path
        ParamLoader.setBasePath(args[0]);

        // Lancement du contrôleur principal
        new SimToleController();    
	}	
}
