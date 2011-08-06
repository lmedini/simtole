package intersimul.model.experiment;

/**
 * A Scenario is a part of the data of an experiment.
 * It stores both primary information and results of a 
 */
public class Scenario {
	private String mode;
	private int idCycle;
	private int idSource;
	private int idCible;
	private String name;
	public Scenario(String mode, int idCycle, int idSource, int idCible){
		this.mode = mode;
		this.idCible = idCible;
		this.idSource = idSource;
		this.idCycle = idCycle;
		//this.name= name;
	}

	@Override
	public String toString(){
		return "mode: " + this.mode + ";idCycle" + this.idCycle + ";idSource" + this.idSource + ";idCible" + this.idCible;
	}

	public void setName(String name){
		this.name  = name;
	}

	public String getName(){
		return this.name;
	}

	public String getMode(){
		return this.mode;
	}

	public void setMode(String mode){
		this.mode= mode;
	}

	public int getIdSource(){
		return this.idSource;
	}

	public void setIdSource(int id){
		this.idSource= id;
	}

	public int getIdCycle(){
		return this.idCycle;
	}

	public void setIdCycle(int id){
		this.idCycle= id;
	}

	public int getIdCible(){
		return this.idCible;
	}

	public void setIdCible(int id){
		this.idCible= id;
	}
}