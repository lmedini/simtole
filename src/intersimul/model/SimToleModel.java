package intersimul.model;

import intersimul.controller.ExperimentController;
import intersimul.controller.SimToleController;
import intersimul.model.experiment.ParamLoader;

import java.util.ArrayList;

/**
 * Model class responsible for managing all experiments:
 * <ul>
 * <li>experiments which paths are stored in the configuration file</li>
 * <li>experiments added by the user through the "New experiment" menu</li>
 * </ul>
 * @author Lionel Médini
 *
 */
public class SimToleModel {

	SimToleController controller;
	ArrayList<ExperimentController> delegates;

	public SimToleModel(SimToleController controller) {
		this.controller = controller;
		this.delegates = new ArrayList<ExperimentController>();
	}

	public int getExperimentCount() {
		return delegates.size();
	}

	public ExperimentController getExperimentController(int i) {
		return delegates.get(i);
	}

	public void addExperimentController(ExperimentController delegate) {
		delegates.add(delegate);
	}

	public void removeExperimentController(ExperimentController delegate) {
		int i = 0, delegateIndex = -1;
		for(ExperimentController del: delegates) {
			if(del == delegate) {
				delegateIndex = i;
			}
			i++;
		}
		removeExperimentController(delegateIndex);
	}

	public void removeExperimentController(int index) {
		if(index >= 0 && index < getExperimentCount())
			delegates.remove(index);
	}

	/**
	 * Service method of the model. Only picks the paths from the config file and returns it to the controller.
	 */
	public void run() {
		// Gets the list of experiment file urls in folder 'config'
		ArrayList<String> sims = ParamLoader.getExperiments();

		for (int i = 0; i < sims.size() ; i++) { // pour chaque url ...
			controller.createExperimentFromConfigFile(sims.get(i)); // add a new experiment
		}
	}
}
