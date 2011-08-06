package example.test;


import peersim.core.*;

public class TableauHolder implements Protocol
{
	


	//--------------------------------------------------------------------------
	//Fields
	//--------------------------------------------------------------------------
		
	/** Value held by this protocol */
	protected int[] tab;
	protected int value;
		

	//--------------------------------------------------------------------------
	//Initialization
	//--------------------------------------------------------------------------

	/**
	 * constructer
	 */
	public TableauHolder(String prefix)
	{
		tab=new int[10];
		value=0;
	}

	//--------------------------------------------------------------------------

	/**
	 * Clones the value holder.
	 */
	public Object clone()
	{
		TableauHolder svh=null;
		try { svh=(TableauHolder)super.clone(); 
			  svh.tab=(int[])tab.clone();
		}
		catch( CloneNotSupportedException e ) {} // never happens
		return svh;
	}

	//--------------------------------------------------------------------------
	//methods
	//--------------------------------------------------------------------------
	public void setValue(int i,int val)
	{
		tab[i]=val;
	}

	//--------------------------------------------------------------------------

	/**
	 * Returns the value as a string.
	 */
	//public String toString() { return ""+value; }


}
