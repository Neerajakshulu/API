package com.thomsonreuters.automation.search;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Tests for the Search API
 * 
 * TestData: SearchTestData.xlsx
 */
public class SearchTest extends AbstractBase {

	@Test
	public void searchTest() throws Exception {
		testDataExcelPath = "src/test/test-data/SearchTestData.xlsx";
		appName = "1PSEARCH";
		runTests();
	}
}
