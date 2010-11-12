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
package org.processbase.bpm;

import com.sun.appserv.security.ProgrammaticLogin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.ow2.bonita.facade.exception.UndeletableProcessException;
import org.processbase.core.Constants;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RepairAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.ParticipantDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.DeploymentException;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ParticipantNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.facade.exception.UndeletableInstanceException;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.identity.auth.DomainOwner;
import org.ow2.bonita.identity.auth.UserOwner;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.bpm.diagram.Diagram;
import org.processbase.bpm.forms.BonitaFormParcer;
import org.processbase.bpm.forms.XMLFormDefinition;
import org.processbase.bpm.forms.XMLProcessDefinition;

/**
 *
 * @author mgubaidullin
 */
public class BPMModule {

    final RuntimeAPI runtimeAPI = AccessorUtil.getAPIAccessor(Constants.BONITA_EJB_ENV).getRuntimeAPI();
    final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getAPIAccessor(Constants.BONITA_EJB_ENV).getQueryRuntimeAPI();
    final ManagementAPI managementAPI = AccessorUtil.getAPIAccessor(Constants.BONITA_EJB_ENV).getManagementAPI();
    final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getAPIAccessor(Constants.BONITA_EJB_ENV).getQueryDefinitionAPI();
    final RepairAPI repairAPI = AccessorUtil.getAPIAccessor(Constants.BONITA_EJB_ENV).getRepairAPI();
    final ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
    public static final int BAR = 0;
    public static final int XPDL = 1;
    private String currentUserUID;

    public BPMModule(String currentUserUID) {
        Constants.loadConstants();
//        System.out.println("currentUserUID = " + currentUserUID);
        this.currentUserUID = currentUserUID;
    }

    private void initContext() throws Exception{
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        DomainOwner.setDomain("default");
        UserOwner.setUser(currentUserUID);
    }

    public Set<ProcessDefinition> getProcessDefinitions() throws Exception {
        initContext();
        return queryDefinitionAPI.getProcesses();
    }

    public ProcessInstanceUUID startNewProcess(ProcessDefinitionUUID uuid, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        initContext();
        return runtimeAPI.instantiateProcess(uuid, vars);
    }

    public void saveProcessVariables(TaskInstance task, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        for (String key : vars.keySet()) {
            setProcessInstanceVariable(task.getProcessInstanceUUID(), key, vars.get(key));
        }
    }

    public void saveProcessVariables2(ActivityInstance activity, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        for (String key : vars.keySet()) {
            setProcessInstanceVariable(activity.getProcessInstanceUUID(), key, vars.get(key));
        }
    }

    public Set<DataFieldDefinition> getProcessDataFields(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessDataFields(uuid);
    }

