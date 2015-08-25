package com.thomsonreuters.automation.search;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;


/**
 * Tests for the Search API
 * 
 * TestData: SearchTestData.xlsx
 */
public class SearchTest extends AbstractBase {

	private static final Logger logger = LogManager.getLogger();
	
	@BeforeSuite
	public void beforeSuite() throws Exception {
		logger.info("@BeforeSuite - any initialization / activity to perform before starting your test suite");
		strDateTime = new SimpleDateFormat(TESTOUTPUT_FOLDER_DATEFORMAT).format(new Date());

		String eurekaURL = System.getProperty("eurekaUrl");
		
		String envSuffix = System.getProperty("envSuffix");
		
		String IP = System.getProperty("IP");

		logger.info("eurekaURL = " + eurekaURL);

		logger.info("envSuffix = " + envSuffix);

		logger.info("IP = " + IP);
		
		strDateTime = new SimpleDateFormat(TESTOUTPUT_FOLDER_DATEFORMAT).format(new Date());

		// This method get all the application host names for the given environment
		getAllAppHostsForGivenEnv(eurekaURL, envSuffix, IP);
	}

	@Test
	public void searchTest() throws Exception {
		testDataExcelPath = "src/test/test-data/SearchTestData.xlsx";
		appName = "1PSEARCH";
		runTests();
	}
}
