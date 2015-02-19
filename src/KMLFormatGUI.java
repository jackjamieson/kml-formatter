import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

//Jack Jamieson 2015
//This class creates and maintains the GUI.
public class KMLFormatGUI implements TreeSelectionListener {

	// private JList list;
	private JTree tree;

	private JFrame frmKmlFormatter;
	private JEditorPane contentPane;

	private Parse parse;

	private Set<String> lithicGroups;
	private Set<String> ages;
	private Set<String> agesWithLithicGroups;

	private JProgressBar progressBar;

	private JFileChooser fc;

	private Boolean hasFile = false;
	private Boolean createdGUI = false;
	private Boolean hasWrittenOnce = false;

	private JSplitPane splitPane;

	private JMenuBar menuBar_1;
	private JMenuItem mntmOpenKml;
	private JMenu mnExportAs;
	private JMenu mnExportAsKmz;
	
	List<Feature> placemarksWriting;

	//private int count = 0;//Used to keep track of file opening.

	private enum Option {
		AGE, LITHIC, AGEWLITHIC
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					KMLFormatGUI window = new KMLFormatGUI();
					window.frmKmlFormatter.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public KMLFormatGUI() {
		initialize();

	}

	// Recreate the panels when a new file is opened
	private void openFile() {

		hasWrittenOnce = false;

		if (createdGUI) {
			frmKmlFormatter.getContentPane().remove(splitPane);//Remove the split pane so we can add a new one, 
															   //otherwise it will double.

		}
		if (!createdGUI) {
			createdGUI = true;
		}

		//Recreate the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				parse.getDocumentName());
		createNodes(top);

		//Redo the settings on the tree.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addTreeSelectionListener(this);

		JScrollPane scrollPane = new JScrollPane(tree);

		contentPane = new JEditorPane();
		contentPane.setEditable(false);
		JScrollPane contentScroll = new JScrollPane(contentPane);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(scrollPane);
		splitPane.setRightComponent(contentScroll);

		frmKmlFormatter.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		splitPane.setDividerLocation(300);

		frmKmlFormatter.getContentPane().add(splitPane);

		frmKmlFormatter.validate();//Apply it.

	}
	
	//When the file is a KMZ, just remove the features of the GUI until user loads KML.
	public void clearGUI(){
		
		if (createdGUI) {
			frmKmlFormatter.getContentPane().remove(splitPane);//Remove the split pane so we can add a new one, 
															   //otherwise it will double.

		}
		mnExportAs.setEnabled(false);
		mnExportAsKmz.setEnabled(false);
		
		frmKmlFormatter.repaint();
		
		frmKmlFormatter.validate();//Apply it.
	
	}

	// Prepare for agnostic use?
	/*
	 * private void createNodesAgnostic(DefaultMutableTreeNode top) {
	 * DefaultMutableTreeNode children = null; List<Feature> features =
	 * parse.getFeatures();
	 * 
	 * for (Feature feat : features) { children = new
	 * DefaultMutableTreeNode(feat.getName());
	 * 
	 * for (Object feat2 : feat.getFeatureSimpleExtension()) {
	 * DefaultMutableTreeNode down = new DefaultMutableTreeNode( ((Feature)
	 * feat2).getName()); children.add(down); } top.add(children); } }
	 */

