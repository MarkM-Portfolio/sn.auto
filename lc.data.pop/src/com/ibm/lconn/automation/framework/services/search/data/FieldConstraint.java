package com.ibm.lconn.automation.framework.services.search.data;


import java.util.Arrays;

public class FieldConstraint{

	private String fieldId;
	private String[] values;
	private boolean exactMatch = true; 
	
	public FieldConstraint(String fieldId, String[] values, boolean exactMatch) {
		this.fieldId = fieldId;
		this.values = values;
		this.exactMatch = exactMatch;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{\"type\":").append("\"field\", ");
		sb.append("\"id\":").append("\"").append(fieldId).append("\"").append(", \"values\": ").append("[");
		for (String val : values) {
			sb.append("\"").append(val).append("\"").append(", ");
		}
		sb.setLength(sb.length() - 2);
		sb.append("], \"exactMatch\":").append(exactMatch);
		sb.append("}");
		return sb.toString();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldConstraint other = (FieldConstraint) obj;
		if (exactMatch != other.exactMatch)
			return false;
		if (fieldId == null) {
			if (other.fieldId != null)
				return false;
		} else if (!fieldId.equals(other.fieldId))
			return false;
		if (!Arrays.deepEquals(values, other.values))
			return false;
		return true;
	}
}

