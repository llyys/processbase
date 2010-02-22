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
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.ow2.bonita.facade.exception.UndeletableProcessException;
import org.processbase.util.Constants;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
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
import org.processbase.util.db.HibernateUtil;
import org.ow2.bonita.facade.exception.UndeletableInstanceException;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.util.db.PbActivityUi;

/**
 *
 * @author mgubaidullin
 */
public class BPMModule {

    final RuntimeAPI runtimeAPI = AccessorUtil.getAPIAccessor(Constants.EJB_ENV).getRuntimeAPI();
    final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getAPIAccessor(Constants.EJB_ENV).getQueryRuntimeAPI();
    final ManagementAPI managementAPI = AccessorUtil.getAPIAccessor(Constants.EJB_ENV).getManagementAPI();
    final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getAPIAccessor(Constants.EJB_ENV).getQueryDefinitionAPI();
    final ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
    public static final int BAR = 0;
    public static final int XPDL = 1;
    private String currentUserUID;

    public BPMModule(String currentUserUID) {
        Constants.loadConstants();
        this.currentUserUID = currentUserUID;
    }

    public Set<ProcessDefinition> getProcessDefinitions() throws Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcesses();
    }

    public ProcessInstanceUUID startNewProcess(ProcessDefinitionUUID uuid, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
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
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessDataFields(uuid);
    }

    public Map<String, ActivityDefinition> getProcessInitialActivities(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcess(uuid).getInitialActivities();
    }

    public Set<ActivityDefinition> getProcessActivities(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessActivities(uuid);
    }

    public Collection<TaskInstance> getTaskList(ActivityState state) throws Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryRuntimeAPI.getTaskList(state);
    }

    public Set<ProcessInstance> getUserInstances() throws Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryRuntimeAPI.getUserInstances();
    }

    public Set<LightProcessInstance> getLightUserInstances() throws Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryRuntimeAPI.getLightUserInstances();
    }

    public TaskInstance startTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        TaskInstance ti = getTaskInstance(activityInstanceUUID);
        if (ti.getState().equals(ActivityState.READY)) {
            runtimeAPI.startTask(activityInstanceUUID, b);
            return getTaskInstance(activityInstanceUUID);
        }
        return ti;
    }

    public void finishTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.finishTask(activityInstanceUUID, b);
    }

    public void finishTask(TaskInstance task, boolean b, Map<String, Object> vars) throws TaskNotFoundException, IllegalTaskStateException, InstanceNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        for (String varKey : vars.keySet()) {
            setProcessInstanceVariable(task.getProcessInstanceUUID(), varKey, vars.get(varKey));
        }
        runtimeAPI.finishTask(task.getUUID(), b);
    }

    public TaskInstance assignTask(ActivityInstanceUUID activityInstanceUUID, String user) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        TaskInstance ti = getTaskInstance(activityInstanceUUID);
        if (ti.isTaskAssigned() && !ti.getTaskUser().equals(user)) {
            return null;
        }
        runtimeAPI.assignTask(activityInstanceUUID, user);
        return getTaskInstance(activityInstanceUUID);
    }

    public void resumeTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.resumeTask(activityInstanceUUID, b);
    }

    public void suspendTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.suspendTask(activityInstanceUUID, b);
    }

    public void setProcessInstanceVariable(ProcessInstanceUUID piUUID, String varName, Object varValue) throws InstanceNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.setProcessInstanceVariable(piUUID, varName, varValue);
    }

    public Map<String, Object> getActivityInstanceVariables(ActivityInstanceUUID aiUUID) throws ActivityNotFoundException, Exception, ActivityNotFoundException, ActivityNotFoundException, ActivityNotFoundException, ActivityNotFoundException {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryRuntimeAPI.getActivityInstanceVariables(aiUUID);
    }

    public Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryRuntimeAPI.getProcessInstanceVariables(piUUID);
    }

    public Object getProcessInstanceVariable(ProcessInstanceUUID piUUID, String varName) throws InstanceNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryRuntimeAPI.getProcessInstanceVariable(piUUID, varName);
    }

    public HashMap<String, String> getFormNames(String uiString) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("ui.xml", HashMap.class);
        return (HashMap<String, String>) xstream.fromXML(uiString);
    }

    public ActivityDefinition getProcessActivity(ProcessDefinitionUUID pdUUID, String ActivityName) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessActivity(pdUUID, ActivityName);
    }

    public ParticipantDefinition getProcessParticipant(ProcessDefinitionUUID pdUUID, String participant) throws ParticipantNotFoundException, ProcessNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessParticipant(pdUUID, participant);
    }

    public Set<ProcessDefinition> getProcesses() throws Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcesses();
    }

    public ProcessDefinition getProcessDefinition(ProcessDefinition pd) throws ProcessNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcess(pd.getUUID());
    }

    public ProcessDefinition deploy(BusinessArchive bar) throws DeploymentException, ProcessNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        ProcessDefinition result = managementAPI.deploy(bar);
        // add Human tasks
        Set<ActivityDefinition> acts = this.getProcessActivities(result.getUUID());
        ArrayList<PbActivityUi> activities = new ArrayList<PbActivityUi>();
        for (ActivityDefinition ad : acts) {
            if (ad.isTask()) {
                PbActivityUi pbActivityUi = new PbActivityUi();
                pbActivityUi.setProccessUuid(result.getUUID().getValue());
                pbActivityUi.setActivityUuid(ad.getUUID().getValue());
                pbActivityUi.setActivityLabel(ad.getLabel());
                pbActivityUi.setIsStart("F");
                pbActivityUi.setIsMobile("F");
                activities.add(pbActivityUi);
            }
        }
        // add initial activities
        Map<String, ActivityDefinition> acts2 = this.getProcessInitialActivities(result.getUUID());
        for (ActivityDefinition ad : acts2.values()) {
            PbActivityUi pbActivityUi = new PbActivityUi();
            pbActivityUi.setProccessUuid(result.getUUID().getValue());
            pbActivityUi.setActivityUuid(ad.getUUID().getValue());
            pbActivityUi.setActivityLabel(ad.getLabel());
            pbActivityUi.setIsStart("T");
            pbActivityUi.setIsMobile("F");
            activities.add(pbActivityUi);
        }
        HibernateUtil hutil = new HibernateUtil();
        hutil.addProcessUiEmpty(result.getUUID().toString(), result.getLabel(), activities);
        return result;
    }

    public void deployJar(String jarName, byte[] body) throws Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        if (managementAPI.getAvailableJars().contains(jarName)) {
            managementAPI.removeJar(jarName);
        }
        managementAPI.deployJar(jarName, body);
    }

    public void deleteProcess(ProcessDefinition pd) throws UndeletableInstanceException, UndeletableProcessException, ProcessNotFoundException, Exception {
        HibernateUtil hutil = new HibernateUtil();
        hutil.deletePbProcess(getProcessDefinition(pd).getUUID().toString());
        managementAPI.deleteProcess(pd.getUUID());
    }

    public void deleteAllProcessInstances(ProcessDefinition pd) throws ProcessNotFoundException, UndeletableInstanceException {
        ArrayList<String> piUUIDs = new ArrayList<String>();
        for (ProcessInstance pi : getProcessInstancesByUUID(pd.getUUID())) {
            piUUIDs.add(pi.getProcessInstanceUUID().toString());
        }
        runtimeAPI.deleteAllProcessInstances(pd.getUUID());
        HibernateUtil hutil = new HibernateUtil();
        hutil.deletePbProcessess(piUUIDs);
    }

    public Set<ProcessInstance> getProcessInstances() {
        return queryRuntimeAPI.getProcessInstances();
    }

    public Set<LightProcessInstance> getLightProcessInstances() {
        return queryRuntimeAPI.getLightProcessInstances();
    }

    public Set<ProcessInstance> getProcessInstancesByUUID(ProcessDefinitionUUID piUUID) throws ProcessNotFoundException {
        return queryRuntimeAPI.getProcessInstances(piUUID);
    }

    public Set<ProcessInstance> getProcessInstances(ProcessDefinitionUUID piUUID, InstanceState state) throws ProcessNotFoundException {
        Set<ProcessInstance> result = new HashSet<ProcessInstance>();
        Set<ProcessInstance> pis = queryRuntimeAPI.getProcessInstances(piUUID);
        for (ProcessInstance pi : pis) {
            if (pi.getInstanceState().equals(state)) {
                result.add(pi);
            }
        }
        return result;
    }

    public Set<LightProcessInstance> getProcessInstancesByStatus(InstanceState state) {
        Set<LightProcessInstance> result = new HashSet<LightProcessInstance>();
        Set<LightProcessInstance> pis = getLightProcessInstances();
        for (LightProcessInstance pi : pis) {
            if (pi.getInstanceState().equals(state)) {
                result.add(pi);
            }
        }
        return result;
    }

    public Set<LightActivityInstance> getActivityInstances() throws ProcessNotFoundException, ActivityNotFoundException {
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
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryRuntimeAPI.getActivityInstances(processInstanceUUID);
    }

    public ActivityInstance getActivityInstance(ActivityInstanceUUID activityInstanceUUID) throws ActivityNotFoundException {
        return queryRuntimeAPI.getActivityInstance(activityInstanceUUID);
    }

    public TaskInstance getTaskInstance(ActivityInstanceUUID activityInstanceUUID) throws ProcessNotFoundException, TaskNotFoundException {
        return queryRuntimeAPI.getTask(activityInstanceUUID);
    }

    public void deleteProcessInstance(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, InstanceNotFoundException, InstanceNotFoundException, UndeletableInstanceException {
        runtimeAPI.deleteProcessInstance(piUUID);
        HibernateUtil hutil = new HibernateUtil();
        hutil.deletePbProcess(piUUID.toString());
    }

    public ProcessDefinition getProcessDefinition(ProcessDefinitionUUID pdUUID) throws ProcessNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcess(pdUUID);
    }

    public ActivityDefinition getProcessActivityDefinition(ActivityInstance ai) throws ProcessNotFoundException, ActivityNotFoundException {
        return queryDefinitionAPI.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
    }

    public ActivityDefinition getTaskDefinition(ActivityInstance ai) throws ProcessNotFoundException, ActivityNotFoundException {
        return queryDefinitionAPI.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
    }

    public void assignTask(ActivityInstanceUUID activityInstanceUUID, Set<String> users) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.assignTask(activityInstanceUUID, users);
    }

    public void assignTask(ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.assignTask(activityInstanceUUID);
    }

    public void unassignTask(ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.unassignTask(activityInstanceUUID);
    }
}
