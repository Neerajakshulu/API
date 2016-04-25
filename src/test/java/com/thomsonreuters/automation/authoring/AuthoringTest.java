package com.thomsonreuters.automation.authoring;

import org.testng.annotations.Test;
import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The AuthoringTest program is an entry point for running Authoring API test cases. This class initializes app name,
 * excel file path which are utilized by AbstractBase class. Also executes external test case and update the test
 * status.
 *
 * @author Janardhan
 * @version 1.0
 * @since 2015-08-31
 */
public class AuthoringTest extends AbstractBase {

	/**
	 * This method is entry point for testing. Initializes excel file path and app name. Calls runTests method for
	 * executing test cases specified in the excel file.
	 * 
	 * @return Nothing
	 * @throws Exception
	 * 
	 */
	@Test
	public void authoringTest() throws Exception {
		testDataExcelPath = "src/test/test-data/AuthoringTestData.xlsx";
		appName = "1PAUTHORING";
		Thread.sleep(20000);
		runTests();
	}
}
