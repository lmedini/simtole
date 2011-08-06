package fr.cnrs.liris.simtole.query;

import java.util.ArrayList;

import peersim.core.Node;

public interface Query {

	public Object clone();

	public String getQueryID();

	public void setQueryID(String id);

	public long getNodeDepart();

	public void setNodeDepart(long nodeDepart);

	public long getNodeDestinatair();

	public void setNodeDestinatair(long nodeDestinatair);

	public ArrayList<Node> getPath();

	public void setPath(ArrayList<Node> path);

	public int getNoCycle();

	public void setNoCycle(int cycle);

	public int getTtl();

	public void setTtl(int ttl);

	public boolean getTraite();

	public void setTraite(boolean tr);

	public boolean endTTL();

	public void decreaseTTL();
}