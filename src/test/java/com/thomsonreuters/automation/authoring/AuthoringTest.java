/**
* The AuthoringTest program is an entry point for running Authoring API test cases.
* This class initializes app name, excel file path which are utilized by AbstractBase class.
* Also executes external test case and update the test status.   
*
* @author  Janardhan
* @version 1.0
* @since   2015-08-31 
*/
package com.thomsonreuters.automation.authoring;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.common.AbstractBase;
import com.thomsonreuters.automation.common.RowData;


public class AuthoringTest extends AbstractBase {

	/**
	 * This method is entry point for testing. Initializes excel file path and app name. 
	 * Calls runTests method for executing test cases specified in the excel file.  
	 * 
	 * @return 		Nothing
	 * @throws 		Exception
	 * 
	 */
	@Test
	public void authoringTest() throws Exception {
		testDataExcelPath = "src/test/test-data/AuthoringTestData.xlsx";
		appName = "1PAUTHORING";
		Thread.sleep(20000);
		runTests();
	}

	/**
	 * This method is entry point for testing on priority.  
	 * This method tests for comment length = 2500 or not and updates test status.
	 * 
	 * @return 		Nothing
	 * @throws 		Exception
	 * 
	 */
	@Test(priority = 1)
	public void testCommentMaxLength() throws Exception {
		logger.info("Entered Authoring testCommentMaxLength method...");
		appName = "1PAUTHORING";
		rowData = new RowData();
		rowData.setTestName("OPQA-682");
		rowData.setHost("1PAUTHORING");
		rowData.setDescription("Verify that create comment with Max length and verify comment count");
		rowData.setApiPath("/comments");
		rowData.setMethod("POST");
		rowData.setQueryString("");
		rowData.setHeaders("X-1P-User=(SYS_USER1)||Content-Type=application/json");
		rowData.setValidations("status=200||comments.userId=(SYS_USER1)||comments.targetId=468387744WOS1");
		rowData.setBody(
				"{\"targetType\":\"wos\",\"targetId\":\"468387744WOS1\",\"content\":\"Comment Max Length Test: As a researcher, I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge base, make connections to my peers and gain exposure to others in my field. As a researcher, I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge base, make connections to my peers and gain exposure to others in my field. As a researcher, I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge base, make connections to my peers and gain exposure to others in my field. As a researcher, I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge base, make connections to my peers and gain exposure to others in my field. As a researcher, I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge base, make connections to my peers and gain exposure to others in my field. As a researcher, I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge base, make connections to my peers and gain exposure to others in my field. As a researcher, I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge base, make connections to my peers and gain exposure to others in my field. As a researcher,>1500I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge base, make connections to my peers and gain exposure to others in my field. As a researcher, I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge base, make connections to my peers and gain exposure to others in my field. As a researcher, I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge base, make connections to my peers and gain exposure to others in my field. As a researcher, I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge base, make connections to my peers and gain exposure to others in my field. As a researcher, I want to interact with Thomson Reuters’ research content so that I can discover new data, contribute to the knowledge...! >2500\"}");
		rowData.setStore("comments.id");
		testReporter = reporter.startTest(rowData.getTestName(), rowData.getDescription()).assignCategory(appName);
		executeTest(rowData);
		reporter.endTest(testReporter);
		logger.info("Ennding Authoring testCommentMaxLength method...");
	}

	/**
	 * This method executes specified test case, validates the response, stores the response and update the test status.  
	 * 
	 * @return 		Nothing
	 * @throws 		Exception
	 * 
	 */
	private void executeTest(RowData rowData) throws Exception {
		Response response = null;
		String sheetName = "Authoring";
		String responseJson = null;
		String statusCode = null;
		boolean testSuccess = false;
		String validationString = null;

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
				try {
					response = getAPIResponce();
					validationString = replaceDynamicPlaceHolders(rowData.getValidations());
					responseJson = response.asString();
					statusCode = String.valueOf(response.getStatusCode());
					testSuccess = validateResponse(validationString, responseJson, statusCode);
					logger.info("Status code:" + statusCode);
					
					// Validate the response with expected data
					if (testSuccess) {
						JsonPath jsonPath = new JsonPath(responseJson);
						String content = jsonPath.getString("comments.content");
						int oldCount = Integer.parseInt(dataStore.get("OPQA-344_1_counterValue"));
						int newCount = Integer.parseInt(jsonPath.getString("size"));
						if ((content.substring(1, content.length() - 1)).length() == 2500 && newCount == oldCount + 1) {
							logger.info("Comment content was truncated to 2500 characters");
							logger.info("Comment size was increased ");
							testReporter.log(LogStatus.INFO, "Comment content was truncated to 2500 characters");
							testReporter.log(LogStatus.INFO, "Comment size was increased");
							testSuccess = true;
							storeDependentTestsData(responseJson, rowData.getStore(), rowData.getTestName());
							testReporter.log(LogStatus.PASS, "PASS");
						} else {// Added by Janardhan to log fail case
							logger.info("Comment is not truncated to 2500 characters! and Old comment count:" + oldCount
									+ " new count:" + newCount);
							logger.info("Response content::" + content);
							testReporter.log(LogStatus.ERROR, "Comment is not truncated to 2500 characters!");
							testReporter.log(LogStatus.FAIL, "Validation Failed");
						}
					} else {
						logger.info("Status code:" + statusCode);
						// testReporter.log(LogStatus.FAIL, "Error status code:"+statusCode);
					}
					// Save API response to file
					saveAPIResponse(responseJson, sheetName, rowData.getTestName());

					if (!testSuccess) {
						testReporter.log(LogStatus.FAIL, "FAIL");
						// throw new Exception("Validation Failed");
					}
					logger.info("End execution of test:" + rowData.getTestName());
					logger.info("-----------------------------------------------------------------------");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
