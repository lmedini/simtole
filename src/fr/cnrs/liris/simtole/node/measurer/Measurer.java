package fr.cnrs.liris.simtole.node.measurer;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLClass;

import fr.cnrs.liris.simtole.mapping.EquivalentMapping;
import fr.cnrs.liris.simtole.mapping.SubsomptionMapping;
import fr.cnrs.liris.simtole.node.SemanticNode;

/**
 * Classe contenant la logique m�tier qui effectue le calcul de similarit�.
 * Chaque measurer est rattach� � un pair (tout comme le reasoner), pour �tre plus proche du "cas r�el" (pair avec toute son intelligence embarqu�e).<br />
 * Attention : j'ai fait un truc crade en nommant plusieurs fonctions priv�es calcule, calcule1, calcule2 avec des algos diff�rents...
 * Il faut modifier la m�thode publique calcule pour appeler celle qu'on souhaite utiliser.
 * @Author Lionel M�dini
 */
public class Measurer {
	private SemanticNode node;

	public Measurer(SemanticNode node) {
		this.node = node;
	}

	/**
	 * Calcul initial
	 */
	private double calcule(int degreEquivalent, int degreSubclass)
	{
		return 1. - (2. * degreEquivalent + 1. * degreSubclass) / (double) node.getOntologyAxiomCount();
	}

	/**
	 * 1) Distance bas�e sur le nombre de subsomptions:
	 * d1(o,o') = 1 - 1/( nbMappingsSubsomptions(o,o') );
	 */
	private double calcule1(int degreEquivalent, int degreSubclass)
	{
		return 1. - 1./(degreSubclass) / (double) node.getOntologyAxiomCount();
	}

	/**
	 * 2) Distance bas�e sur le nombre de subsomptions et d'�quivalences:
	 * d2(o,o') = 1 - 1/(  alpha * nbMappingsEquivalence + nbMappingsSubsomptions(o,o') );
	 * avec alpha un param�tre libre permettant de valoriser les �quivalences.
	 */
	private double calcule2(int degreEquivalent, int degreSubclass)
	{
		return 1. - 1./(2. * degreEquivalent + 1. * degreSubclass) / (double) node.getOntologyAxiomCount();
	}

	/**
	 * 3) Distance bas�e sur la pr�servation (Euzenat) : On valorise le chemin qui a le plus de mappings
	 * d(oi,oj) = 1 - ( nbConceptsMapp�sEntre(oi,oj) / nbConcept(oi) );
	 */
	private double calcule3(int otherNodeId)
	{
		return new Double (getNombreConceptsMappes(otherNodeId)) / new Double (node.getOntologyAxiomCount());
	}

	/**
	 * 4) Distance bas�e sur la pr�servation (Lionel) : On valorise le chemin qui a le plus de mappings
	 * d(oi,oj) = max (  nbConceptsMapp�sEntre(oi,oj)  / nbTotalDeMappings(oi)  );
	 */
	private double calcule4(int otherNodeId)
	{
		return new Double (getNombreConceptsMappes(otherNodeId)) / (2. * new Double (node.getEquivalentMappingsCount()) + new Double (node.getSubsomptionMappingsCount()));
	}

	/**
	 * Renvoie la distance entre le noeud courant et un autre noeud du r�seau.<br />
	 * A faire : interface + classe abstraite, et un calcul sp�cifique dans chaque classe d�riv�e + documentation...
	 * Plusieurs algorithmes de calcul peuvent �tre impl�ment�s
	 * @param otherNodeId Id du noeud par rapport auquel on calcule la distance
	 * @return Une valeur positive ou nulle (nulle si ontologies �quivalentes) repr�sentant la distance s�mantique entre les noeuds
	 */
	public double calcule(int otherNodeId)
	{
		int degreEquivalent = node.getEquivalentMappingsAvec(otherNodeId).size();
		int degreSubclass = node.getSubsomptionMappingsAvec(otherNodeId).size();
/*
 * Atention truc crade : c'est ici qu'il faut changer l'appel pour avoir la m�thode de calcul souhait�e.
 */
		double res = calcule3(otherNodeId);
		node.setDistanceAvec(otherNodeId, res);
		return res;
	}

	private int getNombreConceptsMappes(int otherNodeId) {
		ArrayList<OWLClass> conceptsMappes = new ArrayList<OWLClass>();
		for(EquivalentMapping equ: node.getEquivalentMappingsAvec(otherNodeId)) {
			conceptsMappes.add(equ.getLocalClass().asOWLClass());
		}
		for(SubsomptionMapping sub: node.getSubsomptionMappingsAvec(otherNodeId)) {
			conceptsMappes.add(sub.getLocalClass().asOWLClass());
		}
		return conceptsMappes.size();
	}
}
