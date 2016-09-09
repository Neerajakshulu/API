/*******************************************************************************
 * Copyright (c) 2012 : Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information
 * of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited
 *
 ******************************************************************************/
package com.thomsonreuters.automation.entitlemets;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code OauthTest} class to test for the 1POAUTH API's.
 *
 * @author Ramesh Lalam
 * 
 */
public class EntitlementTest extends AbstractBase {

	/**
	 * {@code entitlementTest} method is the entry point to test 1PENTITLEMENTS API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * EntitlementTestData.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1PENTITLEMENT tests
	 * @see Exception
	 * 
	 */

	@Test
	public void entitlementTest() throws Exception {
		testDataExcelPath = "src/test/test-data/EntitlementTestData.xlsx";
		appName = "1PENTITLEMENT";
		runTests();
	}



}
