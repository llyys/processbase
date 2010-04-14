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
package org.processbase.ui.template;

import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.portlet.PortletSession;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.processbase.bpm.BPMModule;

/**
 *
 * @author mgubaidullin
 */
public abstract class TaskWindow extends PbWindow {

    protected ProcessDefinition processDefinition = null;
    protected TaskInstance task = null;
    protected Map<String, Object> processVars = new HashMap<String, Object>();
    protected Set<DataFieldDefinition> dfds = null;
    protected BPMModule bpmModule = null;
    protected HorizontalLayout buttons = new HorizontalLayout();
    protected Button cancelBtn = new Button(messages.getString("btnCancel"));
    protected Button applyBtn = new Button(messages.getString("btnOK"));
    protected boolean isNew = true;
    protected Set<String> candidates = null;

    public TaskWindow(PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
        Label emptyLabel = new Label("");
        buttons.addComponent(emptyLabel);
        buttons.setExpandRatio(emptyLabel, 1);
        buttons.setStyleName(Reindeer.LAYOUT_WHITE);
    }

    public boolean setTaskInfo(String processDefinitionUUID) {
        try {
            bpmModule = new BPMModule(getCurrentUser().getScreenName());
            if (processDefinitionUUID == null) {
                String taskUUID = (String) this.portletApplicationContext2.getPortletSession().getAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE);
                task = taskUUID != null ? bpmModule.getTaskInstance(new ActivityInstanceUUID(taskUUID)) : null;
            } else {
                processDefinition = bpmModule.getProcessDefinition(new ProcessDefinitionUUID(processDefinitionUUID));
            }
            if (task == null & processDefinition == null) {
                throw new Exception("ATTRIBUTES TASK AND PROCESSDEFINITION NOT SET!");
            } else if (task != null & processDefinition == null) {
                if (!this.task.isTaskAssigned() || !this.task.getTaskUser().equals(getCurrentUser().getScreenName())) {
                    throw new Exception("TASK NOT ASSIGNED TO CURRENT USER!");
                }
                processDefinition = bpmModule.getProcessDefinition(task.getProcessDefinitionUUID());
                isNew = Boolean.FALSE;
                dfds = bpmModule.getProcessDataFields(task.getProcessDefinitionUUID());
                processVars = bpmModule.getProcessInstanceVariables(task.getProcessInstanceUUID());
                candidates = task.getTaskCandidates();
            } else if (task == null & processDefinition != null) {
                isNew = Boolean.TRUE;
                dfds = bpmModule.getProcessDataFields(processDefinition.getUUID());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public abstract void exec();

    public DataFieldDefinition getDataFieldDefinition(String name) {
        for (DataFieldDefinition dfd : dfds) {
            if (dfd.getName().equalsIgnoreCase(name)) {
                return dfd;
            }
        }
        return null;
    }

    public TaskInstance getTask() {
        return task;
    }

    @Override
    public void close() {
        this.portletApplicationContext2.getPortletSession().removeAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE);
        this.portletApplicationContext2.getPortletSession().removeAttribute("PROCESSBASE_SHARED_PROCESSDEFINITION", PortletSession.APPLICATION_SCOPE);
        super.close();
        this.portletApplicationContext2.getPortletSession().removeAttribute("PROCESSBASE_PORTLET_CREATED", PortletSession.PORTLET_SCOPE);
        this.getApplication().close();
    }

    public HorizontalLayout getButtons() {
        return buttons;
    }
}
