package fr.cnrs.liris.simtole.mapping;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;

import fr.cnrs.liris.simtole.node.SemanticNode;

import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

public class SuperClassSubsomptionMapping extends SubsomptionMapping {

// Méthodes de gestion du cycle de vie
	protected SuperClassSubsomptionMapping(SemanticNode node, OWLClassExpression localClass, OWLClassExpression distantClass)
	{
		super(node, localClass, distantClass, SubsomptionMapping.SUPERCLASS_RELATION_DIRECTION);
	}

	public Object clone(){
		SuperClassSubsomptionMapping tmp = new SuperClassSubsomptionMapping(node, localClass, distantClass);
		return super.clone(tmp);
	}

// Accesseurs des champs propres
	public OWLAxiom getAxiom() {
		OWLDataFactory factory = node.getManager().getOWLDataFactory();
		return new OWLSubClassOfAxiomImpl(factory, localClass, distantClass, new ArrayList<OWLAnnotation>());
	}
}
