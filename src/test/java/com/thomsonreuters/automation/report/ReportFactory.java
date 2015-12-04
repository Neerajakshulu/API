package com.thomsonreuters.automation.report;

import java.util.Date;

import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;

public class ReportFactory {

	private static ExtentReports reporter;

	public static synchronized ExtentReports getReporter() {
		if (reporter == null) {
			Date today = new Date();
			Long time = today.getTime();
			String date_time=new java.sql.Date(time)+"_"+ today.getHours()+":"+today.getMinutes();
//			reporter = new ExtentReports("Reports/1P-API-AUTOMATION-TEST-REPORT.html", true,
			reporter = new ExtentReports("Reports/1P-API-AUTOMATION-TEST-REPORT_" +date_time + ".html", true,
					DisplayOrder.OLDEST_FIRST);
			reporter.config().documentTitle("1P-API-AUTOMATION-TEST-REPORT").reportName("Regression")
					.reportHeadline("1P-API-AUTOMATION-TEST-REPORT");
			reporter.addSystemInfo("Rest Assured", "2.4.1").addSystemInfo("Environment", "Dev-Stable");
		}
		return reporter;
	}

	public static synchronized void closeReporter() {
		reporter.flush();
		reporter.close();
	}
}