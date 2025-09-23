package resources;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * This class is a helper class, providing certain 
 * methods to classes such as XMLAnalyzer and
 * XMLPrinter to help process an XML Document. 
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-05-22
 */
public class XMLHelper {

	/**
	 * constructor
	 */
	public XMLHelper() {}
	
	/**
	 * buildBranchList <br><br>
	 * 
	 * Returns the list of direct children's element names to the root element
	 * while removing doubles
	 * 
	 * @param doc - (Document) The document whose branches will be 
	 * 				identified and built into the resulting list
	 * 
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> buildBranchList(Document doc) {
		Node node = doc.getDocumentElement();
		ArrayList<Node> nlist = XMLHelper.getElementChildren(node);
		ArrayList<String> slist = new ArrayList<String>();
			
		for(int i = 0; i < nlist.size(); i++) {
			Node n = nlist.get(i);
			String nname = n.getNodeName();
			
			//boolean statements
			boolean contained = (slist.contains(nname)); 
			boolean top_child = n.getParentNode().equals(node);
			
			if (top_child && !contained) {
				slist.add(nlist.get(i).getNodeName()); //add the node name
			}
		}
		return slist;
	}
	
	/**
	 * getElementChildren <br><br>
	 * 
	 * Returns an ArrayList<Node> of the given Node's
	 * children that are elements
	 * 
	 * @param node - (Node) Node whose element children are to be returned
	 * 
	 * @return ArrayList<Node>
	 */
	public static ArrayList<Node> getElementChildren(Node node) {
		ArrayList<Node> alist = new ArrayList<Node>();
		NodeList nlist = node.getChildNodes();
		
		for(int i = 0; i < nlist.getLength(); i++) {
			Node n = nlist.item(i);
			boolean type_node = (n.getNodeType() == Node.ELEMENT_NODE);
			
			if (type_node) {
				alist.add(n);
			}
		}
		return alist;
	}
	
