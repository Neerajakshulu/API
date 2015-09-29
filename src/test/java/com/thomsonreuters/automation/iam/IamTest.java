package com.thomsonreuters.automation.iam;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test the IAM APIs
 */
public class IamTest extends AbstractBase {

	@Test
	public void iamTest() throws Exception {
		testDataExcelPath = "src/test/test-data/IAMTestData.xlsx";
		appName = "1PAUTH";
		runTests();
	}
}
