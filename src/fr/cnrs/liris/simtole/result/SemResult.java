package fr.cnrs.liris.simtole.result;

import java.util.ArrayList;

import peersim.core.Node;

public class SemResult extends AbstractResult {
	// parcours de la requete (quel que soit son type...)
	protected ArrayList<Node> path;
	// Résultat de traitement associé
	private SemResultInfos resultProcess;

	// Constructeurs
	public SemResult(String id){
		super(id);
		this.path=new ArrayList<Node>();
		this.resultProcess = new SemResultInfos(id);
	}

	public SemResult(String id, long nodeID) {
		super(id);
		this.nodeID = nodeID;
		this.path=new ArrayList<Node>();
		this.resultProcess = new SemResultInfos(id, nodeID);
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Result#clone()
	 */
	@Override
	public Object clone() {
		SemResult cloneRes = new SemResult(this.id);

		cloneRes.nodeID = this.nodeID;
		for (int i=0;i<this.path.size();i++) {
			cloneRes.path.add(this.path.get(i));
		}
		return cloneRes;
	}

	// Accesseurs
	public ArrayList<Node> getPath() {
		return path;
	}

	public void setPath(ArrayList<Node> path) {
		this.path = path;
	}

	public SemResultInfos getResultInfos() {
		return resultProcess;
	}
}