	/**
	 * childlessNodeCounter <br><br>
	 * 
	 * Returns how many childless nodes of name n appear
	 * in the ArrayList<Node> nlist. This is used for 
	 * calculating how many childless nodes in a file appear
	 * so that when an XML file is printed, a large group
	 * of childless nodes can be summarized.
	 * 
	 * @param n - (Node) The node whose name will be searched in the nlist
	 * @param nlist - (ArrayList<Node>) The list of nodes that will be searched for nodes of name n
	 */
	public static int childlessNodeCounter(Node n, ArrayList<Node> nlist) {
		int count = 0;
		
		for(int i = 0; i < nlist.size(); i++){
			Node d = nlist.get(i);
			String dname = d.getNodeName();
			String nname = n.getNodeName();
			
			boolean same_name = dname == nname;
			boolean childless = XMLHelper.getElementChildren(d).size() == 0;
			
			if (same_name && childless) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * parentFinder <br><br>
	 * 
	 * Returns the first generation parent (not the Document Element)
	 * of a list of children. 
	 * 
	 * NOTE: This will break if given the root node as a child
	 * 
	 * @param child - (Node) The child being identified as first generation or other
	 * @param doc - (Document) The document
	 * 
	 * @return Node
	 */
	public static Node parentFinder(Node child, Document doc) {
		Node parent = child.getParentNode();
		Node root = doc.getDocumentElement();
		boolean origin = root.equals(parent);

		if (!origin) {
			//System.out.println(child.getNodeName() + " is child of " + parent.getNodeName());
			return XMLHelper.parentFinder(parent, doc);
		}
		else { //child is first generation
			//System.out.println(child.getNodeName() + " is first gen");
			return child;
		}
	}
	
	/**
	 * getFirstBorns <br><br>
	 * 
	 * Returns an ArrayList of first generation children in nlist
	 * 
	 * @param nlist - (NodeList) The list of child nodes
	 * @param xdoc - (Document) The document being looked in
	 * 
	 * @return ArrayList<Node>
	 */
	public static ArrayList<Node> getFirstBorns(NodeList nlist, Document xdoc) {
		ArrayList<Node> alist = new ArrayList<Node>();
		Node top = xdoc.getDocumentElement();
		
		for(int i = 0; i < nlist.getLength(); i++) {
			Node n = nlist.item(i);
			
			if(!n.equals(top)) { //if the element is not the root
				boolean first_born = n.equals(XMLHelper.parentFinder(n, xdoc)); //will break if given a root node, see if its first born
				
				if (first_born) {//if first born
					alist.add(n);
				}
			}
		}
		
		return alist;
	}
	
	/**
	 * getNodeInfo <br><br>
	 * 
	 * Return a string that represents the all
	 * the attributes or text element children of the node
	 * 
	 * @param node - (Node) The node to get info from
	 * @param type - (String) What information to pull from the node
	 * 
	 * @return String
	 */
	public static String getNodeInfo(Node node, String type) {
		NamedNodeMap alist = node.getAttributes();
		NodeList clist = node.getChildNodes();
		String result = "";
		
		if (type.equalsIgnoreCase("attribute")) {//Add the attributes to the result string
			for(int i = 0; i < alist.getLength(); i++) {
				Node attr = alist.item(i);
				
				//make a readable string of the node's attributes
				result = result.concat(attr.toString() + "\n");
			}
		}
		
		if (type.equalsIgnoreCase("text")) {//Add the text nodes to the result string
			for(int i = 0; i < clist.getLength(); i++) {
				Node child = clist.item(i);
				boolean is_text = child.getNodeType() == Node.TEXT_NODE;
				
				//if the element being observed is a text element
				if (is_text) {
					result = result.concat(child.getNodeValue() + "\n");
				}
			}	
		}
		
		return result;
	}
	
	/**
	 * hasAttributeDifferneces<br><br>
	 * 
	 * Does the comp_node have different 
	 * attribute-value pairs than node
	 * 
	 * @param node - (Node) The node whose attributes we know
	 * @param comp_node - (Node) The node whose attributes are going to be compared to the other's
	 * 
	 * @return boolean
	 */
	public static boolean hasAttributeDifferences(Node node, Node comp_node) {
		NamedNodeMap attrs = node.getAttributes(); //These are the attrs we know for sure
		NamedNodeMap comp_attrs = comp_node.getAttributes(); // These are the attributes we are comparing to attrs
		boolean answer = false; //assume true, only if we know each attr is equal can it be false
		
		//First check the size of the attribute map for each node
		if (attrs.getLength() != comp_attrs.getLength()) { //if they are different sizes, then there are definitely differences
			answer = true;
		}
		
		else { //If they are the same size, 
			//Check them attribute for attribute to see if they are the same
			for(int i=0; i < attrs.getLength(); i++) {
				Node attr = attrs.item(i); //the attribute we want to find in the comp_node
				String aname = attr.getNodeName();
				
				//check if the node exists in the other attribute set
				if (comp_attrs.getNamedItem(aname) == null) { //if this attribute is not in the comp_node
					answer = true;
					break;
				}
				
				else { //if this attribute is in the comp_node
					String aval = attrs.getNamedItem(aname).toString();
					String cval = comp_attrs.getNamedItem(aname).toString();
					if(!aval.equals(cval)) {//if the attributes are not of the same value in the nodes
						answer = true;
						break;
					}
					else { //if they are the same value, then they match
						answer = false;
					}
				}
			}
			/* 
			//Possible alternative to identifying differences
			NamedNodeMap ats = node.getAttributes();
			if (countAttributeMatches(node, comp_node) != ats.getLength()) { //if the matches counted are not 1to1 with the node attrs, then there must be diffs
				answer = true;
			}*/
		}
		
		return answer; //is there a difference between the node's attributes?
	}
	
	/**
	 * findClosestMatch <br><br>
	 * 
	 * Find the best possible match for the Node child.
	 * This method should be called if the child does not 
	 * have a Unique ID, so a match can be identified 
	 * strictly by order or more intelligently by 
	 * comparing next generation lists to find the closest
	 * match between a possible node and child. <br><br>
	 * 
	 * Match Priority:<br>
	 * 1.Most Positive Child Matches:<br>
	 * 
	 * 2.Fewest Negative Child Matches:<br>
	 * 
	 * 3.Most Positive Attribute Matches<br>
	 * 
	 * NOTE:<br>
	 * The way this method is constructed means that if 
	 * when a child is looking for a match and strict is false,
	 * then it will compare its children with the children of 
	 * each like-named member of comp_children. If all the members
	 * of comp_children have the same name as child, and all
	 * have the same name number of matches, then the match 
	 * chosen will be the first node observed. Thus, 
	 * if the members of comp_children are identical in their
	 * names and children (size and names of children that is),
	 * then the method effectively becomes strict. 
	 * 
	 * @param child - (Node) The child we want to find a match of to compare it to
	 * @param comp_children - (ArrayLisT<Node>) The list we want to find a match for child in
	 * @param strict - (boolean) True if we want to match strictly by order, or false if we want to find a generation match
	 * 
	 * @return Node
	 */
	public static Node findClosestMatch(Node child, ArrayList<Node> comp_children, boolean strict) {
		Node rnode = null; //the node we will return
		int highest_children = 0;
		int highest_attrs = 0;
		int fewest_extras = -1;
		
		//if we want to guess as accurately as possible, we want to be more clever and not compare strictly by element position
		if(!strict) {
			//find the closest match in the comp_children
			for(Node c: comp_children) {

				//if there is a match between child's name and another child name
				if(child.getNodeName() == c.getNodeName()) {
					ArrayList<Node> next_children = XMLHelper.getElementChildren(child);
					ArrayList<Node> next_comp = XMLHelper.getElementChildren(c);

					if(rnode == null) { //the first name match should be the initial suspect match
						rnode = c;
					}

					//count the matches of children and attributes between child and the comp_child being observed
					int new_children = countChildMatches(next_children, next_comp);
					int new_attrs = countAttributeMatches(child, c);
					int new_extras = countExtras(next_children, next_comp);

					if (fewest_extras == -1) {//if this is the first time through, set the fewest extras
						fewest_extras = new_extras;
					}

					if (new_children > highest_children) { //if there are more matches among children than we've found so far, set the rnode to comp_child c
						highest_children = new_children;
						rnode = c;
					}
					else if(new_children == highest_children) { //if the child matches are the same...
						if(new_extras < fewest_extras) {//but comp_child c has fewer extra children than the current rnode, set the rnode to comp_child c
							fewest_extras = new_extras;
							rnode = c;
						}
						else if (new_extras == fewest_extras) {//if the extra counts are the same...
							if(new_attrs > highest_attrs) {//but the comp_child c has more matching attributes than the current rnode, set the rnode to comp_child c
								highest_attrs = new_attrs;
								rnode = c;
							}
						}
					}
				}
			}
		}
		//if we want to match as strictly as possible (which is to say strict ordering of the element)
		else {
			for(Node c: comp_children) {
				if(child.getNodeName() == c.getNodeName()) {
					rnode = c;
					break;
				}	
			}	
		}
		return rnode;
	}

	/**
	 * countChildMatches <br><br>
	 * 
	 * Counts the number of nodes with the same name 
	 * from both lists. When counting the nodes of the 
	 * same name, it is important that the final count
	 * returned should be 1to1 matches, which is to say
	 * that if children has 2 nodes of name "name" and 
	 * comp_children has 5 nodes of name "name", then 
	 * the number of matches returned should me 2, because
	 * that is the maximum number of 1to1 matches
	 * for nodes named "name" between children and comp_children. 
	 * 
	 * @param children - (ArrayList<Node) The list of a node's children
	 * @param comp_children - (ArrayList<Node>) The list of nodes we want to find matches in
	 * 
	 * @return int
	 */
	public static int countChildMatches(ArrayList<Node> children, ArrayList<Node> comp_children) {
		int count = 0;
		
		//for each child 
		for(Node c: children) {
			String cname = c.getNodeName();
			
			//if there is a node in the other children with the same name
			for (Iterator<Node> iter = comp_children.iterator(); iter.hasNext();) {
				Node d = iter.next();
				String dname = d.getNodeName();
				
				//add 1 to the count of matches
				if(cname == dname) {
					count++;
					iter.remove(); //prevent future match counts for children for one node named d
					break; //break to avoid adding to count for multiple nodes of the same name.
					//in other words, it breaks as soon as it finds a match to represent a 1to1 match count.
				}
			}
		}
		return count;
	}
	
	/**
	 * 
	 * @param children
	 * @param comp_children
	 * @return
	 */
	public static int countAttributeMatches(Node node, Node comp) {
		int count = 0;
		NamedNodeMap attrs = node.getAttributes();
		NamedNodeMap comp_attrs = comp.getAttributes();
		
		//for each attribute of child
		for(int i=0; i < attrs.getLength(); i++) {
			Node attr = attrs.item(i); //the attribute we want to find in the comp_node
			String aname = attr.getNodeName();
			
			//check if the node exists in the other attribute set
			if (comp_attrs.getNamedItem(aname) != null) { 
				String aval = attrs.getNamedItem(aname).toString();
				String cval = comp_attrs.getNamedItem(aname).toString();
				
				//if the attributes are the same value
				if(aval.equals(cval)) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * countExtras  <br><br>
	 * 
	 * Counts how many children appear in the comp_children
	 * that aren't in the children
	 * 
	 * @param children - (ArrayList<Node>) A series of nodes
	 * @param comp_children - (ArrayList<Node>) A series of nodes with potential extras
	 * 
	 * @return int
	 */
	public static int countExtras(ArrayList<Node> children, ArrayList<Node> comp_children) {
		int extras = 0;
		
		if (children.isEmpty()) {//if children is empty, then all the comp_children are extra
			extras = comp_children.size();
		}
		else {
			//for each child 
			for(Node c: children) {
				String cname = c.getNodeName();
				
				//if there is a node in the other children not with the same name
				for (Iterator<Node> iter = comp_children.iterator(); iter.hasNext();) {
					Node d = iter.next();
					String dname = d.getNodeName();
					
					//add 1 to the count of extras
					if(cname != dname) {
						extras++;
					}
				}
			}
		}
		return extras;
	}

	/**
	 * convertList <br><br>
	 * 
	 * Takes in an ArrayList<Node> and converts it into
	 * an ArrayList<String> of the node names
	 * 
	 * @param nlist - (ArrayList<Node>) List of node objects whose names will be used in the new list
	 * 
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> convertList(ArrayList<Node> nlist) {
		ArrayList<String> slist = new ArrayList<String>();
		
		for(Node n: nlist) {
			String nname = n.getNodeName();
			
			slist.add(nname);
		}
		return slist;
	}
	
	/**
	 * convertList <br><br>
	 * 
	 * Takes in a NodeList and converts it into
	 * an ArrayList<String> of the node names
	 * 
	 * @param nlist - (NodeList) List of node objects whose names will be used in the new list
	 * 
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> convertList(NodeList nlist){
		ArrayList<String> slist = new ArrayList<String>();
		
		for(int i = 0; i < nlist.getLength(); i++) {
			Node n = nlist.item(i);
			String nname = n.getNodeName();
			
			slist.add(nname);
		}
		return slist;
	}
	
	/**
	 * convertList <br><br>
	 * 
	 * Takes in a NodeList and converts it into
	 * an ArrayList<String> of the node names
	 * 
	 * @param nlist - (NodeList) List of node objects whose names will be used in the new list
	 * 
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> convertListTree(ArrayList<TreeItem> tlist){
		ArrayList<String> slist = new ArrayList<String>();
		
		for(TreeItem n: tlist) {
			String nname = n.getText();
			
			slist.add(nname);
		}
		return slist;
	}
	
	/**
	 * convertListNode <br><br>
	 * 
	 * Moves the elements of a NodeList into an ArrayList<Node> and returns
	 * the new ArrayList<Node>
	 * 
	 * @param nlist - (NodeList) The NodeList to be converted into an ArrayList<Node>
	 * 
	 * @return ArrayList<Node>
	 */
	public static ArrayList<Node> convertListNode(NodeList nlist) {
		ArrayList<Node> list = new ArrayList<Node>();
		
		for(int i = 0; i < nlist.getLength(); i++) {
			Node n = nlist.item(i);
			
			list.add(n);
		}
		return list;
	}
	
	
	/**
	 * removeChild <br><br>
	 * 
	 * Remove the child from the list that contains it 
	 * safely by using an iterator
	 * 
	 * @param child - (Node) The node we want removed
	 * @param children - (ArrayList<Node>) The list we want the node removed from
	 * 
	 * @return void
	 */
	public static void removeChild(Node child, ArrayList<Node> children) {
		for(Iterator<Node> itr = children.iterator(); itr.hasNext();) {
			if (child.equals(itr.next())) {
				itr.remove();
				break;
			}
		}
	}
	
	/**
	 * removeChild <br><br>
	 * 
	 * Remove the child from the list that contains it 
	 * safely by using an iterator (for ArrayList<TreeItem>)
	 * 
	 * @param item - (TreeItem) The TreeItem we want removed from the list
	 * @param tlist - (ArrayList<TreeItem>) The list we want the item removed from
	 * 
	 * @return void
	 */
	public static void removeChild(TreeItem item, ArrayList<TreeItem> tlist) {
		for(Iterator<TreeItem> itr = tlist.iterator(); itr.hasNext();) {
			if (item.equals(itr.next())) {
				itr.remove();
				break;
			}
		}
	}
}