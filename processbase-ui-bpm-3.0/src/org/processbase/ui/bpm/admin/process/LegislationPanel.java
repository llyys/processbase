package org.processbase.ui.bpm.admin.process;

import java.util.UUID;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.processbase.ui.bpm.admin.ProcessDefinitionWindow;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ITabsheetPanel;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.PbTableFieldFactory;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.core.template.TableLinkButton;

import com.vaadin.data.Item;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Runo;

public class LegislationPanel extends PbPanel implements ITabsheetPanel {
	private Table tableLegislations = new Table();
	private Button addBtn=null;
	private Button saveBtn=null;
	 @Override
	 public void initUI() {
	   
	   setSpacing(true);
       
       addBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnAdd")
				, new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						try {
							addLegislationRow(null);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							showError(e.getMessage());
							e.printStackTrace();
						}
					}					
				});
		
		addBtn.setStyleName(Runo.BUTTON_SMALL);
		addComponent(addBtn);
		setComponentAlignment(addBtn, Alignment.MIDDLE_RIGHT);
       
		saveBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnSave")
				, new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						/*try {
							addLegislationRow(null);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							showError(e.getMessage());
							e.printStackTrace();
						}*/
					}					
				});
		
		
	   tableLegislations.addContainerProperty("name", Component.class, null, "Õigusakti nimetus", null, null);
	   tableLegislations.setColumnWidth("name", 300);
       tableLegislations.addContainerProperty("url", Component.class, null, "Õigusakti aadress", null, null);
       
       tableLegislations.addContainerProperty("actions",Component.class,null,ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"), null, null);
       tableLegislations.setColumnWidth("actions", 50);
       
       tableLegislations.setWidth("100%");
       tableLegislations.setPageLength(10);
       
       
       tableLegislations.setTableFieldFactory(new PbTableFieldFactory());
       
       tableLegislations.setEditable(true);
       tableLegislations.setImmediate(true);
       addComponent(tableLegislations);
       setSizeFull();
       refreshTable();
	 }
	 
	 public void onActivate(boolean isActive) {		 
		 
	 }
	 
	  
	 private void addLegislationRow(Legislation row) {
		 try {
			 String uuid = UUID.randomUUID().toString();
				Legislation legislation = new Legislation();
				Item woItem = tableLegislations.addItem(legislation);
				
				TextField urlField = new TextField();
				urlField.setInputPrompt("Sisestage õigusaktile viitav URL");
				urlField.setWidth("100%");
				urlField.setNullRepresentation("");

				TextField nameField = new TextField();
				nameField.setInputPrompt("Sisestage õigusakti nimetus");
				nameField.setWidth("100%");
				nameField.setNullRepresentation("");
				
				woItem.getItemProperty("url").setValue(urlField);
				woItem.getItemProperty("name").setValue(nameField);
				
				TableLinkButton tlb = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDelete"), "icons/cancel.png", uuid, 
						new Button.ClickListener() {
							public void buttonClick(ClickEvent event) {
								/*TableLinkButton tlb = (TableLinkButton) event .getButton();
								String uuid = (String) tlb.getTableValue();
								tableMembership.removeItem(uuid);
								if (!uuid.startsWith("NEW_MEMBERSHIP_UUID")) {
									deletedMembership.add(uuid);
								}*/
							}
						}, Constants.ACTION_DELETE);
				woItem.getItemProperty("actions").setValue(tlb);
				
		} catch (Exception e) {
			showError(e.getMessage());
		}
		
	}
	 
	private void showError(String message) {			
		 ((PbWindow) getWindow()).showError(message);
	}
	 
	 public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}
	private ProcessDefinition processDefinition = null;
	
	@Override
	public String getCaption(){
		return ProcessbaseApplication.getCurrent().getPbMessages().getString("processLegislationInfo");		
	}
	
	public void refreshTable()
	{
		tableLegislations.removeAllItems();
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void setParentWindow(ProcessDefinitionWindow parentWindow) {
		this.parentWindow = parentWindow;
	}

	public ProcessDefinitionWindow getParentWindow() {
		return parentWindow;
	}

	public class Legislation{
		public Legislation()
		{
			uuid=UUID.randomUUID().toString();
			name="";
			url="";
		}
		private String uuid;
		private String name;
		private String url;
		
		public void setUrl(String url) {
			this.url = url;
		}
		public String getUrl() {
			return url;
		}
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
		public String getUuid() {
			return uuid;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	private ProcessDefinitionWindow parentWindow;
	
}
