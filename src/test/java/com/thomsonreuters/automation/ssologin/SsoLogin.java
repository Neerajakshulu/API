package com.thomsonreuters.automation.ssologin;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;


public class SsoLogin extends AbstractBase{
	@Test
	public void ssologinTest() throws Exception {
		testDataExcelPath = "src/test/test-data/SsoLoginTestData.xlsx";
		appName = "SSOLOGIN";
		runTests();
	}
	
}
