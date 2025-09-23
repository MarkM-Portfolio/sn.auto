package com.ibm.conn.auto.util;

import com.ibm.atmn.waffle.base.BaseExecutionListener;
import com.ibm.atmn.waffle.base.BaseTestListener;
import com.ibm.conn.auto.TestExample;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Development class for running tests bveing developed
 * Programatically run testng tests, dynamically creates testng config xml file
 */
public class RunTests {


    public static void main(String[] args) {
        HashMap<String,String> testParams = new HashMap<String, String>();
        testParams.put("browser_start_command", "gc_31_Windows");
        testParams.put("browser_url","https://lc45linux1.swg.usma.ibm.com/");

        RunTests tests = new RunTests();
        tests.setParams(testParams);

        XmlSuite suiteDev = tests.addTestSuite("Development");
        tests.addTestClass(suiteDev,TestExample.class,"Template");

//        XmlSuite suiteFvt = tests.addTestSuite("Suite 2");
//        tests.addTestClass(suiteDev,Template.class,"Template1");
//        tests.addTestClass(suiteFvt,Template.class,"Template");

        tests.run();
    }


    TestListenerAdapter tla = new TestListenerAdapter();
    TestNG testng = new TestNG();
    List<XmlSuite> testSuites = new ArrayList<XmlSuite>();
    HashMap<String,String> testParams = new HashMap<String, String>();

    public RunTests() {
        testng.addListener(tla);
        testng.addListener(new BaseTestListener());
        testng.addListener(new BaseExecutionListener());
        testng.setParallel("classes");
        testng.setPreserveOrder(true);
        testng.setXmlSuites(testSuites);
        this.setParams();
    }

    private void setParams(){
        testParams.put("browser_start_command", "gc_31_Windows");
        testParams.put("browser_url","https://lc45linux1.swg.usma.ibm.com/");
        testParams.put("product_name","onprem");

    }

    void setParams(HashMap<String,String> testParams){
        this.testParams.putAll(testParams);
    }

    XmlSuite addTestSuite(String testSuiteName) {
        XmlSuite testSuite = new XmlSuite();
        testSuite.setName(testSuiteName);
        testSuite.setParameters(testParams);
        testSuites.add(testSuite);
        return testSuite;
    }

    void addTestClass(XmlSuite testSuite, Class testClass, String testName){
        XmlTest test = new XmlTest(testSuite);
        test.setName(testName);
        List<XmlClass> testClasses= new ArrayList<XmlClass>();
        testClasses.add(new XmlClass(testClass));
        test.setXmlClasses(testClasses);
    }


    void run(){
        testng.run();
    }

}
