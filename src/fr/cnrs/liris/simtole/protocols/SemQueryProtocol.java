package fr.cnrs.liris.simtole.protocols;

import java.util.ArrayList;

import peersim.cdsim.CDProtocol;
import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.Node;
import fr.cnrs.liris.simtole.InfoGlobal;
import fr.cnrs.liris.simtole.node.SemanticNode;
import fr.cnrs.liris.simtole.query.Query;
import fr.cnrs.liris.simtole.query.SemQuery;
import fr.cnrs.liris.simtole.query.SemQueryInfos;
import fr.cnrs.liris.simtole.result.Result;
import fr.cnrs.liris.simtole.result.SemResult;

/**
 * Protocole d'envoi d'une requête d'alignement sémantique.<br />
 * Une requête sémantique part d'un noeud et est transmise à tous les voisins jusqu'à arrivée au noeud destination ou échéance du TTL.
 * &Agrave; chaque étape (différente du noeud source), la distance avec le noeud précédent est calculée et stockée pour l"identification future du plus court chemin.<br />
 * &Agrave; l'arrivée au noeud destination, un résultat (<code>Result</code>) est généré à partir de l'ensemble du chemin et des distances associées aux arcs parcourus.
 * Il est rajouté au tableaux de résultats dans le protocole de traitement des résultats (<code>SemResultProtocol</code>) pour y être traités.
 * @author Lionel Médini
 *
 */
public class SemQueryProtocol extends AbstractQueryFloodingProtocol implements CDProtocol {

	// identifiant des protocoles passés en paramètre
	private final int pidQueryQueue;
	private final int pidResult;

	public SemQueryProtocol(String prefix) {
		this.prefix=prefix;
		pidQueryQueue = Configuration.getPid(prefix + "." + PAR_PROT_QUEUE);
		pidResult = Configuration.getPid(prefix + "." + PAR_PROT_RESULT);
	}

	/**
	 * Méthode de traitement d'un noeud sémantique lors de la phase aller d'une requête
	 * <br />Doit être utilisée avec un SemanticNode, car elle utilise les données spécifiques de ces pairs. Si tel n'est pas le cas, une ClassCastException devrait être lancée dès le début de l'exécution de la méthode.
	 */
	public void nextCycle(Node node, int protocolID) {

		// Récupération des protocoles associés au noeud courant
		QueryQueueManagementProtocol protocolQ = (QueryQueueManagementProtocol) node.getProtocol(pidQueryQueue);
		try{
			for(int i=0; i < protocolQ.getPendingQueriesCount(); i++) //Pour chaque requête de la file
			{
				System.out.println("Traitement de la requête " + protocolQ.getPendingQuery(i).getQueryID());

				SemQuery query = (SemQuery) protocolQ.getPendingQuery(i);
				// Si la requête n'est pas passée par le destinataire, elle est encore valide et c'est le bon cycle pour la traiter
				if (!query.getTraite() && query.getTtl() > 0 && CDState.getCycle() == query.getNoCycle() )
				{
					processPendingQuery((SemanticNode) node, protocolID, query);
				}
				else if(query.getTtl() < 0)
				{
					protocolQ.removePendingQuery(query);
				}
			}
		}
		catch(Exception e){
			System.err.print("Erreur pendant traitement aller - noeud " + node.getID() + " ; cycle " + CDState.getCycle() + " : ");
			e.printStackTrace();
		}
	}

	private void processPendingQuery(SemanticNode node, int protocolID, SemQuery query) {

		// Calcul et ajout de la distance courante dans la requête
		SemQueryInfos queryInfos = query.getInfosTraitement();
		if(query.getPath().size() > 0) {
			int idPrecedent = (int) query.getPath().get(query.getPath().size() -1).getID();
			queryInfos.getTabDistance().add(node.getDistanceAvec(idPrecedent));
			//System.out.println("tabDist : " + queryInfos.getTabDistance());
		} else {
			// On met 0 pour avoir la même longueur de tableau que pour le path.
			queryInfos.getTabDistance().add(new Double(0.));
		}

		// Ajout du noeud courant dans le path de la requête
		// IMPORTANT : faire après l'ajout de distance, car on se sert du path d'avant pour la calculer.
		query.getPath().add(node);

		// Affichages
		System.out.println("\n*** QueryProtocol : ***");
		System.out.println("Noeud : " + node.getID() + (node.getID() == query.getNodeDestinatair()?" (noeud destination)":""));
		System.out.println(query.toString());

		if(node.getID()!=query.getNodeDestinatair()) // Cas du début : avant que la requête n'arrive au destinataire
		{
				// inondation des requetes du noeud courant vers ses voisins et gestion du TTL
				sendQueryFlooding(node, protocolID, query);
		} else // Cas de la requête qui vient d'arriver au destinataire
		{
			// Traitement de la requête sur le noeud destinataire
			receiveQuery(node, query);
		}
		System.out.println("*** Fin QueryProtocol ***");
	}

