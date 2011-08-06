package fr.cnrs.liris.simtole.controls;

import java.util.ArrayList;
import java.util.Random;
import java.io.*;

import example.aggregation.AverageFunction;
import peersim.cdsim.CDSimulator;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.util.IncrementalStats;


/**
 * 
 * Ajouter au fichier de config
 * 
 * init.genonto fr.cnrs.liris.simtole.OntOwlGenerator
 * init.genonto.protocol lnk
 * init.genonto.nbmaxxoncepts 6
 * init.genonto.nbfils 1
 * init.genonto.nbAxiomes 1
 * 
 * 
 *init.needinit.nbrequetes
 */



public class OntOwlGenerator implements Control {
	
	
	private static final String PAR_CON = "nbmaxconcepts";
	private static final String PAR_FILS = "nbfils";
	private static final String PAR_AX = "nbAxiomes";
	private static final String PAR_VOIS = "protocol";
	
	private static final String NODE_DE = "nodeDepart";
	private static final String NODE_A = "nodeDestinataire";
	private static final String MODE = "mode";

	// initialisation du generateur en utilisant la graine du fichier de config
	protected static Random generateur = new Random(CommonState.r.getLastSeed());
	// identifiant du protocole
	protected static int pid;
	//id du protocol query
	protected static int pidQue;
	// nombre de requetes
	protected static int nbFils;
	// nombre maximal de concept dans l'ontologie
	private static int nbConceptMax;
	// Nb max axiomes
	protected static int nbAxiomes;
	
	protected static int idvoisin;
	
	public OntOwlGenerator(String prefix) {
		
		
		nbConceptMax = Configuration.getInt(prefix + "." + PAR_CON);
		nbFils = Configuration.getInt(prefix + "." + PAR_FILS);
		nbAxiomes = Configuration.getInt(prefix + "." + PAR_AX);
		idvoisin = Configuration.getPid(prefix + "." + PAR_VOIS);
		
		
		
	}
	
	public boolean execute() {
		System.out.println("--------------------- Creation des ontologies ------------------------");
		
		
		String filename = "";
		String filecontent = "";
		
		

		
		int cid = -1;
		
		 for (int i = 0; i < Network.size(); i++) {
			 
			 
			 //int linkableID = FastConfig.getLinkable(idvoisin);
		     Linkable linkable = (Linkable) Network.get(i).getProtocol(idvoisin);
		    
		     int[] idnodes = new int[linkable.degree()];
		     
		     int nbconcepts = nbConceptMax;
		     long[] axiomes = new long[nbconcepts];
		     for( int k = 0 ; k< nbconcepts ; k++ )
		    	 axiomes[k]=-1;
		     
		     int cpt = 0;
		     
		     int nbattributions = Math.min(nbconcepts, nbAxiomes);
		     
		     for( int g = 0 ; g < nbattributions; g++){
		    	 axiomes[g] = linkable.getNeighbor(CommonState.r.nextInt(linkable.degree())).getID();
		     }
		     
		     /*for( int h = 0 ; h < linkable.degree() ; h++ ){
		    	 
			    	axiomes[CommonState.r.nextInt(nbconcepts)] = linkable.getNeighbor(h).getID();
				    
			     }
		    */
			 cid = (int)Network.get(i).getID();
			 
			 filename="./generatedowl/ontoN" + cid + ".owl";
			 
			 
			filecontent ="<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n";
			filecontent += "<rdf:RDF xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n";
			filecontent += "xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n";
			filecontent += "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n";
			filecontent += "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"; 
			filecontent += "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n";
			
			filecontent += "xml:base=\"http://www.example.org/" +cid +"\"\n";
			filecontent += "xmlns=\"http://www.example.org/"+ cid +"#\">\n\n";
			
			filecontent += "<owl:Ontology rdf:about=\"\">\n";
			filecontent += "  <rdfs:comment>Ontology generate for SimTOLE project</rdfs:comment>\n";
			filecontent += "</owl:Ontology>\n\n";

			
			
			// nombre de concept dans l'ontologie
			
			
			String concept = "";
			String conceptpere = "";
			String distnode = "";
			for( int j = 0 ; j < nbconcepts ; j++ ){
				
				concept = "" + (char)(65 + j);
				conceptpere = "" + (char)(64 + j);
				
				if( j==0 ){
					
					filecontent += "<owl:Class rdf:ID=\""+ concept+"\">\n";
					filecontent += "</owl:Class>\n";
					
				}else{
					
					filecontent += "<owl:Class rdf:ID=\""+ concept +"\">\n";
					filecontent += "<rdfs:subClassOf rdf:resource=\"#"+ conceptpere  +"\"/>\n";
					System.out.println("axiomes[j]:" + axiomes[j]);
					if( axiomes[j] > -1 ){
					distnode = "" + axiomes[j];
					filecontent += "<rdfs:subClassOf rdf:resource=\"http://www.example.org/"+ distnode +"#"+ concept +"\"/>\n";
					}
					filecontent += "</owl:Class>\n";
					
				}
				
				
				
			}

			filecontent += "</rdf:RDF>\n";
		 
		 
		 
		 // écriture du fichier
		 
		 BufferedOutputStream bufferedOutput = null;
	        
	        try {
	            
	            //Construct the BufferedOutputStream object
	            bufferedOutput = new BufferedOutputStream(new FileOutputStream(filename));
	            
	            //Start writing to the output stream
	            bufferedOutput.write(filecontent.getBytes());
	           
	        } catch (FileNotFoundException ex) {
	            ex.printStackTrace();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        } finally {
	            try {
	                if (bufferedOutput != null) {
	                    bufferedOutput.flush();
	                    bufferedOutput.close();
	                }
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
	        
	
		 System.out.println("---------Print: "+ filename +"-----------------------------------");

		 }
		 
		 
		 System.out.println("-----END GENERATION --------");
		 
		 return true;
	}


}
