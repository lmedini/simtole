package fr.cnrs.liris.simtole.mapping;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Repr�sente tous les types de mappings g�r�s dans l'application.<br />
 * Un mapping doit avoir l'un des 2 types suivants : �quivalent ou de subsomption, qui sont d�finis dans des constantes.<br /> 
 * Un mapping est toujours rattach� � un noeud et conna�t la classe et l'ontologie locale de ce noeud auxquelles il est rattach�. Il conna�t �galement la classe et l'ontologie distantes auxquelles le noeud local est mapp�.<br />
 * <br />
 * Les mappings de subsomption poss�dent en plus une direction (<i>cf.</i> SubsomptionMapping) qui indiquent si la classe distante est une super-classe ou une sous-classe de la classe distante.
 * <strong>La r�gle est de toujours se placer par rapport � l'ontologie locale.</strong><br />
 * <br />
 * Les mappings doivent �tre cr��s par la classe statique <code>MappingFactory</code>
 * 
 * @author Lionel M�dini
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