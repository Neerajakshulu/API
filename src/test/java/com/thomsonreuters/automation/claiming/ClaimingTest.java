package com.thomsonreuters.automation.claiming;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test the Profile APIs
 */
public class ClaimingTest extends AbstractBase {

	@Test
	public void claimingTest() throws Exception {
		testDataExcelPath = "src/test/test-data/ClaimingTestData.xlsx";
		appName = "1PCLAIMING";
		runTests();
	}

}
