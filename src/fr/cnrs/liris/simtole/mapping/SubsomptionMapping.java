package fr.cnrs.liris.simtole.mapping;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;

import fr.cnrs.liris.simtole.node.SemanticNode;

public abstract class SubsomptionMapping extends AbstractMapping{
	public static final String SUPERCLASS_RELATION_DIRECTION = "superClass";
	public static final String SUBCLASS_RELATION_DIRECTION = "subClass";

	protected String relationDirection;

// Méthodes de gestion du cycle de vie
	protected SubsomptionMapping(SemanticNode node, OWLClassExpression localClass, OWLClassExpression distantClass, String relation) {
		super(Mapping.SUBSOMPTION_TYPE, node, localClass, distantClass);
		this.relationDirection = relation;
	}

// Accesseurs
	public String getRelation() {
		return relationDirection;
	}

	public abstract OWLAxiom getAxiom();
}