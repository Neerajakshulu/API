package com.thomsonreuters.automation.decorator;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code BatchJobEvent} class to create events for the Decorator notification API's.
 *
 * @author Ramesh Lalam
 * 
 */
public class BatchJobEvent extends AbstractBase  {
	
	/**
	 * This {@code notifyTest} method is entry point to test 1PNOTIFY API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * NotifyTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PNOTIFY tests
	 * @see Exception
	 * 
	 */
	@Test
	public void notifyTest1() throws Exception {
		testDataExcelPath = "src/test/test-data/DecoratorBatchJobTest.xlsx";
		appName = "1PDECORATOR";
		runTests("Event Generation");
		
		
		
		
	}
}
