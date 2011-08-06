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
	 * (Ré)initialise l'ontologie utilisée par le reasoner à partir de l'ontologie locale. Surcharge de la méthode de la superclasse avec suppression des instances de l'ontologie renvoyée...
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
