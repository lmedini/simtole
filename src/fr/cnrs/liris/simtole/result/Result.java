package fr.cnrs.liris.simtole.result;

public interface Result {

	public ResultInfos getResultInfos();

	public Object clone();

	// Accesseurs du résultat
	public long getNodeID();

	public void setNodeID(long nid);
}