package com.ibm.atmn.waffle.extensions.waitConditions;

/**
 * Waffle implementation of ExpectedConditions from Selenium jar
 * Altered to allow us to use RCLocationExecutor which also allows us to use JAVASCRIPT selectors
 * 
 * @author Liam Walsh
 */
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.StaleElementReferenceException;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;

public class Conditions {
	
	//private RCLocationExecutor driver;
	private Conditions(RCLocationExecutor driver) {
	    // Utility class
		  //this.driver = driver;
	  }

	  /**
	   * An expectation for checking the title of a page.
	   *
	   * @param title the expected title, which must be an exact match
	   * @return true when the title matches, false otherwise
	   */
	  public static BaseCondition<Boolean> titleIs(final String title) {
	    return new BaseCondition<Boolean>() {
	      private String currentTitle = "";

	      @Override
	      public Boolean apply(RCLocationExecutor driver) {
	        currentTitle = driver.getTitle();
	        return title.equals(currentTitle);
	      }

	      @Override
	      public String toString() {
	        return String.format("title to be \"%s\". Current title: \"%s\"", title, currentTitle);
	      }
	    };
	  }

	  /**
	   * An expectation for checking that the title contains a case-sensitive
	   * substring
	   *
	   * @param title the fragment of title expected
	   * @return true when the title matches, false otherwise
	   */
	  public static BaseCondition<Boolean> titleContains(final String title) {
	    return new BaseCondition<Boolean>() {
	      private String currentTitle = "";

	      @Override
	      public Boolean apply(RCLocationExecutor driver) {
	        currentTitle = driver.getTitle();
	        return currentTitle != null && currentTitle.contains(title);
	      }

	      @Override
	      public String toString() {
	        return String.format("title to contain \"%s\". Current title: \"%s\"", title, currentTitle);
	      }
	    };
	  }

	  /**
	   * An expectation for checking that an element is present on the DOM of a
	   * page. This does not necessarily mean that the element is visible.
	   *
	   * @param locator used to find the element
	   * @return the Element once it is located
	   */
	  public static BaseCondition<Element> presenceOfElementLocated(
	      final String locator) {
	    return new BaseCondition<Element>() {
	      @Override
	      public Element apply(RCLocationExecutor driver) {
	        return driver.getSingleElement(locator);
	      }

	      @Override
	      public String toString() {
	        return "presence of element located by: " + locator;
	      }
	    };
	  }

	  /**
	   * An expectation for checking that an element is present on the DOM of a page
	   * and visible. Visibility means that the element is not only displayed but
	   * also has a height and width that is greater than 0.
	   *
	   * @param locator used to find the element
	   * @return the Element once it is located and visible
	   */
	  public static BaseCondition<Element> visibilityOfElementLocated(
	      final String locator) {
	    return new BaseCondition<Element>() {
	      @Override
	      public Element apply(RCLocationExecutor driver) {
	        try {
	          return elementIfVisible(driver.getSingleElement(locator));
	        } catch (StaleElementReferenceException e) {
	          return null;
	        }
	      }

	      @Override
	      public String toString() {
	        return "visibility of element located by " + locator;
	      }
	    };
	  }

	  /**
	   * An expectation for checking that an element, known to be present on the DOM
	   * of a page, is visible. Visibility means that the element is not only
	   * displayed but also has a height and width that is greater than 0.
	   *
	   * @param element the Element
	   * @return the (same) Element once it is visible
	   */
	  public static BaseCondition<Element> visibilityOf(
	      final Element element) {
	    return new BaseCondition<Element>() {
	      @Override
	      public Element apply(RCLocationExecutor driver) {
	        return elementIfVisible(element);
	      }

	      @Override
	      public String toString() {
	        return "visibility of " + element;
	      }
	    };
	  }

	  /**
	   * @return the given element if it is visible and has non-zero size, otherwise
	   *         null.
	   */
	  private static Element elementIfVisible(Element element) {
	    return element.isDisplayed() ? element : null;
	  }

