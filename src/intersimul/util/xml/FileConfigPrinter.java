package intersimul.util.xml;

import intersimul.model.experiment.ExperimentBuilder;

import java.io.FileWriter;
import java.io.IOException;

public class FileConfigPrinter {
	public static final String SPACE = "   ";
	String spc = "";
	FileWriter fw = null ;
	
	public FileConfigPrinter(String url) throws IOException{
		fw = new FileWriter( url );
	}
	
	
	public void write(  ) throws IOException{
		
	}
	
	public void close() throws IOException {
		
		fw.close();
		
	}
}
