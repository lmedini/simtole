package fr.cnrs.liris.simtole.controls;

import fr.cnrs.liris.simtole.node.SemanticNode;
import fr.cnrs.liris.simtole.owl.WriteXMLfunctions;
import intersimul.model.experiment.ParamLoader;
import intersimul.util.RecurseDirectoryTraversal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.IRI;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

/**
 * Mapping :
 * Classe permettant l'initialisation des pairs s�mantiques.
 * La m�thode principale est la m�thode <code>execute()</code>.
 * Elle est appel�e par la m�thode <code>runInitializer()</code> de CDSimulator.
 */
public class OwlDistributionInitializer implements Control {
	private static final String PAR_DIR = "directory";
	private static String dir;

	/**
	 * Constructeur
	 * @param prefix
	 */
	public OwlDistributionInitializer(String prefix) {
		dir = Configuration.getString(prefix + "." + PAR_DIR);
	}

	/**
	 * Fonction principale 
	 */
	@Override
	public boolean execute() {
		
		// Initialisation des noeuds en fonction des ontologies
		System.out.println("R�cup�ration des ontologies dans le dossier " + dir);

		ArrayList<File> allFiles = new ArrayList<File>();
		RecurseDirectoryTraversal.recurseDirs(dir, allFiles);

		String[] iris = new String[allFiles.size()];  // Liste des IRIs (d'o� le nom � la con)

		for (int j = 0; j < allFiles.size(); j++) {
			iris[j] = allFiles.get(j).toString();
		}

		// ecrire le fichier xml pour stocker l'info de l'ontologie
		String path = "out\\OntoInfo.xml";
		File f = new File(path);
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			WriteXMLfunctions writeOntoInfo = new WriteXMLfunctions(path);
			writeOntoInfo.writeHead("OntoInfos");

			// distribuer les ontologies
			for (int i = 0; i < Network.size(); i++) {
				System.out.println("*** Initialisation du noeud " + i);

				// R�cup�ration du noeud
				SemanticNode node = (SemanticNode) Network.get(i);

				// Physical IRI de l'ontologie
				String phName = null;
				if (dir.equals("generatedowl")) {
					// cas des ontologies g�n�r�es al�atoirement
					phName = "file:" + ParamLoader.basePath + ParamLoader.slash + Network.get(i).getID() + ".owl";
				} else {
					// cas des ontologies saisie � la main
					int kk = i % (allFiles.size());
					phName = "file:" + ParamLoader.basePath + ParamLoader.slash + iris[kk];
				}
				IRI phIRI = IRI.create(phName.replaceAll("\\\\", "/")); // n�cessaire pour le chargement du fichier (cr�ation d'une URI)
				// Inscription de l'URI dans le fichier ontoInfo
				writeOntoInfo.writeOntoInfo(i, phName); 

				// IRI locale de l'ontologie
				String onName = "network:/" + i;
				IRI onIRI = IRI.create(onName);

				//Nom de fichier de l'ontologie
				String splitter = ParamLoader.slash;
				int indice = phName.lastIndexOf(splitter);
				String fileName = phName.substring(indice + 1);

				// Initialisation du pair
				System.out.println("\nInitialisation du pair " + i + " : (" + fileName + " ; " + phName + " ; " + onName + ")");
				node.setPhysicalIri(phIRI);
				node.setOntologyIri(onIRI);
				node.setNomFichier(fileName);
				node.semInit();
				node.affiche();

				System.out.println("*** Fin de l'initialisation du noeud " + i);
			}
			writeOntoInfo.writeFin("OntoInfos");
			writeOntoInfo.close();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (ExceptionInInitializerError e) {
			System.err.println("Erreur d'initialisation d'un pair s�mantique.");
			e.printStackTrace();
		}

		return false;
	}
}
