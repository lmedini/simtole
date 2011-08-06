package fr.cnrs.liris.simtole.result;

/**
 * Classe abstraite qui présente un résultat générique. Sera sous-classée en fonction des données des résultats des différents types de protocoles.
 * @author Lionel Médini
 *
 */
public abstract class AbstractResult implements Result  {
	// identifiant du noeud ayant emis le resultat
	protected long nodeID;
	//Identifiant
	protected String id;

	public AbstractResult(String id){
		this.id = id;
	}

	public abstract ResultInfos getResultInfos();

	public abstract Object clone();

	// Accesseurs
	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Result#getNodeID()
	 */
	@Override
	public long getNodeID() {
		return nodeID;
	}
	
	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Result#setNodeID(long)
	 */
	@Override
	public void setNodeID(long nid) {
		this.nodeID = nid;
	}
}