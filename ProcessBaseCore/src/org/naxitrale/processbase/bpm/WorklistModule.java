/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.bpm;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.ui.template.TaskWindow;
import org.naxitrale.processbase.ui.worklist.DefaultTaskWindow;
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

    public Set<ProcessDefinition> getProcessDefinitions() {
        return queryDefinitionAPI.getProcesses();
    }

    public ProcessDefinition getProcessDefinition(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, ProcessNotFoundException {
        ProcessInstance processInstance = queryRuntimeAPI.getProcessInstance(piUUID);
        return queryDefinitionAPI.getProcess(processInstance.getProcessDefinitionUUID());

    }

    public ProcessDefinition getProcessDefinition(ProcessDefinitionUUID pdUUID) throws InstanceNotFoundException, ProcessNotFoundException {
        return queryDefinitionAPI.getProcess(pdUUID);
    }

    public ProcessInstanceUUID startNewProcess(ProcessDefinitionUUID uuid, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException {
        ProcessInstanceUUID puuid = runtimeAPI.instantiateProcess(uuid, vars);
        return puuid;
    }

    public Set<DataFieldDefinition> getProcessDataFields(ProcessDefinitionUUID uuid) throws ProcessNotFoundException {
        return queryDefinitionAPI.getProcessDataFields(uuid);
    }

    public Set<ActivityDefinition> getProcessActivities(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException {
        return queryDefinitionAPI.getProcessActivities(uuid);
    }

    public Collection<ActivityInstance<TaskInstance>> getActivities(ActivityState state) {
        return queryRuntimeAPI.getTaskList(state);
    }

    public void startTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException {
        runtimeAPI.startTask(taskUUID, b);
    }

    public void finishTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException {
        runtimeAPI.finishTask(taskUUID, b);
    }

    public void finishTask(ActivityInstance<TaskInstance> task, boolean b, Map<String, Object> vars) throws TaskNotFoundException, IllegalTaskStateException, InstanceNotFoundException, VariableNotFoundException {
        for (String varKey : vars.keySet()) {
            setProcessInstanceVariable(task.getProcessInstanceUUID(), varKey, vars.get(varKey));
        }
        runtimeAPI.finishTask(task.getBody().getUUID(), b);
    }

    public void assignTask(TaskUUID taskUUID, String user) throws TaskNotFoundException, IllegalTaskStateException {
        runtimeAPI.assignTask(taskUUID, user);
    }

    public void resumeTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException {
        runtimeAPI.resumeTask(taskUUID, b);
    }

    public void suspendTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException {
        runtimeAPI.suspendTask(taskUUID, b);
    }

    public void setProcessInstanceVariable(ProcessInstanceUUID piUUID, String varName, Object varValue) throws InstanceNotFoundException, VariableNotFoundException {
        runtimeAPI.setProcessInstanceVariable(piUUID, varName, varValue);
    }

    public Map<String, Object> getActivityInstanceVariables(ActivityInstanceUUID aiUUID) throws ActivityNotFoundException {
        return queryRuntimeAPI.getActivityInstanceVariables(aiUUID);
    }

    public Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID piUUID) throws InstanceNotFoundException {
        return queryRuntimeAPI.getProcessInstanceVariables(piUUID);
    }

    public Collection<Resource> getPackage(PackageDefinitionUUID pdUUID) throws PackageNotFoundException {
        return queryDefinitionAPI.getPackage(pdUUID).getBusinessArchive().getClasses();
    }

    public HashMap<String, String> getFormNames(String uiString) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("ui.xml", HashMap.class);
        return (HashMap<String, String>) xstream.fromXML(uiString);
    }

    public TaskWindow getStartWindow(ProcessDefinition procd) {
        try {
            PackageDefinition pd = queryDefinitionAPI.getPackage(procd.getPackageDefinitionUUID());
            Resource uiResource = pd.getBusinessArchive().getResource("ui.xml");
            HashMap<String, String> uiMap = getFormNames(new String(uiResource.getData(), "UTF-8"));
            String formClassName = uiMap.get(procd.getProcessId());
            SystemClassLoader scl = new SystemClassLoader();
//            Logger.getLogger(WorklistModule.class.getName()).log(Level.SEVERE, "scl = " + scl);
            Class b = scl.loadClass(formClassName);
//          Logger.getLogger(WorklistModule.class.getName()).log(Level.SEVERE, "b = " + b);
            if (b == null) {
                NewClassLoader ncl = new NewClassLoader();
                Resource formResource = pd.getBusinessArchive().getResource(formClassName.replace('.', '\\').concat(".class"));
                ncl.setSource(formResource.getData());
                b = ncl.findClass(formClassName);
            }
            TaskWindow taskWindow = (TaskWindow) b.newInstance();
            taskWindow.setTaskInfo(procd, null);
            taskWindow.exec();
            return taskWindow;
        } catch (Exception ex) {
            Logger.getLogger(WorklistModule.class.getName()).log(Level.SEVERE, ex.toString());
            DefaultTaskWindow defaultTaskWindow = new DefaultTaskWindow(procd, null);
            defaultTaskWindow.exec();
            return defaultTaskWindow;
        }
    }

    public TaskWindow getTaskWindow(ActivityInstance<TaskInstance> task) {
        ProcessDefinition procd = null;
        try {
            procd = queryDefinitionAPI.getProcess(task.getProcessDefinitionUUID());
            PackageDefinition pd = queryDefinitionAPI.getPackage(procd.getPackageDefinitionUUID());
            Resource uiResource = pd.getBusinessArchive().getResource("ui.xml");
            HashMap<String, String> uiMap = getFormNames(new String(uiResource.getData(), "UTF-8"));
            String formClassName = uiMap.get(task.getActivityId());
            SystemClassLoader scl = new SystemClassLoader();
//            Logger.getLogger(WorklistModule.class.getName()).log(Level.SEVERE, "scl = " + scl);
            Class b = scl.loadClass(formClassName);
//          Logger.getLogger(WorklistModule.class.getName()).log(Level.SEVERE, "b = " + b);
            if (b == null) {
                NewClassLoader ncl = new NewClassLoader();
                Resource formResource = pd.getBusinessArchive().getResource(formClassName.replace('.', '\\').concat(".class"));
                ncl.setSource(formResource.getData());
                b = ncl.findClass(formClassName);
            }
            TaskWindow taskWindow = (TaskWindow) b.newInstance();
            taskWindow.setTaskInfo(procd, task);
            taskWindow.exec();
            return taskWindow;
        } catch (Exception ex) {
            Logger.getLogger(WorklistModule.class.getName()).log(Level.SEVERE, ex.toString());
            DefaultTaskWindow defaultTaskWindow = new DefaultTaskWindow(procd, task);
            defaultTaskWindow.exec();
            return defaultTaskWindow;
        }
    }

    public ActivityDefinition getProcessActivity(ActivityInstance<ActivityBody> ai) throws ProcessNotFoundException, ActivityNotFoundException {
        return queryDefinitionAPI.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityId());
    }

    public ActivityDefinition getProcessActivity(ProcessDefinitionUUID pdUUID, String ActivityID) throws ProcessNotFoundException, ActivityNotFoundException {
        return queryDefinitionAPI.getProcessActivity(pdUUID, ActivityID);
    }

    public ParticipantDefinition getProcessParticipant(ProcessDefinitionUUID pdUUID, String participant) throws ParticipantNotFoundException, ProcessNotFoundException {
        return queryDefinitionAPI.getProcessParticipant(pdUUID, participant);
    }
}
