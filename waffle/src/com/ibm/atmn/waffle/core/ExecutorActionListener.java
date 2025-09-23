/**
 * 
 */
package com.ibm.atmn.waffle.core;

/**
 * 
 * Implementations of this interface can be set for all the Test Methods in a Test (using {@link TestManager#addTestActionListener(ExecutorActionListener)}
 * or for the life of a browser (removed on quit) using {@link Executor#addBrowserLifeActionListener(ExecutorActionListener)}.
 * 
 * Events are less refined than the range available, so an event may be notified in more than one action method of the {@link Executor}
 * or {@link Element}.
 * <p />
 * Note that in some scenarios that the Executor or Element may not be a valid to be used. This is as normal in the case of beofore, or 
 * possibly as a result of the event for after events.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 *
 */
public interface ExecutorActionListener {

	public enum ElementEvent {
		
		CLICK,
		CLEAR,
		TYPE,
		TYPE_FILE_PATH,
		HOVER,
		SELECT_OPTION,
		LOCATION,
		GET_INNER_TEXT,
		GET_ELEMENT_SIZE,
		GET_ELEMENT_LOCATION,
		GET_ELEMENT_ATTRIBUTE,
		IS_VISIBLE_TEST,
		IS_ENABLED_TEST,
		IS_DISPLAYED_TEST;
		
	}
	
	public enum ExecutorEvent {
		
		TYPE_NATIVE,
		LOCATION,
		SWITCH_FRAME,
		SWITCH_WINDOW,
		CLOSE_WINDOW,
		SWITCH_TO_ALERT,
		EXECUTE_SCRIPT,
		NAVIGATE,
		GET_MARKUP_TEXT;
		
	}
	
	void beforeElementEvent(Executor exec, Element elem, ElementEvent event);
	
	void afterElementEvent(Executor exec, Element elem, ElementEvent event);
	
	void beforeExecutorEvent(Executor exec, ExecutorEvent event);
	
	void afterExecutorEvent(Executor exec, ExecutorEvent event);
	
	void onExecutorLoad(Executor exec);
	
	void beforeExecutorQuit(Executor exec);
}
