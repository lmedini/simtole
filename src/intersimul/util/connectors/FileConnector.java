package intersimul.util.connectors;

import java.io.File;
import java.util.ArrayList;



public class FileConnector {
	
	// r�pertoire racine des fichiers � r�cup�rer
	String pathDir = null;
	// liste des fichiers collect�s
	ArrayList<File> allFiles=new ArrayList<File>();
	
	
	public FileConnector( String pth ){
		
		this.pathDir = pth;
		this.allFiles=new ArrayList<File>();
		this.allFiles.clear();
		
		this.loadFiles();
		
	}
	
	
	

	/**
	 * R�cup�re les fichiers
	 */
	public void loadFiles(){
		
		// parcour r�cursif de l'arborescence de fichiers
		recurseDirs( this.pathDir ,this.allFiles);
		
		System.out.println("Scan of " + this.pathDir + " : " + this.allFiles.size() + " files loaded !");
		
			
	}
	
	
	
	/**
	 * 
	 * @return : la liste des uri des fichiers charg�s
	 */
	public String[] getFiles(){
		
		String[] files = new String[this.allFiles.size()];
		
		for(int i = 0 ; i< this.allFiles.size() ; i++ ){
			
			files[i] = this.allFiles.get(i).getPath();
			
		}	
		
		return files;
		
	}
	
	
    
    /**
     * 
     * @param repertoire :  r�pertoire racine � explorer
     * @param files : liste des fichiers rempli par effet de bord
     */
    private void recurseDirs(String repertoire,ArrayList<File> files)
    {
    	File fichier = new File(repertoire);
    	String list[];
    	
    	if (fichier.isDirectory())
    	{
    		list = fichier.list();
    		for (int i = 0; i < list.length ; i++)
    		{
    			recurseDirs(repertoire + File.separatorChar + list[i], files);
    		}
    	}
    	else
    	{
  
    		allFiles.add(fichier);
    	}
 
    }

	

}
