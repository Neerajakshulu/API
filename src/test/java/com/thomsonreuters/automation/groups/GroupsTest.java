package com.thomsonreuters.automation.groups;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test for the Groups API's
 * 
 * TestData: GroupsTestData.xlsx
 */
public class GroupsTest extends AbstractBase {

	@Test
	public void wathclistTest() throws Exception {
		testDataExcelPath = "src/test/test-data/GroupsTestData.xlsx";
		appName = "1PGROUPS";
		runTests();
	}

}
