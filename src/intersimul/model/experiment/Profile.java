package intersimul.model.experiment;

import java.util.ArrayList;

public class Profile {
	
	
	int pid = 0;
	String pname =  "";
	ArrayList<StoredTheme> themes = null;
	
	
	public Profile(){
		this.themes = new ArrayList<StoredTheme>(); 
		
	}
	
	public Profile( int tid , String tname ) {
		this();
		this.pid = tid;
		this.pname = tname;

	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public ArrayList<StoredTheme> getThemes() {
		return themes;
	}

	public void setThemes(ArrayList<StoredTheme> themes) {
		this.themes = themes;
	}

	public void addThemes(StoredTheme theme ) {
		this.themes.add(theme);
	}

}
