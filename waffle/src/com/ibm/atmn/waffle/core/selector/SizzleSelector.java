package com.ibm.atmn.waffle.core.selector;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Executor;
import com.ibm.atmn.waffle.core.JavaScriptLoader;
import com.ibm.atmn.waffle.core.JavaScriptLoader.Script;

/**
 * Selector to be located using the Sizzle JS library. This class also takes responsibility for working around a bug in sizzle where unested parentheses are within the text give to
 * the :contains(text) filter. This workaround will not function under certain scenarios such as if :contains is itself nested within another filter (e.g. :not(:contains(text))).
 * Location depends on the corresponding script file.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 * 
 */
public class SizzleSelector implements JavaScriptSelector {

	private static final Logger log = LoggerFactory.getLogger(SizzleSelector.class);

	private static final String WAFFLE_LOC_VAR = Script.WAFFLE_LOCATION.getHandle();
	private static final String EXTRA_SIZZLE_VAR = Script.EXTRA_SIZZLE.getHandle();

	private static final String CONTAINS_DEFINITION = ":contains(";
	private static final String CONTAINS_ESCAPED_DEFINITION = ":containsEscaped(";
	private static final String[] PARENTHESISED_SELECTOR_DEFINITIONS = { CONTAINS_DEFINITION, CONTAINS_ESCAPED_DEFINITION, ":not(", ":nth(", ":eq(", ":lt(", ":gt(", ":nth-child(",
			":lang(" };
	private String rawSelector;
	private Strategy strategy;
	private String query;
	private String sizzleQuery;
	private ArrayList<Object> args = new ArrayList<Object>();
	private String preContextQuery = null;

	public SizzleSelector(String selector) {

		setStrategy(Strategy.JAVASCRIPT);
		this.rawSelector = selector;
		setQuery(createSizzleQuery(selector));
	}

	private String createSizzleQuery(String selector) {

		this.sizzleQuery = EXTRA_SIZZLE_VAR + "(" + addArg(selector) + ")";
		return this.sizzleQuery;
	}

	private String addArg(Object arg) {

		this.args.add(arg);
		return "arguments[" + String.valueOf(this.args.size() - 1) + "]";
	}

	@Override
	public Strategy getStrategy() {

		return this.strategy;
	}

	@Override
	public String getQuery() {

		return this.query;
	}

	@Override
	public Selector setStrategy(Strategy strategy) {

		this.strategy = strategy;
		return this;
	}

	@Override
	public Selector setQuery(String query) {

		this.query = query;
		return this;
	}

	@Override
	public Selector addToParent() {

		String baseQuery = getQuery();
		setQuery(WAFFLE_LOC_VAR + ".toParentElement(" + baseQuery + ")");
		return this;
	}

	@Override
	public Selector addToParentByTagName(String tagName) {

		String baseQuery = getQuery();
		setQuery(WAFFLE_LOC_VAR + ".toParentElementByTagName(" + baseQuery + ", " + addArg(tagName) + ")");
		return this;
	}

	@Override
	public Object[] getArguments() {

		log.trace("Returning arguments: " + Arrays.toString(this.args.toArray()) + ", for SizzleSlector: " + this.toString());
		return this.args.toArray();
	}

	public String toString() {

		return "{(" + getStrategy() + ")," + rawSelector + "}";
	}

	@Override
	public void afterLocation(Executor exec) {

		if (this.preContextQuery != null) {

			setQuery(this.preContextQuery);
			this.preContextQuery = null;
		}
	}

	@Override
	public String beforeLocation(Executor exec) {

		log.trace("beforeLocation for SizzleSlector: " + this.toString());
		JavaScriptLoader.loadScript(exec, Script.EXTRA_SIZZLE);
		JavaScriptLoader.loadScript(exec, Script.WAFFLE_LOCATION);

		String escapedSelector = escapeForContainsBug(this.rawSelector, exec);
		//log.debug("Escaped selector: " + escapedSelector);

		setQuery(getQuery().replace(this.rawSelector, escapedSelector));

		// the raw query should always be argument[0]

		for (Object o : this.args) {

			String a = o.toString();
			if (a.equalsIgnoreCase(this.rawSelector)) {
				a = a.replace(this.rawSelector, escapedSelector);
				this.args.set(this.args.indexOf(o), a);
			}
		}

		//log.debug("Returning final SizzleSelector query: " + getQuery());
		return getQuery();
	}

