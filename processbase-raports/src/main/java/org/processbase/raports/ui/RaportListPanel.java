package org.processbase.raports.ui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.processbase.raports.birt.generator.ReportItem;
import org.processbase.raports.data.ReportingDataStore;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.osgi.PbPanelModule;

import com.vaadin.data.Item;
import com.vaadin.ui.Table;

public class RaportListPanel extends PbPanelModule{

	 protected Table table = new Table();
	
	@Override
	public String getTitle(Locale arg0) {
		return "Reports";
	}
	
	String[] getResourceListing(String path) throws IOException, URISyntaxException {
		URL dirURL = this.getClass().getResource(path);
		return new File(dirURL.toURI()).list();		
		
	}
	@Override
	public void initUI() {
		
		table.setSizeFull();
        table.setPageLength(15);
        table.addStyleName("striped");
        
        table.addContainerProperty("name", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionTask"), null, null);
        table.addContainerProperty("description", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionTask"), null, null);
	}
	
	public void refreshTable() {
		table.removeAllItems();
		try {
			File aReportDir = new File(ProcessbaseApplication.getCurrent().getContext().getBaseDirectory(), "Reports");
			List<ReportItem> list = ReportingDataStore.getInstance().listReportsFromDirectory(aReportDir, false);
			for (ReportItem reportItem : list) {
				Item row=table.addItem(reportItem);
				row.getItemProperty("name").setValue(reportItem.getReportName());
				row.getItemProperty("description").setValue(reportItem.getDescription());
			}
			
		} catch (Exception e) {
			
		}
	}

}
