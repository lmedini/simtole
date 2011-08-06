package fr.cnrs.liris.simtole.query;


/**
 * Sous-classe de AbstractQuery. G�re en plus une classe permettant d'acc�der aux informations (param�tres et r�sultats) de la requ�te.. 
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
	 * ATTENTION : le clonage ne conserve pas l'�tat trait�e ou non : une requ�te clon�e est � nouveau non trait�e.
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