	@Override
	public void addContext(Object context) {

		this.preContextQuery = getQuery();
		String startSizzleQuery = this.sizzleQuery;
		this.sizzleQuery = startSizzleQuery.substring(0, startSizzleQuery.length() - 1) + "," + addArg(context) + ")";
		setQuery(this.preContextQuery.replace(startSizzleQuery, this.sizzleQuery));
	}

	private String escapeForContainsBug(String query, Executor exec) {

		String escapedQuery;
		if (containsUnestedParentheses(query)) {
			if (query.contains(CONTAINS_DEFINITION)) {
				escapedQuery = escapeAllContains(query, exec);
			} else {
				log.error("There are unested parentheses in the query: " + query);
				throw new InvalidParameterException("There are unested parentheses in the query: " + query);
			}
		} else {
			escapedQuery = query;
		}
		return escapedQuery;
	}

	/**
	 * This is quite horrific but it can successfully parse and escape for the :contains bug. See {@link SizzleSelector} doc for limitations.
	 * 
	 * @param query
	 * @param exec
	 * @return
	 */
	private String escapeAllContains(String query, Executor exec) {

		StringBuilder processed = new StringBuilder("");
		String remainder = query;

		while (remainder.length() != 0) {

			int containsStartPos = remainder.indexOf(CONTAINS_DEFINITION);

			if (containsStartPos >= 0) {

				String beforeContains = remainder.substring(0, containsStartPos);
				if (countOpenParentheses(beforeContains) > 0)
					throw new InvalidParameterException("A nested :contains can not be escaped automatically. Use :containsEscaped(escapedText).");
				processed.append(beforeContains);

				remainder = remainder.substring(containsStartPos);
				String meatPlusChange = remainder.substring(CONTAINS_DEFINITION.length());

				// find the next valid starting brace in the query after the start of the contains text
				int nextValidStartPos = getFirstValidParenthesisOpeningPos(meatPlusChange);
				String nextUp = "";
				if (nextValidStartPos >= 0) {
					nextUp = meatPlusChange.substring(nextValidStartPos);
					meatPlusChange = meatPlusChange.substring(0, nextValidStartPos);
				}
				int lastClosingParPos = meatPlusChange.lastIndexOf(")");
				if (lastClosingParPos < 0) throw new InvalidParameterException("An error occurred parsing selector to automatically escape. Use :containsEscaped manually");
				String change = meatPlusChange.substring(lastClosingParPos);
				String meat = meatPlusChange.substring(0, lastClosingParPos);
				//Note: single quotes around text are escaped and handled in the js
				String escapedMeat = (String) exec.executeScript("return " + WAFFLE_LOC_VAR + ".jsEscape(arguments[0]);", meat);

				processed.append(CONTAINS_ESCAPED_DEFINITION);
				processed.append(escapedMeat);
				processed.append(change);

				remainder = nextUp;

			} else {

				processed.append(remainder);
				remainder = "";
			}
		}

		return processed.toString();
	}

	private int getFirstValidParenthesisOpeningPos(String meatPlusChange) {

		int minIndex = -1;
		for (String def : PARENTHESISED_SELECTOR_DEFINITIONS) {

			int index = meatPlusChange.indexOf(def);
			if (index > minIndex) minIndex = index;
		}
		return minIndex;
	}

	private boolean containsUnestedParentheses(String query) {

		char[] chars = query.toCharArray();
		int open = 0;
		for (char a : chars) {
			if (a == '(') {
				open++;
			} else if (a == ')') {
				if (open == 0) {
					return true;
				} else {
					open--;
				}
			}
		}
		return open != 0;
	}

	private int countOpenParentheses(String text) {

		char[] chars = text.toCharArray();
		int open = 0;
		for (char a : chars) {
			if (a == '(') {
				open++;
			} else if (a == ')') {
				if (open == 0) {
					log.error("There is a unexpected closing parenthesis before an opening parenthesis in the query: " + query);
					throw new InvalidParameterException("There is a unexpected closing parenthesis before an opening parenthesis in the query: " + query);
				} else {
					open--;
				}
			}
		}
		return open;
	}
}