	  /**
	   * An expectation for checking that there is at least one element present on a
	   * web page.
	   *
	   * @param locator used to find the element
	   * @return the list of Elements once they are located
	   */
	  public static BaseCondition<List<Element>> presenceOfAllElementsLocatedBy(
	      final String locator) {
	    return new BaseCondition<List<Element>>() {
	      @Override
	      public List<Element> apply(RCLocationExecutor driver) {
	        List<Element> elements = driver.getElements(locator);	     
	        return elements.size() > 0 ? elements : null;
	      }

	      @Override
	      public String toString() {
	        return "presence of any elements located by " + locator;
	      }
	    };
	  }

	  /**
	   * An expectation for checking if the given text is present in the specified
	   * element.
	   */
	  public static BaseCondition<Boolean> textToBePresentInElement(
	      final String locator, final String text) {

	    return new BaseCondition<Boolean>() {
	      @Override
	      public Boolean apply(RCLocationExecutor driver) {
	        try {
	          String elementText = driver.getSingleElement(locator).getText();
	          return elementText.contains(text);
	        } catch (StaleElementReferenceException e) {
	          return null;
	        }
	      }

	      @Override
	      public String toString() {
	        return String.format("text ('%s') to be present in element found by %s",
	            text, locator);
	      }
	    };
	  }
	  
	  /**
	   * An expectation for checking if the given text is not present in the specified
	   * element.
	   */
	  public static BaseCondition<Boolean> textToBeNotPresentInElement(
	      final String locator, final String text) {

	    return new BaseCondition<Boolean>() {
	      @Override
	      public Boolean apply(RCLocationExecutor driver) {
	    	 // if ((testConfig.browserIs(BrowserType.FIREFOX) && Float.parseFloat(testConfig.getBrowserVersion()) >= 4)){//
		        try {
		          String IsThereText = driver.getSingleElement(locator).getText();//Changing from isDisplayed
		          if (IsThereText != null ){
		        	  return null; 
		          }else{
		        	  //IsThereText.contains(text);
		        	  return true;
		          }
		        } catch (NoSuchElementException e) {
		          // Returns true because the element is not present in DOM. The
		          // try block checks if the element is present but is invisible.
		          return false;
		        }
				//return null;
		      }
	   
	      @Override
	      public String toString() {
	        return String.format("text ('%s') is not present in element as expected",
	            text, locator);
	      }
	    };
	  }

	  /**
	   * An expectation for checking if the given text is present in the specified
	   * elements value attribute.
	   */
	  public static BaseCondition<Boolean> textToBePresentInElementValue(
	      final String locator, final String text) {

	    return new BaseCondition<Boolean>() {
	      @Override
	      public Boolean apply(RCLocationExecutor driver) {
	        try {
	          String elementText = driver.getSingleElement(locator).getAttribute("value");
	          if (elementText != null) {
	            return elementText.contains(text);
	          } else {
	            return false;
	          }
	        } catch (StaleElementReferenceException e) {
	          return null;
	        }
	      }

	      @Override
	      public String toString() {
	        return String.format("text ('%s') to be the value of element located by %s",
	            text, locator);
	      }
	    };
	  }

	  /**
	   * An expectation for checking whether the given frame is available to switch
	   * to. <p> If the frame is available it switches the given driver to the
	   * specified frame.
	   */
	  public static BaseCondition<RCLocationExecutor> frameToBeAvailableAndSwitchToIt(
	      final String frameLocator) {
	    return new BaseCondition<RCLocationExecutor>() {
	      @Override
	      public RCLocationExecutor apply(RCLocationExecutor driver) {
	        try {
	          return (RCLocationExecutor) driver.switchToFrame().selectSingleFrameBySelector(frameLocator);	          
	        } catch (NoSuchFrameException e) {
	          return null;
	        }
	      }

	      @Override
	      public String toString() {
	        return "frame to be available: " + frameLocator;
	      }
	    };
	  }

