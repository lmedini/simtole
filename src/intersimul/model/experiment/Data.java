package intersimul.model.experiment;

public abstract class Data {
	
	int did = 0;
	Profile profile = null;
	

	public Data() {
		
	}

	public Data(int did ,  Profile profile) {
		this.did = did;
		this.profile = profile;
		
	}


	public long getDid() {
		return did;
	}


	public Profile getProfile() {
		return profile;
	}

	
}
