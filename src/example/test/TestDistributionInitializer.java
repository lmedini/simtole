package example.test;

import java.util.Random;
import peersim.config.*;
import peersim.core.*;


public class TestDistributionInitializer implements Control
{
	private static final String PAR_VALUE = "value";
	private static final String PAR_PROT = "protocol";
	private final int pid;
	private final int value;
	
	//constructeur
    public TestDistributionInitializer(String prefix) {
        value = Configuration.getInt(prefix + "." + PAR_VALUE);
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
    }
	

	@Override
	public boolean execute() {
		int temp;
		Random rand = new Random();
		for (int i = 0; i < Network.size(); i++) {
            TableauHolder prot = (TableauHolder) Network.get(i).getProtocol(pid);
            for(int j=0;j<10;j++)
            {
            	temp=rand.nextInt(value);
            	prot.setValue(j, temp);
            }
        }

		return false;
	}

}
