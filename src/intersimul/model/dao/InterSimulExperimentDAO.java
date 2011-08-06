/**
 * 
 */
package intersimul.model.dao;

import intersimul.model.experiment.Experiment;
import intersimul.model.experiment.ParamLoader;

/**
 * @author Lionel Médini
 *
 */
public class InterSimulExperimentDAO extends AbstractDAO {

	/**
	 * @param expe
	 */
	public InterSimulExperimentDAO(Experiment expe, String path) {
		super(expe);
		this.path = expe.getFileUrl();
	}

	/* (non-Javadoc)
	 * @see intersimul.model.dao.AbstractDAO#getConfig(intersimul.model.experiment.Experiment)
	 */
	@Override
	protected String getConfig(Experiment expe) {
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
}