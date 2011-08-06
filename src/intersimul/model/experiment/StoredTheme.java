package intersimul.model.experiment;

public class StoredTheme {
	
	Theme ctheme = null;
	int proportion = -1 ;
	
	
	public StoredTheme( Theme  ctheme , int proportion ){
		this.ctheme = ctheme;
		this.proportion = proportion;
		
	}


	public Theme getCtheme() {
		return ctheme;
	}


	public void setCtheme(Theme ctheme) {
		this.ctheme = ctheme;
	}


	public int getProportion() {
		return proportion;
	}


	public void setProportion(int proportion) {
		this.proportion = proportion;
	}
	
	

}
