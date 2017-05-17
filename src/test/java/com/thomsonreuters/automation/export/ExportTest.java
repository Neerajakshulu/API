package com.thomsonreuters.automation.export;

import org.testng.annotations.Test;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.common.AbstractBase;
import com.thomsonreuters.automation.common.RowData;

/**
 * The {@code ExportTest} class to test for the Export API's.
 *
 * @author Janardhan
 * 
 */
public class ExportTest extends AbstractBase {

	/**
	 * {@code ExportTest} method is the entry point to test 1PEXPORT API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * ExportTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PEXPORT tests
	 * @see Exception
	 * 
	 */
	//@Test
	public void ExportTest() throws Exception {
		testDataExcelPath = "src/test/test-data/ExportTestData.xlsx";
		appName = "1PEXPORT";
		runTests();
	}

	/*Test export image*/
	@Test(priority = 1)
	public void testExportImage() throws Exception {
		logger.info("Entered EXPORT testExportImage method...");
		appName = "1PEXPORT";
		rowData = new RowData();
		rowData.setTestName("OPQA-EXP");
		rowData.setHost("1PEXPORT");
		rowData.setDescription("Verify that image is exported for domain leaders filter in technology area using EXPORT API");
		rowData.setApiPath("/export/ipa-record/image");
		rowData.setMethod("POST");
		rowData.setQueryString("");
		rowData.setHeaders("Accept=image/png||X-1P-User=67355603-9d5f-4cf1-948e-d8cb350c371c||Accept-Encoding=gzip, deflate, br||Content-Type=application/json");
		rowData.setValidations("status=200");
		rowData.setBody("{\"areaSelector\":\".save-export-dashboard-render\",\"waitSelector\":\"#rendered-chart\",\"filters\":[],\"names\":[\"java\"],\"fileName\":\"Export_IPAnalytics_14Apr2017_12-06PM\",\"url\":\"/ipa/#/dashboard/technology/competitors?query=JAVA\"}");
		testReporter=reporter.startTest(rowData.getTestName(), rowData.getDescription()).assignCategory(appName);
		ExecuteTest(rowData);
		reporter.endTest(testReporter);
		logger.info("Ennding EXPORT testExportImage method...");
	}


	private void ExecuteTest(RowData rowData) throws Exception {
		Response response = null;
		String sheetName = "Authoring";
		String responseJson = null;
		String statusCode = null;
		boolean testSuccess = false;
		String validationString = null;

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
				try{
					response=getAPIResponce();
					validationString = replaceDynamicPlaceHolders(rowData.getValidations());
					responseJson = response.asString();
					statusCode = String.valueOf(response.getStatusCode());
					logger.info("status code:"+statusCode);
					//testSuccess = validateResponse(validationString, responseJson, statusCode);
					if(statusCode.equals("200")){
						testReporter.log(LogStatus.INFO, "Image is exported successfully.");
						testSuccess = true;
						storeDependentTestsData(responseJson, rowData.getStore(), rowData.getTestName());
						testReporter.log(LogStatus.PASS, "PASS" );

					}else{
						logger.info("Status code:"+statusCode);
						logger.error("Response content::"+responseJson);
						//testReporter.log(LogStatus.FAIL, "Error status code:"+statusCode);
					}
					// Save API response to file
					saveAPIResponse(responseJson, sheetName, rowData.getTestName());

					if (!testSuccess) {
						testReporter.log(LogStatus.FAIL, "FAIL");
						//throw new Exception("Validation Failed");
					}

					logger.info("End execution of test:" + rowData.getTestName());
					logger.info("-----------------------------------------------------------------------");
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

}

