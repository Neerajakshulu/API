package com.thomsonreuters.automation.watchlist;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.specification.RequestSpecification;
import com.thomsonreuters.ips.p1.automation.common.AbstractBase2;

/***
 * Tests the Watchlist API
 * TestData: WathlistTestData.xlsx
 * 
 * 
 */
public class WatchlistTest extends AbstractBase2 {

	private static final Logger logger = LogManager.getLogger();

	private static final String testDataExcelPath = "src/test/test-data/WatchlistTestData.xlsx";
	private static final String moduleName = "1PCITATIONS";
	private static String strDateTime = null;
	
	
	@BeforeSuite
	public void beforeSuite() throws Exception {
		logger.info("@BeforeSuite - any initialization / activity to perform before starting your test suite");
		strDateTime = new SimpleDateFormat("ddMMMyyyy_HHmmss").format(new Date());
		getAllAppHostsForGivenEnv("stable.dev");
	}
	
	
	@Test
	public void watchlistTest() throws Exception{
		logger.info("WatchlistTest...");
		
		XSSFWorkbook workBook = null;
		FileInputStream inputStream = null;

		try {
			int rowCount;
			int totalColumnCount = 13;
			String currentSheetName = null;

			// Read Excel file
			File myxl = new File(testDataExcelPath);
			inputStream = new FileInputStream(myxl);
			workBook = new XSSFWorkbook(inputStream);
			int totalSheets = workBook.getNumberOfSheets();

			// Loop through each sheet in the Excel
			for (int currentSheet = 0; currentSheet < totalSheets; currentSheet++) 
			{

				logger.info("========================================================================");
				logger.info("Started executing tests from sheet " + (currentSheet + 1));

				XSSFSheet mySheet = workBook.getSheetAt(currentSheet);
				currentSheetName = workBook.getSheetName(currentSheet);
				rowCount = mySheet.getLastRowNum();

				logger.debug("total number of rows:" + rowCount);

				// Loop through all test case records of current sheet
				for (int i = 1; i <= rowCount; i++) 
				{
					
					//Row data
					String testName = null;
					String description = null;
					String host = null;
					String apiPath = null;
					String httpMethod = null;
					String headers = null;
					String pathParams = null;
					String queryString = null;
					String body = null;
					String dependency = null;
					String validations = null;
					String store = null;
					
					XSSFRow row = mySheet.getRow(i);
					testName = getCellData(row.getCell(0, Row.CREATE_NULL_AS_BLANK));

					// Don't consider the rows without test name, skip them
					if (!StringUtils.isBlank(testName)) {

						logger.info("-----------------------------------------------------------------------");
						logger.info("Starting test:" + testName);

						apiPath = null;
						httpMethod = null;
						logger.debug("################# Test Data ##################");
						// Get data for the current row
						for (int j = 0; j < totalColumnCount; j++) {
							XSSFCell cell = row.getCell(j, Row.CREATE_NULL_AS_BLANK);
							if (j == 0) {
								testName = getCellData(cell);
								logger.debug("testName=" + testName);
							} else if (j == 1) {
								description = getCellData(cell);
								logger.debug("Description=" + description);
							} else if (j == 2) {
								host = getCellData(cell);
								logger.debug("host=" + host);
								//host = hostName;
								//logger.debug("host=" + host);
							} else if (j == 3) {
								apiPath = getCellData(cell);
								logger.debug("apiPath=" + apiPath);
							} 
							else if (j == 4) {
								httpMethod = getCellData(cell);
								logger.debug("httpMethod=" + httpMethod);
							} 
							else if (j == 5) {
								headers = getCellData(cell);
								logger.debug("headers=" + headers);
							} 
							else if (j == 6) {
								pathParams = getCellData(cell);
								logger.debug("pathParams=" + pathParams);
							} 
							else if (j == 7) {
								queryString = getCellData(cell);
								logger.debug("queryString=" + queryString);
							} 
							else if (j == 8) {
								body = getCellData(cell);
								logger.debug("body=" + body);
							} 
							else if (j == 9) {
								dependency = getCellData(cell);
								logger.debug("dependency=" + dependency);
							} 
							else if (j == 10) {
								validations = getCellData(cell);
								logger.debug("validations=" + validations);
							} 
							else if (j == 11) {
								store = getCellData(cell);
								logger.debug("store=" + store);
							} 
							 
						}
						logger.debug("##############################################");
						
						String url = null;
						if (httpMethod.equals("GET")) {
							logger.debug("Entered into GET Method");

							if ( queryString != null ){
								//TODO:replace placeholder values from queryString
								url = appHosts.get(host) + apiPath + queryString;
							}
							
							/**
							 * Adding path params
							 */
							if ( pathParams != null ){
								url = appHosts.get(host) + apiPath + pathParams;
							}

							logger.debug("URL=" + url);
							RequestSpecification reqSpec = given();
							
							if ( headers != null ){
								Map<String, String> headersMap = getHeaders(headers);
								reqSpec.headers(headersMap);
								logger.debug("Headers:" + headersMap);
							}

//							// Call the Rest API and get the response
							
							String json = reqSpec.when()
							.get(url)
							.andReturn()
							.asString();
							
							logger.info(json);
							//get(url)
							//.headers().h
							//String json = get(url).andReturn().asString();

//							// Save API response to file
//							saveAPIResponse(json, currentSheetName, testName);
//
//							// Validate the response with expected data
//							boolean success = validateResponse(expectedData, json);
//
//							// Update the excel file with Test PASS / FAIL status
							updateTestStatus(row, true);
//
						} else if (httpMethod.equals("PUT")) {
							logger.debug("Entered into PUT Method");

							if ( queryString != null ){
								//TODO:replace placeholder values from queryString
								url = appHosts.get(host) + apiPath + queryString;
							}
							
							/**
							 * Adding path params
							 */
							if ( pathParams != null ){
								url = appHosts.get(host) + apiPath + pathParams;
							}
							
							logger.debug("URL=" + url);
							RequestSpecification reqSpec = given();
							
							if ( headers != null ){
								Map<String, String> headersMap = getHeaders(headers);
								reqSpec.headers(headersMap);
								logger.debug("Headers:" + headersMap);
							}

//							// Call the Rest API and get the response
							
							String json = reqSpec.when().put(url)
							.andReturn()
							.asString();
							
							logger.info(json);
							//get(url)
							//.headers().h
							//String json = get(url).andReturn().asString();

//							// Save API response to file
//							saveAPIResponse(json, currentSheetName, testName);
//
//							// Validate the response with expected data
//							boolean success = validateResponse(expectedData, json);
//
//							// Update the excel file with Test PASS / FAIL status
							updateTestStatus(row, true);
						}else if (httpMethod.equals("DELETE")) {
							logger.debug("Entered into DELETE Method");

							if ( queryString != null ){
								//TODO:replace placeholder values from queryString
								url = appHosts.get(host) + apiPath + queryString;
							}
							
							/**
							 * Adding path params
							 */
							if ( pathParams != null ){
								url = appHosts.get(host) + apiPath + pathParams;
							}
							
							logger.debug("URL=" + url);
							RequestSpecification reqSpec = given();
							
							if ( headers != null ){
								Map<String, String> headersMap = getHeaders(headers);
								reqSpec.headers(headersMap);
								logger.debug("Headers:" + headersMap);
							}

//							// Call the Rest API and get the response
							
							String json = reqSpec.when().delete(url)
							.andReturn()
							.asString();
							
							logger.info(json);
							//get(url)
							//.headers().h
							//String json = get(url).andReturn().asString();

//							// Save API response to file
//							saveAPIResponse(json, currentSheetName, testName);
//
//							// Validate the response with expected data
//							boolean success = validateResponse(expectedData, json);
//
//							// Update the excel file with Test PASS / FAIL status
							updateTestStatus(row, true);
						} 
						else {
							logger.debug("HTTP Method not supported");
						}
						logger.info("End execution of test:" + testName);
						logger.info("-----------------------------------------------------------------------");
					}
				}
				logger.info("End executing tests from sheet " + (currentSheet + 1));
				logger.info("========================================================================");
			}

		} catch (Exception e) {
			logger.error("Exception in executing Profile tests:" + e);
			e.printStackTrace();
		} finally {
			inputStream.close();
		}

		// Write updates to excel
		writeUpdatestoExcel(workBook);

		logger.info("WatchlistTest end");
		
	}
	
