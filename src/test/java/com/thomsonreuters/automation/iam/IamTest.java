package com.thomsonreuters.automation.iam;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test the IAM APIs
 */
public class IamTest extends AbstractBase {

	private static final Logger logger = LogManager.getLogger();

	@BeforeSuite
	public void beforeSuite() throws Exception {
		logger.info("@BeforeSuite - any initialization / activity to perform before starting your test suite");
		strDateTime = new SimpleDateFormat(TESTOUTPUT_FOLDER_DATEFORMAT).format(new Date());

		String eurekaURL = System.getProperty("eurekaUrl");
		
		String envSuffix = System.getProperty("envSuffix");
		
		logger.info("eurekaURL = "+ eurekaURL);
		
		logger.info("envSuffix = "+ envSuffix);
		
		strDateTime = new SimpleDateFormat(TESTOUTPUT_FOLDER_DATEFORMAT).format(new Date());

		// This method get all the application host names for the given environment
		getAllAppHostsForGivenEnv(eurekaURL, envSuffix);
	}

	@Test
	public void iamTest() throws Exception {
		testDataExcelPath = "src/test/test-data/IAMTestData.xlsx";
		appName = "1PAUTH";
		runTests();
	}
}
