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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.uuid.AbstractUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.template.PbColumnGenerator;
import org.processbase.core.Constants;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.TableExecButtonBar;

/**
 *
 * @author mgubaidullin
 */
public class ActivityInstancesPanel extends TablePanel implements Button.ClickListener {

    private ProcessDefinitionUUID filter = null;

    public ActivityInstancesPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("processName", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionProcess"), null, null);
        table.addContainerProperty("label", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionActivityName"), null, null);
        table.addContainerProperty("type", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionType"), null, null);
        table.addContainerProperty("lastUpdate", Date.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionLastUpdatedDate"), null, null);
        table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
        table.setColumnWidth("lastUpdate", 100);
        table.addContainerProperty("state", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionState"), null, null);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            Set<LightActivityInstance> ais = null;
            if (filter != null) {
                ais = PbPortlet.getCurrent().bpmModule.getActivityInstances(filter);
            } else {
                ais = PbPortlet.getCurrent().bpmModule.getActivityInstances();
            }

            for (LightActivityInstance ai : ais) {
                Item woItem = table.addItem(ai);
                LightProcessDefinition lpd = PbPortlet.getCurrent().bpmModule.getLightProcessDefinition(ai.getProcessDefinitionUUID());
                String processName = lpd.getLabel() != null ? lpd.getLabel() : lpd.getName();
                String processInstanceUUID = ai.getProcessInstanceUUID().toString();
                woItem.getItemProperty("processName").setValue(processName + "  #" + processInstanceUUID.substring(processInstanceUUID.lastIndexOf("--") + 2));
                StringBuilder link = new StringBuilder(ai.getActivityLabel() != null ? ai.getActivityLabel() : ai.getActivityName());

                if (ai.getDynamicLabel() != null && ai.getDynamicDescription() != null) {
                    link.append("(").append(ai.getDynamicLabel()).append(" - ").append(ai.getDynamicDescription()).append(")");
                } else if (ai.getDynamicLabel() != null && ai.getDynamicDescription() == null) {
                    link.append("(").append(ai.getDynamicLabel()).append(")");
                } else if (ai.getDynamicLabel() != null && ai.getDynamicDescription() != null) {
                    link.append("(").append(ai.getDynamicDescription()).append(")");
                }

                TableLinkButton teb = new TableLinkButton(link.toString(), ai.getActivityDescription(), null, ai, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("label").setValue(teb);
                woItem.getItemProperty("lastUpdate").setValue(ai.getLastUpdateDate());
                woItem.getItemProperty("state").setValue(ai.getState());
                if (ai.isTask()) {
                    woItem.getItemProperty("type").setValue(PbPortlet.getCurrent().messages.getString("task"));
                } else if (ai.isAutomatic()) {
                    woItem.getItemProperty("type").setValue(PbPortlet.getCurrent().messages.getString("automatic"));
                } else if (ai.isTimer()) {
                    woItem.getItemProperty("type").setValue(PbPortlet.getCurrent().messages.getString("timer"));
                } else if (ai.isSubflow()) {
                    woItem.getItemProperty("type").setValue(PbPortlet.getCurrent().messages.getString("subflow"));
                }
            }
            table.setSortContainerPropertyId("processName");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            Logger.getLogger(ActivityInstancesPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.toString());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            TableLinkButton execBtn = (TableLinkButton) event.getButton();
            LightActivityInstance activity = (LightActivityInstance) execBtn.getTableValue();
            try {
                if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                    ActivityWindow activityWindow = new ActivityWindow(activity);
                    getApplication().getMainWindow().addWindow(activityWindow);
                } else if (execBtn.getAction().equals(Constants.ACTION_STOP)) {
                    PbPortlet.getCurrent().bpmModule.stopExecution(activity.getProcessInstanceUUID(), activity.getActivityName());
                    Item woItem = table.getItem(activity);
                    woItem.getItemProperty("state").setValue(ActivityState.CANCELLED);
                    TableExecButtonBar tebb = new TableExecButtonBar();
                    tebb.addButton(new TableLinkButton(PbPortlet.getCurrent().messages.getString("btnOpen"), "icons/document.png", activity, this, Constants.ACTION_OPEN));
                    woItem.getItemProperty("actions").setValue(tebb);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
            }
        }
    }

    public void setFilter(ProcessDefinitionUUID filter) {
        this.filter = filter;
    }
}
