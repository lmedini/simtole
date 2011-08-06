package intersimul.model.experiment;


import java.util.ArrayList;
import java.util.Random;

public class Simulation {

	private Experiment parent;
	private String name;

	public Simulation(Experiment parent, String name) {
		super();
		this.parent = parent;
		this.name = name;
	}

	public void loadTopology() throws UnknownTopologyException {
		String cTopology = 	parent.getTopologyStrategy();
		if(cTopology.equals(ParamLoader.RANDOM)){
			System.out.println("\n*** Topology defined:");
			this.randomTopology();
		} else{
			throw new UnknownTopologyException();
		}
	}

	/**
	 * Recopie de loadTopology (pour l'instant)
	 * @throws UnknownTopologyException
	 */
	public void updateTopology() throws UnknownTopologyException {
		String cTopology = 	parent.getTopologyStrategy();
		if(cTopology.equals(ParamLoader.RANDOM)){
			System.out.println("\n*** Topology redefined:");
			this.randomTopology();
		} else{
			throw new UnknownTopologyException();
		}
	}

	private void randomTopology(){
		Random r = parent.getRandom();
		int n = parent.getPeersNb();
		int k = parent.getNeighborsNb();
		ArrayList<Node> voisins = null;
		if( n < 2 )
			return;

		if( n <= k )
			k=n-1;

		int[] nodes = new int[n];
		for(int i=0; i<nodes.length; ++i)
			nodes[i]=i;

		int j=0;
		int newedge = 0;
		int tmp = 0;
		for(int i=0; i<n; ++i) {
			System.out.print("Voisins du noeud " + i + " : ");
			j=0;
			voisins = new ArrayList<Node>(k);
			voisins.clear();
			while(j<k) {
				newedge = j+r.nextInt(n-j);
				tmp = nodes[j];
				nodes[j] = nodes[newedge];
				nodes[newedge] = tmp;
				if( nodes[j] != i ) {
					voisins.add( parent.getNode(nodes[j]));
					j++;
					System.out.print(newedge + " ");
				}
			}
			parent.getNode(i).setNeighborhood( voisins );
			System.out.println();
		}
	}

	public String getName() {
		return name;
	}
}