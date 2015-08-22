package com.thomsonreuters.ips.p1.automation.common;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

/**
 * Common setup class for all the tests
 */
public abstract class AbstractBase {

	private static final Logger logger = LogManager.getLogger();

	protected static final String APPLICATION_JSON = "application/json";
	protected static final String WRAPPED_JSON = "application/vnd.rhq.wrapped+json";
	protected static final String APPLICATION_XML = "application/xml";
	protected static final String TEXT_CSV = "text/csv";
	protected static final String TEXT_HTML = "text/html";

	protected static final Header acceptJson = new Header("Accept", APPLICATION_JSON);
	protected static final Header acceptWrappedJson = new Header("Accept", WRAPPED_JSON);
	protected static final Header acceptXml = new Header("Accept", APPLICATION_XML);
	protected static final Header acceptCsv = new Header("Accept", TEXT_CSV);
	protected static final Header acceptHtml = new Header("Accept", TEXT_HTML);

	public static ExtentReports extent = null;
	public static ExtentTest test = null;

	String eurekaURL = "http://eureka.us-west-2.dev.oneplatform.build:8080/v2/apps";
	protected Map<String, String> appHosts = new HashMap<String, String>();
	
	public void setUp() throws Exception {

		RestAssured.baseURI = "http://" + System.getProperty("rest.server", "localhost");
		RestAssured.port = 7080;
		RestAssured.basePath = "/rest/";
	}

	public static ExtentReports getInstance() {
		if (extent == null) {
			// extent = new ExtentReports(System.getProperty("user.dir")+"\\testReports\\test_report.html", true);
			extent = new ExtentReports("src/test/test-reports/test_report.html", true);

			// optional
			extent.config().documentTitle("1P API Automation Report").reportName("Regression")
					.reportHeadline("1-P PLATFORM QA");
		}
		return extent;
	}

	public void getAppDetails(String appName) throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		String hostName = null;
		String port = null;
		boolean appFound = false;

		URL url = new URL("http://eureka.us-west-2.dev.oneplatform.build:8080/v2/apps");
		URLConnection conn = url.openConnection();

		XMLEventReader eventReader = inputFactory.createXMLEventReader(conn.getInputStream());

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			// reach the start of an item
			if (event.isStartElement()) {

				StartElement startElement = event.asStartElement();

				// if provided app name encountered then make appFound as true
				if (startElement.getName().getLocalPart().equals("name")) {
					event = eventReader.nextEvent();
					if (event.asCharacters().getData().equals(appName)) {
						appFound = true;
					}
				}

				// when appFound is true then get host name and port of that app.
				if (appFound == true) {
					if (startElement.getName().getLocalPart().equals("hostName")) {
						event = eventReader.nextEvent();
						hostName = event.asCharacters().getData();
						System.out.println("hostName=" + hostName);
					}

					if (startElement.getName().getLocalPart().equals("port")) {
						event = eventReader.nextEvent();
						port = event.asCharacters().getData();
						System.out.println("port=" + port);
						break;
					}
				}

			}
		}

	}

	public void getAllAppHostsForGivenEnv(String env) throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		String appName = null;
		String hostName = null;
		String port = null;
		String vipAddress = null;

		URL url = new URL(eurekaURL);
		URLConnection conn = url.openConnection();

		XMLEventReader eventReader = inputFactory.createXMLEventReader(conn
				.getInputStream());

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			// reach the start of an item
			if (event.isStartElement()) {

				StartElement startElement = event.asStartElement();

				// Get app name
				if (startElement.getName().getLocalPart().equals("name")) {
					event = eventReader.nextEvent();
					if (!event.asCharacters().getData().equalsIgnoreCase("Amazon"))
						appName = event.asCharacters().getData();
				}

				// Get host name
				if (startElement.getName().getLocalPart().equals("hostName")) {
					event = eventReader.nextEvent();
					hostName = event.asCharacters().getData();
				}

				// Get port
				if (startElement.getName().getLocalPart().equals("port")) {
					event = eventReader.nextEvent();
					port = event.asCharacters().getData();
				}

				// Get vip address
				if (startElement.getName().getLocalPart().equals("vipAddress")) {
					event = eventReader.nextEvent();
					vipAddress = event.asCharacters().getData();
					if (vipAddress.endsWith(env))
						appHosts.put(appName, hostName + ":" + port);
				}

			}
		}
	}
	
	public String[] getRowAsArray(String testCaseName,
			int sheetNo) throws Exception {

		XSSFWorkbook myWB = null;
		String[] rowData = null;

		try {
			int rowCount;
			int colCount;
			String excelPath = "src/test/test-data/TestData.xlsx";

			File myxl = new File(excelPath);
			FileInputStream myStream = new FileInputStream(myxl);

			myWB = new XSSFWorkbook(myStream);
			XSSFSheet mySheet = myWB.getSheetAt(sheetNo - 1); // Sheet 1 means zero

			rowCount = mySheet.getLastRowNum() + 1;
			logger.info(rowCount);
			colCount = mySheet.getRow(0).getLastCellNum();
			logger.info(colCount);

			rowData = new String[colCount];

			for (int i = 0; i < rowCount; i++) {
				XSSFRow row = mySheet.getRow(i);
				String value = getCellData(row.getCell(0));

				if (value.equalsIgnoreCase(testCaseName)) {
					for (int j = 0; j < colCount; j++) {
						XSSFCell cell = row.getCell(j);
						rowData[j] = getCellData(cell);
					}
				}

			}

		} catch (Exception e) {
			System.out.println("Exception in reading the excel file:" + e);
		} finally {
			myWB.close();
		}
		return rowData;
	}

	public String getCellData(XSSFCell cell) {
		// This function will convert an object of type excel cell to a string value
		int type = cell.getCellType();
		Object result;
		switch (type) {
			case XSSFCell.CELL_TYPE_NUMERIC: // 0
				result = cell.getNumericCellValue();
				break;
			case XSSFCell.CELL_TYPE_STRING: // 1
				result = cell.getStringCellValue();
				break;
			case XSSFCell.CELL_TYPE_FORMULA: // 2
				throw new RuntimeException("We can't evaluate formulas in Java");
			case XSSFCell.CELL_TYPE_BLANK: // 3
				result = "";
				break;
			case XSSFCell.CELL_TYPE_BOOLEAN: // 4
				result = cell.getBooleanCellValue();
				break;
			case XSSFCell.CELL_TYPE_ERROR: // 5
				throw new RuntimeException("This cell has an error");
			default:
				throw new RuntimeException("We don't support this cell type: " + type);
		}
		return result.toString();
	}
	
	

}
