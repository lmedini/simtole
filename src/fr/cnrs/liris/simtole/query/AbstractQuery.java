package fr.cnrs.liris.simtole.query;

import java.util.ArrayList;

import fr.cnrs.liris.simtole.node.SemanticNode;

import peersim.core.Node;

/**
 * Classe abstraite qui définit l'implémentation d'une requête (interface Query). Deux classes dérivées en fonction du type de requête (ROUTE_QUERY_TYPE ou PROCESSING_QUERY_TYPE).
 * @author Lionel
 *
 */
public abstract class AbstractQuery implements Query {

	// identifiant de la requete
	protected String queryID = null;
	// identifiant du noeud de depart
	protected long nodeDepart;
	protected long nodeDestinatair;
	// time-to-live
	protected int ttl;
	// chemin effectue par la requete
	protected ArrayList<Node> path;
	// numero du cycle
	protected int noCycle;
	//deja traite ou pas
	protected boolean traite;
	//compteur de noeud passe dans le path
	protected int compteur;

	//Méthodes de gestion du cycle de vie
	/**
	 * Constructeurs
	 */
	protected AbstractQuery() {
		path = new ArrayList<Node>();
	}

	protected AbstractQuery(String id) {
		this();
		setQueryID(id);
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#clone()
	 */
	public abstract Object clone();

	/**
	 * Partie du clonage qui gère les attributs définis dans la classe abstraite.
	 * ATTENTION : le clonage ne conserve pas l'état traitée ou non : une requête clonée est à nouveau non traitée.
	 * @param cloneQuery
	 */
	protected void abstractClone(AbstractQuery cloneQuery) {

		cloneQuery.setQueryID(this.queryID);
		cloneQuery.setTtl(this.ttl);
		cloneQuery.setNoCycle(this.noCycle);
		cloneQuery.setNodeDepart(this.nodeDepart);
		cloneQuery.setNodeDestinatair(this.nodeDestinatair);

		for (Node node:this.path) {
			cloneQuery.path.add(node);
		}
	}

	/**
	 * Méthode à implémenter en fonction du type de requête à gérer.
	 * @return Un objet de type approprié à la gestion des infos (paramètres et résultats) de la requête.
	 */
	public abstract Object getInfosTraitement();

	//Accesseurs
	public int getCompteur() {
		return compteur;
	}

	public void setCompteur(int compteur) {
		this.compteur = compteur;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#getQueryID()
	 */
	@Override
	public String getQueryID() {
		return queryID;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#setQueryID(java.lang.String)
	 */
	@Override
	public void setQueryID(String id) {
		this.queryID = id;	
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#getNodeDepart()
	 */
	@Override
	public long getNodeDepart() {
		return nodeDepart;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#setNodeDepart(long)
	 */
	@Override
	public void setNodeDepart(long nodeDepart) {
		this.nodeDepart = nodeDepart;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#getNodeDestinatair()
	 */
	@Override
	public long getNodeDestinatair() {
		return nodeDestinatair;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#setNodeDestinatair(long)
	 */
	@Override
	public void setNodeDestinatair(long nodeDestinatair) {
		this.nodeDestinatair = nodeDestinatair;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#getPath()
	 */
	@Override
	public ArrayList<Node> getPath() {
		return path;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#setPath(java.util.ArrayList)
	 */
	@Override
	public void setPath(ArrayList<Node> path) {
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#getNoCycle()
	 */
	@Override
	public int getNoCycle() {
		return noCycle;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#setNoCycle(int)
	 */
	@Override
	public void setNoCycle(int cycle) {
		this.noCycle = cycle;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#getTtl()
	 */
	@Override
	public int getTtl() {
		return ttl;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#setTtl(int)
	 */
	@Override
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#getTraite()
	 */
	@Override
	public boolean getTraite(){
		return traite;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#setTraite(boolean)
	 */
	@Override
	public void setTraite(boolean tr){
		traite=tr;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#endTTL()
	 */
	@Override
	public boolean endTTL() {
		if (ttl == 0) return true;
		else return false;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Query#decreaseTTL()
	 */
	@Override
	public void decreaseTTL() {
		ttl--;
	}

	@Override
	public boolean equals (Object query) {
		return this.queryID.equals(((Query) query).getQueryID());
	}

	@Override
	public String toString() {
		String result = "Query : " + this.getQueryID()
			+ " ; TTL : " + this.getTtl()
			+ " ; nb. de noeuds parcourus : " + this.getPath().size()
			+ "\nChemin : [ ";
		for(Node n: this.getPath()) {
			result += ((SemanticNode) n).getID() + " ";
		}
		result += "]\nDistances : "
			+ this.getInfosTraitement().toString();
	return result;	
	}

	public String toStringDebug() {
		String result = "Query : " + this.getQueryID()
			+ " ; TTL : " + this.getTtl()
			+ " ; traitée : " + this.getTraite()
			+ " ; taille du path : " + this.getPath().size()
			+ " ; taille du tab distances : " + ((SemQueryInfos) this.getInfosTraitement()).getTabDistance().size()
			+ "\nChemin : [ ";
		for(Node n: this.getPath()) {
			result += ((SemanticNode) n).getID() + " ";
		}
		result += "]\nDistances : "
			+ this.getInfosTraitement().toString();
		return result;	
	}
}