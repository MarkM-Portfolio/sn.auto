package com.ibm.lconn.automation.framework.services.search.data;


public class RangeConstraint{
	
	private static final long serialVersionUID = -3378716686631063383L;
	private String fieldId;
	private NumericRange[] numericValues;
	private StringRange[] stringValues;
	
	public RangeConstraint(String fieldId, NumericRange[] numericValues, StringRange[] stringValues){
		this.fieldId = fieldId;
		this.numericValues = numericValues;
		this.stringValues = stringValues;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{\"type\":").append("\"range\", ");
		sb.append("\"id\":").append("\"").append(fieldId).append("\"").append(", \"values\": ");
		if (numericValues!=null){
			for (NumericRange val : numericValues) {
				sb.append(val).append(", ");
			}
		}
		if (stringValues!=null){
			for (StringRange val : stringValues) {
				sb.append(val).append(", ");
			}
		}
		sb.setLength(sb.length() - 2);
		sb.append("}");
		return sb.toString();
	}

}

