package org.processbase.raports.birt.generator;

import java.util.Map;

import org.processbase.raports.birt.BirtReportGenerator;
import org.processbase.raports.util.AsyncCallback;

public class BirtGeneratorServiceAsync {
	public void generateReport(ReportItem reportItem, Map params, AsyncCallback<String> callback) {
		try {
			
			//BirtReportGenerator.getInstance().renderReportFromFile(params, reportItem);
			ReportHandler rh = new ReportHandler();
			String htmlReport = rh.executeHTMLReport(reportItem, params);
			callback.onSuccess(htmlReport);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			callback.onFailure(e);
		}
	}
}
