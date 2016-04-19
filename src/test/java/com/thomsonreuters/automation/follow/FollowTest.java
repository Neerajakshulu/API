package com.thomsonreuters.automation.follow;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code FollowTest} class to test for the Follow API's.
 *
 * @author Avinash P
 * 
 */

public class FollowTest extends AbstractBase {

	/**
	 * {@code followTest} method is the entry point to test 1PFOLLOW API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * FollowTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PFOLLOW tests
	 * @see Exception
	 * 
	 */

	@Test
	public void followTest() throws Exception {
		testDataExcelPath = "src/test/test-data/FollowTestData.xlsx";
		appName = "1PFOLLOW";
		runTests();
	}
}
