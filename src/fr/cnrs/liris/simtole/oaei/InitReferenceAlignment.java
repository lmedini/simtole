package fr.cnrs.liris.simtole.oaei;

import java.io.File;
import java.io.FileReader;

import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;

import fr.cnrs.liris.simtole.InfoGlobal;
import fr.cnrs.liris.simtole.mapping.EquivalentMapping;
import fr.cnrs.liris.simtole.mapping.MappingFactory;
import fr.cnrs.liris.simtole.node.SemanticNode;
import fr.cnrs.liris.simtole.owl.OWLUtils;

/**
 * Classe permettant le parsing (en SAX) des fichiers de mappings définis dans les reference-alignment fournis avec les ontologies OAEI'2010.
 * @author Lionel Médini
 *
 */
public class InitReferenceAlignment extends DefaultHandler {
	// Ontologies sue lesquelles porte le fichier
	private String onto1 = null;
	private String onto2 = null;

	// Eléments impliqués dans un mapping
	private String iri1 = null;
	private String iri2 = null;
	private String relation = null; // A faire : vérifier qu'il s'agit de mappings d'équivalence (mais bon, il n'y a que ceux-là).

	private SemanticNode node1, node2;
	private OWLClass class1, class2;
	private OWLDataFactory dataFactory;

	// Pour l'affichage
	private String sourceName;
	private int nbMappings;
	/**
	 * Méthode statique appelée par OWLDistributionInitializer
	 * @param source
	 */
	public static void initReferenceAlignmentFromFile(File source) {
		  try {
			  // On crée un parser et on l'associe au handler
			  XMLReader xr = XMLReaderFactory.createXMLReader();
			  InitReferenceAlignment handler = new InitReferenceAlignment(source.getName());
				xr.setContentHandler(handler);
				xr.setErrorHandler(handler);

				// On lance le parsing
				FileReader r = new FileReader(source);
			    xr.parse(new InputSource(r));
		  } catch (Throwable err) {
		        err.printStackTrace ();
		  }
	}

	private InitReferenceAlignment(String sourceName) {
		this.sourceName = sourceName;
		nbMappings = 0;
		dataFactory = new OWLDataFactoryImpl();
	}

	public void startDocument () {
		System.out.println("*** Initialisation des mappings du fichier : " + sourceName);
    }

    public void endDocument () {
    	System.out.println("*** Fin de l'initialisation des mappings du fichier : " + sourceName + " : " + nbMappings + " mappings traités entre " + onto1 + " et " + onto2 + " (" + InfoGlobal.nbMappingInit + " mappings au total)\n");
    }

    public void startElement (String uri, String name, String qName, Attributes atts) {
    	if (qName.equals("Ontology")) {
    		if(onto1 == null) {
    			onto1 = atts.getValue("rdf:about");
    			node1 = OWLUtils.getNodeFromOntologyName(onto1);
    			System.out.println("Ontologie 1 = " + onto1 + " (noeud " + node1.getID() + ")");
    		} else {
    			onto2 = atts.getValue("rdf:about");
    			node2 = OWLUtils.getNodeFromOntologyName(onto2);
    			System.out.println("Ontologie 2 = " + onto2 + " (noeud " + node2.getID() + ")");
    		}
    	} else if(qName.equals("map")) {
    		nbMappings ++;
    	} else if(qName.equals("entity1")) {
    		// Récupération des données du fichier
    		iri1 = atts.getValue("rdf:resource");
    		class1 = new OWLClassImpl(dataFactory, IRI.create(iri1));
    		System.out.print("Mapping entre : " + iri1);
    	} else if(qName.equals("entity2")) {
    		// Récupération des données du fichier
    		iri2 = atts.getValue("rdf:resource");
    		class2 = new OWLClassImpl(dataFactory, IRI.create(iri2));
    		System.out.println(" et " + iri2);
    	}
    }

    public void endElement (String uri, String name, String qName) {
    	if (qName.equals ("map")) {
    		// Création du mapping
    		if(class1 != null && class2 != null) {
    			// Création des données nécessaires
    			Set<OWLClass> classes = new TreeSet<OWLClass>();
    			classes.add(class1);
    			classes.add(class2);
    			OWLEquivalentClassesAxiom axiom = new OWLEquivalentClassesAxiomImpl(dataFactory, classes, new TreeSet<OWLAnnotation>());
    			// Premier mapping
    			EquivalentMapping m1 = (EquivalentMapping) MappingFactory.createMapping(node1, axiom);
    			node1.addEquivalentMappingsAvec((int) node2.getID(), m1);
    			// Second mapping
    			EquivalentMapping m2 = (EquivalentMapping) MappingFactory.createMapping(node2, axiom);
    			node2.addEquivalentMappingsAvec((int) node1.getID(), m2);
    			// Pour les stats
    			InfoGlobal.nbMapping++;
				InfoGlobal.nbMappingInit++;
    		}
    		iri1 = iri2 = null;
    		class1 = class2 = null;
    	}
    }
}