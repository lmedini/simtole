package fr.cnrs.liris.simtole.controls;

import intersimul.util.RecurseDirectoryTraversal;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import fr.cnrs.liris.simtole.InfoGlobal;
import fr.cnrs.liris.simtole.mapping.EquivalentMapping;
import fr.cnrs.liris.simtole.mapping.MappingFactory;
import fr.cnrs.liris.simtole.mapping.SubClassSubsomptionMapping;
import fr.cnrs.liris.simtole.mapping.SuperClassSubsomptionMapping;
import fr.cnrs.liris.simtole.node.SemanticNode;
import fr.cnrs.liris.simtole.oaei.InitReferenceAlignment;
import fr.cnrs.liris.simtole.owl.OWLUtils;

/**
 * Mapping :
 * Classe permettant l'initialisation des pairs sémantiques.
 * La méthode principale est la méthode <code>execute()</code>.
 * Elle est appelée par la méthode <code>runInitializer()</code> de CDSimulator.
 */
public class MappingsInitializer implements Control {
	private static final String PAR_DIR = "directory";
	private static String dir;

	/**
	 * Constructeur
	 * @param prefix
	 */
	public MappingsInitializer(String prefix) {
		dir = Configuration.getString(prefix + "." + PAR_DIR);
	}

	/**
	 * Fonction principale 
	 */
	@Override
	public boolean execute() {

		// Initialisation des mappings de référence
		System.out.println("\nRécupération des mappings dans le dossier " + dir);

		ArrayList<File> refFiles = new ArrayList<File>();
		RecurseDirectoryTraversal.recurseDirs(dir, refFiles);

		String[] iris = new String[refFiles.size()];

		for (int j = 0; j < refFiles.size(); j++) {
			iris[j] = refFiles.get(j).toString();
			iris[j] = iris[j].replaceAll("\\\\", "/");
		}

		System.out.println("\n*** Initialisation des mappings de référence : dossier " + dir + "(" + refFiles.size() + " fichiers)");
		for (int i = 0; i < refFiles.size(); i++) {
			InitReferenceAlignment.initReferenceAlignmentFromFile(refFiles.get(i));
		}

		System.out.println("\n*** Extraction des mappings contenus dans les ontologies");
		for (int i = 0; i < Network.size(); i++) {
			extractInitialMappings((SemanticNode) Network.get(i));
		}
		return false;
	}

	/**
	 *  Extrait les mappings intégrés à l'ontologie du noeud et les rajoute dans la liste de mappings des noeuds concernés. 
	 * @param node Noeud courant
	 * @param protocolQ QueryFileProtocol du noeud courant
	 */
	private void extractInitialMappings(SemanticNode node) {
		//InfoLocalProtocol protocolInfo = (InfoLocalProtocol) node.getProtocol(pidInfoLocal);
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
							//protocolInfo.addMapping(equ);
							InfoGlobal.nbMapping++;
							InfoGlobal.nbMappingInit++;
							break;

						case 1:
							SuperClassSubsomptionMapping sup = (SuperClassSubsomptionMapping) MappingFactory.createMapping(node, oa);
							node.addSubsomptionMappingsAvec((int) otherNode.getID(), sup);
							otherNode.addSubsomptionMappingsAvec((int) node.getID(), sup);
							//protocolInfo.addMapping(sup);
							InfoGlobal.nbMapping++;
							InfoGlobal.nbMappingInit++;
							break;

						case 2:
							SubClassSubsomptionMapping sub = (SubClassSubsomptionMapping) MappingFactory.createMapping(node, oa);
							node.addSubsomptionMappingsAvec((int) otherNode.getID(), sub);
							otherNode.addSubsomptionMappingsAvec((int) node.getID(), sub);
							//protocolInfo.addMapping(sub);
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
