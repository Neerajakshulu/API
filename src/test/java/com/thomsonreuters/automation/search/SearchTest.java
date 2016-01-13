package com.thomsonreuters.automation.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.thomsonreuters.automation.common.AbstractBase;

/**
 * Tests for the Search API
 * 
 * TestData: SearchTestData.xlsx
 */
public class SearchTest extends AbstractBase {

	@Test
	public void searchTest() throws Exception {
		testDataExcelPath = "src/test/test-data/SearchTestData.xlsx";
		appName = "1PSEARCH";
		runTests();
	}

	protected boolean validateResponse(final String validations,
			final String json,
			final String statusCode) throws Exception {
		boolean status = true;
		if (StringUtils.isNotBlank(validations)) {
			StringTokenizer validationsTokenizer = new StringTokenizer(validations, TOKENIZER_DOUBLE_AMPERSAND);
			while (validationsTokenizer.hasMoreTokens()) {
				String validationsToken = validationsTokenizer.nextToken();
				status = validateSearch(validationsToken, json, statusCode);
				if (!status)
					break;
			}
		}
		return status;
	}

	public boolean validateSearch(String validations,
			final String json,
			final String statusCode) throws Exception {
		boolean status = true;
		if (StringUtils.isNotBlank(validations)) {
			Matcher matcher = Pattern.compile(PLACEHOLDER_MATCHER_PATTERN_VALIDATION).matcher(validations);
			if (matcher.find()) {
				validations = validations.substring(1, validations.length() - 1);
				StringTokenizer validationsTokenizer = new StringTokenizer(validations, TOKENIZER_DOUBLE_PIPE);
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
			} else {
				if (rowData.getDescription().contains("sort")) {
					status = super.validateResponse(validations, json, statusCode);
					if (status) {
						JsonPath jsonPath = new JsonPath(json);
						List<Object> actualValue = null;
						if (rowData.getDescription().contains("times cited")) {
							if (rowData.getDescription().contains("patents")) {
								actualValue = jsonPath.getList("hits.hits.sort");
							} else {
								actualValue = jsonPath.getList("hits.hits.fields.citingsrcslocalcount");
							}
							status = verifySortorder(actualValue, rowData.getDescription());
						} else if (rowData.getDescription().contains("relevance")) {
							actualValue = jsonPath.getList("hits.hits._score");
							status = verifySortorder(actualValue, rowData.getDescription());
						} else if (rowData.getDescription().contains("pub date")) {
							actualValue = jsonPath.getList("hits.hits.fields.sortdate");
							status = verifySortorder(actualValue, rowData.getDescription());
						}
					}
				} else
					status = super.validateResponse(validations, json, statusCode);
			}
		}

		return status;
	}

	private boolean verifySortorder(List<Object> actualValue,
			String description) {
		boolean status = false;
		List<String> actualList = new ArrayList<String>();
		for (int i = 0; i < actualValue.size(); i++) {
			String tmp = String.valueOf(actualValue.get(i));
			if (tmp.startsWith("["))
				actualList.add(tmp.substring(1, tmp.length() - 1));
			else
				actualList.add(tmp);
		}
		List<String> tempList = new ArrayList<String>(actualList);
		if (StringUtils.containsIgnoreCase(description, "asc")) {
			Collections.sort(tempList);
			status = tempList.equals(actualList);
		} else {
			Comparator cmp = Collections.reverseOrder();
			Collections.sort(tempList, cmp);
			status = tempList.equals(actualList);
		}
		return status;
	}
}
