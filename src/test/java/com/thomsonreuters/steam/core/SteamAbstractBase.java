package com.thomsonreuters.steam.core;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.xml.XmlPath.from;
import static com.jayway.restassured.path.xml.XmlPath.with;
import static com.jayway.restassured.specification.ProxySpecification.host;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jayway.restassured.path.xml.element.Node;
import com.jayway.restassured.response.Response;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.thomsonreuters.automation.common.RowData;
import com.thomsonreuters.automation.report.ReportFactory;

/**
 * Common setup class for all the tests
 */
public abstract class SteamAbstractBase {

	protected ExtentReports reporter = ReportFactory.getReporter();
	protected ExtentTest testReporter = null;
	protected static final Logger logger = LogManager.getLogger();
	private static final String EUREKA_APP_NAME = "name";
	private static final String EUREKA_HOST_NAME = "hostName";
	private static final String EUREKA_IP_ADDRESS = "ipAddr";
	private static final String EUREKA_HOST_PORT = "port";
	private static final String EUREKA_VIP_ADDRESS = "vipAddress";
	private static final String EUREKA_DC_NAME = "Amazon";
	private static final int TESTDATA_COLUMN_COUNT = 13;
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
	protected static final String TOKENIZER_DOUBLE_AMPERSAND = "&&";
	protected static final String PLACEHOLDER_MATCHER_PATTERN_VALIDATION = "\\{.*?}";
	private static final String HTTP = "http://";
	private static final String UTF8_ENCODING = "utf-8";
	private static final String TEST_OUTPUT_FOLDER_PATH = "src/test/test-responses";
	private static Path TEST_OUTPUT_ROOT_FOLDER_PATH = null;
	protected static final String STATUS = "status";
	protected static final String NOT_EMPTY = "NOTEMPTY";
	private static final String USER_VAR = "SYS_USER";
	protected Map<String, String> appHosts = new HashMap<String, String>();
	protected Map<String, String> dataStore = new HashMap<String, String>();
	private Map<String, String> testStatus = new HashMap<String, String>();
	protected static final String TESTOUTPUT_FOLDER_DATEFORMAT = "ddMMMyyyy_HHmmss";
	protected String strDateTime = null;
	protected String testDataExcelPath = null;
	protected String appName = null;
	protected RowData rowData = null;
	protected boolean isTestFail = false;
	protected String isTestFailDescroption = null;
	// For STEAM
	private static String steamURL = "http://10.205.140.204:5000/esti/xrpc";
	protected static String SID = "";
	protected String templatePath = null;
	private static final String XMLFILE_EXT = ".xml";
	private static final String XML_REQUEST_PLACEHOLDER_MATCHER_PATTERN = "\\{\\{(.*?)}\\}";
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder;
	protected static String local = "";

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
		local = System.getProperty("Local");
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
		// Get Admin SID
		SID = AdminLogin.login();
		logger.info("Admin SID = " + SID);
		// store SID in datastore
		dataStore.put("SID", SID);
	}

	protected void process(XSSFRow row,
			String sheetName) {
		// RowData rowData = null;
		Response response = null;
		String validationString = null;
		String responseXML = null;
		String statusCode = null;
		boolean testSuccess = false;
		/*
		 * If mandatory information like test case name, host, api path and valid http method are not provided then skip
		 * those tests and update the status as fail.
		 */
		if (StringUtils.isNotBlank(rowData.getTestName()) && StringUtils.isNotBlank(rowData.getHost())
				&& StringUtils.isNotBlank(rowData.getApiPath()) && isSupportedMethod(rowData.getMethod())) {
			testReporter = reporter.startTest(rowData.getTestName(), rowData.getDescription()).assignCategory(appName);
			if (isDependencyTestsPassed(rowData.getDependencyTests())) {
				validationString = replaceDynamicPlaceHolders(rowData.getValidations());
				response = getAPIResponse();
				responseXML = response.thenReturn().asString();
				statusCode = String.valueOf(response.getStatusCode());
				// logger.debug("RESPONSE CODE::"+statusCode);

				try {
					// Save API response to file
					saveAPIResponse(responseXML, sheetName, rowData.getTestName());

					// Validate the response with expected data
					testSuccess = validateResponse(validationString, responseXML, statusCode);

					// Update the excel file with Test PASS / FAIL status
					updateTestStatus(rowData.getTestName(), row, getStatus(testSuccess));

				} catch (Exception e) {
					e.printStackTrace();
				}

				if (testSuccess) {
					// Store the data which is required for subsequent test cases.
					storeDependentTestsData(responseXML, rowData.getStore(), rowData.getTestName());
					// Report status
					testReporter.log(LogStatus.PASS, "PASS");
				} else {
					isTestFail = true;
					// Report status
					testReporter.log(LogStatus.FAIL, "FAIL");
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

	protected Response getAPIResponse() {
		Response response = null;
		// String apiPath = replaceDynamicPlaceHolders(rowData.getApiPath());
		// String headers = replaceDynamicPlaceHolders(rowData.getHeaders());
		// String queryString = replaceDynamicPlaceHolders(rowData.getQueryString());
		String bodyString = replaceDynamicPlaceHolders(rowData.getBody());
		String completeTemplatePath = templatePath + rowData.getTemplateName() + ".xml";
		// logger.debug("XML TEMPLATE PATH:"+completeTemplatePath);
		// Store body params into map
		Map<String, String> bodyParams = new HashMap<String, String>();
		StringTokenizer bodyTokenizer = new StringTokenizer(bodyString, TOKENIZER_DOUBLE_PIPE);
		String paramToken = null;
		String keyParam = null;
		String valueParam = null;
		// assign SID to map
		bodyParams.put("SID", SID);
		while (bodyTokenizer.hasMoreTokens()) {
			paramToken = bodyTokenizer.nextToken();
			StringTokenizer paramTokenizer = new StringTokenizer(paramToken, TOKENIZER_EQUALTO);
			while (paramTokenizer.hasMoreTokens()) {
				keyParam = paramTokenizer.nextToken();
				// Get next token to get value
				if (paramTokenizer.hasMoreTokens()) {
					valueParam = paramTokenizer.nextToken();
					// store into map
					bodyParams.put(keyParam, valueParam);
					// logger.debug("Key: "+keyParam+" value: "+valueParam);
				} else {
					logger.info("Expected value is empty !! Please provide input for " + keyParam
							+ ". For Empty string check, provide \"\" and for null check, provide null ");
					break;
				}
			}
		}
		// convert xml data to string format
		String stringxml = convertXMLToString(completeTemplatePath);
		// Update dynamic place holders in template xml file
		String updatedPlaceHolder = replaceDynamicPlaceHolders(stringxml);
		// update template with body param values
		String updatedXML = updateTemplate(updatedPlaceHolder, bodyParams);
		// String url = appHosts.get(rowData.getHost()) + apiPath + queryString;
		// Modify url specific to steam format
		// url=url+"&sid="+SID+rowData.getQueryString()+"&request="+updatedXML;
		logger.debug("URL=" + steamURL);
		// local is Y <=> this block to be executed Locally
		if (local.equalsIgnoreCase("Y")) {
			response = given().body(updatedXML).when().post(steamURL);
		} else {// execute in Jenkins with proxy setup
			response = given().proxy(host("squid.oneplatform.build").withPort(3128)).body(updatedXML).when()
					.post(steamURL);
		}
		/*
		 * // Get and set headers to request if (StringUtils.isNotBlank(rowData.getHeaders())) { Map<String, String>
		 * headersMap = getHeaders(headers); reqSpec.headers(headersMap); } if
		 * (!rowData.getMethod().equalsIgnoreCase(GET) && StringUtils.isNotBlank(bodyString)) { //Steam XRPC doesn't
		 * accept body //reqSpec.body(bodyString); } if (rowData.getMethod().equalsIgnoreCase(GET)) { logger.debug(
		 * "Entered into GET Method"); // Call the Rest API and get the response response = reqSpec.when().get(url); }
		 * else if (rowData.getMethod().equalsIgnoreCase(PUT)) { logger.debug("Entered into PUT Method"); // Call the
		 * Rest API and get the response response = reqSpec.when().put(url); } else if
		 * (rowData.getMethod().equalsIgnoreCase(POST)) { logger.debug("Entered into POST Method"); // Call the Rest API
		 * and get the response response = reqSpec.when().post(url); } else if
		 * (rowData.getMethod().equalsIgnoreCase(DELETE)) { logger.debug("Entered into DELETE Method"); // Call the Rest
		 * API and get the response response = reqSpec.when().delete(url); }
		 */
		return response;
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
					if (StringUtils.isNotBlank(rowData.getTestName())) {
						logger.debug("row data=" + rowData.toString());

						if (appHosts.get(rowData.getHost()) != null) {
							logger.debug("Real host=" + appHosts.get(rowData.getHost()));

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
							testReporter = reporter.startTest(rowData.getTestName(), rowData.getDescription())
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
	 * @param xmlresponse current test response body
	 * @param xmlNameKeys json elements for which data to be stored
	 * @param testName current test name
	 */
	protected void storeDependentTestsData(String xmlresponse,
			String xmlNameKeys,
			String testName) {
		if (StringUtils.isNotBlank(xmlNameKeys)) {
			StringTokenizer xmlNameKeysTokenizer = new StringTokenizer(xmlNameKeys, TOKENIZER_DOUBLE_PIPE);
			// JsonPath jsonPath = new JsonPath(xmlresponse);
			String xmlNameKey = null;

			while (xmlNameKeysTokenizer.hasMoreTokens()) {
				xmlNameKey = xmlNameKeysTokenizer.nextToken();
				// String value = jsonPath.getString(xmlNameKey).replaceAll(REPLACE_SQURE_BRACKETS, EMPTY_STRING);
				String value = getXMLNodeValue(xmlresponse, xmlNameKey);
				// logger.debug("Storing token key::"+xmlNameKey+" value::"+value+" in Data store!");
				dataStore.put(testName + UNDERSCORE + xmlNameKey, value);
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
	 * @param xml response body
	 * @param currentSheetName current sheet from where test ran
	 * @param testName current test case name
	 * @throws Exception
	 */
	protected void saveAPIResponse(final String xml,
			final String currentSheetName,
			final String testName) throws Exception {

		Path newDirectoryPath = Paths.get(TEST_OUTPUT_ROOT_FOLDER_PATH.toString(), appName, currentSheetName);
		Files.createDirectories(newDirectoryPath);
		String fileName = newDirectoryPath.toAbsolutePath() + FORWARD_SLASH + testName + XMLFILE_EXT;

		logger.debug("fileName=" + fileName);

		File file = new File(fileName);
		java.nio.file.Files.write(Paths.get(file.toURI()), xml.getBytes(UTF8_ENCODING), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	/**
	 * Validates the expected data provided in validations string with actual xml data.
	 * 
	 * @param validations expected data
	 * @param responsexml response body
	 * @param statusCode status code expecting
	 * @return validation success or failure
	 * @throws Exception
	 */
	protected boolean validateResponse(final String validations,
			final String responsexml,
			final String statusCode) throws Exception {

		boolean success = true;
		String validationsToken = null;
		String xmlNameKey = null;
		String expectedValue = null;
		String actualValue = null;
		String actualRC = null;

		if (rowData.getTemplateName().equals("DeleteUser")) {
			actualRC = from(responsexml).get("response.fn[1].@rc");
			logger.debug("actual RC for Delete User service::" + actualRC);
		} else {
			Node fnResponse = with(responsexml).get("response.fn[1]");
			Map<?, ?> hm = fnResponse.attributes();
			actualRC = (hm.get("rc")).toString();
		}

		if (StringUtils.isNotBlank(validations)) {
			StringTokenizer validationsTokenizer = new StringTokenizer(validations, TOKENIZER_DOUBLE_PIPE);

			while (validationsTokenizer.hasMoreTokens()) {
				if (success == false)
					break;
				validationsToken = validationsTokenizer.nextToken();
				StringTokenizer validationTokenizer = new StringTokenizer(validationsToken, TOKENIZER_EQUALTO);
				while (validationTokenizer.hasMoreTokens()) {
					xmlNameKey = validationTokenizer.nextToken();
					// Get next token to get value
					if (validationTokenizer.hasMoreTokens()) {
						expectedValue = validationTokenizer.nextToken();
						if (xmlNameKey.equalsIgnoreCase(STATUS)) {
							if (StringUtils.isBlank(expectedValue) || !expectedValue.equals(statusCode)) {

								logger.info("Actual status code: " + statusCode
										+ "is not matching expected status code value: " + expectedValue);

								testReporter.log(LogStatus.ERROR, "Actual status code: " + statusCode
										+ "is not matching expected status code value: " + expectedValue);
								success = false;
								break;
							}
						} else if (xmlNameKey.equalsIgnoreCase("rc")) {
							if (StringUtils.isBlank(expectedValue) || !expectedValue.equalsIgnoreCase(actualRC)) {
								String error = with(responsexml).getString("response.fn[1].error");
								testReporter.log(LogStatus.ERROR,
										"Actual response code: " + actualRC
												+ "is not matching expected status code value: " + expectedValue
												+ " Error message:" + error);
								success = false;
								break;
							} else {
								testReporter.log(LogStatus.INFO, "Actual value: " + actualRC + " for key: " + xmlNameKey
										+ " is matching expected value:" + expectedValue);
								success = true;
								break;
							}

						} else if (xmlNameKey.contains("error")) {
							String error = with(responsexml).getString("response.fn[1].error");

							if (StringUtils.isBlank(expectedValue) || !expectedValue.equalsIgnoreCase(error)) {
								testReporter.log(LogStatus.INFO, "Actual value: " + error + " for key: " + xmlNameKey
										+ " is not matching expected value:" + expectedValue);
								break;
							} else {
								testReporter.log(LogStatus.INFO, "Actual value: " + error + " for key: " + xmlNameKey
										+ " is matching expected value:" + expectedValue);
								success = true;
								break;
							}
						} else {
							actualValue = getXMLNodeValue(responsexml, xmlNameKey);
							if (expectedValue.equals(actualValue)) {
								testReporter.log(LogStatus.INFO, "Actual value: " + actualValue + " for key: "
										+ xmlNameKey + " is matching expected value:" + expectedValue);
								success = true;
							} else {
								testReporter.log(LogStatus.ERROR, "Actual value: " + actualValue + " for key: "
										+ xmlNameKey + " is not matching expected value:" + expectedValue);
								success = false;
							}
							logger.debug("Actual value:::::" + actualValue + " Expected Value:::::" + expectedValue);
							break;
						}
					} else {
						testReporter.log(LogStatus.INFO,
								"Expected value is empty !! Please provide input for " + xmlNameKey
										+ ". For Empty string check, provide \"\" and for null check, provide null ");

						success = false;
						break;
					}
				}
			}
		}

		return success;
	}

	/**
	 * Get XML node value for the specified node
	 * 
	 * @param responsexml represents xml response
	 * @param node for getting specific node value
	 * 
	 */

	public String getXMLNodeValue(String responsexml,
			String node) {

		String parentNode = "";
		String childNode = "";
		String nodeValue = "";
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(responsexml)));
			doc.getDocumentElement().normalize();
			if (node.contains(".")) {
				String[] kvPairs = node.split("\\.");
				parentNode = kvPairs[0];
				childNode = kvPairs[1];
				NodeList maps = doc.getElementsByTagName("map");
				for (int x = 0; x < maps.getLength(); x++) {
					if (((maps.item(x).getAttributes().getNamedItem("name")) != null)
							&& (maps.item(x).getAttributes().getNamedItem("name").getNodeValue())
									.equalsIgnoreCase(parentNode)) {
						Element map = null;
						map = (Element) maps.item(x);
						NodeList valList = map.getElementsByTagName("val");
						for (int j = 0; j < valList.getLength(); j++) {
							if ((valList.item(j).getAttributes().getNamedItem("name").getNodeValue())
									.equalsIgnoreCase(childNode)) {
								nodeValue = valList.item(j).getFirstChild().getNodeValue();
							}
						}
					}
				}
			} else {
				NodeList maps = doc.getElementsByTagName("map");
				Element map = null;
				String attributeVal = "";
				for (int i = 0; i < maps.getLength(); i++) {
					map = (Element) maps.item(i);
					NodeList valList = map.getElementsByTagName("val");

					for (int j = 0; j < valList.getLength(); j++) {
						attributeVal = valList.item(j).getAttributes().getNamedItem("name").getNodeValue();
						if (node.equalsIgnoreCase(attributeVal)) {
							nodeValue = valList.item(j).getFirstChild().getFirstChild().getNodeValue();
						}
					}
				}

			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nodeValue;
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
					rowData.setTemplateName(currentCellData);
				case 8:
					rowData.setBody(currentCellData);
				case 9:
					rowData.setDependencyTests(currentCellData);
				case 10:
					rowData.setValidations(currentCellData);
				case 11:
					rowData.setStore(currentCellData);
				case 12:
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

	/**
	 * This function will convert XML template to string
	 * 
	 * @param string xmlfilepath
	 * @return the XML String data
	 */
	public String convertXMLToString(String xmlFilePath) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		String result = "";
		File xmlFile = new File(xmlFilePath);

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StringWriter sw = new StringWriter();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(source, new StreamResult(sw));
			result = sw.toString();
			// logger.debug("After converting XML to String::"+result);

		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * This function will update XML template with map values
	 * 
	 * @param string xmldata
	 * @param Map bodyparams for updating values in xmldata
	 * @return String XMLdata for sending updated data
	 */
	public String updateTemplate(String xmldata,
			Map<String, String> bodyParams) {

		// update xml placeholders with values
		if (StringUtils.isNotBlank(xmldata)) {
			logger.debug("Before replace XML=" + xmldata);

			StringBuffer sb = new StringBuffer();
			String toReplace = null;

			if (StringUtils.isNotBlank(xmldata)) {

				Matcher matcher = Pattern.compile(XML_REQUEST_PLACEHOLDER_MATCHER_PATTERN).matcher(xmldata);

				while (matcher.find()) {

					// What to replace
					toReplace = matcher.group(1);

					// System.out.println("to Replace XML PlaceHolder=" + toReplace);
					String target = "NO_DATA_FOUND";
					if (bodyParams.containsKey(toReplace)) {
						// Replace single specific char $ with \\$ to avoid exception while replace
						target = bodyParams.get(toReplace).replaceAll(Pattern.quote("$"),
								Matcher.quoteReplacement("\\$"));
					}
					// logger.debug("to Replace XML PlaceHolder::"+toReplace+" with Target::"+target);
					// Append replaced match.
					matcher.appendReplacement(sb, target);
				}
				matcher.appendTail(sb);
			}

			// logger.debug("=================================After replace XML ===========================\n" + sb);

			return sb.toString();
		}
		return xmldata;
	}

	@AfterSuite
	public void afterSuite() {
		reporter.close();
	}

	public void addCategory() {

	}

}
