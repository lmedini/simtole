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
		System.out.println("Construction de l'exp�rimentation : " + path );

		experiment = new Experiment();
		try {
			// Initialisation de la classe Configuration, qui permet de r�cup�rer tous les param�tres
			Configuration.setConfig(new ParsedProperties(path));

			//// chargement des propri�t�s principales de la simulation
			// Sauvegarde du nom du fichier associ� � la simulation
			experiment.setFileUrl(path);
			// r�cup�ration du nom associ� � la siulation
			experiment.setName( Configuration.getString(ParamLoader.SIM_NAME));
			// r�cup�ration du nombre de pairs souhait� dans le r�seau P2P
			experiment.setPeersNb( Configuration.getInt(ParamLoader.NB_PEER) );
			// r�cup�ration du nombre de requ�tes ex�cut�es lors de la simulation
			experiment.setQueriesNb(Configuration.getInt(ParamLoader.NB_QRY));
			// r�cup�ration du nombre de voisins par pair
			experiment.setNeighborsNb(Configuration.getInt(ParamLoader.DEGREE));
			// r�cup�ration du TTL
			experiment.setTtl( Configuration.getInt(ParamLoader.TTL) );
			// r�cup�ration de la topologie
			experiment.setTopologyStrategy(  Configuration.getString(ParamLoader.TOPOLOGY)) ;
			// r�cup�ration du param�tre de dynamicit� du r�seau
			// true : la topologie �volue pendant la simulation
			// false : la topologie est statique lors de la simulation
			experiment.setDynNetwork( Configuration.getBoolean(ParamLoader.DYNAMICS));
			// r�cup�ration du protocole de simulation
			// true : simulation par cycle
			// false : simulation par �v�nement
			experiment.setExperimentsByCycle( Configuration.getBoolean(ParamLoader.EXP_CYCLE));
			experiment.setCyclesNb( Configuration.getInt(ParamLoader.NB_CYCLES));
			experiment.setProtocols(Configuration.getString(ParamLoader.PROTOCOLES));

			// R�cup�ration du type de noeud � utiliser
			experiment.setNodeType(Configuration.getString(ParamLoader.NODE));
			//System.out.println("NodeType : " + this.getNodeType());
			// Cr�ation des noeuds
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