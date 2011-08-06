package frame;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.w3c.dom.Document;


public class TreePanel extends JPanel{
	private JTree tree;
	private ModelXML model;
	
	public TreePanel() {
		setLayout(new BorderLayout());
		
		model = new ModelXML();
		tree = new JTree();
		tree.setModel(model);
		tree.setShowsRootHandles(true);
		tree.setEditable(false);
		
		JScrollPane pane = new JScrollPane(tree);
		pane.setPreferredSize(new Dimension(300,400));

		add(pane, "Center");
		
		final JTextField text = new JTextField();
		text.setEditable(false);
		add(text, "South");
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				Object lpc = e.getPath().getLastPathComponent();
				if (lpc instanceof XMLTreeNode) {
					text.setText( ((XMLTreeNode)lpc).getText() );
				}
			}
		});
		
	}
	
	public void setDocument(Document document) {
		model.setDocument(document);
	}
	public Document getDocument() {
		return model.getDocument();
	}
}
