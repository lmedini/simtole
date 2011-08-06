package intersimul.util;

import intersimul.model.experiment.ParamLoader;

import java.io.File;
import java.util.ArrayList;

public class RecurseDirectoryTraversal {

	/**
	 * Parcours récursif d'un répertoire pour récupérer l'ensemble des fichiers.
	 * @param repertoire Répertoire à parcourir. Peut contenir des sous-répertoires.
	 * @param allFiles Liste des fichiers trouvés dans les répertoires de niveau supérieurs. Sera augmentée des fichiers du répertoire "repertoire".
	 */
	public static void recurseDirs(String repertoire, ArrayList<File> allFiles) {
		File fichier = new File(repertoire);

		if (fichier.isDirectory()) {
			String list[] = fichier.list();
			for (int i = 0; i < list.length; i++) {
				recurseDirs(repertoire + ParamLoader.slash + list[i], allFiles);
			}
		} else {
			allFiles.add(fichier);
		}
	}
}
