package intersimul.old;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import fr.cnrs.liris.simtole.InfoGlobal;
import frame.InfoScenario;
import frame.MyLink;
import frame.SimpleGraphView;
import intersimul.model.dao.ExperimentDAO;
import intersimul.model.experiment.Experiment;
import intersimul.model.experiment.ParamLoader;
import intersimul.model.experiment.Scenario;
import intersimul.model.experiment.Statistics;
import intersimul.util.xml.ScenarioHandler;
import intersimul.util.xml.XMLPrinter;
import intersimul.view.DPanel;
import intersimul.view.NetworkGraphView;
import intersimul.view.SFrame;
import intersimul.view.SPanel;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.jfree.ui.RefineryUtilities;
import org.xml.sax.SAXException;

import peersim.Simulator;

/**
 * Fen�tre pr�sentant l'interface graphique permettant de contr�ler les exp�rimentations. Elle permet :
 * <ul>
 * <li>de charger une simulation existante</li>
 * <li>de modifier une simulation existante</li>
 * <li>de lancer une simulation</li>
 * <li>de cr�er une simulation et de la sauvegarder</li>
 * <li>de visualiser et de modifier la topologuie du r�seau</li>
 * <li>etc.</li>
 * </ul>
 */
public class CopyOfSPanel extends SPanel implements ItemListener{
/*
 * En fait, cette classe �tend JPanel (remplac� par SPanel poru ne pas avoir d'erreur de compilation)
 * public class CopyOfSPanel extends javax.swing.JPanel implements ItemListener{
 */
	// Pour l'impl�mentation de Serializable
	private static final long serialVersionUID = 1L;

	// Contr�les Swing pr�sents sur l'interface
	private JTextField nbPeersField;
	//private JLabel jLnbPeers;
	private JTextField nbTCycles;
	private JLabel nbLCycles;
	//private JLabel jLnetparam;
	//private JLabel jLtopo;
	private JComboBox cbTopo;
	private JCheckBox cbExpByCycle;
	private JCheckBox cbDynamics;
	private JButton bSave;
	private JTabbedPane detailPanel;
	private JButton bClose;
	private JButton bGeneration;
	private JButton bSimulate;
	private JButton bInitSimul;
	private JButton bChangeTopology;
	private JTextField nbNeighborsField;
	//private JLabel jLnbVoisins;
	private JTextField ttl;
	private JLabel jLttl;
	private JTextField nbQueriesField;
	private JLabel jLnbQuery;
	private JComboBox jCprotocoles;
	private JComboBox listScenario;
	private JLabel labelScenario;
	private JLabel mode;
	private JCheckBox modeAuto;
	private JCheckBox modeManu;
	private JButton loadScenario;
	private JButton createScenario;
//	private JButton chooseNode;
	private JButton stats;
	private JLabel qryProtolParameter = null;
	
	// Variables m�tier
	private Experiment simul = null;
	private SFrame sf = null;
	private SimpleGraphView sgv = null;
	private Vector<Integer> nodes = null;


	/**
	 * Constructeur du JPanel
	 * @param s : simulation courante associ�e � une simulation
	 * @param sf : fen�tre m�re
	 */
	public CopyOfSPanel(Experiment s , SFrame sf) {
		// configuration de l'interface graphique
		super(s);
		// r�cup�ration des param�tres (simulation et fen�tre m�re)
		this.sf = sf;
		this.simul = s;

		// Initialisation de l'interface Swing
		initGUI();

		// chargement des param�tres de la simulation
		this.loadSimulation( s );
	}

	/**
	 * Load simulation properties in panel fields
	 * @param s : simulation courante
	 */
	private void loadSimulation( Experiment s ){
		/* chargement des param�tres */

		// r�cupp�ration du nom de la simulation
		this.setName(  s.getName() );

		// r�cup�ration du nombnre de pairs dans le r�seau
		this.nbPeersField.setText( "" + s.getPeersNb() );

		// r�cup�ration du nombre de voisin par pair
		this.nbNeighborsField.setText( "" + s.getNeighborsNb() );

		// r�cup�ration du TTL des messages
		this.ttl.setText( "" + s.getTtl() );

		// s�lection de la topologie courante
		this.cbTopo.getModel().setSelectedItem( s.getTopologyStrategy());

		// s�lection du mode de simulation choisie
		this.cbDynamics.setSelected( s.isDynNetwork() );
		this.cbExpByCycle.setSelected(  s.isExperimentsByCycle());

		// dans le cas de dimulation par cycle: 
		// r�cup�ration du nombre de cycle effectu�
		this.nbTCycles.setText("" + s.getCyclesNb());

		// r�cup�ration du protocol courant
		this.jCprotocoles.setSelectedItem(s.getProtocols());

		// r�cup�ration du nombre de requ�tes par noeud
		this.nbQueriesField.setText("" + s.getQueriesNb());
	}

