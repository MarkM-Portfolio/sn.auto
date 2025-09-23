package resources;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * This class defines an XMLAnalyzer.
 * 
 * An XMLAnalyzer stores (and is constructed by)
 * an XML document and is used
 * to check the elemental, structural equality
 * of another XMLAnalyzer. In checking the structural
 * equality of an XMLAnalyzer, it determines if 
 * the documents being compared have the correct 
 * ordering of elements and parent child relationships
 * between those elements.
 * 
 * This means that if two documents are equal,
 * they will have identical element hierarchies,
 * regardless of element attributes. 
 * 
 * It calls on the XMLHelper class to process
 * and repurpose certain parts of the Document. 
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-05-16
 */
public class XMLAnalyzer {
	
	/**
	 * variables 
	 */
	private Document xdoc;
	private XMLPrinter xp;
	
	/**
	 * constructor
	 * 
	 * Sets xdoc as the Document upon construction
	 * 
	 * @param Document - (Document) The Document to be analyzed
	 * 
	 * @return XMLAnalyzer
	 */
	public XMLAnalyzer(Document xml) {
		this.xdoc = xml;
	}
	
	/**
	 * getXDoc <br><br>
	 * 
	 * Return the XMLAnalyzer's Document private variable, xdoc
	 * 
	 * @return Document
	 */
	public Document getXDoc() {
		return this.xdoc;
	}
	
	/**
	 * getXMLPrinter <br><br>
	 * 
	 * Return the XMLAnalyzer's XMLPrinter
	 * 
	 * @return XMLPrinter
	 */
	public XMLPrinter getXMLPrinter() {
		return this.xp;
	}
	
	/**
	 * equals <br><br>
	 * 
	 * Checks if this Object is equal to the given Object.
	 * One XMLAnalyzer is equal to another object (Object obj) if and only if:
	 * - obj is of type XMLAnalyzer
	 * - the xdoc of obj is equal to this XMLAnalyzer's xdoc
	 * - this tree construct of the obj is equal to this XMLAnalyzer's tree construct
	 * 
	 * @param obj - (Object) The object being checked for equality
	 * 
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof XMLAnalyzer) {
			XMLAnalyzer ot = (XMLAnalyzer) obj;
			boolean eq_docs  = this.xdoc.equals(ot.getXDoc());
			return this.hasEqualTree(ot) && eq_docs;
		}
		else {
			return false;
		}
	}
	
	/**
	 * hasEqualTree <br><br>
	 * 
	 * Checks to see that the tree structure of 
	 * an XMLAnalyzer is the same as this one. This test 
	 * is used to show whether or not two XML Documents 
	 * have the same structure of their elements, even if 
	 * their file names and attributes are different. 
	 * 
	 * @param xt - (XMLAnalyzer) XMLAnalyzer being compared for it's Document's structural tree equality
	 * @param print - (boolean) If print is true, then when a difference is encountered between trees it will print them to console
	 * 
	 * @return boolean
	 */
	public boolean hasEqualTree(XMLAnalyzer xt){
		return this.scanTree(xt, "equals");
	}
	
	/**
	 * printIfDifferent <br><br>
	 * 
	 * Compares this XMLAnalyzer's tree to the tree
	 * of the argument xt. If there is a difference 
	 * between the two, output those differences. 
	 * 
	 * 
	 * <br> <br>
	 * NOTE: In scanTree's current version, 
	 * if there are multiple differences in the 
	 * children, it will only print out a 
	 * difference notice for one of them. 
	 * A future version may print out notices
	 * for multiple child differences. <br>
	 * 
	 * @param xt - (XMLAnalyzer) The XMLAnalyzer whose tree is being compared
	 * 
	 * @return String
	 */
	public String printIfDifferent(XMLAnalyzer xt, String type) {
		String diffs; //this is the string that will be set and returned
		
		if (type.equalsIgnoreCase("console")) {
			this.xp = new XMLPrinter(xdoc, "console");
			this.scanTree(xt, "print");
			diffs = xp.output.viewDifferences();
		}
		else if (type.equalsIgnoreCase("string")) {
			this.xp = new XMLPrinter(xdoc, "string");
			this.scanTree(xt, "print");
			diffs = xp.output.viewDifferences();
		}
		else {
			diffs = "!ERROR! IMPROPER OUTPUT TYPE: " + type + " IS NOT A VALID OUTPUT TYPE!";
		}
		return diffs;
	}
	
