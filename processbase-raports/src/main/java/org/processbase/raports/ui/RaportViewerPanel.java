package org.processbase.raports.ui;

import java.util.Map;

import org.processbase.raports.birt.generator.BirtGeneratorServiceAsync;
import org.processbase.raports.util.AsyncCallback;
import org.processbase.ui.core.template.PbPanel;

import com.vaadin.ui.Label;

public class RaportViewerPanel extends PbPanel {
	private BirtGeneratorServiceAsync generator=new BirtGeneratorServiceAsync();
	private Label content=new Label();
	@Override
	public void initUI() {	
		content.setWidth("100%");
		content.setContentMode(Label.CONTENT_XHTML);
	}
	public void generateReport(String reportName, Map params) {
		if(!isInitialized()) 
			return;
		
		generator.generateReport(reportName, params, new AsyncCallback<String>() {			
			public void onSuccess(String result) {
				content.setValue(result);
			}
			
			public void onFailure(Throwable caught) {
				//TODO: Error implementation
			}
		});
	}	
	

}
