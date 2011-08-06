package intersimul.old;

import intersimul.util.*;
import java.util.*;

import java.util.ArrayList;

public class QueryProfiles extends Profiles {
	
	
	
	public QueryProfiles( int nbP , int nbT ){
		
		super(nbP, nbT );
		
	}
	
public QueryProfiles( String[] p , String[] t ){
		
		super(p,t);
	}
	
		
	
	protected void init(){
		
		for( int i = 0 ; i < this.profiles.length ; i++ ){
			this.profiles[i] = "PrQ_" + i;
			
				
		}
		
		for( int j = 0 ; j < this.themes.length ; j++ ){
			this.themes[j] = "Th_" + j;
		}
		
	}
	
	
	

}