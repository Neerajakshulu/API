package com.thomsonreuters.automation.profilesearch;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Tests for the ProfileSearch API
 * 
 * TestData: ProfileSearchTestData.xlsx
 */
public class ProfileSearchTest extends AbstractBase {
	
	@Test
	public void profileSearchTest() throws Exception {
		testDataExcelPath = "src/test/test-data/ProfileSearchTestData.xlsx";
		appName = "1PPROFILESEARCH";
		runTests();
	}

	public boolean validateProfileSearch(final String validations, final String json, final String statusCode)
			throws Exception {
		boolean status = true;
		if (StringUtils.isNotBlank(validations)) {
			StringTokenizer validationsTokenizer = new StringTokenizer(validations, TOKENIZER_DOUBLE_BACK_SLACH);
			if (validationsTokenizer.hasMoreTokens()) {
				while (validationsTokenizer.hasMoreTokens()) {
					String validationsToken = validationsTokenizer.nextToken();
					status = super.validateResponse(validationsToken, json, statusCode);
					if (status)
						break;
				}
			} else {
				status = super.validateResponse(validations, json, statusCode);
			}
		}
		return status;
	}
	
	@Override
	protected boolean validateResponse(final String validations, final String json, final String statusCode)
			throws Exception {
		boolean status = true;
		if (StringUtils.isNotBlank(validations)) {

			StringTokenizer validationsTokenizer = new StringTokenizer(validations,  TOKENIZER_DOUBLE_PIPE);

			while (validationsTokenizer.hasMoreTokens()) {
				String validationsToken = validationsTokenizer.nextToken();
				status = validateProfileSearch(validationsToken, json, statusCode);
				if (!status)
					break;
			}
		}

		return status;

	}
	
}
