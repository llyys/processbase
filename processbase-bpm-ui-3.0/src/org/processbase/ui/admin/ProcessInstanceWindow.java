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
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.ui.Processbase;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.ByteArraySource;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.ConfirmDialog;

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

    public ProcessInstanceWindow(LightProcessInstance process, boolean managed) {
        super(null);
        this.process = process;
        this.managed = managed;
    }

    public void initUI() {
        try {
            setContent(layout);
            ByteArraySource bas = new ByteArraySource(((Processbase) getApplication()).getBpmModule().getProcessDiagramm(process));
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

            closeBtn = new Button(((Processbase) getApplication()).getMessages().getString("btnClose"), this);
            refreshBtn = new Button(((Processbase) getApplication()).getMessages().getString("btnRefresh"), this);
            deleteBtn = new Button(((Processbase) getApplication()).getMessages().getString("btnDelete"), this);
            cancelBtn = new Button(((Processbase) getApplication()).getMessages().getString("btnCancel"), this);

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
            layout.setSplitPosition(400, VerticalSplitPanel.UNITS_PIXELS);
            layout.setLocked(true);
            layout.setStyleName(Reindeer.SPLITPANEL_SMALL);
            layout.addComponent(imageLayout);

            activitiesPanel = new ActivitiesPanel(process.getProcessInstanceUUID());
            layout.addComponent(activitiesPanel);
            activitiesPanel.initUI();
            activitiesPanel.refreshTable();

            if (managed) {
                activitiesPanel.addComponent(buttons);
            }

            String pdUUID = process.getProcessDefinitionUUID().toString();
            setCaption(((Processbase) getApplication()).getMessages().getString("ProcessActivities")
                    + " \"" + pdUUID.split("--")[0] + " " + pdUUID.split("--")[1] + " \"");
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
        ConfirmDialog.show(getApplication().getMainWindow(),
                ((Processbase) getApplication()).getMessages().getString("windowCaptionConfirm"),
                ((Processbase) getApplication()).getMessages().getString("questionDeleteProcessInstance"),
                ((Processbase) getApplication()).getMessages().getString("btnYes"),
                ((Processbase) getApplication()).getMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                ((Processbase) getApplication()).getBpmModule().deleteProcessInstance(process.getProcessInstanceUUID());
                                showInformation(((Processbase) getApplication()).getMessages().getString("executedSuccessfully"));
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void cancel() {
        ConfirmDialog.show(getApplication().getMainWindow(),
                ((Processbase) getApplication()).getMessages().getString("windowCaptionConfirm"),
                ((Processbase) getApplication()).getMessages().getString("questionCancelProcessInstance"),
                ((Processbase) getApplication()).getMessages().getString("btnYes"),
                ((Processbase) getApplication()).getMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                ((Processbase) getApplication()).getBpmModule().cancelProcessInstance(process.getProcessInstanceUUID());
                                showInformation(((Processbase) getApplication()).getMessages().getString("executedSuccessfully"));
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
}
