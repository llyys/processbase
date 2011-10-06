package org.processbase.ui.bpm.generator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.h2.util.StringUtils;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.util.GroovyException;
import org.ow2.bonita.util.GroovyUtil;
import org.processbase.ui.bpm.generator.BarResource;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.Activities;
import org.processbase.ui.core.bonita.forms.FieldValue;
import org.processbase.ui.core.bonita.forms.FormsDefinition;
import org.processbase.ui.core.bonita.forms.PageFlow;
import org.processbase.ui.core.bonita.forms.Widget;
import org.processbase.ui.core.bonita.forms.WidgetType;
import org.processbase.ui.core.bonita.forms.Widgets;
import org.processbase.ui.core.bonita.forms.Activities.Activity;
import org.processbase.ui.core.bonita.forms.FormsDefinition.Process;
import org.processbase.ui.core.bonita.forms.PageFlow.Pages.Page;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.PbWindow;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.VerticalLayout;

public class ProcessManager {
	
	private Object xmlProcess;
	private BarResource barResource;
	protected PbPanel parent=null;
	protected ProcessDefinitionUUID processDefinitionUUID;
	private Set<DataFieldDefinition> processDataFields;
	protected Map<String, Object> processVariables = new HashMap<String, Object>();
	protected BPMModule bpmModule=ProcessbaseApplication.getCurrent().getBpmModule();
	private Map<String, Object> groovyContext;
	
	//rendered from forms.xml file
	private FormsDefinition formsDefinition;
	
	private ProcessInstanceUUID processInstanceUUID;
	private TaskInstance taskInstance;
	private HashMap<String, Activities.Activity> activityDefinitions;
	private ActivityInstanceUUID activityInstanceUUID;
	private final LightProcessDefinition processInstance;
	private TaskManager taskManager;
	//private IActivityChangeListener activityChanged=null;
	/**
	 * Open the process
	 * @param process process definition
	 * @param taskInstance if open existing process
	 * @throws Exception
	 */
	public ProcessManager(LightProcessDefinition processInstance, TaskInstance taskInstance) throws Exception{
		this.processInstance = processInstance;
		processDefinitionUUID= this.processInstance.getUUID();
		
		barResource = BarResource.getBarResource(processDefinitionUUID);
        xmlProcess = barResource.getXmlProcessDefinition(processInstance.getName());
        formsDefinition = this.barResource.getFormsDefinition();
        
        this.setTaskInstance(taskInstance);		
        initProcessVariables();
        
	}
	
	public ProcessManager(ProcessDefinitionUUID processDefinitionUUID, ActivityInstanceUUID activityInstanceUUID) throws Exception{
		this.activityInstanceUUID = activityInstanceUUID;
		this.setTaskInstance(taskInstance);
		this.processDefinitionUUID=processDefinitionUUID;
		this.processInstance=bpmModule.getProcessDefinition(processDefinitionUUID);
		
		barResource = BarResource.getBarResource(processDefinitionUUID);
        xmlProcess = barResource.getXmlProcessDefinition(processInstance.getName());
        formsDefinition = this.barResource.getFormsDefinition();
        
        initProcessVariables();
        
	}
	
	
	
	private void initProcessVariables() {
		try {
			
			initGroovyContext();//update groovy context
            if (this.getTaskInstance() != null) { //open existing task
            	processDataFields = getBpmModule().getProcessDataFields(this.getTaskInstance().getProcessDefinitionUUID());
            	processVariables = getBpmModule().getProcessInstanceVariables(this.getTaskInstance().getProcessInstanceUUID());	                
            } else {
            	processDataFields = getBpmModule().getProcessDataFields(processDefinitionUUID);
            }
            activityDefinitions=new HashMap<String, Activities.Activity>();
            
            for (Activities.Activity a : formsDefinition.getProcesses().get(0).getActivities().getActivities()) {
            	activityDefinitions.put(a.getName(), a);					
			}
            initGroovyContext();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }		
	}

	public Map<String, Object> initGroovyContext() {
		if(groovyContext==null){
			groovyContext=new Hashtable<String, Object>();
			//groovyContext.put("process_manager", this);
			ProcessbaseApplication application = ProcessbaseApplication.getCurrent();
			groovyContext.put("application", application);
	//		groovyContext.put("apiAccessor", application.getBpmModule().getAPIAccessor());
			groovyContext.put("loggedUser", application.getUserName());			
		}
		return groovyContext;
	}
	
	public void setParent(PbPanel parent) {
		this.parent = parent;
	}
	
	public PbPanel getParent() {
		return parent;
	}
	
	public Object evalGroovyExpression(String expression) throws GroovyException {
		return GroovyUtil.evaluate(expression, groovyContext, processDefinitionUUID, true);		
	}
	
	protected BPMModule getBpmModule() {
		return bpmModule;
	}
	public void setProcessVariables(Map<String, Object> processVariables) {
		this.processVariables = processVariables;
	}
	
