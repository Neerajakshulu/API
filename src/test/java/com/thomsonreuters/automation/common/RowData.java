package com.thomsonreuters.automation.common;

public class RowData {

	private String testName;
	private String description;
	private String host;
	private String apiPath;
	private String method;
	private String templateName;
	private String headers;
	private String queryString;
	private String body;
	private String dependencyTests;
	private String validations;
	private String store;
	private String status;

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getDependencyTests() {
		return dependencyTests;
	}

	public void setDependencyTests(String dependencyTests) {
		this.dependencyTests = dependencyTests;
	}

	public String getValidations() {
		return validations;
	}

	public void setValidations(String validations) {
		this.validations = validations;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "RowData [testName=" + testName + ", description=" + description + ", host=" + host + ", apiPath="
				+ apiPath + ", method=" + method + ", headers=" + headers + ", queryString=" + queryString
				+ ", templateName=" + templateName + ", body=" + body + ", dependencyTests=" + dependencyTests
				+ ", validations=" + validations + ", store=" + store + ", status=" + status + "]";
	}

}
