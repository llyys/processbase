/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.template;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.naxitrale.processbase.bpm.WorklistModule;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;

/**
 *
 * @author mgubaidullin
 */
public class TaskWindow extends PbWindow {

    protected ProcessDefinition processDefinition = null;
    protected ActivityInstance<TaskInstance> task = null;
    protected Map<String, Object> processVars = new HashMap<String, Object>();
    protected Set<DataFieldDefinition> dfds = null;
    protected WorklistModule worklistModule = new WorklistModule();
    protected HorizontalLayout buttons = new HorizontalLayout();
    protected Button cancelBtn = new Button(messages.getString("btnCancel"));
    protected Button applyBtn = new Button(messages.getString("btnOK"));
    protected boolean isNew = true;

    public TaskWindow() {
        super();
    }

    public void setTaskInfo(ProcessDefinition pd, ActivityInstance<TaskInstance> t) throws ProcessNotFoundException, InstanceNotFoundException {
        this.processDefinition = pd;
        this.task = t;
        if (task != null) {
            isNew = Boolean.FALSE;
            dfds = worklistModule.getProcessDataFields(task.getProcessDefinitionUUID());
            processVars = worklistModule.getProcessInstanceVariables(task.getProcessInstanceUUID());
        } else{
            isNew = Boolean.TRUE;
            dfds = worklistModule.getProcessDataFields(processDefinition.getProcessDefinitionUUID());
        }
    }

    public void exec() {
        try {
        } catch (Exception ex) {
            showError(ex.getMessage());
        }

    }

    public DataFieldDefinition getDataFieldDefinition (String name){
        for (DataFieldDefinition dfd : dfds){
            if (dfd.getDataFieldId().toString().equalsIgnoreCase(name)){
                return dfd;
            }
        }
        return null;
    }
}
