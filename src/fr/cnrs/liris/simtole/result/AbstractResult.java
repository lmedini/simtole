package fr.cnrs.liris.simtole.result;

/**
 * Classe abstraite qui pr�sente un r�sultat g�n�rique. Sera sous-class�e en fonction des donn�es des r�sultats des diff�rents types de protocoles.
 * @author Lionel M�dini
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