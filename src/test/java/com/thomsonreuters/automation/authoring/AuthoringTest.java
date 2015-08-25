package com.thomsonreuters.automation.authoring;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test the Authoring APIs
 */
public class AuthoringTest extends AbstractBase {

	@Test
	public void authoringTest() throws Exception {
		testDataExcelPath = "src/test/test-data/AuthoringTestData.xlsx";
		appName = "1PAUTHORING";
		runTests();
	}
}
