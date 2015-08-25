package com.thomsonreuters.automation.profile;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test the Profile APIs
 */
public class ProfileTest extends AbstractBase {

	@Test
	public void profileTest() throws Exception {
		testDataExcelPath = "src/test/test-data/ProfileTestData.xlsx";
		appName = "1PPROFILE";
		runTests();
	}
}