	  /**
	   * An expectation for checking that an element is either invisible or not
	   * present on the DOM.
	   *
	   * @param locator used to find the element
	   */
	  public static BaseCondition<Boolean> invisibilityOfElementLocated(
	      final String locator) {
	    return new BaseCondition<Boolean>() {
	      @Override
	      public Boolean apply(RCLocationExecutor driver) {
	        try {
	          return !(driver.getSingleElement(locator).isDisplayed());
	        } catch (NoSuchElementException e) {
	          // Returns true because the element is not present in DOM. The
	          // try block checks if the element is present but is invisible.
	          return true;
	        } catch (StaleElementReferenceException e) {
	          // Returns true because stale element reference implies that element
	          // is no longer visible.
	          return true;
	        }
	      }

	      @Override
	      public String toString() {
	        return "element to no longer be visible: " + locator;
	      }
	    };
	  }

	  /**
	   * An expectation for checking that an element with text is either invisible
	   * or not present on the DOM.
	   *
	   * @param locator used to find the element
	   * @param text of the element
	   */
	  public static BaseCondition<Boolean> invisibilityOfElementWithText(
	      final String locator, final String text) {
	    return new BaseCondition<Boolean>() {
	      @Override
	      public Boolean apply(RCLocationExecutor driver) {
	        try {
	          return !driver.getSingleElement(locator).getText().equals(text);
	        } catch (NoSuchElementException e) {
	          // Returns true because the element with text is not present in DOM. The
	          // try block checks if the element is present but is invisible.
	          return true;
	        } catch (StaleElementReferenceException e) {
	          // Returns true because stale element reference implies that element
	          // is no longer visible.
	          return true;
	        }
	      }


	      @Override
	      public String toString() {
	        return String.format("element containing '%s' to no longer be visible: %s",
	            text, locator);
	      }
	    };
	  }

	  /**
	   * An expectation for checking an element is visible and enabled such that you
	   * can click it.
	   */
	  public static BaseCondition<Element> elementToBeClickable(
	      final String locator) {
	    return new BaseCondition<Element>() {

	      public BaseCondition<Element> visibilityOfElementLocated =
	          Conditions.visibilityOfElementLocated(locator);

	      @Override
	      public Element apply(RCLocationExecutor driver) {
	        Element element = visibilityOfElementLocated.apply(driver);
	        try {
	          if (element != null && element.isEnabled()) {
	            return element;
	          } else {
	            return null;
	          }
	        } catch (StaleElementReferenceException e) {
	          return null;
	        }
	      }

	      @Override
	      public String toString() {
	        return "element to be clickable: " + locator;
	      }
	    };
	  }

	  /**
	   * Wait until an element is no longer attached to the DOM.
	   *
	   * @param element The element to wait for.
	   * @return false is the element is still attached to the DOM, true
	   *         otherwise.
	   */
	  public static BaseCondition<Boolean> stalenessOf(
	      final Element element) {
	    return new BaseCondition<Boolean>() {
	      @Override
	      public Boolean apply(RCLocationExecutor ignored) {
	        try {
	          // Calling any method forces a staleness check
	          element.isEnabled();
	          return false;
	        } catch (StaleElementReferenceException expected) {
	          return true;
	        }
	      }

	      @Override
	      public String toString() {
	        return String.format("element (%s) to become stale", element);
	      }
	    };
	  }

