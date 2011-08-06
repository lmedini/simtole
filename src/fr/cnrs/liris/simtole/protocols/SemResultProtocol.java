package fr.cnrs.liris.simtole.protocols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Node;
import fr.cnrs.liris.simtole.node.SemanticNode;
import fr.cnrs.liris.simtole.query.SemQuery;
import fr.cnrs.liris.simtole.result.Result;
import fr.cnrs.liris.simtole.result.SemResult;

/**
 * Ce protocole gère les résultats des requêtes aller (<i>i.e.</i> les différents chemins possibles entre noeuds de départ et de destination, ainsi que le chemin préféré, résultat du calcul de plus court chemin par le <code>measurer</code> du pair destination).<br />
 * Il stocke les résultats temporaires des requêtes (<code>pendingResults</code>) tant que le TTL de la requête n'est pas passé. Chaque résultat contient un chemin valide entre source et destination et la distance associée à ce chemin. Le tableau de résultats est alimenté par le <code>QueryProtocol</code>.<br />
 * &Agrave; la fin du TTL, ll détermine le plus court chemin parmi tous ceux parcourus et le stocke dans les résultats définitifs (<code>shortestResults</code>).</li>
 * @author Lionel Médini
 *
 */
public class SemResultProtocol extends AbstractQueryProtocol implements CDProtocol{
	protected String prefix;
	//Map des résultats en cours de construction pour le calcul de chemin (la clé est l'id de la requête)
	private HashMap<String, ArrayList<Result>> pendingResults;
	//Map des resultats finaux du calcul de chemin : stocke les résultats de plus court chemin (la clé est l'id de la requête)
	private HashMap<String, Result> shortestResults;

	private final int pidQueryFile;

	public SemResultProtocol(String prefix){
		this.prefix = prefix;
		pidQueryFile = Configuration.getPid(prefix + "." + PAR_PROT_QUEUE);

		pendingResults = new HashMap<String, ArrayList<Result>>();
		shortestResults = new HashMap<String, Result>();
	}

	/**
	 * Traitement des tableaux "pending" (si le TTL de la requête est écoulé)
	 */
	public void nextCycle(Node nod, int protocolID) {
		SemanticNode node = (SemanticNode) nod;
		QueryQueueManagementProtocol protocolQF = (QueryQueueManagementProtocol) node.getProtocol(pidQueryFile);

		// On stocke dans un tableau les ids des requêtes traitées
		ArrayList<String> toBeRemoved = new ArrayList<String>();

		Set<String> queryIds = pendingResults.keySet();
		for(Iterator<String> it = queryIds.iterator(); it.hasNext();)
		//for(String queryId: queryIds) 
		{
			String queryId = it.next();
			SemQuery query = null;

			//Recherche de la requête correspondant à l'élément du tableau pending
			for(int i=0; i<protocolQF.getPendingQueriesCount(); i++) {
				SemQuery tmp = (SemQuery) protocolQF.getPendingQuery(i);
				//System.out.println("*** SemResultProtocol - queryId :" + queryId + " ; tmp : " + tmp);
				if(tmp.getQueryID().equals(queryId))
					query = tmp;
			}
			//Si tous les résultats possibles de la requête sont arrivés (le tableau pending est complet) : identification du chemin de distance minimale
			if(query.endTTL()) {
				System.out.println("\n*** SemResultProtocol ***");
				System.out.println("Noeud : " + node.getID() + "");
				System.out.println(query.toString());

				ArrayList<Result> pending = pendingResults.get(queryId);
				// On lance la recherche du plus court chemin
				int indicePlusCourtChemin = getShortestPathIndex(pending);

				// On crée la requête de retour à partir de celle de l'aller
				String mappingQueryId = queryId.replace("initial", "mapping");
				SemQuery queryRetour = (SemQuery) query.clone();
				queryRetour.setQueryID(mappingQueryId);

				// On ajoute le plus court chemin aux résultats à traiter par le protocole de mappings
				SemResult resultMin = (SemResult) pending.get(indicePlusCourtChemin);
				this.setQueryShortestResult(mappingQueryId, resultMin);

				// On l'ajoute au tableau des requêtes déjà traitées
				toBeRemoved.add(queryId);
				// On supprime la requête dans la file aller du QueryFileProtocol
				protocolQF.removePendingQuery(query);
				// ...et on ajoute la nouvelle requête dans celle du retour
				protocolQF.addMappingQuery(queryRetour);

				// Affichages
				System.out.print("Plus court chemin : indice = " + indicePlusCourtChemin + " ; chemin = ");
				for(Node n: resultMin.getPath()) {
					System.out.print(n.getID() + " ");
				}
				System.out.println();
				System.out.println("*** Fin SemResultProtocol ***");

			} else {
				query.decreaseTTL();
			}
		}
		// On refait une seconde boucle pour supprimer les requêtes à l'extérieur de celle qui boucle sur ces requêtes (sinon : ConcurrentModificationException)
		for(String queryId: toBeRemoved) {
			this.pendingResults.remove(queryId);			
		}
//		System.out.println();
//
//		if(shortestResults!=null && shortestResults.size() > 0){
//			System.out.println("Taille tableau shortestResults : " + shortestResults.size());
//		}
	}

