package com.ibm.conn.auto.util.webeditors.fvt.utils;

import com.ibm.atmn.waffle.core.Element;

class TypeInElement implements BrowserAction { 
	
	private CharSequence text;
	
	public TypeInElement(CharSequence text) {	
		super(); 
		this.text = text; 
	}
	
	@Override
	public void performOn(Element element) { 
		element.type(text); 
	}
} 
