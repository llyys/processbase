/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.ui.bpm.admin;

import java.io.FileNotFoundException;
import java.util.UUID;

import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.ByteArraySource;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class ProcessInstanceWindow extends PbWindow implements Button.ClickListener {

    private LightProcessInstance process;
    private ButtonBar buttons = new ButtonBar();
    private Button closeBtn;
    private Button refreshBtn;
    private Button deleteBtn;
    private Button cancelBtn;
    private ActivitiesPanel activitiesPanel;
    private Embedded processImage = null;
    private VerticalSplitPanel layout = new VerticalSplitPanel();
    private VerticalLayout imageLayout = new VerticalLayout();
    private boolean managed = false;
    private TabSheet tabSheet = new TabSheet();
	private ProcessInstanceUUID processInstanceUUID;
    
    public ProcessInstanceWindow(LightProcessInstance process, boolean managed) {
        super(null);
        this.process = process;
        this.processInstanceUUID=process.getProcessInstanceUUID();
        this.managed = managed;
    }

    

	public ProcessInstanceWindow(ProcessInstanceUUID processInstanceUUID, boolean managed2) {
		BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
		try {
			 this.processInstanceUUID=processInstanceUUID;
			this.process = bpmModule.getLightProcessInstance(this.processInstanceUUID);
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initUI() {
        try {
        	setContent(tabSheet);
        	tabSheet.addTab(layout, ProcessbaseApplication.getString("tableCaptionProcessName", "Process"));
        	tabSheet.setSizeFull();
            
            BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
            if(managed){
	            processInstanceUUID = process.getProcessInstanceUUID();
				ProcessVariablesPanel variablesPanel=new ProcessVariablesPanel(bpmModule.getProcessInstance(processInstanceUUID));
	            variablesPanel.initUI();
	            
	            tabSheet.addTab(variablesPanel, ProcessbaseApplication.getString("processVariables"));
            }
            
			byte[] processDiagramm = bpmModule.getProcessDiagramm(process);
			ByteArraySource bas = new ByteArraySource(processDiagramm);
			String processImageId=UUID.randomUUID().toString()+".png";
            StreamResource imageResource = new StreamResource(bas, processImageId, this.getApplication());
            imageResource.setCacheTime(1000);
            processImage = new Embedded("", imageResource);

            imageLayout.setSizeUndefined();
            imageLayout.addComponent(processImage);
            imageLayout.setStyleName(Reindeer.LAYOUT_WHITE);
            imageLayout.setMargin(false);
            imageLayout.setSpacing(false);
            
            closeBtn = new Button(ProcessbaseApplication.getString("btnClose"), this);
            if(managed){
	            refreshBtn = new Button(ProcessbaseApplication.getString("btnRefresh"), this);
	            deleteBtn = new Button(ProcessbaseApplication.getString("btnDeleteProcess", "Kustuta menetlus"), this);
	            cancelBtn = new Button(ProcessbaseApplication.getString("btnCancelProcess", "Katkesta menetlus"), this);
            
	            buttons.addButton(deleteBtn);
	            buttons.setComponentAlignment(deleteBtn, Alignment.MIDDLE_LEFT);
	            buttons.addButton(cancelBtn);
	            buttons.setComponentAlignment(cancelBtn, Alignment.MIDDLE_LEFT);
	            buttons.addButton(refreshBtn);
	            buttons.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);
	            buttons.setExpandRatio(refreshBtn, 1);
            }
            buttons.addButton(closeBtn);
            buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(true, true, true, true);
            buttons.setWidth("100%");

            layout.setMargin(false);
            layout.setSizeFull();
//            Float f = layout.getHeight() - activitiesPanel.getHeight();
            layout.setSplitPosition(400, VerticalSplitPanel.UNITS_PIXELS);
            layout.setLocked(true);
            layout.setStyleName(Reindeer.SPLITPANEL_SMALL);
            layout.addComponent(imageLayout);

            activitiesPanel = new ActivitiesPanel(processInstanceUUID);
            layout.addComponent(activitiesPanel);
            activitiesPanel.initUI();
            activitiesPanel.refreshTable();

            if (managed) {
                activitiesPanel.addComponent(buttons);
            }

            String pdUUID = process.getProcessDefinitionUUID().toString();
            String[] tmp =  pdUUID.split("--");
            setCaption(ProcessbaseApplication.getString("ProcessActivities")
                    + " \"" + tmp[0] + " " + tmp[1] + "#" + process.getNb() + " \"");
            setWidth("90%");
            setHeight("95%");
            setModal(true);
            setResizable(false);
            
            if (managed) {
	            if(InstanceState.FINISHED.equals(process.getInstanceState()) || 
	            		InstanceState.CANCELLED.equals(process.getInstanceState())){
	            	cancelBtn.setVisible(false);
	            }
            }
            
        } catch(FileNotFoundException ex){
        	getParent().showNotification(ex.getMessage());        	
        	close();
        	
        }
        catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(refreshBtn)) {
                activitiesPanel.refreshTable();
            } else if (event.getButton().equals(deleteBtn)) {
                delete();
            } else if (event.getButton().equals(cancelBtn)) {
                cancel();
            } else if (event.getButton().equals(closeBtn)) {
                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private void delete() {
        ConfirmDialog.show(getApplication().getMainWindow(),
                ProcessbaseApplication.getString("windowCaptionConfirm"),
                ProcessbaseApplication.getString("questionDeleteProcessInstance"),
                ProcessbaseApplication.getString("btnYes"),
                ProcessbaseApplication.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                ProcessbaseApplication.getCurrent().getBpmModule().deleteProcessInstance(process.getProcessInstanceUUID());
                                showInformation(ProcessbaseApplication.getString("executedSuccessfully"));
                                close();
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                });
    }

    private void cancel() {
        ConfirmDialog.show(getApplication().getMainWindow(),
                ProcessbaseApplication.getString("windowCaptionConfirm"),
                ProcessbaseApplication.getString("questionCancelProcessInstance"),
                ProcessbaseApplication.getString("btnYes"),
                ProcessbaseApplication.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                ProcessbaseApplication.getCurrent().getBpmModule().cancelProcessInstance(process.getProcessInstanceUUID());
                                showInformation(ProcessbaseApplication.getString("executedSuccessfully"));
                                close();
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                });
    }
}