	  /**
	   * Wrapper for a condition, which allows for elements to update by redrawing.
	   *
	   * This works around the problem of conditions which have two parts: find an
	   * element and then check for some condition on it. For these conditions it is
	   * possible that an element is located and then subsequently it is redrawn on
	   * the client. When this happens a {@link StaleElementReferenceException} is
	   * thrown when the second part of the condition is checked.
	   */
	  public static <T> BaseCondition<T> refreshed(
	      final BaseCondition<T> condition) {
	    return new BaseCondition<T>() {
	      @Override
	      public T apply(RCLocationExecutor driver) {
	        try {
	          return condition.apply(driver);
	        } catch (StaleElementReferenceException e) {
	          return null;
	        }
	      }

	      @Override
	      public String toString() {
	        return String.format("condition (%s) to be refreshed", condition);
	      }
	    };
	  }

	  /**
	   * An expectation for checking if the given element is selected.
	   */
	  public static BaseCondition<Boolean> elementToBeSelected(final Element element) {
	    return elementSelectionStateToBe(element, true);
	  }

	  /**
	   * An expectation for checking if the given element is selected.
	   */
	  public static BaseCondition<Boolean> elementSelectionStateToBe(final Element element,
	                                                                     final boolean selected) {
	    return new BaseCondition<Boolean>() {
	      @Override
	      public Boolean apply(RCLocationExecutor driver) {
	        return element.isSelected() == selected;
	      }

	      @Override
	      public String toString() {
	        return String.format("element (%s) to %sbe selected", element, (selected ? "" : "not "));
	      }
	    };
	  }

	  public static BaseCondition<Boolean> elementToBeSelected(final String locator) {
	    return elementSelectionStateToBe(locator, true);
	  }

	  public static BaseCondition<Boolean> elementSelectionStateToBe(final String locator,
	                                                                     final boolean selected) {
	    return new BaseCondition<Boolean>() {
	      @Override
	      public Boolean apply(RCLocationExecutor driver) {
	        try {
	          Element element = driver.getSingleElement(locator);
	          return element.isSelected() == selected;
	        } catch (StaleElementReferenceException e) {
	          return null;
	        }
	      }

	      @Override
	      public String toString() {
	        return String.format("element found by %s to %sbe selected",
	            locator, (selected ? "" : "not "));
	      }
	    };
	  }

	  public static BaseCondition<Alert> alertIsPresent() {
	    return new BaseCondition<Alert>() {
	      @Override
	      public Alert apply(RCLocationExecutor driver) {
	        try {
	          return (Alert) driver.switchToAlert();	         
	        } catch (NoAlertPresentException e) {
	          return null;
	        }
	      }

	      @Override
	      public String toString() {
	        return "alert to be present";
	      }
	    };
	  }

	  /**
	   * An expectation with the logical opposite condition of the given condition.
	   * In case of null, it will return false.
	   */
	  public static BaseCondition<Boolean> not(final BaseCondition<?> condition) {
	    return new BaseCondition<Boolean>() {
	      @Override
	      public Boolean apply(RCLocationExecutor driver) {
	        Object result = condition.apply(driver);
	        return !(result == null || result == Boolean.FALSE ||result == Boolean.TRUE);
	      }

	      @Override
	      public String toString() {	    	
	        return "condition to not be valid: " + condition;
	      }
	    };
	  }

	  /**
	   * Looks up an element. Logs and re-throws WebDriverException if thrown. <p/>
	   * Method exists to gather data for http://code.google.com/p/selenium/issues/detail?id=1800
	   */
//	  private static Element findElement(By by, WebDriver driver) {
//	    try {
//	      return driver.findElement(by);
//	    } catch (NoSuchElementException e) {
//	      throw e;
//	    } catch (WebDriverException e) {
//	      log.log(Level.WARNING,
//	          String.format("WebDriverException thrown by findElement(%s)", by), e);
//	      throw e;
//	    }
//	  }

	  /**
	   * @see #findElement(By, WebDriver)
	   */
//	  private static List<Element> findElements(By by, WebDriver driver) {
//	    try {
//	      return driver.findElements(by);
//	    } catch (WebDriverException e) {
//	      log.log(Level.WARNING,
//	          String.format("WebDriverException thrown by findElement(%s)", by), e);
//	      throw e;
//	    }
//	  }


}

