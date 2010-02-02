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
package org.processbase.mobile;

import de.enough.polish.xml.XmlDomNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.processbase.bpm.BPMModule;
import org.processbase.util.ldap.User;

/**
 *
 * @author mgubaidullin
 */
public class MobileTask {

    protected ProcessDefinition processDefinition = null;
    protected TaskInstance task = null;
    protected Map<String, Object> processVars = new HashMap<String, Object>();
    protected Set<DataFieldDefinition> dfds = null;
    protected Set<String> candidates = null;
    protected boolean isNew = true;
    protected BPMModule bpmModule;
    protected User currentUser;

    public void setTaskInfo(BPMModule bpmModule, ProcessDefinition pd, TaskInstance t, User currentUser) throws ProcessNotFoundException, InstanceNotFoundException, Exception {
        this.processDefinition = pd;
        this.task = t;
        this.bpmModule = bpmModule;
        this.currentUser = currentUser;
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

    public String getForm() throws Exception {
        return null;
    }

    public String completeForm(XmlDomNode form) throws Exception {
        return null;
    }
}
