package frame;



import intersimul.model.experiment.ParamLoader;
import intersimul.util.xml.MappingsHandler;
import intersimul.util.xml.OntoInfoHandler;
import intersimul.util.xml.ScenarioHandler;
import intersimul.view.SPanel;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import org.xml.sax.SAXException;


import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;


public class PopupMouse extends AbstractPopupGraphMousePlugin implements MouseListener{
		
		public JPopupMenu popup = null;
		Hashtable listInfo;
		Hashtable listInfoOnto;
		
        protected void handlePopup( MouseEvent e ){
            final VisualizationViewer vv = (VisualizationViewer) e.getSource();
            //Point2D p = vv.inverseViewTransform( e.getPoint() );
            Point2D p =  e.getPoint() ;
            String pathNodeInfo = ParamLoader.basePath+ ParamLoader.slash + "out"+ ParamLoader.slash +"NodesInfo.xml";
    		String pathNodeOWL = ParamLoader.basePath+ ParamLoader.slash + "out"+ ParamLoader.slash +"OntoInfo.xml";
            MappingsHandler px = new MappingsHandler();
    		listInfo = new Hashtable();
    		
    		OntoInfoHandler pxOnto = new OntoInfoHandler();
    		listInfoOnto = new Hashtable();
    		try {
				listInfo = px.parseXMLNodesInfo(pathNodeInfo);
				listInfoOnto = pxOnto.parseInfoNodeOWL(pathNodeOWL);
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
            GraphElementAccessor pickSupport = vv.getPickSupport();
            if ( pickSupport != null ){
                Integer v = (Integer) pickSupport.getVertex( vv.getGraphLayout(), p.getX(), p.getY() );
                if ( v != null ){
                	popup = new JPopupMenu();
                    final String name = v.toString();
                    popup.add( new AbstractAction( name )
                    {
                        public void actionPerformed( ActionEvent e )
                        {
//                            JTextArea info = new JTextArea();
//                            vv.add(info);
//                            //info.setBounds(1050, 20, 300, 300);
//                            info.setText("123");
                        }
                    });
                    popup.add(new AbstractAction("List my neighbors"){
                        public void actionPerformed(ActionEvent e) {
                        	InfoOfNode dlg = (InfoOfNode) listInfo.get(Integer.parseInt(name));
                        	//InfoOfNode dlg = new InfoOfNode();
                            dlg.add(new JTextArea(dlg.listVoisin));
                            dlg.setBounds(200, 200, 300, 100);
                            dlg.setSize(300, 100);
                            dlg.setVisible(true);
                        }
                    });
                    popup.add(new AbstractAction("Subsomption"){
                        public void actionPerformed(ActionEvent e) {
                        	//InfoOfNode dlg = new InfoOfNode();
                        	InfoOfNode dlg = (InfoOfNode) listInfo.get(Integer.parseInt(name));
                            dlg.add(new JTextArea("List of subsomption of Node " + dlg.nbSubsomption));
                            dlg.setSize(300, 100);
                            dlg.setVisible(true);
                        }
                    });
                    popup.add(new AbstractAction("Mapping"){
                        public void actionPerformed(ActionEvent e) {
                        	//InfoOfNode dlg = new InfoOfNode();
                        	InfoOfNode dlg = (InfoOfNode) listInfo.get(Integer.parseInt(name));
                        	dlg.add(new JTextArea("List of mapping" + dlg.nbMapping));
                            dlg.setSize(300, 100);
                            dlg.setVisible(true);
                        }
                    });
                    popup.add(new AbstractAction("Allignement"){
                        public void actionPerformed(ActionEvent e) {
                        	//InfoOfNode dlg = new InfoOfNode();
                        	InfoOfNode dlg = (InfoOfNode) listInfo.get(Integer.parseInt(name));
                        	dlg.add(new JTextArea("List of allignement" + dlg.nbAllignement));
                            dlg.setSize(300, 100);
                            dlg.setVisible(true);
                        }
                    });
                    popup.add(new AbstractAction("See Ontology"){
                        public void actionPerformed(ActionEvent e) {
                        	//InfoOfNode dlg = new InfoOfNode();
                        	InfoOfNode dlg = (InfoOfNode) listInfoOnto.get(Integer.parseInt(name));
                        	
                        	
                        	 BufferedReader lecteurAvecBuffer = null;
                        	    String ligne;
                        	    String content = "";
                        	    System.out.println(dlg.getUriPhysique().substring(5));
                        	    try
                        	      {
                        		lecteurAvecBuffer = new BufferedReader
                        		  (new FileReader(dlg.getUriPhysique().substring(5)));
                        	      
                        	    
                        	    while ((ligne = lecteurAvecBuffer.readLine()) != null)
                        	      content += ligne + "\n";
                        	    
                        	    lecteurAvecBuffer.close();
                        	  
                        }
                	    catch(Exception exc)
                	      {
                		System.out.println("Erreur d'ouverture");
                	      }
                        	
                        dlg.add(new JTextArea(content));
                    	
                            dlg.setSize(400, 500);
                            dlg.setVisible(true);
                        }
                    });
                    popup.show(vv, e.getX(), e.getY());
                } 
                    
                }
            }
}