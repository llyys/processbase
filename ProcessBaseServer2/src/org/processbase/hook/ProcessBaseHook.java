package org.processbase.hook;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.ow2.bonita.definition.Hook;
import org.ow2.bonita.facade.QueryAPIAccessor;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.processbase.bpm.BPMModule;
import org.processbase.util.Constants;

/**
 *
 * @author mgubaidullin
 */
public class ProcessBaseHook implements Hook {

    protected BPMModule bpmModule = null;
    protected Map<String, Object> processVars = new HashMap<String, Object>();
    protected Set<DataFieldDefinition> dfds = null;

    public void execute(ActivityInstance<ActivityBody> activity) throws Exception {
        
    }

    public void execute(QueryAPIAccessor queryAPIAccessor, ActivityInstance<ActivityBody> activity) throws Exception {
//        Logger.getLogger("DEBUG").log(Level.SEVERE, "1111111111111111111111111111111");
        Constants.loadConstants();
        bpmModule = new BPMModule("PROCESBASE");
//        Logger.getLogger("DEBUG").log(Level.SEVERE, "222222222222222222222222222222222222222222");
        dfds = bpmModule.getProcessDataFields(activity.getProcessDefinitionUUID());
//        Logger.getLogger("DEBUG").log(Level.SEVERE, "dfds = " + dfds.toString());
        processVars = bpmModule.getProcessInstanceVariables(activity.getProcessInstanceUUID(), true);
//        Logger.getLogger("DEBUG").log(Level.SEVERE, "pbVars = " + processVars.toString());
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
