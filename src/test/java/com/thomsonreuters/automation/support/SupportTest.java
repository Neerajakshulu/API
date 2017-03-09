package com.thomsonreuters.automation.support;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code GroupsTest} class to test for the Groups API's.
 *
 * @author Janardhan
 * 
 */
public class SupportTest extends AbstractBase {

	/**
	 * {@code SupportTest} method is the entry point to test 1PSUPPORT API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * SupportTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PSUPPORT tests
	 * @see Exception
	 * 
	 */
	@Test
	public void supportTest() throws Exception {
		testDataExcelPath = "src/test/test-data/SupportTestData.xlsx";
		appName = "1PSUPPORT";
		runTests();
	}

}

