package com.thomsonreuters.automation.recommend;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test for the Recommend API's
 * 
 * TestData: RecommendTestData.xlsx
 */
public class RecommendTest extends AbstractBase {

	@Test
	public void recommendTest() throws Exception {
		testDataExcelPath = "src/test/test-data/RecommendTestData.xlsx";
		appName = "1PRECOMMEND";
		runTests();
	}
}
