package com.thomsonreuters.automation.migration;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.thomsonreuters.steam.core.SteamAbstractBase;
import com.thomsonreuters.steam.core.TestUtil;
import com.thomsonreuters.steam.core.Xls_Reader;

public class SteamUserDeleteTest extends SteamAbstractBase {

	int count = 2;

	@Test(dataProvider = "getTestData")
	public void steamTest(String fn,
			String ln,
			String sc,
			String mail,
			String stid,
			String steam_status,
			String mig_id,
			String log_id,
			String status) throws Exception {
		dataStore.put("STEAM_ID", stid);
		testDataExcelPath = "src/test/test-data/MigrationTestData.xlsx";
		templatePath = "src/test/test-data/STEAM_TEMPLATES/";
		appName = "1PSTEAM";
		runTests("STeAM_USER_DELETE");
//		Xls_Reader xls = new Xls_Reader(testDataExcelPath);
//		if (isTestFail) {
//			xls.setCellData("Data", "STeAM_STATUS", count, "FAIL");
//		} else {
//			xls.setCellData("Data", "STeAM_STATUS", count, "PASS");
//			xls.setCellData("Data", "STeAM_ID", count, dataStore.get("OPQA-1410_user.userID"));
//		}
//		System.out.println(count);
//		count++;

	}

	@DataProvider
	public Object[][] getTestData() {
		return TestUtil.getData(new Xls_Reader("src/test/test-data/MigrationTestData.xlsx"), "Data","STeAM1");
	}
}
