package com.thomsonreuters.automation.steam;

import org.testng.annotations.Test;
import com.thomsonreuters.steam.core.SteamAbstractBase;

/**
* The SteamTest program is an entry point for running Steam API test cases.
* This class initializes app name, excel file path and xml templates folder path which are utilized by SteamAbstractBase.
*
* @author  Janardhan
* @version 1.0
* @since   2016-03-31 
*/
public class SteamTest extends SteamAbstractBase {

	/**
	 * This method is entry point for testing. Initializes excel file path and app name. 
	 * Calls runTests method for executing test cases specified in the excel file.  
	 * 
	 * @return 		Nothing
	 * @throws 		Exception
	 * 
	 */
	@Test
	public void steamTest() throws Exception {
		testDataExcelPath = "src/test/test-data/STeAMTestData.xlsx";
		templatePath = "src/test/test-data/STEAM_TEMPLATES/";
		appName = "1PSTEAM";
		runTests();
	}
}
