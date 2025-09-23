package validationTool;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;



/**
 * Serves as a container for various other tools used for XML migration
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */
public class XMLValidationTool {
	

	private static Display display;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		display = Display.getDefault();
		final Shell shell = new Shell (display);
		shell.setSize(900, 7);
		final TabFolder tabFolder = new TabFolder (shell, SWT.BORDER);
		
		tabFolder.setLocation (10, 0);
		
		Migrate migrate = new Migrate(tabFolder, shell);
		Config configTab = new Config(tabFolder, shell);
		BatchMigrate batchTab = new BatchMigrate(tabFolder, shell); 
		XMLDoubleTreeBuilder xdtbuilder = new XMLDoubleTreeBuilder();
		SplashScreen splash = new XMLValidationTool().new SplashScreen(tabFolder);
		Help help = new XMLValidationTool().new Help(tabFolder);
		
		migrate.addObserver(xdtbuilder);
		
		TabItem spalshScreen = new TabItem(tabFolder, SWT.NONE);
		spalshScreen.setText("Welcome");
		spalshScreen.setControl(splash.createContents());
		
		TabItem singleValidation = new TabItem(tabFolder, SWT.NONE);
		singleValidation.setText("Migrate");
		singleValidation.setControl(migrate.createContents());
		
		TabItem configItem = new TabItem (tabFolder, SWT.NONE);
		configItem.setText("Config Editor");
		configItem.setControl(configTab.createContents());
		
		TabItem batchItem = new TabItem (tabFolder, SWT.NONE);
		batchItem.setText("Batch Migrate");
		batchItem.setControl(batchTab.createContents());
		
		TabItem guiComparison = new TabItem(tabFolder, SWT.NONE);
		guiComparison.setText("XML Trees");
		guiComparison.setControl(xdtbuilder.createContents(tabFolder, shell));	
		
		TabItem helpTab = new TabItem(tabFolder, SWT.NONE);
		helpTab.setText("Help");
		helpTab.setControl(help.createContents());
		
		tabFolder.pack ();
		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	
	/**
	 * displays a splash screen for the Migration Tool
	 * 
	 * @author Mike Della Donna (mpdella@us.ibm.com)
	 *
	 */
	private class SplashScreen {
		
		private Composite parent;
		
		public SplashScreen(TabFolder tab)
		{
			parent = new Composite(tab, SWT.NONE);
		}
		
		public Composite createContents()
		{
			Image image = new Image(display, XMLValidationTool.class.getResourceAsStream(
				      "splash.png"));
			Label splash = new Label(parent, SWT.NONE);
			splash.setImage(image);
			splash.setSize(image.getBounds().width, image.getBounds().height);
			parent.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
			/*
			Font font = new Font(Display.getDefault(),"Arial",24,SWT.BOLD);
			Label welcome = new Label(parent, SWT.NONE);
			welcome.setBounds(10, 10, 500, 75);
			welcome.setFont(font);
			welcome.setText("XML Migration Tool");
			
			Font font2 = new Font(Display.getDefault(),"Arial",14,SWT.ITALIC);
			
			Label names = new Label(parent, SWT.NONE);
			names.setBounds(10, 85, 500, 75);
			names.setFont(font2);
			names.setText("Created By:\n  Mike Della Donna\n  Eric Peterson");
			
			Label contact = new Label(parent, SWT.NONE);
			contact.setBounds(10, 180, 500, 200);
			contact.setFont(font2);
			contact.setText("IBM - Lotus - Connections - Automation\nmpdella@us.ibm.com\npetersde@us.ibm.com");
			*/
			return parent;
		}
		
