/**
* The ClaimingTest program is an entry point for running Claiming API test cases.
* This class initializes app name, excel file path which are utilized by AbstractBase class.
*
* @author  Janardhan
* @version 1.0
* @since   2016-01-31 
*/
package com.thomsonreuters.automation.claiming;

import org.testng.annotations.Test;
import com.thomsonreuters.automation.common.AbstractBase;


public class ClaimingTest extends AbstractBase {

	/**
	 * This method is entry point for testing. Initializes excel file path and app name. 
	 * Calls runTests method for executing test cases specified in the excel file.  
	 * 
	 * @return 		Nothing
	 * @throws 		Exception
	 * 
	 */
	@Test
	public void claimingTest() throws Exception {
		testDataExcelPath = "src/test/test-data/ClaimingTestData.xlsx";
		appName = "1PCLAIMING";
		runTests();
	}
}
