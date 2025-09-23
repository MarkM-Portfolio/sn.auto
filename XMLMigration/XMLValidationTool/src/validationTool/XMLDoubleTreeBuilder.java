package validationTool;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;

 
/**
 * This class produces a window containing the tools
 * for graphical XMLTree construction and comparison
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-06-07
 */
public class XMLDoubleTreeBuilder implements Observer{
	private String leftxml = "";
	private String rightxml = "";
	private Composite leftComp;
	private Composite rightComp;
	private Composite comp;
	private Text lblLeftFile;
	private Text lblRightFile;
	private Button btnOpenFileLeft;
	private Button btnOpenFileRight;

	/**
	 * constructor
	 */
	public XMLDoubleTreeBuilder() {
	}

	/**
	 * createContents <br><br>
	 * 
	 * This method creates the meat of the XMLDoubleTree builder,
	 * placing the buttons, labels and components in their
	 * correct spots and handling button events and file 
	 * loading. 
	 * 
	 * @param tab - (TabFolder) The tab that the XMLDoubleTreeBuilder will exist in
	 * @param shell - (final Shell) The shell the XMLDoubleTreeBuilder will belong to 
	 * 
	 * @return Composite
	 */
	public Composite createContents(TabFolder tab, final Shell shell)
	{
		comp = new Composite(tab, SWT.NONE);
		Label divider = new Label(comp, SWT.SEPARATOR | SWT.VERTICAL);
		divider.setBounds(563, 7, 2, 545);
		
		//create the composites and build if possible //////////////////
		leftComp = new Composite(comp, SWT.NONE);
		leftComp.setBounds(0, 65, 555, 515);
		if (leftxml.endsWith(".xml") && rightxml.endsWith(".xml")) {
			buildLeftTree(leftxml, rightxml);
		}
		
		rightComp = new Composite(comp, SWT.NONE);
		rightComp.setBounds(572, 65, 555, 515);
		if (rightxml.endsWith(".xml") && (leftxml.endsWith(".xml"))) {
			buildRightTree(rightxml, leftxml);
		}
		////////////////////////////////////////////////////////////////
		
		//Create the left controls//////////////////////////////////////
		Label lblLeftxml = new Label(comp, SWT.NONE);
		lblLeftxml.setBounds(12, 10, 89, 15);
		lblLeftxml.setText("Migrated XML");
		
		lblLeftFile = new Text(comp, SWT.NONE);
		lblLeftFile.setBounds(91, 36, 466, 15);
		lblLeftFile.setText("C://MIGRATED_FILE.XML");
		lblLeftFile.setEditable(false);
		
		//add the browse button for the left (migrated) XML
		Button btnMig = new Button(comp, SWT.NONE);
		btnMig.setBounds(10, 31, 75, 25);
		btnMig.setText("Browse");
		
		//add a listener for the browse button for the leftxml side
		btnMig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				dialog.setFilterExtensions(new String[]{"*.xml"});
				String fpath = dialog.open();
				
				if ((fpath != null) && new File(fpath).isFile()) {
					replaceLeft(fpath);
				}
			}
		});
		
		//add the open button for the left xml
		btnOpenFileLeft = new Button(comp, SWT.NONE);
		btnOpenFileLeft.setBounds(482, 7, 75, 25);
		btnOpenFileLeft.setText("Open File");
		if (!leftxml.endsWith(".xml") || !(new File(leftxml).isFile())) {
			btnOpenFileLeft.setEnabled(false);	
		}
		
		//add a listener for the "Open File" button
		btnOpenFileLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (leftxml.endsWith(".xml")) {
					Program.launch(leftxml);
				}
			}
		});
		////////////////////////////////////////////////////////////////
		
		//Create the right controls/////////////////////////////////////
		Label lblReferenceXml = new Label(comp, SWT.NONE);
		lblReferenceXml.setBounds(571, 13, 89, 15);
		lblReferenceXml.setText("Reference XML");
		
		lblRightFile = new Text(comp, SWT.NONE);
		lblRightFile.setBounds(652, 39, 475, 15);
		lblRightFile.setText("C://EXPECTED_RESULT_REFERENCE.XML");
		lblRightFile.setEditable(false);
		
		//add the browse button for the right (Reference) XML
		Button btnRef = new Button(comp, SWT.NONE);
		btnRef.setBounds(571, 34, 75, 25);
		btnRef.setText("Browse");
		
		//add a listener for the browse button for the rightxml side
		btnRef.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				dialog.setFilterExtensions(new String[]{"*.xml"});  //show only files that end in .xml
				String fpath = dialog.open();
				
				if ((fpath != null) && new File(fpath).isFile()) {
					replaceRight(fpath);
				}
			}
		});
		
		//add the open button for the right xml
		btnOpenFileRight = new Button(comp, SWT.NONE);
		btnOpenFileRight.setBounds(1052, 7, 75, 25);
		btnOpenFileRight.setText("Open File");
		if (!rightxml.endsWith(".xml") || !(new File(rightxml).isFile())) {
			btnOpenFileRight.setEnabled(false);
		}
		
		//add a listener for the "Open File" button
		btnOpenFileRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (rightxml.endsWith(".xml")) {
					Program.launch(rightxml);
				}
			}
		});
		////////////////////////////////////////////////////////////////
		
		return comp;
	}

	
	/**
	 * buildTree <br><br>
	 * 
	 * This builds a tree out of the file that has been selected
	 * 
	 * This version of buildTree is used for initial Tree creation
	 * 
	 * @param comp - (Composite) The composite the tree will be loaded into
	 * @param xmlA - (String) The filepath for the file that will be constructed
	 * @param xmlB - (String) The filepath for the file that xmlA will be compared to
	 * @param side - (String) The side the Tree should be built on
	 * 
	 * @return void
	 */
	public void buildTree(Composite comp, String xmlA, String xmlB, String side) {
		if ((xmlA != null) && //if the filepath xmlA is not null AND
				(xmlB != null) && //if the filepath xmlB is not null AND
					(new File(xmlA).isFile()) && //if xmlA IS a file AND
						(new File(xmlB).isFile()))  { //if xmlB IS a file...
			XMLTree xTree;
			try {
				xTree = new XMLTree(comp, xmlA, side);
				xTree.setCompareDoc(xmlB); //compare it to the other xml
				xTree.createContents(); //load the left tree's contents
				xTree.addObserver(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
	
	/**
	 * buildTree <br><br>
	 * 
	 * This builds a tree out of a file that has been selected
	 * 
	 * This version of buildTree is used for refreshing a Tree 
	 * if some of its toggles have been changed
	 * 
	 * @param comp - (Composite) The composite the tree will be loaded into
	 * @param xmlA - (String) The filepath for the file that will be constructed
	 * @param xmlB - (String) The filepath for the file that xmlA will be compared to
	 * @param side - (String) The side the Tree should be built on
	 * @param diff - (boolean) The value of highlight_diff in the Tree being updated
	 * @param extra - (boolean) The value of highlight_extra in the Tree being updated
	 * @param data - (boolean) The value of the highlight_data in the Tree being updated
	 * @param guess - (boolean) The value of highlight_guess in the Tree being updated
	 * @param strict - (boolean) The value of guess_strict in the Tree being updated
	 * 
	 * @return void
	 */
	public void buildTree(Composite comp, String xmlA, String xmlB, String side, boolean diff, boolean extra, boolean data, boolean guess, boolean strict) {
		if ((xmlA != null) && //if the filepath xmlA is not null AND
				(xmlB != null) && //if the filepath xmlB is not null AND
					(new File(xmlA).isFile()) && //if xmlA IS a file AND
							(new File(xmlB).isFile())) { //if xmlB IS a file...
			XMLTree xTree;
			try {
				xTree = new XMLTree(comp, xmlA, side, diff, extra, data, guess, strict);
				xTree.setCompareDoc(xmlB);
				xTree.createContents();
				xTree.addObserver(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}	
		}
	}
	
	/**
	 * buildLeftTree <br><br>
	 * 
	 * This builds an XML Tree on the left side of the tab
	 * 
	 * @param xmlA - (String) The file being rendered as a tree on the left
	 * @param xmlB - (String) The file xmlA is being compared against
	 * 
	 * @return void
	 */
	public void buildLeftTree(String xmlA, String xmlB) {
		buildTree(leftComp, xmlA, xmlB, "left");
		btnOpenFileLeft.setEnabled(true);
	}
	
	/**
	 * buildRightTree <br><br>
	 * 
	 * This builds an XML Tree on the right side of the tab
	 * 
	 * @param xmlA - (String) The file being rendered as a tree on the right
	 * @param xmlB - (String) The file xmlA is being compared against
	 * 
	 * @return void
	 */
	public void buildRightTree(String xmlA, String xmlB) {
		buildTree(rightComp, xmlA, xmlB, "right");
		btnOpenFileRight.setEnabled(true);
	}
	
	/**
	 * rebuild <br><br>
	 * 
	 * This method rebuilds the given XMLTree 
	 * into the given component based off of its
	 * new toggle values
	 * 
	 * @param xmlt - (XMLTree) The XMLTree being reconstructed because of a toggled value
	 * @param comp - (Composite) The composite (or side) the reconstructed tree is going to be placed in
	 * 
	 * @return void
	 */
	public void rebuild(XMLTree xmlt, Composite comp) {
		XMLTree tree = xmlt;
		boolean[] values = tree.getToggleValues();
		buildTree(comp, tree.getFPath(), tree.getCPath(), tree.getSide(), values[0], values[1], values[2], values[3], values[4]);
	}

	/**
	 * resetComposite <br><br>
	 * 
	 * The clears out a composite, and makes a new one
	 * with the correct dimensions in its place so 
	 * it can be reloaded
	 * 
	 * @param side - (String) The side of the tab the composite is on
	 * 
	 * @return void
	 */
	private void resetComposite(String side) {
		if (side.equalsIgnoreCase("left")) {
			leftComp.dispose(); //get rid of the current left XMLTree composite so it can be reset
			leftComp = new Composite(comp, SWT.NONE); //clear the composite and reset it
			leftComp.setBounds(0, 65, 555, 515);
		}
		else if (side.equalsIgnoreCase("right")) {
			rightComp.dispose(); //get rid of the current right XMLTree composite so it can be reset
			rightComp = new Composite(comp, SWT.NONE);
			rightComp.setBounds(572, 65, 555, 515);
		}
	}
	
	/**
	 * replaceLeft <br><br>
	 * 
	 * Takes in a filepath and replaces the current composite
	 * on the left side with a composite holding the 
	 * representation of the given filepath as an XMLTree
	 * 
	 * @param fpath - (String) The path to the file being represented
	 * 
	 * @return void
	 */
	private void replaceLeft(String fpath)
	{
		if(fpath != null)
		{
			leftxml = fpath;
			lblLeftFile.setText(leftxml);
			
			if(!fpath.endsWith(".xml")) {
				btnOpenFileLeft.setEnabled(false);
			}
		}	
		
		//if you just want to look at the new file (or left side)
		if (leftxml.endsWith(".xml") && !rightxml.endsWith(".xml")) {
			resetComposite("left");
			buildLeftTree(leftxml, leftxml);
		}
		
		//if we have 2 xmls in place, build them!
		else if (leftxml.endsWith(".xml") && rightxml.endsWith(".xml")) {
			resetComposite("left");
			resetComposite("right");
			buildLeftTree(leftxml, rightxml);
			buildRightTree(rightxml, leftxml);
		}
	}
	
	/**
	 * replaceRight <br><br>
	 * 
	 * Takes in a filepath and replaces the current composite
	 * on the right side with a composite holding the 
	 * representation of the given filepath as an XMLTree
	 * 
	 * @param fpath - (String) The path to the file being represented
	 * 
	 * @return void
	 */
	private void replaceRight(String fpath)
	{
		if(fpath != null)
		{
			rightxml = fpath;
			lblRightFile.setText(rightxml);
			
			if(!fpath.endsWith(".xml")) {
				btnOpenFileRight.setEnabled(false);
			}
		}	
		
		//if you just want to look at the ref file (or right side)
		if (rightxml.endsWith(".xml") && !leftxml.endsWith(".xml") ) {
			resetComposite("right");
			buildRightTree(rightxml, rightxml);
		}
		
		//if we have 2 xmls in place, build them!
		else if (rightxml.endsWith(".xml") && leftxml.endsWith(".xml")) {
			resetComposite("left");
			resetComposite("right");
			buildLeftTree(leftxml, rightxml);
			buildRightTree(rightxml, leftxml);
		}
	}
	/**
	 * Launch the viewer
	 * @param args
	 */
	public static void main(String[] args) {
		//Set up the display and shell////////////////
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Side-By-Side XML Tree Comparison");
		shell.setSize(1170, 630);
		//////////////////////////////////////////////
		
		//Add the divider and composites//////////////

		final TabFolder tabFolder = new TabFolder (shell, SWT.BORDER);
		tabFolder.setSize(850, 520);
		
		tabFolder.setLocation (10, 0);
		
		XMLDoubleTreeBuilder xdtbuilder = new XMLDoubleTreeBuilder();
		
		TabItem guiComparison = new TabItem(tabFolder, SWT.NONE);
		guiComparison.setText("XML Trees");
		guiComparison.setControl(xdtbuilder.createContents(tabFolder, shell));		
		tabFolder.pack ();
		////////////////////////////////////////////
		
		//Close out the shell///////////////////////
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
			}
		display.dispose();
		////////////////////////////////////////////
		}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1 instanceof String && ((String) arg1).equalsIgnoreCase("ref"))
		{
			String fpath = ((Migrate) arg0).getRefLabel().getText();
			replaceRight(fpath);
		}
		else if(arg1 instanceof String && ((String) arg1).equalsIgnoreCase("chk"))
		{
			String fpath = ((Migrate) arg0).getChkLabel().getText();
			replaceLeft(fpath);
		}
		else if(arg1 instanceof String && ((String) arg1).equalsIgnoreCase("mig"))
		{
			String fpath = ((Migrate) arg0).getTransformedFile().getText();
			replaceLeft(fpath);
		}
		else if(arg1 instanceof String && ((String) arg1).equalsIgnoreCase("left")) 
		{
			resetComposite("left"); //reset the composite in preparation for the update
			rebuild((XMLTree) arg0, leftComp); //rebuild the tree on the left side
		}
		else if(arg1 instanceof String && ((String) arg1).equalsIgnoreCase("right")) 
		{
			resetComposite("right"); //reset the composite in preparation for the update
			rebuild((XMLTree) arg0, rightComp); //rebuild the tree on the right side
		}
	}
}