package intersimul.model.dao;

import intersimul.model.experiment.Experiment;
import intersimul.model.experiment.ParamLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ExperimentDAO {

	/**
	 * Transforms a simulation into the data stored in its config file
	 * @return the contain of the config file related to the current simulation
	 */
	private static String getInterSimulConfig(Experiment expe){
		String res = "";

		res += ParamLoader.SIM_NAME + " " + expe.getName() + "\n";
		res += ParamLoader.NB_PEER + " " +expe.getPeersNb()+ "\n";
		res += ParamLoader.NB_QRY + " " + expe.getQueriesNb()+ "\n";
		res += ParamLoader.DEGREE + " " + expe.getNeighborsNb()+ "\n";
		res += ParamLoader.TTL + " " +expe.getTtl( )+ "\n";
		res += ParamLoader.TOPOLOGY + " " + expe.getTopologyStrategy() + "\n";
		res += ParamLoader.DYNAMICS + " " + expe.isDynNetwork()+ "\n";
		res += ParamLoader.EXP_CYCLE + " " + expe.isExperimentsByCycle( )+ "\n";
		res += ParamLoader.NB_CYCLES + " " + expe.getCyclesNb()+ "\n";
		res += ParamLoader.PROTOCOLES + " " + expe.getProtocols()+ "\n";
		res += ParamLoader.NODE + " " + expe.getNodeType();

		return res;
	}

	/**
	 * Returns the config String needed by the PeerSim initializers
	 * @return a String to be written in the PeerSim config file
	 */
	private static String getPeerSimConfig(Experiment expe){
		String res = "";

		res +="networkName " + expe.getName() + "\n";
		res +="random.seed 1234567890" +"\n";
		res +="simulation.cycles" + " " + expe.getCyclesNb() + "\n";
		res +="network.size " +expe.getPeersNb()+ "\n";
		res +="simulneedinit.needinit.nbrequetes" + " " +expe.getQueriesNb()+ "\n";
		res +="ttl" + " " +expe.getTtl()+ "\n";
		//Ajout Lionel :
		res += "network.node" + " " + expe.getNodeType()+ "\n";
		res += ParamLoader.NB_PEER + " " + expe.getPeersNb()+ "\n";

		ArrayList<String> listProtocols = new ArrayList<String>(); 
		BufferedReader buff = null;
		String line = "";

		try{
			buff = new BufferedReader( new FileReader( ParamLoader.getProtocolsList(expe) ) );
			while( buff.ready()){
				line = buff.readLine();
				listProtocols.add(  line );
				res += line + "\n";
			}
			buff.close();
		}catch( IOException ioe ){
			ioe.printStackTrace();
		}
		return res;
	}


	/**
	 * Writes an experiment properties in its InterSimul config file
	 */
	public static void saveExperimentInInterSimulFormat(Experiment s) {
		String path = s.getFileUrl();
		System.out.println("Sauvegarde de l'expérimentation " + s.getName() + " au format InterSimul dans : " + path);
		try {
			String vals = "";
			FileWriter fw = new FileWriter(path);
			vals = getInterSimulConfig(s);
			fw.write(vals);
			fw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Write an experiment properties in its PeerSim config file
	 */

	public static void saveExperimentInPeerSimFormat(Experiment s) {
		String path = ParamLoader.basePath + "." + ParamLoader.slash + "example" + ParamLoader.slash + s.getName();
		System.out.println("Sauvegarde de l'expérimentation " + s.getName() + " au format PeerSim dans : " + path);
		try {
			String vals = "";
			FileWriter fw = new FileWriter(path);
			vals = getPeerSimConfig(s);
			fw.write(vals);
			fw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}