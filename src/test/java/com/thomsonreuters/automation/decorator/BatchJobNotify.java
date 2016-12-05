package com.thomsonreuters.automation.decorator;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.testng.annotations.Test;

import com.jayway.restassured.response.Response;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code NotifyBatchGet} class to test for the Decorator API's notifications.
 *
 * @author Ramesh L
 * 
 */
public class BatchJobNotify extends AbstractBase {
	
	/**
	 * This {@code notifyTest2} method is entry point to test 1PDECORATOR API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * DecoratorBatchJobTest_HPA.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PDECORATOR tests
	 * @see Exception
	 * 
	 */
	@Test
	public void notifyTest2() throws Exception {
		testDataExcelPath = "src/test/test-data/DecoratorBatchJobTest_HPA.xlsx";
		appName = "1PDECORATOR";
		runTests("Notification Test");
		
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
		String final_response = "";
		String statusCode = null;
		boolean testSuccess = false;
		/*
		 * If mandatory information like test case name, host, api path and valid http method are not provided then skip
		 * those tests and update the status as fail.
		 */
		if (rowData.getHost().equals("1PDECORATOR")) {
			if (StringUtils.isNotBlank(rowData.getTestName()) && StringUtils.isNotBlank(rowData.getHost())
					&& StringUtils.isNotBlank(rowData.getApiPath()) && isSupportedMethod(rowData.getMethod())) {
				testReporter = reporter.startTest(testName, rowData.getDescription()).assignCategory(appName);
				if (isDependencyTestsPassed(rowData.getDependencyTests())) {
					validationString = replaceDynamicPlaceHolders(rowData.getValidations());
					if (rowData.getHost().equals("1PDECORATOR")) {
						for (int i = 0; i < 25; i++) {
							response = getAPIResponce();
							responseJson = response.asString();
							statusCode = String.valueOf(response.getStatusCode());
							try {
								// Validate the response with expected data
								testSuccess = validateResponse(validationString, responseJson, statusCode);
								try {
									storeDependentTestsData(responseJson, rowData.getStore(), rowData.getTestName());
								} catch (Exception e) {
									break;
								}
								if (testSuccess) {
									break;
								} else {
									String body = "{\"queryRef\":\"notifyQrRef_V2\",\"params\":{\"size\":[\"20\"],\"byscore\":[\"true\"],\"before\":\"("
											+ rowData.getTestName() + "_notify.pitoId[19])\"}}";
									rowData.setBody(body);
								}
								final_response += responseJson;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						try {
							if (!testSuccess && responseJson != null) {
								logger.info("Response:" + responseJson);
							}

							// Update the excel file with Test PASS / FAIL status
							updateTestStatus(rowData.getTestName(), row, getStatus(testSuccess));

							// Save API response to file
							saveAPIResponse(final_response, sheetName, rowData.getTestName());

						} catch (Exception e) {
							e.printStackTrace();
						}
						if (testSuccess) {
							// Store the data which is required for subsequent test cases.
							storeDependentTestsData(responseJson, rowData.getStore(), rowData.getTestName());
							// Report status
							testReporter.log(LogStatus.PASS, "PASS");
						} else {
							isTestFail = true;
							// Report status
							testReporter.log(LogStatus.FAIL, "FAIL");
						}

					}

					logger.info("End execution of test:" + rowData.getTestName());
					logger.info("-----------------------------------------------------------------------");

				} else {
					logger.debug(
							"Dependency test " + rowData.getDependencyTests() + " failed hence skipping this test.");
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

		} else {
			super.process(row, sheetName);
		}
	}
}
