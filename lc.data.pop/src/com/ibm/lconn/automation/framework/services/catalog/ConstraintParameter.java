package com.ibm.lconn.automation.framework.services.catalog;

public class ConstraintParameter{

	private String constraintType;
	private String constraintId;
	private String[] constraintValues;
	
	public ConstraintParameter(String constraintType, String constraintId, String[] constraintValues) {
		this.constraintType = constraintType;
		this.constraintId = constraintId;
		this.constraintValues = constraintValues;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{\"type\":").append("\"").append(constraintType).append("\"").append(", ");
		sb.append("\"id\":").append("\"").append(constraintId).append("\"").append(", \"values\": ").append("[");
		for (String val : constraintValues) {
			sb.append("\"").append(val).append("\"").append(", ");
		}
		sb.setLength(sb.length() - 2);
		sb.append("]}");
		return sb.toString();
	}

}

