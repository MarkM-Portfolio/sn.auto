package validationTool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.eclipse.swt.widgets.Label;

import resources.Configuration;
import resources.XMLHelper;
import resources.XMLUtilities;
import resources.XMLValidation;
 
/**
 * This class creates a panel inside XMLDoubleTreeBuilder
 * that renders a given XML file as a navigable tree
 * and displays highlighting, warnings, and information about it. <br><br>
 * 
 * In the case on XML file is being compared to another, 
 * another XML file (the comparison XML) becomes a major 
 * part of constructing the rendering of the XML being constructed.
 * Anytime in the class below references are made to the "comparison XML"
 * or the "comparison node" or "comparison doc" or "comparison children",
 * it is referencing elements that belong to the comparison XML, not
 * the XML being rendered on this side. <br><br>
 * 
 * The Structure of an XML Tree: <br>
 * This class is written to produce a graphical representation of an XML file
 * as a "Grouped-Branch Tree". A "Grouped-Branch Tree" is not an official 
 * term, but will be used to refer to the particular organization of this tree.
 * So how is it organized?<br><br>
 * 
 * [root]: <br>
 * At the very top of the graphical tree sits the root node.
 * This represents the single parent element to all other element nodes 
 * in the XML file. As of this writing, many of the files that will be part
 * of the migration process have a root node whose name is "config"
 * (just as an example). <br><br>
 * 
 * [branches]: <br>
 * This is the most important part to understand about the representation of the tree.
 * When the root node is expanded in the tree, one or more "branches" will appear 
 * as children of the root. They are identified by nodes titled "[ <i>somename</i> branch ]"
 * Such a branch is not a representation of a real node in the XML being rendered. 
 * Instead, that branch is the parent to all first generation elements in the XML of name
 * <i>somename</i>. For an element to be first generation, its immediate parent 
 * must be the root node (as opposed to its grandparent, or great grandparent, as all
 * elements are some form of descendant of the root node). So in the graphical XMLTree,
 * first generation are separated from their parent (the root node) by the branch
 * that contains them, as opposed to directly under their parent like they are in the XML. 
 * This decision was made to organize the elements of the tree to help streamline
 * the process of locating specific elements. To reiterate, its important to understand
 * that <b>only first generation</b> elements of <i>somename</i> will appear 
 * as immediate children of the [ <i>somename</i> branch ] node. Therefore, elements
 * of somename that are 2 or more generations deep will appear in the XML Tree correctly
 * oriented in relation to their parent, even if their parent belongs to a branch
 * different from the [ <i>somename</i> branch ]. <br><br>
 * 
 * [node/element/treeitem]:
 * Children of branches are nodes (or elements or treeitems) that represent a node in the
 * XML being rendered. These are intuitively organized, which is to say that
 * nodes that are children of other nodes in the XML, will (correctly) be children of 
 * their parent nodes in the graphical representation. <br><br>
 * 
 * Hovering over any nodes in the graphical tree (whether root, branch, or element node) 
 * will reveal an arrow to the left of the node if the node has children beneath it.
 * Clicking on that node expands it and reveals the children. 
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-06-06
 */
public class XMLTree extends Observable {
	
