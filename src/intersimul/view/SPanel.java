package intersimul.view;
//import com.cloudgarden.layout.AnchorConstraint;
//import com.cloudgarden.layout.AnchorLayout;

import fr.cnrs.liris.simtole.InfoGlobal;
import frame.InfoScenario;
import frame.SimpleGraphView;
import intersimul.model.dao.ExperimentDAO;
import intersimul.model.experiment.Experiment;
import intersimul.model.experiment.ParamLoader;
import intersimul.model.experiment.Scenario;
import intersimul.model.experiment.Simulation;
import intersimul.model.experiment.Statistics;
import intersimul.model.experiment.UnknownTopologyException;
import intersimul.util.xml.ScenarioHandler;
import intersimul.util.xml.XMLPrinter;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jfree.ui.RefineryUtilities;
import org.xml.sax.SAXException;

import peersim.Simulator;

public class SPanel extends javax.swing.JPanel implements ItemListener{
	private static final long serialVersionUID = 1L;
	private JTextField nbPeersField;
	private JLabel jLnbPeers;
	private JTextField nbTCycles;
	private JLabel nbLCycles;
	private JLabel jLnetparam;
	private JLabel jLtopo;
	private JComboBox cbTopo;
	private JCheckBox cbExpByCycle;
	private JCheckBox cbDynamics;
	private JButton bSave;
	private JTabbedPane detailPanel;
//	private JTabbedPane dataPanel;
	private JButton bClose;
	private JButton bGeneration;
	private JButton bSimulate;
	private JButton bInitSimul;
	private JButton bChangeTopology;
	private JTextField nbNeighborsField;
	private JLabel jLnbVoisins;
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
	private JButton chooseNode;
	private JButton stats;
	private ArrayList<Integer> nodes = null;
	private JLabel qryProtolParameter = null;

	private SFrame sf = null;
	private SimpleGraphView sgv = null;

	private Experiment expe = null;
	private Simulation currentSimulation = null;

	/**
	 * Constructor of an experiment view
	 * @param s : The Experiment this view represents
	 */
	public SPanel(Experiment s){
		// configuration de l'interface graphique
		initGUI();

		// récupération des paramêtres (simulation et fenêtre mère)
		this.expe = s;

		// chargement des topologies possibles 
		ComboBoxModel cbTopoModel = new DefaultComboBoxModel(
				this.expe.getTopologyStrategies());
		cbTopo.setModel(cbTopoModel);

		// chargement des paramètre de la simulation
		this.loadSimulation( s );
	}

	public void setSf(SFrame sf) {
		this.sf = sf;
	}	

	/**
	 * Load simulation properties in panel fields
	 * @param s : simulation courante
	 */
	private void loadSimulation( Experiment s ){
		/* chargement des paramètres */

		// récuppération du nom de la simulation
		this.setName(  s.getName() );

		// récupération du nombnre de pairs dans le réseau
		this.nbPeersField.setText( "" + s.getPeersNb() );

		// récupération du nombre de voisin par pair
		this.nbNeighborsField.setText( "" + s.getNeighborsNb() );

		// récupération du TTL des messages
		this.ttl.setText( "" + s.getTtl() );

		// sélection de la topologie courante
		this.cbTopo.getModel().setSelectedItem( s.getTopologyStrategy());

		// sélection du mode de simulation choisie
		this.cbDynamics.setSelected( s.isDynNetwork() );
		this.cbExpByCycle.setSelected(  s.isExperimentsByCycle());

		// dans le cas de dimulation par cycle: 
		// récupération du nombre de cycle effectué
		this.nbTCycles.setText("" + s.getCyclesNb());

		// récupération du protocol courant
		this.jCprotocoles.setSelectedItem(s.getProtocols());

		// récupération du nombre de requêtes par noeud
		this.nbQueriesField.setText("" + s.getQueriesNb());
	}

///////////////////////////// Interface ////////////////////////////////

