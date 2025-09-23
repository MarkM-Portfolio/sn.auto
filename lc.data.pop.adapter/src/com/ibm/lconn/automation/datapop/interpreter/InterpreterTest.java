package com.ibm.lconn.automation.datapop.interpreter;

import org.w3c.dom.Document;

import com.ibm.lconn.automation.datapop.DataPopAdapterException;

public class InterpreterTest
{

    public InterpreterTest()
    {
    }

    public void testInterpreter() throws DataPopAdapterException
    {
        Document d = DataPopInterpreter.parse("C:\\Users\\IBM_ADMIN\\bbqscript.xml");
        System.out.println(d.getDocumentURI());
        DataPopInterpreter.interpret(d);
    }

    public void testInterpreterWiki() throws DataPopAdapterException
    {
        Document d = DataPopInterpreter.parse("C:\\Users\\IBM_ADMIN\\wiki.xml");
        System.out.println(d.getDocumentURI());
        DataPopInterpreter.interpret(d);
    }

    public void testInterpreterForum() throws DataPopAdapterException
    {
        Document d = DataPopInterpreter.parse("C:\\Users\\IBM_ADMIN\\forum.xml");
        System.out.println(d.getDocumentURI());
        DataPopInterpreter.interpret(d);
    }

    public void testInterpreterBlockly() throws DataPopAdapterException
    {
        Document d = DataPopInterpreter.parse("C:\\Users\\IBM_ADMIN\\blocklybbq.xml");
        System.out.println(d.getDocumentURI());
        DataPopInterpreter.interpret(d);
    }

    public void testInterpreterString() throws DataPopAdapterException
    {
        Document d = DataPopInterpreter.parseString("<root><action  url=\"https://lcdatapop.swg.usma.ibm.com\" uname=\"fadams\" password=\"passw0rd\"><bookmark title=\"Yahoo\" content=\"a link to yahoo's homepage.  Don't confuse with yoohoo.\" tagsString=\"link\" linkHref=\"http://www.yahoo.com\"></bookmark></action></root>");
        System.out.println(d.getDocumentURI());
        DataPopInterpreter.interpret(d);
    }

    public void testInterpreterBlocklyLC30LINUX4() throws DataPopAdapterException
    {
        Document d = DataPopInterpreter.parse("C:\\Users\\IBM_ADMIN\\linux4.xml");
        System.out.println(d.getDocumentURI());
        DataPopInterpreter.interpret(d);
    }
}
