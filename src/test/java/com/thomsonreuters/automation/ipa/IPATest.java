package com.thomsonreuters.automation.ipa;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The ProfileTest program is an entry point for running Profile API test cases. This class initializes app name, excel
 * file path which are utilized by AbstractBase class.
 *
 * @author Srinivasa Narayanappa
 * @version 1.0
 * @since 2016-12-05
 */
public class IPATest extends AbstractBase {

	/**
	 * This method is entry point for testing. Initializes excel file path and app name. Calls runTests method for
	 * executing test cases specified in the excel file.
	 * 
	 * @return Nothing
	 * @throws Exception
	 * 
	 */
	@Test
	public void competitorsTest() throws Exception {
		testDataExcelPath = "src/test/test-data/IPA.xlsx";
		appName = "IPAANALYTICS";
		runTests();
	}
}