	/**
	 * printIfDifferent <br><br>
	 * 
	 * This version of printIfDifferent writes to a 
	 * specified file path
	 * 
	 * @param xt - (XMLAnalyer) The XMLAnalyzer whose tree is being compared
	 * @param fpath - (String) The filepath for differences to be written out to
	 * 
	 * @return String
	 */
	public String printIfDifferent(XMLAnalyzer xt, String type, String fpath) {
		String diffs;
		
		if (type.equalsIgnoreCase("file")) {
			//set the xp with a ToFile output, using the fpath for the desired file
			this.xp = new XMLPrinter(xdoc, "file", fpath);
			this.scanTree(xt, "print");
			diffs = xp.output.viewDifferences();	
		}
		else {
			diffs = "!ERROR! IMPROPER OUTPUT TYPE: " + type + " IS NOT A VALID OUTPUT TYPE!";
		}
		
		return diffs;
	}

	/**
	 * printDebug <br><br>
	 * 
	 * Prints out information to console
	 * about the Document in this object
	 * for debugging purposes
	 * 
	 * @return void
	 */
	public void printDebug() {
		this.xp = new XMLPrinter(xdoc, "console");
		xp.printDebug();
	}
	
	/**
	 * printDebug <br><br>
	 * 
	 * Writes out the debug to the the 
	 * file specified by the given file path
	 * 
	 * @param fpath - (String) The path to the file to be written to
	 * 
	 * @return void
	 */
	public void printDebug(String fpath) {
		this.xp = new XMLPrinter(xdoc, "file", fpath);
		xp.printDebug();
	}
	
	/**
	 * scanTree <br><br>
	 * 
	 * Scans this XMLAnalyzer and another one, and based 
	 * off of the given type argument, performs certain
	 * tasks and returns a boolean measuring equality
	 * 
	 * @param xt - (XMLAnalyzer) The XML being compared to this one
	 * @param type - (String) The type of scan we want to complete
	 * 
	 * @return boolean
	 */
	private boolean scanTree(XMLAnalyzer xt, String type){
		Node top_node = this.xdoc.getDocumentElement();
		Node xnode = xt.xdoc.getDocumentElement();
		String top_name = top_node.getNodeName();
		String xname = xnode.getNodeName();
		ArrayList<String> top_branches = XMLHelper.buildBranchList(this.xdoc);
		ArrayList<String> xbranches = XMLHelper.buildBranchList(xt.xdoc);
		
		//checks
		boolean same_top = top_name.equals(xname);
		boolean same_branches = this.scanBranches(top_branches, xbranches, xt, type);
		
		//if there are differences and we want to print them, print them!
		if (type.equalsIgnoreCase("print") && !same_top) {
			xp.printDifferentDocs(top_node,xnode);
		}
		return (same_top && same_branches);
	}
	
	/**
	 * scanBranches <br><br>
	 * 
	 * Check to see that the Branches of the two
	 * XMLAnalyzers are equal by comparing their Branch
	 * lists and then check their children
	 * 
	 * @param cbranches - (ArrayList<String>) The BranchForkList of this XMLAnalyzer
	 * @param xbranches - (ArrayList<String>) The BranchForkList of the XMLAnalyzer being compared
	 * @param xmlt - (XMLAnalyzer) The XMLAnalyzer being compared
	 * @param type - (String) The type of scan we want to complete
	 * 
	 * @return boolean
	 */
	private boolean scanBranches(ArrayList<String> cbranches, ArrayList<String> xbranches, XMLAnalyzer xmlt, String type) {
		boolean equal_list = cbranches.equals(xbranches);
		boolean equal_branches = equalBranchSizes(cbranches, xbranches, xmlt);
		boolean equal_children = false;
		
		if (equal_list && equal_branches) {
			for (int i = 0; i < cbranches.size(); i++) {
				String child = cbranches.get(i);
				String xchild = xbranches.get(i);
				ArrayList<Node> child_children = XMLHelper.getFirstBorns(this.xdoc.getElementsByTagName(child), this.xdoc);
				ArrayList<Node> x_children = XMLHelper.getFirstBorns(xmlt.xdoc.getElementsByTagName(xchild), xmlt.xdoc);
				
				if (i == 0){
					equal_children = true;
				}
				//combine the booleans of the children and set equal_children to it
				equal_children = (equal_children && this.scanBranchMembers(child_children, x_children, type, xmlt.xdoc));
			}
		}
		//if there are differences and we want to print them, print them!
		else if (type.equalsIgnoreCase("print") && !equal_list) {
			xp.printDifferentBranches(cbranches,xbranches, xmlt.xdoc);
		}
		else if (type.equalsIgnoreCase("print") && !equal_branches) {
			xp.printDifferentBranchSizes(cbranches,xbranches, xmlt.xdoc);
		}
		return equal_list && equal_children;
	}
	
