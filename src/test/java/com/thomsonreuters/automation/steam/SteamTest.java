package com.thomsonreuters.automation.steam;

import org.testng.annotations.Test;

import com.thomsonreuters.steam.core.SteamAbstractBase;

public class SteamTest extends SteamAbstractBase {

	@Test
	public void steamTest() throws Exception {
		testDataExcelPath = "src/test/test-data/STeAMTestData.xlsx";
		templatePath="src/test/test-data/STEAM_TEMPLATES/";
		appName = "1PSTEAM";
		runTests();
	}
}
