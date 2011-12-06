/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
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
package org.processbase.ui.core;

//import com.sun.appserv.security.ProgrammaticLogin; //if executed other than glassfish this will throw class not found exception ?
import java.io.File;

import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import org.apache.commons.collections.iterators.EntrySetMapIterator;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.ow2.bonita.env.Environment;
import org.ow2.bonita.facade.BAMAPI;
import org.ow2.bonita.facade.CommandAPI;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.exception.UndeletableProcessException;
import org.ow2.bonita.facade.APIAccessor;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RepairAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.WebAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition.Type;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.ParticipantDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.DeploymentException;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.MetadataNotFoundException;
import org.ow2.bonita.facade.exception.ParticipantNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.RoleAlreadyExistsException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.exception.UserAlreadyExistsException;
import org.ow2.bonita.facade.exception.UserNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.DocumentUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.services.DocumentationManager;
import org.ow2.bonita.services.Folder;
import org.ow2.bonita.services.LargeDataRepository;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.facade.exception.UndeletableInstanceException;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.ProfileMetadata;
import org.ow2.bonita.facade.identity.Role;
import org.ow2.bonita.facade.identity.User;
import org.ow2.bonita.facade.privilege.Rule;
import org.ow2.bonita.facade.privilege.Rule.RuleType;
import org.ow2.bonita.facade.runtime.Category;
import org.ow2.bonita.facade.runtime.Comment;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.facade.uuid.ActivityDefinitionUUID;
import org.ow2.bonita.identity.auth.DomainOwner;
import org.ow2.bonita.identity.auth.UserOwner;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.light.LightTaskInstance;
import org.ow2.bonita.util.GroovyException;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.InitialAttachment;
import org.ow2.bonita.facade.uuid.AbstractUUID;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.Command;
import org.ow2.bonita.util.EnvTool;
import org.ow2.bonita.util.GroovyExpression;
import org.ow2.bonita.util.Misc;
import org.ow2.bonita.util.SimpleCallbackHandler;
import org.processbase.commands.documents.DeleteDocumentCommand;
import org.processbase.engine.bam.command.DeleteMetaDim;
import org.processbase.ui.core.bonita.diagram.Diagram;
import org.processbase.ui.core.bonita.process.ProcessParticipant;
import org.processbase.ui.core.util.CacheUtil;
import org.processbase.ui.core.util.ICacheDelegate;

 

import com.sun.appserv.security.AppservRealm;
import com.sun.appserv.security.ProgrammaticLogin;
import com.sun.enterprise.security.auth.realm.Realm;

import org.apache.log4j.Logger;
import org.h2.util.StringUtils;
/**
 *
 * @author mgubaidullin
 */
public class BPMModule {
	private static final String BUSINESS_ARCHIVE = "BAR_RESOURCE";
	public static final String USER_GUEST = "guest";
    /*private final RuntimeAPI runtimeAPI;
    private final QueryRuntimeAPI queryRuntimeAPI;
    private final ManagementAPI managementAPI;
    private final QueryDefinitionAPI queryDefinitionAPI;
    private final RepairAPI repairAPI;
    private final WebAPI webAPI;
    private final IdentityAPI identityAPI;
    private final BAMAPI bamAPI;
    private final CommandAPI commandAPI;*/
    //final APIAccessor apiAccessor=null; 
    private String currentUserUID;
	final static Logger logger = Logger.getLogger(BPMModule.class);
	//private DocumentationManager documentatinManager;

    public BPMModule(String currentUserUID) {
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }
        this.currentUserUID = currentUserUID;
        try {
            initContext();
        } catch (Exception ex) {
			logger.error("constructor", ex);
            //Logger.getLogger(BPMModule.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        //APIAccessor apiAccessor = AccessorUtil.getAPIAccessor(Constants.BONITA_EJB_ENV);
		/*runtimeAPI = AccessorUtil.getRuntimeAPI();
        queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        managementAPI = AccessorUtil.getManagementAPI();
        queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        repairAPI = AccessorUtil.getRepairAPI();
        webAPI = AccessorUtil.getWebAPI();
        identityAPI = AccessorUtil.getIdentityAPI();
        bamAPI = AccessorUtil.getBAMAPI();
        commandAPI = AccessorUtil.getCommandAPI();*/
        
    }
    
    
    
