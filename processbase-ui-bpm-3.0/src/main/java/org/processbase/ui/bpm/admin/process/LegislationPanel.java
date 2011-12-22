package org.processbase.ui.bpm.admin.process;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.processbase.ui.bpm.admin.ProcessDefinitionWindow;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ITabsheetPanel;
import org.processbase.ui.core.template.LazyLoadingLayout;
import org.processbase.ui.core.template.LazyLoadingLayout.LazyLoader;
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

public class LegislationPanel extends PbPanel implements ITabsheetPanel, LazyLoader {
	private Table tableLegislations = new Table();
	private Button addBtn=null;
	private Button saveBtn=null;
	 @Override
	 public void initUI() {
		 if(super.isInitialized())
			 return;
	   super.setInitialized(true);
	   setSpacing(true);
       setMargin(true);
	   tableLegislations.addContainerProperty("name", String.class, null, "Resursi nimetus", null, null);
	   
       tableLegislations.setWidth("100%");
              
       
       tableLegislations.setEditable(false);
       tableLegislations.setImmediate(false);
       addComponent(tableLegislations);
       tableLegislations.setPageLength(11);
       setSizeFull();
       refreshTable();
	 }
	 
	 public void onActivate(boolean isActive) {		 
		 if(isActive && !isInitialized())
			 initUI();
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
		return ProcessbaseApplication.getString("processResoucses", "Process resources");		
	}
	
	public void refreshTable()
	{
		try {
			Map<String, byte[]> businessArchive = ProcessbaseApplication.getCurrent().getBpmModule().getBusinessArchive(this.processDefinition.getUUID());
			for (Entry<String, byte[]> element : businessArchive.entrySet()) {
				Item addItem = tableLegislations.addItem(element.getKey());
				addItem.getItemProperty("name").setValue(element.getKey());
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
	
	public String getLazyLoadingMessage() {
		// TODO Auto-generated method stub
		return "Loen protsessi definitsioone";
	}

	public Component lazyLoad(LazyLoadingLayout layout) {
		// TODO Auto-generated method stub
		initUI();
		return this;
	}
	
}
