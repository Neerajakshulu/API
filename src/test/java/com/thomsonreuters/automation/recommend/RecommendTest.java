package com.thomsonreuters.automation.recommend;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code RecommendTest} class to test for the Recommend API's.
 *
 * @author Avinash P
 * 
 */
public class RecommendTest extends AbstractBase {

	/**
	 * {@code recommendTest} method is the entry point to test 1PRECOMMEND API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * RecommendTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PRECOMMEND tests
	 * @see Exception
	 * 
	 */

	@Test
	public void recommendTest() throws Exception {
		testDataExcelPath = "src/test/test-data/RecommendTestData.xlsx";
		appName = "1PRECOMMEND";
		runTests();
	}
}
