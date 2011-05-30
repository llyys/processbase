package org.processbase.ui.bpm.admin.process;

import java.util.HashMap;

import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.PbTableFieldFactory;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.core.template.WorkPanel;

import com.vaadin.data.Item;
import com.vaadin.ui.Table;
/**
*
* @author llyys
*/
public class CustomUiPanel extends PbPanel {
	
	private Table activitiesTable = new Table();
	 @Override
	 public void initUI() {
		
		setMargin(false, false, false, false);
		activitiesTable.setSizeFull();
		addComponent(activitiesTable);
		        
        setSizeFull();
        
        activitiesTable.addContainerProperty("activityLabel", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActivityName"), null, null);
        activitiesTable.addContainerProperty("url", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tabCaptionTaskURL"), null, null);
        activitiesTable.setColumnWidth("url", 300);
        activitiesTable.setTableFieldFactory(new PbTableFieldFactory());
        activitiesTable.setEditable(true);
        activitiesTable.setImmediate(true);
        
        refreshTable();                
	 }
	 
	 public void refreshTable() {
	        try {
	            activitiesTable.removeAllItems();
	            // process level Custom UI
	            Item woItem = activitiesTable.addItem(processDefinition);
	            woItem.getItemProperty("activityLabel").setValue(processDefinition.getLabel() != null ? processDefinition.getLabel() : processDefinition.getName());
	            String url = processDefinition.getAMetaData(processDefinition.getUUID().toString());
	            woItem.getItemProperty("url").setValue(url != null ? url : new String());
	            // activity level Custom UI
	            for (ActivityDefinition activityDefinition : processDefinition.getActivities()) {
	                if (activityDefinition.isTask()) {
	                    woItem = activitiesTable.addItem(activityDefinition);
//	                    woItem.getItemProperty("activityUUID").setValue(activityDefinition.getUUID().toString());
	                    woItem.getItemProperty("activityLabel").setValue(activityDefinition.getLabel());
	                    url = processDefinition.getAMetaData(activityDefinition.getUUID().toString());
	                    woItem.getItemProperty("url").setValue(url != null ? url : new String());
	                }
	            }
	            activitiesTable.setSortContainerPropertyId("activityLabel");
	            activitiesTable.setSortAscending(true);
	            activitiesTable.sort();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            showError(ex.getMessage());
	        }
	    }
	 public void showError(String errorMessage) {
		 ((PbWindow) getWindow()).showError(errorMessage);
	 }
	  
	 private HashMap<String, String> getCurrentTableValues() {
	        HashMap<String, String> urlMap = new HashMap<String, String>();
	        for (Object object : activitiesTable.getContainerDataSource().getItemIds()) {
	            ActivityDefinition activityDefinition = (ActivityDefinition) object;
	            if (activitiesTable.getItem(object).getItemProperty("url") != null && activitiesTable.getItem(object).getItemProperty("url").toString().length() > 0) {
	                urlMap.put(activityDefinition.getUUID().toString(), activitiesTable.getItem(object).getItemProperty("url").toString());
	            }
	        }
	        return urlMap;
	    }

	    public void save() throws Exception {
	        for (Object object : activitiesTable.getContainerDataSource().getItemIds()) {
	            if (object instanceof ProcessDefinition) { // process level Custom UI
	                ProcessDefinition pd = (ProcessDefinition) object;
	                if (activitiesTable.getItem(object).getItemProperty("url") != null && !activitiesTable.getItem(object).getItemProperty("url").toString().isEmpty()) {
	                    ProcessbaseApplication.getCurrent().getBpmModule().addProcessMetaData(processDefinition.getUUID(), pd.getUUID().toString(), activitiesTable.getItem(object).getItemProperty("url").toString());
	                } else if (activitiesTable.getItem(object).getItemProperty("url") != null && activitiesTable.getItem(object).getItemProperty("url").toString().isEmpty()) {
	                    ProcessbaseApplication.getCurrent().getBpmModule().deleteProcessMetaData(processDefinition.getUUID(), pd.getUUID().toString());
	                }

	            } else if (object instanceof ActivityDefinition) { // activity level Custom UI
	                ActivityDefinition activityDefinition = (ActivityDefinition) object;
	                if (activitiesTable.getItem(object).getItemProperty("url") != null && !activitiesTable.getItem(object).getItemProperty("url").toString().isEmpty()) {
	                    ProcessbaseApplication.getCurrent().getBpmModule().addProcessMetaData(processDefinition.getUUID(), activityDefinition.getUUID().toString(), activitiesTable.getItem(object).getItemProperty("url").toString());
	                } else if (activitiesTable.getItem(object).getItemProperty("url") != null && activitiesTable.getItem(object).getItemProperty("url").toString().isEmpty()) {
	                    ProcessbaseApplication.getCurrent().getBpmModule().deleteProcessMetaData(processDefinition.getUUID(), activityDefinition.getUUID().toString());
	                }
	            }
	        }
	    }
	 	public void setProcessDefinition(ProcessDefinition processDefinition) {
			this.processDefinition = processDefinition;
		}
		private ProcessDefinition processDefinition = null;
}
