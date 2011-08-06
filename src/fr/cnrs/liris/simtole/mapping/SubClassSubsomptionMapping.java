package fr.cnrs.liris.simtole.mapping;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;

import fr.cnrs.liris.simtole.node.SemanticNode;

import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

public class SubClassSubsomptionMapping extends SubsomptionMapping {

// Méthodes de gestion du cycle de vie
	protected SubClassSubsomptionMapping(SemanticNode node, OWLClassExpression localClass, OWLClassExpression distantClass)
	{
		super(node, localClass, distantClass, SubsomptionMapping.SUBCLASS_RELATION_DIRECTION);
	}

	public Object clone(){
		SubClassSubsomptionMapping tmp = new SubClassSubsomptionMapping(node, localClass, distantClass);
		return super.clone(tmp);
	}

// Accesseurs des champs propres
	public OWLAxiom getAxiom() {
		return new OWLSubClassOfAxiomImpl(factory, distantClass, localClass, new ArrayList<OWLAnnotation>());
	}
}