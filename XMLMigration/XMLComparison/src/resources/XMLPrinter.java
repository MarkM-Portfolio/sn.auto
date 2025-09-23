package resources;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import output.Output;
import output.ToConsole;
import output.ToFile;
import output.AsString;



/**
 * 
 * This class is designed to take in 
 * a Document and print in a special Tree format.
 * 
 * The organization of the print is based around
 * the "branches" of the document. These branches 
 * are ways to group the children of the XML's top node into categories. 
 * All of the top node's children
 * of a particular type are grouped into these branches,
 * and in printing, are found in the section labeled
 * "(branch name) BRANCH" 
 * 
 * It calls on the XMLHelper class to process
 * and interpret certain parts of the Document.
 * 
 * Current legal output arguments for 
 * XMLPrinter construction are:
 * - "console" (for outputting to the console)
 * - "file" (for outputting to the file)
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-05-22
 */
public class XMLPrinter {

	/**
	 * variables
	 */
	private Document xdoc;
	Output output;
	
	//some string variables used for difference printing
	private static String bar;
	private static String in;
	private static String vs;
	private static String divider;
	private static String banner;
	private static String dif;
	private static String closer;
	
	/**
	 * constructor <br><br>
	 * 
	 * Sets the Document for printing to a file
	 * 
	 * @param xml - (Document) The Document to be printed
	 * @param out - (String) The type of output for this object
	 */
	public XMLPrinter(Document xml, String out) {
		this.initialize(xml); //set up the difference strings and xml
		//set output type
		if (out.equalsIgnoreCase("console")) {
			this.output = new ToConsole();
		}
		if (out.equalsIgnoreCase("string")) {
			this.output = new AsString();
		}
	}
	
