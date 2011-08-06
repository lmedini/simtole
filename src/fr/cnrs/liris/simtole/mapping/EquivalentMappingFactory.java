package fr.cnrs.liris.simtole.mapping;

import org.semanticweb.owlapi.model.OWLAxiom;

import fr.cnrs.liris.simtole.node.SemanticNode;

public class EquivalentMappingFactory{
	public static Mapping createMapping(SemanticNode node, OWLAxiom axiome){
		return new EquivalentMapping(node, null, null);
	}

}
