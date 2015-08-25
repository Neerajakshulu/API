package com.thomsonreuters.automation.watchlist;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test for the Watchlist API's
 * 
 * TestData: WatchlistTestData.xlsx
 */
public class WatchlistTest extends AbstractBase {

	@Test
	public void wathclistTest() throws Exception {
		testDataExcelPath = "src/test/test-data/WatchlistTestData.xlsx";
		appName = "1PCITATIONS";
		runTests();
	}
}
