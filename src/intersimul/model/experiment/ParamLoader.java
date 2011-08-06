package intersimul.model.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 
 * @author Enseignement
 * 
 */
public class ParamLoader {

	// définition des noms de paramètres dans les fichiers de configuration (fichiers "*.params")
	public static final String SIM_NAME = "simulName";
	public static final String NB_CYCLES = "nbCycles";
	public static final String DEGREE = "degree";
	public static final String NB_PEER = "nbPeers";
	public static final String NB_QRY = "nbQueries";
	public static final String TTL = "ttl";
	public static final String EXP_CYCLE = "experimentByCycle";
	public static final String DYNAMICS = "dynamicNetwork";
	public static final String TOPOLOGY = "topology";
	public static final String PROTOCOLES = "protocols";
	/**
	 * Specify strategy for network building or document and query distribution
	 */
	public static final String RANDOM = "Random";
	public static final String CLUSTERED = "Clustered";
	public static final String ZIPF = "Zipf";
	public static final String UNIFORM = "Uniform";
	public static final String NODE = "network.node";

	// récupération du bon séparateur (windows ou linux)
	public final static String slash = System.getProperty("file.separator");
	// récupération du chemin d'accès aux fichiers du projet
	public static String basePath;
	// récupération du chemin d'accès aux protocoles
	public static String URL_PROTOCOLS;
	// récupération du chemin d'accès aux configurations
	public static String URL_CONFIG;
	// récupération du fichier de stockage des expérimentations
	public static String URL_PARAMS;
	// définition du répertoire de sortie
	public static String URL_XML_TOPOLOGY;
	// définition du réperoire de stockage des scénarios
	public static String URL_XML_SCENARIO;
	// définition du réperoire de stockage des profils
	public static String URL_PROFILES;

	/**
	 * Return the url of the config file related to simulation s
	 * 
	 * @param s
	 *            the current simulation
	 * @return the url of the config parameters
	 */
	public static String getConfigFileURL(Experiment s) {
		return URL_CONFIG + slash + s.getName() + ".params";
	}

	public static String getProtocolsList(Experiment s) {
		return URL_PROTOCOLS + slash + s.getProtocols();
	}

	/**
	 * Return the url of the file storing document profile values
	 * 
	 * @param s
	 *            the current simulation
	 * @return the url of the document profile file
	 */
	public static String getUrlDocProfiles(Experiment s) {
		return URL_PROFILES + slash + s.getName() + "_DocProfiles.txt";
	}

	/**
	 * Return the url of the file storing query profile values
	 * 
	 * @param s
	 *            the current simulation
	 * @return the url of the query profile file
	 */
	public static String getUrlQryProfiles(Experiment s) {
		return URL_PROFILES + slash + s.getName() + "_QryProfiles.txt";
	}

	/**
	 * Return the url of the xml document storing experiment configuration
	 * 
	 * @param s
	 *            the current simulation
	 * @return the url of the query profile file
	 */
	public static String getXMLdocument(Experiment s, String type) {
		String temp = null;
		if (type.equals("topology"))
			temp = URL_XML_TOPOLOGY + slash + s.getName() + ".xml";
		if (type.equals("scenario"))
			temp = URL_XML_SCENARIO + slash + s.getScenarioName() + ".xml";

		return temp;
	}

	/**
	 * Gets the list of all the files contained in the scenario directory
	 * @return An array of String, each one being the URL of a file
	 */
	public static String[] getScenarioFileConfig() {
		return getDIrectoryFileList(URL_XML_SCENARIO);
	}

	/**
	 * Gets the list of all the files contained in the protocols directory
	 * @return An array of String, each one being the URL of a file
	 */
	public static String[] getProtocolFileConfig() {
		return getDIrectoryFileList(URL_PROTOCOLS);
	}

	/**
	 * Gets the list of all the files contained in a given directory
	 * @param directory The directory name
	 * @return An array of String, each one being the URL of a file
	 */
	private static String[] getDIrectoryFileList(String directory) {
		File file = null;
		String[] children = null;
		file = new File(directory);
		children = file.list();
		return children;		
	}

	/**
	 * update the config file storing the list of experiments
	 * 
	 * @param url
	 *            the url of the config file storing experiment list
	 */
	public static void updateConfigList(String url) {
		try {
			// instanciation d'un fichier d'écriture (écriture à la fin du
			// fichier existant s'il existe)
			FileWriter fw = new FileWriter(URL_PARAMS, true);

			// écriture de l'url associé à la nouvelle simulation
			fw.write("\n" + url);

			fw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Reads the file in URL_PARAMS. Each line is an experiment.
	 * 
	 * @return the list of experiment file urls
	 */
	public static ArrayList<String> getExperiments() {
		// stocke les urls des fichiers de configuration des différentes expérimentations
		ArrayList<String> exps = new ArrayList<String>(); 
		BufferedReader buff = null;
		String line = "";

		try {
			buff = new BufferedReader(new FileReader(URL_PARAMS));
			while (buff.ready()) {
				line = buff.readLine();
				exps.add(line);
			}
			buff.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return exps;
	}

	/**
	 * Parse the string storing the set of values disjointed by ;
	 * 
	 * @param str
	 *            the list of values disjointed by ;
	 * @param lng
	 *            the number of expected values
	 * @return the array storing the values contained in 'str'
	 */
	public static String[] parseConfigValues(String str, int lng) {
		String[] res = new String[lng];
		int i = 0;
		StringTokenizer stk = new StringTokenizer(str, ";");

		while (stk.hasMoreElements()) {
			res[i] = (String) stk.nextElement();
			i++;
		}
		return res;
	}

	/**
	 * Parse values storef in the file addressed in url.
	 * 
	 * @param url
	 *            the url of the file storinf values
	 * @param imax
	 *            the number of expected rows
	 * @param jmax
	 *            the number of expected columns
	 * @return
	 */
	public static int[][] parseProfilesValues(String url, int imax, int jmax) {
		int[][] res = new int[imax][jmax];
		int i = 0;
		int j = 0;

		BufferedReader buff = null;
		StringTokenizer stk = null;
		String line = null;
		int v = 0;
		try {
			buff = new BufferedReader(new FileReader(url));
			while (buff.ready()) {
				line = buff.readLine();
				stk = new StringTokenizer(line, ";");
				j = 0;
				while (stk.hasMoreTokens()) {
					v = Integer.parseInt((String) stk.nextToken());
					res[i][j] = v;
					j++;
				}
				i++;
			}
			buff.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return res;
	}

	public static String getBasePath() {
		return basePath;
	}

	/**
	 * Sets the application base path and all other configuration parameters depending on this path
	 * @param basePath Path to the SimTole project directory
	 */
	public final static void setBasePath(String basePath) {
		ParamLoader.basePath = basePath;
		URL_PROTOCOLS = basePath + slash	+ "protocoles";
		URL_CONFIG = basePath + slash + "config";
		URL_PARAMS = basePath + slash + "config"	+ slash + "expconfig.params";
		URL_XML_TOPOLOGY = basePath + slash + "out";
		URL_XML_SCENARIO = basePath + slash + "scenario";
		URL_PROFILES = basePath + slash + "config" + slash + "profiles";
	}
}