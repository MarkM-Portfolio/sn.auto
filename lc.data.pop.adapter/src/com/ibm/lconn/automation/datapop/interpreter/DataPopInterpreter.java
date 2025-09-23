package com.ibm.lconn.automation.datapop.interpreter;

import com.ibm.lconn.automation.datapop.ComponentService;
import com.ibm.lconn.automation.datapop.ServiceFactory;
import com.ibm.lconn.automation.datapop.DataPopAdapterException;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class DataPopInterpreter
{

    public DataPopInterpreter()
    {
    }

    public static void interpret(Document doc) throws DataPopAdapterException
    {
    	System.out.println("DataPopInterpreter: Entering interpret(doc)...");
        NodeList nodes = doc.getDocumentElement().getChildNodes();
        for(int i = 0; i < nodes.getLength(); i++)
        {
        	System.out.println("DataPopInterpreter: interpret(doc): Node: " + nodes.item(i).getNodeName());
        	if(nodes.item(i).getNodeType() == 1)
            {
            	System.out.println("DataPopInterpreter: interpret(doc): About to call interpret on element: " + nodes.item(i).getNodeName());            
                interpret((Element)nodes.item(i));
            }
        }
    	
        System.out.println("DataPopInterpreter: Leaving interpret(doc)...");
    }

    private static void interpret(Element e) throws DataPopAdapterException
    {
    	System.out.println("DataPopInterpreter: Entering interpret(elem)...");
    	if(e.getNodeName().equalsIgnoreCase("action"))
        {
            ServiceFactory factory = new ServiceFactory(e.getAttribute("url"), e.getAttribute("uname"), e.getAttribute("password"));
            NodeList nodes = e.getChildNodes();
            for(int i = 0; i < nodes.getLength(); i++)
            {
                System.out.println("DataPopInterpreter: interpret(elem): Node: " + nodes.item(i).getNodeName());
            	if(nodes.item(i).getNodeType() == 1)
                {
                	System.out.println("DataPopInterpreter: interpret(elem): About to call interpret on element: " + nodes.item(i).getNodeName());
                    interpret(factory, (Element)nodes.item(i));
                }
            }
        } else
        if(e.getNodeName().equalsIgnoreCase("loop"))
            loop(null, null, e);
    	
    	System.out.println("DataPopInterpreter: Leaving interpret(elem)...");
    }

    private static void interpret(ServiceFactory factory, Element e) throws DataPopAdapterException
    {
    	System.out.println("DataPopInterpreter: Entering interpret(factory, elem), elem: " + e.getNodeName() + "factory: " + factory.toString());
        if(e.getNodeName().equalsIgnoreCase("loop"))
        {
            loop(factory, null, e);
        } else
        {
            System.out.println("DataPopInterpreter: interpret(factory, elem): Before factory.create(elem).");
            ComponentService parent = factory.create(e);
            System.out.println("DataPopInterpreter: interpret(factory, elem): After factory.create(elem): parent: " + parent.toString());
            NodeList nodes = e.getChildNodes();
            for(int i = 0; i < nodes.getLength(); i++)
            {
            	System.out.println("DataPopInterpreter: interpret(factory, elem): elem: "+ nodes.item(i).getNodeName());
                if(nodes.item(i).getNodeType() == 1)
                {
                	System.out.println("DataPopInterpreter: interpret(factory, elem): About to call interpret(factory, parent, elem) on element: " + nodes.item(i).getNodeName());
                    interpret(factory, parent, (Element)nodes.item(i));
                }
            }
        }
    	
        System.out.println("DataPopInterpreter: Leaving interpret(factory, elem), elem: " + e.getNodeName() + "factory: " + factory.toString());
   }

    private static void interpret(ServiceFactory factory, ComponentService parent, Element e) throws DataPopAdapterException
    {
        System.out.println("DataPopInterpreter: Entering interpret(factory, parent, elem): Element: " + e.getNodeName());
    	if(e.getNodeName().equalsIgnoreCase("action"))
        {
            ServiceFactory newFactory = new ServiceFactory(e);
            NodeList nodes = e.getChildNodes();
            for(int i = 0; i < nodes.getLength(); i++)
            {
            	System.out.println("DataPopInterpreter: interpret(factory, parent, elem): Element: " + nodes.item(i).getNodeName());
                if(nodes.item(i).getNodeType() == 1)
                {
                	System.out.println("DataPopInterpreter: interpret(factory, parent, elem): About to call interpret(parent, elem) on: " + nodes.item(i).getNodeName());
                    interpret(newFactory, parent, (Element)nodes.item(i));
                }
            }
        } else
        if(!e.getNodeName().equalsIgnoreCase("member"))
        {
            if(e.getNodeName().equalsIgnoreCase("loop"))
            {
                loop(factory, parent, e);
            } else
            {
                System.out.println("DataPopInterpreter: interpret(factory, parent, elem): About to create child for element: " + e.getNodeName());
                ComponentService child = factory.create(e);
                child.attach(parent);
                NodeList nodes = e.getChildNodes();
                for(int i = 0; i < nodes.getLength(); i++)
                {
                	System.out.println("DataPopInterpreter: interpret(factory, parent, elem): Child element: " + nodes.item(i).getNodeName());
                	if(nodes.item(i).getNodeType() == 1)
                	{
                		System.out.println("DataPopInterpreter: interpret(factory, parent, elem): About to call interpret() for child element: " + nodes.item(i).getNodeName());
                        interpret(factory, child, (Element)nodes.item(i));
                   }
                }
            }
        }
    	
    	System.out.println("DataPopInterpreter: Leaving interpret(factory, parent, elem): Element: " + e.getNodeName());
    }

    public static void loop(ServiceFactory factory, ComponentService parent, Element e) throws DataPopAdapterException
    {
        int loopInc;
        if(e.hasAttribute("inc") && Integer.parseInt(e.getAttribute("inc")) > 0)
            loopInc = Integer.parseInt(e.getAttribute("inc"));
        else
            loopInc = 1;
        int offset;
        if(e.hasAttribute("start") && Integer.parseInt(e.getAttribute("start")) >= 0)
            offset = Integer.parseInt(e.getAttribute("start"));
        else
            offset = 0;
        for(int j = 0; j < Integer.parseInt(e.getAttribute("iteration")); j++)
        {
            NodeList nodes = e.getChildNodes();
            for(int i = 0; i < nodes.getLength(); i++)
                if(nodes.item(i).getNodeType() == 1)
                {
                    if(((Element)nodes.item(i)).hasAttribute("inc"))
                    {
                        String list[] = ((Element)nodes.item(i)).getAttribute("inc").split("\\s");
                        for(int k = 0; k < list.length; k++)
                        {
                            String temp = ((Element)nodes.item(i)).getAttribute(list[k]);
                            if(j > 0)
                                temp = temp.substring(0, temp.length() - String.valueOf((j - 1) * loopInc + offset).length());
                            if(j == 0 && temp.substring(temp.length() - 1, temp.length()).equalsIgnoreCase(String.valueOf(Integer.parseInt(e.getAttribute("iteration")) * loopInc - 1)))
                                temp = temp.substring(0, temp.length() - 1);
                            ((Element)nodes.item(i)).setAttribute(list[k], (new StringBuilder(String.valueOf(temp))).append(j * loopInc + offset).toString());
                        }

                    }
                    if(parent == null)
                    {
                        if(factory == null)
                            interpret((Element)nodes.item(i));
                        else
                            interpret(factory, (Element)nodes.item(i));
                    } else
                    {
                        interpret(factory, parent, (Element)nodes.item(i));
                    }
                }

        }

    }

    public static Document parse(String s)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch(ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        Document doc = null;
        try
        {
            doc = builder.parse(new File(s));
        }
        catch(SAXException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return doc;
    }

    public static Document parseString(String s)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch(ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        Document doc = null;
        try
        {
            doc = builder.parse(new ByteArrayInputStream(s.getBytes()));
        }
        catch(SAXException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return doc;
    }
}
