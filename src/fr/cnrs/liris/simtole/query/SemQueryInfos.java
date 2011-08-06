package fr.cnrs.liris.simtole.query;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Cette classe étend AbstractQuery en y rajoutant tous les champs nécessaires pour la gestion d'une requête de traitement.
 *
 */
public class SemQueryInfos {

	// uri local 
	protected String valeur;
		//seuil de similarite
	protected int seuil;
	//compteur de noeud passe dans le path
	protected int compteur;

	/**
	 * Passage de paramètres / résultats entre pairs lors du transit de la requête :
	 */
	// Valeurs de similarité pour le chemin courant
	private ArrayList<Double> tabDistance;

	// Ontologie temporaire
	private OWLOntology ontologie = null;

	// Cycle de vie
	public SemQueryInfos() {
		tabDistance = new ArrayList<Double>();
	}
//
//	public SemQueryInfos(String id) {
//		tabDistance = new ArrayList<Double>();
//	}

	public Object clone() {
		SemQueryInfos cloneQuery = new SemQueryInfos();

		if(this.tabDistance.size()==1){
			cloneQuery.tabDistance.add(this.tabDistance.get(0));
		}else{
			for (Double distance:this.tabDistance) {
				cloneQuery.tabDistance.add(distance);
			}
		}

		cloneQuery.ontologie=this.ontologie;
		cloneQuery.valeur=this.valeur;
		cloneQuery.seuil=this.seuil;

		//System.out.println("QueryProcess.clone(" + cloneQuery.queryID + ")");
		return cloneQuery;
	}

	// Accesseurs
	public String getValeur() {
		return valeur;
	}

	public void setValeur(String valeur) {
		this.valeur = valeur;	
	}
	
	public int getSeuil(){
		return seuil;
	}

	public void setSeuil(int i){
		seuil=i;
	}

	public OWLOntology getOntologyTemp(){
		return ontologie;
		}

	public void setOntologyTemp(OWLOntology onto){
		ontologie=onto;
		}

	public ArrayList<Double> getTabDistance() {
		return tabDistance;
	}

	public void setTabSDistance(ArrayList<Double> tabSimilarite) {
		this.tabDistance = tabSimilarite;
	}

	@Override
	public String toString() {
		return this.tabDistance.toString();
	}
}