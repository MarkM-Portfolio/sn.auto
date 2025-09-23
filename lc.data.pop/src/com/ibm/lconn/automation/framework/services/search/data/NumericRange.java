package com.ibm.lconn.automation.framework.services.search.data;


import java.io.Serializable;

public class NumericRange implements Serializable{

	private static final long serialVersionUID = 1L;
	private Number max;
	private Number min;
	private boolean maxInclusive;
	private boolean minInlusive;
	
	public NumericRange(Number min, Number max, 
			boolean minInlusive, boolean maxInclusive) {
		this.min = min;
		this.max = max;
		this.maxInclusive = maxInclusive;
		this.minInlusive = minInlusive;
	}

	public Number getMin(){
		return this.min;
	}
	
	public Number getMax(){
		return this.max;
	}
	
	public boolean includesMax(){
		return this.maxInclusive;
	}
	
	public boolean includesMin(){
		return this.minInlusive;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if (min!=null)
			sb.append("{\"g\":").append("\"").append(min).append("\"").append(", ");
		if (max!=null)
			sb.append("\"l\":").append("\"").append(max).append("\"}").append(", ");
		sb.append("{\"ge\":").append("\"").append(minInlusive).append("\"").append(", ");
		sb.append("\"le\":").append("\"").append(maxInclusive).append("\"}").append(", ");
		sb.setLength(sb.length() - 2);
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((max == null) ? 0 : max.hashCode());
		result = prime * result + (maxInclusive ? 1231 : 1237);
		result = prime * result + ((min == null) ? 0 : min.hashCode());
		result = prime * result + (minInlusive ? 1231 : 1237);
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
		NumericRange other = (NumericRange) obj;
		if (max == null) {
			if (other.max != null)
				return false;
		} else if (!max.equals(other.max))
			return false;
		if (maxInclusive != other.maxInclusive)
			return false;
		if (min == null) {
			if (other.min != null)
				return false;
		} else if (!min.equals(other.min))
			return false;
		if (minInlusive != other.minInlusive)
			return false;
		return true;
	}	
}

