package org.processbase.ui.bpm.generator.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.Document;
import org.ow2.bonita.util.GroovyException;
import org.ow2.bonita.util.GroovyExpression;
import org.ow2.bonita.util.GroovyUtil;
import org.processbase.ui.bpm.generator.GeneratedTable;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.SelectMode;
import org.processbase.ui.core.bonita.forms.ValuesList;
import org.processbase.ui.core.bonita.forms.Widget;
import org.processbase.ui.core.bonita.forms.WidgetType;
import org.processbase.ui.core.bonita.forms.Actions.Action;
import org.processbase.ui.core.template.ByteArraySource;
import org.processbase.ui.core.template.ImmediateUpload;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;

/**
 * For individual process data field value holder 
 * maps together component, form widget and it's datafield 
 * @author lauri
 *
 */
public class TaskField {
	
	private List<Action> actions;
	private Component component;
	private final TaskManager taskManager;	
	private Object value;
	private Widget widget;
	
	public TaskField(TaskManager taskManager, Widget widget) {
		this.taskManager = taskManager;		
		this.widget=widget;
		taskManager.getFields().put(getName(), this);
	}
	
	public TaskField(TaskManager taskManager, Widget widget, Component component, Object value){
		this.taskManager = taskManager;
		this.widget=widget;
		this.component=component;
		this.value = value;
		taskManager.getFields().put(getName(), this);
	}
	
	
	
	private Component createComponent() {
		
		//TaskField field=taskManager.getWidgetField(widget);
		this.component=initComponent();
		this.component = (this.component != null) ? this.component : new Label("");
		registerActions(taskManager.getActions());
		if (!(this.component instanceof Button) 
				&& this.component instanceof AbstractField) {
			
			if (value != null) {
				((AbstractField) this.component).setValue(value);
			}
			if (widget.isMandatory() != null) {
				((AbstractField) this.component).setRequired(widget.isMandatory());
			}
			((AbstractField) this.component).setRequiredError(widget.getLabel() + ProcessbaseApplication.getCurrent().getPbMessages().getString("fieldRequired"));
			((AbstractField) this.component).setDescription(widget.getTitle() != null ? widget.getTitle() : "");
			((AbstractField) this.component).setInvalidCommitted(false);
			((AbstractField) this.component).setWriteThrough(false);
		}
		if (widget.isReadonly() != null) {
			this.component.setReadOnly(widget.isReadonly());
		}
		return this.component;
	}
	
	public void addAction(Action action) {
		if(this.actions==null)
			this.actions=new ArrayList<Action>();
		this.actions.add(action);
	}
	
	public List<Action> getActions() {
		return actions;
	}
	
	public Component getComponent() {
		if(component==null)
			createComponent();
		return component;
	}
	