		public Composite createTextContents()
		{

			Font font = new Font(Display.getDefault(),"Arial",24,SWT.BOLD);
			Label welcome = new Label(parent, SWT.NONE);
			welcome.setBounds(10, 10, 500, 75);
			welcome.setFont(font);
			welcome.setText("XML Migration Tool");
			
			Font font2 = new Font(Display.getDefault(),"Arial",14,SWT.ITALIC);
			
			Label names = new Label(parent, SWT.NONE);
			names.setBounds(10, 85, 500, 75);
			names.setFont(font2);
			names.setText("Created By:\n  Mike Della Donna\n  Eric Peterson");
			
			Label contact = new Label(parent, SWT.NONE);
			contact.setBounds(10, 180, 500, 200);
			contact.setFont(font2);
			contact.setText("IBM - Lotus - Connections - Automation\nmpdella@us.ibm.com\npetersde@us.ibm.com");
			
			return parent;
		}
	}
		
	/**
	 * displays a help section in the migration tool
	 * <br>
	 * appears as a smaller tabfolder that mimics the big one, except that the tabs give help information
	 * 
	 * @author Mike Della Donna (mpdella@us.ibm.com)
	 *
	 */
	private class Help {
		
		private Composite parent;
		private TabFolder tab;
		
		public Help(TabFolder tab)
		{
			parent = new Composite(tab, SWT.NONE);
		}
		
		//creates a mock version of this tool, where each tab actually contains
		//a description of its real counterpart
		public Composite createContents()
		{
			tab = new TabFolder (parent, SWT.BORDER);
			tab.setLocation (30, 20);
			
			SplashScreen splash = new XMLValidationTool().new SplashScreen(tab);
			TabItem spalshScreen = new TabItem(tab, SWT.NONE);
			spalshScreen.setText("Welcome");
			spalshScreen.setControl(splash.createTextContents());
			
			TabItem singleValidation = new TabItem(tab, SWT.NONE);
			singleValidation.setText("Migrate");
			singleValidation.setControl(createValidateHelp());
			
			TabItem configItem = new TabItem (tab, SWT.NONE);
			configItem.setText("Config Editor");
			configItem.setControl(createConfigHelp());
			
			TabItem batchItem = new TabItem (tab, SWT.NONE);
			batchItem.setText("Batch Migrate");
			batchItem.setControl(createBatchHelp());
			
			TabItem guiComparison = new TabItem(tab, SWT.NONE);
			guiComparison.setText("XML Trees");
			guiComparison.setControl(createTreeHelp());	
			
			TabItem helpTab = new TabItem(tab, SWT.NONE);
			helpTab.setText("Help");
			Label help = new Label(tab,SWT.NONE);
			help.setLocation(30,30);
			help.setText("You are here");
			helpTab.setControl(help);
			
			tab.pack ();
			return parent;
		}

