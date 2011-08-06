package frame;

import intersimul.model.experiment.Experiment;
import intersimul.model.experiment.ParamLoader;
import intersimul.util.xml.TopologyHandler;

import java.util.ArrayList;

import org.xml.sax.SAXException;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;


public class GraphSimple extends DirectedSparseMultigraph<Object, Object>{
	private static final long serialVersionUID = 1L;
	//UndirectedSparseMultigraph g;
	Integer[] myNodes ;

	public GraphSimple(Experiment expe) throws SAXException{
		TopologyHandler px = new TopologyHandler();
		ArrayList<ArrayList<Integer>> tableNode;
		ArrayList<Integer> neighbors;
		ArrayList<MyLink> links;

		// récupération de la topologie
		tableNode = px.parseXMLTopology(ParamLoader.getXMLdocument(expe,"topology"));

		// les liens
		links = new ArrayList<MyLink>();

		myNodes = new Integer[tableNode.size()];

		for (int i =0;i<tableNode.size();i++)
			myNodes[i] = new Integer(i);

		// poids : 2.0 et capacité: 48
		MyLink linkImg = 	new MyLink(-1, -1);
		links.add(linkImg);

		// pour chaque noeud
		for (int i =0;i<tableNode.size();i++){
			// récupération des voisins du noeud courant
			neighbors = tableNode.get(i);

			// pour chacun des voisins
			for (int j=0;j<neighbors.size();j++){

				//if (!this.addEdge(new MyLink(2.0, 48), myNodes[neighbors.elementAt(j)], myNodes[i] ,EdgeType.UNDIRECTED))
				MyLink link = 	new MyLink(i, neighbors.get(j));

				//MyLink linkT = 	new MyLink(2.0, 48, neighbors.elementAt(j),i);
				//if (!this.findLink(link, links)){
					//System.out.println(links.size());
					//links.add(link);
					this.addEdge(link, myNodes[i], myNodes[neighbors.get(j)],EdgeType.DIRECTED); 
				//}
			}
		}
		System.out.println();
	}

	public int getNode(int in){
		return this.myNodes[in];
	}

	public boolean findLink(MyLink link,ArrayList<MyLink> links){
		boolean test=false;
		int i =0;
		while ((i<links.size()) && (test == false)){
			MyLink linkT = links.get(i);
			if (linkT.testLink(link)){
				test= true;
			}
			i++;
		}	
		return test;
	}
}