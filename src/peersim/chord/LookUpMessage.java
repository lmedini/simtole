package peersim.chord;

import java.math.*;
import java.util.ArrayList;
import peersim.core.*;

public class LookUpMessage implements ChordMessage {

	private Node sender;

	private BigInteger targetId;

	private int hopCounter = -1;
	
	//Chemin emprunté par le message
	private ArrayList<Node> chemin;
	
	private int noCycle;
	
	public LookUpMessage(){}

	public LookUpMessage(Node sender, BigInteger targetId) {
		this.sender = sender;
		this.targetId = targetId;
		this.chemin = new ArrayList<Node>();
	}

	public void increaseHopCounter() {
		hopCounter++;
	}

	/**
	 * @return the senderId
	 */
	public Node getSender() {
		return sender;
	}
	
	public void setSender(Node sender) {
		this.sender = sender;
	}

	/**
	 * @return the target
	 */
	public BigInteger getTarget() {
		return targetId;
	}
	
	public void setTargetId(BigInteger targetId) {
		this.targetId = targetId;
	}

	/**
	 * @return the hopCounter
	 */
	public int getHopCounter() {
		return hopCounter;
	}
	
	public void resetHopCounter(){
		this.hopCounter = 0;
	}
	
	public ArrayList<Node> getChemin() {
		return chemin;
	}
	
	//Ajout d'un noeud emprunté par le message
	public void ajoutNoeud(Node noeud){
		this.chemin.add(noeud);
	}
	
	//Affichage du chemin emprunté par le message
	public void afficheChemin(){
		System.out.println("Chemin emprunté par le message");
		for(int i = 0;i<chemin.size();i++){
			System.out.print(this.chemin.get(i).getID() + " ");
		}
		System.out.println(" ");
	}
	
	public int getNoCycle() {
		return noCycle;
	}

	public void setNoCycle(int noCycle) {
		this.noCycle = noCycle;
	}
	
	public Object clone(){
		LookUpMessage cloneObj = new LookUpMessage();
		cloneObj.setSender(this.sender);
		cloneObj.setTargetId(this.targetId);
		return cloneObj;
	}
}
