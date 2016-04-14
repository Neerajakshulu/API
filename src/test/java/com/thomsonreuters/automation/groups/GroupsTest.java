package com.thomsonreuters.automation.groups;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code GroupsTest} class to test for the Groups API's.
 *
 * @author Avinash P
 * 
 */
public class GroupsTest extends AbstractBase {

	/**
	 * {@code groupsTest} method is the entry point to test 1PGROPUPS API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * GroupsTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PGROUPS tests
	 * @see Exception
	 * 
	 */
	@Test
	public void groupsTest() throws Exception {
		testDataExcelPath = "src/test/test-data/GroupsTestData.xlsx";
		appName = "1PGROUPS";
		runTests();
	}

}