	/**
	 * Calcul du plus court chemin parmi les différents résultats d'une requête
	 * Minimum de chaune des distances totales des chemins considérés
	 * @param pending L'ensemble des résultats à considérer
	 * @return Index du plus court chemin dans le tableau de résultats donné en paramètre
	 */
	private int getShortestPathIndex(ArrayList<Result> pending) {
		System.out.println("** Recherche du plus court chemin parmi " + pending.size() + " candidat(s) **");

		Double minDistance = 1.;
		int indicePlusCourtChemin = 0;
		int i;
		// On parcourt l'ensemble des résultats (à partir d'ici, on travaille en résultats sémantiques)
		for(i=0; i<pending.size(); i++) {
			SemResult semRes = (SemResult) pending.get(i);

			// Calcul de la distance totale parcourue
			Double distTotale = getDistTotaleMax(semRes);

			// Affichages
			System.out.print("Chemin[" + i + "] - noeuds : [");
			for(Node n: semRes.getPath()) {
				System.out.print(n.getID() + " ");
			}
			System.out.print("] ; distances : " + semRes.getResultInfos().getDistances());
			System.out.println(" ; distance totale = " + distTotale);

			// Identification du plus court chemin (minimum des distances totales) 
			if(distTotale < minDistance) { 
				minDistance = distTotale;
				indicePlusCourtChemin = i;
			}
		}
		return indicePlusCourtChemin;
	}

	/**
	 * Calcul de la distance totale associée à un résultat : max de toutes les distances du chemin parcouru.
	 * @param semRes le résultat contenant les distances entre les noeuds
	 * @return
	 */
	private Double getDistTotaleMax(SemResult semRes) {
		Double distTotale = semRes.getResultInfos().getDistances().get(0);
		for(Double dist: semRes.getResultInfos().getDistances()) {
			if(dist > distTotale) {
				distTotale = dist;
				//System.out.println("Calcul intermédiaire : " + distTotale);
			}
		}
		return 1. - distTotale;
	}

	/**
	 * Calcul de la distance totale associée à un résultat : somme de toutes les distances du chemin parcouru.
	 * @param semRes le résultat contenant les distances entre les noeuds
	 * @return
	 */
	private Double getDistTotaleSomme(SemResult semRes) {
		Double distTotale = 0.;
		for(Double dist: semRes.getResultInfos().getDistances()) {
			// On somme pour avoir la distance totale du chemin
			distTotale += dist;
			//System.out.println("Calcul intermédiaire : " + distTotale);
		}
		distTotale /= ((double) semRes.getResultInfos().getDistances().size() -1.);
		return distTotale;
	}

	public Result getQueryShortestResult(String queryId) {
		return shortestResults.get(queryId);
	}

	public void setQueryShortestResult(String queryId, Result result) {
		this.shortestResults.put(queryId, result);
	}

	/**
	 * Renvoie un clone de la liste gérée. Utiliser la méthode setQueryPendingResult en cas de moduifications.
	 * Renvoie nécessairement une ArrayList (même vide si aucun résultat)
	 * @param queryId
	 * @return
	 */
	public ArrayList<Result> getQueryPendingResults(String queryId) {
		if(pendingResults.get(queryId) == null)
			return new ArrayList<Result>();
		return (ArrayList<Result>) pendingResults.get(queryId).clone();
	}

	public void setQueryPendingResults(String queryId, ArrayList<Result> results) {
		this.pendingResults.put(queryId, results);
	}

	@Override
	public Object clone() {
		SemResultProtocol cloneObj = (SemResultProtocol) super.clone();
		cloneObj.prefix = this.prefix;

		for (String queryId:this.pendingResults.keySet()) {
			ArrayList<Result> results = this.pendingResults.get(queryId);
			ArrayList<Result> clonedResults = new ArrayList<Result>();
			for(Result result: results) {
				clonedResults.add((SemResult) result.clone());
			}
			cloneObj.pendingResults.put(queryId,clonedResults);
		}

		for (String queryId:this.shortestResults.keySet()) {
			cloneObj.setQueryShortestResult(queryId,(Result) this.shortestResults.get(queryId).clone());
		}

		return cloneObj;
	}
}