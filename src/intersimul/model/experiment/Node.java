package intersimul.model.experiment;

import java.util.ArrayList;

public class Node {

	int nid = 0;
	ArrayList<Document> documents = null;
	ArrayList<Query> queries = null;
	ArrayList<DocumentProfile> docProfiles = null;
	ArrayList<QueryProfile> qryProfiles = null;
	ArrayList<Node> neighborhood = null;

	public Node( int nid ) {
		this.nid = nid;
	}

	public void loadDocument( ArrayList<Document> docs ){
		this.documents = docs;
	}

	public void loadQuery( ArrayList<Query> qry ){
		this.queries = qry;
	}

	public ArrayList<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
	}

	public int getNbDocuments() {
		return this.documents.size();
	}

	public int getNbQueries() {
		return this.queries.size();
	}

	public int getNid() {
		return nid;
	}

	public void setNid(int nid) {
		this.nid = nid;
	}
	
	public ArrayList<DocumentProfile> getDocProfiles() {
		return docProfiles;
	}

	public void setDocProfiles(ArrayList<DocumentProfile> docProfiles) {
		this.docProfiles = docProfiles;
	}

	public ArrayList<QueryProfile> getQryProfiles() {
		return qryProfiles;
	}

	public void setQryProfiles(ArrayList<QueryProfile> qryProfiles) {
		this.qryProfiles = qryProfiles;
	}

	public ArrayList<Node> getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(ArrayList<Node> neighborhood) {
		this.neighborhood = neighborhood;
	}

	public ArrayList<Query> getQueries() {
		return queries;
	}

	public void setQueries(ArrayList<Query> queries) {
		this.queries = queries;
	}
}
