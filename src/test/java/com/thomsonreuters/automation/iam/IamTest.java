package com.thomsonreuters.automation.iam;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code IamTest} class to test for the IAM API's.
 *
 * @author Mohana Yalamarthi
 * 
 */
public class IamTest extends AbstractBase {

	/**
	 * {@code iamTest} method is the entry point to test 1PAUTH API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * IAMTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the IAM tests
	 * @see Exception
	 * 
	 */
	@Test
	public void iamTest() throws Exception {
		testDataExcelPath = "src/test/test-data/IAMTestData.xlsx";
		appName = "1PAUTH";
		runTests();
	}
}
