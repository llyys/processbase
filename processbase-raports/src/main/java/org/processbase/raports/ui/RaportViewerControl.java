package org.processbase.raports.ui;

import java.util.Map;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.processbase.raports.birt.ReportService;
import org.processbase.raports.birt.generator.BirtGeneratorServiceAsync;
import org.processbase.raports.birt.generator.ReportItem;
import org.processbase.raports.util.AsyncCallback;
import org.processbase.ui.core.template.PbPanel;

import com.vaadin.ui.Label;

public class RaportViewerControl extends PbPanel {
	private BirtGeneratorServiceAsync generator=new BirtGeneratorServiceAsync();
	private Label content=new Label();
	private final ReportItem reportItem;
	
	public RaportViewerControl(ReportItem reportItem) {
		this.reportItem = reportItem;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void initUI() {	
		
		content.setWidth("100%");
		setMargin(true);
		content.setContentMode(Label.CONTENT_XHTML);
		addComponent(content);
		generateReport(reportItem, null);
	}
	public void generateReport(ReportItem reportName, Map params) {
		
		try {
			ReportService reportService=new ReportService();
			String result=reportService.runAndRender(reportItem, params, new HTMLRenderOption());
			content.setValue(result);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		/*
		generator.generateReport(reportName, params, new AsyncCallback<String>() {			
			public void onSuccess(String result) {
				content.setValue(result);
			}
			
			public void onFailure(Throwable caught) {
				throw new RuntimeException(caught);
			}
		});*/
	}	
	

}