    private Class tryClass(String name)
    {
        try
        {
            return Class.forName(name);
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
    }
    
    public User authUserWithJaas(String username, String password){
    	try{
    		LoginContext ctx = new LoginContext("SmartBPM", new ProcessbaseAuthCallbackHandler(username, password));
    		ctx.login();
    		//ctx.getSubject().getPrincipals();
    		return null;
    	}
    	catch(Exception e){
    		logger.error("AuthUser", e);
    	}
		return null;
    }


    public void initContext() throws Exception {
    	if (Constants.APP_SERVER.startsWith("GLASSFISH")) {
    		
    		/*try {
    			Subject subject=new Subject()
    			LoginContext ctx = new LoginContext("processBaseRealm", new ProcessbaseAuthCallbackHandler());
        		ctx.login();
        			
			} catch (Exception e) {
				// TODO: handle exception
			}
    		Class authClass = tryClass("com.sun.appserv.security.ProgrammaticLogin");
    		if(authClass!=null)
    		{
    			com.sun.appserv.security.ProgrammaticLogin pl=new ProgrammaticLogin();
    			
    			Method login=authClass.getMethod("login");
    			login.invoke(authClass.newInstance(), currentUserUID, "".toCharArray(), "processBaseRealm", false);        		
    		}
    		 
    		try {
    			ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
                programmaticLogin.login(currentUserUID, "".toCharArray(), "processBaseRealm", false);	
			} catch (Exception e) {
				
			}
    		if(Realm.isValidRealm("processBaseRealm"))
    		{
    			Realm r=Realm.getInstance("processBaseRealm")
    		}
    		LoginContext loginContext = null;
    		Configuration config = Configuration.getConfiguration();
            CallbackHandler handler = new SimpleCallbackHandler(currentUserUID, "");
            Subject subject=new Subject();
    		 try {
    			 
    			 AppConfigurationEntry[] entries = config.getAppConfigurationEntry("ProcessBaseAuth");
    			 if(entries!=null)
    			 {
					for (AppConfigurationEntry entry : entries) {
						String loginModule=entry.getLoginModuleName();
						Class c=Class.forName(loginModule);
						Object[] args = { };
						 Constructor constructor = c.getConstructor();
						 Object module=constructor.newInstance(args);   
						 Method init=findMethod("init", c);
							if(init!=null)
							{	
								Properties property=new Properties();
								for(Entry<String,?> param:entry.getOptions().entrySet())
									property.put(param.getKey(), param.getValue());
								init.invoke(module, property);
							}
							Method login=findMethod("login", c);
							if(login!=null)
								login.invoke(module, null);
					}
						 
					
    				 
    			 }
    		 }
    			 catch(Exception e)
    			 {
    				 
    			 }*/
    	            /*loginContext = new LoginContext("ProcessBaseAuth", subject, handler, config);
    	            loginContext.login();
    	            loginContext.logout();
    	            
    	        } catch (javax.security.auth.login.LoginException e) {
    	            try {
    	                // make another try after refresh
    	                javax.security.auth.login.Configuration.getConfiguration().refresh();
    	                loginContext = new LoginContext("ProcessBaseAuth", subject, handler, config);
    	                loginContext.login();
    	                loginContext.logout();
    	            } catch (javax.security.auth.login.LoginException e2) {
    	            
    	                throw new RuntimeException("Authentication failed for user '" + currentUserUID + "'");
    	            }
    	        }*/
    		

    	}
        DomainOwner.setDomain(Constants.BONITA_DOMAIN);
        UserOwner.setUser(currentUserUID);
    }
    
    private Method findMethod(String name, Class c)
    {
    	for (Method method : c.getMethods()) 
    	{
			if(method.getName()==name)
				return method;
		}
    	 return null;
    }

    public Set<ProcessDefinition> getProcessDefinitions() throws Exception {
        initContext();
        return getQueryDefinitionAPI().getProcesses();
    }

    public boolean isUserAdmin() throws Exception {
    	logger.debug("isUserAdmin");
        initContext();
        return getManagementAPI().isUserAdmin(currentUserUID);
    }
    
    


    public Set<ProcessDefinition> getProcessDefinitions(ProcessState state) throws Exception {
    	logger.debug("getProcessDefinitions");
        initContext();
        return getQueryDefinitionAPI().getProcesses(state);
    }

    public LightProcessDefinition getLightProcessDefinition(ProcessDefinitionUUID pdUUID) throws Exception {
    	logger.debug("getLightProcessDefinition");
        initContext();
        return getQueryDefinitionAPI().getLightProcess(pdUUID);
    }

    public Set<LightProcessDefinition> getLightProcessDefinitions() throws Exception {
    	logger.debug("getLightProcessDefinitions");
        initContext();
        return getQueryDefinitionAPI().getLightProcesses();
    }

    public Set<LightProcessDefinition> getAllowedLightProcessDefinitions(Group groupFilter) throws Exception {
    	logger.debug("getAllowedLightProcessDefinitions");
        initContext();
        User user = getIdentityAPI().findUserByUserName(currentUserUID);
        Set<String> membershipUUIDs = new HashSet<String>();
        if(groupFilter==null){
        	return getQueryDefinitionAPI().getLightProcesses(ProcessState.ENABLED);
        }
        for (Membership membership : user.getMemberships()) {
        	if(groupFilter==null)
        		membershipUUIDs.add(membership.getUUID());
        	else if(membership.getGroup().getName().equals(groupFilter.getName()))
        		membershipUUIDs.add(membership.getUUID());
        }
        List<Rule> userRules = getManagementAPI().getApplicableRules(RuleType.PROCESS_START, null, null, null, membershipUUIDs, null);

        Set<String> processException;
        Set<ProcessDefinitionUUID> processUUIDException = new HashSet<ProcessDefinitionUUID>();
        for (Rule r : userRules) {
            processException = r.getEntities();
            for (String processID : processException) {
                processUUIDException.add(new ProcessDefinitionUUID(processID));
            }
        }
        Set<LightProcessDefinition> result = new HashSet<LightProcessDefinition>();
        for (LightProcessDefinition lpd : getQueryDefinitionAPI().getLightProcesses(processUUIDException)) {
            if (lpd.getState().equals(ProcessState.ENABLED)) {
                result.add(lpd);
            }
        }
        return result;
    }

    public Set<LightProcessDefinition> getLightProcessDefinitions(ProcessState state) throws Exception {
    	logger.debug("getLightProcessDefinitions");
        initContext();
        return getQueryDefinitionAPI().getLightProcesses(state);
    }

    public void disableProcessDefinitions(ProcessDefinitionUUID uuid) throws Exception {
    	logger.debug("disableProcessDefinitions");
        initContext();
        getManagementAPI().disable(uuid);
    }

    public void enableProcessDefinitions(ProcessDefinitionUUID uuid) throws Exception {
    	logger.debug("enableProcessDefinitions");
        initContext();
        getManagementAPI().enable(uuid);
    }

    public void archiveProcessDefinitions(ProcessDefinitionUUID uuid) throws Exception {
    	logger.debug("archiveProcessDefinitions");
        initContext();
        getManagementAPI().archive(uuid);
    }

    public ProcessInstanceUUID startNewProcess(ProcessDefinitionUUID uuid, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("startNewProcess");
        initContext();
        return getRuntimeAPI().instantiateProcess(uuid, vars);
    }

    public ProcessInstanceUUID startNewProcess(ProcessDefinitionUUID uuid, Map<String, Object> vars, Collection<InitialAttachment> initialAttachments) throws ProcessNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("startNewProcess");
        initContext();
        return getRuntimeAPI().instantiateProcess(uuid, vars, initialAttachments);
    }

    public ProcessInstanceUUID startNewProcess(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("startNewProcess");
        initContext();
        return getRuntimeAPI().instantiateProcess(uuid);
    }

    public void saveProcessVariables(TaskInstance task, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("saveProcessVariables");
        for (String key : vars.keySet()) {
            setProcessInstanceVariable(task.getProcessInstanceUUID(), key, vars.get(key));
        }
    }

    public void saveProcessVariables2(ActivityInstance activity, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("saveProcessVariables2");
        for (String key : vars.keySet()) {
            setProcessInstanceVariable(activity.getProcessInstanceUUID(), key, vars.get(key));
        }
    }

    public Set<DataFieldDefinition> getProcessDataFields(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, Exception {
    	logger.debug("getProcessDataFields");
        initContext();
        return getQueryDefinitionAPI().getProcessDataFields(uuid);
    }

    public Set<DataFieldDefinition> getActivityDataFields(ActivityDefinitionUUID aduuid) throws ProcessNotFoundException, Exception {
    	logger.debug("getActivityDataFields");
        initContext();
        return getQueryDefinitionAPI().getActivityDataFields(aduuid);
    }

    public DataFieldDefinition getProcessDataField(ProcessDefinitionUUID uuid, String varName) throws ProcessNotFoundException, Exception {
    	logger.debug("getProcessDataField");
        initContext();
        return getQueryDefinitionAPI().getProcessDataField(uuid, varName);
    }

    public Map<String, ActivityDefinition> getProcessInitialActivities(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("getProcessInitialActivities");
        initContext();
        return getQueryDefinitionAPI().getProcess(uuid).getInitialActivities();
    }

    public Set<ActivityDefinition> getProcessActivities(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("getProcessActivities");
        initContext();
        return getQueryDefinitionAPI().getProcessActivities(uuid);
    }
    
   

    public Collection<TaskInstance> getTaskList(ActivityState state) throws Exception {
    	logger.debug("getTaskList");
        initContext();
        return getQueryRuntimeAPI().getTaskList(state);
    }

    public Collection<LightTaskInstance> getLightTaskList(ActivityState state) throws Exception {
    	logger.debug("getLightTaskList");
        initContext();
        return getQueryRuntimeAPI().getLightTaskList(state);
    }
  
    public Collection<LightTaskInstance> getLightTaskList(ProcessInstanceUUID instanceUUID, ActivityState state) throws Exception {
	        initContext();
	        return getQueryRuntimeAPI().getLightTaskList(instanceUUID, state);
	}

    public Set<ProcessInstance> getUserInstances() throws Exception {
    	logger.debug("getUserInstances");
        initContext();
        return getQueryRuntimeAPI().getUserInstances();
    }

    public Set<LightProcessInstance> getLightUserInstances() throws Exception {
    	logger.debug("getLightUserInstances");
        initContext();
        return getQueryRuntimeAPI().getLightUserInstances();
    }

    public TaskInstance startTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
    	logger.debug("startTask");
        initContext();
        TaskInstance ti = getTaskInstance(activityInstanceUUID);
        if (ti != null && ti.getState().equals(ActivityState.READY)) {
            getRuntimeAPI().startTask(activityInstanceUUID, b);
            return getTaskInstance(activityInstanceUUID);
        }
        return ti;
    }
    