	/**
	 *  g�n�ration de l'interface graphique	principale
	 */
	private void initGUI() {
		try {
			this.setLayout(null);
			this.setPreferredSize(new java.awt.Dimension(810, 596));
			this.setName("exp1");

			// Initialisation du panel contenant les contr�les des param�tres du r�seau
			JPanel pNetParams = initNetParams();
			this.add(pNetParams);
			
			/**
			 * Nombre de requ�tes
			 */
			{
				jLnbQuery = new JLabel();
				this.add(jLnbQuery);
				jLnbQuery.setText("Number of Queries");
				jLnbQuery.setBounds(12, 466, 110, 14);

				nbQueriesField = new JTextField();
				this.add(nbQueriesField);
				nbQueriesField.setBounds(157, 464, 50, 21);
			}
			{
				nbTCycles = new JTextField();
				this.add(nbTCycles);
				nbTCycles.setBounds(157, 188, 50, 24);
			}
			{
				ttl = new JTextField();
				this.add(ttl);
				ttl.setBounds(157, 64, 50, 21);
			}
			{
				listScenario = new JComboBox(ParamLoader.getScenarioFileConfig());
				this.add(listScenario);
				listScenario.setBounds(12, 390, 200, 22);
			}
			{
				labelScenario = new JLabel("Saved Scenarios");
				this.add(labelScenario);
				labelScenario.setBounds(12, 364, 150, 22);
			}
			{
				loadScenario = new JButton();
				loadScenario.setText("Update config file");
				this.add(loadScenario);
				loadScenario.setBounds(12, 427, 150, 22);
				loadScenario.addMouseListener( new java.awt.event.MouseListener() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
						bClicked_loadScenario(e);
					}
					public void mouseEntered(java.awt.event.MouseEvent e) {} public void mouseExited(java.awt.event.MouseEvent e) {} public void mousePressed(java.awt.event.MouseEvent e) {} public void mouseReleased(java.awt.event.MouseEvent e) {} });
			}
			{
				mode = new JLabel("Scenarios");
				this.add(mode);
				mode.setBounds(12, 280, 90, 22);
			}
			{
				modeAuto= new JCheckBox();
				this.add(modeAuto);
				modeAuto.setText("Automatic");

				modeAuto.setBounds(12, 300, 100, 25);
			}
			{
				modeManu= new JCheckBox();
				this.add(modeManu);
				modeManu.setText("Manual");

				modeManu.setBounds(122, 300, 100, 25);
				modeManu.addItemListener(this);
			}
//			{
//				/**
//				 * Il sert � rien, celui-l�...
//				 */
//				chooseNode= new JButton();
//
//				chooseNode.setText("Validate");
//				chooseNode.setBounds(102, 300, 90, 25);
//				chooseNode.addMouseListener( new java.awt.event.MouseListener() {
//					public void mouseClicked(java.awt.event.MouseEvent e) {
//
//						bClicked_chooseNode(e);
//					}
//					public void mouseEntered(java.awt.event.MouseEvent e) {} public void mouseExited(java.awt.event.MouseEvent e) {} public void mousePressed(java.awt.event.MouseEvent e) {} public void mouseReleased(java.awt.event.MouseEvent e) {} });
//			}
			{
				createScenario = new JButton();
				createScenario.setText("Create Scenario");
				this.add(createScenario);
				createScenario.setBounds(12, 330, 150, 22);
				createScenario.setFont(new java.awt.Font("Tahoma",1,12));
				createScenario.addMouseListener( new java.awt.event.MouseListener() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
						bClicked_createScenario(e);
					}
					public void mouseEntered(java.awt.event.MouseEvent e) {} public void mouseExited(java.awt.event.MouseEvent e) {} public void mousePressed(java.awt.event.MouseEvent e) {} public void mouseReleased(java.awt.event.MouseEvent e) {} });
			}

			{
				nbLCycles = new JLabel();
				this.add(nbLCycles);
				nbLCycles.setText("Nb of Cycles");
				nbLCycles.setBounds(14, 192, 149, 14);
			}

			{
				jLttl = new JLabel();
				this.add(jLttl);
				jLttl.setText("TTL");
				jLttl.setBounds(12, 63, 115, 14);
			}

			{
				// Pour la g�n�ration du graphe
				bGeneration = new JButton();
				bGeneration.setText("Generate Network");
				bGeneration.setBounds(346, 544, 382, 21);
				bGeneration.setFont(new java.awt.Font("Tahoma",1,12));
				this.add(bGeneration);
				// Lancement de la g�n�ration du graphe
				bGeneration.addMouseListener( new java.awt.event.MouseListener() {
					public void mouseClicked(java.awt.event.MouseEvent e) {

						bGeneration_MouseClicked(e);
					}
					public void mouseEntered(java.awt.event.MouseEvent e) {} public void mouseExited(java.awt.event.MouseEvent e) {} public void mousePressed(java.awt.event.MouseEvent e) {} public void mouseReleased(java.awt.event.MouseEvent e) {} });
			}
			{
				bInitSimul = new JButton();
				this.add(bInitSimul);
				bInitSimul.setText("Init simulation");

				bInitSimul.setBounds(12, 505, 150, 22);
				bInitSimul.setFont(new java.awt.Font("Tahoma",1,12));
				bInitSimul.addMouseListener( new java.awt.event.MouseListener() {
					public void mouseClicked(java.awt.event.MouseEvent e) {

						try {
							bInitSimul_MouseClicked(e);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					public void mouseEntered(java.awt.event.MouseEvent e) {} public void mouseExited(java.awt.event.MouseEvent e) {} public void mousePressed(java.awt.event.MouseEvent e) {} public void mouseReleased(java.awt.event.MouseEvent e) {} });

			}
			{
				bChangeTopology = new JButton();
				this.add(bChangeTopology);
				bChangeTopology.setText("Update topology");

				bChangeTopology.setBounds(180, 505, 150, 22);
				bChangeTopology.setFont(new java.awt.Font("Tahoma",1,12));
				bChangeTopology.addMouseListener( new java.awt.event.MouseListener() {
					public void mouseClicked(java.awt.event.MouseEvent e) {

						bChangeTopology_MouseClicked(e);
					}
					private void bChangeTopology_MouseClicked(MouseEvent e) {
					}
					public void mouseEntered(java.awt.event.MouseEvent e) {} public void mouseExited(java.awt.event.MouseEvent e) {} public void mousePressed(java.awt.event.MouseEvent e) {} public void mouseReleased(java.awt.event.MouseEvent e) {} });

			}
			{
				bSimulate = new JButton();
				this.add(bSimulate);
				bSimulate.setText("Simulate");

				bSimulate.setBounds(12, 544, 150, 22);
				bSimulate.setFont(new java.awt.Font("Tahoma",1,12));
				bSimulate.addMouseListener( new java.awt.event.MouseListener() {
					public void mouseClicked(java.awt.event.MouseEvent e) {

						try {
							bSimulate_MouseClicked(e);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					public void mouseEntered(java.awt.event.MouseEvent e) {} public void mouseExited(java.awt.event.MouseEvent e) {} public void mousePressed(java.awt.event.MouseEvent e) {} public void mouseReleased(java.awt.event.MouseEvent e) {} });
			}
			{
				stats = new JButton();
				stats.setText("Get results");
				this.add(stats);
				stats.setBounds(180, 544, 150, 22);
				stats.setFont(new java.awt.Font("Tahoma",1,12));
				stats.addMouseListener( new java.awt.event.MouseListener() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
						Statistics stats = new Statistics("Results");
						stats.pack();
						RefineryUtilities.centerFrameOnScreen(stats);
						stats.setVisible(true);

					}
					public void mouseEntered(java.awt.event.MouseEvent e) {} public void mouseExited(java.awt.event.MouseEvent e) {} public void mousePressed(java.awt.event.MouseEvent e) {} public void mouseReleased(java.awt.event.MouseEvent e) {} });
			}
			{
				bClose = new JButton();
				this.add(bClose);
				bClose.setText("Close this config");
				bClose.setFont(new java.awt.Font("Tahoma",1,12));
				bClose.setBounds(596, 505, 132, 21);
				bClose.addMouseListener( new java.awt.event.MouseListener() {
					public void mouseClicked(java.awt.event.MouseEvent e) {

						bClose_Clicked(e);
					}
					public void mouseEntered(java.awt.event.MouseEvent e) {} public void mouseExited(java.awt.event.MouseEvent e) {} public void mousePressed(java.awt.event.MouseEvent e) {} public void mouseReleased(java.awt.event.MouseEvent e) {} });
			}
			{
				detailPanel = new JTabbedPane();
				this.add(detailPanel);
				detailPanel.setBounds(297, 12, 492, 472);
			}

			{
				bSave = new JButton();
				this.add(bSave);
				bSave.setText("Save simulation configuration");
				bSave.setBounds(346, 505, 216, 21);
				bSave.setFont(new java.awt.Font("Tahoma",1,12));
				bSave.addMouseListener( new java.awt.event.MouseListener() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
						bSave_Clicked(e);
					}
					public void mouseEntered(java.awt.event.MouseEvent e) {} public void mouseExited(java.awt.event.MouseEvent e) {} public void mousePressed(java.awt.event.MouseEvent e) {} public void mouseReleased(java.awt.event.MouseEvent e) {} });
			}
			{
				cbExpByCycle = new JCheckBox();
				this.add(cbExpByCycle);
				cbExpByCycle.setText("Experiment by cycle");
				cbExpByCycle.setBounds(11, 166, 172, 18);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialise les contr�les du r�seau
	 */
	private JPanel initNetParams() {
		// JPanel contenant les param�tres du r�seau (nb de noeuds, topologie...)
		// En mode "sans layout" : http://download.oracle.com/javase/tutorial/uiswing/layout/none.html
		JPanel pNetParams = new JPanel();
		pNetParams.setLayout(null);
		pNetParams.setBounds(5, 5, 345, 173);
		pNetParams.setBorder(new LineBorder(Color.BLACK));

		/**
		 * Titre du JPanel
		 */
		{
			JLabel jLnetparam = new JLabel();
			pNetParams.add(jLnetparam);
			jLnetparam.setText("Network parameters");
			jLnetparam.setBounds(12, 11, 172, 14);
			jLnetparam.setFont(new java.awt.Font("Tahoma",1,14));
			jLnetparam.repaint();
		}

		/**
		 * Nombre de pairs
		 */
		{
			JLabel jLnbPeers = new JLabel();
			pNetParams.add(jLnbPeers);
			jLnbPeers.setText("Number of peers");
			jLnbPeers.setBounds(12, 40, 115, 14);
			jLnbPeers.repaint();
		
			nbPeersField = new JTextField();
			pNetParams.add(nbPeersField);
			nbPeersField.setBounds(290, 37, 50, 21);
			nbPeersField.repaint();
		}

		/**
		 * Nombre de voisins
		 */
		{
			JLabel jLnbVoisins = new JLabel();
			pNetParams.add(jLnbVoisins);
			jLnbVoisins.setText("Degree (nb neighbors)");
			jLnbVoisins.setBounds(12, 65, 138, 14);
			jLnbVoisins.repaint();

			nbNeighborsField = new JTextField();
			pNetParams.add(nbNeighborsField);
			nbNeighborsField.setBounds(290, 63, 50, 21);
			nbNeighborsField.repaint();
		}

		/**
		 * Topologie
		 */
		{
			JLabel jLtopo = new JLabel();
			pNetParams.add(jLtopo);
			jLtopo.setText("Topology strategy");
			jLtopo.setBounds(12, 91, 108, 14);
			jLtopo.repaint();

			cbTopo = new JComboBox(this.simul.getTopologyStrategies());
			pNetParams.add(cbTopo);
			// chargement des topologies possibles 
			//ComboBoxModel cbTopoModel = new DefaultComboBoxModel(this.simul.getTopologyStrategies());
			//cbTopo.setModel(cbTopoModel);

			cbTopo.setBounds(240, 89, 100, 21);
			cbTopo.repaint();
		}

		/**
		 * Protocoles
		 */
		{
			qryProtolParameter = new JLabel();
			pNetParams.add(qryProtolParameter);
			qryProtolParameter.setText("Protocols definition");
			qryProtolParameter.setBounds(12,117,130,14);
			qryProtolParameter.repaint();

			jCprotocoles = new JComboBox(ParamLoader.getProtocolFileConfig());
			pNetParams.add(jCprotocoles);
			jCprotocoles.setBounds(140, 115, 200, 21);
			jCprotocoles.repaint();
		}

		/**
		 * Dynamique
		 */
		{
			cbDynamics = new JCheckBox();
			pNetParams.add(cbDynamics);
			cbDynamics.setText("Dynamic network");
			cbDynamics.setBounds(12, 141, 172, 21);
			cbDynamics.repaint();
		}
		return pNetParams;
	}

	/**
	 * Action associ�e au chargement d'un sc�nario stock� au format XML
	 * @param e
	 */
	private void bClicked_loadScenario(MouseEvent e) {

		// chemin vers le fichier de configuration associ� � la simulation
		String path = ParamLoader.basePath+"." + ParamLoader.slash + "example" + ParamLoader.slash + simul.getName();	

		// parser XML pour lire le fichier de configuration du sc�nario
		ScenarioHandler px = new ScenarioHandler();

		System.out.println("load Scenario" + this.simul.getQueriesNb());

		// r�cup�ration du nom du sc�nario
		String nameScenario = (String)this.listScenario.getSelectedItem();

		// r�cup�ration du nom du sc�nario sans l'extension du fichier
		String nscenario = nameScenario.substring(0, nameScenario.length()-4);

		// m�morisation du nom du sc�nario
		this.simul.setScenarioName(new String(nscenario));

		// enregistrement de la simulation courante
		updateSimulation();

		System.out.println("loading scenario " + nscenario);

		// d�finition du chemin de d�finition du sc�natio courant
		String pathScenario = ParamLoader.basePath + ParamLoader.slash + "scenario" + ParamLoader.slash + simul.getScenarioName();

		//System.out.println("load load " + pathScenario);
		try{	

			// modification du document XML associ� � la simulation courante
			FileWriter fw = new FileWriter(path,true);

			String infoScenario = null;

			if (simul.getScenarioName() != null ){ // dans le cas o� le sc�nario a un nom

				// parcour du docuemnt XML  associ� au sc�nario courant
				Scenario sc = px.parseXMLScenario(pathScenario+".xml");
				// �criture du nom du sc�nario
				fw.write("nomScenario " + simul.getScenarioName() + "\n");
				// �criture du mode
				fw.write("mode " + sc.getMode() + "\n");
				// �criture du noeud de d�part
				fw.write("nodeDepart " + sc.getIdSource() + "\n");
				// �criture du noeud cibl�
				fw.write("nodeDestinataire " + sc.getIdCible());

				infoScenario = new String("nomScenario: " + simul.getScenarioName() + "\n"
						+ "Id of Node source " + sc.getIdSource() + "\n"
						+ "Id of Node cible " + sc.getIdCible()
				);
				System.out.println(infoScenario);

			}
			fw.close();

			// affichage des informations li�es au sc�nario dans une petite fen�tre
			InfoScenario dlg = new InfoScenario();

			dlg.add(new JTextArea(infoScenario));
			dlg.setBounds(200, 200, 300, 100);
			dlg.setSize(300, 100);
			dlg.setVisible(true);

		}catch( IOException  ioe){ioe.printStackTrace();} catch (SAXException e1) {
			e1.printStackTrace();
		}
		//		System.err.println("Simulator: loading configuration");
		//		String pathSimul = ParamLoader.pathRelative +ParamLoader.slash+ "example" +ParamLoader.slash+ this.simul.getSimulName();
		//		try {
		//			Configuration.setConfig( new ParsedProperties(pathSimul) );
		//		} catch (IOException e1) {
		//			e1.printStackTrace();
		//		}

	}

	/**
	 * Action associ�e au bouton "Simulate"
	 * @param evt
	 * @throws IOException
	 */
	private void bSimulate_MouseClicked(MouseEvent evt) throws IOException {

		InfoGlobal.nbMapping = InfoGlobal.nbMappingInit;
		InfoGlobal.nbMessage = 0;
		this.startSimulation();
	}


	/**
	 * Action associ�e au bouton "Save simulation configuration"
	 * @param e
	 */
	private void bSave_Clicked(java.awt.event.MouseEvent e){
		updateSimulation();
	}


	private void updateSimulation() {

		//m�morisation des param�tres de la simulation (parametres de configuration Peersim)
		ExperimentDAO.saveExperimentInPeerSimFormat( this.simul ); // param�tres seuls

		//m�morisation des param�tres de la simulation (dans des fichier de configuration diff�rents)
		ExperimentDAO.saveExperimentInInterSimulFormat( this.simul ); // param + protocoles au format PeerSim

	}

	/**
	 * Action associ�e au bouton "Close this config"
	 * @param e
	 */
	private void bClose_Clicked(java.awt.event.MouseEvent e){

		// supprime la configuration courante
		this.sf.removeSimulationPanel( this );

	}


	/**
	 * M�morisation des param�tres de la simulation
	 */
	private void loadSimulationParameters(){

		/// m�morisation des param�tres
		//Le nombre de pair
		this.simul.setPeersNb( Integer.parseInt( this.nbPeersField.getText() ) );

		// Le nombre de voisins
		this.simul.setNeighborsNb(Integer.parseInt( this.nbNeighborsField.getText()));

		// le TTL des messages
		this.simul.setTtl( Integer.parseInt( this.ttl.getText() ));

		// le mode de fonctionnement des simulations (cyclique ou �v�nementiel)
		this.simul.setDynNetwork( this.cbDynamics.isSelected());
		this.simul.setExperimentsByCycle( this.cbExpByCycle.isSelected());

		// le nombre de cycle
		this.simul.setCyclesNb(Integer.parseInt( this.nbTCycles.getText() ));

		// le type de topologie (al�atoire ou clusteris�)
		this.simul.setTopologyStrategy( (String)this.cbTopo.getModel().getSelectedItem());

		// le protocole courant
		this.simul.setProtocols((String) this.jCprotocoles.getSelectedItem());


	}

	/**
	 * Lancement de la simulation
	 * @throws IOException
	 */
	private void startSimulation() throws IOException{
		System.out.println("Interface-SimTole");
		//generate file config for peersim
		String pathSimul = ParamLoader.basePath +ParamLoader.slash+ "example" +ParamLoader.slash+ this.simul.getName();		
		System.out.println("Lancer Simulation : " + pathSimul);
		Simulator.Simulate(pathSimul);
	}

	/**
	 * G�n�ration de l'interface graphique contenant le graphe. Action associ�e au bouton "Generate network"
	 * @param evt: clic bouton
	 */
	private void bGeneration_MouseClicked(MouseEvent evt) {

		// Initialisation des parametres de la simulation
		this.loadSimulationParameters();

		// construction de l'exp�rimentation
		this.experimentBuilder();

		try {
			// cr�ation du canevas
			sgv = new SimpleGraphView(this.simul);

			NetworkGraphView gp = new NetworkGraphView(sgv);

			sgv.loadGPane(gp);

			// affichage du graphe
			this.detailPanel.add(gp);

			//this.detailPanel.add( sgv.vv  );
			this.detailPanel.setTitleAt(0, "Network");

		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fonction non utilis�e. Apparemment, elle affiche un graphe � partir d'un tableau de String pass� en param�tre.
	 * @param infos Infos sur les noeuds du graphe � afficher ?
	 */
	private void initGUI4(String[] infos)  {

		String[] lesData = infos;

		DPanel dp = new DPanel(lesData);
		this.detailPanel.add(dp);

		//this.detailPanel.add( sgv.vv  );
		this.detailPanel.setTitleAt(1, "Data");
	}


	/**
	 * Creation d'un sc�nario
	 * @param evt
	 */
	private void bClicked_createScenario(MouseEvent evt){
		// pour l'externalisation d'info XML
		XMLPrinter xp = null;

		// Sc�nario courant
		Scenario sc= null;

		// Mode choisi (al�atoire ou manuel)
		String mode = null;

		// nom du sc�nario
		String nameScenario = null;

		int idCycle = -1;

		// identifiant du noeud initiateur de la requ�te
		int idSource = -1;

		// identifiant du noeud cibl�
		int idCible = -1;


		// nom du sc�nario courant que l'on est en train de cr�er
		nameScenario = JOptionPane.showInputDialog(null, "Enter the name of scenario");

		// chargement du nom courant du sc�nario dans la simulation courante
		this.simul.setScenarioName(nameScenario);

		if (this.modeAuto.isSelected()){  // cr�ation d'un sc�nario al�atoire
			mode = "auto";
			idCycle = -1;
			idSource = -1;
			idCible = -1;
			sc = new Scenario(mode, idCycle, idSource, idCible);

		}else if (this.modeManu.isSelected()){
			this.modeAuto.setSelected(false);
			mode = "manu";
			idCycle = 10;
			idSource = nodes.elementAt(0);
			idCible = nodes.elementAt(1);			
			sc = new Scenario(mode, idCycle, idSource, idCible);

		}else{
			// affichage d'un message d'information
			System.out.println("Il est n�cessaire de choisir un mode (Automatique ou Manuel)");
		}

		System.out.print(nameScenario + "cycle: " +idCycle+ ", source:" +idSource+ ", cible:"+idCible);



		// externalisation du sc�nario au format XML
		try {
			// cr�ation d'un document
			xp = new XMLPrinter(ParamLoader.getXMLdocument(this.simul,"scenario"));

			// �criture du sc�nario
			xp.write(sc);

			// fermeture du fichier XML
			xp.close();

			// Ajout dans la liste des sc�narios du sc�nario nouvellement cr��
			this.listScenario.addItem(this.simul.getScenarioName() + ".xml");
			// d�selection des chekbox
			this.modeAuto.setSelected(false);
			this.modeManu.setSelected(false);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate and print the experiment configuration according to current parameters
	 *
	 */
	protected void experimentBuilder(){

//		ExperimentBuilder eb = new ExperimentBuilder(this.simul) ;
		XMLPrinter xp = null;

		// g�n�ration de l'experimentation associ�e � la configuration courante
//		eb.build();

		try{
			// cr�ation de la sortie XML
			xp = new XMLPrinter(ParamLoader.getXMLdocument(this.simul,"topology"));

			// �criture dans le document de l'exp�rimentation
			xp.write(simul);

			// fermeture du fichier XML
			xp.close();

		}catch( IOException ioe ){

			ioe.printStackTrace();

		}



	}

	// compteur servant � d�trminer le nombre de noeuds s�lectionn�s pour la construction du sc�nario 
	int test = 0;

	/**
	 * R�cup�ration des clic sur le graphe
	 */
	public void itemStateChanged(ItemEvent e) {

		if(e.getStateChange() == ItemEvent.SELECTED){
			System.out.println("Construction du scenario manuellement :");

			// liste des identifiants des noeuds s�lectionn�s
			nodes = new Vector<Integer>();
			System.out.println("test = " + test);

			if (test < 2){ // s'il n'y a pas encore deux noeuds de s�lectionn�s

				this.sgv.vv.addGraphMouseListener( new GraphMouseListener(){

					// red�finition de l'action de clic
					public void graphClicked(Object arg0, MouseEvent e) {

						final VisualizationViewer<Integer,MyLink> vv1 = (VisualizationViewer<Integer,MyLink>)e.getSource();

						GraphElementAccessor<Integer,MyLink> pickSupport = vv1.getPickSupport();

						Point2D p = e.getPoint();

						Integer v = pickSupport.getVertex(vv1.getGraphLayout(), p.getX(), p.getY());

						// modification de la couleur du noeud


						// Affichage du noeud selectionn�
						if( test == 0)
							System.out.print("Scenario : FROM  N" + v.toString());
						if( test == 1)
							System.out.println(" TO N" + v.toString());

						// m�morisation du noeud
						nodes.add(v);

						// incr�mentation du compteur
						test++;
					}

					@Override
					public void graphPressed(Object arg0, MouseEvent arg1) {
						//				infoNode = new JTextArea();
						//				infoNode.isVisible();
					}

					@Override
					public void graphReleased(Object arg0, MouseEvent arg1) {

					}} );
			}
		}	
	}




	//this.nbDocumentsField.setText("" + s.getNbDocument() );
	//this.nbProfileDocField.setText( "" + s.getNbProfileDoc() );
	//this.nbProfileQryField.setText( "" + s.getNbProfileQuery() );

	//this.nbThemesField.setText("" + s.getNbTheme() );
	//this.cbDocDistrib.getModel().setSelectedItem( s.getDocDistrib());
	//this.cbDistribQuery.getModel().setSelectedItem( s.getQryDistrib());
	//this.cbQryAccDoc.setSelected( s.isQryBasedDoc());
	//chargement des profiles de documents
	//try{
	//	
	//	this.detailPanel.add( this.pp =  new ProPanel( s ) );
	//	
	//}catch(Exception e){
	//	
	//	this.detailPanel.add( this.pp =  new ProPanel( s,  s.getNbProfileDoc(), s.getNbTheme() ));
	//	
	//}

	// chargement des profiles de requ�tes
	//try{
	//	
	//	this.detailPanel.add( this.pqp =  new ProQPanel( s ) );
	//	
	//}catch(Exception e){
	//	this.detailPanel.add( this.pqp =  new ProQPanel( s,  s.getNbProfileQuery(), s.getNbTheme() ));
	//	
	//}

	//	{
	//	infoNode = new JTextArea();
	//	this.add(infoNode);
	//	//System.out.println("12378");
	//	//infoNode.setVisible(false);
	//	infoNode.setText("information of node");
	//	infoNode.setBounds(850, 20, 300, 300);
	//}




	//{
	//	nbThemeField = new JLabel();
	//	this.add(nbThemeField);
	//	nbThemeField.setBounds(14, 298, 131, 15);
	//	nbThemeField.setText("Nb  Themes");
	//}
	//{
	//	nbThemesField = new JTextField();
	//	this.add(nbThemesField);
	//	nbThemesField.setBounds(163, 295, 97, 21);
	//}




	//	private void bDefProQry_MouseClicked(MouseEvent evt) {
	//	
	//		this.delProfiles();
	//		this.addProfiles();
	//		
	//	}


	/**
	 * Add  document profile and query profile
	 *
	 */
	//	protected void addProfiles(){
	//		int nbPD = Integer.parseInt( this.nbProfileDocField.getText() );
	//		int nbPQ = Integer.parseInt( this.nbProfileQryField.getText() );
	//		int nbT = Integer.parseInt( this.nbThemesField.getText() );
	//		
	//		
	//		this.detailPanel.add( this.pp =  new ProPanel(this.simul,  nbPD, nbT ));
	//		this.detailPanel.add( this.pqp =  new ProQPanel(this.simul,  nbPQ, nbT ));
	//		
	//		
	//	}

	/**
	 * delete all profiles (document profiles and query profiles)
	 *
	 */
	//	protected void delProfiles(){
	//		
	//		this.detailPanel.remove( this.pp );
	//		this.detailPanel.remove( this.pqp );
	//		
	//	}

	//	private void bSimulate_MouseClicked1(MouseEvent evt) throws IOException {
	//	
	//	sgv.sgv.getNode(1).setName("pizzahutterla");
	//	sgv.frame.repaint();
	//}

	//	public void infoOfNode(){
	//	System.out.println("abc");
	//	infoNode.setText("change Text");
	//}

	//maj profile doc
	//	try{
	//		
	//		this.pp.loadValues();
	//		
	//	}catch( Exception e ){System.err.println("No document profiles saved !");}
	//	
	//	//maj profile qry
	//	try{
	//	
	//		this.pqp.loadValues();
	//	
	//	}catch( Exception e ){System.err.println("No query profiles saved !");}




	//	private void initGUI2() {
	//		try {
	//			
	//			ComboBoxModel cbTopoModel = new DefaultComboBoxModel(
	//					this.simul.getTopologyStrategies());
	//			cbTopo.setModel(cbTopoModel);
	//			
	//			
	//			ComboBoxModel cbDocDistribModel = new DefaultComboBoxModel(
	//					this.simul.getDistribNbDocByNode());
	//			cbDocDistrib.setModel(cbDocDistribModel);
	//			
	//			ComboBoxModel cbDistribQueryModel = new DefaultComboBoxModel(
	//					this.simul.getDistribNbQueryByNode());
	//			cbDistribQuery.setModel(cbDistribQueryModel);
	//			
	//			
	//			
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//	}


	/**
	 * Cr�ation d'un ProfPanel
	 * @param e
	 */	

	//private void bDefProDoc_Clicked(java.awt.event.MouseEvent e){
	//
	//	
	//	int nbP = Integer.parseInt( this.nbProfileDocField.getText() );
	//	int nbT = Integer.parseInt( this.nbThemesField.getText() );
	//	
	//	
	//	this.detailPanel.add( this.pp = new ProPanel( this.simul, nbP, nbT ));
	//	
	//	
	//}

	private void bInitSimul_MouseClicked(MouseEvent e)  throws IOException {
		this.initSimulation();
	}

	/**
	 * Lancement de la simulation
	 * @throws IOException
	 */
	private void initSimulation() throws IOException{
		System.out.println("Interface-SimTole");
		//generate file config for peersim
		String pathSimul = ParamLoader.basePath +ParamLoader.slash+ "example" +ParamLoader.slash+ this.simul.getName();		
		System.out.println("Initialisation de la Simulation : " + pathSimul);
		Simulator.init(pathSimul);
	}

	private void bClicked_chooseNode(MouseEvent evt){

	}	

}
