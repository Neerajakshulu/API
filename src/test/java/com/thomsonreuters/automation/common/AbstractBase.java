package com.thomsonreuters.automation.common;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * Common setup class for all the tests
 */
public abstract class AbstractBase {

	protected static final Logger logger = LogManager.getLogger();

	// private static final String EUREKA_URL =
	// "http://eureka.us-west-2.dev.oneplatform.build:8080/v2/apps";

	private static final String EUREKA_APP_NAME = "name";
	private static final String EUREKA_HOST_NAME = "hostName";
	private static final String EUREKA_IP_ADDRESS = "ipAddr";
	private static final String EUREKA_HOST_PORT = "port";
	private static final String EUREKA_VIP_ADDRESS = "vipAddress";
	private static final String EUREKA_DC_NAME = "Amazon";

	private static final int TESTDATA_COLUMN_COUNT = 12;

	protected static final String GET = "GET";
	protected static final String POST = "POST";
	protected static final String PUT = "PUT";
	protected static final String DELETE = "DELETE";
	private static final String PASS = "PASS";
	private static final String FAIL = "FAIL";
	private static final String DEPENDENCY_FAIL = "DEPFAIL";

	private static final String EMPTY_STRING = "";
	protected static final String TOKENIZER_DOUBLE_BACK_SLACH = "//";
	protected static final String TOKENIZER_DOUBLE_PIPE = "||";
	protected static final String TOKENIZER_EQUALTO = "=";
	private static final String UNDERSCORE = "_";
	private static final String COLON = ":";
	private static final String FORWARD_SLASH = "/";
	private static final String PLACEHOLDER_MATCHER_PATTERN = "\\((.*?)\\)";
	private static final String REPLACE_SQURE_BRACKETS = "[\\[\\]]";
	protected static final String TOKENIZER_DOUBLE_AMPERSAND = "&&";
	protected static final String PLACEHOLDER_MATCHER_PATTERN_VALIDATION = "\\{.*?}";

	private static final String HTTP = "http://";
	private static final String UTF8_ENCODING = "utf-8";
	private static final String TEXTFILE_EXT = ".txt";
	private static final String TEST_OUTPUT_FOLDER_PATH = "src/test/test-responses";
	private static Path TEST_OUTPUT_ROOT_FOLDER_PATH = null;
	protected static final String STATUS = "status";
	protected static final String NOT_EMPTY = "NOTEMPTY";

	protected Map<String, String> appHosts = new HashMap<String, String>();
	protected Map<String, String> dataStore = new HashMap<String, String>();
	private Map<String, String> testStatus = new HashMap<String, String>();

	protected static final String TESTOUTPUT_FOLDER_DATEFORMAT = "ddMMMyyyy_HHmmss";
	// protected static final String ENV = "stable.dev";
	protected String strDateTime = null;
	protected String testDataExcelPath = null;
	protected String appName = null;
	protected RowData rowData = null;

	protected boolean isTestFail = false;

	public void setUp() throws Exception {
	}

	@BeforeSuite
	public void beforeSuite() throws Exception {
		strDateTime = new SimpleDateFormat(TESTOUTPUT_FOLDER_DATEFORMAT).format(new Date());
		TEST_OUTPUT_ROOT_FOLDER_PATH = Paths.get(TEST_OUTPUT_FOLDER_PATH, strDateTime);
		Files.createDirectories(TEST_OUTPUT_ROOT_FOLDER_PATH);
	}

	@BeforeClass
	public void beforeClass() throws Exception {
		logger.info("@BeforeSuite - any initialization / activity to perform before starting your test suite");

		String eurekaURL = System.getProperty("eurekaUrl");
		String envSuffix = System.getProperty("envSuffix");
		String IP = System.getProperty("IP");
		String user1 = System.getProperty("sys_user1");
		String user2 = System.getProperty("sys_user2");
		dataStore.put("SYS_USER1", user1);
		dataStore.put("SYS_USER2", user2);

		logger.info("eurekaURL = " + eurekaURL);
		logger.info("envSuffix = " + envSuffix);
		logger.info("IP = " + IP);
		logger.info("SYS_USER1 = " + user1);
		logger.info("SYS_USER2 = " + user2);

		// strDateTime = new
		// SimpleDateFormat(TESTOUTPUT_FOLDER_DATEFORMAT).format(new Date());

		// This method get all the application host names for the given
		// environment
		getAllAppHostsForGivenEnv(eurekaURL, envSuffix, IP);

	}

