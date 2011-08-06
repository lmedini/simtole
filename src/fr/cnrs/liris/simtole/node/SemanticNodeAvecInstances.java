package fr.cnrs.liris.simtole.node;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;

public class SemanticNodeAvecInstances extends SemanticNode {

	public SemanticNodeAvecInstances(String prefix) {
		super(prefix);
	}

	public SemanticNodeAvecInstances(String prefix, String nomFich, IRI physIri, IRI ontoIri) {
		super(prefix, nomFich, physIri, ontoIri);
	}

	/**
	 * (Ré)initialise l'ontologie utilisée par le reasoner à partir de l'ontologie locale. Surcharge de la méthode de la superclasse en laissant les instances dans l'ontologie renvoyée...
	 */
	protected void initOntoReasoner() {
		// On vide l'ontologie du reasoner
		manager.removeAxioms(reasoningOntology, reasoningOntology.getAxioms());
		// Et on la remplit des axiomes de l'ontologie locale (telle quelle)

		Set<OWLAxiom> axiomes = localOntology.getAxioms();
		for(OWLAxiom axtmp:axiomes) {
			manager.addAxiom(reasoningOntology, axtmp);
		}
	}

@Override
	public Object clone() {
		return new SemanticNodeAvecInstances(prefix);
	}
}
