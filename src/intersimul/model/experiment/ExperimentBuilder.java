package intersimul.model.experiment;


import java.io.IOException;

import peersim.config.Configuration;
import peersim.config.ParsedProperties;

public class ExperimentBuilder {

	private static Experiment experiment = null;

	/**
	 * Method to be used when creating an experiment already stored in a configuration file.<br />
	 * Initializes the simulation properties with the params stored in the config file.
	 * @param path Path to the "*.params" file that describes the experiment
	 */
	public static void buildExperimentFromPath(String path) {
		// affichage dans la console de la simulation courante en cours de traitement
		System.out.println("Construction de l'expérimentation : " + path );

		experiment = new Experiment();
		try {
			// Initialisation de la classe Configuration, qui permet de récupérer tous les paramètres
			Configuration.setConfig(new ParsedProperties(path));

			//// chargement des propriétés principales de la simulation
			// Sauvegarde du nom du fichier associé à la simulation
			experiment.setFileUrl(path);
			// récupération du nom associé à la siulation
			experiment.setName( Configuration.getString(ParamLoader.SIM_NAME));
			// récupération du nombre de pairs souhaité dans le réseau P2P
			experiment.setPeersNb( Configuration.getInt(ParamLoader.NB_PEER) );
			// récupération du nombre de requêtes exécutées lors de la simulation
			experiment.setQueriesNb(Configuration.getInt(ParamLoader.NB_QRY));
			// récupération du nombre de voisins par pair
			experiment.setNeighborsNb(Configuration.getInt(ParamLoader.DEGREE));
			// récupération du TTL
			experiment.setTtl( Configuration.getInt(ParamLoader.TTL) );
			// récupération de la topologie
			experiment.setTopologyStrategy(  Configuration.getString(ParamLoader.TOPOLOGY)) ;
			// récupération du paramêtre de dynamicité du réseau
			// true : la topologie évolue pendant la simulation
			// false : la topologie est statique lors de la simulation
			experiment.setDynNetwork( Configuration.getBoolean(ParamLoader.DYNAMICS));
			// récupération du protocole de simulation
			// true : simulation par cycle
			// false : simulation par événement
			experiment.setExperimentsByCycle( Configuration.getBoolean(ParamLoader.EXP_CYCLE));
			experiment.setCyclesNb( Configuration.getInt(ParamLoader.NB_CYCLES));
			experiment.setProtocols(Configuration.getString(ParamLoader.PROTOCOLES));

			// Récupération du type de noeud à utiliser
			experiment.setNodeType(Configuration.getString(ParamLoader.NODE));
			//System.out.println("NodeType : " + this.getNodeType());
			// Création des noeuds
			experiment.loadNodes();

		} catch (IOException ioe) { // if no param file is found
			System.err.println("Erreur dans le fichier de config " + path);
			ioe.printStackTrace();
		}
	}

	/**
	 * Method to be used when creating a new experiment using the "New experiment" menu of the main window.
	 * @param name Name of the experiment; will be shown in the experiment tab
	 */
	public static void buildExperimentFromName(String name) {
		experiment = new Experiment();
		experiment.setName(name);		
		experiment.setFileUrl(ParamLoader.getConfigFileURL(experiment));
		experiment.setProtocols(ParamLoader.getProtocolsList(experiment));
	}

	public static Experiment getInstance() {
		return experiment;
	}
}