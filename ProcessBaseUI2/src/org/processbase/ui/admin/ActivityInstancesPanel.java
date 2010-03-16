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
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.util.Date;
import java.util.Set;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.light.LightActivityInstance;
import org.processbase.ui.template.PbColumnGenerator;
import org.processbase.core.Constants;

/**
 *
 * @author mgubaidullin
 */
public class ActivityInstancesPanel extends TablePanel implements Button.ClickListener {

    public ActivityInstancesPanel(PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("UUID", String.class, null, "UUID", null, null);
        table.setColumnWidth("UUID", 0);
        table.addContainerProperty("label", String.class, null, messages.getString("tableCaptionActivityName"), null, null);
        table.addContainerProperty("type", String.class, null, messages.getString("tableCaptionType"), null, null);
//            table.addContainerProperty("performer", String.class, null, "Исполнитель", null, null);
        table.addContainerProperty("readyDate", Date.class, null, messages.getString("tableCaptionCreatedDate"), null, null);
        table.addGeneratedColumn("readyDate", new PbColumnGenerator());
        table.setColumnWidth("readyDate", 100);
        table.addContainerProperty("startedDate", Date.class, null, messages.getString("tableCaptionStartedDate"), null, null);
        table.addGeneratedColumn("startedDate", new PbColumnGenerator());
        table.setColumnWidth("startedDate", 100);
        table.addContainerProperty("endDate", Date.class, null, messages.getString("tableCaptionFinishedDate"), null, null);
        table.addGeneratedColumn("endDate", new PbColumnGenerator());
        table.setColumnWidth("endDate", 100);
        //            table.addContainerProperty("iteration", String.class, null, "Итерация", null, null);
        table.addContainerProperty("state", String.class, null, messages.getString("tableCaptionState"), null, null);
        table.addContainerProperty("actions", TableExecButton.class, null, messages.getString("tableCaptionActions"), null, null);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            Set<LightActivityInstance> ais = bpmModule.getActivityInstances();
            for (LightActivityInstance ai : ais) {
                Item woItem = table.addItem(ai);
                woItem.getItemProperty("UUID").setValue(ai.getUUID().toString());
                woItem.getItemProperty("label").setValue(ai.getActivityLabel());
                woItem.getItemProperty("readyDate").setValue(ai.getReadyDate());
                woItem.getItemProperty("startedDate").setValue(ai.getStartedDate());
                woItem.getItemProperty("endDate").setValue(ai.getEndedDate());
                woItem.getItemProperty("state").setValue(ai.getState());
                woItem.getItemProperty("type").setValue(ai.isTask() ? messages.getString("task") : messages.getString("automatic"));
                woItem.getItemProperty("actions").setValue(
                        new TableExecButton(messages.getString("btnDeleteProcessInstance"), "icons/document.png", ai, this, Constants.ACTION_OPEN));
            }
            table.setSortContainerPropertyId("readyDate");
            table.setSortAscending(false);
            table.sort();
            table.setColumnWidth("UUID", 30);
        } catch (Exception ex) {
            showError(ex.toString());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            try {
                LightActivityInstance activity = (LightActivityInstance) ((TableExecButton) event.getButton()).getTableValue();
                ActivityWindow activityWindow = new ActivityWindow(activity, getPortletApplicationContext2());
                activityWindow.addListener((Window.CloseListener) this);
                getApplication().getMainWindow().addWindow(activityWindow);
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
            }
        }
    }
}
