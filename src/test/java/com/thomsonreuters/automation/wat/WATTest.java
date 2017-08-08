package com.thomsonreuters.automation.wat;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code FollowTest} class to test for the WAT API's.
 *
 * @author Chinna Putha
 * 
 */

public class WATTest extends AbstractBase {

	/**
	 * {@code followTest} method is the entry point to test 1PRecommend(WAT) API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * FollowTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the WAT  tests
	 * @see Exception
	 * 
	 */

	@Test
	public void wosAuthTransTest() throws Exception {
		testDataExcelPath = "src/test/test-data/WATTestData.xlsx";
		appName = "1PRECOMMEND";
		runTests();
	}
}
