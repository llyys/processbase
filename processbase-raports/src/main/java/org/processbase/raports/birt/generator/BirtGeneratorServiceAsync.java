package org.processbase.raports.birt.generator;

import java.util.Map;

import org.processbase.raports.util.AsyncCallback;

public class BirtGeneratorServiceAsync {
	public void generateReport(String name, Map params, AsyncCallback<String> callback) {
		ReportHandler rh = new ReportHandler();
		String htmlReport = rh.executeHTMLReport(name, params);
		callback.onSuccess(htmlReport);
	}
}
