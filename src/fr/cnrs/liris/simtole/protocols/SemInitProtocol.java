package fr.cnrs.liris.simtole.protocols;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import peersim.cdsim.CDProtocol;
import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import fr.cnrs.liris.simtole.InfoGlobal;
import fr.cnrs.liris.simtole.mapping.EquivalentMapping;
import fr.cnrs.liris.simtole.mapping.MappingFactory;
import fr.cnrs.liris.simtole.mapping.SubClassSubsomptionMapping;
import fr.cnrs.liris.simtole.mapping.SuperClassSubsomptionMapping;
import fr.cnrs.liris.simtole.node.SemanticNode;
import fr.cnrs.liris.simtole.owl.OWLUtils;

public class SemInitProtocol extends AbstractQueryProtocol implements CDProtocol {
	private final int pidInfoLocal;

	public SemInitProtocol(String prefix) {
		pidInfoLocal = Configuration.getPid(prefix + "." + PAR_PROT_INFO);
	}

	/**
	 * Extraction des mappings initiaux depuis l'ontologie du pair
	 */
	@Override
	public void nextCycle(Node node, int protocolID) {
		try{
			if(CDState.getCycle()==0) //uniquement au premier cycle
			{
				extractInitialMappings((SemanticNode) node);
			}
		}
		catch(Exception e){
			System.err.print("Erreur pendant initialisation des mappings - noeud " + node.getID() + " ; cycle " + CDState.getCycle() + " : ");
			e.printStackTrace();
		}
	}

	/**
	 *  Extrait les mappings intégrés à l'ontologie du noeud et les rajoute dans la liste de mappings des noeuds concernés. 
	 * @param node Noeud courant
	 * @param protocolQ QueryFileProtocol du noeud courant
	 */
	private void extractInitialMappings(SemanticNode node) {
		InfoLocalProtocol protocolInfo = (InfoLocalProtocol) node.getProtocol(pidInfoLocal);
		System.out.println("Extraction des mappings initiaux du noeud " + node.getID());
		//Récupération des données du noeud
		OWLOntology ontology = node.getOntology();

		System.out.println("Ontologie : " + node.getNomFichier());
		Set<OWLAxiom> axioms=ontology.getAxioms();
		if(axioms!=null) {
			for(OWLAxiom oa : axioms) {
				String iriLocal=null;
				String iriDistant=null;
				//if(oa instanceof OWLSubClassOfAxiom)
				if(OWLUtils.isMapping(oa)) {
					System.out.println("Mapping : " + oa.toString());

					//Récupération des ontologies locale et distante
					String [] ontologies = OWLUtils.getOntologiesFromMapping(oa);
					if(ontologies[0].equals(node.getNamespace())) {
						iriLocal = ontologies[0];
						iriDistant = ontologies[1];
					} else if(ontologies[1].equals(node.getNamespace())) {
						iriLocal = ontologies[1];
						iriDistant = ontologies[0];
					}
					SemanticNode otherNode = null;
					for(int i=0; i<Network.size(); i++) {
						if(((SemanticNode) Network.get(i)).getNamespace().equals(iriDistant)) {
							otherNode = (SemanticNode) Network.get(i);
						}
					}

					if(otherNode != null) {
						//Création d'un mapping de type approprié
						int type = OWLUtils.getMappingType(oa, iriLocal);
						switch(type) {
						case 0:
							EquivalentMapping equ = (EquivalentMapping) MappingFactory.createMapping(node, oa);
							node.addEquivalentMappingsAvec((int) otherNode.getID(), equ);
							otherNode.addEquivalentMappingsAvec((int) node.getID(), equ);							
							protocolInfo.addMapping(equ);
							InfoGlobal.nbMapping++;
							InfoGlobal.nbMappingInit++;
							break;

						case 1:
							SuperClassSubsomptionMapping sup = (SuperClassSubsomptionMapping) MappingFactory.createMapping(node, oa);
							node.addSubsomptionMappingsAvec((int) otherNode.getID(), sup);
							otherNode.addSubsomptionMappingsAvec((int) node.getID(), sup);
							protocolInfo.addMapping(sup);
							InfoGlobal.nbMapping++;
							InfoGlobal.nbMappingInit++;
							break;

						case 2:
							SubClassSubsomptionMapping sub = (SubClassSubsomptionMapping) MappingFactory.createMapping(node, oa);
							node.addSubsomptionMappingsAvec((int) otherNode.getID(), sub);
							otherNode.addSubsomptionMappingsAvec((int) node.getID(), sub);
							protocolInfo.addMapping(sub);
							InfoGlobal.nbMapping++;
							InfoGlobal.nbMappingInit++;
							break;

						default:
							break;
						}
					}
				}
			}
		}			
		System.out.println("Fin extraction des mappings initiaux : " + InfoGlobal.nbMappingInit + " mappings dans le protocole InfoGlobal");
	}
}
