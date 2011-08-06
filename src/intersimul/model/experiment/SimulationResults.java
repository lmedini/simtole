package intersimul.model.experiment;

/**
 * Encapsulates results of a simulation
 * @author Lionel Médini
 *
 */
public class SimulationResults {

	private String name;
	private String nbMessages;
	public String nbMappingInit;
	public String nbMappingFinal;


	public SimulationResults(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNbMessages(String a){
		this.nbMessages = a;
	}

	public String getNbMessages() {
		return this.nbMessages;
	}

	public void setMappingInit(String value) {
		this.nbMappingInit = value;
	}

	public void setMappingFinal(String value) {
		this.nbMappingFinal = value;	
	}

	public String getNbMappingInit() {
		return nbMappingInit;
	}

	public void setNbMappingInit(String nbMappingInit) {
		this.nbMappingInit = nbMappingInit;
	}

	public String getNbMappingFinal() {
		return nbMappingFinal;
	}

	public void setNbMappingFinal(String nbMappingFinal) {
		this.nbMappingFinal = nbMappingFinal;
	}
}
