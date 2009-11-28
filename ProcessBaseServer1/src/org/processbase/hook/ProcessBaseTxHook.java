package org.processbase.hook;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.ow2.bonita.definition.TxHook;
import org.ow2.bonita.facade.APIAccessor;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.processbase.bpm.BPMModule;

/**
 *
 * @author mgubaidullin
 */
public class ProcessBaseTxHook implements TxHook {

    protected BPMModule bpmModule = null;
    protected Map<String, Object> processVars = new HashMap<String, Object>();
    protected Set<DataFieldDefinition> dfds = null;

    public void execute(ActivityInstance<ActivityBody> activity) throws ProcessNotFoundException, Exception {
        
    }

    public void execute(APIAccessor apiAccessor, ActivityInstance<ActivityBody> activity) throws Exception {
        bpmModule = new BPMModule("");
        dfds = bpmModule.getProcessDataFields(activity.getProcessDefinitionUUID());
        processVars = bpmModule.getProcessInstanceVariables(activity.getProcessInstanceUUID(), true);
        execute(activity);
    }

    public BPMModule getBpmModule() {
        return bpmModule;
    }

    public void setBpmModule(BPMModule bpmModule) {
        this.bpmModule = bpmModule;
    }

    public Set<DataFieldDefinition> getDfds() {
        return dfds;
    }

    public void setDfds(Set<DataFieldDefinition> dfds) {
        this.dfds = dfds;
    }

    public Map<String, Object> getProcessVars() {
        return processVars;
    }

    public void setProcessVars(Map<String, Object> processVars) {
        this.processVars = processVars;
    }

    
}
