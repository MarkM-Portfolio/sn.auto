/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2013                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.perf.jmeter.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="testResults")
@XmlAccessorType(XmlAccessType.FIELD)
public class TestResults {
 
	//@XmlElement(name="httpSample")
	@XmlElementRefs({
		@XmlElementRef(type=HttpSample.class, name="httpSample"),
		@XmlElementRef(type=Sample.class, name="sample")
	})
    private List<Sample> samplers;

	public List<Sample> getSamplers() {
		return samplers;
	}

	public void setSamplers(List<Sample> samplers) {
		this.samplers = samplers;
	}

}