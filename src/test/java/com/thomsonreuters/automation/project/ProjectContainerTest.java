package com.thomsonreuters.automation.project;

import org.testng.annotations.Test;
import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The ProjectContainerTest program is an entry point for running Container API for Project type test cases. This class initializes app
 * name, excel file path which are utilized by AbstractBase class.
 *
 * @author Janardhan
 * @version 1.0
 * @since 2016-10-23
 */
public class ProjectContainerTest extends AbstractBase {

	/**
	 * This method is entry point for testing. Initializes excel file path and app name. Calls runTests method for
	 * executing test cases specified in the excel file.
	 * 
	 * @return Nothing
	 * @throws Exception
	 * 
	 */
	@Test
	public void ProjectContainerTest() throws Exception {
		testDataExcelPath = "src/test/test-data/ProjectContainerTest.xlsx";
		appName = "1PCONTAINER";
		runTests();
	}
}
