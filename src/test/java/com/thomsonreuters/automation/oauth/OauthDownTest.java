/*******************************************************************************
 * Copyright (c) 2012 : Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information
 * of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited
 *
 ******************************************************************************/
package com.thomsonreuters.automation.oauth;

import java.util.UUID;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * The {@code OauthDownTest} class to test for the OAUTH API's When STEAM Service is down.
 *
 * @author Ramesh Lalam
 * 
 */
public class OauthDownTest extends AbstractBase {

	/**
	 * {@code oauthDownTest} method is the entry point to test 1POAUTH API's.<BR>
	 * Initializes excel file path and app name. Calls runTests method for executing test cases specified in the
	 * STEAM_DOWN.xlsx file.</BR>
	 * 
	 * @throws Exception On Executing the 1POAUTH tests
	 * @see Exception
	 * 
	 */

	@Test
	public void oauthDownTest() throws Exception {
		dataStore.put(TESTOUTPUT_FOLDER_DATEFORMAT,strDateTime);
		dataStore.put("UUID",UUID.randomUUID().toString());
		testDataExcelPath = "src/test/test-data/OauthTestData.xlsx";
		appName = "1POAUTH";
		runTests("STEAM_DOWN");
	}

}
