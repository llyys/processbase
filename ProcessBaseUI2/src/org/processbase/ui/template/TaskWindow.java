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

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.processbase.ProcessBase;
import org.processbase.bpm.BPMModule;
import org.processbase.util.ldap.User;

/**
 *
 * @author mgubaidullin
 */
public class TaskWindow extends PbWindow {

    protected ProcessDefinition processDefinition = null;
    protected TaskInstance task = null;
    protected Map<String, Object> processVars = new HashMap<String, Object>();
    protected Set<DataFieldDefinition> dfds = null;
    protected BPMModule bpmModule = ((ProcessBase) getApplication()).getCurrent().getBpmModule();
    protected HorizontalLayout buttons = new HorizontalLayout();
    protected Button cancelBtn = new Button(messages.getString("btnCancel"));
    protected Button applyBtn = new Button(messages.getString("btnOK"));
    protected boolean isNew = true;
    protected AttachmentBar attachmentBar = new AttachmentBar();
    protected Set<String> candidates = null;

    public TaskWindow() {
        super();
        Label emptyLabel = new Label("");
        buttons.addComponent(emptyLabel);
        buttons.setExpandRatio(emptyLabel, 1);
    }

    public void setTaskInfo(ProcessDefinition pd, TaskInstance t) throws ProcessNotFoundException, InstanceNotFoundException, Exception {
        this.processDefinition = pd;
        this.task = t;
        if (task != null) {
            isNew = Boolean.FALSE;
            dfds = bpmModule.getProcessDataFields(task.getProcessDefinitionUUID());
            processVars = bpmModule.getProcessInstanceVariables(task.getProcessInstanceUUID());
            candidates = task.getTaskCandidates();
        } else {
            isNew = Boolean.TRUE;
            dfds = bpmModule.getProcessDataFields(processDefinition.getUUID());
        }
    }

    public void exec() {
        try {
        } catch (Exception ex) {
            showError(ex.getMessage());
        }

    }

    public DataFieldDefinition getDataFieldDefinition(String name) {
        for (DataFieldDefinition dfd : dfds) {
            if (dfd.getName().equalsIgnoreCase(name)) {
                return dfd;
            }
        }
        return null;
    }

    @SuppressWarnings("static-access")
    public User getCurrenUser(){
        return ((ProcessBase) getApplication()).getCurrent().getUser();
    }
}
