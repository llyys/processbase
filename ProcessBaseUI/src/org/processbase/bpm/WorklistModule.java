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

import com.sun.appserv.security.ProgrammaticLogin;
import org.processbase.util.ProcessBaseClassLoader;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.ProcessBase;
import org.processbase.ui.template.TaskWindow;
import org.processbase.ui.worklist.DefaultTaskWindow;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.element.Resource;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.PackageDefinition;
import org.ow2.bonita.facade.def.majorElement.ParticipantDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.PackageNotFoundException;
import org.ow2.bonita.facade.exception.ParticipantNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.PackageDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.facade.uuid.TaskUUID;
import org.ow2.bonita.util.AccessorUtil;

/**
 *
 * @author mgubaidullin
 */
public class WorklistModule {

    final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
    final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
    final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
    final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
    final ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();

    public Set<ProcessDefinition> getProcessDefinitions() throws Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcesses();
    }

    public ProcessDefinition getProcessDefinition(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, ProcessNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        ProcessInstance processInstance = queryRuntimeAPI.getProcessInstance(piUUID);
        return queryDefinitionAPI.getProcess(processInstance.getProcessDefinitionUUID());
    }

    public ProcessDefinition getProcessDefinition(ProcessDefinitionUUID pdUUID) throws InstanceNotFoundException, ProcessNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcess(pdUUID);
    }

    public ProcessInstanceUUID startNewProcess(ProcessDefinitionUUID uuid, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        ProcessInstanceUUID puuid = runtimeAPI.instantiateProcess(uuid, vars);
        return puuid;
    }

    public Set<DataFieldDefinition> getProcessDataFields(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessDataFields(uuid);
    }

    public Set<ActivityDefinition> getProcessActivities(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessActivities(uuid);
    }

    public Collection<ActivityInstance<TaskInstance>> getActivities(ActivityState state) throws Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        return queryRuntimeAPI.getTaskList(state);
    }

    public void startTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        runtimeAPI.startTask(taskUUID, b);
    }

    public void finishTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        runtimeAPI.finishTask(taskUUID, b);
    }

    public void finishTask(ActivityInstance<TaskInstance> task, boolean b, Map<String, Object> vars) throws TaskNotFoundException, IllegalTaskStateException, InstanceNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        for (String varKey : vars.keySet()) {
            setProcessInstanceVariable(task.getProcessInstanceUUID(), varKey, vars.get(varKey));
        }
        runtimeAPI.finishTask(task.getBody().getUUID(), b);
    }

    public void assignTask(TaskUUID taskUUID, String user) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        runtimeAPI.assignTask(taskUUID, user);
    }

    public void resumeTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        runtimeAPI.resumeTask(taskUUID, b);
    }

    public void suspendTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        runtimeAPI.suspendTask(taskUUID, b);
    }

    public void setProcessInstanceVariable(ProcessInstanceUUID piUUID, String varName, Object varValue) throws InstanceNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        runtimeAPI.setProcessInstanceVariable(piUUID, varName, varValue);
    }

    public Map<String, Object> getActivityInstanceVariables(ActivityInstanceUUID aiUUID) throws ActivityNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        return queryRuntimeAPI.getActivityInstanceVariables(aiUUID);
    }

    public Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        return queryRuntimeAPI.getProcessInstanceVariables(piUUID);
    }

    public Collection<Resource> getPackage(PackageDefinitionUUID pdUUID) throws PackageNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        return queryDefinitionAPI.getPackage(pdUUID).getBusinessArchive().getClasses();
    }

    public HashMap<String, String> getFormNames(String uiString) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("ui.xml", HashMap.class);
        return (HashMap<String, String>) xstream.fromXML(uiString);
    }

    public TaskWindow getStartWindow(ProcessDefinition procd, Application application) {
        try {
            programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
            PackageDefinition pd = queryDefinitionAPI.getPackage(procd.getPackageDefinitionUUID());
            Resource uiResource = pd.getBusinessArchive().getResource("ui.xml");
            HashMap<String, String> uiMap = getFormNames(new String(uiResource.getData(), "UTF-8"));
            String formClassName = uiMap.get(procd.getProcessId());
            Class b = ((ProcessBase) application).processBaseClassLoader.loadClass(formClassName);
            TaskWindow taskWindow = (TaskWindow) b.newInstance();
            taskWindow.setTaskInfo(procd, null);
            taskWindow.exec();
            return taskWindow;
        } catch (Exception ex) {
            Logger.getLogger(WorklistModule.class.getName()).log(Level.SEVERE, ex.getMessage());
            DefaultTaskWindow defaultTaskWindow = new DefaultTaskWindow(procd, null);
            defaultTaskWindow.exec();
            return defaultTaskWindow;
        }
    }

    public TaskWindow getTaskWindow(ActivityInstance<TaskInstance> task, Application application) {
        ProcessDefinition procd = null;
        try {
            programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
            procd = queryDefinitionAPI.getProcess(task.getProcessDefinitionUUID());
            PackageDefinition pd = queryDefinitionAPI.getPackage(procd.getPackageDefinitionUUID());
            Resource uiResource = pd.getBusinessArchive().getResource("ui.xml");
            HashMap<String, String> uiMap = getFormNames(new String(uiResource.getData(), "UTF-8"));
            String formClassName = uiMap.get(task.getActivityId());
            ProcessBaseClassLoader scl = new ProcessBaseClassLoader();
            Class b = ((ProcessBase) application).processBaseClassLoader.loadClass(formClassName);
            TaskWindow taskWindow = (TaskWindow) b.newInstance();
            taskWindow.setTaskInfo(procd, task);
            taskWindow.exec();
            return taskWindow;
        } catch (Exception ex) {
            Logger.getLogger(WorklistModule.class.getName()).log(Level.SEVERE, ex.getMessage());
            DefaultTaskWindow defaultTaskWindow = new DefaultTaskWindow(procd, task);
            defaultTaskWindow.exec();
            return defaultTaskWindow;
        }
    }

    public ActivityDefinition getProcessActivity(ActivityInstance<ActivityBody> ai) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityId());
    }

    public ActivityDefinition getProcessActivity(ProcessDefinitionUUID pdUUID, String ActivityID) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessActivity(pdUUID, ActivityID);
    }

    public ParticipantDefinition getProcessParticipant(ProcessDefinitionUUID pdUUID, String participant) throws ParticipantNotFoundException, ProcessNotFoundException, Exception {
        programmaticLogin.login(ProcessBase.getCurrent().getUser().getPbuser().getUsername(), "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessParticipant(pdUUID, participant);
    }
}