    public DataFieldDefinition getProcessDataField(ProcessDefinitionUUID uuid, String varName) throws ProcessNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessDataField(uuid, varName);
    }

    public Map<String, ActivityDefinition> getProcessInitialActivities(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcess(uuid).getInitialActivities();
    }

    public Set<ActivityDefinition> getProcessActivities(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessActivities(uuid);
    }

    public Collection<TaskInstance> getTaskList(ActivityState state) throws Exception {
        initContext();
        return queryRuntimeAPI.getTaskList(state);
    }

    public Set<ProcessInstance> getUserInstances() throws Exception {
        initContext();
        return queryRuntimeAPI.getUserInstances();
    }

    public Set<LightProcessInstance> getLightUserInstances() throws Exception {
        initContext();
        return queryRuntimeAPI.getLightUserInstances();
    }

    public TaskInstance startTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        TaskInstance ti = getTaskInstance(activityInstanceUUID);
        if (ti != null && ti.getState().equals(ActivityState.READY)) {
            runtimeAPI.startTask(activityInstanceUUID, b);
            return getTaskInstance(activityInstanceUUID);
        }
        return ti;
    }

    public void finishTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.finishTask(activityInstanceUUID, b);
    }

    public void finishTask(TaskInstance task, boolean b, Map<String, Object> vars) throws TaskNotFoundException, IllegalTaskStateException, InstanceNotFoundException, VariableNotFoundException, Exception {
        initContext();
        for (String varKey : vars.keySet()) {
            setProcessInstanceVariable(task.getProcessInstanceUUID(), varKey, vars.get(varKey));
        }
        runtimeAPI.finishTask(task.getUUID(), b);
    }

    public TaskInstance assignTask(ActivityInstanceUUID activityInstanceUUID, String user) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        TaskInstance ti = getTaskInstance(activityInstanceUUID);
        if (ti != null && ti.isTaskAssigned() && !ti.getTaskUser().equals(user)) {
            return null;
        }
        runtimeAPI.assignTask(activityInstanceUUID, user);
        return getTaskInstance(activityInstanceUUID);
    }

    public TaskInstance resumeTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.resumeTask(activityInstanceUUID, b);
        return getTaskInstance(activityInstanceUUID);
    }

    public TaskInstance suspendTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.suspendTask(activityInstanceUUID, b);
        return getTaskInstance(activityInstanceUUID);
    }

    public void setProcessInstanceVariable(ProcessInstanceUUID piUUID, String varName, Object varValue) throws InstanceNotFoundException, VariableNotFoundException, Exception {
        initContext();
        runtimeAPI.setProcessInstanceVariable(piUUID, varName, varValue);
    }

    public Map<String, Object> getActivityInstanceVariables(ActivityInstanceUUID aiUUID) throws ActivityNotFoundException, Exception, ActivityNotFoundException, ActivityNotFoundException, ActivityNotFoundException, ActivityNotFoundException {
        initContext();
        return queryRuntimeAPI.getActivityInstanceVariables(aiUUID);
    }

    public Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, Exception {
        initContext();
        return queryRuntimeAPI.getProcessInstanceVariables(piUUID);
    }

    public Object getProcessInstanceVariable(ProcessInstanceUUID piUUID, String varName) throws InstanceNotFoundException, Exception {
        initContext();
        return queryRuntimeAPI.getProcessInstanceVariable(piUUID, varName);
    }

    public ActivityDefinition getProcessActivity(ProcessDefinitionUUID pdUUID, String ActivityName) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessActivity(pdUUID, ActivityName);
    }

    public ParticipantDefinition getProcessParticipant(ProcessDefinitionUUID pdUUID, String participant) throws ParticipantNotFoundException, ProcessNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessParticipant(pdUUID, participant);
    }

    public Set<ProcessDefinition> getProcesses() throws Exception {
        initContext();
        return queryDefinitionAPI.getProcesses();
    }

    public ProcessDefinition getProcessDefinition(ProcessDefinition pd) throws ProcessNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcess(pd.getUUID());
    }

    public ProcessDefinition deploy(BusinessArchive bar) throws DeploymentException, ProcessNotFoundException, VariableNotFoundException, Exception {
        initContext();
        ProcessDefinition result = managementAPI.deploy(bar);
        return result;
    }

    public void deployJar(String jarName, byte[] body) throws Exception {
        initContext();
        if (managementAPI.getAvailableJars().contains(jarName)) {
            managementAPI.removeJar(jarName);
        }
        managementAPI.deployJar(jarName, body);
    }

    public void deleteProcess(ProcessDefinition pd) throws UndeletableInstanceException, UndeletableProcessException, ProcessNotFoundException, Exception {
        initContext();
        managementAPI.deleteProcess(pd.getUUID());
    }

    public void deleteAllProcessInstances(ProcessDefinition pd) throws Exception {
        initContext();
        runtimeAPI.deleteAllProcessInstances(pd.getUUID());
    }

    public Set<ProcessInstance> getProcessInstances() throws Exception {
        initContext();
        return queryRuntimeAPI.getProcessInstances();
    }

    public Set<LightProcessInstance> getLightProcessInstances() throws Exception {
        initContext();
        return queryRuntimeAPI.getLightProcessInstances();
    }

    public Set<ProcessInstance> getProcessInstancesByUUID(ProcessDefinitionUUID piUUID) throws Exception {
        initContext();
        return queryRuntimeAPI.getProcessInstances(piUUID);
    }

    public Set<ProcessInstance> getProcessInstances(ProcessDefinitionUUID piUUID, InstanceState state) throws ProcessNotFoundException, Exception {
        initContext();
        Set<ProcessInstance> result = new HashSet<ProcessInstance>();
        Set<ProcessInstance> pis = queryRuntimeAPI.getProcessInstances(piUUID);
        for (ProcessInstance pi : pis) {
            if (pi.getInstanceState().equals(state)) {
                result.add(pi);
            }
        }
        return result;
    }

    public Set<LightProcessInstance> getProcessInstancesByStatus(InstanceState state) throws Exception {
        initContext();
        Set<LightProcessInstance> result = new HashSet<LightProcessInstance>();
        Set<LightProcessInstance> pis = getLightProcessInstances();
        for (LightProcessInstance pi : pis) {
            if (pi.getInstanceState().equals(state)) {
                result.add(pi);
            }
        }
        return result;
    }

    public Set<LightActivityInstance> getActivityInstances() throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        initContext();
        Set<LightActivityInstance> result = new HashSet();
        try {
            Set<LightProcessInstance> pis = queryRuntimeAPI.getLightProcessInstances();
            for (LightProcessInstance pi : pis) {
                result.addAll(queryRuntimeAPI.getLightActivityInstances(pi.getProcessInstanceUUID()));
            }
        } catch (InstanceNotFoundException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public Set<ActivityInstance> getActivityInstances(ProcessInstanceUUID processInstanceUUID) throws Exception {
        initContext();
        return queryRuntimeAPI.getActivityInstances(processInstanceUUID);
    }

    public Set<LightActivityInstance> getLightActivityInstances(ProcessInstanceUUID processInstanceUUID) throws Exception {
        initContext();
        return queryRuntimeAPI.getLightActivityInstances(processInstanceUUID);
    }

    public ActivityInstance getActivityInstance(ActivityInstanceUUID activityInstanceUUID) throws ActivityNotFoundException, Exception {
        initContext();
        return queryRuntimeAPI.getActivityInstance(activityInstanceUUID);
    }

    public TaskInstance getTaskInstance(ActivityInstanceUUID activityInstanceUUID) throws ProcessNotFoundException, Exception {
        initContext();
        try {
            return queryRuntimeAPI.getTask(activityInstanceUUID);
        } catch (TaskNotFoundException tex) {
            tex.printStackTrace();
            return null;
        }
    }

    public void deleteProcessInstance(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, InstanceNotFoundException, InstanceNotFoundException, UndeletableInstanceException, Exception {
        initContext();
        runtimeAPI.deleteProcessInstance(piUUID);
    }

    public ProcessDefinition getProcessDefinition(ProcessDefinitionUUID pdUUID) throws ProcessNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcess(pdUUID);
    }

    public ActivityDefinition getProcessActivityDefinition(ActivityInstance ai) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
    }

    public ActivityDefinition getProcessActivityDefinition(LightActivityInstance lai) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessActivity(lai.getProcessDefinitionUUID(), lai.getActivityName());
    }

    public ActivityDefinition getTaskDefinition(ActivityInstance ai) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
    }

    public void assignTask(ActivityInstanceUUID activityInstanceUUID, Set<String> users) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.assignTask(activityInstanceUUID, users);
    }

    public void assignTask(ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.assignTask(activityInstanceUUID);
    }

    public TaskInstance unassignTask(ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.unassignTask(activityInstanceUUID);
        return getTaskInstance(activityInstanceUUID);
    }

    public void addProcessMetaData(ProcessDefinitionUUID processDefinitionUUID, String key, String value) throws Exception {
        initContext();
        runtimeAPI.addProcessMetaData(processDefinitionUUID, key, value);
    }

    public Map<String, String> getProcessMetaData(ProcessDefinitionUUID processDefinitionUUID) throws Exception {
        initContext();
        return queryDefinitionAPI.getProcess(processDefinitionUUID).getMetaData();
    }

    public byte[] getProcessDiagramm(ProcessInstanceUUID processInstanceUUID) throws Exception {
        initContext();
        Map<String, byte[]> resource = queryDefinitionAPI.getBusinessArchive(processInstanceUUID.getProcessDefinitionUUID()).getResources();
        byte[] img = null;
        byte[] proc = null;
        for (String key : resource.keySet()) {
            if (key.substring(key.length() - 4, key.length()).equals("proc")) {
                proc = resource.get(key);
            } else if (key.substring(key.length() - 3, key.length()).equals("png")) {
                img = resource.get(key);
            }
        }
        Diagram d = new Diagram(img, proc, queryRuntimeAPI.getLightActivityInstances(processInstanceUUID));
        return d.getImage();
    }

    public ArrayList<XMLFormDefinition> getXMLFormDefinition(TaskInstance taskInstance) throws Exception {
        XMLProcessDefinition process = getXMLProcessDefinition(taskInstance.getProcessDefinitionUUID());
        return process.getForms().get(taskInstance.getActivityName());
    }

    public XMLProcessDefinition getXMLProcessDefinition(ProcessInstanceUUID processInstanceUUID) throws Exception {
        return getXMLProcessDefinition(processInstanceUUID.getProcessDefinitionUUID());
    }

    public XMLProcessDefinition getXMLProcessDefinition(ProcessDefinitionUUID processDefinitionUUID) throws Exception {
        initContext();
        Map<String, byte[]> resource = queryDefinitionAPI.getBusinessArchive(processDefinitionUUID).getResources();
        byte[] proc = null;
        for (String key : resource.keySet()) {
            if (key.substring(key.length() - 4, key.length()).equals("proc")) {
                proc = resource.get(key);
            }
        }
        BonitaFormParcer bfb = new BonitaFormParcer(proc);
        return bfb.getProcess();
    }

    public byte[] getProcessDiagramm(ProcessDefinitionUUID processDefinitionUUID) throws Exception {
        initContext();
        Map<String, byte[]> resource = queryDefinitionAPI.getBusinessArchive(processDefinitionUUID).getResources();
        byte[] img = null;
        for (String key : resource.keySet()) {
            if (key.substring(key.length() - 3, key.length()).equals("png")) {
                img = resource.get(key);
            }
        }
        return img;
    }

    public void stopExecution(ProcessInstanceUUID piUUID, String stepName) throws Exception {
        initContext();
        repairAPI.stopExecution(piUUID, stepName);
    }

    public ActivityInstanceUUID startExecution(ProcessInstanceUUID piUUID, String stepName) throws Exception {
        initContext();
        return repairAPI.startExecution(piUUID, stepName);
    }

    public ActivityInstanceUUID reStartExecution(ProcessInstanceUUID piUUID, String stepName) throws Exception {
        initContext();
        repairAPI.stopExecution(piUUID, stepName);
        return repairAPI.startExecution(piUUID, stepName);
    }
}
