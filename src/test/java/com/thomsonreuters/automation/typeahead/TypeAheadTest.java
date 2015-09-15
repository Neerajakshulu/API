package com.thomsonreuters.automation.typeahead;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test for the Type Ahead Test API's
 * 
 * TestData: TypeAheadTestData.xlsx
 */
public class TypeAheadTest extends AbstractBase{
	@Test
	public void typeAheadTest() throws Exception {
		testDataExcelPath = "src/test/test-data/TypeAheadTestData.xlsx";
		appName = "1PTYPEAHEAD";
		runTests();
	}
}