		private Composite createTreeHelp() {
			final Composite parent = new Composite(tab,SWT.NONE);
			Text help = new Text(parent, SWT.WRAP | SWT.V_SCROLL);
			help.setEditable(false);
			help.setBounds(0,0,900,450);
			parent.setSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			help.setText("The XML Trees tab gives a visual representation of two XML documents.\n" +
					"The XML Trees are automatically populated by the Migrate tab when appropriate reference " +
					"and migration files are selected.  Before the migrate button is pressed, the left tree will represent " +
					"the XML document selected by the Migration XML button, and the right tree will represent the XML document " +
					"selected by the Reference XML button.  After the migrate button is pressed, the left tree will change to reflect the XML document " +
					"created during the transformation process, if a stylesheet is applied.\n\nIt is also possible to manually change the XML documents" +
					" using the Browse buttons on each tree. \n\n"+
					"Navigating the graphical XML Tree: \n" +
					"If a file has been loaded, either by " +
					"the Validate tab or by the Browse button " +
					"in the XML Trees tab, its contents will be expressed " +
					"in a graphical, interactive tree. " +
					"\n\n" +
					"[XML Tree]: \n" +
					"If an XML file has been loaded into the panel, then a tall white box should exist on the left hand side. " +
					"If addition to the box, the name of the XML's root node should be displayed " +
					"at the top of this box as an Tree Item. By hovering over the space to the immediate left of this top " +
					"Tree Item, a small triangle will appear. Like familiar computer directories, clicking on this arrow " +
					"will then display the children of this top node beneath it, each representing a branch of the tree.  " +
					"A branch is a collection of elements of the same name that are first generation children of the  " +
					"root node. So if a tree's root node has the children x,y,y, then in the graphical representation it " + 
					"will have two branches, the [ x branch ] and the  [ y branch ], which have 1 and 2 children " +
					"respectively. If hovering over these children reveals a triangle to their left, that means " +
					"it can be expanded to show the element's element children. In the case that a triangle does not " +
					"appear, then the element has no more element children. " +
					" \n\n" +
					"Learning more about a node in the tree: \n" +
					"By selecting a node in the tree with a mouse click,  " +
					"information about the node in question will appear on the right " +
					"side of the XML Tree panel. " +
					" \n\n" +
					"An important definition to understand is our definition of " +
					"a node's match. A node can only have a match if either the element " +
					"it represents appears only once in the XML, or if it has  " +
					"a unique ID. For an element to have a unique ID, the element must " +
					"have an attribute that similar elements have (i.e. elements of the " +
					"same name), but each of their values for that attribute are unique.  " +
					"By finding this attribute-value 'pair' for an element, the " +
					"comapring XML can search for an element with the same 'pair " +
					"(unique ID), and if it finds one, set it as a match to the original node.  " +
					" \n\n" +
					"[Attributes]: \n" +
					"This box displays all of the attributes of the selected node, " +
					"one attribute expressed per line. " +
					" \n\n" +
					"[Text Elements]: \n" +
					"This box displays the text elements of the selected node. If there " +
					"are new lines and spaces in the XML, they will exist in this box, " +
					"but will obviously not be visible.  " +
					" \n\n" +
					"[Warning]: \n" +
					"This box describes the warning the selected node has been identified for.  " +
					"The possible warnings are as follows: " +
					" \n\n" +
					"None - No differences from its match in the other XML: \n" +
					"If the warning box displays this for a selected node, that means " +
					"this node has been compared to its match in the other XML file, and  " +
					"no differences between them has been found. " +
					"\n \n" +
					"<!> - Differences between this element and its match in the other XML \n" +
					"If the warning box displays this for a selected node, that means this  " +
					"node contains differences from the node it is being compared with in the " +
					"other XML. Really what this is describing, is that one (or more) of this node's  " +
					"descendents is an extra, or a node that does not appear in the same family " +
					"line as the comparison node's. " +
					" \n\n" +
					"(+) - Element does not exist in the other xml: \n" +
					"If the warning box displays this for a selected node, that means this  " +
					"node does not exist in the comparison XML file. If the node with this " +
					"warning is a first generation child of the root node in the XML file, " +
					"then both this node, and the branch that contains it will have this difference too. " +
					"If the parents of this node exist in the comparison XML, then its parent " +
					"will have the <!>[...] warning because there is a difference between the " + 
					"parent in this XML and the comparison XML. " +
					" \n\n" +
					"<@> - Element has differences in data to its match in the other XML: \n" +
					"If the warning box displays this for a selected node, it means that " +
					"this node's match in the other XML has different data. This is determined by comparing " +
					"the arrtibutes of the elements. If one element is being compared to another, and " +
					"its attributes are not identical to the other's attributes, then the node will be " +
					"tagged with the <@> warning and highlighted the appropriate color. Looking at the attributes " +
					"in the 'Attribues' box will show the attributes and their values for the element, which " +
					"is helpful in identifying what attributes are different between nodes. " +
					"\n\n" +
					"(%) - Element is compared against an estimated match, and the estimate may be incorrect: \n" +
					"If the warning box displays this for a selected node, that means this  " +
					"node does not have a unique ID and therefore could not find a precise match  " +
					"in the comparison XML. Without a unique ID, there is no  " +
					"definitive way to know which two nodes are supposed to be compared. " +
					"The node was ,however, assigned a possible match by the program. " +
					"If the XML Tree is showing differences between two trees that are known to be equivalent,  " +
					"nodes with a <!> warning but also tagged with a (%) could mean that the program " +
					"guessed an incorrect match, and compared two nodes that should not have been matched. " +
					"\n" +
					" \n" +
					"Note On Highlighting: \n" +
					"While a node will only have one color, and one warning in the warning box, it CAN " +
					"have multiple tags associated with it. The <!> tag does not descibe a difference with " +
					"the node itself, but rather it describes that there are differences in its children. The <@> " +
					"tag, however, describes actual data differences identified about the node itself. So it is " +
					"very possible for a node to have both differences with its children (the <!> tag) and with " +
					"its own data (the <@> tag). Finally, if a true match could not be identified, the node will " +
					"have the (%) tag to indicate there is a chance that when comparing the element, the program " +
					"incorrectly selected an element to compare with. "  +
					"\n\n" +
					"Buttons of an XMLTree panel: \n" +
					"Apart from the tree items themselves, there are a few buttons at the user's disposal " +
					"for navigating and interpreting XML Trees. " +
					" \n\n" +
					"[Browse]: \n" +
					"The browse button brings up a dialog window for users to select an XML file to " +
					"render in the panel. If an XML file is loaded into the other panel, the  " +
					"two will automatically recompare once the new file is loaded.  " +
					" \n\n" +
					"[Open File]: \n" +
					"This button takes the XML being rendered in the panel and opens it in the default " +
					"application for viewing XMLs.  " +
					" \n\n" +
					"[Highlight Differences In Element Children]: \n" +
					"This toggle controls the highlighting of elements with compared element differences. " +
					"When this box is checked, nodes with a warning corresponding to the <!> warning " +
					"are highlighted red. " +
					" \n\n" +
					"[Highlight Extra Elements]: \n" +
					"This toggle controls the highlighting of extra elements. When this box is checked, nodes  " +
					"with a warning corresponding to the (+) warning are highlighted blue.  " +
					" \n\n" +
					"[Highlight Differences In Element Data]: \n" +
					"This toggle controls the highlighting of identified data differences. When this box is" +
					"checked, nodes with a warning corresponding to the <@> warning are teal (a greenish-blue color)." +
					"\n\n" +
					"[Highlight Elements Possibly Matched Wrong]: \n" +
					"This toggle controls the highlighting of elements whose match was guessed. When this box " +
					"is checked, nodes with a warning corresponding to the (%) warning are highlighted purple.  " +
					" \n\n" +
					"[Element Order Matters]: \n" +
					"This toggle controls the way in which possible element matches are chosen. By default, " +
					"this box is unchecked, meaning that element order does not matter. When the box is unchecked, " + 
					"the matching function of the program tries to find an element's match as accurately as possible " +
					"by looking for a node with children most similar to the element's. When this box is checked however, " + 
					"the order that the elements appear in matters because an element will be paired with the first element " +
					"it finds of the same name. " + 
					"\n\n" +
					"Note on the check boxes: \n" +
					"Every time one of the toggles is checked or unchecked, the XML Tree is reloaded with the " +
					"proper highlighting changes. However, when the tree is refreshed, it is collapsed, so only " +
					"the root node will be visible at first." +
					" \n\n" +
					"A trouble shooting tip: \n" +
					"In the Validate tab, the validation of XML structure is strict, which means the order that " +
					"the elements appear in matters for comparison of elements without unique IDs. " +
					"However, by default, order doesn't matter in the graphical comparison of the XML Trees when guessing " + 
					"matches. What this means, is that if during validation a message about the file structures " +
					"not matching appears, but in the graphical comparison there are no warnings, it may be because " +
					"the [Element Order Matters When Matching] toggle is unchecked. Checking that box essentially turns " +
					"on the strict comparison performed by the validation.");
			return parent;
		}