	/**
	 * Execute all the test cases defined in the excel file.
	 * 
	 * @throws Exception
	 */
	protected void runTests() throws Exception {
		logger.info("Entered the process method...");

		XSSFWorkbook workBook = null;
		FileInputStream inputStream = null;

		try {
			int sheetRowCount;
			XSSFSheet sheet = null;
			XSSFRow row = null;
			// RowData rowData = null;
			Response response = null;
			String sheetName = null;
			String apiPath = null;
			String headers = null;
			String queryString = null;
			String validationString = null;
			String bodyString = null;
			String url = null;
			String responseJson = null;
			String statusCode = null;
			boolean testSuccess = false;

			// Read Excel file
			File myxl = new File(testDataExcelPath);
			inputStream = new FileInputStream(myxl);
			workBook = new XSSFWorkbook(inputStream);
			int totalSheets = workBook.getNumberOfSheets();

			// Loop through each sheet in the Excel
			for (int currentSheet = 0; currentSheet < totalSheets; currentSheet++) {

				logger.info("========================================================================");
				logger.info("Started executing tests from sheet " + (currentSheet + 1));

				// Get current sheet information
				sheet = workBook.getSheetAt(currentSheet);
				sheetName = workBook.getSheetName(currentSheet);
				sheetRowCount = sheet.getLastRowNum();

				logger.debug("total number of rows:" + sheetRowCount);

				// Loop through all test case records of current sheet, start
				// with 1 to leave header.
				for (int i = 1; i <= sheetRowCount; i++) {

					// Get current row information
					row = sheet.getRow(i);

					if (row != null) {

						rowData = getRowData(row);

						logger.debug("row data=" + rowData.toString());

						logger.debug("Real host=" + appHosts.get(rowData.getHost()));

						/*
						 * If mandatory information like test case name, host,
						 * api path and valid http method are not provided then
						 * skip those tests and update the status as fail.
						 */
						if (StringUtils.isNotBlank(rowData.getTestName()) && StringUtils.isNotBlank(rowData.getHost())
								&& StringUtils.isNotBlank(rowData.getApiPath())
								&& isSupportedMethod(rowData.getMethod())) {

							// If any of the dependency test failed then don't
							// proceed.
							if (isDependencyTestsPassed(rowData.getDependencyTests())) {

								logger.info("-----------------------------------------------------------------------");
								logger.info("Starting test:" + rowData.getTestName());

								apiPath = replaceDynamicPlaceHolders(rowData.getApiPath());
								headers = replaceDynamicPlaceHolders(rowData.getHeaders());
								queryString = replaceDynamicPlaceHolders(rowData.getQueryString());
								validationString = replaceDynamicPlaceHolders(rowData.getValidations());

								bodyString = replaceDynamicPlaceHolders(rowData.getBody());

								url = appHosts.get(rowData.getHost()) + apiPath + queryString;
								logger.debug("URL=" + url);

								RequestSpecification reqSpec = given();

								// Get and set headers to request
								if (StringUtils.isNotBlank(rowData.getHeaders())) {
									Map<String, String> headersMap = getHeaders(headers);
									reqSpec.headers(headersMap);
								}

								// Set body to request if the http method is not
								// GET.
								// if
								// (!rowData.getMethod().equalsIgnoreCase(GET)
								// && StringUtils.isNotBlank(rowData.getBody()))
								// {
								// reqSpec.body(rowData.getBody());
								// }

								if (!rowData.getMethod().equalsIgnoreCase(GET) && StringUtils.isNotBlank(bodyString)) {
									reqSpec.body(bodyString);
								}

								if (rowData.getMethod().equalsIgnoreCase(GET)) {
									logger.debug("Entered into GET Method");

									// Call the Rest API and get the response
									response = reqSpec.when().get(url);

								} else if (rowData.getMethod().equalsIgnoreCase(PUT)) {
									logger.debug("Entered into PUT Method");

									// Call the Rest API and get the response
									response = reqSpec.when().put(url);

								} else if (rowData.getMethod().equalsIgnoreCase(POST)) {
									logger.debug("Entered into POST Method");

									// Call the Rest API and get the response
									response = reqSpec.when().post(url);

								} else if (rowData.getMethod().equalsIgnoreCase(DELETE)) {
									logger.debug("Entered into DELETE Method");

									// Call the Rest API and get the response
									response = reqSpec.when().delete(url);
								}

								// response.then().log().all();
								responseJson = response.asString();
								statusCode = String.valueOf(response.getStatusCode());

								// Validate the response with expected data
								// testSuccess =
								// validateResponse(rowData.getValidations(),
								// responseJson, statusCode);
								testSuccess = validateResponse(validationString, responseJson, statusCode);

								// Update the excel file with Test PASS / FAIL
								// status
								updateTestStatus(rowData.getTestName(), row, getStatus(testSuccess));

								// Save API response to file
								saveAPIResponse(responseJson, sheetName, rowData.getTestName());

								if (testSuccess) {
									// Store the data which is required for
									// subsequent test cases.
									storeDependentTestsData(responseJson, rowData.getStore(), rowData.getTestName());
								} else {
									isTestFail = true;
								}

								logger.info("End execution of test:" + rowData.getTestName());
								logger.info("-----------------------------------------------------------------------");

							} else {
								logger.debug("Dependency tests failed hence skipping this test.");
								updateTestStatus(rowData.getTestName(), row, DEPENDENCY_FAIL);
							}
						} else {
							logger.debug(
									"Mandatory information like test name, host, api path or http method not provided.");
							updateTestStatus(rowData.getTestName(), row, FAIL);
						}
					}
				}
				logger.info("End executing tests from sheet " + (currentSheet + 1));
				logger.info("========================================================================");
			}

		} catch (Exception e) {
			logger.error("Exception while executing the tests:" + e);
			e.printStackTrace();
		} finally {
			inputStream.close();
		}

		// Write updates to excel
		writeUpdatestoExcel(workBook);
		Assert.assertFalse(isTestFail, "One or more tests in " + appName + " failed");
		logger.info("End of processs method...");
	}

