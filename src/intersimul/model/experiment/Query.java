package intersimul.model.experiment;

public class Query extends Data{
	
	int ttl = 0;
	String qtype = null;
	String basedDocLocal = null;
	int cycleStart = -1;

	public Query() {
		
	}

	public Query(int did, Profile profile , String type , String isLocal , int cycleStart ) {
		super( did , profile );
		this.qtype = type;
		this.basedDocLocal = isLocal;
		this.cycleStart = cycleStart;
		
	}

	public String getBasedDocLocal() {
		return basedDocLocal;
	}

	public int getCycleStart() {
		return cycleStart;
	}

	public String getQtype() {
		return qtype;
	}

	public int getTtl() {
		return ttl;
	}

}
