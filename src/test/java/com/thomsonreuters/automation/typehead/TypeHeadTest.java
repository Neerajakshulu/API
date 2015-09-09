package com.thomsonreuters.automation.typehead;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test for the TypeheadTest API's
 * 
 * TestData: TypeHeadTestData.xlsx
 */
public class TypeHeadTest extends AbstractBase{
	@Test
	public void wathclistTest() throws Exception {
		testDataExcelPath = "src/test/test-data/TypeHeadTestData.xlsx";
		appName = "1PTYPEAHEAD";
		runTests();
	}
}
