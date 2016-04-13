package com.thomsonreuters.automation.notify;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Test the Notify APIs
 */
public class NotifyTest extends AbstractBase {

	@Test
	public void notifyTest() throws Exception {
		testDataExcelPath = "src/test/test-data/NotifyTestData.xlsx";
		appName = "1PNOTIFY";
		runTests();
	}
}
