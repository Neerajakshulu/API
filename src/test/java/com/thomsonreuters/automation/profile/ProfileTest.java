package com.thomsonreuters.automation.profile;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.common.AbstractBase;
import com.thomsonreuters.automation.common.RowData;

/**
 * The ProfileTest program is an entry point for running Profile API test cases. This class initializes app name, excel
 * file path which are utilized by AbstractBase class.
 *
 * @author Janardhan
 * @version 1.0
 * @since 2015-08-31
 */
public class ProfileTest extends AbstractBase {

	/**
	 * This method is entry point for testing. Initializes excel file path and app name. Calls runTests method for
	 * executing test cases specified in the excel file.
	 * 
	 * @return Nothing
	 * @throws Exception
	 * 
	 */
	@Test
	public void profileTest() throws Exception {
		testDataExcelPath = "src/test/test-data/ProfileTestData.xlsx";
		appName = "1PPROFILE";
		runTests();
	}
}
