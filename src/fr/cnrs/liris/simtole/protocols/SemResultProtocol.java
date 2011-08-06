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
 * Ce protocole g�re les r�sultats des requ�tes aller (<i>i.e.</i> les diff�rents chemins possibles entre noeuds de d�part et de destination, ainsi que le chemin pr�f�r�, r�sultat du calcul de plus court chemin par le <code>measurer</code> du pair destination).<br />
 * Il stocke les r�sultats temporaires des requ�tes (<code>pendingResults</code>) tant que le TTL de la requ�te n'est pas pass�. Chaque r�sultat contient un chemin valide entre source et destination et la distance associ�e � ce chemin. Le tableau de r�sultats est aliment� par le <code>QueryProtocol</code>.<br />
 * &Agrave; la fin du TTL, ll d�termine le plus court chemin parmi tous ceux parcourus et le stocke dans les r�sultats d�finitifs (<code>shortestResults</code>).</li>
 * @author Lionel M�dini
 *
 */
public class SemResultProtocol extends AbstractQueryProtocol implements CDProtocol{
	protected String prefix;
	//Map des r�sultats en cours de construction pour le calcul de chemin (la cl� est l'id de la requ�te)
	private HashMap<String, ArrayList<Result>> pendingResults;
	//Map des resultats finaux du calcul de chemin : stocke les r�sultats de plus court chemin (la cl� est l'id de la requ�te)
	private HashMap<String, Result> shortestResults;

	private final int pidQueryFile;

	public SemResultProtocol(String prefix){
		this.prefix = prefix;
		pidQueryFile = Configuration.getPid(prefix + "." + PAR_PROT_QUEUE);

		pendingResults = new HashMap<String, ArrayList<Result>>();
		shortestResults = new HashMap<String, Result>();
	}

	/**
	 * Traitement des tableaux "pending" (si le TTL de la requ�te est �coul�)
	 */
	public void nextCycle(Node nod, int protocolID) {
		SemanticNode node = (SemanticNode) nod;
		QueryQueueManagementProtocol protocolQF = (QueryQueueManagementProtocol) node.getProtocol(pidQueryFile);

		// On stocke dans un tableau les ids des requ�tes trait�es
		ArrayList<String> toBeRemoved = new ArrayList<String>();

		Set<String> queryIds = pendingResults.keySet();
		for(Iterator<String> it = queryIds.iterator(); it.hasNext();)
		//for(String queryId: queryIds) 
		{
			String queryId = it.next();
			SemQuery query = null;

			//Recherche de la requ�te correspondant � l'�l�ment du tableau pending
			for(int i=0; i<protocolQF.getPendingQueriesCount(); i++) {
				SemQuery tmp = (SemQuery) protocolQF.getPendingQuery(i);
				//System.out.println("*** SemResultProtocol - queryId :" + queryId + " ; tmp : " + tmp);
				if(tmp.getQueryID().equals(queryId))
					query = tmp;
			}
			//Si tous les r�sultats possibles de la requ�te sont arriv�s (le tableau pending est complet) : identification du chemin de distance minimale
			if(query.endTTL()) {
				System.out.println("\n*** SemResultProtocol ***");
				System.out.println("Noeud : " + node.getID() + "");
				System.out.println(query.toString());

				ArrayList<Result> pending = pendingResults.get(queryId);
				// On lance la recherche du plus court chemin
				int indicePlusCourtChemin = getShortestPathIndex(pending);

				// On cr�e la requ�te de retour � partir de celle de l'aller
				String mappingQueryId = queryId.replace("initial", "mapping");
				SemQuery queryRetour = (SemQuery) query.clone();
				queryRetour.setQueryID(mappingQueryId);

				// On ajoute le plus court chemin aux r�sultats � traiter par le protocole de mappings
				SemResult resultMin = (SemResult) pending.get(indicePlusCourtChemin);
				this.setQueryShortestResult(mappingQueryId, resultMin);

				// On l'ajoute au tableau des requ�tes d�j� trait�es
				toBeRemoved.add(queryId);
				// On supprime la requ�te dans la file aller du QueryFileProtocol
				protocolQF.removePendingQuery(query);
				// ...et on ajoute la nouvelle requ�te dans celle du retour
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
		// On refait une seconde boucle pour supprimer les requ�tes � l'ext�rieur de celle qui boucle sur ces requ�tes (sinon : ConcurrentModificationException)
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
	 * Calcul du plus court chemin parmi les diff�rents r�sultats d'une requ�te
	 * Minimum de chaune des distances totales des chemins consid�r�s
	 * @param pending L'ensemble des r�sultats � consid�rer
	 * @return Index du plus court chemin dans le tableau de r�sultats donn� en param�tre
	 */
	private int getShortestPathIndex(ArrayList<Result> pending) {
		System.out.println("** Recherche du plus court chemin parmi " + pending.size() + " candidat(s) **");

		Double minDistance = 1.;
		int indicePlusCourtChemin = 0;
		int i;
		// On parcourt l'ensemble des r�sultats (� partir d'ici, on travaille en r�sultats s�mantiques)
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
	 * Calcul de la distance totale associ�e � un r�sultat : max de toutes les distances du chemin parcouru.
	 * @param semRes le r�sultat contenant les distances entre les noeuds
	 * @return
	 */
	private Double getDistTotaleMax(SemResult semRes) {
		Double distTotale = semRes.getResultInfos().getDistances().get(0);
		for(Double dist: semRes.getResultInfos().getDistances()) {
			if(dist > distTotale) {
				distTotale = dist;
				//System.out.println("Calcul interm�diaire : " + distTotale);
			}
		}
		return 1. - distTotale;
	}

	/**
	 * Calcul de la distance totale associ�e � un r�sultat : somme de toutes les distances du chemin parcouru.
	 * @param semRes le r�sultat contenant les distances entre les noeuds
	 * @return
	 */
	private Double getDistTotaleSomme(SemResult semRes) {
		Double distTotale = 0.;
		for(Double dist: semRes.getResultInfos().getDistances()) {
			// On somme pour avoir la distance totale du chemin
			distTotale += dist;
			//System.out.println("Calcul interm�diaire : " + distTotale);
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
	 * Renvoie un clone de la liste g�r�e. Utiliser la m�thode setQueryPendingResult en cas de moduifications.
	 * Renvoie n�cessairement une ArrayList (m�me vide si aucun r�sultat)
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