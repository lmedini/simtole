package intersimul.model.dao;

import java.io.FileWriter;
import java.io.IOException;

import intersimul.model.experiment.Experiment;

/**
 * Common static methods for DAO objects
 * @author Lionel Médini
 *
 */
public abstract class AbstractDAO {
	protected String path;
	protected Experiment expe;

	public AbstractDAO(Experiment expe) {
		super();
		this.expe = expe;
	}

	public void save() {
		System.out.println("Sauvegarde de l'expérimentation " + expe.getName() + " dans : " + path);
		try {
			String vals = "";
			FileWriter fw = new FileWriter(path);
			vals = getConfig(expe);
			fw.write(vals);
			fw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}	
	}

	public void load(String path) {
		//TODO mettre le processus de chargement ici
	}

	protected abstract String getConfig(Experiment expe);
}