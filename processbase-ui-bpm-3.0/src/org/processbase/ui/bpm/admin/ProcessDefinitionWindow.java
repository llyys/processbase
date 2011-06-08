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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.processbase.ui.bpm.admin.process.CustomUiPanel;
import org.processbase.ui.bpm.admin.process.DescriptionPanel;
import org.processbase.ui.bpm.admin.process.LegislationPanel;
import org.processbase.ui.bpm.admin.process.ProcessAccessPanel;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.ITabsheetPanel;
import org.processbase.ui.core.template.PbWindow;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 * @author mgubaidullin
 */
public class ProcessDefinitionWindow extends PbWindow implements
        ClickListener,
        Upload.SucceededListener,
        Upload.FailedListener,
        Upload.Receiver,
        TabSheet.SelectedTabChangeListener {

    private ProcessDefinition processDefinition = null;
    
    private ButtonBar buttons = new ButtonBar();
    private Button closeBtn;
        
    private Upload upload = new Upload("", (Upload.Receiver) this);
    private File file;
    private String filename;
    private String originalFilename;
    private String fileExt;
    
    private TabSheet tabSheet = new TabSheet();
    private DescriptionPanel descPanel=new DescriptionPanel();
    private CustomUiPanel uiPanel=new CustomUiPanel();
    private ProcessAccessPanel accessPanel=new ProcessAccessPanel();
    private LegislationPanel legislationPanel=new LegislationPanel();
    
    public ProcessDefinitionWindow(ProcessDefinition processDefinition) {
        super(processDefinition.getLabel());
        this.processDefinition = processDefinition;
    }

    public void initUI() {
        try {
            String caption = processDefinition.getLabel() != null ? processDefinition.getLabel() : processDefinition.getName();
            setCaption(caption + " (v." + processDefinition.getVersion() + ")");
            
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);
            
            descPanel.setProcessDefinition(processDefinition);
            descPanel.setParentWindow(this);
            descPanel.initUI();
            tabSheet.addTab(descPanel, ProcessbaseApplication.getCurrent().getPbMessages().getString("tabDescription"), null);
            
            uiPanel.setProcessDefinition(processDefinition);
            uiPanel.setParentWindow(this);
            uiPanel.initUI();
            uiPanel.onActivate(false);
            tabSheet.addTab(uiPanel, ProcessbaseApplication.getCurrent().getPbMessages().getString("tabCustomUI"), null);

            accessPanel.setProcessDefinition(processDefinition);
            accessPanel.setParentWindow(this);
            accessPanel.initUI();
            accessPanel.onActivate(false);
            tabSheet.addTab(accessPanel, ProcessbaseApplication.getCurrent().getPbMessages().getString("processAccess"), null);
            
            
            legislationPanel.setProcessDefinition(processDefinition);
            legislationPanel.setParentWindow(this);
            legislationPanel.initUI();
            legislationPanel.onActivate(false);
            tabSheet.addTab(legislationPanel, legislationPanel.getCaption(), null);

            tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);
            tabSheet.setSizeFull();
            tabSheet.addListener((TabSheet.SelectedTabChangeListener) this);
            
            layout.addComponent(tabSheet);
            layout.setExpandRatio(tabSheet, 1);

            closeBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnClose"), this);
                       
            Label expand = new Label("");
            getButtons().addComponent(expand);
            getButtons().setExpandRatio(expand, 1);
            
            getButtons().addButton(closeBtn);
            getButtons().setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
            getButtons().setMargin(false);
            getButtons().setHeight("30px");
            getButtons().setWidth("100%");
            layout.addComponent(getButtons());
            layout.setWidth("800px");
            layout.setHeight("400px");
            setResizable(false);
            setModal(true);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
    

    public void buttonClick(ClickEvent event) {
        try {
           
            close();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }


    public void uploadSucceeded(SucceededEvent event) {
        try {
//            byte[] readData = new byte[new Long(event.getLength()).intValue()];
//            FileInputStream fis = new FileInputStream(file);
//            int i = fis.read(readData);
//            HashMap<String, String> urlMap = (HashMap<String, String>) XMLManager.createObject(new String(readData, "UTF-8"));
//            for (String key : urlMap.keySet()) {
//                PbPortlet.getCurrent().bpmModule.addProcessMetaData(processDefinition.getUUID(), key, urlMap.get(key));
//            }
//            fis.close();
//            file.delete();
//            refreshTable();
        } catch (Exception ex) {
            ex.printStackTrace();
            getWindow().showNotification(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void uploadFailed(FailedEvent event) {
        event.getReason().printStackTrace();
        showError(event.getReason().getMessage());
    }

    public OutputStream receiveUpload(
            String filename, String MIMEType) {
        this.originalFilename = filename;
        this.filename = UUID.randomUUID().toString();
        FileOutputStream fos = null;
        try {
            file = new File(this.filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return fos;
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
    	ITabsheetPanel selectedTab = (ITabsheetPanel)event.getTabSheet().getSelectedTab();
		
		for (Iterator<Component> i = tabSheet.getComponentIterator(); i.hasNext(); )
		{
			ITabsheetPanel panel=(ITabsheetPanel)i.next();
			if(panel!=selectedTab)
				panel.onActivate(false);
		}
		selectedTab.onActivate(true);
    }
    
    
	public ButtonBar getButtons() {
		return buttons;
	}
}