	public Map<String, Object> getProcessVariables() {
		return processVariables;
	}
	
	public void setProcessDataFieldDefinitions(Set<DataFieldDefinition> processDataFields) {
				this.setProcessDataFields(processDataFields);
	}
	
	/**
	 * Start new process
	 * @return
	 * @throws Exception
	 */
	public List<Component> openStartTask() throws Exception
	{
		Process process = formsDefinition.getProcesses().get(0);
		PageFlow pageFlow=process.getPageflow();
		taskManager = new TaskManager(this);
		if(pageFlow!=null)//this is a entry pageflow, start process after clicking next button
		{
			return taskManager.renderPageflow(pageFlow);
		}
		
		//open process first task where is no process entry pageflow, this scenario start's immediatelly a process
		 setProcessInstanceUUID(bpmModule.startNewProcess(processDefinitionUUID));
		 setTaskInstance(bpmModule.nextUserTask(getProcessInstanceUUID(), ProcessbaseApplication.getCurrent().getUserName()));
		 
		 taskManager.setTask(getTaskInstance());
		 
		 String activityName=getTaskInstance().getActivityName();
		 return taskManager.renderPageflow(activityDefinitions.get(activityName).getPageflow());
	}

	/**
	 * Open already started task
	 * @param taskInstance
	 * @return
	 * @throws Exception
	 */
	public List<Component> openTask(TaskInstance taskInstance) throws Exception
	{
		this.taskInstance=taskInstance;
		
		if(!activityDefinitions.containsKey(taskInstance.getActivityLabel()))
			return null;//activity not found quitting
		
		processInstanceUUID= taskInstance.getProcessInstanceUUID();
		Activity activity=activityDefinitions.get(taskInstance.getActivityLabel());
		PageFlow pageFlow=activity.getPageflow();
		
		taskManager = new TaskManager(this);

		//clean and reload all process variables
		
		initProcessVariables();
		
		return taskManager.renderPageflow(pageFlow);
		
	}
	
	public TaskManager getCurrentTaskManager(){
		return this.taskManager;
	}
	public void setProcessDataFields(Set<DataFieldDefinition> processDataFields) {
		this.processDataFields = processDataFields;
	}
	public Set<DataFieldDefinition> getProcessDataFields() {
		return processDataFields;
	}
	
	
	
	public List<Page> LoadPages(String activityName){
		//loaded an xml from /forms/forms.xml file
		org.processbase.ui.core.bonita.forms.FormsDefinition.Process process=barResource.getFormsDefinition().getProcesses().get(0);
		PageFlow pageFlow=process.getPageflow();
		//find first page
		Activity activity=activityDefinitions.get(activityName);
		if(activity==null)
			return null;
		
		if (activity.getPageflow() != null) {
			return activity.getPageflow().getPages().getPages();			
		}
		return null;
	}
	
	public BarResource getBarResource() {
		return barResource;
	}

	public void setTaskInstance(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
		if(taskInstance==null)
			this.activityInstanceUUID=null;
		else
			this.activityInstanceUUID=new ActivityInstanceUUID(taskInstance.getActivityInstanceId());
	}

	public TaskInstance getTaskInstance() {
		return taskInstance;
	}

	public void setProcessInstanceUUID(ProcessInstanceUUID processInstanceUUID) {
		this.processInstanceUUID = processInstanceUUID;
		
	}

	public ProcessInstanceUUID getProcessInstanceUUID() {
		return processInstanceUUID;
	}

	public Map<String, Object> getGroovyContext() {
		return groovyContext;
	}

	public void initializeNewProcess() throws ProcessNotFoundException, VariableNotFoundException, Exception {
		  ProcessInstanceUUID startNewProcess = getBpmModule().startNewProcess(processDefinitionUUID, getProcessVariables());
		  setProcessInstanceUUID(startNewProcess);
		
	}

	public void finishTask(TaskManager manager) {
		try {
		String confirmationMessage = manager.getConfirmationMessage();
		if(!StringUtils.isNullOrEmpty(confirmationMessage))
		{
			getBpmModule().finishTask(taskInstance, true,
					getProcessVariables(), manager.getActivityVariables(),null);//TODO: ATTACHMENTS now it's null
			
			//find next task to execute
			taskInstance = bpmModule.nextUserTask(getProcessInstanceUUID(), ProcessbaseApplication.getCurrent().getUserName());
			if(taskInstance!=null){
				manager.Dispose();
				openTask(taskInstance);				
			}
			else{
				//there is no more steps show info message
				if(window!=null)
				{
					window.showInformation("Process finished or assinged to another user!");
					window.close();
				}
			}
			
		}
		} catch (TaskNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalTaskStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VariableNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private PbWindow window;
	
	public void updateVariableValue(String variable, Object componentValue) {
		processVariables.put(variable, componentValue);		
	}

	public void setWindow(PbWindow window) {
		this.window = window;
	}

	public PbWindow getWindow() {
		return window;
	}
	
}
