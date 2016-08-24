/*******************************************************************************
 * Copyright (c) 2012 : Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information
 * of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited
 *
 ******************************************************************************/
package com.thomsonreuters.automation.appbridge;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code AppBridgeMaintenanceTest} class to test for the Appbridge API's When APPBRIDGE Service is under maintenance.
 *
 * @author Avinash P
 * 
 */
public class AppBridgeMaintenanceTest extends AbstractBase {

	/**
	 * {@code appBridgeMaintenanceTest} method is the entry point to test 1PAPPBRIDGE API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * AppBridgeTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PAPPBRIDGE tests
	 * @see Exception
	 * 
	 */

	@Test
	public void appBridgeMaintenanceTest() throws Exception {
		testDataExcelPath = "src/test/test-data/AppBridgeTestData.xlsx";
		appName = "1PAPPBRIDGE";
		runTests("ENDNOTE_MAINTENANCE");
	}

}
