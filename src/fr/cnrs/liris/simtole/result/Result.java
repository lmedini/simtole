package fr.cnrs.liris.simtole.result;

public interface Result {

	public ResultInfos getResultInfos();

	public Object clone();

	// Accesseurs du r�sultat
	public long getNodeID();

	public void setNodeID(long nid);
}