package com.thomsonreuters.automation.draSSOLogin;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;


public class SsoLoginTest extends AbstractBase{
	@Test
	public void ssologinTest() throws Exception {
		testDataExcelPath = "src/test/test-data/SsoLoginTestData.xlsx";
		appName = "SSOLOGIN";
		runTests();
	}
	
}
