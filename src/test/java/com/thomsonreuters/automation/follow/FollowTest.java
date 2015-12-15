package com.thomsonreuters.automation.follow;

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
 * Test the Follow APIs
 */
public class FollowTest extends AbstractBase {

	@Test
	public void followTest() throws Exception {
		testDataExcelPath = "src/test/test-data/FollowTestData.xlsx";
		appName = "1PFOLLOW";
		runTests();
	}

	// Follow a user and Check Followers count got increased or not
	@Test(priority = 1)
	public void checkFallowersCount() throws Exception {
		logger.info("Entered 1PFOLLOW checkFallowersCount method...");
		appName = "1PFOLLOW";
		rowData = new RowData();
		rowData.setTestName("OPQA-447_2");
		rowData.setHost("1PFOLLOW");
		rowData.setDescription("Verify that to check count of my followers");
		rowData.setApiPath("/follow/user/(SYS_USER1)/count/followers");
		rowData.setMethod("GET");
		testReporter = reporter.startTest(rowData.getTestName(), rowData.getDescription()).assignCategory(appName);
		ExecuteTest(rowData);
		reporter.endTest(testReporter);

		rowData.setTestName("OPQA-462_1");
		rowData.setHost("1PFOLLOW");
		rowData.setDescription("Verify that Stop following a user");
		rowData.setApiPath("/follow/user/(SYS_USER2)/following/(SYS_USER1)");
		rowData.setMethod("DELETE");
		ExecuteTest(rowData);

		logger.info("Ending 1PFOLLOW checkFallowersCount method...");
	}

	private void ExecuteTest(RowData rowData) throws Exception {
		Response response = null;
		String sheetName = "Follow";
		String apiPath = null;
		String headers = null;
		String queryString = null;
		String url = null;
		String responseJson = null;
		String statusCode = null;
		boolean testSuccess = false;
		String bodyString = null;

		logger.debug("row data=" + rowData.toString());
		logger.debug("Real host=" + appHosts.get(rowData.getHost()));
		/*
		 * If mandatory information like test case name, host, api path and valid http method are not provided then skip
		 * those tests and update the status as fail.
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
				if (!rowData.getMethod().equalsIgnoreCase(GET) && StringUtils.isNotBlank(bodyString)) {
					reqSpec.body(bodyString);
				}

				if (rowData.getMethod().equalsIgnoreCase(GET)) {
					logger.debug("Entered into GET Method");

					// Call the Rest API and get the response
					response = reqSpec.when().get(url);

				} else if (rowData.getMethod().equalsIgnoreCase(PUT)) {
					logger.debug("Entered into PUT Method");

					// Call the Rest API and get the response
					response = reqSpec.when().put(url);

				} else if (rowData.getMethod().equalsIgnoreCase(POST)) {
					logger.debug("Entered into POST Method");

					// Call the Rest API and get the response
					response = reqSpec.when().post(url);

				} else if (rowData.getMethod().equalsIgnoreCase(DELETE)) {
					logger.debug("Entered into DELETE Method");

					// Call the Rest API and get the response
					response = reqSpec.when().delete(url);
				}

				responseJson = response.asString();
				statusCode = String.valueOf(response.getStatusCode());
				// Save API response to file
				saveAPIResponse(responseJson, sheetName, rowData.getTestName());
				if (statusCode.equals("200") && "OPQA-447_2".equalsIgnoreCase(rowData.getTestName())) {
					JsonPath jsonPath = new JsonPath(responseJson);
					// String content = jsonPath.getString("count");
					int oldCount = Integer.parseInt(dataStore.get("OPQA-447_1_count"));
					int newCount = Integer.parseInt(jsonPath.getString("count"));
					if (newCount == oldCount + 1) {
						logger.info("Fallowers count was increased ");
						testReporter.log(LogStatus.INFO, "Fallowers count was increased");
						testSuccess = true;
					}
				} else if (statusCode.equals("200")) {
					testSuccess = true;
				}
				if (!testSuccess) {
					testReporter.log(LogStatus.FAIL, "FAIL");
					throw new Exception("Validation Failed");
				} else {
					testReporter.log(LogStatus.PASS, "PASS");
					storeDependentTestsData(responseJson, rowData.getStore(), rowData.getTestName());
				}

				logger.info("End execution of test:" + rowData.getTestName());
				logger.info("-----------------------------------------------------------------------");
			}
		}
	}
}
