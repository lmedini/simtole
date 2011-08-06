package intersimul.old;

import intersimul.util.*;
import java.util.*;

import java.util.ArrayList;

public class DocumentProfiles extends Profiles {
	
	
	
	public DocumentProfiles( int nbP , int nbT ){
		
		super(nbP, nbT );
		
	}
	
public DocumentProfiles( String[] p , String[] t ){
		
		super(p,t);
	}
	
		
	
	protected void init(){
		
		for( int i = 0 ; i < this.profiles.length ; i++ ){
			this.profiles[i] = "PrD_" + i;
			
				
		}
		
		for( int j = 0 ; j < this.themes.length ; j++ ){
			this.themes[j] = "Th_" + j;
		}
		
	}
	
	
	

}
