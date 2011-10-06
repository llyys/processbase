package org.processbase.ui.bpm.generator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringEscapeUtils;
import org.h2.util.StringUtils;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.util.GroovyException;
import org.ow2.bonita.util.GroovyExpression;
import org.ow2.bonita.util.GroovyUtil;
import org.processbase.ui.bpm.generator.CSSProperty;
import org.processbase.ui.bpm.generator.ComponentStyle;
import org.processbase.ui.bpm.generator.TableStyle;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.ActionType;
import org.processbase.ui.core.bonita.forms.Actions.Action;
import org.processbase.ui.core.bonita.forms.Activities.Activity;
import org.processbase.ui.core.bonita.forms.PageFlow;
import org.processbase.ui.core.bonita.forms.PageFlow.Pages.Page;
import org.processbase.ui.core.bonita.forms.FieldValue;
import org.processbase.ui.core.bonita.forms.ValuesList;
import org.processbase.ui.core.bonita.forms.VariableType;
import org.processbase.ui.core.bonita.forms.Widget;
import org.processbase.ui.core.bonita.forms.WidgetGroup;
import org.processbase.ui.core.bonita.forms.WidgetType;
import org.processbase.ui.core.bonita.forms.Widgets;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class TaskManager //extends ProcessManager
{
	private final ProcessManager processManager;

	public TaskManager(ProcessManager processManager){
		this.processManager = processManager;
		
	}
	
	
	private Map<String, Object> activityVariables = new HashMap<String, Object>();
	
	private Map<String, TaskField> fields;
	private List<Action> actions;
	private Widget clickedWidget;
	
	private Set<DataFieldDefinition> activityDataFields;
	private TaskInstance task;

	private Activity activity;
		
	
	public void setFields(Map<String, TaskField> fields) {
		this.fields = fields;
	}
	public Map<String, TaskField> getFields() {
		if(fields==null)
			fields=new Hashtable<String, TaskField>();
		return fields;
	}
	
	public TaskField findTaskFieldByName(String name){
		if(getFields().containsKey(name))
			return getFields().get(name);
		return null;
	}
	
	public void registerComponent(Component component, String name) {
		TaskField field=findTaskFieldByName(name);
		if(field!=null){
			field.setComponent(component);
		}		
	}
	
	public Iterable<TaskField> getIterator() {
        return fields.values();
    }
	
	public static String stripGroovyExpression(String script){
		script = script.replace(GroovyExpression.START_DELIMITER, "");
		int end = script.lastIndexOf(GroovyExpression.END_DELIMITER);
		return script.substring(0, end > 0 ? end : script.length());		
	}
	
	
	
	public void setActivityVariables(Map<String, Object> activityVariables) {
		this.activityVariables = activityVariables;
	}
	public Map<String, Object> getActivityVariables() {
		updateActionValues();
		return activityVariables;
	}
	
	
	private void updateActionValues(){
		if(getActions() == null) return;
		
		List<Action> processActions=new ArrayList<Action>();
		Map<String, Object> merged=new Hashtable<String, Object>();
		
		for (Action action : getActions()) {
			if (action.getType().equals(ActionType.SET_VARIABLE)) {
				if (action.getExpression().startsWith("field")) {
					TaskField tf=findTaskFieldByName(action.getExpression());					
					if (action.getVariableType().equals(VariableType.PROCESS_VARIABLE)) {
						processManager.getProcessVariables().put(action.getVariable(), tf.getComponentValue());
						merged.put(action.getVariable(), tf.getComponentValue());
					} else if (action.getVariableType().equals(VariableType.ACTIVITY_VARIABLE)) {
						activityVariables.put(action.getVariable(), tf.getComponentValue());
						merged.put(action.getVariable(), tf.getComponentValue());
					}
				}
				//handle button actions
				else if(org.apache.commons.lang.StringUtils.isNotBlank(action.getSubmitButton()) && this.clickedWidget != null){
					
					String script = action.getExpression();
					if(!GroovyExpression.isGroovyExpression(script))						
						script = GroovyExpression.START_DELIMITER + script + GroovyExpression.END_DELIMITER;
					
					Object actionValue=null;
					try {

						if (this.clickedWidget.getId().equals(action.getSubmitButton())) {							
							if (action.getVariableType().equals(VariableType.PROCESS_VARIABLE)) {
								actionValue = GroovyUtil.evaluate(script, merged);
								processManager.getProcessVariables().put(action.getVariable(), actionValue);

							} else if (action.getVariableType().equals(VariableType.ACTIVITY_VARIABLE)) {
								actionValue = GroovyUtil.evaluate(script,merged);
								activityVariables.put(action.getVariable(), actionValue);
							}
						}
					} catch (GroovyException e) {
						// plain ignorance ;)
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
	public List<Action> getActions() {
		return actions;
	}	
	
	public void setActivityDataFieldDefinitions(
			Set<DataFieldDefinition> activityDataFields) {
				this.activityDataFields = activityDataFields;
		
	}
	public Object getWidgetValue(Widget widget) throws GroovyException {
		if (widget.getInitialValue() != null && widget.getInitialValue().getExpression() != null) {				
			return evalGroovyExpression(widget.getInitialValue().getExpression());			
		}
		if (widget.getAvailableValues() != null) {
			if (widget.getAvailableValues().getExpression() != null) {
				return evalGroovyExpression(widget.getAvailableValues().getExpression());
				
			} else if (!widget.getAvailableValues().getValuesList().getAvailableValues().isEmpty()) {
				List<String>options = new ArrayList<String>();
				for (ValuesList.AvailableValue avalue : widget.getAvailableValues().getValuesList().getAvailableValues()) {
					options.add(avalue.getValue());
				}
				return options;
			}
		}
		return null;
	}
	private boolean isTaskActive() {
		return !(task == null
				|| task.getState().equals(ActivityState.FINISHED)
				|| task.getState().equals(ActivityState.ABORTED) 
				|| task.getState().equals(ActivityState.CANCELLED));
	}
	
	public void onFinishTask() {
		
		//validate all fields in task
		List<String> errors=new ArrayList<String>();
		
		//save all data from fields
		for (Entry<String, TaskField> field : getFields().entrySet()) {
			TaskField taskField = field.getValue();
			String error=taskField.validate();
			if(error!=null)
				errors.add(error);
			
			if(taskField.getActions()!=null){
				for (Action action : taskField.getActions()) {
					if(action.getVariableType().equals(VariableType.PROCESS_VARIABLE)){
						processManager.updateVariableValue(action.getVariable(), taskField.getComponentValue());
					}
					else if(action.getVariableType().equals(VariableType.PROCESS_VARIABLE)){
						updateVariableValue(action.getVariable(), taskField.getComponentValue());
					}
				}
			}
		}
		if(errors.size()>0)
			return; //do not continue
			//throw new Exception("Validation errors"+errors.toString());
		
		if(task==null) {//this is a
			 try {
				processManager.initializeNewProcess();
				processManager.finishTask(this);
			} catch (ProcessNotFoundException e) {
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
	}
	
	public void updateVariableValue(String variable, Object componentValue) {
		activityVariables.put(variable, componentValue);		
	}
	
	public Object evalGroovyExpression(String expression) throws GroovyException {
		return GroovyUtil.evaluate(expression, processManager.getGroovyContext(), task.getUUID(), false, true);
	}
	
	public void prepareProcessVariables() {
		try {
            if (this.task != null) {
            	processManager.setProcessDataFieldDefinitions(processManager.getBpmModule().getProcessDataFields(this.task.getProcessDefinitionUUID()));
                setActivityDataFieldDefinitions(processManager.getBpmModule().getActivityDataFields(this.task.getActivityDefinitionUUID()));
                
                processManager.setProcessVariables(processManager.getBpmModule().getProcessInstanceVariables(this.task.getProcessInstanceUUID()));
                setActivityVariables(processManager.getBpmModule().getActivityInstanceVariables(this.task.getUUID()));
            } else {
            	//setProcessDataFieldDefinitions(processManager.getBpmModule().getProcessDataFields());                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }		
	}
	
	public void setTask(TaskInstance task) {
		this.task = task;
		
	}	
	
	
	
	private Label getLabel(Widget widget, Object value) {
		String escaped = StringEscapeUtils.unescapeHtml(value.toString());
		Label component = new Label(escaped);
		component.setWidth("100%");
		component.setContentMode(Label.CONTENT_XHTML);
		return component;
	}
	
	private List<Component> components=null;
	
	public void setActivity(Activity activity) {
		this.activity = activity;		
		
	}
	public List<Component> renderPageflow(PageFlow pageFlow) {
		components=new ArrayList<Component>();
		if(pageFlow==null){
			components.add(RenderDynamicPageComponents(this.task.getActivityLabel()));
			return components;
		}
		confirmationMessage=pageFlow.getConfirmationMessage();
		List<Page> pages = pageFlow.getPages().getPages();
		components=new ArrayList<Component>(pages.size());
		for (Page page : pages) {
			components.add(RenderPageComponents(page));
		}
		return components;
	}
	
	private Component RenderPageComponents(Page page) {
		// TODO Auto-generated method stub
		int gridColumns=1;
		int gridRows=page.getWidgets().getWidgetsAndGroups().size();
		if(page.getActions()!=null) 
			this.setActions(page.getActions().getActions());
		else 
			this.setActions(null);
		TableStyle tableStyle = processManager.getBarResource().getTableStyle(page);
		if(tableStyle!=null)
		{
			gridColumns=tableStyle.getColumns();
			gridRows=tableStyle.getRows();
		}
		
		GridLayout gridLayout = new GridLayout(gridColumns, gridRows);
		gridLayout.setSizeFull();
		gridLayout.setSpacing(true);
		int row1 = 0;
		int row2 = 0;
		
		for (Object wg : page.getWidgets().getWidgetsAndGroups()) {
			Component c = null;
			if (wg instanceof Widget) {
				Widget widget = (Widget) wg;
				TaskField field=new TaskField(this, widget);
				field.registerActions(this.actions);
				c = field.getComponent();

				if (c != null) {
										
					int col1 = 0;
					int col2 = 0;
					if (tableStyle!=null) {
						ComponentStyle componentStyle = tableStyle.getElements().get(widget.getId());
						
						col1 = componentStyle.getPosition().getFColumn();
						row1 = componentStyle.getPosition().getFRow();
						col2 = componentStyle.getPosition().getTColumn();
						row2 = componentStyle.getPosition().getTRow();
						
						CSSProperty cssProperty = componentStyle.getCss();
						gridLayout.addComponent(c, col1, row1, col2, row2);
					} else {
						gridLayout.addComponent(c, col1, row1, col2, row2);
						row2++;
						row1++;
					}
				}
			} else if (wg instanceof WidgetGroup) {
				// TODO WidgetGroup
			}
		}
	
		return gridLayout;
		
	}
	public void setComponents(List<Component> components) {
		this.components = components;
	}
	public List<Component> getComponents() {
		return components;
	}
	public TaskField getWidgetField(Widget widget) {
		return getFields().get(widget.getId());
	}
	
	public Component RenderDynamicPageComponents(String pageLabel) {
		
		VerticalLayout layout=new VerticalLayout(); 
		for (DataFieldDefinition dataField : processManager.getProcessDataFields()) {
			
			Widget w = new Widget();
			w.setType(WidgetType.TEXT);
			
			w.setId(dataField.getName());
			w.setReadonly(true);
			w.setAllowHtmlInField(false);
			w.setVariableBound("${" + dataField.getName() + "}");
			w.setLabel(dataField.getLabel());

			FieldValue value = new FieldValue();
			value.setExpression(w.getVariableBound());

			w.setInitialValue(value);
			TaskField field=new TaskField(this, w);
			layout.addComponent(field.getComponent());
			
		}

		// append last submit button
		Widget submitWidget = new Widget();
		submitWidget.setType(WidgetType.BUTTON_SUBMIT);
		submitWidget.setId("submit");
		submitWidget.setLabelButton(false);
		submitWidget.setLabel("Submit");
		
		TaskField field=new TaskField(this, submitWidget);
		layout.addComponent(field.getComponent());
		
		return layout;
		
	}
	
	public void onTaskFieldButtonClick(TaskField field, ClickEvent event){
		if(field.getWidget().getType()==WidgetType.BUTTON_SUBMIT){
			onFinishTask();
		}		
	}
	
	String confirmationMessage;
	public String getConfirmationMessage() {
		if(StringUtils.isNullOrEmpty(confirmationMessage))
			return null;
		
		if(GroovyExpression.isGroovyExpression(confirmationMessage))
		{
			try {
				Object result=evalGroovyExpression(confirmationMessage);
				if(result!=null)
					return result.toString();
			} catch (GroovyException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		
		return confirmationMessage;
	}
	/**
	 * Clean up myself
	 */
	public void Dispose() {
		
		
	}
	
}
