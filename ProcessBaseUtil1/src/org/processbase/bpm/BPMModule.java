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
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.util.Constants;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.element.Resource;
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
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.runtime.var.Enumeration;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.PackageDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.facade.uuid.TaskUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.processbase.util.db.HibernateUtil;
import org.ow2.bonita.facade.exception.UndeletableInstanceException;
import org.ow2.bonita.facade.exception.UndeletablePackageException;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.pvm.Deployment;

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
        this.currentUserUID = currentUserUID;
    }

    public Set<ProcessDefinition> getProcessDefinitions() throws Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcesses();
    }

    public ProcessInstanceUUID startNewProcess(ProcessDefinitionUUID uuid, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        Map<String, Object> bonitaVars = new HashMap<String, Object>();
        Map<String, Object> pbVars = new HashMap<String, Object>();
        for (String key : vars.keySet()) {
            Object value = vars.get(key);
            if (value instanceof String || value instanceof Enumeration || value instanceof Long || value instanceof Double || value instanceof Date) {
                bonitaVars.put(key, value);
            } else {
                pbVars.put(key, value);
            }
        }
        ProcessInstanceUUID puuid = runtimeAPI.instantiateProcess(uuid, bonitaVars);
        HibernateUtil hutil = new HibernateUtil();
        hutil.saveObjects(puuid.toString(), null, pbVars);
        return puuid;
    }

    public void saveProcessVariables(ActivityInstance<TaskInstance> task, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        Map<String, Object> bonitaVars = new HashMap<String, Object>();
        Map<String, Object> pbVars = new HashMap<String, Object>();
        for (String key : vars.keySet()) {
            Object value = vars.get(key);
            if (value instanceof String || value instanceof Enumeration || value instanceof Long || value instanceof Double || value instanceof Date) {
                bonitaVars.put(key, value);
            } else if (value != null) {
                pbVars.put(key, value);
            }
        }
        HibernateUtil hutil = new HibernateUtil();
//        Logger.getLogger("DEBUG").log(Level.SEVERE, "pbVars = " + pbVars.keySet());
        hutil.saveObjects(task.getProcessInstanceUUID().toString(), task.getUUID().toString(), pbVars);
        for (String key : bonitaVars.keySet()) {
            setProcessInstanceVariable(task.getProcessInstanceUUID(), key, vars.get(key));
        }
    }

    public Set<DataFieldDefinition> getProcessDataFields(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessDataFields(uuid);
    }

    public Set<ActivityDefinition> getProcessActivities(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessActivities(uuid);
    }

    public Collection<ActivityInstance<TaskInstance>> getActivities(ActivityState state) throws Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryRuntimeAPI.getTaskList(state);
    }

    public void startTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.startTask(taskUUID, b);
    }

    public void finishTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.finishTask(taskUUID, b);
    }

    public void finishTask(ActivityInstance<TaskInstance> task, boolean b, Map<String, Object> vars) throws TaskNotFoundException, IllegalTaskStateException, InstanceNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        for (String varKey : vars.keySet()) {
            setProcessInstanceVariable(task.getProcessInstanceUUID(), varKey, vars.get(varKey));
        }
        runtimeAPI.finishTask(task.getBody().getUUID(), b);
    }

    public void assignTask(TaskUUID taskUUID, String user) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.assignTask(taskUUID, user);
    }

    public void resumeTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.resumeTask(taskUUID, b);
    }

    public void suspendTask(TaskUUID taskUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.suspendTask(taskUUID, b);
    }

    public void setProcessInstanceVariable(ProcessInstanceUUID piUUID, String varName, Object varValue) throws InstanceNotFoundException, VariableNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.setProcessInstanceVariable(piUUID, varName, varValue);
    }

    public Map<String, Object> getActivityInstanceVariables(ActivityInstanceUUID aiUUID) throws ActivityNotFoundException, Exception, ActivityNotFoundException, ActivityNotFoundException, ActivityNotFoundException, ActivityNotFoundException {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryRuntimeAPI.getActivityInstanceVariables(aiUUID);
    }

    public Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID piUUID, boolean loadObject) throws InstanceNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        Map<String, Object> result = new HashMap<String, Object>();
        result.putAll(queryRuntimeAPI.getProcessInstanceVariables(piUUID));
        if (loadObject) {
//            Logger.getLogger("DEBUG").log(Level.SEVERE, "loadObject = " + loadObject);
            HibernateUtil hutil = new HibernateUtil();
//            Logger.getLogger("DEBUG").log(Level.SEVERE, "hutil = " + hutil);
            Map<String, Object> pbVars = hutil.findObjects(piUUID.toString(), piUUID.toString());
//            Logger.getLogger("DEBUG").log(Level.SEVERE, "pbVars.size = " + pbVars.size());
            result.putAll(pbVars);
//            Logger.getLogger("DEBUG").log(Level.SEVERE, "result.size = " + result.size());
        }
        return result;
    }

    public Collection<Resource> getPackage(PackageDefinitionUUID pdUUID) throws PackageNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getPackage(pdUUID).getBusinessArchive().getClasses();
    }

    public HashMap<String, String> getFormNames(String uiString) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("ui.xml", HashMap.class);
        return (HashMap<String, String>) xstream.fromXML(uiString);
    }

