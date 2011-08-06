package fr.cnrs.liris.simtole.protocols;

import java.util.ArrayList;

import fr.cnrs.liris.simtole.mapping.AbstractMapping;
import fr.cnrs.liris.simtole.mapping.Mapping;
import fr.cnrs.liris.simtole.query.Query;

import peersim.core.Protocol;

/**
 * Protocole qui centralise les mappings trouvés ?
 *
 */
public class InfoLocalProtocol implements Protocol{

	protected String prefix = null;
	protected int nbMapping;
	protected int nbEquivalent;
	protected int nbSubsomption;
	protected int nbAlignement;
	protected String uri;

	protected ArrayList<Mapping> listMapping; 

	public InfoLocalProtocol(String prefix) {
		this.prefix = prefix;
		nbEquivalent = 0;
		nbSubsomption = 0;
		nbAlignement=0;
		uri = null;
		
		this.listMapping = new ArrayList<Mapping>();
	}

	public Object clone() {
		InfoLocalProtocol cloneObj = new InfoLocalProtocol(this.prefix);

		cloneObj.nbMapping = this.nbMapping;
		cloneObj.nbEquivalent = this.nbEquivalent;
		cloneObj.nbSubsomption = this.nbSubsomption;
		cloneObj.nbAlignement = this.nbAlignement;
		cloneObj.uri = this.uri;

		for(Mapping mapping:this.listMapping) {
			cloneObj.listMapping.add((AbstractMapping)mapping.clone());
		}

		return cloneObj;
	}

	public int getNbMapping() {
		return nbMapping;
	}

	public void setNbMapping(int nbMapping) {
		this.nbMapping = nbMapping;
	}

	public int getNbEquivalent() {
		return nbEquivalent;
	}

	public void setNbEquivalent(int nbEquivalent) {
		this.nbEquivalent = nbEquivalent;
	}

	public int getNbSubsomption() {
		return nbSubsomption;
	}

	public void setNbSubsomption(int nbSubsomption) {
		this.nbSubsomption = nbSubsomption;
	}
	
	public void addNbEquivalent(int i) {
		nbEquivalent=nbEquivalent+i;
	}
	
	public void addNbSubsomption(int i) {
		nbSubsomption=nbSubsomption+i;
	}

	public int getNbAlignements() {
		return nbAlignement;
	}

	public void setNbAlignements(int nbAlignement) {
		this.nbAlignement = nbAlignement;
	}

	public void setUri(String uri){
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public ArrayList<Mapping> getListMapping() {
		return this.listMapping;
	}

	//Accesseurs de la liste de mappings
	public int getMappingsCount() {
		return this.listMapping.size();
	}

	public boolean containsMapping(Query query) {
		return this.listMapping.contains(query);
	}

	public Mapping getMapping(int i) {
		return this.listMapping.get(i);
	}

	public void addMapping(Mapping mapping) {
		this.listMapping.add(mapping);
	}

	public void removeMapping(int i) {
		this.listMapping.remove(i);
	}

	public void removeMapping(Query query) {
		this.listMapping.remove(query);
	}
}