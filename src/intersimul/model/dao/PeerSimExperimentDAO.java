/**
 * 
 */
package intersimul.model.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import intersimul.model.experiment.Experiment;
import intersimul.model.experiment.ParamLoader;

/**
 * @author Lionel Médini
 *
 */
public class PeerSimExperimentDAO extends AbstractDAO {

	/**
	 * @param expe
	 */
	public PeerSimExperimentDAO(Experiment expe) {
		super(expe);
		this.path = ParamLoader.basePath + "." + ParamLoader.slash + "example" + ParamLoader.slash + expe.getName();
	}

	/* (non-Javadoc)
	 * @see intersimul.model.dao.AbstractDAO#getConfig(intersimul.model.experiment.Experiment)
	 */
	@Override
	protected String getConfig(Experiment expe) {
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
}