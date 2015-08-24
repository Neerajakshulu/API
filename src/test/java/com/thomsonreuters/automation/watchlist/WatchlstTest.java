package com.thomsonreuters.automation.watchlist;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;


public class WatchlstTest extends AbstractBase {

	private static final Logger logger = LogManager.getLogger();
	
	@BeforeSuite
	public void beforeSuite() throws Exception {
		logger.info("@BeforeSuite - any initialization / activity to perform before starting your test suite");
		strDateTime = new SimpleDateFormat(TESTOUTPUT_FOLDER_DATEFORMAT).format(new Date());

		// This method get all the application host names for the given environment
		getAllAppHostsForGivenEnv(ENV);
	}

	@Test
	public void wathclistTest() throws Exception {
		testDataExcelPath = "src/test/test-data/WatchlistTestData.xlsx";
		appName = "1PCITATIONS";
		runTests();
	}
}
