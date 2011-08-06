package intersimul.controller;

import intersimul.model.dao.ExperimentDAO;
import intersimul.model.experiment.Experiment;
import intersimul.model.experiment.ExperimentBuilder;
import intersimul.model.experiment.ParamLoader;
import intersimul.view.SPanel;

/**
 * Controller class for an experiment. Links between its interface (SPanel) and model (Experiment).
 * @author Lionel Médini
 *
 */
public class ExperimentController {

	// TODO rajouter une méthode remove qui passe par le parent pour supprimer une expérimentation.
	private SimToleController parent;
	private Experiment expe;
	private SPanel panel;

	public ExperimentController() {
		super();
	}

	public ExperimentController(SimToleController parent) {
		this();
		this.parent = parent;
	}

	public void createExperimentFromPath(String path) {
		ExperimentBuilder.buildExperimentFromPath(path);
		this.expe = ExperimentBuilder.getInstance();
		this.panel = new SPanel(this.expe);
	}

	public void createExperimentFromName(String name) {
		// création d'une simulation vide
		ExperimentBuilder.buildExperimentFromName(name);
		this.expe = ExperimentBuilder.getInstance();

		// Sauvegarde de l'expérimentation
		ExperimentDAO.saveExperimentInInterSimulFormat(this.expe);

		// mise à jour du fichier listing des configurations
		ParamLoader.updateConfigList(this.expe.getFileUrl());

		this.panel = new SPanel(this.expe);
	}

	public Experiment getExperiment() {
		return expe;
	}

	public SPanel getSPanel() {
		return panel;
	}
}
