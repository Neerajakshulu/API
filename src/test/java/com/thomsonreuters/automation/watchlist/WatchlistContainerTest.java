package com.thomsonreuters.automation.watchlist;

import org.testng.annotations.Test;
import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The WatchlistContainerTest program is an entry point for running Container API with Watchlist type test cases. This class initializes app
 * name, excel file path which are utilized by AbstractBase class.
 *
 * @author Janardhan
 * @version 1.0
 * @since 2016-10-21
 */
public class WatchlistContainerTest extends AbstractBase {

	/**
	 * This method is entry point for testing. Initializes excel file path and app name. Calls runTests method for
	 * executing test cases specified in the excel file.
	 * 
	 * @return Nothing
	 * @throws Exception
	 * 
	 */
	@Test
	public void WatchlistContainerTest() throws Exception {
		testDataExcelPath = "src/test/test-data/WatchlistContainerTestData.xlsx";
		appName = "1PCONTAINER";
		runTests();
	}
}
