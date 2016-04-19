/*******************************************************************************
 * Copyright (c) 2012 : Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information
 * of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited
 *
 ******************************************************************************/
package com.thomsonreuters.automation.exp;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code ExperimentTest} class to test for the Experiment API's.
 *
 * @author Avinash P
 * 
 */
public class ExperimentTest extends AbstractBase {

	/**
	 * {@code experimentTest} method is the entry point to test 1PEXP API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * ExperimentTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PEXP tests
	 * @see Exception
	 * 
	 */

	@Test
	public void experimentTest() throws Exception {
		testDataExcelPath = "src/test/test-data/ExperimentTestData.xlsx";
		appName = "1PEXP";
		runTests();
	}

}
