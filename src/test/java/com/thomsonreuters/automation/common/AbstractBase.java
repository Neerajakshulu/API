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
import java.util.Iterator;
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
import org.apache.poi.util.SystemOutLogger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.report.ReportFactory;

/**
 * The {@code AbstractBase} class is the common setup class for all the test classes.
 *
 * The class {@code AbstractBase} is support to test RestFull web services.
 * 
 * @author Mohana Yalamarthi
 */

public abstract class AbstractBase {

	protected ExtentReports reporter = ReportFactory.getReporter();
	protected ExtentTest testReporter = null;
	protected static final Logger logger = LogManager.getLogger();
	private static final String EUREKA_APP_NAME = "name";
	private static final String EUREKA_HOST_NAME = "hostName";
	private static final String EUREKA_IP_ADDRESS = "ipAddr";
	private static final String EUREKA_SERVICE_STATUS = "status";
	private static final String EUREKA_HOST_PORT = "port";
	private static final String EUREKA_VIP_ADDRESS = "vipAddress";
	private static final String EUREKA_DC_NAME = "Amazon";
	private static final int TESTDATA_COLUMN_COUNT = 12;
	protected static final String GET = "GET";
	protected static final String POST = "POST";
	protected static final String PUT = "PUT";
	protected static final String DELETE = "DELETE";
	private static final String PASS = "PASS";
	protected static final String FAIL = "FAIL";
	protected static final String DEPENDENCY_FAIL = "DEPFAIL";
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
	private static final String USER_VAR = "SYS_USER";
	protected Map<String, String> appHosts = new HashMap<String, String>();
	protected Map<String, String> dataStore = new HashMap<String, String>();
	private Map<String, String> testStatus = new HashMap<String, String>();
	protected static final String TESTOUTPUT_FOLDER_DATEFORMAT = "ddMMMyyyy_HHmmss";
	protected static String strDateTime = null;
	protected String testDataExcelPath = null;
	protected String appName = null;
	protected RowData rowData = null;
	protected boolean isTestFail = false;
	protected String isTestFailDescroption = null;

	protected String testName = null;

	public void setUp() throws Exception {
	}

	/**
	 * Creates root directory to store all the test responses.
	 *
	 * @exception Exception On folder creation error
	 * @see Exception
	 */
	@BeforeSuite
	public void beforeSuite() throws Exception {
		strDateTime = new SimpleDateFormat(TESTOUTPUT_FOLDER_DATEFORMAT).format(new Date());
		TEST_OUTPUT_ROOT_FOLDER_PATH = Paths.get(TEST_OUTPUT_FOLDER_PATH, strDateTime);
		Files.createDirectories(TEST_OUTPUT_ROOT_FOLDER_PATH);
	}

