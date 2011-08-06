package intersimul.old;

import intersimul.util.*;
import java.util.*;

import java.util.ArrayList;

public abstract class Profiles {
	
	protected String[] profiles = null;
	protected String[] themes = null;
	protected int[][] values = null;
	public static Random alea = new Random(123654789);
	
	
	public Profiles( int nbP , int nbT ){
		
		this.profiles = new String[nbP];
		this.themes = new String[nbT];
		this.values = new int[nbP][nbT];
		
		this.init();
		
	}
	
public Profiles( String[] p , String[] t ){
		
		this.profiles = p;
		this.themes = t;
		this.values = new int[p.length][t.length];
		
	}
	
		
	
	protected abstract void init();
	
	
	public void randomDistribution(){
		int sum = 0;
		int val = 0;
		
		int nbTh = 0;
		int[] index = null;
		int[] tags = null;
		ArrayList<Integer> indexes = new ArrayList<Integer>(this.themes.length);
		
		
		this.reset();
		
		
		for( int i = 0 ; i < this.profiles.length ; i++ ){
			sum = 0;
			// on choisit un nombre aléatoire de thèmes
			nbTh = 1 + alea.nextInt( this.themes.length - 2 );
			index = new int[nbTh];
			tags = new int[nbTh];
			indexes.clear();
			
			for( int j = 0 ; j < this.themes.length ; j++ ){
				indexes.add( new Integer(j));
			}
			
			
				
			for( int j = 0 ; j < this.themes.length - nbTh ; j++ ){
				val = alea.nextInt(indexes.size());
				val = indexes.remove(val).intValue();
				
			}
			
			for( int j = 0 ; j < indexes.size() ; j++ ){
				val = indexes.get(j).intValue();
				index[j] = val ;
			
			}
			
			
			for( int j = 0 ; (j < index.length - 1) & (sum < 100) ; j++ ){
				
				val = alea.nextInt( 100 - sum );
			
				sum += val; 
				this.values[i][ index[j] ] = val;
			}
			if( sum < 100){
				this.values[i][ index[ index.length - 1] ] = 100 - sum;
			}
		}
		
		
	}
	
	public void reset(){
		
		for( int i = 0 ; i < this.profiles.length ; i++ ){
			for( int j = 0 ; j < this.themes.length ; j++ ){
				this.values[i][j] = 0;
			}
		}
		
	}
	
	
	
	public void verify() throws ProfileConsistencyException{
		ArrayList<Integer> illegaleRow = new ArrayList<Integer>();
		int sum = 0;
		
		for( int i = 0 ; i < this.profiles.length ; i++ ){
		sum = 0;
		for( int j = 0 ; j < this.themes.length ; j++ ){
			sum +=  this.values[i][j];
		}
		if ( sum != 100 ){
			illegaleRow.add(  new Integer(i) );
			//SOP.prtln("add " + i + " : " + sum ); 
		}
	}
	
		if ( illegaleRow.size() > 0 ){
			throw new ProfileConsistencyException( illegaleRow );
		}
		
	}

	public String[] getProfiles() {
		return profiles;
	}
	public String getProfiles(int i) {
		return profiles[i];
	}

	public void setProfiles(String[] profiles) {
		this.profiles = profiles;
	}

	public String[] getThemes() {
		return themes;
	}
	public String getTheme( int i) {
		return themes[i];
	}

	public void setThemes(String[] themes) {
		this.themes = themes;
	}

	public int[][] getValues() {
		return values;
	}
	public int getValues(int i , int j) {
		return values[i][j];
	}

	public void setValues(int[][] values) {
		this.values = values;
	}
	
	

}
