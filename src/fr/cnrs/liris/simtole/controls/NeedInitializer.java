package fr.cnrs.liris.simtole.controls;

import java.util.ArrayList;
import java.util.Random;

import fr.cnrs.liris.simtole.protocols.NeedProtocol;
import fr.cnrs.liris.simtole.query.SemQuery;

import peersim.cdsim.CDSimulator;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class NeedInitializer implements Control{
	// Paramètres de configuration issus du fichier de config protocol
	private static final String PAR_PROT_NEED = "need_protocol";
	private static final String PAR_PROT_QUERY = "query_protocol";
	// Autres paramètres
	private static final String PAR_REQ = "nbrequetes";
	private static final String NODE_DE = "nodeDepart";
	private static final String NODE_A = "nodeDestinataire";
	private static final String MODE = "mode";
	private static final String TTL = "ttl";
	private static final String NETWORK_SIZE = "network.size";
	// initialisation du generateur en utilisant la graine du fichier de config
	protected static Random generateur;
	// identifiant du protocole
	protected static int pid;
	//id du protocol query
	protected static int pidQue;
	// nombre de requetes
	protected static int nbRequetes;
	// nombre maximal de cycles
	private static int nbCyclesMax;
	// time-to-live initial
	protected static int TTLinitial;
	protected static int TTLactu;
	protected static int nodeDepart;
	protected static String mode;
	
	public NeedInitializer(String prefix) {
		TTLinitial = 3;
		TTLactu = Configuration.getInt(TTL);
		mode = Configuration.getString(MODE);

		pidQue=Configuration.getPid(prefix + "." + PAR_PROT_QUERY);
		if (mode.equals("auto")){
			nbRequetes = Configuration.getInt(NETWORK_SIZE);
		} else if(mode.equals("manu")) {
			nbRequetes = Configuration.getInt(prefix + "." + PAR_REQ);
		} else {
			System.err.println("Erreur d'initialisation du mode du scénario.");
			throw new ExceptionInInitializerError();
		}
		pid = Configuration.getPid(prefix + "." + PAR_PROT_NEED);	
		nbCyclesMax = Configuration.getInt(CDSimulator.PAR_CYCLES) - 2* TTLinitial;

		//Pour ne pas que toutes les requêtes arrivent au même cycle.
		generateur = new Random(CommonState.r.getLastSeed());
	}

	public boolean execute() {
		System.out.println("\n--------------------------------------------------------------------");
		System.out.println("------------------ Initialisation des requêtes ---------------------");
		//System.out.println("--------------------------------------------------------------------");

		// iteration sur le nombre de requetes
		for (int ii=0; ii<nbRequetes; ii++) {

			// initialisation des identifiants des noeuds
			int noNoeudDepart;
			int noNoeudDest;

			//Création "automatique" de la requête (tous les noeuds se mappent avec tous les autres
			if(mode.equals("auto")) {
				noNoeudDepart = ii%Network.size();	 
				noNoeudDest=generateur.nextInt(Network.size());
				if (noNoeudDest==noNoeudDepart) {
					noNoeudDest =(noNoeudDepart+1)%(Network.size()-1);
				}
			} else {
				noNoeudDepart = Configuration.getInt(NODE_DE);
				noNoeudDest = Configuration.getInt(NODE_A);
			}
			String queryId = "initial_query_" + noNoeudDepart + "_" + noNoeudDest + "_" + System.currentTimeMillis(); // Identifiant unique
			int queryTTL = TTLactu; // TTL défini dans le fichier de configuration
			int queryCycle = generateur.nextInt(2); // Cycle de début

			initMappingQuery(queryId, noNoeudDepart, noNoeudDest, queryTTL, queryCycle);
		}

		//System.out.println("--------------------------------------------------------------------");
		System.out.println("-------------- Fin de l'initialisation des requêtes ----------------");
		System.out.println("--------------------------------------------------------------------\n");

		return true;
	}

	private void initMappingQuery(String id, int noNoeudDepart, int noNoeudDest, int ttl, int cycle) {
		/**
		 *  Création de la requête
		 */
		SemQuery query = new SemQuery();

		query.setQueryID(id);
		query.setNodeDepart(noNoeudDepart);
		query.setNodeDestinatair(noNoeudDest);
		query.setTtl(ttl);
		query.setNoCycle(cycle);
		//Initialisation de l'état du traitement de la requête
		query.setTraite(false);

		System.out.println("Requete " + query.getQueryID() + " : cycle= " + query.getNoCycle() + " ; noeud départ= " + query.getNodeDepart() + " ; noeud destinataire= " + query.getNodeDestinatair() + " ; TTL= "+ query.getTtl());



		/** 
		 * Ajout de la requête au NeedProtocol du noeud de départ
		 */
		// Récuperation du NeedProtocol associé au noeud de départ
		Node noeudDepart = Network.get(noNoeudDepart);
		NeedProtocol needProtocol = (NeedProtocol) noeudDepart.getProtocol(pid);

		// Récupération de la file correspondant à la requête courante et ajout de la requête
		needProtocol.addQuery(query, cycle);

		ArrayList<SemQuery> listeReq = needProtocol.getTabRequetes(cycle);
		System.out.print("NeedProtocol du noeud " + noNoeudDepart + " : cycle= "+ query.getNoCycle() + " ; nb requêtes = "+ listeReq.size() +" : ");

		for(SemQuery ReqTmp: listeReq) {
			System.out.print(" " + ReqTmp.getQueryID());
		}
		System.out.print("\n");

	}
}