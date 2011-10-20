package org.processbase.ui.bpm.generator.view;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.codehaus.groovy.control.CompilationFailedException;
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
import org.ow2.bonita.util.Command;
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
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.core.template.TablePanel;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;

public class ProcessManager extends PbPanel {
	
	private HashMap<String, Activities.Activity> activityDefinitions;
	private ActivityInstanceUUID activityInstanceUUID;
	private BarResource barResource;
	
	Binding groovyBinding = null;
	GroovyShell groovyShell=null;
	
	private IProcessManagerActions actions;
	protected BPMModule bpmModule=ProcessbaseApplication.getCurrent().getBpmModule();

	//rendered from forms.xml file
	private FormsDefinition formsDefinition;
	private Map<String, Object> groovyContext;
	protected PbPanel parent=null;
	private Set<DataFieldDefinition> processDataFields;
	
	private Panel taskPanel = new Panel();
	
	protected ProcessDefinitionUUID processDefinitionUUID;
	
	private LightProcessDefinition processInstance;
	private ProcessInstanceUUID processInstanceUUID;
	protected Map<String, Object> processVariables = new HashMap<String, Object>();
	private TaskInstance taskInstance;
	private TaskManager taskManager;
	private PbWindow window;
	private Object xmlProcess;
	private Process process;
	private String label;
	
