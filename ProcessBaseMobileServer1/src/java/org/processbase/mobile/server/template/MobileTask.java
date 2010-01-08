package org.processbase.mobile.server.template;

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

/**
 *
 * @author mgubaidullin
 */
public class MobileTask {
    protected ProcessDefinition processDefinition = null;
    protected ActivityInstance<TaskInstance> task = null;
    protected Map<String, Object> processVars = new HashMap<String, Object>();
    protected Set<DataFieldDefinition> dfds = null;
    protected Set<String> candidates = null;
    protected boolean isNew = true;
    private BPMModule bpmModule;

    public void setTaskInfo(BPMModule bpmModule, ProcessDefinition pd, ActivityInstance<TaskInstance> t) throws ProcessNotFoundException, InstanceNotFoundException, Exception {
        this.processDefinition = pd;
        this.task = t;
        this.bpmModule = bpmModule;
        if (task != null) {
            isNew = Boolean.FALSE;
            dfds = bpmModule.getProcessDataFields(task.getProcessDefinitionUUID());
            processVars = bpmModule.getProcessInstanceVariables(task.getProcessInstanceUUID(), true);
            candidates = task.getBody().getTaskCandidates();
        } else {
            isNew = Boolean.TRUE;
            dfds = bpmModule.getProcessDataFields(processDefinition.getProcessDefinitionUUID());
        }
    }

    public String getForm() {
        return null;
    }

    public String completeForm(XmlDomNode form) {
        return null;
    }
}
