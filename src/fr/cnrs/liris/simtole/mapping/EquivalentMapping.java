package fr.cnrs.liris.simtole.mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;

import fr.cnrs.liris.simtole.node.SemanticNode;

import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;

public class EquivalentMapping extends AbstractMapping{

// Méthodes de gestion du cycle de vie
	protected EquivalentMapping(SemanticNode node, OWLClassExpression localClass, OWLClassExpression distantClass) {
		super(Mapping.EQUIVALENT_TYPE, node, localClass, distantClass);
	}

	public Object clone(){
		EquivalentMapping tmp = new EquivalentMapping(node, localClass, distantClass);
		return super.clone(tmp);
	}

// Accesseurs spécifiques
	public OWLAxiom getAxiom() {
		OWLDataFactory factory = node.getManager().getOWLDataFactory();
		Set<OWLClassExpression> expressions = new HashSet<OWLClassExpression>();
		expressions.add(localClass);
		expressions.add(distantClass);
		return new OWLEquivalentClassesAxiomImpl(factory, expressions, new ArrayList<OWLAnnotation>());
	}
}
