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
package org.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.UUID;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.ByteArraySource;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.PbTableFieldFactory;
import org.processbase.ui.util.XMLManager;

/**
 *
 * @author mgubaidullin
 */
public class ProcessInstanceWindow extends PbWindow implements
        ClickListener {

    private LightProcessInstance process;
    private ButtonBar buttons = new ButtonBar();
    private Button closeBtn = null;
    private Button refreshBtn = null;
    private BPMModule bpmModule;
    private ActivitiesPanel activitiesPanel;
    private Embedded processImage = null;
    private SplitPanel layout = new SplitPanel();
    private VerticalLayout imageLayout = new VerticalLayout();

    public ProcessInstanceWindow(PortletApplicationContext2 portletApplicationContext2, BPMModule bpmModule, ResourceBundle messages, LightProcessInstance process) {
        super(portletApplicationContext2);
        this.process = process;
        this.bpmModule = bpmModule;
    }

    public void exec() {
        try {
            closeBtn = new Button(messages.getString("btnClose"), this);
            refreshBtn = new Button(messages.getString("btnRefresh"), this);

            ByteArraySource bas = new ByteArraySource(bpmModule.getProcessDiagramm(process.getProcessInstanceUUID()));
            StreamResource imageResource = new StreamResource(bas, "processInstance.png", this.getApplication());
            imageResource.setCacheTime(1000);
            processImage = new Embedded("", imageResource);
//            imageLayout.setWidth("100%");
//            imageLayout.setHeight("500px");
            imageLayout.setSizeUndefined();
            imageLayout.addComponent(processImage);
            imageLayout.setStyleName(Reindeer.LAYOUT_WHITE);
            imageLayout.setMargin(false);
            imageLayout.setSpacing(false);

            activitiesPanel = new ActivitiesPanel(this.getPortletApplicationContext2(), bpmModule, messages, process.getProcessInstanceUUID());
            activitiesPanel.refreshTable();

            buttons.addButton(refreshBtn);
            buttons.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);
            buttons.addButton(closeBtn);
            buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(true, true, false, false);

            layout.setMargin(false);
            layout.setSizeFull();
//            Float f = layout.getHeight() - activitiesPanel.getHeight();
            layout.setSplitPosition(400, SplitPanel.UNITS_PIXELS);
            layout.setLocked(true);
            layout.setStyleName(Reindeer.SPLITPANEL_SMALL);
            layout.addComponent(imageLayout);
            layout.addComponent(activitiesPanel);
            
//            layout.addComponent(activitiesPanel);
//            layout.addComponent(buttons);
//            layout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
            setContent(layout);
            setCaption(messages.getString("ProcessActivities") + " \"" + process.getProcessDefinitionUUID().getProcessName() + " " + process.getProcessDefinitionUUID().getProcessVersion() + " \"");
            setWidth("90%");
            setHeight("95%");
            setModal(true);
            setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(refreshBtn)) {
                activitiesPanel.refreshTable();
            } else {
                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
}