	// Create the nodes for the USGS KML parser
	private void createNodes(DefaultMutableTreeNode top) {
		try {
			DefaultMutableTreeNode list = null;
			DefaultMutableTreeNode groups = null;
			DefaultMutableTreeNode age = null;
			DefaultMutableTreeNode ageWithLithic = null;

			DefaultMutableTreeNode child = null;

			list = new DefaultMutableTreeNode("Features");
			top.add(list);

			groups = new DefaultMutableTreeNode("Lithic Groups");
			top.add(groups);

			age = new DefaultMutableTreeNode("Age");
			top.add(age);

			ageWithLithic = new DefaultMutableTreeNode(
					"Age with Lithic Subgroups");
			top.add(ageWithLithic);

			List<Feature> placemarks = parse.getFeatures();
			lithicGroups = new TreeSet<String>();
			ages = new TreeSet<String>();

			// Add the already existing features into a folder
			for (Object obj : placemarks) {

				try {
					Placemark p = (Placemark) obj;
					Pmark pmark = new Pmark(p.getName(), p.getDescription(),
							p.getStyleUrl());

					lithicGroups.add(pmark.getLithicGroup());
					ages.add(pmark.getAge());

					child = new DefaultMutableTreeNode(pmark);
					list.add(child);
				} catch (ClassCastException e) {
				}

			}

			// Add the lithic groups to the top level folder
			for (String str : lithicGroups) {

				Dir d = new Dir(str);

				child = new DefaultMutableTreeNode(d);
				groups.add(child);
			}

			// Add lithic group items
			for (Object obj : placemarks) {
				Placemark p = (Placemark) obj;
				String group = Pmark.getLithicGroupGlobal(p.getStyleUrl());

				int index = 0;
				try {
					while (groups.children().hasMoreElements()) {
						if (groups.getChildAt(index).toString().equals(group)) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) groups
									.getChildAt(index);

							node.add(new DefaultMutableTreeNode(p.getName()));
						}
						index++;
					}
				} catch (IndexOutOfBoundsException excp) {
				}

			}

			// Create directiories for the ages
			for (String str : ages) {
				Dir d = new Dir(str);

				child = new DefaultMutableTreeNode(d);
				age.add(child);
				// ageWithLithic.add(child);

			}

			for (String str : ages) {
				Dir d = new Dir(str);

				child = new DefaultMutableTreeNode(d);
				// age.add(child);
				ageWithLithic.add(child);

			}