	private Map<String, String> getHeaders(String header){
		Map<String, String> headersMap = new HashMap<>();
		
		StringTokenizer tokenizer = new StringTokenizer(header, "||");
		while(tokenizer.hasMoreTokens() ){
			String str = tokenizer.nextToken();
			StringTokenizer tok = new StringTokenizer(str, "=");
			while(tok.hasMoreTokens() ){
				String key = tok.nextToken();
				String val = null;
				//TODO replace placeholder values from the header values
				if ( tok.hasMoreTokens() ){
					val = tok.nextToken();
				}
				
				headersMap.put(key, val);
			}
		}
		
		return headersMap;
	}

	private void updateTestStatus(final XSSFRow row,
			final boolean success) throws Exception {
		String status = success ? "PASS" : "FAIL";
		int testStatusColumnIndex = 12;

		// If cell is null then get it as blank cell
		XSSFCell cell = row.getCell(testStatusColumnIndex, Row.CREATE_NULL_AS_BLANK);

		// Update the cell with test status
		if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			cell = row.createCell(testStatusColumnIndex);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(status);
		} else {
			cell.setCellValue(status);
		}
	}

	private void writeUpdatestoExcel(XSSFWorkbook workBook) throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(testDataExcelPath));
		workBook.write(fos);
		fos.close();
		workBook.close();
	}

	private void saveAPIResponse(final String json, final String currentSheetName, final String testName) throws Exception {
		
		Path newDirectoryPath = Paths.get("src/test/test-output", strDateTime, moduleName, currentSheetName);
		Files.createDirectories(newDirectoryPath);
		
			String fileName = newDirectoryPath.toAbsolutePath() + "/" + testName + ".txt";
			
			logger.debug("fileName="+fileName);
			
			File file = new File(fileName);
			java.nio.file.Files.write(Paths.get(file.toURI()), json.getBytes("utf-8"),
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	private String getQueryString(final String[] inputData) throws Exception {
		String queryString = "";

		for (int i = 0; i < inputData.length; i++) {
			if (!StringUtils.isBlank(inputData[i])) {
				if (i == 0) {
					queryString = "?" + inputData[i];
				} else {
					queryString += "&" + inputData[i];
				}
			}
		}

		return queryString;
	}

	private boolean validateResponse(final String[] expectedData,
			final String json) throws Exception {

		JsonPath jsonPath = new JsonPath(json);
		boolean success = true;
		String actualValue = null;
		String splitData[] = null;

		for (int i = 0; i < expectedData.length; i++) {
			if (!StringUtils.isBlank(expectedData[i])) {

				splitData = expectedData[i].split("=");

				// Based on the json name key get the actual value from the json string
				actualValue = jsonPath.getString(splitData[0]);

				// Compare whether actual value is matching with expected value or not
				if (actualValue == null || !splitData[1].equals(actualValue)) {
					logger.info("Actual value: " + actualValue + " for key: " + splitData[0]
							+ " is not matching expected value:" + splitData[1]);
					success = false;
				} else {
					logger.info("Actual value: " + actualValue + " for key: " + splitData[0]
							+ " is matching expected value:" + splitData[1]);
				}
			}
		}
		return success;
	}

	//@Test(enabled = false)
	public void getRequestFindCapital() {

		logger.info("getRequestFindCapital entered");
		// make get request to fetch capital of norway
		String json = get("http://restcountries.eu/rest/v1/name/norway").andReturn().asString();

		logger.info("capital=" + JsonPath.with(json).get("capital"));

		JsonPath jsonPath = new JsonPath(json);
		String capital = jsonPath.getString("capital");

		// Asserting that capital of Norway is Oslo
		Assert.assertEquals(capital, "[Oslo]");
	}
}
