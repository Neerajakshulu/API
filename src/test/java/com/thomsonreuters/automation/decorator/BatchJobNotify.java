package com.thomsonreuters.automation.decorator;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code NotifyBatchGet} class to test for the Decorator API's notifications.
 *
 * @author Ramesh L
 * 
 */
public class BatchJobNotify extends AbstractBase {
	
	/**
	 * This {@code notifyTest} method is entry point to test 1PDECORATOR API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * DecoratorBatchJobTest_HPA.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PNOTIFY tests
	 * @see Exception
	 * 
	 */
	@Test
	public void notifyTest2() throws Exception {
		testDataExcelPath = "src/test/test-data/DecoratorBatchJobTest_HPA.xlsx";
		appName = "1PNOTIFY";
		runTests("Notification Test");
		
	}

}
