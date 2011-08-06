package fr.cnrs.liris.simtole.mapping;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import fr.cnrs.liris.simtole.node.SemanticNode;
import fr.cnrs.liris.simtole.owl.OWLUtils;

public class MappingFactory {
	
	protected static class EquivalentMappingFactory{
		protected static Mapping createMapping(SemanticNode node, OWLEquivalentClassesAxiom axiome){
			OWLClass localClass = null;
			OWLClass distantClass = null;
			//System.out.println("EquivalentMappingFactory : " + axiome.toString());

			for(OWLClass expression: axiome.getClassesInSignature()) {
				if(OWLUtils.getOntologyNameFromClass(expression).equals(node.getNamespace()))
					localClass = expression;
				else
					distantClass = expression;
			}
			//System.out.println("Nb de classes de l'axiome  = " + axiome.getClassesInSignature().size() + " ; ontologie locale  = " + node.getNamespace() + " ; localclass = " + localClass + " ; distantclass = " + distantClass);
			return new EquivalentMapping(node, localClass, distantClass);
		}
	}

	protected static class SubMappingFactory {
		protected static Mapping createMapping(SemanticNode node, OWLSubClassOfAxiom axiome, String relation){
			if(relation.equals(SubsomptionMapping.SUPERCLASS_RELATION_DIRECTION)) {
				OWLClass distantClass = axiome.getSuperClass().asOWLClass();
				OWLClass localClass = axiome.getSubClass().asOWLClass();
				return new SuperClassSubsomptionMapping(node, localClass, distantClass);
			}
			if(relation.equals(SubsomptionMapping.SUBCLASS_RELATION_DIRECTION)) {
				OWLClass localClass = axiome.getSuperClass().asOWLClass();
				OWLClass distantClass = axiome.getSubClass().asOWLClass();
				return new SubClassSubsomptionMapping(node, localClass, distantClass);
			}
			return null;
		}
	}

	public static Mapping createMapping(SemanticNode node, OWLAxiom axiome) {
		Mapping mapping = null;
		if(OWLUtils.isMapping(axiome)) {
				int type = OWLUtils.getMappingType(axiome, node.getNamespace());
				String direction = null;
				switch(type) { // Cf. doc de la méthode getMappingType()
				case 0:
					mapping = EquivalentMappingFactory.createMapping(node, (OWLEquivalentClassesAxiom) axiome);
					break;
				case 1:
					direction = SubsomptionMapping.SUPERCLASS_RELATION_DIRECTION;
					mapping = SubMappingFactory.createMapping(node, (OWLSubClassOfAxiom) axiome, direction);
					break;
				case 2:
					direction = SubsomptionMapping.SUBCLASS_RELATION_DIRECTION;
					mapping = SubMappingFactory.createMapping(node, (OWLSubClassOfAxiom) axiome, direction);
					break;
				default:
					throw new UnsupportedOperationException("Type de mapping inconnu : " + type);
				}
		}
		return mapping;
	
	}
}
