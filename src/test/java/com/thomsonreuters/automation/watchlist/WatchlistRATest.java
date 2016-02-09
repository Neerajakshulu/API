package com.thomsonreuters.automation.watchlist;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test for the WatchlistRA API's
 * 
 * TestData: WatchlistRATestData.xlsx
 */
public class WatchlistRATest extends AbstractBase {

	@Test
	public void wathclistTest() throws Exception {
		testDataExcelPath = "src/test/test-data/WatchlistTestRAData.xlsx";
		appName = "1PWATCHLISTRA";
		runTests();
	}
}
