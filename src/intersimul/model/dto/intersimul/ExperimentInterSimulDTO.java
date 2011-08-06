package intersimul.model.dto.intersimul;

import intersimul.model.experiment.ParamLoader;

import java.io.Serializable;

/**
 * DTO created by an Experiment to be saved in an InterSimul config file.
 * Only needs to be serialized (no method to update the object from the config file).
 * @author Lionel Médini
 *
 */
public class ExperimentInterSimulDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private int peersNb;
	private int queriesNb;
	private int neighborsNb;
	private int ttl;
	private String topologyStrategy;
	private boolean dynNetwork;
	private boolean experimentsByCycle;
	private int cyclesNb;
	private String protocols;
	private String nodeType;

	/**
	 * Transforms a simulation into the data stored in its config file
	 * @return the contain of the config file related to the current simulation
	 */
	public String serialize(){
		String res = "";
	
		res += ParamLoader.SIM_NAME + " " + getName() + "\n";
		res += ParamLoader.NB_PEER + " " +getPeersNb()+ "\n";
		res += ParamLoader.NB_QRY + " " + getQueriesNb()+ "\n";
		res += ParamLoader.DEGREE + " " + getNeighborsNb()+ "\n";
		res += ParamLoader.TTL + " " +getTtl( )+ "\n";
		res += ParamLoader.TOPOLOGY + " " + getTopologyStrategy() + "\n";
		res += ParamLoader.DYNAMICS + " " + isDynNetwork()+ "\n";
		res += ParamLoader.EXP_CYCLE + " " + isExperimentsByCycle( )+ "\n";
		res += ParamLoader.NB_CYCLES + " " + getCyclesNb()+ "\n";
		res += ParamLoader.PROTOCOLES + " " + getProtocols()+ "\n";
		res += ParamLoader.NODE + " " + getNodeType();
	
		return res;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPeersNb() {
		return peersNb;
	}

	public void setPeersNb(int peersNb) {
		this.peersNb = peersNb;
	}

	public int getQueriesNb() {
		return queriesNb;
	}

	public void setQueriesNb(int queriesNb) {
		this.queriesNb = queriesNb;
	}

	public int getNeighborsNb() {
		return neighborsNb;
	}

	public void setNeighborsNb(int neighborsNb) {
		this.neighborsNb = neighborsNb;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public String getTopologyStrategy() {
		return topologyStrategy;
	}

	public void setTopologyStrategy(String topologyStrategy) {
		this.topologyStrategy = topologyStrategy;
	}

	public boolean isDynNetwork() {
		return dynNetwork;
	}

	public void setDynNetwork(boolean dynNetwork) {
		this.dynNetwork = dynNetwork;
	}

	public boolean isExperimentsByCycle() {
		return experimentsByCycle;
	}

	public void setExperimentsByCycle(boolean experimentsByCycle) {
		this.experimentsByCycle = experimentsByCycle;
	}

	public int getCyclesNb() {
		return cyclesNb;
	}

	public void setCyclesNb(int cyclesNb) {
		this.cyclesNb = cyclesNb;
	}

	public String getProtocols() {
		return protocols;
	}

	public void setProtocols(String protocols) {
		this.protocols = protocols;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
}