	ButtonBar buttons=null;
	
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
		this.setTaskInstance(taskInstance);		
        initManagerVariables();
	}
	
	public ProcessManager(ProcessDefinitionUUID processDefinitionUUID, ActivityInstanceUUID activityInstanceUUID) throws Exception{
		this.activityInstanceUUID = activityInstanceUUID;
		this.processDefinitionUUID=processDefinitionUUID;
		this.setTaskInstance(getBpmModule().getTaskInstance(activityInstanceUUID));
		initManagerVariables();   	
		
	}
	
	
	@Override
	public void initUI() {
		this.setHeight("100%");
		this.setWidth("100%");
		this.addComponent(taskPanel);
		taskPanel.setHeight("100%");
	
		this.setExpandRatio(taskPanel, 1.0f);
		setMargin(false);
        setSpacing(false);
        
        this.setComponentAlignment(taskPanel, Alignment.TOP_CENTER);
        
        try {
        	buttons=new ButtonBar();
	        //buttons.addButton(new com.vaadin.ui.Button("test5"));
	        buttons.setMargin(true, true, false, true);
	        
	        this.addComponent(buttons);
	        buttons.setHeight("50px");
	        buttons.setWidth("100%");
	        
			if(taskInstance==null){			
				openStartTask();			
			}
			else{
				openTask(taskInstance);
			}
		} catch (Exception e) {
			getWindow().getWindow().showNotification(e.getMessage());
			getWindow().close();
			e.printStackTrace();
			throw new RuntimeException("Problem on process start", e);
			
		}
        this.setSizeFull(); 
	}
	
	Label taskLabel;
	Label taskDescription;
	private boolean isCanceable=true;
	
	public void initManagerVariables() throws Exception {
		barResource = BarResource.getBarResource(processDefinitionUUID);
		processInstance=bpmModule.getProcessDefinition(processDefinitionUUID);
		xmlProcess = barResource.getXmlProcessDefinition(processInstance.getName());
		formsDefinition = this.barResource.getFormsDefinition();
		process = formsDefinition.getProcesses().get(0);
		activityDefinitions=new HashMap<String, Activities.Activity>();
		
        for (Activities.Activity a : formsDefinition.getProcesses().get(0).getActivities().getActivities()) {
         	activityDefinitions.put(a.getName(), a);					
		}  
        
	}
	
	public Object evalGroovyExpression(String expression) throws Exception {
			try{
				return GroovyUtil.evaluate(expression, groovyBinding);
			}catch(Exception ex){
			if(taskInstance==null)
				return getBpmModule().evaluateGroovyExpression(expression, processDefinitionUUID, groovyContext, false);
			else 
				return getBpmModule().evaluateGroovyExpression(expression, taskInstance.getUUID(), groovyContext, false, false);
			}
		//return GroovyUtil.evaluate(expression, groovyContext, processDefinitionUUID, true);		
	}
	
	public BarResource getBarResource() {
		return barResource;
	}
	
	protected BPMModule getBpmModule() {
		return bpmModule;
	}
	
	public TaskManager getCurrentTaskManager(){
		return this.taskManager;
	}
	public Map<String, Object> getGroovyContext() {
		return groovyContext;
	}
	
	public PbPanel getParent() {
		return parent;
	}
	
	public Set<DataFieldDefinition> getProcessDataFields() {
		return processDataFields;
	}
	
	public ProcessInstanceUUID getProcessInstanceUUID() {
		return processInstanceUUID;
	}

	public Map<String, Object> getProcessVariables() {
		return processVariables;
	}
	
	public TaskInstance getTaskInstance() {
		return taskInstance;
	}
	public PbWindow getWindow() {
		return window;
	}
	public Map<String, Object> initGroovyContext() {
			groovyContext=new HashMap<String, Object>();
			//groovyContext.put("process_manager", this);
			ProcessbaseApplication application = ProcessbaseApplication.getCurrent();
			groovyContext.put("parent", this);
			groovyContext.put("taskManager", this.taskManager);
			groovyContext.put("appication", application);
	//		groovyContext.put("apiAccessor", application.getBpmModule().getAPIAccessor());
			if(application.getApplicationType()==ProcessbaseApplication.LIFERAY_PORTAL)
				
				groovyContext.put("loggedUser", application.getSessionAttribute("LiferayUser"));
			else
				groovyContext.put("loggedUser", application.getUserName());
		
			for (Entry<String, Object> pvar : processVariables.entrySet()) {
				Object value = pvar.getValue();
				groovyContext.put(pvar.getKey(), value);
			}		
	
		groovyBinding=new Binding(groovyContext);
		groovyShell = new GroovyShell(groovyBinding);
		
		if(modifiedProcessVariables!=null)
			modifiedProcessVariables.clear();
		
		return groovyContext;
	}
		
	
	public void initializeNewProcess() throws ProcessNotFoundException, VariableNotFoundException, Exception {
		  ProcessInstanceUUID startNewProcess = getBpmModule().startNewProcess(processDefinitionUUID, getProcessVariables());
		  setProcessInstanceUUID(startNewProcess);
	}
	
	void initProcessVariables() {
		try {
			
            if (this.getTaskInstance() != null) { //open existing task
            	processDataFields = getBpmModule().getProcessDataFields(this.getTaskInstance().getProcessDefinitionUUID());
            	processVariables = getBpmModule().getProcessInstanceVariables(this.getTaskInstance().getProcessInstanceUUID());	                
            } else {
            	processDataFields = getBpmModule().getProcessDataFields(processDefinitionUUID);
            }
            
            initGroovyContext();
            label=process.getProcessLabel();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }		
	}

	public List<Page> LoadPages(String activityName){
		//loaded an xml from /forms/forms.xml file
		org.processbase.ui.core.bonita.forms.FormsDefinition.Process process=barResource.getFormsDefinition().getProcesses().get(0);
		PageFlow pageFlow=process.getPageflow();
		initProcessVariables();
		Activity activity=activityDefinitions.get(activityName);
		if(activity==null)
			return null;
		
		if (activity.getPageflow() != null) {
			return activity.getPageflow().getPages().getPages();			
		}
		return null;
	}
	
	public String getLabel(){
		return process.getProcessLabel();
	}

	/**
	 * Start new process
	 * @return
	 * @throws Exception
	 */
	public void openStartTask() throws Exception
	{
		
		PageFlow pageFlow=process.getPageflow();
		taskManager = new TaskManager(this);
		
		if(pageFlow!=null && pageFlow.getPages()!=null)//this is a entry pageflow, start process after clicking next button
		{			
			updateView(taskManager.renderPageflow(pageFlow));
			return;
		}
		
		//open process first task where is no process entry pageflow, this scenario start's immediatelly a process
		 setProcessInstanceUUID(bpmModule.startNewProcess(processDefinitionUUID));
		 setTaskInstance(bpmModule.nextUserTask(getProcessInstanceUUID(), ProcessbaseApplication.getCurrent().getUserName()));
		 
		 String activityName=getTaskInstance().getActivityName();
		 updateView(taskManager.renderPageflow(activityDefinitions.get(activityName).getPageflow()));
		 
	}
	
	/**
	 * Open already started task
	 * @param taskInstance
	 * @return
	 * @throws Exception
	 */
	public void openTask(TaskInstance taskInstance) throws Exception
	{
		this.taskInstance=taskInstance;
		
		if(!activityDefinitions.containsKey(taskInstance.getActivityName()))
			return;//activity not found quitting
		
		processInstanceUUID= taskInstance.getProcessInstanceUUID();
		Activity activity=activityDefinitions.get(taskInstance.getActivityName());
		PageFlow pageFlow=activity.getPageflow();
		
		taskManager = new TaskManager(this);
		taskManager.setActivity(activity);
		//clean and reload all process variables
		
		//initProcessVariables();
		 
		updateView(taskManager.renderPageflow(pageFlow));
	}


	private void updateView(List<Component> components) throws Exception {
		
		
		
		this.buttons.removeAllComponents();
		this.taskPanel.removeAllComponents();
		if(components.size()==1){
			Component c = components.get(0);			
			this.taskPanel.addComponent(c);
		}
		else {
			TabSheet accordionLayout=new TabSheet();
			accordionLayout.setSizeFull();
			
			for (Component component : components) {
				String caption = component.getCaption();
				component.setCaption(null);
				accordionLayout.addTab(component, caption, null);
				
			}
			this.taskPanel.addComponent(accordionLayout);
		}
		for (Entry<String, TaskField> component : this.taskManager.getFields().entrySet()) {
			TaskField tf=component.getValue();
			if(tf.getWidget().getType()== WidgetType.BUTTON_SUBMIT){
				Button button = (Button) tf.getComponent();
				
				this.buttons.addButton(button);
				this.buttons.setComponentAlignment(button, Alignment.MIDDLE_LEFT);
				
			}
		}
		
		Label empty=new Label("");
		this.buttons.addComponent(empty);
		this.buttons.setComponentAlignment(empty, Alignment.MIDDLE_RIGHT);
		buttons.setExpandRatio(empty, 1.0f);
		if(taskInstance==null || (taskInstance.isTaskAssigned() && isCanceable)){
			
			
			Button buttonKatkesta = new Button("Katkesta");
			buttonKatkesta.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if(taskInstance!=null)
							getBpmModule().cancelProcessInstance(getProcessInstanceUUID());
						getWindow().close();
					} catch (Exception e) {
						getWindow().showError("Unable to cancel the process");
						throw new RuntimeException(e);
					}
				}
			});
			this.buttons.addButton(buttonKatkesta);
			this.buttons.setComponentAlignment(buttonKatkesta, Alignment.MIDDLE_RIGHT);
			
			if(taskInstance!=null && taskInstance.isTaskAssigned()==false)
				isCanceable=false;
			
		}
		//buttonKatkesta.setWidth("100%");
		
		Button buttonClose = new Button("Sulge");
		buttonClose.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				getWindow().close();
			}
		});
		
		this.buttons.addButton(buttonClose);
		this.buttons.setComponentAlignment(buttonClose, Alignment.MIDDLE_RIGHT);
		//buttonClose.setWidth("100%");
	}
	
	public void setParent(PbPanel parent) {
		this.parent = parent;
	}

	public void setProcessDataFieldDefinitions(Set<DataFieldDefinition> processDataFields) {
				this.setProcessDataFields(processDataFields);
	}

	public void setProcessDataFields(Set<DataFieldDefinition> processDataFields) {
		this.processDataFields = processDataFields;
	}

	public void setProcessInstanceUUID(ProcessInstanceUUID processInstanceUUID) {
		this.processInstanceUUID = processInstanceUUID;
		
	}
	public void setProcessVariables(Map<String, Object> processVariables) {
		this.processVariables = processVariables;
	}
	
	public void setTaskInstance(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
		if(taskInstance==null)
			this.activityInstanceUUID=null;
		else
			this.activityInstanceUUID=new ActivityInstanceUUID(taskInstance.getActivityInstanceId());
	}
	
	public void finishTask(TaskManager manager) {
		try {
		String confirmationMessage = manager.getConfirmationMessage();
		if(!StringUtils.isNullOrEmpty(confirmationMessage))
		{
			getWindow().showInformation(confirmationMessage);
		}
		if(taskInstance!=null){
			
			//Speed improvement:Filter out only modified variables, because bonita does un-nessesery calculation on all fields.
			Map<String, Object> mpv=new HashMap<String, Object>();
			Map<String, Object> mav=new HashMap<String, Object>();
			if(modifiedProcessVariables!=null){
				for(String variable:modifiedProcessVariables){
					if(getProcessVariables().containsKey(variable)){
						mpv.put(variable, getProcessVariables().get(variable));
					}
					if(manager.getActivityVariables().containsKey(variable)){
						mav.put(variable, manager.getActivityVariables().get(variable));
					}
				}
			}
			getBpmModule().finishTask(taskInstance, true, mpv, mav, null);//TODO: ATTACHMENTS now it's null
			if(actions!=null)
				actions.onTaskFinished(taskInstance);
		}
		//find next task to execute
		TaskInstance newTask = bpmModule.nextUserTask(getProcessInstanceUUID(), ProcessbaseApplication.getCurrent().getUserName()); 
		
		if(newTask!=null){
			manager.Dispose();
			if(newTask.getProcessDefinitionUUID().equals(this.processDefinitionUUID)==false){
				//this is new (sub)process
				if(this.actions !=null)
					this.actions.onStartSubProcess(newTask.getProcessDefinitionUUID(), newTask.getUUID());
				return;
			}
			openTask(newTask);
		}
		else {
			//there is no more steps show info message
			if(actions!=null)
				actions.onFinishProcess(processDefinitionUUID);
			return;			
		}			
		
		} catch (TaskNotFoundException e) {
			window.showError(e.getMessage());
			throw new RuntimeException(e);
		} catch (IllegalTaskStateException e) {
			window.showError(e.getMessage());
			throw new RuntimeException(e);
		} catch (InstanceNotFoundException e) {
			window.showError(e.getMessage());
			throw new RuntimeException(e);
		} catch (VariableNotFoundException e) {
			window.showError(e.getMessage());
			throw new RuntimeException(e);
		} catch (Exception e) {
			window.showError(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	public void finishTask() throws Exception{
		if(taskManager!=null)
			taskManager.onFinishTask();
	}
	
	
	public void setWindow(PbWindow window) {
		this.window = window;
	}

	Set<String> modifiedProcessVariables=null;
	public void registerModifiedVariable(String variable){
		if(modifiedProcessVariables==null)
			modifiedProcessVariables=new HashSet<String>();
		modifiedProcessVariables.add(variable);
		
	}
	public void updateVariableValue(String variable, Object componentValue) {
		
		processVariables.put(variable, componentValue);		
		groovyContext.put(variable, componentValue);
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setActions(IProcessManagerActions actions) {
		this.actions = actions;
	}

	public void reloadTask() {
		try { 
			TaskInstance nextTask= bpmModule.nextUserTask(getProcessInstanceUUID(), ProcessbaseApplication.getCurrent().getUserName());
			if(nextTask!=null)
			{
				openTask(nextTask);
				return;
			}
			
			//if sub task is an ending task end the current process
			//there is no more steps show info message
			if(actions!=null)
				actions.onFinishProcess(processDefinitionUUID);
			return;			
					
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

	public TaskManager getTaskManager() {
		return taskManager;
	}

	
	
}