	/**
	 * Support to stores the input parameters and Host/IP names.
	 *
	 * @exception Exception On storing parameters.
	 * @see Exception
	 */
	@BeforeClass
	public void beforeClass() throws Exception {
		logger.info("@BeforeSuite - any initialization / activity to perform before starting your test suite");

		String eurekaURL = System.getProperty("eurekaUrl");
		String envSuffix = System.getProperty("envSuffix");
		String local = System.getProperty("Local");
		String usersList = System.getProperty("sys_users");

		if (StringUtils.isNotBlank(usersList)) {
			String[] users = StringUtils.split(usersList, TOKENIZER_DOUBLE_PIPE);
			for (int i = 0; i < users.length; i++)
				dataStore.put(USER_VAR + String.valueOf(i + 1), users[i]);
		}

		logger.info("envSuffix = " + envSuffix);
		logger.info("Local = " + local);
		logger.info("Users = " + dataStore);
		getAllAppHostsForGivenEnv(eurekaURL, envSuffix, local);

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

	/**
	 * {@code getAPIResponce} method to build URL and calling rest assured API's and returns response object
	 * 
	 * @return Response json
	 */
	protected Response getAPIResponce() {
		Response response = null;
		String apiPath = replaceDynamicPlaceHolders(rowData.getApiPath());
		String headers = replaceDynamicPlaceHolders(rowData.getHeaders());
		String queryString = replaceDynamicPlaceHolders(rowData.getQueryString());
		String bodyString = replaceDynamicPlaceHolders(rowData.getBody());

		String url = appHosts.get(rowData.getHost()) + apiPath + queryString;
		logger.debug("URL=" + url);
		testReporter.log(LogStatus.INFO, "Request Method - " + rowData.getMethod());
		if (StringUtils.isNotBlank(headers))
			testReporter.log(LogStatus.INFO, "Request Headers - " + headers);
		if (StringUtils.isNotBlank(bodyString))
			testReporter.log(LogStatus.INFO, "Request Body - " + bodyString);
		testReporter.log(LogStatus.INFO, "Request URL - " + url);
		
	
		
		RequestSpecification reqSpec = given();
		/* for urlEncoding */
		 
		if(headers.trim().equals("content-type=application/x-www-form-urlencoded"))
		{
			 
			if (StringUtils.isNotBlank(bodyString))
				System.out.println("BodyString is ==>>"+bodyString);
			
			JSONObject jsonString = new JSONObject(bodyString.trim());
			Iterator<?> keys = jsonString.keys();

			while( keys.hasNext() ) {
			    String key = (String)keys.next();
			    String value=jsonString.get(key).toString();
			    
			    reqSpec.formParam(key, value);
				

			    }

		}
		
		 /* for urlEncoding */
		// Get and set headers to request
		if (StringUtils.isNotBlank(rowData.getHeaders())) {
			Map<String, String> headersMap = getHeaders(headers);
			reqSpec.headers(headersMap);
		}
		if (!rowData.getMethod().equalsIgnoreCase(GET) && StringUtils.isNotBlank(bodyString) && !headers.trim().equals("content-type=application/x-www-form-urlencoded")) {
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
		return response;
	}

	public void getTestName() {
		String[] testid = StringUtils.split(rowData.getTestName(), "_");
		if (testid.length > 1) {
			testName = "<a href=\"http://ent.jira.int.thomsonreuters.com/browse/" + testid[0] + "\" target=\"_blank\">"
					+ rowData.getTestName() + "</a>";
		} else {
			testName = "<a href=\"http://ent.jira.int.thomsonreuters.com/browse/" + rowData.getTestName()
					+ "\" target=\"_blank\">" + rowData.getTestName() + "</a>";
		}
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
			String sheetName = null;

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
					rowData = getRowData(row);
					getTestName();
					if (StringUtils.isNotBlank(rowData.getTestName())) {
						logger.debug("row data=" + rowData.toString());

						if (appHosts.get(rowData.getHost()) != null) {
							logger.debug("Real host=" + appHosts.get(rowData.getHost()));
							if ("1PNOTIFY".equalsIgnoreCase(rowData.getHost())) {
								Thread.sleep(15000);
							}
							try {
								process(row, sheetName);
							} catch (Exception e) {
								logger.error("Exception while executing the test: " + rowData.getTestName() + e);
								e.printStackTrace();
								testReporter.log(LogStatus.ERROR, e.toString());
								testReporter.log(LogStatus.FAIL, "Testcase Failed due to " + e.toString());
								reporter.endTest(testReporter);
								isTestFail = true;
								isTestFailDescroption = "Testcase Failed due to " + e.toString();
							}
						} else {
							testReporter = reporter.startTest(testName, rowData.getDescription())
									.assignCategory(appName);
							testReporter.log(LogStatus.FAIL, "Testcase failed due to service unavailable");
							reporter.endTest(testReporter);
							isTestFail = true;
							isTestFailDescroption = "Testcase failed due to service unavailable";
							updateTestStatus(rowData.getTestName(), row, FAIL);
							logger.info("Testcase failed due to service unavailable");
							logger.info("-----------------------------------------------------------------------");
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
		if (StringUtils.isNotBlank(isTestFailDescroption)) {
			Assert.assertFalse(isTestFail, isTestFailDescroption);
		}
		Assert.assertFalse(isTestFail, "One or more tests in " + appName + " failed");
		logger.info("End of processs method...");
	}

	/**
	 * Execute all the test cases defined in the excel sheet.
	 * 
	 * @throws Exception
	 */
	protected void runTests(String sheetName) throws Exception {
		logger.info("Entered the process method...");

		XSSFWorkbook workBook = null;
		FileInputStream inputStream = null;

		try {
			int sheetRowCount;
			XSSFSheet sheet = null;
			XSSFRow row = null;

			// Read Excel file
			File myxl = new File(testDataExcelPath);
			inputStream = new FileInputStream(myxl);
			workBook = new XSSFWorkbook(inputStream);
			// Loop through each sheet in the Excel
			// for (int currentSheet = 0; currentSheet < totalSheets; currentSheet++) {

			logger.info("========================================================================");
			// logger.info("Started executing tests from sheet " + (currentSheet + 1));

			// Get current sheet information
			sheet = workBook.getSheet(sheetName);
			// sheetName = workBook.getSheetName(currentSheet);
			sheetRowCount = sheet.getLastRowNum();

			logger.debug("total number of rows:" + sheetRowCount);

			// Loop through all test case records of current sheet, start
			// with 1 to leave header.
			for (int i = 1; i <= sheetRowCount; i++) {

				// Get current row information
				row = sheet.getRow(i);
				rowData = getRowData(row);
				getTestName();
				if (StringUtils.isNotBlank(rowData.getTestName())) {
					logger.debug("row data=" + rowData.toString());

					if (appHosts.get(rowData.getHost()) != null) {
						logger.debug("Real host=" + appHosts.get(rowData.getHost()));
						if ("1PNOTIFY".equalsIgnoreCase(rowData.getHost())
								|| "/authorize".equals(rowData.getApiPath())) {
							// Thread.sleep(25000);
						}
						try {
							process(row, sheetName);
						} catch (Exception e) {
							logger.error("Exception while executing the test: " + rowData.getTestName() + e);
							e.printStackTrace();
							testReporter.log(LogStatus.ERROR, e.toString());
							testReporter.log(LogStatus.FAIL, "Testcase Failed due to " + e.toString());
							reporter.endTest(testReporter);
							isTestFail = true;
							isTestFailDescroption = "Testcase Failed due to " + e.toString();
						}
					} else {
						testReporter = reporter.startTest(testName, rowData.getDescription()).assignCategory(appName);
						testReporter.log(LogStatus.FAIL, "Testcase failed due to service unavailable");
						reporter.endTest(testReporter);
						isTestFail = true;
						isTestFailDescroption = "Testcase failed due to service unavailable";
						updateTestStatus(rowData.getTestName(), row, FAIL);
						logger.info("Testcase failed due to service unavailable");
						logger.info("-----------------------------------------------------------------------");
					}

				}
			}
			// logger.info("End executing tests from sheet " + (currentSheet + 1));
			logger.info("========================================================================");
			// }

		} catch (Exception e) {
			logger.error("Exception while executing the tests:" + e);
			e.printStackTrace();
		} finally {
			inputStream.close();
		}

		// Write updates to excel
		writeUpdatestoExcel(workBook);
		if (StringUtils.isNotBlank(isTestFailDescroption)) {
			Assert.assertFalse(isTestFail, isTestFailDescroption);
		}
		Assert.assertFalse(isTestFail, "One or more tests in " + appName + " failed");
		logger.info("End of processs method...");
	}

	/**
	 * Checks whether current test dependency tests passed or not.
	 * 
	 * @param dependencyTests dependency tests
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
	 * @param method the method as configured in the test case
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
	 * This method replace the place holders in path, headers, query string and body with respective values captured
	 * from the previous tests.
	 * 
	 * @param stringToFormat string with place holders
	 * @return string after replacing the place holders
	 */
	protected String replaceDynamicPlaceHolders(String stringToFormat) {
		if (StringUtils.isNotBlank(stringToFormat)) {
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
		return stringToFormat;

	}

	/**
	 * As configured in the excel, this method stores required data from current test response
	 * 
	 * @param json current test response body
	 * @param jsonNameKeys json elements for which data to be stored
	 * @param testName current test name
	 */
	protected void storeDependentTestsData(String json,
			String jsonNameKeys,
			String testName) {
		if (testName.equals("OPQA-4135")) {
		if (StringUtils.isNotBlank(jsonNameKeys)) {
			StringTokenizer jsonNameKeysTokenizer = new StringTokenizer(jsonNameKeys, TOKENIZER_DOUBLE_PIPE);
			JsonPath jsonPath = new JsonPath(json);
			@SuppressWarnings("unused")
			String jsonNameKey = null;

			while (jsonNameKeysTokenizer.hasMoreTokens()) {
				jsonNameKey = jsonNameKeysTokenizer.nextToken();
				String value = jsonPath.getString("private.desc").replaceAll(REPLACE_SQURE_BRACKETS, EMPTY_STRING);
				String[] test = value.replaceAll(",", ":").split(":");
				for(int i=0;i<test.length;i=i+2) {
					dataStore.put(testName + UNDERSCORE + test[i].trim(), test[i+1].trim());
					
				}
				//dataStore.put(testName + UNDERSCORE + jsonNameKey, value);
			}
		}
		} else {
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
		}
		logger.debug("DataStore:" + dataStore);
	}

	/**
	 * Get configured header from excel and load to a map.
	 * 
	 * @param header as configured in excel
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
	 * @param isSuccess true means test pass else fail.
	 * @return pass / fail
	 */
	protected String getStatus(final boolean isSuccess) {
		String status = isSuccess ? PASS : FAIL;
		return status;
	}

	/**
	 * Updates the current test case status.
	 * 
	 * @param testName test case name
	 * @param row current test case row
	 * @param status test fail/pass status
	 * @throws Exception
	 */
	protected void updateTestStatus(final String testName,
			final XSSFRow row,
			final String status) throws Exception {

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
	 * @param workBook current excel from where tests run
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
	 * @param json response body
	 * @param currentSheetName current sheet from where test ran
	 * @param testName current test case name
	 * @throws Exception
	 */
	protected void saveAPIResponse(final String json,
			final String currentSheetName,
			final String testName) throws Exception {
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
	 * Validates the expected data provided in validations string with actual json data.
	 * 
	 * @param validations expected data
	 * @param json response body
	 * @param statusCode status code expecting
	 * @return validation success or failure
	 * @throws Exception
	 */
	protected boolean validateResponse(final String validations,
			final String json,
			final String statusCode) throws Exception {

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

						Integer output = 0;
						if (expectedValue.contains("eval$")) {
							expectedValue = expectedValue.replace("eval$", "");
							String[] s1 = null;
							if (expectedValue.contains("+")) {
								s1 = expectedValue.split("\\+");
								output = Integer.parseInt(s1[0]) + Integer.parseInt(s1[1]);
							} else if (expectedValue.contains("-")) {
								s1 = expectedValue.split("\\-");
								output = Integer.parseInt(s1[0]) - Integer.parseInt(s1[1]);
							}
							expectedValue = String.valueOf(output);
						}

						if (jsonNameKey.equalsIgnoreCase(STATUS)) {
							if (StringUtils.isBlank(expectedValue) || !expectedValue.equals(statusCode)) {
								if (statusCode.equals("500") && (rowData.getApiPath().equals("/comments")
										|| rowData.getApiPath().equals("/posts/"))) {
									Thread.sleep(1000);
								}
								logger.info("Actual status code: " + statusCode
										+ "is not matching expected status code value: " + expectedValue);

								testReporter.log(LogStatus.ERROR,
										"<font color=\"red\">Actual status code: " + statusCode
												+ "is not matching expected status code value: " + expectedValue
												+ "</font>");
								success = false;
								break;
							}
						} else if (expectedValue.equalsIgnoreCase(NOT_EMPTY)) {
							// Get actual value for the key from json string
							actualValue = jsonPath.getString(jsonNameKey);

							if (StringUtils.isBlank(actualValue)) {

								logger.info("Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is not matching expected value:" + expectedValue);
								testReporter.log(LogStatus.INFO,
										"<font color=\"red\">Actual value: " + actualValue + " for key: " + jsonNameKey
												+ " is not matching expected value:" + expectedValue + "</font>");
								success = false;
								break;
							}
						} else {
							// Get actual value for the key from json string
							actualValue = jsonPath.getString(jsonNameKey);

							// Compare whether actual value is matching with
							// expected value or not
							if (actualValue == null) {
								logger.info(" Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is not matching expected value:" + expectedValue);
								testReporter.log(LogStatus.INFO,
										"<font color=\"red\">Actual value: " + actualValue + " for key: " + jsonNameKey
												+ " is not matching expected value:" + expectedValue + "</font>");
								success = false;
								break;
							} else if (actualValue.startsWith("[")
									&& StringUtils.containsIgnoreCase(actualValue, expectedValue)) {
								// This scenario is when json value for the key
								// contains array of values
								logger.info("Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is matching expected value:" + expectedValue);
								testReporter.log(LogStatus.INFO, "Actual value: " + actualValue + " for key: "
										+ jsonNameKey + " is matching expected value:" + expectedValue);
								success = true;
								break;
							} else if (expectedValue.equals(actualValue)) {
								logger.info(" Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is matching expected value:" + expectedValue);
								testReporter.log(LogStatus.INFO, "Actual value: " + actualValue + " for key: "
										+ jsonNameKey + " is matching expected value:" + expectedValue);
								success = true;
								break;
							} else if (StringUtils.containsIgnoreCase(actualValue, expectedValue)) {
								logger.info(" Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is matching expected value:" + expectedValue);
								testReporter.log(LogStatus.INFO, "Actual value: " + actualValue + " for key: "
										+ jsonNameKey + " is matching expected value:" + expectedValue);
								success = true;
								break;
							} else if (expectedValue.trim().equalsIgnoreCase("\"\"")
									|| expectedValue.trim().equalsIgnoreCase("\'\'")) {// Added by Janardhan for Empty
								// Get actual value for the key from json string
								actualValue = jsonPath.getString(jsonNameKey);
								if (actualValue.isEmpty() || actualValue.trim().length() == 0) {
									logger.info("Actual value: " + actualValue + " for key: " + jsonNameKey
											+ " is matching expected value:" + expectedValue);
									testReporter.log(LogStatus.INFO, "Actual value: " + actualValue + " for key: "
											+ jsonNameKey + " is matching expected value:" + expectedValue);
									success = true;
									break;
								} else {
									logger.info("Actual value: " + actualValue + " for key: " + jsonNameKey
											+ " is not matching expected value:" + expectedValue);
									testReporter.log(LogStatus.INFO,
											"<font color=\"red\">Actual value: " + actualValue + " for key: "
													+ jsonNameKey + " is not matching expected value:" + expectedValue
													+ "</font>");
									success = false;
									break;
								}
							} else {
								System.out.println("Expected value:" + expectedValue);
								logger.info("Actual value: " + actualValue + " for key: " + jsonNameKey
										+ " is not matching expected value:" + expectedValue);
								testReporter.log(LogStatus.INFO,
										"<font color=\"red\">Actual value: " + actualValue + " for key: " + jsonNameKey
												+ " is not matching expected value:" + expectedValue + "</font>");
								success = false;
								break;
							}
						}
					} else {// Added by Janardhan for Empty string in the expected value
						logger.info("Expected value is empty !! Please provide input for " + jsonNameKey
								+ ". For Empty string check, provide \"\" and for null check, provide null ");
						testReporter.log(LogStatus.INFO,
								"<font color=\"red\">Expected value is empty !! Please provide input for " + jsonNameKey
										+ ". For Empty string check, provide \"\" and for null check, provide null "
										+ "</font>");
						success = false;
						break;
					}
				}
			}
		}

		return success;
	}

	/**
	 * Reads host name for the given application and environment
	 * 
	 * @param appName specific application name
	 * @param env environment for which the tests connect
	 * @throws Exception
	 */
	protected void getSpecificAppHostForGivenEnv(String appName,
			String eurekaURL,
			String env) throws Exception {
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
	 * Read host names across all applications for the given environment and load them to map.
	 * 
	 * @param env environment for which the tests connect
	 * @throws Exception
	 */
	protected void getAllAppHostsForGivenEnv(String eurekaURL,
			String env,
			String local) throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		String appName = null;
		String hostName = null;
		String port = null;
		boolean status = false; 

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
				if (startElement.getName().getLocalPart().equals(EUREKA_HOST_NAME) && local.equalsIgnoreCase("Y")) {
					event = eventReader.nextEvent();
					hostName = event.asCharacters().getData();
				}

				if (startElement.getName().getLocalPart().equals(EUREKA_IP_ADDRESS) && local.equalsIgnoreCase("N")) {
					event = eventReader.nextEvent();
					hostName = event.asCharacters().getData();
				}

				// Get service status
				if (startElement.getName().getLocalPart().equals(EUREKA_SERVICE_STATUS)) {
					event = eventReader.nextEvent();
					status = ((event.asCharacters().getData().equalsIgnoreCase("up"))?true:false);
				}
				
				// Get port
				if (startElement.getName().getLocalPart().equals(EUREKA_HOST_PORT)) {
					event = eventReader.nextEvent();
					port = event.asCharacters().getData();
				}

				// Get vip address
				if (startElement.getName().getLocalPart().equals(EUREKA_VIP_ADDRESS)) {
					event = eventReader.nextEvent();
					if (event.asCharacters().getData().endsWith(env) && (status == true)){
						appHosts.put(appName, HTTP + hostName + COLON + port);
						logger.debug("APPNAME:"+appName+" HOSTNAME:"+hostName+" is UP");
					}
				}

			}
		}
	}

	/**
	 * Capture current excel row data in an object and return
	 * 
	 * @param excel row
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
	 * @param cell excel cell
	 * @return the cell value
	 */
	protected String getCellData(XSSFCell cell) {
		int type = cell.getCellType();
		Object result;
		switch (type) {
			case Cell.CELL_TYPE_STRING:
				result = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				result = cell.getNumericCellValue();
				break;
			case Cell.CELL_TYPE_FORMULA:
				throw new RuntimeException("We can't evaluate formulas in Java");
			case Cell.CELL_TYPE_BLANK:
				result = EMPTY_STRING;
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				result = cell.getBooleanCellValue();
				break;
			case Cell.CELL_TYPE_ERROR:
				throw new RuntimeException("This cell has an error");
			default:
				throw new RuntimeException("We don't support this cell type: " + type);
		}
		return result.toString();
	}

	@AfterSuite
	public void afterSuite() {
		reporter.close();
	}

	public void addCategory() {

	}

}