	/**
	 * constants
	 */
	private static org.eclipse.swt.graphics.Color XBLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private static org.eclipse.swt.graphics.Color XWHITE = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	private static org.eclipse.swt.graphics.Color XRED = Display.getDefault().getSystemColor(SWT.COLOR_RED);
	private static org.eclipse.swt.graphics.Color XBLUE = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
	private static org.eclipse.swt.graphics.Color XMAGENTA = Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA);
	private static org.eclipse.swt.graphics.Color XCYAN = Display.getDefault().getSystemColor(SWT.COLOR_DARK_CYAN);
	private static org.eclipse.swt.graphics.Color XORANGE = new Color(Display.getCurrent(), 235,153,50);
	private static org.eclipse.swt.graphics.Color XPURPLE = new Color(Display.getCurrent(), 160, 32, 180);
	private static org.eclipse.swt.graphics.Color XPINK = Display.getDefault().getSystemColor(SWT.COLOR_MAGENTA); //possible replacement for guess if XMAGENTA is too dark
	
	public static org.eclipse.swt.graphics.Color XDIFF_CHILDREN = XRED; //color of an element whose children have differences
	public static org.eclipse.swt.graphics.Color XEXTRA_ELEMENT = XBLUE; //color of an element that doesn't appear in the comparison doc
	public static org.eclipse.swt.graphics.Color XDATA_WARNING = XCYAN; //color of an element that has a data mismatch with its match
	public static org.eclipse.swt.graphics.Color XGUESS_WARNING = XPURPLE; //color of an element that may or may not have found the correct match
	
	public static String warning_none = "None - No differences from its match in the other XML";
	public static String warning_diff = "<!> - Differences between this element and its match in the other XML";
	public static String warning_extra = "(+) - Element does not exist in the other xml";
	public static String warning_data = "<@> - Element has differences in data to its match in the other XML";
	public static String warning_guess = "(%) - Element is compared against an estimated match, and the estimate may be incorrect";
	
	/**
	 * variables
	 */
	private Composite composite; //the composite the XMLTree is going to sit in
	private String fpath;//the file path to the file being rendered
	private String cpath;//the file path to the comparison file
	private String side;//the side the XMLTree is going to be on in XMLDoubleTreeBuilder
	private Document xdoc;//the document of fpath
	private Document cdoc; //the doc xdoc will be compared to, the document of cpath
	private Map<String, String> xrefmap;//the reference map of special attributes for this file
	private Map<String, String> crefmap;//the reference map for the comparison file
	Display display = Display.getDefault();
	final Shell shell = new Shell (display);
	
	private boolean highlight_diff;//a toggle to highlight nodes that have differences from the comparison XML
	private boolean highlight_extra;//a toggle to highlight nodes that do not appear in the comparison XML
	private boolean highlight_data; //a toggle to highlight nodes with data mistmatches with their matched node
	private boolean highlight_guess;//a toggle to highlight nodes whose match was guessed because there was no way to precisely identify one
	private boolean guess_strict; //a toggle for which guessing method should be used when guessing matches, when true it means order matters
	
	//////////////////////////////////////
	/*
	 * ALERT!:
	 * the following variables are in place
	 * strictly for testing purposes in XMLTreeTest.
	 * They should not be used for building the tree,
	 * and should only be set in createContents.
	 */
	private TreeItem testRootNode;
	private ArrayList<TreeItem> testBranchNodes;
	//////////////////////////////////////
	
	/**
	 * constructor <br><br>
	 * 
	 * @param c - (Composite) The composite the XMLTree is going to belong to
	 * @param f - (String) The filepath to the file that is going to be rendered as a Tree
	 * @param s - (String) The side (of the XMLDoubleTreeBuilder tab) this Tree will appear on
	 * 
	 * @throws Exception 
	 */
	public XMLTree(Composite c, String f, String s) throws Exception {
		composite = c;
		this.fpath = f;
		this.cpath = f;
		this.side = s;
		try {
			this.xdoc = XMLUtilities.getXMLDocNoCatch(fpath);
			this.cdoc = XMLUtilities.getXMLDocNoCatch(fpath);
		} catch (SAXParseException e) {
			createWarning(fpath);
		} catch (SAXException e) {
			createWarning(fpath);
		} catch (Exception e) {
			createWarning(fpath);
		}
		Configuration xconfig = new Configuration(fpath,fpath);
		this.xrefmap = xconfig.getRefMap();
		Configuration cconfig = new Configuration(fpath,fpath);
		this.crefmap = cconfig.getRefMap();
		
		highlight_diff = true;
		highlight_extra = true;
		highlight_data = true;
		highlight_guess = true;
		guess_strict = false;
		
		shell.setSize(900, 7);
	}
	
	/**
	 * constructor <br><br>
	 * 
	 * @param c - (Composite) The composite the XMLTree is going to belong to
	 * @param f - (String) The filepath to the file that is going to be rendered as a Tree
	 * @param s - (String) The side (of the XMLDoubleTreeBuilder tab) this Tree will appear on
	 * @param diff - (boolean) The toggle value that the new tree will set for highlight_diff
	 * @param extra - (boolean) The toggle value that the new tree will set for highlight_extra
	 * @param data - (boolean) The toggle value that the new tree will set for highlight_data
	 * @param guess - (boolean) The toggle value that the new tree will set for highlight_guess
	 * @param strict - (boolean) The toggle value that the new tree will set for guess_strict
	 * 
	 * @throws Exception 
	 */
	public XMLTree(Composite c, String f, String s, boolean diff, boolean extra, boolean data, boolean guess, boolean strict) throws Exception {
		composite = c;
		this.fpath = f;
		this.cpath = f;
		this.side = s;
		try {
			this.xdoc = XMLUtilities.getXMLDocNoCatch(fpath);
			this.cdoc = XMLUtilities.getXMLDocNoCatch(fpath);
		} catch (SAXParseException e) {
			createWarning(fpath);
		} catch (SAXException e) {
			createWarning(fpath);
		} catch (Exception e) {
			createWarning(fpath);
		}
		Configuration xconfig = new Configuration(fpath,fpath);
		this.xrefmap = xconfig.getRefMap();
		Configuration cconfig = new Configuration(fpath,fpath);
		this.crefmap = cconfig.getRefMap();
		
		highlight_diff = diff;
		highlight_extra = extra;
		highlight_data = data;
		highlight_guess = guess;
		guess_strict = strict;
		
		shell.setSize(900, 7);
	}

	/**
	 * getHighlightDiff <br><br>
	 * 
	 * Get the value of the variable highlight_diff
	 * 
	 * @return boolean
	 */
	public boolean getHighlightDiff() {
		return highlight_diff;
	}
	
	/**
	 * setHighlightDiff <br><br>
	 * 
	 * Set the value of the variable highlight_diff
	 * 
	 * @param bool
	 * 
	 * @return void
	 */
	public void setHighlightDiff(boolean bool) {
		highlight_diff = bool;
	}
	
	/**
	 * getHighlightExtra <br><br>
	 * 
	 * Get the value of the variable highlight_extra
	 * 
	 * @return boolean
	 */
	public boolean getHighlightExtra() {
		return highlight_extra;
	}
	
	/**
	 * setHighlightExtra <br><br>
	 * 
	 * Set the value of the variable highlight_extra
	 * 
	 * @param bool
	 * 
	 * @return void
	 */
	public void setHighlightExtra(boolean bool) {
		highlight_extra = bool;
	}
	
	/**
	 * getHighlightGuess <br><br>
	 * 
	 * Get the value of the variable highlight_guess
	 * 
	 * @return boolean
	 */
	public boolean getHighlightGuess() {
		return highlight_guess;
	}
	
	/**
	 * setHighlightGuess <br><br>
	 * 
	 * Set the value of the variable highlight_guess
	 * 
	 * @param bool
	 * 
	 * @return void
	 */
	public void setHighlightGuess(boolean bool) {
		highlight_guess = bool;
	}
	
	
	/**
	 * getGuessStrict <br><br>
	 * 
	 * Get the value of the variable guess_strict
	 * 
	 * @return boolean
	 */
	public boolean getGuessStrict() {
		return guess_strict;
	}
	
	/**
	 * setGuessStrict  <br><br>
	 * 
	 * Set the value of the variable guess_strict
	 * 
	 * @param bool
	 * 
	 * @return void
	 */
	public void setGuessStrict(boolean bool) {
		guess_strict = bool;
	}
	
	/**
	 * getFPath <br><br>
	 * 
	 * Get the value of the variable fpath
	 * 
	 * @return string
	 */
	public String getFPath() {
		return fpath;
	}
	
	/**
	 * getCPath <br><br>
	 * 
	 * Get the value of the variable cpath
	 * 
	 * @return string
	 */
	public String getCPath() {
		return cpath;
	}
	
	/**
	 * getXDoc <br><br>
	 * 
	 * Return the xdoc
	 * 
	 * @return Document
	 */
	public Document getXDoc() {
		return xdoc;
	}

	/**
	 * getCDoc <br><br>
	 * 
	 * Return the cdoc
	 * 
	 * @return Document
	 */
	public Document getCDoc() {
		return cdoc;
	}
	
	/**
	 * getSide <br><br>
	 * 
	 * Get the value of the variable side
	 * 
	 * @return string
	 */
	public String getSide() {
		return side;
	}
	
	/**
	 * getToggleValues <br><br>
	 * 
	 * Get an array of the three highlighting variables
	 * and the guess_strict variable
	 * 
	 * @return boolean[]
	 */
	public boolean[] getToggleValues() {
		boolean[] booleanArray;
		booleanArray = new boolean[5];
		booleanArray[0] = highlight_diff;
		booleanArray[1] = highlight_extra;
		booleanArray[2] = highlight_data;
		booleanArray[3] = highlight_guess;
		booleanArray[4] = guess_strict;
		
		return booleanArray;
	}
	
	/**
	 * getTestRootNode <br><br>
	 * 
	 * This returns the root node of the
	 * tree, and is used for testing 
	 * purposes to ensure that addRoot
	 * is working properly
	 * 
	 * @return TreeItem
	 */
	public TreeItem getTestRootNode() {
		return testRootNode;
	}
	
	/**
	 * getTestBranchNodes <br><br>
	 * 
	 * This returns the list of 
	 * branch nodes in the tree, and is
	 * used for testing purposes to 
	 * ensure that addBranches iw working
	 * properly. 
	 * 
	 * @return ArrayList<TreeItem>
	 */
	public ArrayList<TreeItem> getTestBranchNodes() {
		return testBranchNodes;
	}
	
	/**
	 * setCompareDoc <br><br>
	 * 
	 * Set the value of the doc to be compared
	 * to this document
	 * 
	 * @param file - (String) The filepath to the file we want to compare
	 * 
	 * @return void
	 */
	public void setCompareDoc(String cfile) {
		try {
			this.cpath = cfile;
			Document d;
			d = XMLUtilities.getXMLDocNoCatch(cfile);
			this.cdoc = d;
			Configuration xconfig = new Configuration(cfile, fpath);
			this.xrefmap = xconfig.getRefMap();
			Configuration cconfig = new Configuration(fpath, cfile);
			this.crefmap = cconfig.getRefMap();
		} catch (SAXParseException e) {
			createWarning(cfile);
		} catch (SAXException e) {
			createWarning(cfile);
		} catch (Exception e) {
			createWarning(cfile);
		}
	}

	/**
	 * createContents <br><br>
	 * 
	 * Uses information from the document 
	 * to build the trees contents
	 * 
	 * @return Composite
	 */
	public Composite createContents() {
		
		//Initialization///////////////////////////
		//create a tree
		final Tree tree = new Tree(composite, SWT.ARROW | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		tree.setLocation(10, 31);
		tree.setSize(265, 461);
		//tree.setBackground(XBLACK); //TODO: COLOR INVERT
		
		//create the text box that will display attribute information for the node
		final Text abox = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		abox.setLocation(291, 51);
		abox.setSize(265, 168);
		abox.setEditable(false);

		//create the text box that will display the text element information for the node
		final Text ebox = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		ebox.setLocation(291, 256);
		ebox.setSize(265, 83);
		ebox.setEditable(false);
		
		//create the text box that will display the warning information for the node
		final Text wbox = new Text(composite, SWT.BORDER);
		wbox.setBounds(291, 372, 265, 21);
		wbox.setEditable(false);

		//labels
		Label lblXmlTree = new Label(composite, SWT.NONE);
		lblXmlTree.setBounds(10, 10, 55, 15);
		lblXmlTree.setText("XML Tree");
		
		Label infolabel = new Label(composite, SWT.NONE);
		infolabel.setBounds(352, 10, 149, 15);
		infolabel.setText("Selected Node Information");

		Label attributeslbl = new Label(composite, SWT.NONE);
		attributeslbl.setBounds(291, 31, 83, 15);
		attributeslbl.setText("Attributes:");

		Label testlbl = new Label(composite, SWT.NONE);
		testlbl.setBounds(291, 235, 83, 15);
		testlbl.setText("Text Elements:");
		
		Label lblWarning = new Label(composite, SWT.NONE);
		lblWarning.setBounds(291, 351, 55, 15);
		lblWarning.setText("Warning:");
		/////////////////////////////////////////////

		/////////////////////////////////////////////
		//[V]----------------------------------------------------//
		final Button btnDiffs = new Button(composite, SWT.CHECK);
		btnDiffs.setBounds(291, 410, 265, 16);
		btnDiffs.setText("Highlight Differences In Element Children");
		if(highlight_diff) {
			btnDiffs.setSelection(true);	
		}
		
		//diffs toggle listener
		//TODO find a wat to refactor these listeners, the problem is changing the highlight boolean because it needs to be final
		btnDiffs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (btnDiffs.getSelection()) {
                	highlight_diff = true;
                } else {
                	highlight_diff = false;
                }
                setChanged();
				notifyObservers(side);
            }
        });
		//-------------------------------------------------------//

		//[V]----------------------------------------------------//
		final Button btnExtra = new Button(composite, SWT.CHECK);
		btnExtra.setBounds(291, 432, 149, 16);
		btnExtra.setText("Highlight Extra Elements");
		if(highlight_extra) {
			btnExtra.setSelection(true);
		}

		//extras toggle listener
		//TODO find a wat to refactor these listeners, the problem is changing the highlight boolean because it needs to be final
		btnExtra.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnExtra.getSelection()) {
					highlight_extra = true;
				} else {
					highlight_extra = false;
				}
				
				setChanged();
				notifyObservers(side);
			}
		});
		//-------------------------------------------------------//

		//[V]----------------------------------------------------//
		final Button btnData = new Button(composite, SWT.CHECK);
		btnData.setBounds(291, 454, 220, 16);
		btnData.setText("Highlight Differences In Element Data");
		if(highlight_data) {
			btnData.setSelection(true);
		}

		//data toggle listener
		//TODO find a wat to refactor these listeners, the problem is changing the highlight boolean because it needs to be final
		btnData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnData.getSelection()) {
					highlight_data = true;
				} else {
					highlight_data = false;
				}
				
				setChanged();
				notifyObservers(side);
			}
		});
		
		//[V]----------------------------------------------------//
		final Button btnGuess = new Button(composite, SWT.CHECK);
		btnGuess.setBounds(291, 476, 265, 16);
		btnGuess.setText("Highlight Elements Possibly Matched Wrong");
		if(highlight_guess) {
			btnGuess.setSelection(true);
		}
		
		//guess toggle listener
		//TODO find a wat to refactor these listeners, the problem is changing the highlight boolean because it needs to be final
		btnGuess.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnGuess.getSelection()) {
					highlight_guess = true;
				} else {
					highlight_guess = false;
				}
				
				setChanged();
				notifyObservers(side);
			}
		});
		//-------------------------------------------------------//

		//[V]----------------------------------------------------//
		final Button btnStrict = new Button(composite, SWT.CHECK);
		btnStrict.setBounds(140, 10, 230, 16);
		btnStrict.setText("Element Order Matters");
		if(guess_strict) {
			btnStrict.setSelection(true);
		}
		
		//order toggle listener
		//TODO find a wat to refactor these listeners, the problem is changing the highlight boolean because it needs to be final
		btnStrict.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnStrict.getSelection()) {
					guess_strict = true;
				} else {
					guess_strict = false;
				}

				setChanged();
				notifyObservers(side);
			}
		});
		//-------------------------------------------------------//
		/////////////////////////////////////////////

		//Add element information listener///////////
		Listener select = new Listener() {
			public void handleEvent (Event event) {
				Point pxy = new Point(event.x, event.y);
				TreeItem node = tree.getItem(pxy);

				if (node != null) {
					abox.setText(node.getData("attribute").toString());
					ebox.setText(node.getData("text").toString());
					wbox.setText(node.getData("warning").toString());
				}
			}
		};

		tree.addListener(SWT.MouseDown, select);
		/////////////////////////////////////////////

		//Establish the root node////////////////////
		TreeItem root = addRoot(tree);
		testRootNode = root; //for testing addRoot
		/////////////////////////////////////////////

		//Add the branches///////////////////////////
		ArrayList<String> blist = XMLHelper.buildBranchList(xdoc);
		ArrayList<String> cblist = XMLHelper.buildBranchList(cdoc);

		ArrayList<TreeItem> branches = addBranches(root, blist, cblist);
		testBranchNodes = branches; //for testing addBranches
		////////////////////////////////////////////

		//Recursively Add (Branch) Children//////////
		for (TreeItem t : branches) {
			addBranchMembers(t); //Adds the children of each branch and all their descendants 
		}
		
		//root.setExpanded(true); //this is just preference, but probably not worth making a toggle for. 
		/////////////////////////////////////////////

		return composite;

	}
	
	/**
	 * createWarning <br><br>
	 * 
	 * Creates a warning dialog in the case
	 * that the file selected by the user to be
	 * rendered as an XML Tree has broken sytnax
	 * and therefore cannot be parsed. 
	 * 
	 * @param fpath - (String) The file path to the broken xml
	 * 
	 * @return void
	 */
	public void createWarning(String fpath) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		messageBox.setMessage("The file: \n\n" + fpath + "\n\n" + "could not be parsed. \n" + "Please check the file for formatting and syntax errors.");
		messageBox.setText("Invalid XML!");
		messageBox.open();
	}
	
	/**
	 * addRoot <br><br>
	 * 
	 * Add the root to the SWT tree
	 * 
	 * @param root - (Tree) The very root node
	 * 
	 * @return TreeItem
	 */
	private TreeItem addRoot(Tree root) {
		Node xroot = xdoc.getDocumentElement();
		Node croot = cdoc.getDocumentElement();
		
		TreeItem node = new TreeItem(root, SWT.ARROW);
		//node.setForeground(XWHITE); //TODO: COLOR INVERT
		node.setText(xroot.getNodeName());
		node.setData("attribute", XMLHelper.getNodeInfo(xroot, "attribute"));
		node.setData("text", XMLHelper.getNodeInfo(xroot, "text"));
		node.setData("warning", warning_none);
		
		
		if((xroot.getNodeName() != croot.getNodeName())) { //if the root elements for the doc arent the same, color the root red
			node.setData("warning", warning_extra);
			node.setText(node.getText() + " (+)");
			
			if(highlight_extra) {
				colorTrace(node, XEXTRA_ELEMENT);
			}
		}
		else {
			if(XMLHelper.hasAttributeDifferences(xroot, croot)) { //if there are attribute differences, highlight them
				node.setData("warning", warning_data);
				
				if(highlight_data) {
					colorTrace(node, XDATA_WARNING);
				}
			}
		}
		
		return node;
	}
	
	/**
	 * addBranchNodes <br><br>
	 * 
	 * Adds the list of branches 
	 * as nodes to the Tree and return
	 * the list of added Branch TreeItems
	 * 
	 * @param parent - (TreeItem) The parent (root) node to the branches
	 * @param branches - (ArrayList<String>) A list of the tree's branches
	 * @param cbrahnces - (ArrayList<String>) A list of the comparison tree's branches
	 * 
	 * @return ArrayList<TreeItem>
	 */
	private ArrayList<TreeItem> addBranches(TreeItem parent, ArrayList<String> branches, ArrayList<String> cbranches) {
		ArrayList<TreeItem> tlist = new ArrayList<TreeItem>(); //empty list to be filled with branches
		
		for(int i = 0; i < branches.size(); i++) {
			String branch = branches.get(i);
			//this is the message that will appear when the item is clicked
			String msg = "All the first generation" + "\n\n"  + "( " + branch + " ) elements " + "\n\n" +
					"This is a branch node, and therefore" + "\n" +
					"is only a grouping of like elements, not a"+ "\n" + 
					"representation of a real element in the XML";
			
			TreeItem node = new TreeItem(parent, SWT.ARROW);
			//node.setForeground(XWHITE); //TODO: COLOR INVERT
			node.setText("[ " + branch + " branch ]");
			node.setData("attribute", msg);
			node.setData("text", msg);
			node.setData("warning", warning_none);
			tlist.add(node);
			
			//check to see if this branch exists in the other list of branches of the comparison doc
			if (!cbranches.contains(branch)) {
				if(highlight_extra) {
					node.setForeground(XEXTRA_ELEMENT);
					//it can't have the " (+)" added here because that throws off addBranchMembers which searches for a precise substring
					//that would be off if " (+)" is added.
				}
				node.setData("warning", warning_extra);
				colorTrace(parent, XDIFF_CHILDREN);
			}
		}
		return tlist;
	}
	
	/**
	 * addBranchMembers <br><br>
	 * 
	 * Adds the memebers of a given branch 
	 * 
	 * @param branch - (TreeItem) The branch whose members are being added
	 * 
	 * @return void
	 */
	private void addBranchMembers(TreeItem branch) {
		//get all the kids of name "branch"
		NodeList children = xdoc.getElementsByTagName(branch.getText().substring(2, branch.getText().length() - 9)); //branch name includes " branch:", so substring that out
		NodeList compchildren = cdoc.getElementsByTagName(branch.getText().substring(2, branch.getText().length() - 9));

		//get the first born of those kids
		ArrayList<Node> first_children = XMLHelper.getFirstBorns(children, xdoc);
		ArrayList<Node> compfirst_children = XMLHelper.getFirstBorns(compchildren, cdoc);
		
		//if the branch has already been flagged as an extra
		if (branch.getData("warning") == warning_extra /* ||compfirst_children.size() == 0*/) {
			branch.setText(branch.getText() + " (+)");
			//branch doesn't need to be colored here because it should be already colored by 
			//addBranchNodes if it is an extra branch
		}
		addChildren(branch, first_children, compfirst_children);
	}
	
	/**
	 * addNextChildren <br><br>
	 * 
	 * This method guides the determination process for identifying 
	 * a nodes match within the list of nodes from the comparison 
	 * doc, and using that information to build a new node 
	 * into the tree and comparing its children against the identified match
	 * to continue the highlighting process.
	 * 
	 * @param parent - (TreeItem) The node the given children will be parented under in the Tree
	 * @param xchildren - (ArrayList<Node>) The children in the tree being constructed under parent
	 * @param comp_children - (ArrayList<Node>) The children to be the xchildren will be compared against
	 * 
	 * @return void
	 */
	private void addChildren(TreeItem parent, ArrayList<Node> xchildren, ArrayList<Node> comp_children) {
		ArrayList<TreeItem> tlist = new ArrayList<TreeItem>();
		
		//OUTLINE
		//for each member of xchildren...
		for(int i=0; i < xchildren.size(); i++) {
			Node child = xchildren.get(i); //child to add
			ArrayList<Node> next_children = XMLHelper.getElementChildren(child);// the element children of the node being observed

			//if the comparing list of children is empty, we know that if we are looking at a node it doesn't appear in the other list
			if (comp_children.isEmpty()) {
				TreeItem tnode = addNodeToList(parent, child, tlist);
				
				//if the parent is an already colored extra branch, colorTrace will overwrite its warning which we want to avoid
				if (parent.getText().startsWith("[") && parent.getForeground().equals(XEXTRA_ELEMENT)) { //if the parent is extra, dont colorTrace the node, just color
					tnode.setForeground(XEXTRA_ELEMENT);
					tnode.setText(tnode.getText() + " (+)");
				}
				else {
					colorTrace(tnode, XEXTRA_ELEMENT);	
				}
				
				tnode.setData("warning", warning_extra);
				addChildrenUncompared(tnode, next_children);
			}
			
			//if the comparison list is not empty, we have potential nodes to pair up with
			else {
				//(0) Does this child appear more than once in the doc and therefore exist in the xrefmap
				if (xrefmap.containsKey(child.getNodeName())) {
					//(1) Does this node have a special ID. 
					if (getUniqueId(child, xrefmap) != "NONENONE") {
						String uid =getUniqueId(child, xrefmap);

						//- (2) If yes: Does a matching node exist in cchildren?
						if (getIdMatch(child, uid, comp_children) != null ) {
							//- (3) If yes: pair the two with addNextChildren and pop the comp child from the list
							//This the best case, where we find a complete "match" of this node, in the comparing list
							addRealMatchChildren(parent, child, uid, comp_children, tlist);
						}
						//- (4) If no: Then highlight this child and create its children uncompared
						// (this is highlighted as extra because we know for certain it is not in the comparison
						else {
							//add child to parent, and to list that will be colored
							//add the tree item
							TreeItem tnode = addNodeToList(parent, child, tlist);
							colorTrace(tnode, XEXTRA_ELEMENT);
							tnode.setData("warning", warning_extra);
							addChildrenUncompared(tnode, next_children);
						}
					}
					//- (5) If no: Create the TreeItem and compare it with the first matching node (perhaps best guess??)
					else {
						//add child to parent, and to list that will be colored
						//highlight as impossible to check, (or add to a list of impossible to check because of color overlaps?)
						TreeItem tnode =addNodeToList(parent, child, tlist);
						Node match_child = XMLHelper.findClosestMatch(child, comp_children, guess_strict); //find the closest child remaining in the comp_children
						
						//highlight the node as a potential problem because its match is a guess
						if (highlight_guess) {
							tnode.setForeground(XGUESS_WARNING);
							tnode.setData("warning", warning_guess);
							tnode.setText(tnode.getText() + " (%)");
						}
						
						if (match_child != null) {
							
							//check for attribute data differences
							if (XMLHelper.hasAttributeDifferences(child, match_child) && highlight_data) {
								colorTrace(tnode, XDATA_WARNING);
							}
							
							ArrayList<Node> match_children = XMLHelper.getElementChildren(match_child);
							addChildren(tnode, next_children, match_children);

							//remove both children from the lists to leave only extra ones and avoid re-comparing
							//XMLTree.removeChild(tnode, tlist);
							XMLHelper.removeChild(match_child, comp_children);
						}
						
						else if (match_child == null) {
							TreeItem gnode = addNodeToList(parent, child, tlist);
							colorTrace(tnode, XEXTRA_ELEMENT);
							tnode.setData("warning", warning_extra);
							addChildrenUncompared(gnode, next_children);
						}
					}
				}
				//- (6) If no: the child appears only once in the doc
				else {
					//make a TreeItem out of the node
					TreeItem tnode = addNodeToList(parent, child, tlist);

					//this child only appears once in this doc, so check if it also appears in the other doc
					addSingleChild(tnode, child, comp_children, tlist);//below(?)
				}
			}
		}
	}
	
	/**
	 * getUniqueId <br><br>
	 * 
	 * Get the unique ID (an attribute that exists in only
	 * one node) if there is one and return it
	 * 
	 * returns "NONENONE" if an Id cannot be found
	 * 
	 * @param child - (Node) The child we are getting the ID of
	 * @param ref - (Map<String, String>) The reference map we are looking for the child's ID
	 * 
	 * @return String
	 */
	private String getUniqueId(Node child, Map<String, String> ref) {
		String id = "NONENONE"; //the id for the the child
		
		//if this node appears more than once in both maps, we have to compare more specifically
		if (ref.containsKey(child.getNodeName())) {
			NamedNodeMap attrs = child.getAttributes();
			String xkey_attr = xrefmap.get(child.getNodeName()); //what is the special, defining attribute for this element

			//if the defining attribute it neither TEXT or NULL
			if ((xkey_attr != "TEXTTEXT") && (xkey_attr != null)) {
				String xattr = attrs.getNamedItem(xkey_attr).toString(); //get the specific attribute 
				id = xattr; //this child has a unique Id and xattr is it!
			}
		}
		return id; 
	}
	
	/**
	 * getIdMatch  <br><br>
	 * 
	 * Return the node that matches the child and has the uid,
	 * in the list of nodes being compared
	 * 
	 * @param child - (Node) The child we are looking to find the match of 
	 * @param uid - (String) The id of the child to identify in the comparison children
	 * @param compare_children - (ArrayList<Node>) The list children we hope contains a match of child with the same uid
	 * 
	 * @return Node
	 */
	private Node getIdMatch(Node child, String uid, ArrayList<Node> compare_children) {
		//try to find the node, may not
		Node cnode = null;
		try {
			cnode = XMLValidation.getNode(cdoc, child.getNodeName(), uid);

			if (cnode != null) {
				return cnode;
			}

			//otherwise, this element can not be identified by the attribute in the comparison
			//xml, so it should be highlighted and created uncompared because there is not way to compare it
			else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return cnode;
	}
	
	
	/**
	 * addRealMatchChildren  <br><br>
	 * 
	 * This gets called if a child's match (node with the same uniqe id 
	 * in the comparison list) exists, then add the next generation
	 * of children to the proper parent and compare the children of the child node
	 * and its match because they should definitely be compared!
	 * 
	 * @param parent - (TreeItem) The TreeItem we want to parent the child under
	 * @param child - (Node) The child being added to the tree
	 * @param uid - (String) The unique id of the child 
	 * @param comp_children - (ArrayList<Node>) The children we want to find the match in
	 * @param tlist - (ArrayList<TreeItem>) The list of TreeItems each created TreeItem will be added to
	 * 
	 * @return void
	 */
	private void addRealMatchChildren(TreeItem parent, Node child, String uid, ArrayList<Node> comp_children, ArrayList<TreeItem> tlist) {
		Node match_child = getIdMatch(child, uid, comp_children);
		ArrayList<Node> next_children = XMLHelper.getElementChildren(child);
		ArrayList<Node> match_children = XMLHelper.getElementChildren(match_child);
		
		//add child to parent
		TreeItem tnode = addNodeToList(parent, child, tlist);
		
		//check for attribute data differences
		if (XMLHelper.hasAttributeDifferences(child, match_child) && highlight_data) {
			colorTrace(tnode, XDATA_WARNING);
		}
		
		//add the next generation of children
		addChildren(tnode, next_children, match_children);
		
		//remove both children from the lists to leave only extra ones and avoid re-comparing
		//XMLTree.removeChild(tnode, tlist);
		XMLHelper.removeChild(match_child, comp_children);
	}
	
	/**
	 * addSingleChild  <br><br>
	 * 
	 * If this is called, there is only one of this element in the document
	 * being constructed, so add it and check the other doc to see 
	 * if there is a matching node and construct it accordingly
	 * 
	 * @param parent - (TreeItem) The parent the node will be added under
	 * @param check - (Node) The node being added under the parent as a new item
	 * @param compare_list - (ArrayList<Node) The list that may contain a match to check
	 * @param tlist - (ArrayList<TreeItem>) The list of treeitems new ones will be added to
	 * 
	 * @return void
	 */
	private void addSingleChild(TreeItem parent, Node check, ArrayList<Node> compare_list, ArrayList<TreeItem> tlist) {
		ArrayList<Node> next_children = XMLHelper.getElementChildren(check); //the children that belong to parent, and may or may not be compared
		
		//lookup this element in the other doc, because here it should only appear once
		NodeList celements = cdoc.getElementsByTagName(check.getNodeName());//there should not be more than 1!!!

		//check to see if this node appears in the other document for child comparison
		if (celements.getLength() > 0) {
			Node match_child = celements.item(0);
			ArrayList<Node> match_children = XMLHelper.getElementChildren(match_child);//get the children of the identified node and send them to findAndAdd for comparison\

			addChildren(parent, next_children, match_children); //add next_children to parent and compare them to match_children
			
			//remove both children from the lists to leave only extra ones and avoid re-comparing
			XMLHelper.removeChild(parent, tlist);
			XMLHelper.removeChild(match_child, compare_list);
		}
		//if its not in the other doc, its an extra and should be colored as such
		else {
			colorTrace(parent, XEXTRA_ELEMENT);
			addChildrenUncompared(parent, next_children); //add children the parent unmatched
		}
	}
	
	/**
	 * addChildrenUncompared  <br><br>
	 * 
	 * This method is called when the child 
	 * cannot be compared to anything else in the comparison
	 * doc, so it will be highlighted as an extra and
	 * its children will be not compared to anything else
	 * 
	 * @param parent - (TreeItem) The parent the children will be added under
	 * @param children - (ArrayList<Node>) The children that belong to the parent
	 * 
	 * @return void
	 */
	private void addChildrenUncompared(TreeItem parent, ArrayList<Node> children) {
		ArrayList<TreeItem> tlist = new ArrayList<TreeItem>();
		
		for(Node child: children) {
			ArrayList<Node> next_children = XMLHelper.getElementChildren(child);
			TreeItem tnode = addNodeToList(parent, child, tlist);
			if(highlight_extra) {
				tnode.setForeground(XEXTRA_ELEMENT);
			}
			tnode.setData("warning", warning_extra);
			addChildrenUncompared(tnode, next_children);
		}
	}
	
	/**
	 * addNodeToList  <br><br>
	 * 
	 * This method adds the child to the the given parent
	 * in the form of a TreeItem, and then adds that
	 * new TreeItem to the given list of TreeItems.
	 * 
	 * It returns the newly created TreeItem
	 * 
	 * @param parent - (TreeItem) The parent of the child being added
	 * @param child - (Node) The node of the child to be added as a new TreeItem
	 * @param tlist - (ArrayList<TreeItem>) The list of TreeItems this new TreeItem should be added to
	 * 
	 * @return TreeItem
	 */
	private TreeItem addNodeToList(TreeItem parent, Node child, ArrayList<TreeItem> tlist) {
		TreeItem tnode = new TreeItem(parent, SWT.ARROW);
		//tnode.setForeground(XWHITE); //TODO: COLOR
		tnode.setText(child.getNodeName());
		tnode.setData("attribute", XMLHelper.getNodeInfo(child, "attribute"));//store the nodes attributes as a kv pair of the node data
		tnode.setData("text", XMLHelper.getNodeInfo(child, "text"));//store the nodes text children as a kv pair of the node date
		tnode.setData("warning", warning_none);
		
		tlist.add(tnode); //add the node to the list of branch children
		return tnode;
	}
	
	/**
	 * colorTrace  <br><br>
	 * 
	 * Color this node, and highlight its line of 
	 * ancestors to inform the user where the 
	 * differences are occurring in the tree
	 * 
	 * @param tnode - (TreeItem) The item that will be colored
	 * @param Color - (Color) The color (SWT color) to highlight the nodes
	 * 
	 * @return void
	 */
	public void colorTrace(TreeItem tnode, org.eclipse.swt.graphics.Color color) {
		TreeItem pitem = tnode.getParentItem();

		//if we want to color child diffs (or differences) and if we can
		if (color.equals(XDIFF_CHILDREN) && highlight_diff) {
			tnode.setForeground(XDIFF_CHILDREN);
			tnode.setData("warning", warning_diff);
			
			//check if there there is a tag on it, if not add one
			if (!tnode.getForeground().equals(XEXTRA_ELEMENT) && !tnode.getText().endsWith("(+)") && !tnode.getText().endsWith("<!>")) {
				tnode.setText(tnode.getText() + " <!>");
			}
		}
		
		//if we want to color because of data differences and if we can
		else if (color.equals(XDATA_WARNING) && highlight_data) {
			tnode.setForeground(XDATA_WARNING);
			tnode.setData("warning", warning_data);
			
			//check if there is already a tag on it or not, if not, add one
			if (!tnode.getText().endsWith("<@>")) {
				tnode.setText(tnode.getText() + " <@>"); //we add this no matter what because an elements can have both a <!> or (%) AND a (#)
			}
		}
		
		//if we want to color extra children and if we can
		else if (color.equals(XEXTRA_ELEMENT) && highlight_extra) {
				tnode.setForeground(XEXTRA_ELEMENT);
				tnode.setData("warning", warning_extra);
				
			//check if it has a tag on it yet, if not add one
			if (!tnode.getText().endsWith("(+)")) { 
				tnode.setText(tnode.getText() + " (+)");
			}
		}
		
		//check if there is a parent to color
		if(pitem != null){
			colorTrace(pitem, XDIFF_CHILDREN); //color the parent
		}
	}
	
	/**
	 * Launch the viewer
	 * @param args
	 */
	public static void main(String[] args) {
		//take in and prep the file path string
		//- turn it into a document
		//- make a new NewXMLTreeModernBuilder with it
		//- set all the Tree Items in place
		/*
		String fpath = args[0]; //must be a file path!
 		Display display = Display.getDefault();
		final Shell shell = new Shell (display);
		shell.setSize(900, 7);
		final TabFolder tabFolder = new TabFolder (shell, SWT.BORDER);
		tabFolder.setSize(568, 500);
		//Make an XMLTree
		tabFolder.setLocation (10, 0);		

		TabItem singleTree = new TabItem(tabFolder, SWT.NONE);
		singleTree.setText("XML Tree");
		XMLTree xtree;
		try {
			xtree = new XMLTree(tabFolder,fpath, "left");
			singleTree.setControl(xtree.createContents());
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		tabFolder.pack ();
		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
		*/
		////////////////////////////////////////////
		}
}