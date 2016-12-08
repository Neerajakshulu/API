package com.thomsonreuters.automation.dra;

import org.testng.annotations.Test;
import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The DRAContainerTest program is an entry point for running Container API for DRA Saved Search type test cases. This class initializes app
 * name, excel file path which are utilized by AbstractBase class.
 *
 * @author Janardhan
 * @version 1.0
 * @since 2016-12-08
 */
public class DRAContainerTest extends AbstractBase {

	/**
	 * This method is entry point for testing. Initializes excel file path and app name. Calls runTests method for
	 * executing test cases specified in the excel file.
	 * 
	 * @return Nothing
	 * @throws Exception
	 * 
	 */
	@Test
	public void DRAContainerTest() throws Exception {
		testDataExcelPath = "src/test/test-data/DRAContainerTestData.xlsx";
		appName = "1PCONTAINER";
		runTests();
	}
}
