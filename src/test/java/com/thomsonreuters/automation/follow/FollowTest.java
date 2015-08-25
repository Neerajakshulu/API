package com.thomsonreuters.automation.follow;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test the Follow APIs
 */
public class FollowTest extends AbstractBase {

	@Test
	public void followTest() throws Exception {
		testDataExcelPath = "src/test/test-data/FollowTestData.xlsx";
		appName = "1PFOLLOW";
		runTests();
	}
}