	public void sendQueryFlooding(Node node, int protocolID, Query query) {
		if (!query.endTTL()) {
			//boolean requetePropagee=false;
			QueryQueueManagementProtocol protocolQ = (QueryQueueManagementProtocol) node.getProtocol(pidQueryQueue);

			// liste des voisins
			ArrayList <Node> voisins = flooding(node, protocolID);
			//System.out.println("Flooding : " + voisins.size() + " voisins trouvés");

			Node voisin = null;
			for (int i = 0; i < voisins.size(); i++) {
				voisin = voisins.get(i);

				// Si le voisin n'est pas disponible, on le passe
				if (!voisin.isUp()) {
					continue;
				}

				// Sinon, récupération des infos
				String queryId = query.getQueryID();
				QueryQueueManagementProtocol voisinProtocolQ = (QueryQueueManagementProtocol)voisin.getProtocol(pidQueryQueue);

				//Vérifier si la requête est déjà traitée par le voisin :
				boolean noeudDejaParcouru = false;
				// Cas du noeud de départ
				if(voisin.getID() == query.getNodeDepart()) {
					noeudDejaParcouru = true;
					// Cas général
				} else {
					//On pacourt toutes les queries de la liste des requêtes déjà traitées et s'il y en a une (autre) qui est passée par le voisin, on bloque.
					for(int j=0; j<voisinProtocolQ.getSentQueriesCount(); j++) {
						SemQuery queryTmp = (SemQuery) voisinProtocolQ.getSentQuery(j);
						if(queryTmp.getQueryID().equals(queryId)) {
							noeudDejaParcouru=true;
							break;
						}				
					}
					// Idem avec la liste des requêtes à traiter...
					for(int j=0; j<voisinProtocolQ.getPendingQueriesCount(); j++) {
						SemQuery queryTmp = (SemQuery) voisinProtocolQ.getPendingQuery(j);
						if(queryTmp.getQueryID().equals(queryId)) {
							noeudDejaParcouru=true;
							break;
						}
					}
				}
				// Affichage debug pour indiquer le résultat du parcours sur ce voisin
				//System.out.println("Flooding : voisin " + voisin.getID() + " ; " + noeudDejaParcouru + " ; " + query.getNoCycle() + " ; " + CDState.getCycle());

				if (!noeudDejaParcouru || voisin.getID() == query.getNodeDestinatair())
				{
					System.out.println("* Flooding: noeud "+ node.getID() +  " propage vers son voisin " + voisin.getID() + " *");

					SemQuery cloneQuery = (SemQuery)query.clone();
					cloneQuery.setNoCycle(CDState.getCycle() + 1);
					cloneQuery.setTraite(false);
					// decrementation du TTL de chaque requete
					cloneQuery.decreaseTTL();

					// Suppression des requêtes à traiter pour le noeud courant
					voisinProtocolQ.addPendingQuery(cloneQuery);

					InfoGlobal.nbMessage++;
				}
			}
				protocolQ.removePendingQuery(query);
				protocolQ.addSentQuery(query);
		}
	}

	private void receiveQuery(SemanticNode node, SemQuery queryRout) {
		SemQueryInfos queryInfos = queryRout.getInfosTraitement();
		SemResultProtocol protocolR=(SemResultProtocol)node.getProtocol(pidResult);

		// On affiche qu'on est content
		System.out.print("** Query " + queryRout.getQueryID() + " arrivée au destinataire avec TTL=" + queryRout.getTtl() + ". Chemin : ");
		for(Node n: queryRout.getPath()) {
			System.out.print(n.getID() + " ");
		}
		System.out.println("**");

		// On crée un résultat de routage avec le path et les distances
		SemResult result = new SemResult(queryRout.getQueryID(), node.getID());
		for(int i=0; i<queryRout.getPath().size(); i++) // Rappel : les tableaux de path et de distances ont la même longueur
		{
			result.getPath().add(queryRout.getPath().get(i));
			result.getResultInfos().getDistances().add(queryInfos.getTabDistance().get(i));
		}

		ArrayList<Result> pending = protocolR.getQueryPendingResults(queryRout.getQueryID());
		//On rajoute le nouveau résultat au tableau de résultats (existant ou non) en attente du protocole résultat
		pending.add(result);
		protocolR.setQueryPendingResults(queryRout.getQueryID(), pending);
		System.out.println("PendingResults : " + protocolR.getQueryPendingResults(queryRout.getQueryID()).size() + " élément(s) pour la requête " + queryRout.getQueryID());
		System.out.print("tab distances : ");
		for(Result r: pending) {
			System.out.print(((SemResult) r).getResultInfos().getDistances() + " ");
		}
		System.out.println();

		// On indique que la requête est arrivée
		queryRout.setTraite(true);

		//Pas tout compris cette ligne :
		InfoGlobal.nbMessage += (result.getPath().size()-1)*3;
	}

	@Override
	public Object clone()
	{
		SemQueryProtocol tmp = (SemQueryProtocol) super.clone();
		tmp.prefix = this.prefix;
		return tmp;
	}

	public void affiche(String nomProtocole, SemanticNode node, Query query) {
		System.out.println("\n*** Protocole : " + this.getClass() + " ***");
		System.out.println("Noeud : " + node.getID() + " (ontologie : " + node.getNomFichier() + ")");
		System.out.println("Query : " + query.getQueryID());
	}
}