package com.thomsonreuters.automation.migration;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;
import com.thomsonreuters.steam.core.TestUtil;
import com.thomsonreuters.steam.core.Xls_Reader;

public class MigrateUserTest extends AbstractBase {

	int count = 2;

	@Test(dataProvider = "getTestData")
	public void migrationTest(String fn,
			String ln,
			String sc,
			String mail,
			String stid,
			String steam_status,
			String mig_id,
			String log_id,
			String status) throws Exception {
		dataStore.put("MailId", mail);
		testDataExcelPath = "src/test/test-data/MigrationTestData.xlsx";
		appName = "1PAUTH";
		runTests("1PAUTH");
		Xls_Reader xls = new Xls_Reader(testDataExcelPath);
		xls.setCellData("Data", "Migid", count, dataStore.get("OPQA-3026_records.truid"));
		xls.setCellData("Data", "logtid", count, dataStore.get("OPQA-2706_userid"));
		logger.info(dataStore.get("OPQA-3026_records.truid").equals(dataStore.get("OPQA-2706_userid")));
		if (dataStore.get("OPQA-3026_records.truid").equals(dataStore.get("OPQA-2706_userid"))) {
			xls.setCellData("Data", "status", count, "PASS");
		} else {
			xls.setCellData("Data", "status", count, "FAIL");
		}
		count++;
	}

	@DataProvider
	public Object[][] getTestData() {
		return TestUtil.getData(new Xls_Reader("src/test/test-data/MigrationTestData.xlsx"), "Data", "NEON");
	}

}
