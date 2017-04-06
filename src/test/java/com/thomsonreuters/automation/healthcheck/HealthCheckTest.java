package com.thomsonreuters.automation.healthcheck;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The HealthCheckTest program is an entry point for running Health check API test cases for all the services. This class initializes app name,
 * excel file path which are utilized by AbstractBase class.
 *
 * @author Janardhan
 * @version 1.0
 * @since 2017-04-05
 */
public class HealthCheckTest extends AbstractBase {

	/**
	 * This method is entry point for testing. Initializes excel file path and app name. Calls runTests method for
	 * executing test cases specified in the excel file.
	 * 
	 * @return Nothing
	 * @throws Exception
	 * 
	 */
	@Test
	public void HealthCheckTestEntry() throws Exception {
		testDataExcelPath = "src/test/test-data/HealthCheckData.xlsx";
		appName = "1PHEALTHCHECK";
		runTests();
	}
	
	/**
	 * {@code getAPIResponce} method to build URL and calling rest assured API's and returns response object
	 * 
	 * @return Response json
	 */
	@Override
	protected Response getAPIResponce() {
		Response response = null;
		String apiPath = replaceDynamicPlaceHolders(rowData.getApiPath());
		String headers = replaceDynamicPlaceHolders(rowData.getHeaders());
		String queryString = replaceDynamicPlaceHolders(rowData.getQueryString());
		String bodyString = replaceDynamicPlaceHolders(rowData.getBody());

		String hosturl = appHosts.get(rowData.getHost());
		String healthcheckUrl = hosturl.replace(hosturl.substring(hosturl.lastIndexOf(":")), ":8077");
		String url = healthcheckUrl + apiPath + queryString;
		logger.debug("URL=" + url);
		testReporter.log(LogStatus.INFO, "Request Method - " + rowData.getMethod());
		if (StringUtils.isNotBlank(headers))
			testReporter.log(LogStatus.INFO, "Request Headers - " + headers);
		if (StringUtils.isNotBlank(bodyString))
			testReporter.log(LogStatus.INFO, "Request Body - " + bodyString);
		testReporter.log(LogStatus.INFO, "Request URL - " + url);

		RequestSpecification reqSpec = given();

		// Get and set headers to request
		if (StringUtils.isNotBlank(rowData.getHeaders())) {
			Map<String, String> headersMap = getHeaders(headers);
			reqSpec.headers(headersMap);
		}
		if (!rowData.getMethod().equalsIgnoreCase(GET) && StringUtils.isNotBlank(bodyString)) {
			reqSpec.body(bodyString);
		}

		if (rowData.getMethod().equalsIgnoreCase(GET)) {
			logger.debug("Entered into GET Method");

			// Call the Rest API and get the response
			response = reqSpec.when().get(url);

		}
		return response;
	}
}
