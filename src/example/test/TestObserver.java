package example.test;


import peersim.config.*;
import peersim.core.*;
import peersim.util.IncrementalStats;

public class TestObserver implements Control{
	
	private static final String PAR_PROT = "protocol";
	private static final String PAR_ACCURACY = "accuracy";
	
	private final int pid;
	private final String name;
	private final double accuracy;
	
	public TestObserver(String name){
		this.name=name;
		pid = Configuration.getPid(name + "." + PAR_PROT);
		accuracy = Configuration.getDouble(name + "." + PAR_ACCURACY, -1);
	}
	
	public boolean execute() {
        long time = peersim.core.CommonState.getTime();

        IncrementalStats is = new IncrementalStats();

        for (int i = 0; i < Network.size(); i++) {

            //SingleValue protocol = (SingleValue) Network.get(i).getProtocol(pid);
        	TestFunction protocol = (TestFunction) Network.get(i).getProtocol(pid);
            is.add(protocol.getValue());
        }

        /* Printing statistics */
        System.out.println(name + ": " + time + " " + is);

        /* Terminate if accuracy target is reached */
        return (is.getStD() <= accuracy);
    }
	
	

	
}
