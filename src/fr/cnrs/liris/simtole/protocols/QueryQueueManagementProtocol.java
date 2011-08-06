package fr.cnrs.liris.simtole.protocols;

import java.util.ArrayList;

import fr.cnrs.liris.simtole.query.Query;

/**
 * Protocole de gestion des files de requêtes. Gère les files de requêtes aller (à traiter et déjà traitées) et retour (mapping) pour chaque noeud.
 * Propage les accesseurs des éléments des listes pour éviter les erreurs de modifications concurrentes.
 * 
 */
public class QueryQueueManagementProtocol extends AbstractQueryProtocol {

	// Listes de requêtes propres à chaque pair
	private ArrayList<Query> pendingQueriesList;
	private ArrayList<Query> sentQueriesList;
	private ArrayList<Query> mappingQueriesList;

	public QueryQueueManagementProtocol(String prefix) {
		this.prefix = prefix;
		this.pendingQueriesList = new ArrayList<Query>();
		this.sentQueriesList = new ArrayList<Query>();
		this.mappingQueriesList = new ArrayList<Query>();
	}

	public Object clone() {
		QueryQueueManagementProtocol cloneObj = (QueryQueueManagementProtocol) super.clone();
			//new QueryQueueManagementProtocol(this.prefix);
		cloneObj.prefix = this.prefix;
		
		for(Query query:this.pendingQueriesList) {
			cloneObj.pendingQueriesList.add((Query)query.clone());
		}
		for(Query query:this.sentQueriesList) {
			cloneObj.sentQueriesList.add((Query)query.clone());
		}
		for(Query query:this.mappingQueriesList) {
			cloneObj.mappingQueriesList.add((Query)query.clone());
		}

		return cloneObj;
	}

	//Accesseurs de la liste de requêtes non traitées

	public int getPendingQueriesCount() {
		return this.pendingQueriesList.size();
	}

	public boolean containsPendingQuery(Query query) {
		return this.pendingQueriesList.contains(query);
	}

	public Query getPendingQuery(int i) {
		return this.pendingQueriesList.get(i);
	}

	public void addPendingQuery(Query query) {
		this.pendingQueriesList.add(query);
	}

	public void removePendingQuery(int i) {
		this.pendingQueriesList.remove(i);
	}

	public void removePendingQuery(Query query) {
		ArrayList<Query> indexes = new ArrayList<Query>();
		for(Query tmp: this.pendingQueriesList) {
			if(tmp.equals(query))
				indexes.add(tmp);
		}
		for(Query tmp: indexes)
			this.pendingQueriesList.remove(tmp);
	}

	//Accesseurs de la liste de requêtes déjà traitées

	public int getSentQueriesCount() {
		return this.sentQueriesList.size();
	}

	public boolean containsSentQuery(Query query) {
		return this.sentQueriesList.contains(query);
	}

	public Query getSentQuery(int i) {
		return this.sentQueriesList.get(i);
	}

	public void addSentQuery(Query query) {
		this.sentQueriesList.add(query);
	}

	public void removeSentQuery(int i) {
		this.sentQueriesList.remove(i);
	}

	public void removeSentQuery(Query query) {
		ArrayList<Query> indexes = new ArrayList<Query>();
		for(Query tmp: this.sentQueriesList) {
			if(tmp.equals(query))
				indexes.add(tmp);
		}
		for(Query tmp: indexes)
			this.sentQueriesList.remove(tmp);
	}

	//Accesseurs de la liste de requêtes de mapping

	public int getMappingQueriesCount() {
		return this.mappingQueriesList.size();
	}

	public boolean containsMappingQuery(Query query) {
		return this.mappingQueriesList.contains(query);
	}

	public Query getMappingQuery(int i) {
		return this.mappingQueriesList.get(i);
	}

	public void addMappingQuery(Query query) {
		this.mappingQueriesList.add(query);
	}

	public void removeMappingQuery(int i) {
		this.mappingQueriesList.remove(i);
	}

	public void removeMappingQuery(Query query) {
		ArrayList<Query> indexes = new ArrayList<Query>();
		for(Query tmp: this.mappingQueriesList) {
			if(tmp.equals(query))
				indexes.add(tmp);
		}
		for(Query tmp: indexes)
			this.mappingQueriesList.remove(tmp);
	}
}