	/**
	 * scanBranchMembers<br><br>
	 * 
	 * Scans the lists of members in a branch (all the elements of the same name)
	 * and compares them between docs. Then sends each list member to scanChildren
	 * to check them. 
	 * 
	 * @param clist - (ArrayList<Node>) The list of nodes from the doc we are reading
	 * @param xlist - (ArrayList<Node>) The list of nodes from the other doc
	 * @param type - (String) The type of scan we want to complete
	 * @param doc - (Document)
	 * 
	 * @return
	 */
	private boolean scanBranchMembers(ArrayList<Node> clist, ArrayList<Node> xlist, String type, Document doc) {
		ArrayList<String> cs = XMLHelper.convertList(clist);
		ArrayList<String> xs = XMLHelper.convertList(xlist);
		boolean equal = false;
		
		if (cs.equals(xs) && cs.isEmpty()) {
			equal = true;
		}
		else if (cs.equals(xs)){
			for(int i = 0; i < clist.size(); i++) {
				Node n = clist.get(i);
				Node x = xlist.get(i);
				
				if (i == 0) {
					equal = true;
				}
				equal = (equal && this.scanChildren(n, x, type, doc));
			}
		}
		//if there are differences in branch member size, 
		//they will get adressed and printed in bulk by scanBranches
		return equal;
	}
	
	/**
	 * scanChildren <br><br>
	 * 
	 * Takes in a NodeList of an elements children from both files, 
	 * as well as the file being compared, and checks if both NodeLists
	 * are equal by running equalChildren on their children.
	 * 
	 * @param parent
	 * @param xparent
	 * @param type - (String) The type of scan we want to complete
	 * @param doc
	 * 
	 * @return
	 */
	private boolean scanChildren(Node parent, Node xparent, String type, Document doc) {
		ArrayList<Node> clist = XMLHelper.getElementChildren(parent);
		ArrayList<Node> xlist = XMLHelper.getElementChildren(xparent);
		ArrayList<String> cs = XMLHelper.convertList(clist);
		ArrayList<String> xs = XMLHelper.convertList(xlist);
		boolean equal = false;
		
		if (cs.equals(xs) && cs.isEmpty()) {
			equal = true;
		}
		else if (cs.equals(xs)){
			for(int i = 0; i < clist.size(); i++) {
				Node n = clist.get(i);
				Node x = xlist.get(i);
				
				if (i == 0) {
					equal = true;
				}
				equal = (equal && this.scanChildren(n, x, type, doc));
			}
		}
		//if there are differences and we want to print them, print them!
		else if (!cs.equals(xs) && type.equalsIgnoreCase("print")){
			xp.printDifferentChildren(parent, xparent, doc);
		}
		return equal;
	}
	
	/**
	 * equalBranchSizes <br><br>
	 * 
	 * Returns whether or not the branch in each list has the same
	 * number of occurrences of each branch item in their 
	 * respective documents
	 * 
	 * @param mybranches - (ArrayList<String>) The branches of the reference doc
	 * @param otherbranches - (ArrayList<String>) The branches of the doc being checked
	 * @param mydoc - (Document) The reference doc
	 * @param otherdoc - (Document) The doc being checked
	 * 
	 * @return boolean
	 */
	private boolean equalBranchSizes(ArrayList<String> mybranches, ArrayList<String> otherbranches, XMLAnalyzer xmlt) {
		boolean equal = true;
		
		if (!mybranches.equals(otherbranches)) {
			equal = false;
			return equal;
		}
		else {
			for(int i = 0; i < mybranches.size(); i++) {
				String m = mybranches.get(i);
				ArrayList<Node> mchildren = XMLHelper.getFirstBorns(this.xdoc.getElementsByTagName(m), this.xdoc);
				ArrayList<Node> ochildren = XMLHelper.getFirstBorns(xmlt.xdoc.getElementsByTagName(m), xmlt.xdoc);
				
				//equal is true only if both docs have the same number of children by the name 
				equal = equal && (mchildren.size() == ochildren.size());
			}
		}
		return equal;
	}
}