    public TaskInstance nextUserTask(ProcessInstanceUUID processInstanceUUID, String currentUserName)  throws Exception {
    	logger.debug("nextUserTask");
        initContext();
    	//Set<ActivityInstance> activities= getQueryRuntimeAPI().getActivityInstances(processInstanceUUID);
    	Set<LightActivityInstance> lightActivities = getQueryRuntimeAPI().getLightActivityInstances(processInstanceUUID);
    	//Collection<TaskInstance> taskList = getQueryRuntimeAPI().getTaskList(processInstanceUUID, ActivityState.EXECUTING);
    	for (LightActivityInstance instance : lightActivities) {    		
    		if(instance.getState() == ActivityState.READY 
    				|| instance.getState() == ActivityState.EXECUTING 
					|| instance.getState()==ActivityState.SUSPENDED)
			{
    			
    			if(instance.getType()==Type.Subflow){
    				ProcessInstance subProcessInstance = getProcessInstance(instance.getSubflowProcessInstanceUUID());
    				Set<TaskInstance> tasks = subProcessInstance.getTasks();
    				for (TaskInstance taskInstance : tasks) {
						if(taskInstance.getTaskCandidates().contains(currentUserName) 
								&& (taskInstance.getState() == ActivityState.READY 
									|| taskInstance.getState()==ActivityState.EXECUTING 
									|| taskInstance.getState()==ActivityState.SUSPENDED)
								)
							return assignAndStartTask(taskInstance.getUUID(), currentUserName);
					}
    				
    			}
    			else{
    				
					if(getQueryRuntimeAPI().getTaskCandidates(instance.getUUID()).contains(currentUserName))
						return assignAndStartTask(instance.getUUID(), currentUserName);
	    			}
			}
		}
		return null;
		
	} 

