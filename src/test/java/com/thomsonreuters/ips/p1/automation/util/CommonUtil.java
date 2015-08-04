package com.thomsonreuters.ips.p1.automation.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CommonUtil {

	public static void main(String args[]) {

		try {
			String str = new SimpleDateFormat("ddMMMyyyy_HHmmss").format(new Date());

			System.out.print("Date:" + str);
			String toSave = "My string to save";
			
			Path newDirectoryPath = Paths.get("C:/1P-Projects/Automation/1p-api-automation/src/test/test-output", "29Jul2015_173951", "Profiles", "Sheet1");
			Files.createDirectories(newDirectoryPath);
			
			System.out.println(newDirectoryPath.toAbsolutePath());
			
			//java.nio.file.Files.createDirectories(dir, attrs)
			File file = new File(newDirectoryPath.toAbsolutePath() + "/" + "test.txt");
			
			java.nio.file.Files.write(Paths.get(file.toURI()), toSave.getBytes("utf-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
