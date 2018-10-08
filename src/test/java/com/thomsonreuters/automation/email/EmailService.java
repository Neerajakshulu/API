package com.thomsonreuters.automation.email;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

public class EmailService extends AbstractBase {
	@Test
	public void emailTest() throws Exception {
		strDateTime = new SimpleDateFormat(TESTOUTPUT_FOLDER_DATEFORMAT).format(new Date());
		dataStore.put(TESTOUTPUT_FOLDER_DATEFORMAT,strDateTime);
		dataStore.put("UUID",UUID.randomUUID().toString());
		testDataExcelPath = "src/test/test-data/EmailServiceTestData.xlsx";
		appName = "EmailService";
		runTests();
	}

}
