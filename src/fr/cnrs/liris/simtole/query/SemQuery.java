package fr.cnrs.liris.simtole.query;


/**
 * Sous-classe de AbstractQuery. Gère en plus une classe permettant d'accéder aux informations (paramètres et résultats) de la requête.. 
 */
public class SemQuery extends AbstractQuery {
	protected SemQueryInfos semInfos = null;

	/**
	 * Constructeurs
	 */
	public SemQuery() {
		super();
		semInfos = new SemQueryInfos();
	}

	public SemQuery(String id) {
		super(id);
		semInfos = new SemQueryInfos();
	}

	/**
	 * ATTENTION : le clonage ne conserve pas l'état traitée ou non : une requête clonée est à nouveau non traitée.
	 */
	public Object clone() {
		SemQuery cloneQuery = new SemQuery();
		abstractClone(cloneQuery);

		cloneQuery.semInfos = (SemQueryInfos) this.semInfos.clone();

		return cloneQuery;
	}

	//Accesseur
	public SemQueryInfos getInfosTraitement() {
		return semInfos;
	}
}