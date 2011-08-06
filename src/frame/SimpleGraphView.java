package frame;
import intersimul.model.experiment.Experiment;
import intersimul.view.NetworkGraphView;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.xml.sax.SAXException;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class SimpleGraphView {
	public GraphSimple sgv;
	public static JFrame frame;
	NetworkGraphView gpanel = null;
	DefaultModalGraphMouse<Integer,String> gm = null;
	public VisualizationViewer<Integer,String> vv;

	public SimpleGraphView(Experiment expe) throws SAXException{
		sgv = new GraphSimple(expe); 

		//Layout<Integer, String> layout = new CircleLayout(sgv);
		//SpringLayout layout = new SpringLayout(sgv);
		FRLayout<Integer,String> layout = new FRLayout(sgv);

		layout.setSize(new Dimension(400,400));
	    vv =new VisualizationViewer<Integer,String>(layout);

		vv.setPreferredSize(new Dimension(450,450));
		//name of node
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());	
		//position of the text in node
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		//gm = new DefaultModalGraphMouse();
		gm = new DefaultModalGraphMouse<Integer,String>();
		vv.setGraphMouse( gm );
		gm.add( new PopupMouse() );
	}

	public void loadGPane(NetworkGraphView gp){
		gp.add(this.vv);
	}

	public void setMode(int nummode){

		switch(nummode){
		case 0 : gm.setMode(ModalGraphMouse.Mode.TRANSFORMING); break;
		case 1 : gm.setMode(ModalGraphMouse.Mode.PICKING); break;
		}
	}
}