		private Composite createBatchHelp() {
			Composite parent = new Composite(tab,SWT.NONE);
			Label help = new Label(parent, SWT.WRAP);
			help.setBounds(0,0,900,450);
			help.setText("This tab allows you to execute a preconfigured set of migrations based "+
			"on a configuration file.  This file can be generated in the config tab. To execute the "+
			"the migrations, press the choose button and sleect a configuration file. If the configuration "+
			"file is valid, a list of all the files to be migration will be displayed in the box.  "+
			"Pressing the migrate button starts the migration process.  Each file listed in the display"+
			" will be migrated, and a summary of the results will be displayed.");
			return parent;
		}

		private Composite createConfigHelp() {
			Composite parent = new Composite(tab,SWT.NONE);
			Label help = new Label(parent, SWT.WRAP);
			help.setBounds(0,0,900,450);
			help.setText("The config tab allows you to create pre configured migration sets to be run multiple times.  You can execute these config " +
					"files in the batch tab. Begin by selelcting a location to store this" +
		    		" configuration file.  Next, select the location of the log file." +
		    		" The mode selector allows you to specify between short and verbose modes.  In short mode, only errors wil be reported. In verbose mode, printout will include a record of every node inspected." +
		    		" Next, select the first document to act as a reference.  This should be a" +
		    		" document that represents the result of the transformation" +
		    		" Next, select a transformation style sheet, or check the box. " +
		    		" Then select a schema file, or check the box.  Next, select any number of files to" +
		    		" be transformed, and validated according to the schema, then checked against the reference " +
		    		"document.  Next, you can either click create, or start the cycle over again by adding another " +
		    		"reference document ");
			return parent;
		}

