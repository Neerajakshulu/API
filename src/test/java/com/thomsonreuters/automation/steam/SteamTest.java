package com.thomsonreuters.automation.steam;

import org.testng.annotations.Test;

import com.thomsonreuters.steam.core.SteamAbstractBase;

public class SteamTest extends SteamAbstractBase {

	@Test
	public void steamTest() throws Exception {
		//setting system properties
		//System.setProperty("steam.proxy.host", "squid.dev.oneplatform.build");
		//System.setProperty("steam.xrpc.endpoint", "http://10.205.140.204:5000/esti/xrpc");
		
		//System.out.println("Getting system property::steam.proxy.host="+System.getProperty("steam.proxy.host"));
		//System.out.println("Getting system property::steam.xrpc.endpoint="+System.getProperty("steam.xrpc.endpoint"));
		
		testDataExcelPath = "src/test/test-data/STeAMTestData.xlsx";
		templatePath="src/test/test-data/STEAM_TEMPLATES/";
		appName = "1PSTEAM";
		runTests();
	}
}
