package fr.cnrs.liris.simtole.mapping;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Représente tous les types de mappings gérés dans l'application.<br />
 * Un mapping doit avoir l'un des 2 types suivants : équivalent ou de subsomption, qui sont définis dans des constantes.<br /> 
 * Un mapping est toujours rattaché à un noeud et connaît la classe et l'ontologie locale de ce noeud auxquelles il est rattaché. Il connaît également la classe et l'ontologie distantes auxquelles le noeud local est mappé.<br />
 * <br />
 * Les mappings de subsomption possèdent en plus une direction (<i>cf.</i> SubsomptionMapping) qui indiquent si la classe distante est une super-classe ou une sous-classe de la classe distante.
 * <strong>La règle est de toujours se placer par rapport à l'ontologie locale.</strong><br />
 * <br />
 * Les mappings doivent être créés par la classe statique <code>MappingFactory</code>
 * 
 * @author Lionel Médini
 *
 */
public interface Mapping {

	public static final String EQUIVALENT_TYPE = "equivalence";
	public static final String SUBSOMPTION_TYPE = "subsomption";

	public Object clone();

	public String getType();

	public String getLocalOntologyName();

	public String getDistantOntologyName();

	public OWLAxiom getAxiom();
	
	public boolean equals(Object mapping);
}