    public void finishTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
    	logger.debug("finishTask");
        initContext();
        getRuntimeAPI().finishTask(activityInstanceUUID, b);
    }

   public void finishTask(TaskInstance task, boolean b, Map<String, Object> pVars, Map<String, Object> aVars) throws TaskNotFoundException, IllegalTaskStateException, InstanceNotFoundException, VariableNotFoundException, Exception {
	   logger.debug("finishTask");
       initContext();
        //runtimeAPI.setProcessInstanceVariables(task.getProcessInstanceUUID(), pVars);
        //runtimeAPI.setActivityInstanceVariables(task.getUUID(), aVars);
        setProcessAndActivityInstanceVariables(task, pVars, aVars);
        getRuntimeAPI().finishTask(task.getUUID(), b);
    }

	private void setProcessAndActivityInstanceVariables(TaskInstance task,
			Map<String, Object> pVars, Map<String, Object> aVars)
			throws InstanceNotFoundException, VariableNotFoundException,
			ActivityNotFoundException {
		logger.debug("setProcessAndActivityInstanceVariables");
		getRuntimeAPI().setProcessInstanceVariables(task.getProcessInstanceUUID(), pVars);
		if(aVars.size()>0)
			getRuntimeAPI().setActivityInstanceVariables(task.getUUID(), aVars);
        /*for (Map.Entry<String, Object> entry : pVars.entrySet()) {
            getRuntimeAPI().setProcessInstanceVariable(task.getProcessInstanceUUID(), entry.getKey(), entry.getValue());
		}
        for (Map.Entry<String, Object> entry : aVars.entrySet()) {
            getRuntimeAPI().setActivityInstanceVariable(task.getUUID(), entry.getKey(), entry.getValue());
        }*/
	}
	
    public void finishTask(TaskInstance task, boolean b, Map<String, Object> pVars, Map<String, Object> aVars, Map<AttachmentInstance, byte[]> attachments) throws TaskNotFoundException, IllegalTaskStateException, InstanceNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("finishTask");
        initContext();
        if(task.isTaskAssigned()==false && task.getTaskCandidates().contains(currentUserUID)){
        	LightTaskInstance lightTaskInstance = getQueryRuntimeAPI().getLightTaskInstance(task.getUUID());
        	if(lightTaskInstance.getState()!=ActivityState.EXECUTING)
        		getRuntimeAPI().startTask(task.getUUID(), true);
        	 //assignTask(task.getUUID(), currentUserUID);
        }
        //runtimeAPI.setProcessInstanceVariables(task.getProcessInstanceUUID(), pVars);
        //runtimeAPI.setActivityInstanceVariables(task.getUUID(), aVars);
        setProcessAndActivityInstanceVariables(task, pVars, aVars);
        if(attachments!=null){
	        for (AttachmentInstance a : attachments.keySet()) {
	        	logger.debug(a.getProcessInstanceUUID() + " " + a.getName() + " " + a.getFileName() + " " + attachments.get(a).length);
	        }
	        getRuntimeAPI().addAttachments(attachments);
        }
        getRuntimeAPI().finishTask(task.getUUID(), true);
    }

    public void addAttachment(ProcessInstanceUUID instanceUUID, String name, String fileName, String mimeType, byte[] value) throws Exception {
    	if(value.length==0)
    		return;
    	logger.debug("addAttachment");
        initContext();
        Map<String, String> metadata=new Hashtable<String, String>();
        metadata.put("content-type", mimeType);
		//runtimeAPI.addAttachment(instanceUUID, name, fileName, value);
        getRuntimeAPI().addAttachment(instanceUUID, name, null, null, fileName, metadata, value);
    }

    public byte[] getAttachmentValue(String processUUID, String name) throws Exception {
    	logger.debug("getAttachmentValue");
    	initContext();
    	AttachmentInstance attachmentInstance = getQueryRuntimeAPI().getLastAttachment(new ProcessInstanceUUID(processUUID), name, new Date());
    	return getQueryRuntimeAPI().getAttachmentValue(attachmentInstance);
    }
    
    public AttachmentInstance getAttachment(String processUUID, String name) throws Exception {
    	logger.debug("getAttachmentValue");
        initContext();
        AttachmentInstance attachmentInstance = getQueryRuntimeAPI().getLastAttachment(new ProcessInstanceUUID(processUUID), name, new Date());
        return attachmentInstance;
        //return queryRuntimeAPI.getAttachmentValue(attachmentInstance);
    }
    
    public byte[] getAttachmentBytes(AttachmentInstance attachmentInstance) throws Exception {
    	logger.debug("getAttachmentValue");
        initContext();
        return getQueryRuntimeAPI().getAttachmentValue(attachmentInstance);
    }

    public List<AttachmentInstance> getLastAttachments(ProcessInstanceUUID instanceUUID, String regex) throws Exception {
    	logger.debug("getLastAttachments");
        initContext();
        return new ArrayList<AttachmentInstance>(getQueryRuntimeAPI().getLastAttachments(instanceUUID, regex));
    }

    public org.ow2.bonita.facade.runtime.Document getDocument(DocumentUUID docId) throws Exception 
    {
    	initContext();
    	org.ow2.bonita.facade.runtime.Document doc = getQueryRuntimeAPI().getDocument(docId);
    	return doc;
    }
    public List<AttachmentInstance> getLastAttachments(ProcessInstanceUUID instanceUUID, Set<String> attachmentNames) throws Exception {
    	logger.debug("getLastAttachments");
        initContext();
        return new ArrayList<AttachmentInstance>(getQueryRuntimeAPI().getLastAttachments(instanceUUID, attachmentNames));
    }

	public TaskInstance assignTask(ActivityInstanceUUID activityInstanceUUID, String user) throws TaskNotFoundException, IllegalTaskStateException, Exception {
    	logger.debug("assignTask");
        initContext();
        TaskInstance ti = getTaskInstance(activityInstanceUUID);
        if (ti != null && ti.isTaskAssigned() && !ti.getTaskUser().equals(user)) {
            return null;
        }
        getRuntimeAPI().assignTask(activityInstanceUUID, user);
        return getTaskInstance(activityInstanceUUID);
    }

    public TaskInstance assignAndStartTask(ActivityInstanceUUID activityInstanceUUID, String user) throws TaskNotFoundException, IllegalTaskStateException, Exception {
    	logger.debug("assignAndStartTask");
        initContext();
        TaskInstance ti = getTaskInstance(activityInstanceUUID);
        if (ti != null && ti.isTaskAssigned() && !ti.getTaskUser().equals(user)) {
            return null;
        }
        getRuntimeAPI().assignTask(activityInstanceUUID, user);
        if (ti != null && ti.getState().equals(ActivityState.READY)) {
            getRuntimeAPI().startTask(activityInstanceUUID, true);
        }
        return getTaskInstance(activityInstanceUUID);
    }

    public TaskInstance resumeTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
    	logger.debug("resumeTask");
        initContext();
        getRuntimeAPI().resumeTask(activityInstanceUUID, b);
        return getTaskInstance(activityInstanceUUID);
    }

    public TaskInstance suspendTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
    	logger.debug("suspendTask");
        initContext();
        getRuntimeAPI().suspendTask(activityInstanceUUID, b);
        return getTaskInstance(activityInstanceUUID);
    }

    public void setProcessInstanceVariable(ProcessInstanceUUID piUUID, String varName, Object varValue) throws InstanceNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("setProcessInstanceVariable");
        initContext();
        getRuntimeAPI().setProcessInstanceVariable(piUUID, varName, varValue);
    }

    public void setActivityInstanceVariable(ActivityInstanceUUID aiuuid, String varName, Object varValue) throws InstanceNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("setActivityInstanceVariable");
        initContext();
        getRuntimeAPI().setActivityInstanceVariable(aiuuid, varName, varValue);
    }

    public Map<String, Object> getActivityInstanceVariables(ActivityInstanceUUID aiUUID) throws ActivityNotFoundException, Exception, ActivityNotFoundException, ActivityNotFoundException, ActivityNotFoundException, ActivityNotFoundException {
    	logger.debug("getActivityInstanceVariables");
        initContext();
        return getQueryRuntimeAPI().getActivityInstanceVariables(aiUUID);
    }

    public Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, Exception {
    	logger.debug("getProcessInstanceVariables");
        initContext();
        return getQueryRuntimeAPI().getProcessInstanceVariables(piUUID);
    }

    public Object getProcessInstanceVariable(ProcessInstanceUUID piUUID, String varName) throws InstanceNotFoundException, Exception {
    	logger.debug("getProcessInstanceVariable");
        initContext();
        return getQueryRuntimeAPI().getProcessInstanceVariable(piUUID, varName);
    }

    public ActivityDefinition getProcessActivity(ProcessDefinitionUUID pdUUID, String ActivityName) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
    	logger.debug("getProcessActivity");
        initContext();
        return getQueryDefinitionAPI().getProcessActivity(pdUUID, ActivityName);
    }

    public ParticipantDefinition getProcessParticipant(ProcessDefinitionUUID pdUUID, String participant) throws ParticipantNotFoundException, ProcessNotFoundException, Exception {
    	logger.debug("getProcessParticipant");
        initContext();
        return getQueryDefinitionAPI().getProcessParticipant(pdUUID, participant);
    }

    public Set<ProcessDefinition> getProcesses() throws Exception {
    	logger.debug("getProcess");
        initContext();
        return getQueryDefinitionAPI().getProcesses();
    }

    public Set<ProcessDefinition> getProcesses(ProcessState ps) throws Exception {
    	logger.debug("getProcesses");
        initContext();
        return getQueryDefinitionAPI().getProcesses(ps);
    }

    public ProcessDefinition getProcessDefinition(ProcessDefinition pd) throws ProcessNotFoundException, Exception {
    	logger.debug("getProcessDefinition");
        initContext();
        return getQueryDefinitionAPI().getProcess(pd.getUUID());
    }

    public ProcessDefinition deploy(BusinessArchive bar) throws DeploymentException, ProcessNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("deploy");
        initContext();
        ProcessDefinition result = getManagementAPI().deploy(bar);
        return result;
    }

    public ProcessDefinition deploy(BusinessArchive bar, String emptyCategoryName) throws DeploymentException, ProcessNotFoundException, VariableNotFoundException, Exception {
    	logger.debug("deploy");
        initContext();
        ProcessDefinition result = getManagementAPI().deploy(bar);
        // add to empty category
        if (result.getCategoryNames().isEmpty()) {
            Set<String> emptyCategory = new HashSet<String>(1);
            emptyCategory.add(emptyCategoryName);
            if (getWebAPI().getCategories(emptyCategory).isEmpty()) {
                getWebAPI().addCategory(emptyCategoryName, "", "", "");
            }
            getWebAPI().setProcessCategories(result.getUUID(), emptyCategory);
        }
        // create PROCESS_START rule for process
        Set<ProcessDefinitionUUID> processes = new HashSet<ProcessDefinitionUUID>(1);
        processes.add(result.getUUID());
        Rule rule = getManagementAPI().createRule(result.getUUID().toString(), result.getName(), "PROCESS_START Rule for ProcessDefinitionUUID" + result.getUUID().toString(), RuleType.PROCESS_START);
        getManagementAPI().addExceptionsToRuleByUUID(rule.getUUID(), processes);
        return result;
    }

    public Rule createRule(String name, String label, String description, RuleType type) throws Exception{
    	logger.debug("createRule");
        initContext();
        return getManagementAPI().createRule(name, label, description, type);
    }
    
    public Rule findRule(String name, Collection<String> memberships, String entityId, RuleType type) throws Exception{
    	logger.debug("createRule");
        initContext();

        List<Rule>rules=getManagementAPI().getApplicableRules(type, null, null, null, memberships, entityId);
		if(rules==null) return null;
		for (Rule rule : rules) {
			if(rule.getName().equalsIgnoreCase(name))
				return rule;
		}
        return null;
    }

    public <E extends AbstractUUID> void addExceptionsToRuleByUUID(final String ruleUUID, final Set<E> exceptions) throws Exception{
    	logger.debug("addExceptionsToRuleByUUID");
        initContext();
        getManagementAPI().addExceptionsToRuleByUUID(ruleUUID, exceptions);
    }

    public <E extends AbstractUUID> void removeExceptionsFromRuleByUUID(final String ruleUUID, final Set<E> exceptions) throws Exception{
    	logger.debug("removeExceptionsFromRuleByUUID");
        initContext();
        getManagementAPI().removeExceptionsFromRuleByUUID(ruleUUID, exceptions);
    }

    public void deployJar(String jarName, byte[] body) throws Exception {
    	logger.debug("deployJar");
        initContext();
        if (getManagementAPI().getAvailableJars().contains(jarName)) {
            getManagementAPI().removeJar(jarName);
        }
        getManagementAPI().deployJar(jarName, body);
    }

    public void removeJar(String jarName) throws Exception {
    	logger.debug("removeJar");
        initContext();
        if (getManagementAPI().getAvailableJars().contains(jarName)) {
            getManagementAPI().removeJar(jarName);
        }
        getManagementAPI().removeJar(jarName);
    }

    public void deleteProcess(ProcessDefinition pd) throws UndeletableInstanceException, UndeletableProcessException, ProcessNotFoundException, Exception {
    	logger.debug("deleteProcess");
        initContext();
        getManagementAPI().deleteProcess(pd.getUUID());
        Rule rule = findRule(pd.getUUID().toString());
        getManagementAPI().deleteRuleByUUID(rule.getUUID());
        CacheUtil.remove("BAR_RESOURCE", pd.getUUID());//remove elemend from ehcache
        //new bonita 5.5 uses xCMIS and when deleting the process, API does not remove the folder from xCMIS this will fix it.
        execute(new DeleteDocumentCommand(pd.getUUID().toString()));
    }

    public void deleteAllProcessInstances(ProcessDefinition pd) throws Exception {
    	logger.debug("deleteAllProcessInstances");
        initContext();
        getRuntimeAPI().deleteAllProcessInstances(pd.getUUID());
    }

    public Set<ProcessInstance> getProcessInstances() throws Exception {
    	logger.debug("getProcessInstances");
        initContext();
        return getQueryRuntimeAPI().getProcessInstances();
    }

    public Set<LightProcessInstance> getLightProcessInstances() throws Exception {
    	logger.debug("getLightProcessInstances");
        initContext();
        return getQueryRuntimeAPI().getLightProcessInstances();
    }

    public Set<LightProcessInstance> getLightProcessInstances(ProcessDefinitionUUID pduuid) throws Exception {
    	logger.debug("getLightProcessInstances");
        initContext();
        return getQueryRuntimeAPI().getLightProcessInstances(pduuid);
    }

    public Set<ProcessInstance> getProcessInstancesByUUID(ProcessDefinitionUUID piUUID) throws Exception {
    	logger.debug("getProcessInstancesByUUID");
        initContext();
        return getQueryRuntimeAPI().getProcessInstances(piUUID);
    }

    public Set<ProcessInstance> getProcessInstances(ProcessDefinitionUUID piUUID, InstanceState state) throws ProcessNotFoundException, Exception {
    	logger.debug("getProcessInstances");
        initContext();
        Set<ProcessInstance> result = new HashSet<ProcessInstance>();
        Set<ProcessInstance> pis = getQueryRuntimeAPI().getProcessInstances(piUUID);
        for (ProcessInstance pi : pis) {
            if (pi.getInstanceState().equals(state)) {
                result.add(pi);
            }
        }
        return result;
    }

    public Set<LightProcessInstance> getProcessInstancesByStatus(InstanceState state) throws Exception {
    	logger.debug("getProcessInstancesByStatus");
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
    	logger.debug("getActivityInstances");
        initContext();
        Set<LightActivityInstance> result = new HashSet();
        try {
            Set<LightProcessInstance> pis = getQueryRuntimeAPI().getLightProcessInstances();
            for (LightProcessInstance pi : pis) {
                result.addAll(getQueryRuntimeAPI().getLightActivityInstances(pi.getProcessInstanceUUID()));
            }
        } catch (InstanceNotFoundException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public Set<LightActivityInstance> getActivityInstances(ProcessDefinitionUUID pduuid) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
    	logger.debug("getActivityInstances");
        initContext();
        Set<LightActivityInstance> result = new HashSet();
        try {
            Set<LightProcessInstance> pis = getQueryRuntimeAPI().getLightProcessInstances(pduuid);
            for (LightProcessInstance pi : pis) {
                result.addAll(getQueryRuntimeAPI().getLightActivityInstances(pi.getProcessInstanceUUID()));
            }
        } catch (InstanceNotFoundException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public Set<ActivityInstance> getActivityInstances(ProcessInstanceUUID processInstanceUUID) throws Exception {
    	logger.debug("getActivityInstances");
        initContext();
        return getQueryRuntimeAPI().getActivityInstances(processInstanceUUID);
    }

    public Set<LightActivityInstance> getLightActivityInstances(ProcessInstanceUUID processInstanceUUID) throws Exception {
    	logger.debug("getLightActivityInstances");
        initContext();
        return getQueryRuntimeAPI().getLightActivityInstances(processInstanceUUID);
    }

    public ActivityInstance getActivityInstance(ActivityInstanceUUID activityInstanceUUID) throws ActivityNotFoundException, Exception {
    	logger.debug("getActivityInstance");
        initContext();
        return getQueryRuntimeAPI().getActivityInstance(activityInstanceUUID);
    }

    public TaskInstance getTaskInstance(ActivityInstanceUUID activityInstanceUUID) throws ProcessNotFoundException, Exception {
    	logger.debug("getTaskInstance");
        initContext();
        try {
            return getQueryRuntimeAPI().getTask(activityInstanceUUID);
        } catch (TaskNotFoundException tex) {
            tex.printStackTrace();
            return null;
        }
    }

    public void deleteProcessInstance(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, InstanceNotFoundException, InstanceNotFoundException, UndeletableInstanceException, Exception {
    	logger.debug("deleteProcessInstance");
        initContext();
        getRuntimeAPI().deleteProcessInstance(piUUID);
    }

    public ProcessDefinition getProcessDefinition(ProcessDefinitionUUID pdUUID) throws ProcessNotFoundException, Exception {
    	logger.debug("getProcessDefinition");
        initContext();
        return getQueryDefinitionAPI().getProcess(pdUUID);
    }

    public ActivityDefinition getProcessActivityDefinition(ActivityInstance ai) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
    	logger.debug("getProcessActivityDefinition");
        initContext();
        return getQueryDefinitionAPI().getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
    }

    public ActivityDefinition getProcessActivityDefinition(LightActivityInstance lai) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
    	logger.debug("getProcessActivityDefinition");
        initContext();
        return getQueryDefinitionAPI().getProcessActivity(lai.getProcessDefinitionUUID(), lai.getActivityName());
    }

    public ActivityDefinition getTaskDefinition(ActivityInstance ai) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
    	logger.debug("getTaskDefinition");
        initContext();
        return getQueryDefinitionAPI().getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
    }

    public void assignTask(ActivityInstanceUUID activityInstanceUUID, Set<String> users) throws TaskNotFoundException, IllegalTaskStateException, Exception {
    	logger.debug("assignTask");
        initContext();
        getRuntimeAPI().assignTask(activityInstanceUUID, users);
    }

    public void assignTask(ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException, IllegalTaskStateException, Exception {
    	logger.debug("assignTask");
        initContext();
        getRuntimeAPI().assignTask(activityInstanceUUID);
    }

    public void setActivityInstancePriority(ActivityInstanceUUID activityInstanceUUID, int priority) throws TaskNotFoundException, IllegalTaskStateException, Exception {
    	logger.debug("setActivityInstancePriority");
        initContext();
        getRuntimeAPI().setActivityInstancePriority(activityInstanceUUID, priority);
    }

    public TaskInstance unassignTask(ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException, IllegalTaskStateException, Exception {
    	logger.debug("unassignTask");
        initContext();
        getRuntimeAPI().unassignTask(activityInstanceUUID);
        return getTaskInstance(activityInstanceUUID);
    }

    public void addProcessMetaData(ProcessDefinitionUUID processDefinitionUUID, String key, String value) throws Exception {
    	logger.debug("addProcessMetaData");
        initContext();
        getRuntimeAPI().addProcessMetaData(processDefinitionUUID, key, value);
    }

    public void deleteProcessMetaData(ProcessDefinitionUUID processDefinitionUUID, String key) throws Exception {
    	logger.debug("deleteProcessMetaData");
        initContext();
        getRuntimeAPI().deleteProcessMetaData(processDefinitionUUID, key);
    }

    public Map<String, String> getProcessMetaData(ProcessDefinitionUUID processDefinitionUUID) throws Exception {
    	logger.debug("getProcessMetaData");
        initContext();
        return getQueryDefinitionAPI().getProcess(processDefinitionUUID).getMetaData();
    }
    
    

    public byte[] getProcessDiagramm(LightProcessInstance pi) throws Exception {
    	logger.debug("getProcessDiagramm");
        Map<String, byte[]> resource = getBusinessArchive(pi.getProcessDefinitionUUID());
        byte[] img = getProcessDiagramm(pi.getProcessDefinitionUUID());
        byte[] proc = null;
        for (String key : resource.keySet()) {
            if (key.substring(key.length() - 4, key.length()).equals("proc")) {
                proc = resource.get(key);
            } 
        }
        File x = new File("AAA.png");
        FileOutputStream fos = new FileOutputStream(x);
        fos.write(img);
        fos.close();

        Diagram d = new Diagram(img, proc, getQueryRuntimeAPI().getLightActivityInstances(pi.getRootInstanceUUID()));
        return d.getImage();
    }

//    public XMLTaskDefinition getXMLTaskDefinition(ProcessDefinitionUUID pdUUID, String stepName) throws Exception {
//        XMLProcessDefinition process = getXMLProcessDefinition(pdUUID);
//        return process.getTasks().get(stepName);
//    }
//
//    public XMLProcessDefinition getXMLProcessDefinition(ProcessInstanceUUID processInstanceUUID) throws Exception {
//        return getXMLProcessDefinition(processInstanceUUID.getProcessDefinitionUUID());
//    }
//
//    public XMLProcessDefinition getXMLProcessDefinition(ProcessDefinitionUUID processDefinitionUUID) throws Exception {
//        initContext();
//        Map<String, byte[]> resource = queryDefinitionAPI.getBusinessArchive(processDefinitionUUID).getResources();
//        byte[] proc = null;
//        for (String key : resource.keySet()) {
//            if (key.substring(key.length() - 4, key.length()).equals("proc")) {
//                proc = resource.get(key);
//            }
//        }
//        BonitaFormParcer bfb = new BonitaFormParcer(proc);
//        return bfb.getProcess();
//    }

    public byte[] getProcessDiagramm(ProcessDefinitionUUID processDefinitionUUID) throws Exception {
    	logger.debug("getProcessDiagramm");
        Map<String, byte[]> resource = getBusinessArchive(processDefinitionUUID);
        byte[] img = null;
        for (String key : resource.keySet()) { 
        	if(key.contains(processDefinitionUUID.toString()+".png"))
            {
        		return resource.get(key);
            }
        }
        return null;
    }
    
    public Map<String, byte[]> getBusinessArchive(final ProcessDefinitionUUID processDefinitionUUID) throws Exception {
    	return CacheUtil.getOrCache(BUSINESS_ARCHIVE, processDefinitionUUID, new ICacheDelegate<Map<String, byte[]>>() {
			@Override
			public Map<String, byte[]> execute() throws Exception {
				initContext();
		        return getQueryDefinitionAPI().getBusinessArchive(processDefinitionUUID).getResources();
			}    		
		});
    	
        
    }
    public byte[] getBusinessArchiveFile(ProcessDefinitionUUID uuid) throws Exception {
    	logger.debug("getBusinessArchiveFile");
        initContext();
    	BusinessArchive ba= getQueryDefinitionAPI().getBusinessArchive(uuid);
    	File file=new File(ba.getProcessDefinition().getName()+".bar");
    	byte[] barContent = Misc.generateJar(ba.getResources());
    	return barContent;
	}
   
    public void stopExecution(ProcessInstanceUUID piUUID, String stepName) throws Exception {
    	logger.debug("stopExecution");
        initContext();
        getRepairAPI().stopExecution(piUUID, stepName);
    }

    public ActivityInstanceUUID startExecution(ProcessInstanceUUID piUUID, String stepName) throws Exception {
    	logger.debug("startExecution");
        initContext();
        return getRepairAPI().startExecution(piUUID, stepName);
    }

    public ActivityInstanceUUID reStartExecution(ProcessInstanceUUID piUUID, String stepName) throws Exception {
    	logger.debug("reStartExecution");
        initContext();
        getRepairAPI().stopExecution(piUUID, stepName);
        return getRepairAPI().startExecution(piUUID, stepName);
    }

    public LightProcessDefinition setProcessCategories(ProcessDefinitionUUID pduuid, Set<String> set) throws Exception {
    	logger.debug("setProcessCategories");
        initContext();
        return getWebAPI().setProcessCategories(pduuid, set);
    }

    public void deleteCategories(Set<String> set) throws Exception {
    	logger.debug("deleteCategories");
        initContext();
        getWebAPI().deleteCategories(set);
    }

    public void addCategory(String string, String string1, String string2, String string3) throws Exception {
    	logger.debug("addCategory");
        initContext();
        getWebAPI().addCategory(string, string1, string2, string3);
    }

    public Set<Category> getAllCategories() throws Exception {
    	logger.debug("getAllCategories");
        initContext();
        return getWebAPI().getAllCategories();
    }

    public Object evaluateGroovyExpression(String expression, ActivityInstance ai, boolean propagate) throws InstanceNotFoundException, GroovyException, Exception {
    	logger.debug("evaluateGroovyExpression");
        initContext();
        return getRuntimeAPI().evaluateGroovyExpression(expression, ai.getUUID(), false, propagate);

    }

    public Object evaluateExpression(String expression, ActivityInstance ai, boolean propagate) throws InstanceNotFoundException, GroovyException, Exception {
    	logger.debug("evaluateExpression");
        if (expression != null && GroovyExpression.isGroovyExpression(expression)) {
            initContext();
            return getRuntimeAPI().evaluateGroovyExpression(expression, ai.getUUID(), false, propagate);
        } else {
            return expression;
        }
    }

    public Object evaluateExpression(String expression, ProcessDefinitionUUID pduuid) throws InstanceNotFoundException, GroovyException, Exception {
    	logger.debug("evaluateExpression");
        if (expression != null && GroovyExpression.isGroovyExpression(expression)) {
            initContext();
            return getRuntimeAPI().evaluateGroovyExpression(expression, pduuid);
        } else {
            return expression;
        }
    }

    public Object evaluateGroovyExpression(String expression, ProcessDefinitionUUID pduuid) throws InstanceNotFoundException, GroovyException, Exception {
    	logger.debug("evaluateGroovyExpression");
        initContext();
        return getRuntimeAPI().evaluateGroovyExpression(expression, pduuid);
    }

    public Object evaluateGroovyExpression(String script, ActivityInstanceUUID activityUUID, Map<String, Object> context, boolean useActivityScope, boolean propagate) throws Exception {
    	initContext();
    	 logger.debug("evaluateGroovyExpressions");
         if (StringUtils.isNullOrEmpty(script)==false && GroovyExpression.isGroovyExpression(script)) {
    		   Object result=getRuntimeAPI().evaluateGroovyExpression(script, activityUUID, context, useActivityScope, propagate);
               return result;	           
        } else {
            return null;
        }
    }    
    
     public Map<String, Object> evaluateGroovyExpressions(Map<String, String> expressions,
            ActivityInstanceUUID activityUUID, Map<String, Object> context, boolean useActivityScope, boolean propagate)
            throws InstanceNotFoundException, ActivityNotFoundException, GroovyException {
    	 logger.debug("evaluateGroovyExpressions");
         if (!expressions.isEmpty()) {        	
            return getRuntimeAPI().evaluateGroovyExpressions(expressions, activityUUID, context, useActivityScope, propagate);
        } else {
            return null;
        }
    }

     public Object evaluateGroovyExpression(String script, ProcessDefinitionUUID processDefinitionUUID, Map<String, Object> context, boolean useInitialVariableValues) throws Exception {
    	 initContext();
       	 logger.debug("evaluateGroovyExpressions");
            if (StringUtils.isNullOrEmpty(script)==false && GroovyExpression.isGroovyExpression(script)) {
                     return getRuntimeAPI().evaluateGroovyExpression(script, processDefinitionUUID, context);
            }
            return null;
	}
     
    public Map<String, Object> evaluateGroovyExpressions(Map<String, String> expressions, ProcessDefinitionUUID processDefinitionUUID, Map<String, Object> context, boolean useInitialVariableValues)
            throws InstanceNotFoundException, ProcessNotFoundException, GroovyException {
    	logger.debug("evaluateGroovyExpressions");
    	return getRuntimeAPI().evaluateGroovyExpressions(expressions, processDefinitionUUID, context);
    }

    public void cancelProcessInstance(ProcessInstanceUUID piuuid) throws Exception {
    	logger.debug("cancelProcessInstance");
        initContext();
        getRuntimeAPI().cancelProcessInstance(piuuid);
    }

    public void addComment(ProcessInstanceUUID piuuid, String message, String userId) throws InstanceNotFoundException, Exception {
    	logger.debug("addComment");
        initContext();
        getRuntimeAPI().addComment(piuuid, message, userId);
    }

    public void addComment(ActivityInstanceUUID aiuuid, String message, String userId) throws InstanceNotFoundException, Exception {
    	logger.debug("addComment");
        initContext();
        getRuntimeAPI().addComment(aiuuid, message, userId);
    }

    public List<Comment> getCommentFeed(ProcessInstanceUUID piuuid) throws InstanceNotFoundException, Exception {
    	logger.debug("getCommentFeed");
        initContext();
        return getQueryRuntimeAPI().getCommentFeed(piuuid);
    }

    public ProcessInstance getProcessInstance(ProcessInstanceUUID piuuid) throws Exception {
    	logger.debug("getProcessInstance");
        initContext();
        return getQueryRuntimeAPI().getProcessInstance(piuuid);
    }

    public List<User> getAllUsers() throws Exception {
    	logger.debug("getAllUsers");
        initContext();
        return getIdentityAPI().getAllUsers();
    }

    public List<Role> getAllRoles() throws Exception {
    	logger.debug("getAllRoles");
        initContext();
        return getIdentityAPI().getAllRoles();
    }
    
    public Role findRoleByName(String name)throws Exception{
    	logger.debug("findRoleByName");
    	for(Role role:getAllRoles())
    	{
    		if(role.getName().equalsIgnoreCase(name))
    			return role;
    	}
    	return null;
    }
    
    public Group findGroupByName(String name)throws Exception{
    	logger.debug("findGroupByName");
    	for(Group g:getAllGroups()){
    		if(g.getName().equalsIgnoreCase(name))
    			return g;
    	}
    	return null;
    }

    public List<Group> getAllGroups() throws Exception {
    	logger.debug("getAllGroups");
        initContext();
        return getIdentityAPI().getAllGroups();
    }

    public List<ProfileMetadata> getAllProfileMetadata() throws Exception {
    	logger.debug("getAllProfileMetadata");
        initContext();
        return getIdentityAPI().getAllProfileMetadata();
    }

    public User addUser(String username, String password, String firstName, String lastName, String title, String jobTitle, String managerUserUUID, Map<String, String> profileMetadata) throws Exception {
    	logger.debug("addUser");
        initContext();
        return getIdentityAPI().addUser(username, password, firstName, lastName, title, jobTitle, managerUserUUID, profileMetadata);
    }

    public void removeUserByUUID(String userUUID) throws Exception {
    	logger.debug("removeUserByUUID");
        initContext();
        getIdentityAPI().removeUserByUUID(userUUID);
    }

    public ProfileMetadata addProfileMetadata(String name, String label) throws Exception {
    	logger.debug("addProfileMetadata");
        initContext();
        return getIdentityAPI().addProfileMetadata(name, label);
    }

    public ProfileMetadata addProfileMetadata(String name) throws Exception {
    	logger.debug("addProfileMetadata");
        initContext();
        return getIdentityAPI().addProfileMetadata(name);
    }

    public void removeProfileMetadataByUUID(String profileMetadataUUID) throws Exception {
    	logger.debug("removeProfileMetadataByUUID");
        initContext();
        getIdentityAPI().removeProfileMetadataByUUID(profileMetadataUUID);
    }

    public Role addRole(String name, String label, String description) throws Exception {
    	logger.debug("addRole");
        initContext();
        return getIdentityAPI().addRole(name, label, description);
    }

    public void removeRoleByUUID(String roleUUID) throws Exception {
    	logger.debug("removeRoleByUUID");
        initContext();
        getIdentityAPI().removeRoleByUUID(roleUUID);
    }

    public Role updateRoleByUUID(String roleUUID, String name, String label, String description) throws Exception {
    	logger.debug("updateRoleByUUID");
        initContext();
        return getIdentityAPI().updateRoleByUUID(roleUUID, name, label, description);
    }

    public ProfileMetadata updateProfileMetadataByUUID(String profileMetadataUUID, String name, String label) throws Exception {
    	logger.debug("updateProfileMetadataByUUID");
        initContext();
        return getIdentityAPI().updateProfileMetadataByUUID(profileMetadataUUID, name, label);
    }

    public Group addGroup(String name, String label, String description, String parentGroupUUID) throws Exception {
    	logger.debug("addGroup");
        initContext();
        return getIdentityAPI().addGroup(name, label, description, parentGroupUUID);
    }

    public Group addGroup(String name, String parentGroupUUID) throws Exception {
    	logger.debug("addGroup");
        initContext();
        return getIdentityAPI().addGroup(name, parentGroupUUID);
    }

    public Group updateGroupByUUID(String groupUUID, String name, String label, String description, String parentGroupUUID) throws Exception {
    	logger.debug("updateGroupByUUID");
        initContext();
        return getIdentityAPI().updateGroupByUUID(groupUUID, name, label, description, parentGroupUUID);
    }

    public void removeGroupByUUID(String groupUUID) throws Exception {
    	logger.debug("removeGroupByUUID");
        initContext();
        getIdentityAPI().removeGroupByUUID(groupUUID);
    }

    public void updateUserProfessionalContactInfo(String userUUID, String email, String phoneNumber, String mobileNumber, String faxNumber, String building, String room, String address, String zipCode, String city, String state, String country, String website) throws Exception {
    	logger.debug("updateUserProfessionalContactInfo");
        initContext();
        getIdentityAPI().updateUserProfessionalContactInfo(userUUID, email, phoneNumber, mobileNumber, faxNumber, building, room, address, zipCode, city, state, country, website);
    }

    public void updateUserPersonalContactInfo(String userUUID, String email, String phoneNumber, String mobileNumber, String faxNumber, String building, String room, String address, String zipCode, String city, String state, String country, String website) throws Exception {
    	logger.debug("updateUserPersonalContactInfo");
        initContext();
        getIdentityAPI().updateUserPersonalContactInfo(userUUID, email, phoneNumber, mobileNumber, faxNumber, building, room, address, zipCode, city, state, country, website);
    }

    public User updateUserByUUID(String userUUID, String username, String firstName, String lastName, String title, String jobTitle, String managerUserUUID, Map<String, String> profileMetadata) throws Exception {
    	logger.debug("updateUserByUUID");
        initContext();
        return getIdentityAPI().updateUserByUUID(userUUID, username, firstName, lastName, title, jobTitle, managerUserUUID, profileMetadata);
    }

    public User updateUserPassword(String userUUID, String password) throws Exception {
    	logger.debug("updateUserPassword");
        initContext();
        return getIdentityAPI().updateUserPassword(userUUID, password);
    }
    
    public void updateUserMetadata(User user, String metaKey, String metaValue) throws Exception{
    	initContext();
    	
    	ProfileMetadata foundProfileMetadata=null;
    	List<ProfileMetadata> profileMetadatas = getAllProfileMetadata();
    	for (ProfileMetadata profileMetadata : profileMetadatas) {
			if(metaKey.equals(profileMetadata.getName()))
				{
					foundProfileMetadata=profileMetadata;
					break;
				}
		}
    	if(foundProfileMetadata==null){
    		addProfileMetadata(metaKey, metaKey);
    	}
    	
    	
    	Map<String, String> metadata=new Hashtable<String, String>();
    	 for (ProfileMetadata profileMetadata1 : user.getMetadata().keySet()) {
             if (profileMetadata1.getName().equals(metaKey)) {
                 metadata.put(metaKey, metaValue);
             }
             else
             {
            	 metadata.put(profileMetadata1.getName(), user.getMetadata().get(profileMetadata1.getName()));
             }
         }
    	if(!metadata.containsKey(metaKey)){
    		metadata.put(metaKey, metaValue);
    	}
		getIdentityAPI().updateUserByUUID(user.getUUID(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getTitle(), user.getJobTitle(), user.getManagerUUID(), metadata);
    }

    public void setUserMemberships(String userUUID, Collection<String> membershipUUIDs) throws Exception {
    	logger.debug("setUserMemberships");
        initContext();
        getIdentityAPI().setUserMemberships(userUUID, membershipUUIDs);
    }

    public void addMembershipToUser(String userUUID, String membershipUUID) throws Exception {
    	logger.debug("addMembershipToUser");
        initContext();
        getIdentityAPI().addMembershipToUser(userUUID, membershipUUID);
    }

    public void addMembershipsToUser(String userUUID, Collection<String> membershipUUIDs) throws Exception {
    	logger.debug("addMembershipsToUser");
        initContext();
        getIdentityAPI().addMembershipsToUser(userUUID, membershipUUIDs);
    }

    public Membership getMembershipByUUID(String membershipUUID) throws Exception {
    	logger.debug("getMembershipByUUID");
        initContext();
        return getIdentityAPI().getMembershipByUUID(membershipUUID);
    }

    public void removeMembershipFromUser(String userUUID, String membershipUUID) throws Exception {
    	logger.debug("removeMembershipFromUser");
        initContext();
        getIdentityAPI().removeMembershipFromUser(userUUID, membershipUUID);
    }

    public void removeMembershipsFromUser(String userUUID, Collection<String> membershipUUIDs) throws Exception {
    	logger.debug("removeMembershipsFromUser");
        initContext();
        getIdentityAPI().removeMembershipsFromUser(userUUID, membershipUUIDs);
    }

    public Membership getMembershipForRoleAndGroup(String roleUUID, String groupUUID) throws Exception {
    	logger.debug("getMembershipForRoleAndGroup");
        initContext();
        return getIdentityAPI().getMembershipForRoleAndGroup(roleUUID, groupUUID);
    }
    
    public String getUserMetadataValue(User user, String metadataName) {
    	 
         for (ProfileMetadata profileMetadata : user.getMetadata().keySet()) {
             if (profileMetadata.getName().equals(metadataName)) {
                 return user.getMetadata().get(profileMetadata);
             }
         }
         return null;
    }

    public User findUserByUserName(String userName) throws Exception {
    	logger.debug("findUserByUserName:"+ userName);
        initContext();
        return getIdentityAPI().findUserByUserName(userName);
    }

    public boolean checkUserCredentials(String username, String password) throws Exception {
    	
    	//authUserWithJaas(username, password);
    	logger.debug("checkUserCredentials");
    	initContext();
    	if(username==USER_GUEST){
    		if(findUserByUserName(USER_GUEST) == null)
    			addUser(username, "guest", "", "", "", "", null, null);
    		return true;
    	}        
        return getManagementAPI().checkUserCredentials(username, password);
    }

    public Rule findRule(String ruleName) throws Exception {
    	logger.debug("findRule");
        initContext();
        for (Rule rule : getManagementAPI().getAllRules()) {
            if (rule.getName().equals(ruleName)) {
                return rule;
            }
        }
        return null;
    }

    public void applyRuleToEntities(final String ruleUUID, final Collection<String> userUUIDs, final Collection<String> roleUUIDs, final Collection<String> groupUUIDs, final Collection<String> membershipUUIDs, final Collection<String> entityIDs) throws Exception {
    	logger.debug("applyRuleToEntities");
        initContext();
        getManagementAPI().applyRuleToEntities(ruleUUID, userUUIDs, roleUUIDs, groupUUIDs, membershipUUIDs, entityIDs);
    }

    public void removeRuleFromEntities(final String ruleUUID, final Collection<String> userUUIDs, final Collection<String> roleUUIDs, final Collection<String> groupUUIDs, final Collection<String> membershipUUIDs, final Collection<String> entityIDs) throws Exception {
    	logger.debug("removeRuleFromEntities");
        initContext();
        getManagementAPI().removeRuleFromEntities(ruleUUID, userUUIDs, roleUUIDs, groupUUIDs, membershipUUIDs, entityIDs);
    }

    public void addMetaData(String key, String value) throws Exception {
    	logger.debug("addMetaData");
        initContext();
        getManagementAPI().addMetaData(key, value);
    }

    public String  getMetaData(String key) throws Exception {
    	logger.debug("getMetaData");
        initContext();
        return getManagementAPI().getMetaData(key);
    }

    public String getUserMetadata(String metadataName) throws Exception {
    	logger.debug("getUserMetadata");
        initContext();
        User user = getIdentityAPI().findUserByUserName(currentUserUID);
        for (ProfileMetadata profileMetadata : user.getMetadata().keySet()) {
            if (profileMetadata.getName().equals(metadataName)) {
                return user.getMetadata().get(profileMetadata);
            }
        }
        return null;
    }
    
    public <T extends Object> T execute(Command<T> cmnd) throws Exception{
    	logger.debug("execute");
        initContext();
        return getCommandAPI().execute(cmnd);
    }

	public QueryDefinitionAPI getQueryDefinitionAPI() {
		return AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
	}

	public RuntimeAPI getRuntimeAPI() {
		return AccessorUtil.getRuntimeAPI();
	}

	public QueryRuntimeAPI getQueryRuntimeAPI() {
		return AccessorUtil.getQueryRuntimeAPI();
	}

	public ManagementAPI getManagementAPI() {
		return AccessorUtil.getManagementAPI();
	}

	public RepairAPI getRepairAPI() {
		return AccessorUtil.getRepairAPI();
	}

	public WebAPI getWebAPI() {
		return AccessorUtil.getWebAPI();
	}

	public IdentityAPI getIdentityAPI() {
		return AccessorUtil.getIdentityAPI();
	}

	public BAMAPI getBamAPI() {
		return AccessorUtil.getBAMAPI();
	}

	public CommandAPI getCommandAPI() {
		return AccessorUtil.getCommandAPI();
	}

	public User authUser(final User authUser) throws Exception {
		return CacheUtil.getOrCache("user_auth", authUser.getUsername(), new ICacheDelegate<User>(){
			public User execute() throws Exception {				
			
				initContext();
				IdentityAPI identityAPI = getIdentityAPI();
				User user =null;
				try{
					user = identityAPI.findUserByUserName(authUser.getUsername());
					return user;
				}
				catch(UserNotFoundException ex){
					
					String password=authUser.getPassword();			
					if(StringUtils.isNullOrEmpty(password))
						password=RandomStringUtils.randomAlphabetic(8);
					
					user=identityAPI.addUser(authUser.getUsername(), password, authUser.getFirstName(), authUser.getLastName(), "", null, null, null);
					
					List<Group> groups= identityAPI.getAllGroups();
					Group group=null;
					for (Group group1 : groups) {
						if(group1.getName().equalsIgnoreCase(identityAPI.DEFAULT_GROUP_NAME));
						{
							group=group1;
							break;
						}
					}
					Role role = identityAPI.findRoleByName(identityAPI.USER_ROLE_NAME);
					if(role!=null && group!=null)
					{
						Membership membership = identityAPI.getMembershipForRoleAndGroup(role.getUUID(), group.getUUID());
						if(membership!=null){
							List<String> membershipUUIDs=new ArrayList<String>();
							membershipUUIDs.add(membership.getUUID());
							identityAPI.addMembershipsToUser(user.getUUID(), membershipUUIDs);
						}
					}					
				}		
				return user;
			}
		});
	}



	public void updateUserGroups(List<ProcessParticipant> processRoles) throws Exception {
		//TODO: Vaja lisada juurde grupi ja initiaatori importimise kontrollid.
		if(processRoles==null)return;
		initContext();
		List<Role> roles = getIdentityAPI().getAllRoles();
		Set<String> existingRoles = new HashSet<String>();
		for(Role role:roles){
			existingRoles.add(role.getLabel());
		}
		for (ProcessParticipant role:processRoles) {
			if(existingRoles.add(role.getName())){//if no dublicate detected
				if("Initiator".equals(role)==false){
					getIdentityAPI().addRole(role.getName(), role.getLabel(), "AUTO IMPORTED");	
				}
			}
		}
	}
	
	 public byte[] getLargeDataRepositoryAttachment(final ProcessDefinitionUUID processDefinitionUUID, final String attachmentName){
		 try {
			 byte[] result=execute(new Command<byte[]>() {

				@Override
				public byte[] execute(Environment environment) throws Exception {
					
					List<String> attachmentCategories = Misc.getAttachmentCategories(processDefinitionUUID);
					final LargeDataRepository ldr = EnvTool.getLargeDataRepository();
					Set<String> keys = ldr.getKeys(attachmentCategories);
					 
					if(keys!=null && keys.contains(attachmentName)) {
						byte[] data = ldr.getData(byte[].class,attachmentCategories, attachmentName);
						return data;
					}
				   	return null;
				}
			});
			 return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
   	    	   
   }
	
}
