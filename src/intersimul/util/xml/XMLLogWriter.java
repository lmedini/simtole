package intersimul.util.xml;

import java.io.FileWriter;
import java.io.IOException;


public class XMLLogWriter {

	public static final String SPACE = "   ";
	FileWriter fw = null;
	String path = "./cycles/"; 
	
	public XMLLogWriter( String cycle){
		
		try{
		this.fw = new FileWriter(path + "/" + cycle + ".xml",true);
		String desc = "";
		String amorce = amorce(0);
		
		// écriture de l'entete
		desc = amorce+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n\n";
		fw.write( desc );
		}catch(IOException e){e.printStackTrace();}
		
	}	
	
	/**
	 * Mémorisation des topologies dans un fichier de config
	 * @param ct
	 * @param lest
	 * @throws IOException
	 */
	
public void write( String msg ){
		
		try{
		
		fw.write( msg + "\n");
		
		
		}catch(IOException e){e.printStackTrace();}
		
		
	}
	
	
	
	
	public void close(){
		try{
			
			this.fw.close();
			
			
			}catch(IOException e){e.printStackTrace();}
		
	}
	
private String amorce( int i){
		
		switch( i  ){
		case 0 : return "";
		case 1 : return SPACE;
		default : return SPACE + amorce( i-1 );
		}	
	}
	
	
}
