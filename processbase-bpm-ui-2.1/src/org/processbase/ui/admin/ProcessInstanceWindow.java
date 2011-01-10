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

import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.ByteArraySource;
import org.processbase.ui.template.PbWindow;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author mgubaidullin
 */
public class ProcessInstanceWindow extends PbWindow implements
        ClickListener {

    private LightProcessInstance process;
    private ButtonBar buttons = new ButtonBar();
    private Button closeBtn = new Button(PbPortlet.getCurrent().messages.getString("btnClose"), this);
    private Button refreshBtn = new Button(PbPortlet.getCurrent().messages.getString("btnRefresh"), this);
    private Button deleteBtn = new Button(PbPortlet.getCurrent().messages.getString("btnDelete"), this);
    private Button cancelBtn = new Button(PbPortlet.getCurrent().messages.getString("btnCancel"), this);
    private ActivitiesPanel activitiesPanel;
    private Embedded processImage = null;
    private SplitPanel layout = new SplitPanel();
    private VerticalLayout imageLayout = new VerticalLayout();
    private boolean managed = false;

    public ProcessInstanceWindow(LightProcessInstance process, boolean managed) {
        super(null);
        this.process = process;
        this.managed = managed;
    }

    public void exec() {
        try {
            ByteArraySource bas = new ByteArraySource(PbPortlet.getCurrent().bpmModule.getProcessDiagramm(process.getProcessInstanceUUID()));
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

            activitiesPanel = new ActivitiesPanel(process.getProcessInstanceUUID());
            activitiesPanel.refreshTable();

            buttons.addButton(deleteBtn);
            buttons.setComponentAlignment(deleteBtn, Alignment.MIDDLE_LEFT);
            buttons.addButton(cancelBtn);
            buttons.setComponentAlignment(cancelBtn, Alignment.MIDDLE_LEFT);
            buttons.addButton(refreshBtn);
            buttons.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(refreshBtn, 1);
            buttons.addButton(closeBtn);
            buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(true, true, true, true);
            buttons.setWidth("100%");

            layout.setMargin(false);
            layout.setSizeFull();
//            Float f = layout.getHeight() - activitiesPanel.getHeight();
            layout.setSplitPosition(400, SplitPanel.UNITS_PIXELS);
            layout.setLocked(true);
            layout.setStyleName(Reindeer.SPLITPANEL_SMALL);
            layout.addComponent(imageLayout);
            layout.addComponent(activitiesPanel);

            if (managed) {
                activitiesPanel.addComponent(buttons);
            }

            setContent(layout);
            setCaption(PbPortlet.getCurrent().messages.getString("ProcessActivities") + " \"" + process.getProcessDefinitionUUID().getProcessName() + " " + process.getProcessDefinitionUUID().getProcessVersion() + " \"");
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
        }
    }

    private void delete() {
        ConfirmDialog.show(PbPortlet.getCurrent().getMainWindow(),
                PbPortlet.getCurrent().messages.getString("windowCaptionConfirm"),
                PbPortlet.getCurrent().messages.getString("questionDeleteProcessInstance"),
                PbPortlet.getCurrent().messages.getString("btnYes"),
                PbPortlet.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                PbPortlet.getCurrent().bpmModule.deleteProcessInstance(process.getProcessInstanceUUID());
                                showInformation(PbPortlet.getCurrent().messages.getString("executedSuccessfully"));
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void cancel() {
        ConfirmDialog.show(PbPortlet.getCurrent().getMainWindow(),
                PbPortlet.getCurrent().messages.getString("windowCaptionConfirm"),
                PbPortlet.getCurrent().messages.getString("questionCancelProcessInstance"),
                PbPortlet.getCurrent().messages.getString("btnYes"),
                PbPortlet.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                PbPortlet.getCurrent().bpmModule.cancelProcessInstance(process.getProcessInstanceUUID());
                                showInformation(PbPortlet.getCurrent().messages.getString("executedSuccessfully"));
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
}
