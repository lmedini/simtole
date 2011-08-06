package frame;

/**
 * Classe qui sert visiblement à tester s'il existe un lien entre 2 noeuds
 *
 */
public class MyLink {
	private int node1;
	private int node2;

	public MyLink(int node1, int node2){
		this.node1 = node1;
		this.node2 = node2;
	}

	public boolean testLink(MyLink link2){
		return (this.node1 == link2.node2)&&(this.node2==link2.node1);
	}
}