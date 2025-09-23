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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.ibm.icu.text.MessageFormat;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="sample")
public class Sample {
	
	@XmlAttribute private String by;
	@XmlAttribute private String de;
	@XmlAttribute private String dt;
	@XmlAttribute private int ec;
	@XmlAttribute private String hn;
	@XmlAttribute private String it;
	@XmlAttribute private String lb;
	@XmlAttribute private long lt;
	@XmlAttribute private int na;
	@XmlAttribute private int ng;
	@XmlAttribute private String rc;
	@XmlAttribute private String rm;
	@XmlAttribute private String s;
	@XmlAttribute private long sc;
	@XmlAttribute private long t;
	@XmlAttribute private String tn;
	@XmlAttribute private String ts;
	@XmlAttribute private String varname;
	
	public String getBy() {
		return by;
	}
	public void setBy(String by) {
		this.by = by;
	}
	public String getDe() {
		return de;
	}
	public void setDe(String de) {
		this.de = de;
	}
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	public int getEc() {
		return ec;
	}
	public void setEc(int ec) {
		this.ec = ec;
	}
	public String getHn() {
		return hn;
	}
	public void setHn(String hn) {
		this.hn = hn;
	}
	public String getIt() {
		return it;
	}
	public void setIt(String it) {
		this.it = it;
	}
	public String getLb() {
		return lb;
	}
	public void setLb(String lb) {
		this.lb = lb;
	}
	public long getLt() {
		return lt;
	}
	public void setLt(long lt) {
		this.lt = lt;
	}
	public int getNa() {
		return na;
	}
	public void setNa(int na) {
		this.na = na;
	}
	public int getNg() {
		return ng;
	}
	public void setNg(int ng) {
		this.ng = ng;
	}
	public String getRc() {
		return rc;
	}
	public void setRc(String rc) {
		this.rc = rc;
	}
	public String getRm() {
		return rm;
	}
	public void setRm(String rm) {
		this.rm = rm;
	}
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	public long getSc() {
		return sc;
	}
	public void setSc(long sc) {
		this.sc = sc;
	}
	public long getT() {
		return t;
	}
	public void setT(long t) {
		this.t = t;
	}
	public String getTn() {
		return tn;
	}
	public void setTn(String tn) {
		this.tn = tn;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public String getVarname() {
		return varname;
	}
	public void setVarname(String varname) {
		this.varname = varname;
	}
	
	public boolean isSuccess()
	{
		return s.equalsIgnoreCase("true");
	}
	
	public boolean isFail()
	{
		return !isSuccess();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Sample [");
		if (by != null) {
			builder.append("by=");
			builder.append(by);
			builder.append(", ");
		}
		if (de != null) {
			builder.append("de=");
			builder.append(de);
			builder.append(", ");
		}
		if (dt != null) {
			builder.append("dt=");
			builder.append(dt);
			builder.append(", ");
		}
		builder.append("ec=");
		builder.append(ec);
		builder.append(", ");
		if (hn != null) {
			builder.append("hn=");
			builder.append(hn);
			builder.append(", ");
		}
		if (it != null) {
			builder.append("it=");
			builder.append(it);
			builder.append(", ");
		}
		if (lb != null) {
			builder.append("lb=");
			builder.append(lb);
			builder.append(", ");
		}
		builder.append("lt=");
		builder.append(lt);
		builder.append(", na=");
		builder.append(na);
		builder.append(", ng=");
		builder.append(ng);
		builder.append(", ");
		if (rc != null) {
			builder.append("rc=");
			builder.append(rc);
			builder.append(", ");
		}
		if (rm != null) {
			builder.append("rm=");
			builder.append(rm);
			builder.append(", ");
		}
		if (s != null) {
			builder.append("s=");
			builder.append(s);
			builder.append(", ");
		}
		builder.append("sc=");
		builder.append(sc);
		builder.append(", t=");
		builder.append(t);
		builder.append(", ");
		if (tn != null) {
			builder.append("tn=");
			builder.append(tn);
			builder.append(", ");
		}
		if (ts != null) {
			builder.append("ts=");
			builder.append(ts);
			builder.append(", ");
		}
		if (varname != null) {
			builder.append("varname=");
			builder.append(varname);
		}
		builder.append("]");
		return builder.toString();
	}
	
	public String toCustomizedString() 
	{
		return MessageFormat.format("{0} {1}: {2}", new Object[]{ isSuccess()? "[SUCCESS]":"[FAIL]", lb, toString()});
	}
	
}
