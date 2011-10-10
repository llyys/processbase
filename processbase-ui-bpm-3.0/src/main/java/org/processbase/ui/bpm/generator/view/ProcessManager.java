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
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.PbWindow;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class ProcessManager extends PbPanel{
	
	private HashMap<String, Activities.Activity> activityDefinitions;
	private ActivityInstanceUUID activityInstanceUUID;
	private BarResource barResource;
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
		this.setTaskInstance(null);
		initManagerVariables();   	
		
	}
	
	
	@Override
	public void initUI() {
		/*taskLabel=new Label("test");
        taskLabel.setVisible(true);
        taskLabel.setSizeFull();
        
        this.addComponent(taskLabel);
        
        taskDescription=new Label("test2");
        taskDescription.setVisible(true);
        this.addComponent(taskDescription);
        */
        //this.panel.setScrollable(true);
		
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
			
			
	        //this.setComponentAlignment(buttons, Alignment.BOTTOM_CENTER);
	        //setExpandRatio(buttons, 0f);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.setSizeFull(); 
	}
	
	Label taskLabel;
	Label taskDescription;
	
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
	
	public Object evalGroovyExpression(String expression) throws GroovyException {
		return GroovyUtil.evaluate(expression, groovyContext, processDefinitionUUID, true);		
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
	
	
	
	public void initializeNewProcess() throws ProcessNotFoundException, VariableNotFoundException, Exception {
		  ProcessInstanceUUID startNewProcess = getBpmModule().startNewProcess(processDefinitionUUID, getProcessVariables());
		  setProcessInstanceUUID(startNewProcess);
	}
	
	private void initProcessVariables() {
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
		if(pageFlow!=null)//this is a entry pageflow, start process after clicking next button
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
		
		initProcessVariables();
		 
		updateView(taskManager.renderPageflow(pageFlow));
	}


	private void updateView(List<Component> components) {
		this.buttons.removeAllComponents();
		this.taskPanel.removeAllComponents();
		if(components.size()==1){
			Component c = components.get(0);			
			this.taskPanel.addComponent(c);
		}
		else {
			Accordion accordionLayout=new Accordion();
			accordionLayout.setSizeFull();
			
			for (Component component : components) {
				accordionLayout.addTab(component, component.getCaption(), null);
			}
			this.taskPanel.addComponent(accordionLayout);
		}
		for (Entry<String, TaskField> component : this.taskManager.getFields().entrySet()) {
			TaskField tf=component.getValue();
			if(tf.getWidget().getType()== WidgetType.BUTTON_SUBMIT){
				Button button = (Button) tf.getComponent();
				this.buttons.addButton(button);
			}
		}
		Button buttonKatkesta = new Button("Katkesta");
		this.buttons.addButton(buttonKatkesta);
		this.buttons.setComponentAlignment(buttonKatkesta, Alignment.MIDDLE_RIGHT);
		buttonKatkesta.setSizeUndefined();
		
		Button buttonSulge = new Button("Sulge");
		this.buttons.addButton(buttonSulge);
		this.buttons.setComponentAlignment(buttonSulge, Alignment.MIDDLE_RIGHT);
		buttonSulge.setSizeUndefined();
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

	public void setWindow(PbWindow window) {
		this.window = window;
	}

	public void updateVariableValue(String variable, Object componentValue) {
		processVariables.put(variable, componentValue);		
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public void finishTask(TaskManager manager) {
		try {
		String confirmationMessage = manager.getConfirmationMessage();
		if(!StringUtils.isNullOrEmpty(confirmationMessage))
		{
			getWindow().showInformation(confirmationMessage);
		}
		if(taskInstance!=null){
			getBpmModule().finishTask(taskInstance, 
					true,
					getProcessVariables(), 
					manager.getActivityVariables(),
					null);//TODO: ATTACHMENTS now it's null
		}
		//find next task to execute
		taskInstance = bpmModule.nextUserTask(getProcessInstanceUUID(), 
				ProcessbaseApplication.getCurrent().getUserName());
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
				return;
			}
		}			
		
		} catch (TaskNotFoundException e) {
			window.showError(e.getMessage());
		} catch (IllegalTaskStateException e) {
			window.showError(e.getMessage());
		} catch (InstanceNotFoundException e) {
			window.showError(e.getMessage());
		} catch (VariableNotFoundException e) {
			window.showError(e.getMessage());
		} catch (Exception e) {
			window.showError(e.getMessage());
		}
	}

	
	
}
