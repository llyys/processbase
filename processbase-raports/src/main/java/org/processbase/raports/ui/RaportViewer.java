package org.processbase.raports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.model.metadata.DateTimePropertyType;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.raports.birt.ReportService;
import org.processbase.raports.birt.generator.ReportItem;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.PbPanel;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.Layout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class RaportViewer extends PbPanel {

	private final ReportItem reportItem;
	private final HorizontalLayout  pForm = new HorizontalLayout();
	private final Label raportControl=new Label();
	private ReportService reportService= new ReportService();
	private OptionGroup timeUnit=null;
	private PopupDateField datetimeStart=null;
	private PopupDateField datetimeEnd=null;
	private ComboBox processesUUIDs=null;
	private ComboBox activityUUIDs=null;
	
	public RaportViewer(ReportItem reportItem) {
		this.reportItem = reportItem;
		
		// Make the FormLayout shrink to its contents 
		pForm.setSizeUndefined();
		
		addComponent(pForm);
		
		
		raportControl.setWidth("100%");
		raportControl.setContentMode(Label.CONTENT_XHTML);
		
		
		addComponent(raportControl);
		setMargin(true);		
	}

	@Override
	public void initUI() {
		if(reportItem.getConfigurationElements().size()>0){
			//pForm.setCaption("Raporti parameetrid");
			 // Set the footer layout. 
			//pForm.setFooter(new HorizontalLayout());
			Layout layout = pForm;
			pForm.setSpacing(true);
			
			for (String element : reportItem.getConfigurationElements()) {
				Component component=null;
				if("TimeUnit".equalsIgnoreCase(element)){
					 component = timeUnit = new OptionGroup(element, 
							 Arrays.asList(new String[] {"YEAR","MONTH","DAY"}));
					 timeUnit.setImmediate(true);
					 timeUnit.addListener(new Property.ValueChangeListener() {
						
						public void valueChange(ValueChangeEvent event) {
							int resolution=PopupDateField.RESOLUTION_DAY;
							
							if("YEAR".equals(event.getProperty().getValue())){
								resolution= PopupDateField.RESOLUTION_YEAR;
							}
							if("MONTH".equals(event.getProperty().getValue())){
								resolution= PopupDateField.RESOLUTION_MONTH;
							}
							if("DAY".equals(event.getProperty().getValue())){
								resolution= PopupDateField.RESOLUTION_DAY;
							}
							if(datetimeStart!=null)
								datetimeStart.setResolution(resolution);
							if(datetimeEnd!=null)
								datetimeEnd.setResolution(resolution);
						}
					});
				}
				else if(element.endsWith("StartDate")){
					PopupDateField datetime = new PopupDateField("Alates");					
					datetime.setResolution(PopupDateField.RESOLUTION_DAY);
					component=datetimeStart=datetime;
				}
				else if(element.endsWith("EndDate")){
					PopupDateField datetime = new PopupDateField("Kuni");					
					datetime.setResolution(PopupDateField.RESOLUTION_DAY);
					component=datetimeEnd=datetime;
				}
				else if(element.equals("ActivityUUID")){
					component=activityUUIDs = new ComboBox(element);
				}
				else if(element.equals("ProcessUUID")){
					try {
						Set<LightProcessDefinition> processDefinitions = ProcessbaseApplication.getCurrent().getBpmModule().getLightProcessDefinitions();
						List<String> processes=new ArrayList<String>();
						
						for (LightProcessDefinition pd : processDefinitions) {
							processes.add(pd.getUUID().toString());
						}
						Collections.sort(processes);
						component=processesUUIDs = new ComboBox(element,processes);
						processesUUIDs.setNullSelectionAllowed(false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				if(component==null)					 
				layout.addComponent(new TextField(element));					
			}
			
			if(timeUnit!=null)
				layout.addComponent(timeUnit);
			
			if(datetimeStart!=null)
				layout.addComponent(datetimeStart);
			
			if(datetimeEnd!=null)
				layout.addComponent(datetimeEnd);
			
			if(processesUUIDs!=null)
				layout.addComponent(processesUUIDs);
			
			if(activityUUIDs!=null)
			{
				processesUUIDs.addListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try{
							String selectedProcessUUID=(String) event.getProperty().getValue();
							activityUUIDs.removeAllItems();
							Set<LightActivityInstance> processActivities = ProcessbaseApplication.getCurrent().getBpmModule().getActivityInstances(new ProcessDefinitionUUID(selectedProcessUUID));
							List<String> processes=new ArrayList<String>();
							
							for (LightActivityInstance pd : processActivities) {
								//processes.add(pd.getUUID().toString());
								activityUUIDs.addItem(pd.getUUID().toString());
							}
							//Collections.sort(processes);
							activityUUIDs.setEnabled(true);
							
						}catch(Exception e){
							
						}
					}
				});
				layout.addComponent(activityUUIDs);
				activityUUIDs.setNullSelectionAllowed(false);
				
			}
			
			Button okbutton = new Button("Ok");
			okbutton.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					
					try {
						
						Map params=new HashMap();
						
						if(timeUnit!=null) 
							params.put("TimeUnit", timeUnit.getValue());						
						if(datetimeStart!=null) 
							params.put("StartDate", datetimeStart.getValue());
						if(datetimeEnd!=null) 
							params.put("EndDate", datetimeEnd.getValue());
						if(processesUUIDs!=null) 
							params.put("ProcessUUID", processesUUIDs.getValue());
						
						String result=reportService.runAndRender(reportItem, params, new HTMLRenderOption());
						raportControl.setValue(result);
						
					} catch (Exception e) {
						raportControl.setValue(e.getMessage());
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			});
			layout.addComponent(okbutton);
			
		}
		else{//execute raport instantly
			try {
				
				String result=reportService.runAndRender(reportItem, null, new HTMLRenderOption());
				raportControl.setValue(result);
				
			} catch (Exception e) {
				raportControl.setValue(e.getMessage());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
	}

}
