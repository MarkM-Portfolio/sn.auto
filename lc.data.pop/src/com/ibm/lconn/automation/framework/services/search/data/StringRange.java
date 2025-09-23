package com.ibm.lconn.automation.framework.services.search.data;


import java.io.Serializable;

public class StringRange implements Serializable{

	private static final long serialVersionUID = 1L;
	private String upperTerm;
	private String lowerTerm;
	private boolean includesUpper;
	private boolean includesLower;
	
	public StringRange(String lowerTerm, String upperTerm, 
			boolean includesLower, boolean includesUpper) {
		this.lowerTerm = lowerTerm;
		this.upperTerm = upperTerm;
		this.includesUpper = includesUpper;
		this.includesLower = includesLower;
	}

	public String getLowerTerm(){
		return this.lowerTerm;
	}
	
	public String getUpperTerm(){
		return this.upperTerm;
	}
	
	public boolean includesUpper(){
		return this.includesUpper;
	}
	
	public boolean includesLower(){
		return this.includesLower;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if (lowerTerm!=null)
			sb.append("{\"g\":").append("\"").append(lowerTerm).append("\"").append(", ");
		if (upperTerm!=null)
			sb.append("\"l\":").append("\"").append(upperTerm).append("\"}").append(", ");
		sb.append("{\"ge\":").append("\"").append(includesLower).append("\"").append(", ");
		sb.append("\"le\":").append("\"").append(includesUpper).append("\"}").append(", ");
		sb.setLength(sb.length() - 2);
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (includesLower ? 1231 : 1237);
		result = prime * result + (includesUpper ? 1231 : 1237);
		result = prime * result
				+ ((lowerTerm == null) ? 0 : lowerTerm.hashCode());
		result = prime * result
				+ ((upperTerm == null) ? 0 : upperTerm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringRange other = (StringRange) obj;
		if (includesLower != other.includesLower)
			return false;
		if (includesUpper != other.includesUpper)
			return false;
		if (lowerTerm == null) {
			if (other.lowerTerm != null)
				return false;
		} else if (!lowerTerm.equals(other.lowerTerm))
			return false;
		if (upperTerm == null) {
			if (other.upperTerm != null)
				return false;
		} else if (!upperTerm.equals(other.upperTerm))
			return false;
		return true;
	}	
}