	private Component initComponent() {
		Object value = null;
		DataFieldDefinition dfd = null;
		Collection options = null;
		try {
			value=updateComponentValue();
			String label=widget.getLabel(); 
			if(label==null)
				label="";
			if(value!=null && value instanceof Component){//if this is a vaadin component
				return (Component) value;
			}
			WidgetType type = widget.getType();
			if (type.equals(WidgetType.MESSAGE)) {
				String escaped = StringEscapeUtils.unescapeHtml(value.toString());
				Label component = new Label(escaped);
				component.setWidth("100%");
				component.setContentMode(Label.CONTENT_XHTML);
				return component;
			}
			if (type.equals(WidgetType.TEXT)) {
				String val = value == null ? "" : value.toString();
				String escaped = StringEscapeUtils.unescapeHtml(val.toString());
				String content = label + "<br/>" + escaped;
				if("".equals(label))
					content=escaped;
				Label component = new Label(content);
				component.setWidth("100%");
				component.setContentMode(Label.CONTENT_XHTML);
				return component;				
			}
			
			if (type.equals(WidgetType.HIDDEN)) {
				TextField component = new TextField(label);
				component.setNullRepresentation("");
				component.setValue(value);
				component.setVisible(false);
				return component;				
			}
			if (type.equals(WidgetType.TEXTBOX)) {
				TextField component = new TextField(label);
				component.setNullRepresentation("");
				component.setValue(value);
				return component;
			}
			if (type.equals(WidgetType.DATE)) {
				PopupDateField component = new PopupDateField(label);
				component.setResolution(PopupDateField.RESOLUTION_DAY);
				return component;
			}
			if (type.equals(WidgetType.TEXTAREA)) {
				TextArea component = new TextArea(label);
				component.setNullRepresentation("");
				return component;
			}
			if (type.equals(WidgetType.RICH_TEXTAREA)) {
				RichTextArea component = new RichTextArea(label);
				component.setNullRepresentation("");
				return component;
			}
			
			if (type.equals(WidgetType.PASSWORD)) {
				TextField component = new TextField(label);
				component.setNullRepresentation("");				
				return component;
			}

			if (type.equals(WidgetType.LISTBOX_SIMPLE)) {
				NativeSelect component = new NativeSelect(label, (Collection)value);
				return component;
			}
			if (type.equals(WidgetType.SUGGESTBOX)) {
				ComboBox component = new ComboBox(label, (Collection)value);
				component.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
				return component;
			}
			if (type.equals(WidgetType.RADIOBUTTON_GROUP)) {
				OptionGroup component = new OptionGroup(label, (Collection)value);
				if (widget.getSelectMode() != null
						&& widget.getSelectMode().equals(SelectMode.MULTIPLE)) {
					component.setMultiSelect(true);
				}
				return component;
			}
			if (type.equals(WidgetType.LISTBOX_MULTIPLE)) {
				ListSelect component = new ListSelect(label, (Collection)value);
				component.setMultiSelect(true);
				return component;
			}
			if (type.equals(WidgetType.CHECKBOX)) {
				return new CheckBox(label);				
			}
			if (type.equals(WidgetType.EDITABLE_GRID)) {
				//c = new GeneratedTable(widget, value, groovyScripts);
			}
			if (type.equals(WidgetType.CHECKBOX_GROUP)) {
				OptionGroup component = new OptionGroup(label, (Collection)value);
				if (widget.getSelectMode() != null && widget.getSelectMode().equals(SelectMode.MULTIPLE)) {
					component.setMultiSelect(true);
				}
				return component;
			}
			if (type.equals(WidgetType.FILEUPLOAD)) {
				//hasAttachments = true;
				return getUpload((String)value);
			}
			if (type.equals(WidgetType.FILEDOWNLOAD)) {
				//hasAttachments = true;
				return getDownload((String)value);
			}
			if (type.equals(WidgetType.BUTTON_SUBMIT)/* || widget.getType().equals(WidgetType.BUTTON_NEXT) || widget.getType().equals(WidgetType.BUTTON_PREVIOUS)*/) {
				Button component = new Button(label);
				//component.addListener((Button.ClickListener) this);
				if (widget.isLabelButton()) {
					component.setStyleName(Reindeer.BUTTON_LINK);
				}
				component.setData(this);
				component.addListener(new Button.ClickListener() {					
					public void buttonClick(ClickEvent event) {
						TaskField that=(TaskField) event.getButton().getData();
						taskManager.onTaskFieldButtonClick(that, event);
					}
				});
				return component;
			}			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Label("");
	}
	

	public Object updateComponentValue() throws Exception{
		if (widget.getInitialValue() != null 
				&& widget.getInitialValue().getExpression() != null) {
			
			String expression = widget.getInitialValue().getExpression();
			
			if(GroovyExpression.isGroovyExpression(expression))
				value = taskManager.evalGroovyExpression(expression);
			else
				return expression;

			if (value instanceof Component)// if value is vaadin component return component instance
				return (Component) value;
		}
		
		Collection options = null;
		if (widget.getAvailableValues() != null) {
			if (widget.getAvailableValues().getExpression() != null) {
				options = (Collection) taskManager.evalGroovyExpression(widget.getAvailableValues().getExpression());
			} else if (!widget.getAvailableValues().getValuesList().getAvailableValues().isEmpty()) {
				options = new ArrayList<String>();
				for (ValuesList.AvailableValue avalue : widget
						.getAvailableValues().getValuesList()
						.getAvailableValues()) {
					options.add(avalue.getValue());
				}
			}
		}		
		return options;
		
	}
	
	public Object getComponentValue(){
		
		if(component!=null)
		{
			if (component instanceof AbstractField) {
				return ((AbstractField) component).getValue();
			} else if (component instanceof GeneratedTable) {
				return ((GeneratedTable) component).getTableValue();
			} else if (component instanceof CheckBox) {
				return ((CheckBox) component).booleanValue();
			} else {
				//return action.getExpression();
			}
		}
		if(widget==null){
			return null;		
		} 
		return null;		
	}
	
	private Component getDownload(String fileName) {

		Button b = new Button(getName());
		b.setStyleName(Reindeer.BUTTON_LINK);

		// if there is no attached document then this button should be disabled
		// mode.
		if (fileName == null)
			b.setEnabled(false);
		else {
			b.addListener(new Button.ClickListener() {

				public void buttonClick(ClickEvent event) {
					
					//Widget w = getWidgets(event.getButton());
					byte[] bytes;
					try {
						ProcessManager processManager = taskManager.getProcessManager();
						String processUUID = processManager.getProcessInstanceUUID().toString();
						String fileName = widget.getVariableBound();
						BPMModule bpmModule = processManager.getBpmModule();
						AttachmentInstance attachment = bpmModule.getAttachment(processUUID,widget.getVariableBound());
						Document document = bpmModule.getDocument(attachment.getUUID());
						bytes = bpmModule.getAttachmentBytes(attachment);
						ByteArraySource bas = new ByteArraySource(bytes);

						StreamResource streamResource = new StreamResource(bas,document.getContentFileName(), processManager.getApplication());
						streamResource.setCacheTime(50000); // no cache (<=0)
															// does not work
															// with IE8
						streamResource.setMIMEType("application/octet-stream");
						processManager.getWindow().open(streamResource, "_blank");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		return b;
		
	}
	
	public String getName() {
		return widget.getId();
	}
	
	private ImmediateUpload getUpload(String fileName) {
		ImmediateUpload component = null;
		String processUUID = null;
		//String fileName = null;
		boolean hasFile = false;
		ProcessManager processManager = taskManager.getProcessManager();
		if (processManager.getTaskInstance() != null) {
			processUUID = processManager.getTaskInstance().getProcessInstanceUUID().toString();
			try {
				AttachmentInstance attachment = processManager.getBpmModule().getAttachment(processUUID,widget.getVariableBound());
				fileName=attachment.getFileName();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//fileName = attachmentFileNames.get(widget.getInitialValue().getExpression());
			if (fileName != null) {
				hasFile = true;
			}		
		}
		component = new ImmediateUpload(processUUID, widget.getLabel()
				, widget.getInitialValue().getExpression(), fileName, hasFile
				, widget.isReadonly(), ProcessbaseApplication.getCurrent().getPbMessages());
		return component;
	}
	
	public Object getValue() {
		return value;
	}
	
	public Widget getWidget() {
		return widget;
	}
	
	public void registerActions(List<Action> actions) {
		if(actions==null)
			return;
		for (Action action : actions) {
			String expression="";
			if(GroovyExpression.isGroovyExpression(action.getExpression()))				
				expression=TaskManager.stripGroovyExpression(action.getExpression());
			else
				expression=action.getExpression();
			
			if(("field_"+this.widget.getId()).equalsIgnoreCase(expression))
				this.addAction(action);
			
		}
	}
	
	public void setComponent(Component component) {
		this.component = component;
	}
	
	private void setWidget(Widget widget) {
		this.widget = widget;
	}
	
	public String validate(){
		
		StringBuilder errorMsg=new StringBuilder();
		if(this.component!=null && component instanceof AbstractField){
			try {
				((AbstractField) component).setComponentError(null);
				((AbstractField) component).validate();
			} catch (InvalidValueException ex) {
				
				if (ex instanceof EmptyValueException){
					((AbstractField) component).setComponentError(new UserError(
							((AbstractField) component).getRequiredError()));
					errorMsg.append("Required"+ex.getMessage());
				}
				errorMsg.append("Invalid value"+ex.getMessage());
				throw ex;
			}
		}
		return errorMsg.length()==0?null:errorMsg.toString();
	}
	
	
}
