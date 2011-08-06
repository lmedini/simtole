package fr.cnrs.liris.simtole.owl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import peersim.cdsim.CDSimulator;
import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import fr.cnrs.liris.simtole.protocols.InfoLocalProtocol;

public class WriteXML implements Control{
	
	private static final String PAR_PROT_QUERY = "query_protocol";
	private static final String PAR_PROT_QUEUE = "queryqueue_protocol";
	private static final String PAR_PROT_INFO = "info_protocol";
	
	private final int pid_query;
	private final int pid_info;
	private String nomScenario;
	private static final String NOM_SCENARIO = "nomScenario";
	
	public WriteXML(String name){
		pid_query = Configuration.getPid(name + "." + PAR_PROT_QUERY);
		Configuration.getPid(name + "." + PAR_PROT_QUEUE);
//		pid_result = Configuration.getPid(name + "." + PAR_PROT_RESULT);
		pid_info = Configuration.getPid(name + "." + PAR_PROT_INFO);
		nomScenario = Configuration.getString(NOM_SCENARIO);
	}
	
	public boolean execute() {
		try{
			if(CDState.getCycle()==CDSimulator.getCycles()-1)
			{
				
				
				
				String path3 = "out\\InfoGlobal.xml";
			    File f3=new File(path3);
			    
			    if(!f3.exists())
			    {
					f3.createNewFile();
			    }
			    
				WriteXMLfunctions writeInfoGlobal = new WriteXMLfunctions(path3);
				//writeInfoGlobal.writeHead("scenarioInfo");
				
				String path = "out\\NodesInfo.xml";
			    File f=new File(path);
			    
			    if(!f.exists())
			    {
					f.createNewFile();
			    }
				WriteXMLfunctions writeLocalInfo = new WriteXMLfunctions(path);
				writeLocalInfo.writeHead("nodesInfos");
				
				String path2 = "out\\TableCorrespondance.xml";
			    File f2=new File(path);
			    
			    if(!f2.exists())
			    {
					f2.createNewFile();
			    }
				WriteXMLfunctions writeMapping = new WriteXMLfunctions(path2);
				writeMapping.writeHead("mappings");
	
			    //ecrire dans fichier infoGlobal
				writeInfoGlobal.writeInfoGlobal(this.nomScenario);
				//ecire dans fichier nodesinfo et tablecorrespondance
				for (int i = 0; i < Network.size(); i++)
				{
					Node node = Network.get(i);
					//ecrire les info locaux
					InfoLocalProtocol protocolInfo=(InfoLocalProtocol)node.getProtocol(pid_info);
					int linkableID = FastConfig.getLinkable(pid_query);
					Linkable linkable = (Linkable) node.getProtocol(linkableID);
					writeLocalInfo.write(node,protocolInfo,linkable);
					//ecrire les tables mapping
					//QueryQueueManagementProtocol queryFileProtocol = (QueryQueueManagementProtocol)node.getProtocol(pid_query_file);
					writeMapping.write(node, protocolInfo);
					
				}
				writeLocalInfo.writeFin("nodesInfos");
				writeMapping.writeFin("mappings");
				//writeInfoGlobal.writeFin("scenarioInfo");
				writeLocalInfo.close();
				writeMapping.close();
				writeInfoGlobal.close();
				
				String pathStats = "out\\Stats.xml";
				File fileStats = new File (pathStats);
				
				if(!fileStats.exists())
			    {
					fileStats.createNewFile();
			    }
				
				// add node in Stats.xml
				
				Builder builder = new Builder();
				
				nu.xom.Document alertDoc = builder.build(new File(pathStats));
				nu.xom.Document weatherDoc =  builder.build(new File(path3));
				//nu.xom.Document mainDoc = builder.build(new File("out\\LanTest.xml"));

				nu.xom.Element root1 = alertDoc.getRootElement();
				
				//root.appendChild(alertDoc.getRootElement().copy());
				root1.appendChild(weatherDoc.getRootElement().copy());
				//root.replaceChild(root.getFirstChildElement("blank"), weatherDoc.getRootElement().copy());
				
				BufferedWriter out = new BufferedWriter(new FileWriter(pathStats));
				out.write(alertDoc.toXML());
				out.close();	
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ValidityException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
		
		return false;
    }

}
