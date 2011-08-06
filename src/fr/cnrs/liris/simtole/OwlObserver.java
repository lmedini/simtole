package fr.cnrs.liris.simtole;

import fr.cnrs.liris.simtole.mapping.Mapping;
import fr.cnrs.liris.simtole.protocols.InfoLocalProtocol;
import fr.cnrs.liris.simtole.protocols.AbstractQueryFloodingProtocol;
import fr.cnrs.liris.simtole.protocols.QueryQueueManagementProtocol;
import fr.cnrs.liris.simtole.protocols.SemResultProtocol;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

public class OwlObserver implements Control{
	
	private static final String PAR_PROT_QUERY = "query_protocol";
	private static final String PAR_PROT_QUEUE = "queryqueue_protocol";
	private static final String PAR_PROT_RESULT = "result_protocol";
	private static final String PAR_PROT_INFO = "info_protocol";
	
	private final int pid_query;
	private final int pid_query_file;
	private final int pid_result;
	private final int pid_info;
	
	private final String name;
	
	public OwlObserver(String name){
		this.name=name;
		pid_query = Configuration.getPid(name + "." + PAR_PROT_QUERY);
		pid_query_file = Configuration.getPid(name + "." + PAR_PROT_QUEUE);
		pid_result = Configuration.getPid(name + "." + PAR_PROT_RESULT);
		pid_info = Configuration.getPid(name + "." + PAR_PROT_INFO);
	}
	
	public boolean execute() {
		for (int i = 0; i < Network.size(); i++)
		{
			QueryQueueManagementProtocol protocolQ = (QueryQueueManagementProtocol) Network.get(i).getProtocol(pid_query_file);
			AbstractQueryFloodingProtocol self = (AbstractQueryFloodingProtocol)Network.get(i).getProtocol(pid_query);
			SemResultProtocol protocolR=(SemResultProtocol)Network.get(i).getProtocol(pid_result);
			InfoLocalProtocol protocolInfo=(InfoLocalProtocol)Network.get(i).getProtocol(pid_info);
			
			protocolInfo.setNbMapping(protocolInfo.getMappingsCount());
			int nbEqui=0;
			int nbSub=0;
			for(int j=0; j<protocolInfo.getMappingsCount(); j++) {
				Mapping m = protocolInfo.getMapping(j);
				if(m != null) {
					if(m.getType().equals(Mapping.EQUIVALENT_TYPE)) {
						nbEqui++;
					} else if(m.getType().equals(Mapping.SUBSOMPTION_TYPE)) {
						nbSub++;
					}
				}
			}
			protocolInfo.setNbEquivalent(nbEqui);
			protocolInfo.setNbSubsomption(nbSub);

			//System.out.println("Node "+Network.get(i).getID()+" "+protocolInfo.getNbMapping()+" "+protocolInfo.getNbEquivalent()+" "+protocolInfo.getNbSubsomption());
			//System.out.println("+++++++++++++++++nombre d'allignement local est "+protocolInfo.getNbAlignements());
		}
		//System.out.println("+++++++++++++++++++++++nombre messages transmis est "+InfoGlobal.nbMessage);
		return false;
    }
}
