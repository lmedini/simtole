package fr.cnrs.liris.simtole.result;

import java.util.ArrayList;

import fr.cnrs.liris.simtole.mapping.Mapping;

public class SemResultInfos implements ResultInfos {
	private String id;
	// Stockage des informations de plus court chemin
	private ArrayList<Double> distances;

	// liste de mappings
	private ArrayList<Mapping> listMapping;

	// Constructeurs
	public SemResultInfos(String id){
		this.id = id;
		distances = new ArrayList<Double>();
		listMapping = new ArrayList<Mapping>();
	}

	public SemResultInfos(String id, long nodeID) {
		this(id);
		//this.nodeID = nodeID;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.Result#clone()
	 */
	@Override
	public Object clone() {
		SemResultInfos cloneRes = new SemResultInfos(this.id);

		//cloneRes.nodeID = this.nodeID;
		for(int i=0;i<this.listMapping.size();i++) {
			cloneRes.getListMapping().set(i, this.listMapping.get(i));
		}
		for(int i=0;i<this.distances.size();i++) {
			cloneRes.getDistances().set(i, this.distances.get(i));
		}
		return cloneRes;
	}

	public ArrayList<Mapping> getListMapping() {
		return listMapping;
	}

	/**
	 * Setter par recopie.
	 * @param listMapping
	 */
	public void setListMapping(ArrayList<Mapping> listMapping) {
		for(Mapping mapping: listMapping) {
			this.listMapping.add(mapping);
		}
	}

	public ArrayList<Double> getDistances() {
		return distances;
	}

	/**
	 * Setter par recopie.
	 * @param listMapping
	 */
	public void setDistances(ArrayList<Double> distances) {
		for(int i=0;i<distances.size();i++) {
			this.distances.set(i, distances.get(i));
		}
	}
}