		private Composite createValidateHelp() {
			Composite parent = new Composite(tab,SWT.NONE);//<<<<<<Add V_SCROLL if the content hits the bottom of the window.
			Label help = new Label(parent, SWT.WRAP);
			help.setBounds(0,0,900,450);
			help.setText("The migrate tab performs one full migration process.  The process can be broken down into " +
					"three main steps. \n" +
					"\n" +
					"The first step is to apply the stylesheet specified by the XSL file.  This is the core of the migration process. " +
					"The stylesheet contains instructions to transform the migration XML from its current state into the new, migrated state. This new document is stored " +
					"in a seperate file." +
					"\n\n" +
					"The second step is to validate the newly transformed document against a schema specified by the XSD file.  The schema file contains constraints " +
					"that define the format of the new file.  This validation process verifies that the transformation was successful and that the new document is both valid XML " +
					"and conforms to the rules held by the schema." +
					"\n\n" +
					"The third and final step is to compare the newly created file against a refernce document.  The reference document should be a document that represents " +
					"what the transformed file should look like.  For example, if the migration should be moving an XML file from version 1 to version 2, the " +
					"reference file would be a version 2 file.  Then a comparison takes place between the new transformed file and the reference file.  This comparison " +
					"will point out differences between the two files, such as extra or missing tags, extra or missing attributes, or inconsistent attribute or text values." +
					"\n\n" +
					"Input:\n" +
					"The migrate tab consists of five buttons.  The first four buttons allow you to select files corresponding to their respective labels.  The fifth button, migrate, " +
					"commences the migration process described above, provided all the files are present. " +
					"\n\n"+
					"Output:\n"+
					"The out put is displayed in three areas.  The file location for the transformed version of the file is displayed in the file location bar.  The table displays information "+
					"about the current migration process, red indicatng a failure, green indicating success, "+
					"and yellow indicating a warning.  Failures indicate that the operation "+
					"has failed, warnings indicate that the operation has not taken place, usually due to a "+
					"a necessary file being loaded.  The XML Validation procedure only gives a pass (no out"+
					"put) or a warning (any output).  The final ouput field is the large ouput box. "+
					"The box displays information about differences in the files.  If the transformed file "+
					"matches the refernce file, the box will be empty. Otherwise, it will display the differences "+
					"displayed discovered by the comparison process.  If the schema validation fails, information will be "+
					"displayed in this box."+
					"");
			return parent;
		}
	}
}

