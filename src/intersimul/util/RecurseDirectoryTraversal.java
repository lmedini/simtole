package intersimul.util;

import intersimul.model.experiment.ParamLoader;

import java.io.File;
import java.util.ArrayList;

public class RecurseDirectoryTraversal {

	/**
	 * Parcours r�cursif d'un r�pertoire pour r�cup�rer l'ensemble des fichiers.
	 * @param repertoire R�pertoire � parcourir. Peut contenir des sous-r�pertoires.
	 * @param allFiles Liste des fichiers trouv�s dans les r�pertoires de niveau sup�rieurs. Sera augment�e des fichiers du r�pertoire "repertoire".
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
