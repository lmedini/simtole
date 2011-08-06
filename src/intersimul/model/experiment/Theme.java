package intersimul.model.experiment;


public class Theme {

	int tid = 0;
	String tname =  "";

	
	public Theme() {
		
	}

	public Theme( int tid , String tname ) {
		this.tid = tid;
		this.tname = tname;

	}


	public int getTid() {
		return tid;
	}


	public void setTid(int tid) {
		this.tid = tid;
	}


	public String getTname() {
		return tname;
	}


	public void setTname(String tname) {
		this.tname = tname;
	}


	

}