			// Add the ages to the folder
			for (Object obj : placemarks) {
				Placemark p = (Placemark) obj;
				String ageStr = Pmark.getAgeGlobal(p.getName());

				int index = 0;
				try {
					while (age.children().hasMoreElements()) {
						if (age.getChildAt(index).toString().equals(ageStr)) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) age
									.getChildAt(index);

							node.add(new DefaultMutableTreeNode(p.getName()));
						}
						index++;
					}
				} catch (IndexOutOfBoundsException excp) {
				}

			}

			// Add the ages to the folder w/ lithic groups
			for (Object obj : placemarks) {
				Placemark p = (Placemark) obj;
				String ageStr = Pmark.getAgeGlobal(p.getName());
				DefaultMutableTreeNode node = null;
				DefaultMutableTreeNode subChild = null;
				DefaultMutableTreeNode subSubChild = null;

				int index = 0;
				try {
					while (ageWithLithic.children().hasMoreElements()) {

						List<DefaultMutableTreeNode> seenNodes = new ArrayList<DefaultMutableTreeNode>();

						if (ageWithLithic.getChildAt(index).toString()
								.equals(ageStr)) {
							node = (DefaultMutableTreeNode) ageWithLithic
									.getChildAt(index);

							// node.add(new
							// DefaultMutableTreeNode(p.getName()));

							// Add the lithic groups to the top level folder
							for (String str : lithicGroups) {

								Dir d = new Dir(str);

								subChild = new DefaultMutableTreeNode(d);
								if (Pmark.getLithicGroupGlobal(p.getStyleUrl())
										.equals(str)) {
									seenNodes.add(subChild);

									node.add(subChild);
									Dir d2 = (Dir) subChild.getUserObject();
									// if(d2.toString().equals)
									// node = (DefaultMutableTreeNode)
									// ageWithLithic
									// .getChildAt(index);

									subChild.add(new DefaultMutableTreeNode(p
											.getName()));

								}

							}

						}

						index++;
					}
				}

				/*
				 * while(ageWithLithic.children().hasMoreElements()){ subChild =
				 * (DefaultMutableTreeNode)
				 * ageWithLithic.children().nextElement(); int subIndex = 0;
				 * 
				 * while(subChild.children().hasMoreElements()) { subSubChild =
				 * (DefaultMutableTreeNode) subChild.getChildAt(subIndex);
				 * Object subSubObj = subSubChild.getUserObject();//subSubChild
				 * = (Placemark) subSubChild; //subSubObj = (String) subSubObj;
				 * 
				 * agesWithLithicGroups.add((String)subSubObj);
				 * System.out.println((String)subSubObj); subIndex++;
				 * 
				 * }
				 * 
				 * 
				 * }
				 */
				// }

				catch (IndexOutOfBoundsException excp) {
				}

			}

			// Try and catch errors when it is not a proper USGS file
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frmKmlFormatter,
					"Possible errors opening this file!\nAre you sure it is"
							+ " an unchanged USGS State KML?", "Error", 2);
		}

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't use system look and feel.");
		}

		frmKmlFormatter = new JFrame();
		frmKmlFormatter.setTitle("USGS KML Formatter");
		frmKmlFormatter.setBounds(100, 100, 800, 600);
		frmKmlFormatter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create the nodes.
		if (hasFile) {
			DefaultMutableTreeNode top = new DefaultMutableTreeNode(
					parse.getDocumentName());
			createNodes(top);

			tree = new JTree(top);
			tree.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);

			tree.addTreeSelectionListener(this);

			JScrollPane scrollPane = new JScrollPane(tree);

			contentPane = new JEditorPane();
			contentPane.setEditable(false);
			JScrollPane contentScroll = new JScrollPane(contentPane);

			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setLeftComponent(scrollPane);
			splitPane.setRightComponent(contentScroll);

			frmKmlFormatter.getContentPane().setLayout(
					new GridLayout(0, 1, 0, 0));

			splitPane.setDividerLocation(300);

			frmKmlFormatter.getContentPane().add(splitPane);
		}

		fc = new JFileChooser();

		menuBar_1 = new JMenuBar();
		frmKmlFormatter.setJMenuBar(menuBar_1);

		JMenu mnFile = new JMenu("File");
		menuBar_1.add(mnFile);

		/*
		 * OPEN FILE DIALOG
		 */
		mntmOpenKml = new JMenuItem("Open File");
		mntmOpenKml.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (mntmOpenKml.isEnabled()) {
					int returnValue = fc.showOpenDialog(null);

					if (returnValue == JFileChooser.APPROVE_OPTION) {
						mntmOpenKml.setEnabled(false);
						mnExportAs.setEnabled(false);
						mnExportAsKmz.setEnabled(false);

						progressBar.setVisible(true);
						class MyWorker1 extends SwingWorker<String, Void> {
							protected String doInBackground() {

								File selectedFile = fc.getSelectedFile();

								try {
									InputStream is = new FileInputStream(
											selectedFile);

									if (selectedFile.getName().contains("kmz")) {
										parse = new Parse(is, true);
										parse = null;
										clearGUI();
										

									} else {
										parse = new Parse(is, false);
										openFile();
									}
								} catch (IOException e) {

								}
								hasFile = true;

								return "Done";

							}

							protected void done() {
								mntmOpenKml.setEnabled(true);

								//Parse is null when you try to load a KMZ, so don't let user save anything here
								if(parse != null){
									mnExportAsKmz.setEnabled(true);
									mnExportAs.setEnabled(true);
								}

								progressBar.setVisible(false);

							}
						}

						new MyWorker1().execute();

					} else {

					}
				}
			}
		});
		mnFile.add(mntmOpenKml);

		mnExportAs = new JMenu("Export as KML...");
		mnFile.add(mnExportAs);
		mnExportAs.setEnabled(false);

		// Export everything in a KML
		JMenuItem mntmKmlall = new JMenuItem("All");
		mntmKmlall.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				int returnVal = fc.showSaveDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mntmOpenKml.setEnabled(false);
					mnExportAs.setEnabled(false);
					mnExportAsKmz.setEnabled(false);

					final File file = fc.getSelectedFile();

					reWriteAll(true, file);

				}

			}
		});

		// Export lithic groups in KML
		JMenuItem mntmLithicGroup_1 = new JMenuItem("Lithic Group");
		mnExportAs.add(mntmLithicGroup_1);
		mntmLithicGroup_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				int returnVal = fc.showSaveDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mntmOpenKml.setEnabled(false);
					mnExportAs.setEnabled(false);
					mnExportAsKmz.setEnabled(false);

					final File file = fc.getSelectedFile();

					reWriteOther(true, Option.LITHIC, file);

				}

			}

		});

		// Export age in KML
		JMenuItem mntmAge_1 = new JMenuItem("Age");
		mnExportAs.add(mntmAge_1);
		mntmAge_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				int returnVal = fc.showSaveDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mntmOpenKml.setEnabled(false);
					mnExportAs.setEnabled(false);
					mnExportAsKmz.setEnabled(false);

					final File file = fc.getSelectedFile();

					reWriteOther(true, Option.AGE, file);

				}

			}

		});

		// Export age w/ lithic subgroups as KML
		JMenuItem mntmAgeWithLithic_1 = new JMenuItem(
				"Age with Lithic Subgroup");
		mnExportAs.add(mntmAgeWithLithic_1);
		mntmAgeWithLithic_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				int returnVal = fc.showSaveDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mntmOpenKml.setEnabled(false);
					mnExportAs.setEnabled(false);
					mnExportAsKmz.setEnabled(false);

					final File file = fc.getSelectedFile();

					reWriteOther(true, Option.AGEWLITHIC, file);

				}

			}

		});
		mnExportAs.add(mntmKmlall);

		mnExportAsKmz = new JMenu("Export as KMZ...");
		mnExportAsKmz.setEnabled(false);

		mnFile.add(mnExportAsKmz);

		//Export lithic as KMZ
		JMenuItem mntmLithicGroup = new JMenuItem("Lithic Group");
		mntmLithicGroup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				
				int returnVal = fc.showSaveDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mntmOpenKml.setEnabled(false);
					mnExportAs.setEnabled(false);
					mnExportAsKmz.setEnabled(false);

					final File file = fc.getSelectedFile();

					reWriteOther(false, Option.LITHIC, file);

				}
			}
		});
		mnExportAsKmz.add(mntmLithicGroup);

		//Export age as KMZ
		JMenuItem mntmAge = new JMenuItem("Age");
		mntmAge.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				
				int returnVal = fc.showSaveDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mntmOpenKml.setEnabled(false);
					mnExportAs.setEnabled(false);
					mnExportAsKmz.setEnabled(false);

					final File file = fc.getSelectedFile();

					reWriteOther(false, Option.AGE, file);

				}
			}
		});
		mnExportAsKmz.add(mntmAge);

		//Export age with lithic subgroups as KMZ
		JMenuItem mntmAgeWithLithic = new JMenuItem("Age with Lithic Subgroup");
		mntmAgeWithLithic.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				
				int returnVal = fc.showSaveDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mntmOpenKml.setEnabled(false);
					mnExportAs.setEnabled(false);
					mnExportAsKmz.setEnabled(false);

					final File file = fc.getSelectedFile();

					reWriteOther(false, Option.AGEWLITHIC, file);

				}
			}
		});
		mnExportAsKmz.add(mntmAgeWithLithic);

		//Export all folders as KMZ
		JMenuItem mntmKmz = new JMenuItem("All");
		mnExportAsKmz.add(mntmKmz);
		mntmKmz.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				int returnVal = fc.showSaveDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					final File file = fc.getSelectedFile();

					reWriteAll(false, file);

				}

			}
		});

		// Help menu dropdown
		JMenu mnHelp = new JMenu("Help");
		menuBar_1.add(mnHelp);

		// Link to the USGS state KMLS, will open default browser
		JMenuItem mntmUsgsStateKmls = new JMenuItem("USGS State KMLs");
		mntmUsgsStateKmls.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {

				try {
					java.awt.Desktop
							.getDesktop()
							.browse(URI
									.create("http://mrdata.usgs.gov/geology/state/"));
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		});
		mnHelp.add(mntmUsgsStateKmls);

		// Help content, nothing here yet
		JMenuItem mntmHelpContent = new JMenuItem("Help Content");
		mnHelp.add(mntmHelpContent);

		// Popup about message
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {

				JOptionPane.showMessageDialog(frmKmlFormatter,
						"USGS KML Formatter. Developed by Jack Jamieson 2015.\nhttp://www.jackjamieson.me");

			}
		});
		mnHelp.add(mntmAbout);

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);

		menuBar_1.add(progressBar);
	}

	// Shows details in the right hand pane about what the user clicked on
	public void displayContents(Pmark p) {
		contentPane.setContentType("text/html");
		HTMLDocument doc = (HTMLDocument) contentPane.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit) contentPane.getEditorKit();
		String text = "<h3>Description:</h3>" + p.getDescription() + "\n";
		try {
			editorKit.insertHTML(doc, doc.getLength(), text, 0, 0, null);
		} catch (BadLocationException | IOException e) {
			
			e.printStackTrace();
		}

	}

	// Shows details in the right hand pane about what the user clicked on
	public void displayContents(Dir d) {
		contentPane.setContentType("text/html");
		HTMLDocument doc = (HTMLDocument) contentPane.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit) contentPane.getEditorKit();
		String text = "<h3>Description:</h3>" + d.toString();
		try {
			editorKit.insertHTML(doc, doc.getLength(), text, 0, 0, null);
		} catch (BadLocationException | IOException e) {
			
			e.printStackTrace();
		}

	}

	// When the user clicks on something in the tree fire this event
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();
		if (node.isLeaf()) {
			try {
				Pmark pmark = (Pmark) nodeInfo;
				contentPane.setText("");
				displayContents(pmark);
			} catch (ClassCastException cce) {

				if (nodeInfo instanceof String) {
					Pmark p = findFeatureFromString((String) nodeInfo);
					contentPane.setText("");

					displayContents(p);
				}

				else {
					Dir dir = (Dir) nodeInfo;
					contentPane.setText("");
					displayContents(dir);
				}
			}

		}

	}

	// Useful for finding features from just the name of the placemark
	public Pmark findFeatureFromString(String placemarkName) {
		List<Feature> placemarks;
		
		if(hasWrittenOnce)
			placemarks = parse.secondaryGetFeatures();
		else
			placemarks = parse.getFeatures();

		for (Object obj : placemarks) {
			if (obj instanceof Placemark) {
				Placemark p = (Placemark) obj;
				Pmark pmark = new Pmark(p.getName(), p.getDescription(),
						p.getStyleUrl());
				if (pmark.getName().equals(placemarkName)) {
					return pmark;
				}
			}
		}

		return null;
	}

	// Rewrite 'all' for kml and kmz
	public void reWriteAll(final boolean isKML, final File file) {

		Object[] placm;
		
		if(hasWrittenOnce){
			List<Feature> placemarks = parse.secondaryGetFeatures();
			placm = placemarks.toArray();
		}
		else{
			List<Feature> placemarks = parse.getFeatures();
			placm = placemarks.toArray();
		}
		

		parse.createSepFolders();

		for (String str : ages) {

			Folder folder = null;

			folder = parse.addAgeFolders(str);

			for (Object obj : placm) {
				try {
					Placemark p = (Placemark) obj;
					if (Pmark.getAgeGlobal(p.getName()).equals(str)) {
						parse.addToFolder(folder, p);

					}
				} catch (ClassCastException e2) {
				}
			}

		}

		for (String str : lithicGroups) {

			Folder folder = null;

			folder = parse.addLithicGroupFolders(str);

			for (Object obj : placm) {
				try {
					Placemark p = (Placemark) obj;
					if (Pmark.getLithicGroupGlobal(p.getStyleUrl()).equals(str)) {
						parse.addToFolder(folder, p);

					}
				} catch (ClassCastException e) {
				}
			}

		}

		for (String str : ages) {

			Folder folder = null;

			folder = parse.addAgeLithicFolders(str);
			List<Folder> folders = new ArrayList<Folder>();

			for (Object obj : placm) {
				try {
					Placemark p = (Placemark) obj;
					if (Pmark.getAgeGlobal(p.getName()).equals(str)) {
						Pmark temp = new Pmark(p.getName(), p.getDescription(),
								p.getStyleUrl());

						// If there is already a folder with the same name, put
						// the feature in there.
						boolean foundIt = false;
						for (Folder f : folders) {
							if (f.getName().equals(temp.getLithicGroup())) {
								parse.addToFolderNoDelete(f, p);
								foundIt = true;
							}
						}
						if (folders.size() == 0 || foundIt == false) {
							Folder folder2 = folder.createAndAddFolder();
							folder2.setName(temp.getLithicGroup());
							folders.add(folder2);
							// parse.addToFolder(folder2,
							// folder);
							parse.addToFolderNoDelete(folder2, p);
						}

					}

				} catch (ClassCastException e2) {
				}
			}

		}
		//count = 0;
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
		class MyWorker extends SwingWorker<String, Void> {
			protected String doInBackground() {

				if (isKML == true)
					parse.reWriteKML(file.toString());
				else
					parse.reWriteKMZ(file.toString());

				return "Done";

			}

			protected void done() {
				// parse.deleteSepFolders();
				mnExportAsKmz.setEnabled(true);
				mnExportAs.setEnabled(true);
				mntmOpenKml.setEnabled(true);
				progressBar.setVisible(false);

				hasWrittenOnce = true;
			}
		}

		new MyWorker().execute();
	}

	// Rewrite kml and kmz for the rest of the options
	public void reWriteOther(final boolean isKML, Option option, final File file) {
		
		Object[] placm;
		
		if(hasWrittenOnce){
			List<Feature> placemarks = parse.secondaryGetFeatures();
			placm = placemarks.toArray();
		}
		else{
			List<Feature> placemarks = parse.getFeatures();
			placm = placemarks.toArray();
		}
		// parse.deleteSepFolders();

		parse.createSepFolders();

		if (option == Option.AGE) {

			for (String str : ages) {

				Folder folder = null;

				folder = parse.addAgeFolders(str);

				for (Object obj : placm) {
					try {
						Placemark p = (Placemark) obj;
						if (Pmark.getAgeGlobal(p.getName()).equals(str)) {
							parse.addToFolder(folder, p);

						}
					} catch (ClassCastException e2) {
					}
				}

			}

		}

		if (option == Option.LITHIC) {

			for (String str : lithicGroups) {

				Folder folder = null;

				folder = parse.addLithicGroupFolders(str);

				for (Object obj : placm) {
					try {
						Placemark p = (Placemark) obj;
						if (Pmark.getLithicGroupGlobal(p.getStyleUrl()).equals(
								str)) {
							parse.addToFolder(folder, p);

						}
					} catch (ClassCastException e) {
					}
				}

			}

		}

		if (option == Option.AGEWLITHIC) {

			for (String str : ages) {

				Folder folder = null;

				folder = parse.addAgeLithicFolders(str);
				List<Folder> folders = new ArrayList<Folder>();

				for (Object obj : placm) {
					try {
						Placemark p = (Placemark) obj;
						if (Pmark.getAgeGlobal(p.getName()).equals(str)) {
							Pmark temp = new Pmark(p.getName(),
									p.getDescription(), p.getStyleUrl());

							// If there is already a folder with the same name,
							// put
							// the feature in there.
							boolean foundIt = false;
							for (Folder f : folders) {
								if (f.getName().equals(temp.getLithicGroup())) {
									parse.addToFolder(f, p);
									foundIt = true;
								}
							}
							if (folders.size() == 0 || foundIt == false) {
								Folder folder2 = folder.createAndAddFolder();
								folder2.setName(temp.getLithicGroup());
								folders.add(folder2);
								// parse.addToFolder(folder2,
								// folder);
								parse.addToFolder(folder2, p);
							}

						}

					} catch (ClassCastException e2) {
					}
				}

			}

		}

		//count = 0;
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
		class MyWorker extends SwingWorker<String, Void> {
			protected String doInBackground() {

				if (isKML == true)
					parse.reWriteKML(file.toString());
				else
					parse.reWriteKMZ(file.toString());

				return "Done";

			}

			protected void done() {
				// parse.deleteSepFolders();
				mnExportAs.setEnabled(true);
				mnExportAsKmz.setEnabled(true);

				mntmOpenKml.setEnabled(true);
				progressBar.setVisible(false);
				
				hasWrittenOnce = true;

			}
		}

		new MyWorker().execute();

	}
}
