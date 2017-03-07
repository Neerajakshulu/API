package com.thomsonreuters.automation.profile;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The ProfileTest program is an entry point for running Profile API test cases. This class initializes app name, excel
 * file path which are utilized by AbstractBase class.
 *
 * @author Janardhan
 * @version 1.0
 * @since 2015-08-31
 */
public class ProfileTest extends AbstractBase {

	/**
	 * This method is entry point for testing. Initializes excel file path and app name. Calls runTests method for
	 * executing test cases specified in the excel file.
	 * 
	 * @return Nothing
	 * @throws Exception
	 * 
	 */
	@Test
	public void profileTest() throws Exception {
		testDataExcelPath = "src/test/test-data/ProfileTestData.xlsx";
		appName = "1PPROFILE";
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
				String profileBody = rowData.getBody();
				if (StringUtils.isNotBlank(profileBody)){
					StringTokenizer inputsTokenizer = new StringTokenizer(profileBody, TOKENIZER_DOUBLE_PIPE);
					while(inputsTokenizer.hasMoreTokens()){
						String inputToken = inputsTokenizer.nextToken();
						StringTokenizer equalTokenizer = new StringTokenizer(inputToken, TOKENIZER_EQUALTO);
						while(equalTokenizer.hasMoreTokens()){
							String keyToken = equalTokenizer.nextToken();
							if(equalTokenizer.hasMoreTokens()){
								String valueToken = equalTokenizer.nextToken();
								if(valueToken.contains(".json")){
									reqSpec.multiPart(keyToken, new File(valueToken), "application/json");
								}else if(keyToken.contains("imageType")){
									reqSpec.multiPart(keyToken, valueToken);
								}else {
									reqSpec.multiPart(keyToken, new File(valueToken));
								}
							}else{
								logger.info("Value is empty !! Please provide input for " + keyToken
										+ ". For Empty string check, provide \"\" and for null check, provide null");
							}
						}
						logger.info("token:: "+inputToken);
					}
				}
				response = reqSpec.when().put(url);
				/*
				response = reqSpec
						.multiPart("mediaEntity", new File("src/test/resources/media/updateMediaStreamProfile.json"), "application/json")
						.multiPart("mediaStream", new File(rowData.getBody()))
						.when().put(url);
						*/
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
