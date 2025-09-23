package com.ibm.lconn.automation.framework.services.search.data;


import java.util.Arrays;

public class CategoryConstraint{
	
	private String[][] values;
	
	public CategoryConstraint(String[][] values) {
		this.values = values;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{\"type\":").append("\"category\",").append("\"values\":").append("[").append("\"");
		for (String[] vals : values) {
			for (String val : vals) {
				sb.append(val).append("/");
			}
			sb.setLength(sb.length() - 1);
			sb.append("\"");
			sb.append(",");
			sb.append("\"");
		}
		sb.setLength(sb.length() - 2);
		sb.append("]}");
		return sb.toString();
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(values);
		return result;
	}


	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryConstraint other = (CategoryConstraint) obj;
		if (!Arrays.deepEquals(values, other.values))
			return false;
		return true;
	}	
}

