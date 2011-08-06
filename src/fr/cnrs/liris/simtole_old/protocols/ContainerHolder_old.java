package fr.cnrs.liris.simtole_old.protocols;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

//import org.apache.commons.collections.MultiHashMap;
import org.picocontainer.*;

import fr.cnrs.liris.simtole.mapping.EquivalentMapping;
import fr.cnrs.liris.simtole.mapping.SubsomptionMapping;
/*
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
*/
import peersim.core.Protocol;

public class ContainerHolder_old implements Protocol{
	
	protected MutablePicoContainer pico;
//	protected OWLOntologyManager manager;
//	protected OWLOntology ontology;
	protected URI physicalURI;
	protected URI ontologyURI;
	protected String nomFichier;
	protected HashMap<Integer,Integer> tableauMetrique;
	protected ArrayList<EquivalentMapping> tabEquivalent = new ArrayList<EquivalentMapping>(); 
	protected ArrayList<SubsomptionMapping> tabSubsomption = new ArrayList<SubsomptionMapping>();
	
	public ContainerHolder_old(String prefix)
	{
		pico = new DefaultPicoContainer();
		//Ajout d'une instance de OWLOntologyManager.class
//		pico.addComponent(OWLManager.createOWLOntologyManager());
//		manager=(OWLOntologyManager)pico.getComponent(OWLOntologyManager.class);
		tableauMetrique=new HashMap<Integer,Integer>();
	}

	public Object clone()
	{
		ContainerHolder svh=null;
		try {
				svh=(ContainerHolder)super.clone();
				svh.tableauEquivalences=(ArrayList<EquivalentMapping>)this.tabEquivalent.clone();
				svh.tableauSubsomptions=(ArrayList<SubsomptionMapping>)this.tabSubsomption.clone();
				svh.tableauMetrique = (HashMap<Integer,Integer>)this.tableauMetrique.clone();
			}
		catch( CloneNotSupportedException e ) {} // never happens
		return svh;
	}
	
	public String getNomFichier(){
		return nomFichier;
	}
	
	public URI getPhysicalUri(){
		return physicalURI;
	}

	public URI getOntologyUri(){
		return ontologyURI;
	}
	
	public HashMap<Integer,Integer> getTableauMetrique(){
		return tableauMetrique;
	}

	public void setPhysicalUri(URI uri){
		physicalURI=uri;
	}

	public void setOntologyUri(URI uri){
		ontologyURI=uri;
	}

	public ArrayList<EquivalentMapping> getTabEquivalent() {
		return tabEquivalent;
	}

	public void setTabEquivalent(ArrayList<EquivalentMapping> tabEquivalent) {
		this.tabEquivalent = tabEquivalent;
	}

	public ArrayList<SubsomptionMapping> getTabSubsomption() {
		return tabSubsomption;
	}

	public void setTabSubsomption(ArrayList<SubsomptionMapping> tabSubsomption) {
		this.tabSubsomption = tabSubsomption;
	}
}