	/**
	 * constructor <br><br>
	 * 
	 * Sets the Document for printing to a file
	 * 
	 * @param xml - (Document) The Document to be printed
	 * @param out - (String) The type of output for this object
	 * @param target - (String) The filename this will print to
	 */
	public XMLPrinter(Document xml, String out, String target) {
		this.initialize(xml); //set up the difference strings
		//set output type
		if (out.equalsIgnoreCase("file")) {
			try {
				this.output = new ToFile(target);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * initialize
	 */
	private void initialize(Document xml) {
		this.xdoc = xml;
		bar = "***********";
		in = "-------------- IN ---------------";
		vs = "               vs.               ";
		divider = "---------------------------------";
		banner = bar + "DIFFERENCES" + bar;
		dif = "--------Is Different Than--------";
		closer = bar + bar + bar + "\n";
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
		String top_node_name = xdoc.getDocumentElement().getNodeName();
		String line = "-----------------------------------------";
		ArrayList<String> branches =  XMLHelper.buildBranchList(this.xdoc);;
		
		output.display("<<================DEBUG================>>");
		output.display("The top node is : " + top_node_name);
		output.display("In the file " + xdoc.getBaseURI());
		output.display(line);
		output.display("Branch Element Count: (There are " + branches.size() + " branches)");
		output.display("\n");
		this.branchCountPrint();
		output.display(line);
		output.display("The xml document printed by its branches : ");
		output.display("\n");
		this.printStructure();
		output.display("\n");
		output.display("<<=====================================>>");
	}

	/**
	 * branchCountPrint <br><br>
	 * 
	 * Prints out how many first generation (direct children of the
	 * Document node) children are in each branch and the branch name
	 * 
	 * @return void
	 */
	public void branchCountPrint() {
		Node top_node = this.xdoc.getDocumentElement();
		ArrayList<String> branches = XMLHelper.buildBranchList(this.xdoc);
		String bend = " branch";
		
		//for element named branch, get all the elements by that name
		for(int i = 0; i < branches.size(); i++) {
			String b = branches.get(i);
			NodeList blist = this.xdoc.getElementsByTagName(b);
			int count = XMLPrinter.branchCounter(blist, top_node);

			output.display("There are : " + count + " members of the " + "( " +  b + " )" + bend);
		}
	}
	
	/**
	 * branchCounter <br><br>
	 * 
	 * This method counts how many first generation children are 
	 * in the blist argument
	 * 
	 * @param blist - (NodeList) The list of children being counted
	 * @param root - (Node) The root of the document
	 * 
	 * @return int
	 */
	private static int branchCounter(NodeList blist, Node root) {
		int count = 0;
		
		for(int j = 0; j < blist.getLength(); j++){
			Node n = blist.item(j);
			Node nparent = n.getParentNode();
			boolean top_child = nparent.equals(root); //is the node's parent the top node
			
			if(top_child){ //if this node is first generation
				count++;
			}
		}
		return count;
	}
	
	
	/**
	 * printStructure <br><br>
	 * 
	 * Prints out the XML file represented as a series 
	 * of tree branches with branch elements (and their children)
	 * beneath. For example, the following simple XML file:
	 * 
	 * <pre>
	 * {@code
<?xml version="1.0" encoding="UTF-8"?>
<!--Contains configuration for XMLValidator-->
<config>
	<foo/>
	<foo>
		<zoo/>
		<zoo/>
	</foo>
	<bar/>
	<bar/>
</config>
}
	 * </pre>
	 * 
	 * Will print out like this:
	 * <pre>
	 * [CONFIG]
	 *----------foo branch:----------
	 * foo
	 * foo
     *	zoo (x 2)
     *
     *----------bar branch:----------
     * bar (x 2)
     *
     * [/CONFIG]
     * </pre>
     * There are a few properties to how this prints the XML tree structure out.
     * <br>
     * <ol>
     * 	<li>
     * 		Formatting:<br>
     * 		The formatting of the print is designed to be intuitive and reflective 
     * 		of the XML. At the top, in brackets and all caps, is the name of the 
     * 		Document's element. Then, each branch is indicated by a divider with
     * 		dashes and the name of the branch.<br><br>
     * 		Within each branch the name of an element is printed. Every child of an element
     * 		is printed beneath it, and tabbed over 5 spaces right of where its parent's name
     * 		is printed. At the bottom of the print, the Document element is once again 
     *		printed to indicate that all the elements have been printed. <br><br>
     * 	</li>
	 * 	<li>
	 * 		Branches:<br>
	 * 		The XML tree is printed out according by branch. Each branch is defined
	 * 		by a first generation (direct child of the Document element) element. 
	 * 		In the example above, there are two types of first generation elements,
	 * 		"foo" and "bar". Therefore, two branches are created. One for first generation
	 * 		"foos", one for first generation "bars". <br><br>
	 * 		There is NOT a "zoo" branch, because it is NOT a first generation child. <br><br>
	 * 		Likewise, is there existed a "bar" element that was a child of a "zoo", 
	 * 		it would be printed beneath its parent "zoo" inside the "foo" branch. 
	 * 		This is because the "bar" in question is not a first generation "bar",
	 * 		so it would not be listed under the first generation "bar" branch. It would 
	 * 		spaces appropriately as a child beneath a "zoo" element in the "foo" branch.
	 * 		<br><br>
	 * 	</li>
	 * 	<li>
	 * 		Element Summaries:<br>
	 * 		In order to summarize the XML file and minimize the length of the print (while preserving accuracy)
	 * 		some element names have a number (encased in parentheses) printed next to them. 
	 * 		This is a way of grouping elements that are of the same name and have no children. Rather
	 * 		than print out a long list of uninteresting element names in a column, this prints out
	 * 		the name of the element, and how many childless elements of that name exist within
	 * 		the containing parent node. The parentheses with the group number will only print if
	 * 		the number is greater than 1. Single elements with no children are printed out 
	 * 		like elements with children, and simply have no children beneath them. <br><br>
	 * 		In the example above, there are two instances of element summaries. One for the "zoos",
	 * 		and one for the "bars". In the "foo" branch, there are two "foo" elements. One has no children,
	 * 		so just its name is printed. The other has two children, a pair of "zoos". Because the "zoos" in question
	 * 		are identical in that they have the same name and no children, they get grouped together with an 
	 * 		element summary multiplier. Likewise, the two "bars" in the "bar" branch are childless, so they are
	 * 		grouped together with a multiplier as well.  
	 * 		
	 * 	</li>
	 * </ol>
	 */
	public void printStructure() {
		Node top_node = this.xdoc.getDocumentElement();
		String top_node_name = top_node.getNodeName();
		ArrayList<String> branches = XMLHelper.buildBranchList(this.xdoc);
		String bend = " branch:";
		String block = "----------";
		
		output.display("[" + top_node_name.toUpperCase() + "]"); //print the top document node
		for(int i = 0; i < branches.size(); i++) {
			String b = branches.get(i);
			
			output.display(block + b + bend + block);
			this.printBranch(b); //print the branches
		}
		output.display("[/" + top_node_name.toUpperCase() + "]");
	}
	
	/**
	 * printBranch <br><br>
	 *  
	 * Print the branch by the given name. 
	 *  
	 * @param branch - (String) Name of the branch to be printed
	 * 
	 * @return void
	 */
	private void printBranch(String branch){
		Node top_node = this.xdoc.getDocumentElement(); //what is the parent node of the Document
		NodeList nlist = this.xdoc.getElementsByTagName(branch); //get doc elements named "branch"
		ArrayList<Node> alist = new ArrayList<Node>();
		ArrayList<String> acc = new ArrayList<String>();
		
		//for every element that goes by the name "branch"
		for(int i = 0; i < nlist.getLength(); i++){
			Node n = nlist.item(i);
			Node nparent = n.getParentNode();
			boolean top_child = nparent.equals(top_node); //is the node's parent the top node?
			
			if(top_child){ //if this node is first generation
				alist.add(n);
			}
		}
		this.printSummary(alist, acc, 0, "");
		output.display("\n"); //spacer between branches
	}
	
	/**
	 * printSummary <br><br>
	 * 
	 * Prints out each node in nlist. While printing out the nodes in nlist, 
	 * it checks to see if it has children and if it has been printed before.
	 * This method looks at two conditions.<br><br>
	 * <ol>
	 * 	<li>
	 * If the node has children, it prints out the name of the node, and sends
	 * its children to adjustSpace and increments the height so that
	 * the children will be printed bumped over to the right.<br><br>
	 * 	</li>
	 * 	<li>
	 * If the node does not have children, it is eligible to be printed next to
	 * an element summary multiplier. Whether or not it does however, 
	 * depends on if the accumulator acc currently contains the name of the node.
	 * If it doesn't, then the node is printed next to a multiplier describing the 
	 * number of nodes in nlist with the same name that also do not have children.
	 * If the name is in the accumulator, it does not get printed.
	 * 	</li>
	 * 
	 * </ol>
	 * 
	 * @param nlist - (ArrayList<Node>) A series of nodes describing some other nodes children
	 * @param acc - (ArrayList<String>) An accumulation of the names of childless nodes that have been looked at
	 * @param height - (int) The height or depth into the tree
	 * @param space - (String) The current string that defines how far right the elements appear
	 * 
	 * @return void
	 */
	private void printSummary(ArrayList<Node> nlist, ArrayList<String> acc, int height, String space) {
		for(int i = 0; i < nlist.size(); i++){
			Node n = nlist.get(i);
			String nname = n.getNodeName();
			ArrayList<Node> clist = XMLHelper.convertListNode(n.getChildNodes());
			
			boolean type_node = (n.getNodeType() == Node.ELEMENT_NODE); //is this node an element
			boolean has_children = XMLHelper.getElementChildren(n).size() > 0;
			boolean in_acc = acc.contains(nname);

			//if the node has children, dont print a summary. Asjust the space for the children about to be printed
			if (type_node && has_children) {
				output.display(space + nname);
				this.adjustSpace(clist, height+1);
			}

			//if the node doesn't have children, AND hasn't been printed with a multiplier yet, print with (x X)
			else if (type_node && !has_children && !in_acc) {
				String count = "";
				
				//if there is more than one of these nodes with no children, we want a multiplier to print out
				if (XMLHelper.childlessNodeCounter(n,nlist) > 1) {
					count = " (x " + XMLHelper.childlessNodeCounter(n,nlist) + ")";
				}
				output.display(space + nname + count); //print the node
				acc.add(nname); //add to the list of childless nodes already printed 
			}
		}
	}
	
	/**
	 * adjustSpace <br><br>
	 * 
	 * Adds more space to a string based off of the given height.
	 * This will be used to tab over the nlist to its appropriate
	 * position in the print out. 
	 * 
	 * @param nlist - (ArrayList<Node>) The list of nodes being passed along to printSummary
	 * @param height - (int) The height or depth into the tree
	 * 
	 * @return void
	 */
	private void adjustSpace(ArrayList<Node> nlist, int height) {
		String space = "";
		String tab = "    ";
		ArrayList<String> acc = new ArrayList<String>();

		for(int s = 0; s < height; s++){
			 space = space.concat(tab); //change the tab based off height into tree
		}
		this.printSummary(nlist,acc,height,space);
	}
	
	/**
	 * printDifferentDocs <br><br>
	 * 
	 * Prints to console a statement about the two given
	 * nodes (the Document roots of two Documents)
	 * being different from each other 
	 * 
	 * @param mynode - (Node) The root node of the reference Document
	 * @param other - (Node) The root node of the Document being checked
	 * 
	 * @return void
	 */
	public void printDifferentDocs(Node mynode, Node other) {
		String myname = mynode.getNodeName().toUpperCase();
		String othername = other.getNodeName().toUpperCase();
		String myns = mynode.getBaseURI();
		String otherns = other.getBaseURI();
		
		printHeader(myns,otherns); //print the differences header
		output.display("!Difference! = Inequal Document Roots");
		output.display("\n");
		output.display("[" + myname + "]" + " vs. " + "[" + othername + "]");
		output.display(closer);
		
	}
	
	/**
	 * printDifferentBranches <br><br>
	 * 
	 * Prints to console the differences in the branch lists
	 * of two XMLAnalyzers
	 * 
	 * @param mybranches - (ArrayList<String>) The list of branches in the reference Document
	 * @param otherbranches - (ArrayList<String>) The list of branches in the Document being checked
	 * @param mydoc - (Document) The reference Document
	 * @param otherdoc - (Document) The Document being checked
	 * 
	 * @return void
	 */
	public void printDifferentBranches(ArrayList<String> mybranches, ArrayList<String> otherbranches, Document otherdoc) {
		String myns = xdoc.getBaseURI();
		String otherns = otherdoc.getBaseURI();
		
		printHeader(myns,otherns); //print the differences header
		output.display("!Difference! = Inequal Tree Branches");
		output.display("\n");

		ArrayList<String> longer;
		ArrayList<String> shorter;
		if (mybranches.size() >= otherbranches.size()) { //figure out which list is longer
			longer = mybranches;
			shorter = otherbranches;
		}
		else {
			longer = otherbranches;
			shorter = mybranches;
		}
		
		for(int i = 0; i < longer.size(); i++) {
			String m = longer.get(i);
			String t;
			if (i < shorter.size()) { //make sure not to try to index outside the length of the shorter list
				t = shorter.get(i);
			}
			else {
				t = "(no branch)";
			}
			//print the different branches in the order
			//that reflects the XML comparison
			if (longer.equals(mybranches)) {
				output.display(m + " vs. " + t);	
			}
			else {
				output.display(t + " vs. " + m);
			}
		}
		output.display(closer);
	}
	
	/**
	 * printDifferentBranchSizes <br><br>
	 */
	public void printDifferentBranchSizes(ArrayList<String> mybranches, ArrayList<String> otherbranches, Document otherdoc) {
		Node mynode = this.xdoc.getElementsByTagName(mybranches.get(0)).item(0);
		Node othernode = otherdoc.getElementsByTagName(otherbranches.get(0)).item(0);
		
		Node myparent = XMLHelper.parentFinder(mynode, xdoc);
		Node otherparent = XMLHelper.parentFinder(othernode, otherdoc);
		String myns = myparent.getBaseURI();
		String otherns = otherparent.getBaseURI();
		
		printHeader(myns, otherns); //print the differences header
		output.display("!Difference! = Inequal Branch Sizes");
		output.display("\n");
		//go through the branches and print out branches with a different number of children between each file
		for(int i = 0; i < mybranches.size(); i++) {
			String child = mybranches.get(i);
			NodeList mychildren = this.xdoc.getElementsByTagName(child);
			NodeList otherchildren = otherdoc.getElementsByTagName(child);
			
			int myfirstborn = XMLPrinter.branchCounter(mychildren, xdoc.getDocumentElement());
			int otherfirstborn = XMLPrinter.branchCounter(otherchildren, otherdoc.getDocumentElement());
			
			if(myfirstborn != otherfirstborn) {
				output.display("The " + child + " branch has " + mychildren.getLength() + " children" );
			}
		}
		output.display("\n");
		output.display(dif);
		//go through the branches and print out branches with a different number of children between each file
		for(int i = 0; i < otherbranches.size(); i++) {
			String child = otherbranches.get(i);
			NodeList mychildren = this.xdoc.getElementsByTagName(child);
			NodeList otherchildren = otherdoc.getElementsByTagName(child);
			
			int myfirstborn = XMLPrinter.branchCounter(mychildren, xdoc.getDocumentElement());
			int otherfirstborn = XMLPrinter.branchCounter(otherchildren, otherdoc.getDocumentElement());
			
			if(myfirstborn != otherfirstborn) {
				output.display("The " + child + " branch has " + otherchildren.getLength() + " children" );
			}
		}
		output.display("\n");
		output.display(closer);
		
	}
	
	
	/**
	 * printDifferentChildren <br><br>
	 * 
	 * Prints to console two different children and their children
	 * 
	 * @param mychildren - (NodeList) A NodeList of children from the reference Document
	 * @param otherchildren - (NodeList) A NodeList of children from the Document being checked
	 * @param othertop - (String) The root node of the checked Document
	 * 
	 * @return void
	 */
	public void printDifferentChildren(Node parent, Node xparent, Document otherdoc) {
		ArrayList<Node> mychildren = XMLHelper.getElementChildren(parent);
		ArrayList<Node> otherchildren = XMLHelper.getElementChildren(xparent);
		ArrayList<Node> nlist = new ArrayList<Node>();
		ArrayList<Node> tlist = new ArrayList<Node>();
		ArrayList<String> acc = new ArrayList<String>();
		String myns = "";
		String otherns = "";
		Node myparent;
		Node otherparent;

		//check if mychildren is empty
		if (!mychildren.isEmpty()) {//if it is not empty, then we look up its family line to its first gen parent
			myparent = XMLHelper.parentFinder(mychildren.get(0), xdoc);
		}
		else {//if it is empty, parent must be used to find the first generation family
			myparent = XMLHelper.parentFinder(parent, xdoc);
		}
		
		//check if otherchildren is empty
		if (!otherchildren.isEmpty()) {//if it is not empty, then we look up its family line to its first gen parent
			otherparent = XMLHelper.parentFinder(otherchildren.get(0), otherdoc);
		}
		else {//if it is empty, xparent must be used to find the first generation family
			otherparent = XMLHelper.parentFinder(xparent, otherdoc);
		}

		myns = myparent.getBaseURI();
		otherns = otherparent.getBaseURI();

		nlist.add(myparent);
		tlist.add(otherparent);

		printHeader(myns,otherns); //print the differences header
		output.display("!Difference! = Inequal Tree Elements");
		output.display("\n");
		this.printSummary(nlist, acc, 0, "");
		output.display("\n");
		output.display(dif);
		this.printSummary(tlist, acc, 0, "");
		output.display("\n");
		output.display(closer);

	}
	
	/**
	 * printHeader <br><br>
	 * 
	 * Prints the "header" for file differences
	 * 
	 * @param myns - (String) The namespace of the reference file
	 * @param otherns - (String) The namespace of the file being checked
	 * 
	 * @return void
	 */
	private void printHeader(String myns, String otherns) {
		output.display(banner);
		output.display(in);
		output.display(myns);
		output.display(vs);
		output.display(otherns);
		output.display(divider);
	}
}