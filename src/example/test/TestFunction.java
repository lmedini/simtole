package example.test;

import peersim.cdsim.CDProtocol;
import peersim.core.*;
import peersim.config.FastConfig;

public class TestFunction extends TableauHolder implements CDProtocol{
	
	
	public TestFunction(String prefix){
		 super(prefix);
	}
	
	
	 public void nextCycle(Node node, int protocolID) {
		 int linkableID = FastConfig.getLinkable(protocolID);
	     Linkable linkable = (Linkable) node.getProtocol(linkableID);
         if (linkable.degree() > 0) {
            Node peer = linkable.getNeighbor(CommonState.r.nextInt(linkable.degree()));
        	 //Node peer = linkable.getNeighbor(4);
            // Failure handling
            if (!peer.isUp())
                return;

            TestFunction neighbor = (TestFunction) peer.getProtocol(protocolID);
            for(int i=0;i<10;i++)
            {
            	if(this.tab[i]==neighbor.tab[i])
            		{
            			this.value++;
            		}
            }
         }     
	 }
	 
	 public int getValue(){return value;}

}