	/**
	 * Checks whether current test dependency tests passed or not.
	 * 
	 * @param dependencyTests
	 *            dependency tests
	 * @return true if all the dependency tests passed else false
	 */
	protected boolean isDependencyTestsPassed(String dependencyTests) {
		if (StringUtils.isNotBlank(dependencyTests)) {
			StringTokenizer dependencyTestsTokenizer = new StringTokenizer(dependencyTests, TOKENIZER_DOUBLE_PIPE);
			String testCaseName = null;
			while (dependencyTestsTokenizer.hasMoreTokens()) {
				testCaseName = dependencyTestsTokenizer.nextToken();
				if (testStatus.get(testCaseName) == null || !testStatus.get(testCaseName).equals(PASS)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks for valid / supported methods.
	 * 
	 * @param method
	 *            the method as configured in the test case
	 * @return true means valid else invalid method
	 */
	protected boolean isSupportedMethod(String method) {
		if (method.equalsIgnoreCase(GET) || method.equalsIgnoreCase(PUT) || method.equalsIgnoreCase(POST)
				|| method.equalsIgnoreCase(DELETE))
			return true;
		else
			return false;
	}

	/**
	 * This method replace the place holders in path, headers, query string and
	 * body with respective values captured from the previous tests.
	 * 
	 * @param stringToFormat
	 *            string with place holders
	 * @return string after replacing the place holders
	 */
	protected String replaceDynamicPlaceHolders(String stringToFormat) {

		logger.debug("Before replace=" + stringToFormat);

		StringBuffer sb = new StringBuffer();
		String toReplace = null;

		if (StringUtils.isNotBlank(stringToFormat)) {

			Matcher matcher = Pattern.compile(PLACEHOLDER_MATCHER_PATTERN).matcher(stringToFormat);

			while (matcher.find()) {

				// What to replace
				toReplace = matcher.group(1);

				logger.debug("toReplace=" + toReplace);

				// Append replaced match.
				matcher.appendReplacement(sb, dataStore.get(toReplace));
			}
			matcher.appendTail(sb);
		}

		logger.debug("After replace=" + sb);

		return sb.toString();
	}

	/**
	 * As configured in the excel, this method stores required data from current
	 * test response
	 * 
	 * @param json
	 *            current test response body
	 * @param jsonNameKeys
	 *            json elements for which data to be stored
	 * @param testName
	 *            current test name
	 */
	protected void storeDependentTestsData(String json, String jsonNameKeys, String testName) {
		if (StringUtils.isNotBlank(jsonNameKeys)) {
			StringTokenizer jsonNameKeysTokenizer = new StringTokenizer(jsonNameKeys, TOKENIZER_DOUBLE_PIPE);
			JsonPath jsonPath = new JsonPath(json);
			String jsonNameKey = null;

			while (jsonNameKeysTokenizer.hasMoreTokens()) {
				jsonNameKey = jsonNameKeysTokenizer.nextToken();
				String value = jsonPath.getString(jsonNameKey).replaceAll(REPLACE_SQURE_BRACKETS, EMPTY_STRING);
				dataStore.put(testName + UNDERSCORE + jsonNameKey, value);
			}
		}
		logger.debug("DataStore:" + dataStore);
	}

	/**
	 * Get configured header from excel and load to a map.
	 * 
	 * @param header
	 *            as configured in excel
	 * @return map of headers
	 */
	protected Map<String, String> getHeaders(final String header) {
		Map<String, String> headersMap = new HashMap<>();

		StringTokenizer firstTokenizer = new StringTokenizer(header, TOKENIZER_DOUBLE_PIPE);
		String headerToken;
		String key;

		while (firstTokenizer.hasMoreTokens()) {
			headerToken = firstTokenizer.nextToken();
			StringTokenizer secondTokenizer = new StringTokenizer(headerToken, TOKENIZER_EQUALTO);
			while (secondTokenizer.hasMoreTokens()) {
				key = secondTokenizer.nextToken();

				// Get next token to get value
				if (secondTokenizer.hasMoreTokens()) {
					// Add header key and value to map.
					headersMap.put(key, secondTokenizer.nextToken());
				}
			}
		}

		return headersMap;
	}

	/**
	 * Utility method which return pass / fail based on boolean.
	 * 
	 * @param isSuccess
	 *            true means test pass else fail.
	 * @return pass / fail
	 */
	protected String getStatus(final boolean isSuccess) {
		String status = isSuccess ? PASS : FAIL;
		return status;
	}

	/**
	 * Updates the current test case status.
	 * 
	 * @param testName
	 *            test case name
	 * @param row
	 *            current test case row
	 * @param status
	 *            test fail/pass status
	 * @throws Exception
	 */
	protected void updateTestStatus(final String testName, final XSSFRow row, final String status) throws Exception {

		// Maintain test status in this map, so that dependent tests will use
		// this data to run or not.
		testStatus.put(testName, status);

		// If cell is null then get it as blank cell
		XSSFCell cell = row.getCell(TESTDATA_COLUMN_COUNT - 1, Row.CREATE_NULL_AS_BLANK);

		// Update the cell with test status
		if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			cell = row.createCell(TESTDATA_COLUMN_COUNT - 1);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(status);
		} else {
			cell.setCellValue(status);
		}
	}

	/**
	 * Write and commit the changes to excel file.
	 * 
	 * @param workBook
	 *            current excel from where tests run
	 * @throws Exception
	 */
	protected void writeUpdatestoExcel(XSSFWorkbook workBook) throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(testDataExcelPath));
		workBook.write(fos);
		fos.close();
		workBook.close();
	}

	/**
	 * Saves the response from each test/api call to a file.
	 * 
	 * @param json
	 *            response body
	 * @param currentSheetName
	 *            current sheet from where test ran
	 * @param testName
	 *            current test case name
	 * @throws Exception
	 */
	protected void saveAPIResponse(final String json, final String currentSheetName, final String testName)
			throws Exception {
		// Path newDirectoryPath =
		// Paths.get(TEST_OUTPUT_FOLDER_PATH,strDateTime, appName,
		// currentSheetName);
		Path newDirectoryPath = Paths.get(TEST_OUTPUT_ROOT_FOLDER_PATH.toString(), appName, currentSheetName);
		Files.createDirectories(newDirectoryPath);
		String fileName = newDirectoryPath.toAbsolutePath() + FORWARD_SLASH + testName + TEXTFILE_EXT;

		logger.debug("fileName=" + fileName);

		File file = new File(fileName);
		java.nio.file.Files.write(Paths.get(file.toURI()), json.getBytes(UTF8_ENCODING), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	/**
	 * Validates the expected data provided in validations string with actual
	 * json data.
	 * 
	 * @param validations
	 *            expected data
	 * @param json
	 *            response body
	 * @param statusCode
	 *            status code expecting
	 * @return validation success or failure
	 * @throws Exception
	 */
	protected boolean validateResponse(final String validations, final String json, final String statusCode)
			throws Exception {

		JsonPath jsonPath = new JsonPath(json);
		boolean success = true;

		String validationsToken = null;
		String jsonNameKey = null;
		String expectedValue = null;
		String actualValue = null;

		if (StringUtils.isNotBlank(validations)) {
			StringTokenizer validationsTokenizer = new StringTokenizer(validations, TOKENIZER_DOUBLE_PIPE);

			while (validationsTokenizer.hasMoreTokens()) {
				if (success == false)
					break;
				validationsToken = validationsTokenizer.nextToken();
				StringTokenizer validationTokenizer = new StringTokenizer(validationsToken, TOKENIZER_EQUALTO);
				while (validationTokenizer.hasMoreTokens()) {
					jsonNameKey = validationTokenizer.nextToken();

					// Get next token to get value
					if (validationTokenizer.hasMoreTokens()) {
						expectedValue = validationTokenizer.nextToken();

						if (jsonNameKey.equalsIgnoreCase(STATUS)) {
							if (StringUtils.isBlank(expectedValue) || !expectedValue.equals(statusCode)) {

								logger.info("Actual status code: " + statusCode
										+ "is not matching expected status code value: " + expectedValue);
								success = false;
								break;
							}
						} else if (expectedValue.equalsIgnoreCase(NOT_EMPTY)) {
							// Get actual value for the key from json string
							actualValue = jsonPath.getString(jsonNameKey);

							if (StringUtils.isBlank(actualValue)) {

								logger.info("Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is not matching expected value:" + expectedValue);
								success = false;
								break;
							}
						} else {
							// Get actual value for the key from json string
							actualValue = jsonPath.getString(jsonNameKey);

							// Compare whether actual value is matching with
							// expected value or not
							if (actualValue == null) {
								logger.info("Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is not matching expected value:" + expectedValue);
								success = false;
								break;
							}
							// else if (actualValue.startsWith("[") &&
							// (actualValue.contains(expectedValue)||StringUtils.containsIgnoreCase(actualValue,
							// expectedValue))) {
							else if (actualValue.startsWith("[")
									&& StringUtils.containsIgnoreCase(actualValue, expectedValue)) {
								// This scenario is when json value for the key
								// contains array of values

								logger.info("Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is matching expected value:" + expectedValue);
								success = true;
								break;
							} else if (expectedValue.equals(actualValue)) {

								logger.info("Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is matching expected value:" + expectedValue);
								success = true;
								break;
							} else if (StringUtils.containsIgnoreCase(actualValue, expectedValue)) {

								logger.info("Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is matching expected value:" + expectedValue);
								success = true;
								break;
							} else {
								logger.info("Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is not matching expected value:" + expectedValue);
								success = false;
								break;
							}
						}
					}
				}
			}
		}

		return success;
	}

	/**
	 * Reads host name for the given application and environment
	 * 
	 * @param appName
	 *            specific application name
	 * @param env
	 *            environment for which the tests connect
	 * @throws Exception
	 */
	protected void getSpecificAppHostForGivenEnv(String appName, String eurekaURL, String env) throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		String hostName = null;
		String port = null;
		String vipAddress = null;
		boolean appFound = false;

		URL url = new URL(eurekaURL);
		URLConnection conn = url.openConnection();

		XMLEventReader eventReader = inputFactory.createXMLEventReader(conn.getInputStream());

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			// reach the start of an item
			if (event.isStartElement()) {

				StartElement startElement = event.asStartElement();

				// if provided app name encountered then make appFound as true
				if (startElement.getName().getLocalPart().equals(EUREKA_APP_NAME)) {
					event = eventReader.nextEvent();
					if (event.asCharacters().getData().equals(appName)) {
						appFound = true;
					}
				}

				// when appFound is true then get host name and port of that
				// app.
				if (appFound == true) {
					if (startElement.getName().getLocalPart().equals(EUREKA_HOST_NAME)) {
						event = eventReader.nextEvent();
						hostName = event.asCharacters().getData();
						logger.debug("hostName=" + hostName);
					}

					if (startElement.getName().getLocalPart().equals(EUREKA_HOST_PORT)) {
						event = eventReader.nextEvent();
						port = event.asCharacters().getData();
						logger.debug("port=" + port);
					}

					if (startElement.getName().getLocalPart().equals(EUREKA_VIP_ADDRESS)) {
						event = eventReader.nextEvent();
						vipAddress = event.asCharacters().getData();
						if (vipAddress.endsWith(env))
							break;
					}
				}

			}
		}

		logger.debug("hostName=" + hostName + " port=" + port);

	}

	/**
	 * Read host names across all applications for the given environment and
	 * load them to map.
	 * 
	 * @param env
	 *            environment for which the tests connect
	 * @throws Exception
	 */
	protected void getAllAppHostsForGivenEnv(String eurekaURL, String env, String IP) throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		String appName = null;
		String hostName = null;
		String port = null;

		URL url = new URL(eurekaURL);
		URLConnection conn = url.openConnection();

		XMLEventReader eventReader = inputFactory.createXMLEventReader(conn.getInputStream());

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			// reach the start of an item
			if (event.isStartElement()) {

				StartElement startElement = event.asStartElement();

				// Get app name
				if (startElement.getName().getLocalPart().equals(EUREKA_APP_NAME)) {
					event = eventReader.nextEvent();
					if (!event.asCharacters().getData().equalsIgnoreCase(EUREKA_DC_NAME))
						appName = event.asCharacters().getData();
				}

				// Get IP Address
				if (startElement.getName().getLocalPart().equals(EUREKA_HOST_NAME) && IP.equalsIgnoreCase("N")) {
					event = eventReader.nextEvent();
					hostName = event.asCharacters().getData();
				}

				if (startElement.getName().getLocalPart().equals(EUREKA_IP_ADDRESS) && IP.equalsIgnoreCase("Y")) {
					event = eventReader.nextEvent();
					hostName = event.asCharacters().getData();
				}

				// Get port
				if (startElement.getName().getLocalPart().equals(EUREKA_HOST_PORT)) {
					event = eventReader.nextEvent();
					port = event.asCharacters().getData();
				}

				// Get vip address
				if (startElement.getName().getLocalPart().equals(EUREKA_VIP_ADDRESS)) {
					event = eventReader.nextEvent();
					if (event.asCharacters().getData().endsWith(env))
						appHosts.put(appName, HTTP + hostName + COLON + port);
				}

			}
		}
	}

	/**
	 * Capture current excel row data in an object and return
	 * 
	 * @param excel
	 *            row
	 * @return RowData object contains excel row data
	 * @throws Exception
	 */
	protected RowData getRowData(XSSFRow row) throws Exception {

		RowData rowData = new RowData();
		String currentCellData = null;

		for (int currentCell = 0; currentCell < TESTDATA_COLUMN_COUNT; currentCell++) {

			currentCellData = getCellData(row.getCell(currentCell, Row.CREATE_NULL_AS_BLANK));

			switch (currentCell) {
			case 0:
				rowData.setTestName(currentCellData);
			case 1:
				rowData.setDescription(currentCellData);
			case 2:
				rowData.setHost(currentCellData);
			case 3:
				rowData.setApiPath(currentCellData);
			case 4:
				rowData.setMethod(currentCellData);
			case 5:
				rowData.setHeaders(currentCellData);
			case 6:
				rowData.setQueryString(currentCellData);
			case 7:
				rowData.setBody(currentCellData);
			case 8:
				rowData.setDependencyTests(currentCellData);
			case 9:
				rowData.setValidations(currentCellData);
			case 10:
				rowData.setStore(currentCellData);
			case 11:
				rowData.setStatus(currentCellData);
			}
		}

		return rowData;
	}

	/**
	 * This function will convert an object of type excel cell to a string value
	 * 
	 * @param cell
	 *            excel cell
	 * @return the cell value
	 */
	protected String getCellData(XSSFCell cell) {
		int type = cell.getCellType();
		Object result;
		switch (type) {
		case XSSFCell.CELL_TYPE_STRING:
			result = cell.getStringCellValue();
			break;
		case XSSFCell.CELL_TYPE_NUMERIC:
			result = cell.getNumericCellValue();
			break;
		case XSSFCell.CELL_TYPE_FORMULA:
			throw new RuntimeException("We can't evaluate formulas in Java");
		case XSSFCell.CELL_TYPE_BLANK:
			result = EMPTY_STRING;
			break;
		case XSSFCell.CELL_TYPE_BOOLEAN:
			result = cell.getBooleanCellValue();
			break;
		case XSSFCell.CELL_TYPE_ERROR:
			throw new RuntimeException("This cell has an error");
		default:
			throw new RuntimeException("We don't support this cell type: " + type);
		}
		return result.toString();
	}
}
