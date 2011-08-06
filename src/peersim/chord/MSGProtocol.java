package peersim.chord;

import java.util.ArrayList;
import peersim.core.Protocol;

/**
 * Classe MSGProtocol -
 * @author TOURE Baïla
 * @author WAÏS Robleh
 */

public class MSGProtocol implements Protocol {
	
	// prefix du protocol
	protected String prefix = null;
	// liste de message propre a chaque pair
	protected ArrayList<LookUpMessage> msgList = new ArrayList<LookUpMessage>();

	public MSGProtocol(String prefix){
		this.prefix = prefix;
	}
	
	//Pile de messages a traiter
	public ArrayList<LookUpMessage> getMsgList() {
		return msgList;
	}
	
	//Methode pour ajouter un message a traiter
	public void ajoutMessageAtraiter(LookUpMessage message){
		this.msgList.add(message);
	}

	public void setMsgList(ArrayList<LookUpMessage> msgList) {
		this.msgList = msgList;
	}
	
	public Object clone() {
		MSGProtocol cloneObj = new MSGProtocol(this.prefix);
		for (LookUpMessage message:this.msgList) {
			cloneObj.msgList.add((LookUpMessage)message.clone());
		}
		return cloneObj;
	}
}