//    public TaskWindow getStartWindow(ProcessDefinition procd, Application application) {
//        try {
//            programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
//            HibernateUtil hutil = new HibernateUtil();
//            PbActivityUi pbActivityUi = hutil.findPbActivityUi(procd.getUUID().toString());
//            Class b = ProcessBaseClassLoader.getCurrent().loadClass(pbActivityUi.getUiClass());
//            TaskWindow taskWindow = (TaskWindow) b.newInstance();
//            taskWindow.setTaskInfo(procd, null);
//            taskWindow.exec();
//            return taskWindow;
//        } catch (Exception ex) {
//            Logger.getLogger(BPMModule.class.getName()).log(Level.SEVERE, ex.getMessage());
//            DefaultTaskWindow defaultTaskWindow = new DefaultTaskWindow(procd, null);
//            defaultTaskWindow.exec();
//            return defaultTaskWindow;
//        }
//    }
//    public TaskWindow getTaskWindow(ActivityInstance<TaskInstance> task, Application application) {
//        ProcessDefinition procd = null;
//        try {
//            programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
//            procd = queryDefinitionAPI.getProcess(task.getProcessDefinitionUUID());
//            HibernateUtil hutil = new HibernateUtil();
//            PbActivityUi pbActivityUi = hutil.findPbActivityUi(task.getUUID().toString());
//            Class b = ProcessBaseClassLoader.getCurrent().loadClass(pbActivityUi.getUiClass());
//            TaskWindow taskWindow = (TaskWindow) b.newInstance();
//            taskWindow.setTaskInfo(procd, task);
//            taskWindow.exec();
//            return taskWindow;
//        } catch (Exception ex) {
//            Logger.getLogger(BPMModule.class.getName()).log(Level.SEVERE, ex.getMessage());
//            DefaultTaskWindow defaultTaskWindow = new DefaultTaskWindow(procd, task);
//            defaultTaskWindow.exec();
//            return defaultTaskWindow;
//        }
//    }
    public ActivityDefinition getProcessActivity(ActivityInstance<ActivityBody> ai) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityId());
    }

    public ActivityDefinition getProcessActivity(ProcessDefinitionUUID pdUUID, String ActivityID) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessActivity(pdUUID, ActivityID);
    }

    public ParticipantDefinition getProcessParticipant(ProcessDefinitionUUID pdUUID, String participant) throws ParticipantNotFoundException, ProcessNotFoundException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        return queryDefinitionAPI.getProcessParticipant(pdUUID, participant);
    }

    public Set<PackageDefinition> getPackageDefinitions() {
        return queryDefinitionAPI.getPackages();
    }

    public ProcessDefinition getProcessDefinition(PackageDefinition pd) throws PackageNotFoundException {
        return queryDefinitionAPI.getPackageProcesses(pd.getPackageDefinitionUUID()).iterator().next();
    }

    public void deployClassesInJar(byte[] jar) throws DeploymentException {
        managementAPI.deployClassesInJar(jar);
    }

    public Map<String, ProcessDefinition> deploy(byte[] bar, int type) throws DeploymentException, ProcessNotFoundException, VariableNotFoundException, Exception {
        Map<String, ProcessDefinition> result = new HashMap();
        if (type == BAR) {
            result = managementAPI.deployBar(bar);
        } else {
            result = managementAPI.deployXpdl(bar);
        }
        for (ProcessDefinition pd : result.values()) {
            Set<ActivityDefinition> acts = this.getProcessActivities(pd.getProcessDefinitionUUID());
            HashMap<String, String> activities = new HashMap<String, String>();
            for (ActivityDefinition ad : acts) {
                if (ad.getPerformer() != null) {
                    activities.put(ad.getUUID().toString(), ad.getDescription() != null ? ad.getDescription() : ad.getActivityId());
                }
            }
            HibernateUtil hutil = new HibernateUtil();
            hutil.addProcessUiEmpty(pd.getUUID().toString(),
                    pd.getDescription() != null ? pd.getDescription() : pd.getUUID().toString(),
                    activities);
        }
        return result;
    }

    public void deletePackage(PackageDefinition pd) throws PackageNotFoundException, UndeletableInstanceException, UndeletablePackageException {
        HibernateUtil hutil = new HibernateUtil();
        hutil.deletePbProcess(getProcessDefinition(pd).getUUID().toString());
        managementAPI.deletePackage(pd.getPackageDefinitionUUID());
    }

    public void deleteAllProcessInstances(PackageDefinition pd) throws PackageNotFoundException, ProcessNotFoundException, UndeletableInstanceException {
        Set<ProcessDefinition> pds = queryDefinitionAPI.getPackageProcesses(pd.getPackageDefinitionUUID());
        for (ProcessDefinition prd : pds) {
            ArrayList<String> piUUIDs = new ArrayList<String>();
            for (ProcessInstance pi : getProcessInstancesByUUID(prd.getProcessDefinitionUUID())) {
                piUUIDs.add(pi.getProcessInstanceUUID().toString());
            }
            runtimeAPI.deleteAllProcessInstances(prd.getProcessDefinitionUUID());
            HibernateUtil hutil = new HibernateUtil();
            hutil.deletePbProcessess(piUUIDs);
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

    public Set<ActivityInstance> getActivityInstances() throws ProcessNotFoundException, ActivityNotFoundException {
        Set<ActivityInstance> result = new HashSet();
        try {
            Set<ProcessInstance> pis = queryRuntimeAPI.getProcessInstances();
            for (ProcessInstance pi : pis) {
                result.addAll(queryRuntimeAPI.getActivityInstances(pi.getProcessInstanceUUID()));
            }
        } catch (InstanceNotFoundException ex) {
            Logger.getLogger(BPMModule.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return result;
    }

    public ActivityInstance getActivityInstance(ActivityInstanceUUID activityInstanceUUID) throws ProcessNotFoundException, ActivityNotFoundException {
        return queryRuntimeAPI.getActivityInstance(activityInstanceUUID);
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

    public ActivityDefinition getProcessActivityDefinition(ActivityInstance<ActivityBody> ai) throws ProcessNotFoundException, ActivityNotFoundException {
        return queryDefinitionAPI.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityId());
    }

    public ActivityDefinition getTaskDefinition(ActivityInstance<TaskInstance> ai) throws ProcessNotFoundException, ActivityNotFoundException {
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

    public Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID piUUID) throws InstanceNotFoundException {
        return queryRuntimeAPI.getProcessInstanceVariables(piUUID);
    }

//    public ActivityWindow getActivityWindow(ActivityInstance<ActivityBody> activity, ActivityInstance<TaskInstance> task) throws ProcessNotFoundException, PackageNotFoundException {
//        ProcessDefinition procd = queryDefinitionAPI.getProcess(activity.getProcessDefinitionUUID());
//        ActivityWindow activityWindow = new ActivityWindow(procd, activity, task);
//        return activityWindow;
//    }
    public void assignTask(TaskUUID taskUUID, Set<String> users) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.assignTask(taskUUID, users);
    }

    public void assignTask(TaskUUID taskUUID) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.assignTask(taskUUID);
    }

    public void unassignTask(TaskUUID taskUUID) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        programmaticLogin.login(currentUserUID, "", "processBaseRealm", false);
        runtimeAPI.unassignTask(taskUUID);
    }
}