	/**
	 *  génération de l'interface graphique	principale
	 */
	private void initGUI() {
		try {
			this.setLayout(null);
			this.setPreferredSize(new java.awt.Dimension(810, 596));
			this.setName("exp1");
			{
				jLnbPeers = new JLabel();
				this.add(jLnbPeers);
				jLnbPeers.setText("Number of peers");
				jLnbPeers.setBounds(12, 40, 115, 17);
			}
			{
				nbPeersField = new JTextField();
				this.add(nbPeersField);
				nbPeersField.setBounds(157, 37, 50, 23);
			}
			{
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
				nbNeighborsField = new JTextField();
				this.add(nbNeighborsField);
				nbNeighborsField.setBounds(157, 91, 50, 21);
			}
			{
				qryProtolParameter = new JLabel();
				this.add(qryProtolParameter);
				qryProtolParameter.setText("Protocols definition");
				qryProtolParameter.setBounds(12,218,200,22);
			}
			{				
				jCprotocoles = new JComboBox(ParamLoader.getProtocolFileConfig());
				this.add(jCprotocoles);
				jCprotocoles.setBounds(12, 250, 200, 22);
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
			{
				/**
				 * Il sert à rien, celui-là...
				 */
				chooseNode= new JButton();

				chooseNode.setText("Validate");
				chooseNode.setBounds(102, 300, 90, 25);
				chooseNode.addMouseListener( new java.awt.event.MouseListener() {
					public void mouseClicked(java.awt.event.MouseEvent e) {

						bClicked_chooseNode(e);
					}
					public void mouseEntered(java.awt.event.MouseEvent e) {} public void mouseExited(java.awt.event.MouseEvent e) {} public void mousePressed(java.awt.event.MouseEvent e) {} public void mouseReleased(java.awt.event.MouseEvent e) {} });
			}
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
				jLnbQuery = new JLabel();
				this.add(jLnbQuery);
				jLnbQuery.setText("Number of Queries");
				jLnbQuery.setBounds(12, 466, 110, 14);
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
				jLnbVoisins = new JLabel();
				this.add(jLnbVoisins);
				jLnbVoisins.setText("Degree (nb neighbors)");
				jLnbVoisins.setBounds(12, 94, 138, 14);
			}

			{
				jLnetparam = new JLabel();
				this.add(jLnetparam);
				jLnetparam.setText("Network parameters");
				jLnetparam.setBounds(12, 11, 172, 14);
				jLnetparam.setFont(new java.awt.Font("Tahoma",1,14));
			}
			{
				// Pour la génération du graphe
				bGeneration = new JButton();
				bGeneration.setText("Generate Network");
				bGeneration.setBounds(346, 544, 382, 21);
				bGeneration.setFont(new java.awt.Font("Tahoma",1,12));

				this.add(bGeneration);
				// Lancement de la génération du graphe
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
				cbDynamics = new JCheckBox();
				this.add(cbDynamics);
				cbDynamics.setText("Dynamic network");
				cbDynamics.setBounds(12, 147, 172, 18);
			}
			{
				cbExpByCycle = new JCheckBox();
				this.add(cbExpByCycle);
				cbExpByCycle.setText("Experiment by cycle");
				cbExpByCycle.setBounds(11, 166, 172, 18);
			}
			{
				cbTopo = new JComboBox();
				this.add(cbTopo);
				cbTopo.setBounds(156, 118, 84, 21);
			}
			{
				jLtopo = new JLabel();
				this.add(jLtopo);
				jLtopo.setText("Topology");
				jLtopo.setBounds(12, 121, 108, 14);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//////////////////////// Scenario ////////////////////////

	/**
	 * Action associée au chargement d'un scénario stocké au format XML
	 * @param e
	 */
	private void bClicked_loadScenario(MouseEvent e) {

		// chemin vers le fichier de configuration associé à la simulation
		String path = ParamLoader.basePath+"." + ParamLoader.slash + "example" + ParamLoader.slash + expe.getName();	

		// parser XML pour lire le fichier de configuration du scénario
		ScenarioHandler px = new ScenarioHandler();

		System.out.println("load Scenario" + this.expe.getQueriesNb());

		// récupération du nom du scénario
		String nameScenario = (String)this.listScenario.getSelectedItem();

		// récupération du nom du scénario sans l'extension du fichier
		String nscenario = nameScenario.substring(0, nameScenario.length()-4);

		// mémorisation du nom du scénario
		this.expe.setScenarioName(new String(nscenario));

		// enregistrement de la simulation courante
		updateSimulation();

		System.out.println("loading scenario " + nscenario);

		// définition du chemin de définition du scénatio courant
		String pathScenario = ParamLoader.basePath + ParamLoader.slash + "scenario" + ParamLoader.slash + expe.getScenarioName();

		//System.out.println("load load " + pathScenario);
		try{	

			// modification du document XML associé à la simulation courante
			FileWriter fw = new FileWriter(path,true);

			String infoScenario = null;

			if (expe.getScenarioName() != null ){ // dans le cas où le scénario a un nom

				// parcour du docuemnt XML  associé au scénario courant
				Scenario sc = px.parseXMLScenario(pathScenario+".xml");
				// écriture du nom du scénario
				fw.write("nomScenario " + expe.getScenarioName() + "\n");
				// écriture du mode
				fw.write("mode " + sc.getMode() + "\n");
				// écriture du noeud de départ
				fw.write("nodeDepart " + sc.getIdSource() + "\n");
				// écriture du noeud ciblé
				fw.write("nodeDestinataire " + sc.getIdCible());

				infoScenario = new String("nomScenario: " + expe.getScenarioName() + "\n"
						+ "Id of Node source " + sc.getIdSource() + "\n"
						+ "Id of Node cible " + sc.getIdCible()
				);
				System.out.println(infoScenario);

			}
			fw.close();

			// affichage des informations liées au scénario dans une petite fenêtre
			InfoScenario dlg = new InfoScenario();

			dlg.add(new JTextArea(infoScenario));
			dlg.setBounds(200, 200, 300, 100);
			dlg.setSize(300, 100);
			dlg.setVisible(true);

		}catch( IOException  ioe){ioe.printStackTrace();} catch (SAXException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Creation d'un scénario
	 * @param evt
	 */
	private void bClicked_createScenario(MouseEvent evt){
		// Scénario courant
		Scenario sc= null;

		// Mode choisi (aléatoire ou manuel)
		String mode = null;

		// nom du scénario
		String nameScenario = null;

		int idCycle = -1;

		// identifiant du noeud initiateur de la requête
		int idSource = -1;

		// identifiant du noeud ciblé
		int idCible = -1;

		// nom du scénario courant que l'on est en train de créer
		nameScenario = JOptionPane.showInputDialog(null, "Enter the name of scenario");

		// chargement du nom courant du scénario dans la simulation courante
		this.expe.setScenarioName(nameScenario);

		if (this.modeAuto.isSelected()){  // création d'un scénario aléatoire
			mode = "auto";
			idCycle = -1;
			idSource = -1;
			idCible = -1;
			sc = new Scenario(mode, idCycle, idSource, idCible);

		}else if (this.modeManu.isSelected()){
			this.modeAuto.setSelected(false);
			mode = "manu";
			idCycle = 10;
			idSource = nodes.get(0);
			idCible = nodes.get(1);			
			sc = new Scenario(mode, idCycle, idSource, idCible);

		}else{
			// affichage d'un message d'information
			System.out.println("Il est nécessaire de choisir un mode (Automatique ou Manuel)");
		}
		System.out.print(nameScenario + "cycle: " +idCycle+ ", source:" +idSource+ ", cible:"+idCible);

		// écriture du scénario au format XML
		XMLPrinter.printXML(ParamLoader.getXMLdocument(this.expe,"scenario"),sc); // 

		// Ajout dans la liste des scénarios du scénario nouvellement créé
		this.listScenario.addItem(this.expe.getScenarioName() + ".xml");
		// déselection des chekbox
		this.modeAuto.setSelected(false);
		this.modeManu.setSelected(false);
	}

//////////////////////////// Simulation //////////////////////////////////

	/**
	 * Generate and print the experiment configuration according to current parameters
	 * @param evt: clic bouton
	 */
	private void bGeneration_MouseClicked(MouseEvent evt) {

		// construction de l'expérimentation
		System.out.println("\n*** Building experiment.");

		try {
			this.currentSimulation = expe.getSimulation(expe.createSimulation());
			this.currentSimulation.loadTopology();
		} catch (UnknownTopologyException e) {
			e.printStackTrace();
		}

		// Ecriture de la topologie dans un fichier XML
		XMLPrinter.printXML(ParamLoader.getXMLdocument(this.expe,"topology"),this.expe);

		// affichage du graphe
		printNetworkPanel();
	}

	/**
	 * Recreate the topology and update the XML file
	 * @param evt: clic bouton
	 */
	private void bChangeTopology_MouseClicked(MouseEvent evt) {
		try {
			this.currentSimulation = expe.getSimulation(expe.createSimulation());
			this.currentSimulation.updateTopology();
		} catch (UnknownTopologyException e) {
			e.printStackTrace();
		} // regénère uniquement la topologie

		// Ecriture de la nouvelle topologie dans un fichier XML
		XMLPrinter.printXML(ParamLoader.getXMLdocument(this.expe,"topology"),this.expe);

		//this.detailPanel.removeAll();
		// affichage du graphe
		printNetworkPanel();
	}

///////////////////////////////// Experiment //////////////////////////////////////////////

	/**
	 * Action associée au bouton "Save simulation configuration"
	 * @param e
	 */
	private void bSave_Clicked(java.awt.event.MouseEvent e){
		updateSimulation();
	}

	private void updateSimulation() {
		//sauvegarde des parametres de la simulation (dans un objet java)
		this.saveSimulation();

		//mémorisation des paramètres de la simulation (parametres de configuration Peersim)
		ExperimentDAO.saveExperimentInPeerSimFormat( this.expe ); // paramètres seuls

		//mémorisation des paramètres de la simulation (dans des fichier de configuration différents)
		ExperimentDAO.saveExperimentInInterSimulFormat( this.expe ); // param + protocoles au format PeerSim
	}

	/**
	 * Action associée au bouton "Close this config"
	 * @param e
	 */
	private void bClose_Clicked(java.awt.event.MouseEvent e){

		// supprime la configuration courante
		this.sf.removeSimulationPanel( this );
	}

	/**
	 * Mémorisation de la simulation
	 */
	private void saveSimulation(){

		/// mémorisation des paramêtres
		//Le nombre de pair
		this.expe.setPeersNb( Integer.parseInt( this.nbPeersField.getText() ) );

		// Le nombre de voisins
		this.expe.setNeighborsNb(Integer.parseInt( this.nbNeighborsField.getText()));

		// le TTL des messages
		this.expe.setTtl( Integer.parseInt( this.ttl.getText() ));

		// le mode de fonctionnement des simulations (cyclique ou événementiel)
		this.expe.setDynNetwork( this.cbDynamics.isSelected());
		this.expe.setExperimentsByCycle( this.cbExpByCycle.isSelected());

		// le nombre de cycle
		this.expe.setCyclesNb(Integer.parseInt( this.nbTCycles.getText() ));

		// le type de topologie (aléatoire ou clusterisé)
		this.expe.setTopologyStrategy( (String)this.cbTopo.getModel().getSelectedItem());

		// le protocole courant
		this.expe.setProtocols((String) this.jCprotocoles.getSelectedItem());
	}

	/**
	 * Initializes a simulation without launching it
	 * @param e Mouse event (unused)
	 * @throws IOException
	 */
	private void bInitSimul_MouseClicked(MouseEvent e)  throws IOException {
		System.out.println("Interface-SimTole");
		//generate file config for peersim
		String pathSimul = ParamLoader.basePath +ParamLoader.slash+ "example" +ParamLoader.slash+ this.expe.getName();		
		System.out.println("Initialisation de la Simulation : " + pathSimul);
		Simulator.init(pathSimul);
	}

	/**
	 * Launches the current simulation
	 * @param evt Mouse event (unused)
	 * @throws IOException
	 */
	private void bSimulate_MouseClicked(MouseEvent evt) throws IOException {
		System.out.println("Lancement de la simulation " + this.currentSimulation.getName());

		InfoGlobal.nbMapping = InfoGlobal.nbMappingInit;
		InfoGlobal.nbMessage = 0;

		//generate config file for peersim
		String pathSimul = ParamLoader.basePath +ParamLoader.slash+ "example" +ParamLoader.slash+ this.expe.getName();		
		Simulator.Simulate(pathSimul);
	}

////////////////////////////////// AFFICHAGE DU RESEAU /////////////////////////////

	/**
	 * Print the network in ad hoc panel.
	 */
	private void printNetworkPanel() {
		// affichage du graphe
		try {
			NetworkGraphView gp = this.initNetworkPanel();
			this.detailPanel.add(gp);
			int tabNb = this.detailPanel.getComponentCount();
			this.detailPanel.setTitleAt(tabNb-1, this.currentSimulation.getName());
			//this.detailPanel.getComponent(0).setName(this.currentSimulation.getName());
			this.detailPanel.getComponent(tabNb - 1).requestFocusInWindow();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Génération de l'interface graphique contenant le graphe
	 * @throws SAXException
	 */
	private NetworkGraphView initNetworkPanel() throws SAXException {
		// création du canevas
		sgv = new SimpleGraphView(this.expe);
		NetworkGraphView gp = new NetworkGraphView(sgv);
		sgv.loadGPane( gp );
		return gp;
	}

	/**
	 * Récupération des clics sur le graphe
	 */
	public void itemStateChanged(ItemEvent e) {

		if(e.getStateChange() == ItemEvent.SELECTED){
			System.out.println("Construction du scenario manuellement :");

			// compteur du nombre de noeuds sélectionnés (pour la construction du scénario) 
			int clicNb = 0;
			// liste des identifiants des noeuds sélectionnés
			nodes = new ArrayList<Integer>();
			System.out.println("Nb de clics = " + clicNb);

			if (clicNb < 2){ // s'il n'y a pas encore deux noeuds de sélectionnés
				//Traitement des clicks par un listener spécifique au graphe
				this.sgv.vv.addGraphMouseListener(new NetworkGraphMouseListener(clicNb, nodes));
				// incrémentation du compteur
				clicNb++;
			}
		}	
	}

	private void bClicked_chooseNode(MouseEvent evt){

	}
}
