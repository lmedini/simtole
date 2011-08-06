package fr.cnrs.liris.simtole.node;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import fr.cnrs.liris.simtole.owl.OWLUtils;

public class SemanticNodeSansInstance extends SemanticNode {

	public SemanticNodeSansInstance(String prefix) {
		super(prefix);
	}

	public SemanticNodeSansInstance(String prefix, String nomFich, IRI physIri, IRI ontoIri) {
		super(prefix, nomFich, physIri, ontoIri);
	}

	/**
	 * (R�)initialise l'ontologie utilis�e par le reasoner � partir de l'ontologie locale. Surcharge de la m�thode de la superclasse avec suppression des instances de l'ontologie renvoy�e...
	 */
	protected void initOntoReasoner() {
		// On vide l'ontologie du reasoner
		manager.removeAxioms(reasoningOntology, reasoningOntology.getAxioms());
		// Et on la remplit des axiomes de l'ontologie locale (sans les instances)
		try {
			Set<OWLAxiom> axiomes = (OWLUtils.getOntologySansInstance(localOntology)).getAxioms();
			for(OWLAxiom axtmp:axiomes) {
				manager.addAxiom(reasoningOntology, axtmp);
			}
		} catch (OWLOntologyCreationException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Object clone() {
		return new SemanticNodeSansInstance(prefix);
	}
}
