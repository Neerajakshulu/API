package com.thomsonreuters.automation.authoring;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.thomsonreuters.automation.common.AbstractBase;
import com.thomsonreuters.automation.common.RowData;

/**
 * Test the Authoring APIs
 */
public class AuthoringTest extends AbstractBase {

	@Test
	public void authoringTest() throws Exception {
		testDataExcelPath = "src/test/test-data/AuthoringTestData.xlsx";
		appName = "1PAUTHORING";
		runTests();
	}
	
	
	/*comments size which should be 2500 characters*/
	@Test(priority = 1)
	public void testCommentMaxLength() throws Exception {
		logger.info("Entered Authoring testCommentMaxLength method...");
		appName = "1PAUTHORING";
		rowData = new RowData();
		rowData.setTestName("S1_TC_ST1");
		rowData.setHost("1PAUTHORING");
		rowData.setDescription("Create comment and validate Max length");
		rowData.setApiPath("/comments");
		rowData.setMethod("POST");
		rowData.setHeaders("X-1P-User=(SYS_USER1)||Content-Type=application/json");
		rowData.setBody(	
				"{\"targetType\":\"TRRecord\",\"targetId\":\"456539938WOS1\",\"content\":\"Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length Comment test with max length\"}");
		rowData.setStore("comments.id");
		ExecuteTest(rowData);
		logger.info("Ennding Authoring testCommentMaxLength method...");
	}
	

	private void ExecuteTest(RowData rowData) throws Exception {
		Response response = null;
		String sheetName = "Authoring";
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

				if (rowData.getMethod().equalsIgnoreCase(POST)) {
					logger.debug("Entered into POST Method");

					// Call the Rest API and get the response
					response = reqSpec.when().post(url);

				}

				// response.then().log().all();
				responseJson = response.asString();
				statusCode = String.valueOf(response.getStatusCode());
				// Validate the response with expected data
//				testSuccess = testSummaryMaxLengthvalidateResponse(rowData.getValidations(), responseJson, statusCode);
				if(statusCode.equals("200")){
				JsonPath jsonPath = new JsonPath(responseJson);
				String content = jsonPath.getString("comments.content");
				int oldCount = Integer.parseInt(dataStore.get("S1_TC_T39_counterValue"));
				int newCount =Integer.parseInt(jsonPath.getString("size"));
				if ((content.substring(1, content.length()-1)).length() == 2500 && newCount == oldCount+1 ) {
					logger.info("Comment content was truncated to 2500 characters");
					logger.info("Comment size was increased ");
					testSuccess = true;
				}
				}
				// Save API response to file
				saveAPIResponse(responseJson, sheetName, rowData.getTestName());

				if (!testSuccess) {
					throw new Exception("Validation Failed");
				}else{
					storeDependentTestsData(responseJson, rowData.getStore(), rowData.getTestName());
				}

				logger.info("End execution of test:" + rowData.getTestName());
				logger.info("-----------------------------------------------------------------------");
			}
		}
	}
}
	
	
	
	

