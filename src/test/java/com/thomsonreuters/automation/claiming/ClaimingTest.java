package com.thomsonreuters.automation.claiming;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.common.AbstractBase;
import com.thomsonreuters.automation.common.RowData;

/**
 * Test the Profile APIs
 */
public class ClaimingTest extends AbstractBase {

	@Test
	public void claimingTest() throws Exception {
		testDataExcelPath = "src/test/test-data/ClaimingTestData.xlsx";
		appName = "1PCLAIMING";
		runTests();
	}
	
}
