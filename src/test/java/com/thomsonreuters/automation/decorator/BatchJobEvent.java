package com.thomsonreuters.automation.decorator;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.testng.annotations.Test;

import com.jayway.restassured.response.Response;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code BatchJobEvent} class to create events for the Decorator notification API's.
 *
 * @author Ramesh Lalam
 * 
 */
public class BatchJobEvent extends AbstractBase  {
	
	/**
	 * This {@code notifyTest1} method is entry point to create events for 1PDECORATOR API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * DecoratorBatchJobTest_HPA.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PDECORATOR tests
	 * @see Exception
	 * 
	 */
	@Test
	public void notifyTest1() throws Exception {
		dataStore.put(TESTOUTPUT_FOLDER_DATEFORMAT, strDateTime);
		testDataExcelPath = "src/test/test-data/DecoratorBatchJobTest_HPA.xlsx";
		appName = "1PDECORATOR";
		runTests("Event Generation");
		
		
	}
	/**
	 * {@code process} method to build URL with help of row, calling {@code getAPIResponce} method for getting response
	 * and validates response json and updates status(PASS/FAIL/DEPFAIL) back into excel file
	 * 
	 * @param row
	 * @param sheetName
	 */
	protected void process(XSSFRow row,
			String sheetName) {
		// RowData rowData = null;
		Response response = null;
		String validationString = null;
		String responseJson = null;
		String statusCode = null;
		boolean testSuccess = false;
		/*
		 * If mandatory information like test case name, host, api path and valid http method are not provided then skip
		 * those tests and update the status as fail.
		 */
		if (StringUtils.isNotBlank(rowData.getTestName()) && StringUtils.isNotBlank(rowData.getHost())
				&& StringUtils.isNotBlank(rowData.getApiPath()) && isSupportedMethod(rowData.getMethod())) {
			testReporter = reporter.startTest(testName, rowData.getDescription()).assignCategory(appName);
			if (rowData.getTestName().equals("OPQA-1431") || rowData.getTestName().equals("OPQA-236")) {
				try {
					Thread.sleep(60000);
				}catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
			if (isDependencyTestsPassed(rowData.getDependencyTests())) {
				validationString = replaceDynamicPlaceHolders(rowData.getValidations());
				response = getAPIResponce();
				responseJson = response.asString();
				statusCode = String.valueOf(response.getStatusCode());

				try {
					// Validate the response with expected data
					testSuccess = validateResponse(validationString, responseJson, statusCode);

					if (!testSuccess && responseJson != null) {
						logger.info("Response:" + responseJson);
					}

					// Save API response to file
					saveAPIResponse(responseJson, sheetName, rowData.getTestName());

				} catch (Exception e) {
					e.printStackTrace();
				}
				try {

				if (testSuccess) {
					// Store the data which is required for subsequent test cases.
					storeDependentTestsData(responseJson, rowData.getStore(), rowData.getTestName());
					// Update the excel file with Test PASS / FAIL status
					updateTestStatus(rowData.getTestName(), row, getStatus(testSuccess));
					// Report status
					testReporter.log(LogStatus.PASS, "PASS");
				} else {
					isTestFail = true;
					// Report status
					testReporter.log(LogStatus.FAIL, "FAIL");
				} 
				} catch (Exception e) {
					e.printStackTrace();
				}

				logger.info("End execution of test:" + rowData.getTestName());
				logger.info("-----------------------------------------------------------------------");

			} else {
				logger.debug("Dependency test " + rowData.getDependencyTests() + " failed hence skipping this test.");
				try {
					updateTestStatus(rowData.getTestName(), row, DEPENDENCY_FAIL);
				} catch (Exception e) {
					e.printStackTrace();
				}
				testReporter.log(LogStatus.SKIP,
						"Dependency test " + rowData.getDependencyTests() + " failed hence skipping this test.");
			}
		} else {
			logger.debug("Mandatory information like test name, host, api path or http method not provided.");
			try {
				updateTestStatus(rowData.getTestName(), row, FAIL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			testReporter.log(LogStatus.SKIP,
					"Mandatory information like test name, host, api path or http method not provided.");
		}
		reporter.endTest(testReporter);

	}
	
}
