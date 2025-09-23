package com.ibm.conn.auto.util;

public enum JavascriptEvent {
	MESSAGECLOSED("com/ibm/oneui/message/closed", 15);
	
	private String event;
	private String toggle;
	private int waitSeconds;

	private JavascriptEvent(String event, int waitSeconds) {
		this.event = event;
		this.toggle = "selenium_wait_" + event.replace("/", "_").replace(".", "_");
		this.waitSeconds = waitSeconds;
	}
	
	public String getEvent() { return event; }
	public String getToggle() { return toggle; }
	public int getWaitSeconds() { return waitSeconds; }
	
	public String getSetupScript() {
		return "window.selenium_event_callback = function () { window." + this.toggle + " = true; };" + 
				"dojo.subscribe('" + this.event + "', selenium_event_callback);" + 
				"return true;";
	}
	
	public String hasFiredScript() {
		return "return window." + this.toggle + " === true;";
	}
	
	public String getTearDownStript() {
		return "dojo.subscribe('" + this.event + "', selenium_event_callback).remove();" + 
				"delete window." + this.toggle + ";" +
				"return delete window.selenium_event_callback;";
	}
}
