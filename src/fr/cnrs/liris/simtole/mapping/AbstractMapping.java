package fr.cnrs.liris.simtole.mapping;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;

import fr.cnrs.liris.simtole.node.SemanticNode;
import fr.cnrs.liris.simtole.owl.OWLUtils;

public abstract class AbstractMapping implements Mapping {
	/**
	 * Champs propres du mapping
	 */
	protected String type;
	// Noeud auquel est rattaché le mapping
	protected SemanticNode node;
	protected OWLClassExpression localClass;
	protected OWLClassExpression distantClass;
	/**
	 * Champs calculés (pour gagner du temps
	 */
	protected String localOntologyName;
	protected String distantOntologyName;
	protected OWLDataFactory factory;
	protected OWLAxiom axiom;

// Méthodes de gestion du cycle de vie
//	@Deprecated
//	protected AbstractMapping(String localClass, String distantClass, String relation)
//	{
//		super();
//		this.localOntologyName = localClass;
//		this.distantOntologyName = distantClass;
//	}

	/**
	 * Méthode de la classe abstraite appelée par les constructeurs des sous-classes
	 * @param type
	 * @param node
	 * @param localClass
	 * @param distantClass
	 */
	protected AbstractMapping(String type, SemanticNode node, OWLClassExpression localClass, OWLClassExpression distantClass) {
		super();
		// Affectation des champs propres
		this.type = type;
		this.node = node;
		this.localClass = localClass;
		this.distantClass = distantClass;

		// Affectation des champs calculés
		this.localOntologyName = OWLUtils.getOntologyNameFromClass(this.localClass.asOWLClass());
		this.distantOntologyName = OWLUtils.getOntologyNameFromClass(this.distantClass.asOWLClass());
		this.factory = node.getManager().getOWLDataFactory();
		this.axiom = getAxiom();
	}
	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.mapping.MappingInterface#clone()
	 */
	public abstract Object clone();

	protected Object clone(AbstractMapping tmp) {
		// Clonage des champs propres
		tmp.type = this.type;
		tmp.node = this.node;
		tmp.localClass = this.localClass;
		tmp.distantClass = this.distantClass;

		// Clonage des champs calculés
		tmp.distantOntologyName = this.distantOntologyName;
		tmp.localOntologyName = this.localOntologyName;
		tmp.factory = this.factory;
		tmp.axiom = this.axiom;

		return tmp;
	}

// Accesseurs des champs propres
	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.mapping.MappingInterface#getType()
	 */
	public String getType() {
		return type;
	}

	public SemanticNode getNode() {
		return node;
	}

	public OWLClassExpression getLocalClass() {
		return localClass;
	}

	public OWLClassExpression getDistantClass() {
		return distantClass;
	}

// Accesseurs des champs calculés
	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.mapping.MappingInterface#getLocalOntologyName()
	 */
	public String getLocalOntologyName() {
		return localOntologyName;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.mapping.MappingInterface#getDistantOntologyName()
	 */
	public String getDistantOntologyName() {
		return distantOntologyName;
	}

	/* (non-Javadoc)
	 * @see fr.cnrs.liris.simtole.mapping.MappingInterface#getAxiom()
	 */
	public abstract OWLAxiom getAxiom();

	/**
	 * Redéfinition de l'égalité de deux mappings. Si ça marche, on aura de la chance...
	 * Un  mapping est égal à un objet si :
	 * <ul>
	 * <li>cet objet est un mapping</li>
	 * <li>les noms des ontologies locales et distantes des deux mappings correspondent (dans n'importe quel sens)</li>
	 * <li>les formes normalisées de leurs axiomes sont identiques</li>
	 * </ul>
	 */
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Mapping))
			return false;
		Mapping mapping = (Mapping) other;
		if(!(this.localOntologyName.equals(mapping.getLocalOntologyName()) && this.distantOntologyName.equals(mapping.getDistantOntologyName())) || !(this.localOntologyName.equals(mapping.getDistantOntologyName()) && this.distantOntologyName.equals(mapping.getLocalOntologyName())))
			return false;
		return this.axiom.getNNF().equals(mapping.getAxiom().getNNF());
	}
}