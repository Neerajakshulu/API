package com.thomsonreuters.automation.follow;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.common.AbstractBase;
import com.thomsonreuters.automation.common.RowData;

/**
 * The {@code FollowTest} class to test for the Follow API's.
 *
 * @author Avinash P
 * 
 */

public class FollowTest extends AbstractBase {

	/**
	 * {@code followTest} method is the entry point to test 1PFOLLOW API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * FollowTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PFOLLOW tests
	 * @see Exception
	 * 
	 */

	@Test
	public void followTest() throws Exception {
		testDataExcelPath = "src/test/test-data/FollowTestData.xlsx";
		appName = "1PFOLLOW";
		runTests();
	}

	// Follow a user and Check Followers count got increased or not
	/**
	 * {@code checkFallowersCount} method is to test followers count. This meted have dependency with current followers
	 * count which was running in {@code followTest} method
	 * 
	 * @throws Exception On Executing the 1PFOLLOW tests
	 * @see Exception
	 * 
	 */

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
		rowData.setQueryString("");
		testReporter = reporter.startTest(rowData.getTestName(), rowData.getDescription()).assignCategory(appName);
		executeTest(rowData);
		reporter.endTest(testReporter);
		rowData.setTestName("OPQA-462_1");
		rowData.setHost("1PFOLLOW");
		rowData.setDescription("Verify that Stop following a user");
		rowData.setApiPath("/follow/user/(SYS_USER2)/following/(SYS_USER1)");
		rowData.setMethod("DELETE");
		executeTest(rowData);
		logger.info("Ending 1PFOLLOW checkFallowersCount method...");
	}

	/**
	 * {@code executeTest} method executes specified test case, validates the response, stores the response and update
	 * the test status.
	 * 
	 * @throws Exception
	 * 
	 */
	private void executeTest(RowData rowData) throws Exception {
		Response response = null;
		String sheetName = "Follow";
		String responseJson = null;
		String statusCode = null;
		boolean testSuccess = false;
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
				response = getAPIResponce();
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
