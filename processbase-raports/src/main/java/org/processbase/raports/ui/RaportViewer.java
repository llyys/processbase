package org.processbase.raports.ui;

import org.processbase.raports.birt.generator.ReportItem;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.PbPanel;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;

public class RaportViewer extends PbPanel {

	private final ReportItem reportItem;
	private final Form parameterForm = new Form();
	private final RaportViewerControl raportControl;
	public RaportViewer(ReportItem reportItem) {
		this.reportItem = reportItem;
		raportControl=new RaportViewerControl(reportItem);		
	}

	@Override
	public void initUI() {
		if(reportItem.getConfigurationElements().size()>0){
			/*BeanItem<ReportItem> reportParameterItem = new BeanItem<ReportItem>(reportItem);
			
			parameterForm.setCaption(ProcessbaseApplication.getString("raportParameters", "Raporti parameetrid"));
			parameterForm.setWriteThrough(false);
			parameterForm.setInvalidCommitted(false);
			parameterForm.setFormFieldFactory(new ReportParameterFieldFactory());
			parameterForm.setItemDataSource(reportParameterItem);
			addComponent(parameterForm);
			
			Button apply = new Button(ProcessbaseApplication.getString("raportRun", "Käivita"), new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	                try {
	                    parameterForm.commit();
	                } catch (Exception e) {
	                    // Ignored, we'll let the Form handle the errors
	                }
	            }
	        });
			parameterForm.getFooter().addComponent(buttons);*/
		}
		else{//execute raport instantly
		 addComponent(raportControl);
		 raportControl.initUI();
		}
		
	}

}
