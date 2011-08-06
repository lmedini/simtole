package fr.cnrs.liris.simtole_old;

import org.semanticweb.owlapi.model.OWLAxiom;

public class Correspondance {
	private OWLAxiom axiom;
	private int noNode;
	
	public Correspondance(){
		
	}
	
	public OWLAxiom getAxiom(){
		return axiom;
	}
	public int getNoNode(){
		return noNode;
	}
	
	public void setAxiom(OWLAxiom o){
		axiom=o;
	}
	public void setNoNode(int n){
		noNode=n;
	}
	

}
