package fr.cnrs.liris.simtole.owl;

import peersim.cdsim.CDProtocol;
//import peersim.config.FastConfig;
//import peersim.core.CommonState;
//import peersim.core.Linkable;
import peersim.core.Node;

//import org.semanticweb.owl.util.SimpleURIMapper;
//import java.net.URI;
import org.semanticweb.owlapi.model.OWLClass;

import fr.cnrs.liris.simtole_old.protocols.ContainerHolder;

//import org.semanticweb.owl.apibinding.OWLManager;
//import org.semanticweb.owl.io.OWLXMLOntologyFormat;

public class OwlFunction extends ContainerHolder implements CDProtocol{
	
	public OwlFunction(String prefix){
		 super(prefix);
	}

	 public void nextCycle(Node node, int protocolID) {
		 /*int linkableID = FastConfig.getLinkable(protocolID);
	     Linkable linkable = (Linkable) node.getProtocol(linkableID);
         if (linkable.degree() > 0) {
           Node peer = linkable.getNeighbor(CommonState.r.nextInt(linkable.degree()));
           // Failure handling
           if (!peer.isUp())
               return;

           OwlFunction neighbor = (OwlFunction) peer.getProtocol(protocolID);
           try{
           URI physicalURI = URI.create(neighbor.phURI);
           OWLOntology ontology = neighbor.manager.loadOntologyFromPhysicalURI(physicalURI);
           
           System.out.println(neighbor.phURI+" : --------------------------------------------------  ");
           for(OWLClass cls : ontology.getReferencedClasses()) {   
        	   System.out.println(cls);
        	   }
           }
           catch(OWLOntologyCreationException e){}
        }*/
		 OwlFunction self = (OwlFunction)node.getProtocol(protocolID);

		     //URI physicalURI = URI.create(self.phURI);
		     //OWLOntology ontology = self.manager.loadOntologyFromPhysicalURI(physicalURI);
		     
		     System.out.println(self.getNomFichier()+" : --------------------------------------------------  ");
		     for(OWLClass cls : self.ontology.getClassesInSignature()) {   
		  	   System.out.println(cls);
		  	   }
	 }

}
