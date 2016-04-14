package com.thomsonreuters.automation.typeahead;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code TypeAheadTest} class to test for the 1PTYPEAHEAD API's.
 *
 * @author Avinash P
 * 
 */
public class TypeAheadTest extends AbstractBase {

	/**
	 * {@code typeAheadTest} method is the entry point to test 1PTYPEAHEAD API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * TypeAheadTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PTYPEAHEAD tests
	 * @see Exception
	 * 
	 */
	@Test
	public void typeAheadTest() throws Exception {
		testDataExcelPath = "src/test/test-data/TypeAheadTestData.xlsx";
		appName = "1PTYPEAHEAD";
		runTests();
	}
}
