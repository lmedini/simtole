package peersim.chord;

import java.util.ArrayList;
import java.util.Random;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

/**
 * Classe MSGProtocolInitializer -
 * @author TOURE Baïla
 * @author WAÏS Robleh
 */

public class MSGProtocolInitializer implements Control{

	private String prefix = null;
	
	private int pid;
	private int cpid;
	//private int nbmessages;
	protected static Random generateur = new Random(CommonState.r.getLastSeed());
	
	private static final String PAR_PROT = "protocol";
	private static final String PAR_MYPROT = "myprotocol";
	//private static final String PAR_NBMSG = "nbmsg";
	
	public MSGProtocolInitializer(String prefix){
		this.prefix = prefix;
		this.pid = Configuration.getPid(prefix + "." + PAR_PROT);
		this.cpid = Configuration.getPid(prefix + "." + PAR_MYPROT);
		//this.nbmessages = Configuration.getPid(prefix + "." + PAR_NBMSG);
		
	}
	@Override
	public boolean execute() {
		ArrayList<LookUpMessage> msgList = new ArrayList<LookUpMessage>();
		for (int j=0; j<1; j++){
			int size = Network.size();
			Node sender, target;
			int i = 0;
			do {
				i++;
				//Selection aléatoire de l'expediteur et du destinataire
				sender = Network.get(CommonState.r.nextInt(size));
				target = Network.get(CommonState.r.nextInt(size));
			} while (sender == null || sender.isUp() == false || target == null
					|| target.isUp() == false);
			//création d'un nouveau message
			LookUpMessage message = new LookUpMessage(sender, ((ChordProtocol) target.getProtocol(cpid)).chordId);
			int cycle = 1+generateur.nextInt(15);
			message.setNoCycle(cycle);
			msgList.add(message);
			MSGProtocol msg = (MSGProtocol) sender.getProtocol(pid);
			msg.setMsgList(msgList);
		}
		
		return false;
	}

}
