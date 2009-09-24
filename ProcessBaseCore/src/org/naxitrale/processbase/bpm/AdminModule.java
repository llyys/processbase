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

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.ui.admin.ActivityWindow;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.element.impl.BusinessArchiveImpl;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.PackageDefinition;
import org.ow2.bonita.facade.def.majorElement.ParticipantDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.DeploymentException;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.PackageNotFoundException;
import org.ow2.bonita.facade.exception.ParticipantNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.exception.UndeletableInstanceException;
import org.ow2.bonita.facade.exception.UndeletablePackageException;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.PackageDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.facade.uuid.TaskUUID;
import org.ow2.bonita.pvm.Deployment;
import org.ow2.bonita.util.AccessorUtil;

/**
 *
 * @author mgubaidullin
 */
public class AdminModule {

    final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
    final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
    final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
    final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();

    public Set<PackageDefinition> getPackageDefinitions() {
        return queryDefinitionAPI.getPackages();
    }

    public Set<ProcessDefinition> getProcessDefinitions() {
        return queryDefinitionAPI.getProcesses();
    }

    public Map<String, ProcessDefinition> deployXpdl(byte[] bar) throws DeploymentException {
        return managementAPI.deployXpdl(bar);

    }

    public void deployClassesInJar(byte[] jar) throws DeploymentException {
        managementAPI.deployClassesInJar(jar);
    }

    public Map<String, ProcessDefinition> deployBar(byte[] bar) throws DeploymentException {
        return managementAPI.deployBar(bar);
    }

    public void deletePackage(PackageDefinitionUUID pdUUID) throws PackageNotFoundException, UndeletableInstanceException, UndeletablePackageException {
        managementAPI.deletePackage(pdUUID);
    }

    public void deleteAllProcessInstances(PackageDefinitionUUID pdUUID) throws PackageNotFoundException, ProcessNotFoundException, UndeletableInstanceException {
        Set<ProcessDefinition> pds = queryDefinitionAPI.getPackageProcesses(pdUUID);
        for (ProcessDefinition pd : pds) {
            ProcessDefinitionUUID piUUID = pd.getProcessDefinitionUUID();
            runtimeAPI.deleteAllProcessInstances(piUUID);
        }
    }

    public Set<ProcessInstance> getProcessInstances() {
        return queryRuntimeAPI.getProcessInstances();
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

    public Set<ProcessInstance> getProcessInstancesByStatus(InstanceState state) {
        Set<ProcessInstance> result = new HashSet<ProcessInstance>();
        Set<ProcessInstance> pis = getProcessInstances();
        for (ProcessInstance pi : pis) {
            if (pi.getInstanceState().equals(state)) {
                result.add(pi);
            }
        }
        return result;
    }

    public Set<ActivityInstance<ActivityBody>> getActivityInstances() throws ProcessNotFoundException, ActivityNotFoundException {
        Set<ActivityInstance<ActivityBody>> result = new HashSet();
        try {
            Set<ProcessInstance> pis = queryRuntimeAPI.getProcessInstances();
            for (ProcessInstance pi : pis) {
                result.addAll(queryRuntimeAPI.getActivityInstances(pi.getProcessInstanceUUID()));
            }
        } catch (InstanceNotFoundException ex) {
            Logger.getLogger(AdminModule.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return result;
    }

    public void deleteProcessInstance(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, InstanceNotFoundException, InstanceNotFoundException, UndeletableInstanceException {
        runtimeAPI.deleteProcessInstance(piUUID);
    }

    public ProcessDefinition getProcessDefinition(ProcessDefinitionUUID pdUUID) throws ProcessNotFoundException {
        return queryDefinitionAPI.getProcess(pdUUID);
    }

    public ActivityDefinition getProcessActivityDefinition(ActivityInstance<ActivityBody> ai) throws ProcessNotFoundException, ActivityNotFoundException {
        return queryDefinitionAPI.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityId());
    }

    public void addResource(PackageDefinitionUUID pdUUID, String name, byte[] source) throws PackageNotFoundException, DeploymentException {
        PackageDefinition pd = queryDefinitionAPI.getPackage(pdUUID);
        BusinessArchive ba = pd.getBusinessArchive();
        ba.addResource(name, source);
        managementAPI.deploy(ba);
    }

    public void addResource(PackageDefinitionUUID pdUUID, File file) throws PackageNotFoundException {
        Deployment d = new Deployment();
        ProcessDefinition processDefinition = queryDefinitionAPI.getPackageProcesses(pdUUID).iterator().next();
        d.setProcessDefinition((org.ow2.bonita.pvm.ProcessDefinition) processDefinition);
        d.addFile(file);
    }

    public Set<DataFieldDefinition> getProcessDataFields(ProcessDefinitionUUID uuid) throws ProcessNotFoundException {
        return queryDefinitionAPI.getProcessDataFields(uuid);
    }

    public Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID piUUID) throws InstanceNotFoundException {
        return queryRuntimeAPI.getProcessInstanceVariables(piUUID);
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

    public ActivityWindow getActivityWindow(ActivityInstance<TaskInstance> task) throws ProcessNotFoundException, PackageNotFoundException {
        ProcessDefinition procd = queryDefinitionAPI.getProcess(task.getProcessDefinitionUUID());
        //PackageDefinition pd = queryDefinitionAPI.getPackage(procd.getPackageDefinitionUUID());
        ActivityWindow activityWindow = new ActivityWindow(procd, task);
        return activityWindow;
    }

    public void assignTask(TaskUUID taskUUID, Set<String> users) throws TaskNotFoundException, IllegalTaskStateException {
        runtimeAPI.assignTask(taskUUID, users);
    }

    public void assignTask(TaskUUID taskUUID, String user) throws TaskNotFoundException, IllegalTaskStateException {
        runtimeAPI.assignTask(taskUUID, user);
    }

    public void assignTask(TaskUUID taskUUID) throws TaskNotFoundException, IllegalTaskStateException {
        runtimeAPI.assignTask(taskUUID);
    }

    public void unassignTask(TaskUUID taskUUID) throws TaskNotFoundException, IllegalTaskStateException {
        runtimeAPI.unassignTask(taskUUID);
    }
}
