package fr.cnrs.liris.simtole.protocols;

import fr.cnrs.liris.simtole.query.Query;

import peersim.core.Node;
import peersim.core.Protocol;

/**
 * Classe abstraite qui impl�mente quelques m�thodes g�n�riques des protocoles "cycle driven".<br />
 * TODO faire en sorte que les autres protocole h�ritent de celui-l� :
 * - en r�cup�rant la m�thode <code>clone()</code>
 * - en centralisant les variables globales (noms des param�tres de config) ; mais il faut les harmoniser pour �a. 
 * @author Lionel M�dini
 *
 */
public abstract class AbstractQueryProtocol implements Protocol {
	protected String prefix;

	// Noms des param�tres pass�s aux protocoles dans le fichier de config. Les noms sont standardis�s, mais leurs valeurs peuvent �tre diff�rentes pour chaque protocole.
	protected static final String PAR_PROT_NEED = "need_protocol";
	protected static final String PAR_PROT_QUEUE = "queryqueue_protocol";
	protected static final String PAR_PROT_QUERY = "query_protocol";
	protected static final String PAR_PROT_RESULT = "result_protocol";
	protected static final String PAR_PROT_INFO = "info_protocol";

	protected String affichage = "";

	public Object clone() {
		Object res = null;
		try {
			res = super.clone();
		} catch (CloneNotSupportedException e) {
			// Para�t que �a n'arrive jamais de passer l�.
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * A FAIRE : harmoniser les messages de protocoles gr�ce � cette fonction.
	 * @param node
	 * @param query
	 */
	public void affiche(Node node, Query query) {
		System.out.println("\n*** " + this.getClass().getSimpleName() + " ***");
		System.out.println("Noeud : " + node.getID());
		System.out.println("Query : " + query.getQueryID());
		System.out.println(affichage);
		System.out.println("*** Fin " + this.getClass().getSimpleName() + " ***");
	}
}