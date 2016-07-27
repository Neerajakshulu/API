package com.thomsonreuters.steam.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class TestUtil {

	// finds if the test suite is runnable
	public static boolean isSuiteRunnable(Xls_Reader xls,
			String suiteName) {
		System.out.println("suiteName=" + suiteName);

		System.out.println("suite row count=" + xls.getRowCount("Test Suite"));

		boolean isExecutable = true;
		for (int i = 2; i <= xls.getRowCount("Test Suite"); i++) {
			// String suite = xls.getCellData("Test Suite", "TSID", i);
			// String runmode = xls.getCellData("Test Suite", "Runmode", i);
			// changing the logic due to skip happening in Extent reports even Runmodes are Y in both the places, due to
			// this reason changed the logic
			if (xls.getCellData("Test Suite", "TSID", i).equalsIgnoreCase(suiteName)) {
				if (xls.getCellData("Test Suite", "Runmode", i).equalsIgnoreCase("N")
						|| xls.getCellData("Test Suite", "Runmode", i).trim().isEmpty()) {
					isExecutable = false;
					break;
				} else {
					isExecutable = true;
				}
			}

		}
		xls = null; // release memory

		System.out.println("isExecutable master suite=" + isExecutable);

		return isExecutable;

	}

	// returns true if runmode of the test is equal to Y
	public static boolean isTestCaseRunnable(Xls_Reader xls,
			String testCaseName) {

		System.out.println("testCaseName=" + testCaseName);
		System.out.println("xls row count=" + xls.getRowCount("Test Cases"));

		boolean isExecutable = true;
		for (int i = 2; i <= xls.getRowCount("Test Cases"); i++) {
			// String tcid=xls.getCellData("Test Cases", "TCID", i);
			// String runmode=xls.getCellData("Test Cases", "Runmode", i);
			// System.out.println(tcid +" -- "+ runmode);
			// changing the logic due to skip happening in Extent reports even Runmodes are Y in both the places, due to
			// this reason changed the logic
			if (xls.getCellData("Test Cases", "TCID", i).equalsIgnoreCase(testCaseName)) {
				System.out.println("Test run mode-->" + xls.getCellData("Test Cases", "Runmode", i)
						+ "test case name-->" + testCaseName);
				if (xls.getCellData("Test Cases", "Runmode", i).equalsIgnoreCase("N")
						|| xls.getCellData("Test Cases", "Runmode", i).trim().isEmpty()) {
					isExecutable = false;
					break;
				} else {
					isExecutable = true;
				}
			}
		}
		System.out.println("isExecutable child suite=" + isExecutable);
		return isExecutable;

	}

	// return the test data from a test in a 2 dim array
	public static synchronized Object[][] getData(Xls_Reader xls,
			String testCaseName,
			String app) {

		// if the sheet is not present
		if (!xls.isSheetExist(testCaseName)) {
			xls = null;
			return new Object[1][0];
		}

		int rows = xls.getRowCount(testCaseName);
		int cols = xls.getColumnCount(testCaseName);
		// System.out.println("Rows are -- "+ rows);
		// System.out.println("Cols are -- "+ cols);

		Object[][] data = new Object[rows - 1][cols];
		for (int rowNum = 2; rowNum <= rows; rowNum++) {
			for (int colNum = 0; colNum < cols; colNum++) {

				if (colNum == 3 && app.equals("STeAM")) {
					String date = new SimpleDateFormat("ddMMMyyyy_HHmmss").format(new Date());
					String mail = null;
					if ((rowNum + colNum) % 2 == 0) {
						if (StringUtils.isBlank(xls.getCellData(testCaseName, 2, rowNum))) {
							mail = xls.getCellData(testCaseName, 0, rowNum) + "."
									+ xls.getCellData(testCaseName, 1, rowNum) + date + "@tr.com";
						} else {
							mail = xls.getCellData(testCaseName, 0, rowNum) + "."
									+ xls.getCellData(testCaseName, 1, rowNum)
									+ xls.getCellData(testCaseName, 2, rowNum) + date + "@tr.com";
						}
					} else {
						if (StringUtils.isBlank(xls.getCellData(testCaseName, 2, rowNum))) {
							mail = xls.getCellData(testCaseName, 0, rowNum) + "."
									+ xls.getCellData(testCaseName, 1, rowNum) + "@tr.com";
						} else {
							mail = xls.getCellData(testCaseName, 0, rowNum) + xls.getCellData(testCaseName, 2, rowNum)
									+ xls.getCellData(testCaseName, 1, rowNum) + "@tr.com";
						}

					}
					

					// String mail = xls.getCellData(testCaseName, 0, rowNum) + "."
					// + xls.getCellData(testCaseName, 1, rowNum) + "@tr.com";
					xls.setCellData("Data", "mail", rowNum, mail);
				}
				// System.out.print(xls.getCellData(testCaseName, colNum, rowNum) + " -- ");
				data[rowNum - 2][colNum] = xls.getCellData(testCaseName, colNum, rowNum);
			}
			// System.out.println();
		}
		return data;

	}

	// checks RUnmode for dataSet
	public static String[] getDataSetRunmodes(Xls_Reader xlsFile,
			String sheetName) {
		String[] runmodes = null;
		if (!xlsFile.isSheetExist(sheetName)) {
			xlsFile = null;
			sheetName = null;
			runmodes = new String[1];
			runmodes[0] = "Y";
			xlsFile = null;
			sheetName = null;
			return runmodes;
		}
		runmodes = new String[xlsFile.getRowCount(sheetName) - 1];
		for (int i = 2; i <= runmodes.length + 1; i++) {
			runmodes[i - 2] = xlsFile.getCellData(sheetName, "Runmode", i);
		}
		xlsFile = null;
		sheetName = null;
		return runmodes;

	}

	// update results for a particular data set
	public static void reportDataSetResult(Xls_Reader xls,
			String testCaseName,
			int rowNum,
			String result) {
		xls.setCellData(testCaseName, "Results", rowNum, result);
	}

	// return the row num for a test
	public static int getRowNum(Xls_Reader xls,
			String id) {
		for (int i = 2; i <= xls.getRowCount("Test Cases"); i++) {
			String tcid = xls.getCellData("Test Cases", "TCID", i);

			if (tcid.equals(id)) {
				xls = null;
				return i;
			}

		}

		return -1;
	}

}
