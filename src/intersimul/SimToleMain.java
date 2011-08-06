package intersimul;

import intersimul.controller.SimToleController;
import intersimul.model.experiment.ParamLoader;

/**
 * Main class to run SimTole Application
 * 
 * @author Nicolas Lumineau, Lionel M�dini
 */
public class SimToleMain {

	/**
	 * Main entry point of the SimTole application.
	 * @param args Path of the project directory
	 */
	public static void main(String[] args) {

        // M�morisation du path
        ParamLoader.setBasePath(args[0]);

        // Lancement du contr�leur principal
        new SimToleController();    
	}	
}
