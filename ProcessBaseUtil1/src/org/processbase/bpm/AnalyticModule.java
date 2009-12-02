/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.bpm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.util.AccessorUtil;

/**
 *
 * @author mgubaidullin
 */
public class AnalyticModule {

    final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
    final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
    final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
    final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();

    public Map<String, Integer> countPIbyType(InstanceState state) throws ProcessNotFoundException {
        Map<String, Integer> result = new HashMap<String, Integer>();
        Set<ProcessDefinition> pds = queryDefinitionAPI.getProcesses();
        for (ProcessDefinition pd : pds) {
            String pName = pd.getDescription() != null ? pd.getDescription() : pd.getName();
            Integer c = result.containsKey(pName) ? result.get(pName) : new Integer(0);
            Set<ProcessInstance> pis = queryRuntimeAPI.getProcessInstances(pd.getUUID());
            for (ProcessInstance pi : pis) {
                if (pi.getInstanceState().equals(state)) {
                    c++;
                }
            }
            result.put(pName, c);
        }
//        Logger.getLogger(AnalyticModule.class.getName()).log(Level.SEVERE, "result = " + result.toString());
        return result;
    }
}
