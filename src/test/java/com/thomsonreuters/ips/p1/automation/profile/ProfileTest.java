package com.thomsonreuters.ips.p1.automation.profile;

import static com.jayway.restassured.RestAssured.get;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.thomsonreuters.ips.p1.automation.common.AbstractBase;

public class ProfileTest extends AbstractBase {

	private static final Logger logger = LogManager.getLogger();

	private static final String testDataExcelPath = "src/test/test-data/ProfileTestData.xlsx";
	private static final String moduleName = "Profiles";
	private static final int initialColumnsCount = 4;
	private static final int inputColumnsCount = 5;
	private static final int expectedColumnsCount = 5;
	private static String strDateTime = null;
	
	@BeforeSuite
	public void beforeSuite() {
		logger.info("@BeforeSuite - any initialization / activity to perform before starting your test suite");
		strDateTime = new SimpleDateFormat("ddMMMyyyy_HHmmss").format(new Date());
	}

	@BeforeClass
	public void beforeClass() {
		logger.info("@BeforeClass - run before the first test method in the current class is invoked");
	}

	@BeforeMethod
	public void beforeMethod() {
		logger.info("@BeforeMethod - run before each test method annotated with @Test");
	}

	@BeforeTest
	public void beforeTest() {
		logger.info("@BeforeTest - run before any test method belonging to the classes inside the <test> tag is run");
	}

	@AfterSuite
	public void afterSuite() {
		logger.info("@AfterSuite - any clean-up / activity to perform after complete execution of the test suite");
	}

	@AfterClass
	public void afterClass() {
		logger.info("@AfterClass - run after complete execution of the last test method in the current class");
	}

	@AfterMethod
	public void afterMethod() {
		logger.info("@AfterMethod - run after each test method annotated with @Test");
	}

	@AfterTest
	public void afterTest() {
		logger.info("@AfterTest - run after any test method belonging to the classes inside the <test> tag is run");
	}

	@Test
	public void profilesTest() throws Exception {

		logger.info("profilesTest entered");

		String hostName = "http://restcountries.eu/rest/v1/alpha";
		String port = "";

		XSSFWorkbook workBook = null;
		FileInputStream inputStream = null;

		try {
			int rowCount;
			int totalColumnCount = initialColumnsCount + inputColumnsCount + expectedColumnsCount;
			String currentSheetName = null;
			
			//Row data
			String testName = null;
			String apiPath = null;
			String httpMethod = null;
			String description = null;
			String[] inputData = null;
			String[] expectedData = null;

			// Read Excel file
			File myxl = new File(testDataExcelPath);
			inputStream = new FileInputStream(myxl);
			workBook = new XSSFWorkbook(inputStream);
			int totalSheets = workBook.getNumberOfSheets();

			// Loop through each sheet in the Excel
			for (int currentSheet = 0; currentSheet < totalSheets; currentSheet++) {

				logger.info("========================================================================");
				logger.info("Started executing tests from sheet " + (currentSheet + 1));

				XSSFSheet mySheet = workBook.getSheetAt(currentSheet);
				currentSheetName = workBook.getSheetName(currentSheet);
				rowCount = mySheet.getLastRowNum();

				logger.debug("total number of rows:" + rowCount);

				// Loop through all test case records of current sheet
				for (int i = 1; i <= rowCount; i++) {
					XSSFRow row = mySheet.getRow(i);

					testName = getCellData(row.getCell(0, Row.CREATE_NULL_AS_BLANK));

					// Don't consider the rows without test name, skip them
					if (!StringUtils.isBlank(testName)) {

						logger.info("-----------------------------------------------------------------------");
						logger.info("Starting test:" + testName);

						apiPath = null;
						httpMethod = null;
						inputData = new String[inputColumnsCount];
						expectedData = new String[expectedColumnsCount];

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
								apiPath = getCellData(cell);
								logger.debug("apiPath=" + apiPath);
							} else if (j == 3) {
								httpMethod = getCellData(cell);
								logger.debug("httpMethod=" + httpMethod);
							} else if (j < (initialColumnsCount + inputColumnsCount)) {
								inputData[j - initialColumnsCount] = getCellData(cell);
							} else {
								expectedData[j - (initialColumnsCount + expectedColumnsCount)] = getCellData(cell);
							}
						}

						if (httpMethod.equals("GET")) {
							logger.debug("Entered into GET Method");

							String url = hostName + port + apiPath + getQueryString(inputData);

							logger.debug("URL=" + url);

							// Call the Rest API and get the response
							String json = get(url).andReturn().asString();

							// Save API response to file
							saveAPIResponse(json, currentSheetName, testName);

							// Validate the response with expected data
							boolean success = validateResponse(expectedData, json);

							// Update the excel file with Test PASS / FAIL status
							updateTestStatus(row, success);

						} else if (httpMethod.equals("POST")) {
							logger.debug("POST Method not supported yet");
						} else {
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

		logger.info("profilesTest end");
	}

	private void updateTestStatus(final XSSFRow row,
			final boolean success) throws Exception {
		String status = success ? "PASS" : "FAIL";
		int testStatusColumnIndex = initialColumnsCount + inputColumnsCount + expectedColumnsCount;

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

	@Test(enabled = false)
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
