package com.thomsonreuters.automation.profile;

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
public class ProfileTest extends AbstractBase {

	@Test(priority = 1)
	public void profileTest() throws Exception {
		testDataExcelPath = "src/test/test-data/ProfileTestData.xlsx";
		appName = "1PPROFILE";
		runTests();
	}
	
	

	@Test
	public void testSummaryMaxLength() throws Exception {
		logger.info("Entered Profile testSummaryMaxLength method...");
		appName = "1PPROFILE";
		rowData = new RowData();
		rowData.setTestName("S1_TC_ST1");
		rowData.setHost("1PPROFILE");
		rowData.setDescription("Verify that update user profile summary with exceeds Max length and verify that API should truncate to 1500 characters");
		rowData.setApiPath("/users/user/(SYS_USER1)");
		rowData.setMethod("PUT");
		rowData.setHeaders("X-1P-User=(SYS_USER1)||Content-Type=application/json");
		rowData.setBody(
				"{\"summary\":\"Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with   Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length Summary test with max length\"}");
		rowData.setValidations("status=200||hits.hits._source.firstName=Mohana");
		rowData.setStore("hits.hits._id");
		testReporter=reporter.startTest(rowData.getTestName(), rowData.getDescription()).assignCategory(appName);
		ExecuteTest(rowData);
		reporter.endTest(testReporter);
		logger.info("Entered Profile testSummaryMaxLength method...");
	}

	private void ExecuteTest(RowData rowData) throws Exception {
		Response response = null;
		String sheetName = "Profile";
		String apiPath = null;
		String headers = null;
		String queryString = null;
		String url = null;
		String responseJson = null;
		String statusCode = null;
		boolean testSuccess = false;

		logger.debug("row data=" + rowData.toString());
		logger.debug("Real host=" + appHosts.get(rowData.getHost()));
		/*
		 * If mandatory information like test case name, host, api path and
		 * valid http method are not provided then skip those tests and update
		 * the status as fail.
		 */
		if (StringUtils.isNotBlank(rowData.getTestName()) && StringUtils.isNotBlank(rowData.getHost())
				&& StringUtils.isNotBlank(rowData.getApiPath()) && isSupportedMethod(rowData.getMethod())) {
			// If any of the dependency test failed then don't proceed.
			if (isDependencyTestsPassed(rowData.getDependencyTests())) {
				logger.info("-----------------------------------------------------------------------");
				logger.info("Starting test:" + rowData.getTestName());
				apiPath = replaceDynamicPlaceHolders(rowData.getApiPath());
				headers = replaceDynamicPlaceHolders(rowData.getHeaders());
				queryString = replaceDynamicPlaceHolders(rowData.getQueryString());
				url = appHosts.get(rowData.getHost()) + apiPath + queryString;
				logger.debug("URL=" + url);
				RequestSpecification reqSpec = given();
				// Get and set headers to request
				if (StringUtils.isNotBlank(rowData.getHeaders())) {
					Map<String, String> headersMap = getHeaders(headers);
					reqSpec.headers(headersMap);
				}

				// Set body to request if the http method is not GET.
				if (!rowData.getMethod().equalsIgnoreCase(GET) && StringUtils.isNotBlank(rowData.getBody())) {
					reqSpec.body(rowData.getBody());
				}

				if (rowData.getMethod().equalsIgnoreCase(PUT)) {
					logger.debug("Entered into PUT Method");

					// Call the Rest API and get the response
					response = reqSpec.when().put(url);

				}

				// response.then().log().all();
				responseJson = response.asString();
				statusCode = String.valueOf(response.getStatusCode());
				// Validate the response with expected data
//				testSuccess = testSummaryMaxLengthvalidateResponse(rowData.getValidations(), responseJson, statusCode);
				if(statusCode.equals("200")){
				JsonPath jsonPath = new JsonPath(responseJson);
				String summary = jsonPath.getString("summary");
				if (summary.length() == 1500) {
					logger.info("summary text was truncated to 1500 characters");
					testReporter.log(LogStatus.INFO, "summary text was truncated to 1500 characters");
					testSuccess = true;
				}
				}
				// Save API response to file
				saveAPIResponse(responseJson, sheetName, rowData.getTestName());

				if (!testSuccess) {
					testReporter.log(LogStatus.FAIL, "FAIL");
					throw new Exception("Validation Failed");
				}else{
					testReporter.log(LogStatus.PASS, "PASS" );
				}

				logger.info("End execution of test:" + rowData.getTestName());
				logger.info("-----------------------------------------------------------------------");
			}
		}
	}
}
