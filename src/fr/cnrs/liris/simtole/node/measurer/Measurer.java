package fr.cnrs.liris.simtole.node.measurer;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLClass;

import fr.cnrs.liris.simtole.mapping.EquivalentMapping;
import fr.cnrs.liris.simtole.mapping.SubsomptionMapping;
import fr.cnrs.liris.simtole.node.SemanticNode;

/**
 * Classe contenant la logique métier qui effectue le calcul de similarité.
 * Chaque measurer est rattaché à un pair (tout comme le reasoner), pour être plus proche du "cas réel" (pair avec toute son intelligence embarquée).<br />
 * Attention : j'ai fait un truc crade en nommant plusieurs fonctions privées calcule, calcule1, calcule2 avec des algos différents...
 * Il faut modifier la méthode publique calcule pour appeler celle qu'on souhaite utiliser.
 * @Author Lionel Médini
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
	 * 1) Distance basée sur le nombre de subsomptions:
	 * d1(o,o') = 1 - 1/( nbMappingsSubsomptions(o,o') );
	 */
	private double calcule1(int degreEquivalent, int degreSubclass)
	{
		return 1. - 1./(degreSubclass) / (double) node.getOntologyAxiomCount();
	}

	/**
	 * 2) Distance basée sur le nombre de subsomptions et d'équivalences:
	 * d2(o,o') = 1 - 1/(  alpha * nbMappingsEquivalence + nbMappingsSubsomptions(o,o') );
	 * avec alpha un paramètre libre permettant de valoriser les équivalences.
	 */
	private double calcule2(int degreEquivalent, int degreSubclass)
	{
		return 1. - 1./(2. * degreEquivalent + 1. * degreSubclass) / (double) node.getOntologyAxiomCount();
	}

	/**
	 * 3) Distance basée sur la préservation (Euzenat) : On valorise le chemin qui a le plus de mappings
	 * d(oi,oj) = 1 - ( nbConceptsMappésEntre(oi,oj) / nbConcept(oi) );
	 */
	private double calcule3(int otherNodeId)
	{
		return new Double (getNombreConceptsMappes(otherNodeId)) / new Double (node.getOntologyAxiomCount());
	}

	/**
	 * 4) Distance basée sur la préservation (Lionel) : On valorise le chemin qui a le plus de mappings
	 * d(oi,oj) = max (  nbConceptsMappésEntre(oi,oj)  / nbTotalDeMappings(oi)  );
	 */
	private double calcule4(int otherNodeId)
	{
		return new Double (getNombreConceptsMappes(otherNodeId)) / (2. * new Double (node.getEquivalentMappingsCount()) + new Double (node.getSubsomptionMappingsCount()));
	}

	/**
	 * Renvoie la distance entre le noeud courant et un autre noeud du réseau.<br />
	 * A faire : interface + classe abstraite, et un calcul spécifique dans chaque classe dérivée + documentation...
	 * Plusieurs algorithmes de calcul peuvent être implémentés
	 * @param otherNodeId Id du noeud par rapport auquel on calcule la distance
	 * @return Une valeur positive ou nulle (nulle si ontologies équivalentes) représentant la distance sémantique entre les noeuds
	 */
	public double calcule(int otherNodeId)
	{
		int degreEquivalent = node.getEquivalentMappingsAvec(otherNodeId).size();
		int degreSubclass = node.getSubsomptionMappingsAvec(otherNodeId).size();
/*
 * Atention truc crade : c'est ici qu'il faut changer l'appel pour avoir la méthode de calcul souhaitée.
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
