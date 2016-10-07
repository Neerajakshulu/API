package com.thomsonreuters.automation.notify;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code NotifyTest} class to test for the Notify API's.
 *
 * @author Avinash P
 * 
 */
public class NotifyTest extends AbstractBase {

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
	public void notifyTest() throws Exception {
		testDataExcelPath = "src/test/test-data/NotifyTestData.xlsx";
		appName = "1PNOTIFY";
		runTests();
	}
}
