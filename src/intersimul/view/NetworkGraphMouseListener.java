package intersimul.view;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import frame.MyLink;

public class NetworkGraphMouseListener implements GraphMouseListener<Integer> {
	// compteur servant à déterminer le nombre de noeuds sélectionnés pour la construction du scénario 
	int clickNb = 0;
	ArrayList<Integer> nodes;

	public NetworkGraphMouseListener(int clickNb, ArrayList<Integer> nodes) {
		super();
		this.clickNb = clickNb;
		this.nodes = nodes;
	}

	// redéfinition de l'action de clic
		@Override
		public void graphClicked(Integer arg0, MouseEvent e) {
			final VisualizationViewer<Integer,MyLink> vv1 = (VisualizationViewer<Integer, MyLink>) e.getSource();
			GraphElementAccessor<Integer,MyLink> pickSupport = vv1.getPickSupport();
			Point2D p = e.getPoint();

			int v = pickSupport.getVertex(vv1.getGraphLayout(), p.getX(), p.getY());

			// modification de la couleur du noeud

			// Affichage du noeud selectionné
			if( clickNb == 0)
				System.out.print("Scenario : FROM  N" + v);
			if( clickNb == 1)
				System.out.println(" TO N" + v);

			// mémorisation du noeud
			nodes.add(v);
		}

		@Override
		public void graphPressed(Integer arg0, MouseEvent arg1) {
			// TODO Auto-generated method stub
			//				infoNode = new JTextArea();
			//				infoNode.isVisible();
		}

		@Override
		public void graphReleased(Integer arg0, MouseEvent arg1) {
			// TODO Auto-generated method stub
		}

		public ArrayList<Integer> getNodes() {
			return nodes;
		}
}
