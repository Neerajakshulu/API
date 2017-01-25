package com.thomsonreuters.automation.media;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.common.AbstractBase;
import com.thomsonreuters.automation.common.RowData;

/**
 * Test the Media APIs
 */
public class MediaTest extends AbstractBase {

	@Test(priority = 1)
	public void MediaTest() throws Exception {
		testDataExcelPath = "src/test/test-data/MediaTestData.xlsx";
		appName = "1PMEDIA";
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
		boolean isStreamTest = ((StringUtils.isNotBlank(rowData.getHeaders()))?(rowData.getHeaders().contains("multipart")?true:false):false);
		logger.debug("isStreamTest=" + isStreamTest);

		String url = appHosts.get(rowData.getHost()) + apiPath + queryString;
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
		if (!rowData.getMethod().equalsIgnoreCase(GET) && StringUtils.isNotBlank(bodyString) && !isStreamTest) {
			reqSpec.body(bodyString);
		}

		if (rowData.getMethod().equalsIgnoreCase(GET)) {
			logger.debug("Entered into GET Method");

			// Call the Rest API and get the response
			response = reqSpec.when().get(url);

		} else if (rowData.getMethod().equalsIgnoreCase(PUT)) {
			logger.debug("Entered into PUT Method");

			// Call the Rest API and get the response
			if(isStreamTest){
				response = reqSpec
						.multiPart("mediaEntity", new File("src/test/resources/media/updateMediaStreamProfile.json"), "application/json")
						.multiPart("mediaStream", new File(rowData.getBody()))
						.when().put(url);
			}else{
				response = reqSpec.when().put(url);
			}

		} else if (rowData.getMethod().equalsIgnoreCase(POST)) {
			logger.debug("Entered into POST Method");

			// Call the Rest API and get the response
			if(isStreamTest){
				response = reqSpec
						.multiPart("mediaEntity", new File("src/test/resources/media/createMediaStreamProfile.json"), "application/json")
						.multiPart("mediaStream", new File(rowData.getBody()))
						.when().post(url);
			}else{
				response = reqSpec.when().post(url);
			}

		} else if (rowData.getMethod().equalsIgnoreCase(DELETE)) {
			logger.debug("Entered into DELETE Method");

			// Call the Rest API and get the response
			response = reqSpec.when().delete(url);
		}
		return response;
	}
}
