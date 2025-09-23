package resources;

import org.w3c.dom.Node;

/**
 * represents a error in validating an XML file
 * 
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */
@SuppressWarnings("serial")
public class XMLException extends Exception 
{
	Node errorNode;
	String msg;
	
	/**
	 * Constructs a new XMLException
	 * @param errorNode - the node that is causing the exception
	 * @param msg - a description of the problem
	 */
	public XMLException(String msg, Node errorNode) 
	{
		this.errorNode = errorNode;
		this.msg = msg;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getMessage()
	{
		return msg + ": " + errorNode.toString();
	}
}
