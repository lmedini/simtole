package intersimul.old;

import java.util.ArrayList;

public class ProfileConsistencyException extends Exception {
	
	ArrayList<Integer> rows = null;
	
	public ProfileConsistencyException( ArrayList<Integer> r ){
		this.rows = r;
				
	}
	
	public ArrayList<Integer> getIllegalRows(){
		return this.rows;
	}
	

}
