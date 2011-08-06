package fr.cnrs.liris.simtole_old.protocols;

import java.util.ArrayList;
import java.util.HashMap;

import fr.cnrs.liris.simtole.result.AbstractResult;

import peersim.core.Protocol;


public class ResultFileProtocol implements Protocol{
	// prefix du protocol
	protected String prefix = null;
	// table de hachage avec comme cl� le numero de cycle, et comme valeurs des listes de resultats
	
	protected HashMap<Integer,ArrayList<AbstractResult>> fileResultats = new HashMap<Integer,ArrayList<AbstractResult>>();
	
	public ResultFileProtocol (String prefix){
		this.prefix = prefix; 
	}
	
	public HashMap<Integer, ArrayList<AbstractResult>> getFileResultats() { return fileResultats; }

	public void setFileResultats(HashMap<Integer, ArrayList<AbstractResult>> fileResultats) {
		this.fileResultats = fileResultats;
	}
	
	public ArrayList<AbstractResult> getTabResultats(Integer cycle) {
		return fileResultats.get(cycle);
	}

	public Object clone(){
		return null;
	}
}
