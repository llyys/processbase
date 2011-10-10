package org.processbase.ui.bpm.generator.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.util.GroovyException;
import org.ow2.bonita.util.GroovyExpression;
import org.ow2.bonita.util.GroovyUtil;
import org.processbase.ui.bpm.generator.GeneratedTable;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.SelectMode;
import org.processbase.ui.core.bonita.forms.ValuesList;
import org.processbase.ui.core.bonita.forms.Widget;
import org.processbase.ui.core.bonita.forms.WidgetType;
import org.processbase.ui.core.bonita.forms.Actions.Action;
import org.processbase.ui.core.template.ImmediateUpload;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.Validator.InvalidValueException;
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

	private String name;
	private final TaskManager taskManager;
	
	private Object value;
	private Widget widget;
	public TaskField(TaskManager taskManager, Widget widget) {
		this.taskManager = taskManager;		
		this.widget=widget;
		taskManager.getFields().put(widget.getId(), this);
	}
	public TaskField(TaskManager taskManager, Widget widget, Component component, String name, Object value){
		this.taskManager = taskManager;
		this.widget=widget;
		this.component=component;
		this.name=name;
		this.value = value;
		taskManager.getFields().put(name, this);
		
	}
	
	private void addAction(Action action) {
		if(this.actions==null)
			this.actions=new ArrayList<Action>();
		this.actions.add(action);
	}
	private Component createComponent(Widget widget) {
		this.component=getComponent(widget);
		this.component = (this.component != null) ? this.component : new Label("");
		if (!(this.component instanceof Button) && this.component instanceof AbstractField) {
			/*if (value != null) {
				((AbstractField) c).setValue(value);
			}
			*/
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
	public List<Action> getActions() {
		return actions;
	}
	public Component getComponent() {
		if(component==null)
			createComponent(widget);
		return component;
	}
	private Component getComponent(Widget widget) {
		Object value = null;
		DataFieldDefinition dfd = null;
		Collection options = null;
		try {
			TaskField field=taskManager.getWidgetField(widget);
			value=field.getComponentValue();
			
			if(value!=null && value instanceof Component){//if this is a vaadin component
				return (Component) value;
			}
			if (widget.getType().equals(WidgetType.MESSAGE)) {
				String escaped = StringEscapeUtils.unescapeHtml(value.toString());
				Label component = new Label(escaped);
				component.setWidth("100%");
				component.setContentMode(Label.CONTENT_XHTML);
				return component;
			}
			if (widget.getType().equals(WidgetType.TEXT)) {
				String val = value == null ? "" : value.toString();
				String escaped = StringEscapeUtils.unescapeHtml(val.toString());
				Label component = new Label(widget.getLabel() + "<br/>" + val);
				component.setWidth("100%");
				component.setContentMode(Label.CONTENT_XHTML);
				return component;				
			}
			
			if (widget.getType().equals(WidgetType.HIDDEN)) {
				TextField component = new TextField(widget.getLabel());
				component.setNullRepresentation("");
				component.setVisible(false);
				return component;				
			}
			if (widget.getType().equals(WidgetType.TEXTBOX)) {
				TextField component = new TextField(widget.getLabel());
				component.setNullRepresentation("");
				return component;
			}
			if (widget.getType().equals(WidgetType.DATE)) {
				PopupDateField component = new PopupDateField(widget.getLabel());
				component.setResolution(PopupDateField.RESOLUTION_DAY);
				return component;
			}
			if (widget.getType().equals(WidgetType.TEXTAREA)) {
				TextArea component = new TextArea(widget.getLabel());
				component.setNullRepresentation("");
				return component;
			}
			if (widget.getType().equals(WidgetType.RICH_TEXTAREA)) {
				RichTextArea component = new RichTextArea(widget.getLabel());
				component.setNullRepresentation("");
				return component;
			}
			
			if (widget.getType().equals(WidgetType.PASSWORD)) {
				TextField component = new TextField(widget.getLabel());
				component.setNullRepresentation("");				
				return component;
			}

			if (widget.getType().equals(WidgetType.LISTBOX_SIMPLE)) {
				NativeSelect component = new NativeSelect(widget.getLabel(), (Collection)value);
				return component;
			}
			if (widget.getType().equals(WidgetType.SUGGESTBOX)) {
				ComboBox component = new ComboBox(widget.getLabel(), (Collection)value);
				component.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
				return component;
			}
			if (widget.getType().equals(WidgetType.RADIOBUTTON_GROUP)) {
				OptionGroup component = new OptionGroup(widget.getLabel(), (Collection)value);
				if (widget.getSelectMode() != null
						&& widget.getSelectMode().equals(SelectMode.MULTIPLE)) {
					component.setMultiSelect(true);
				}
				return component;
			}
			if (widget.getType().equals(WidgetType.LISTBOX_MULTIPLE)) {
				ListSelect component = new ListSelect(widget.getLabel(), (Collection)value);
				component.setMultiSelect(true);
				return component;
			}
			if (widget.getType().equals(WidgetType.CHECKBOX)) {
				return new CheckBox(widget.getLabel());				
			}
			if (widget.getType().equals(WidgetType.EDITABLE_GRID)) {
				//c = new GeneratedTable(widget, value, groovyScripts);
			}
			if (widget.getType().equals(WidgetType.CHECKBOX_GROUP)) {
				OptionGroup component = new OptionGroup(widget.getLabel(), (Collection)value);
				if (widget.getSelectMode() != null && widget.getSelectMode().equals(SelectMode.MULTIPLE)) {
					component.setMultiSelect(true);
				}
				return component;
			}
			if (widget.getType().equals(WidgetType.FILEUPLOAD)) {
				//hasAttachments = true;
				return getUpload(widget);
			}
			if (widget.getType().equals(WidgetType.FILEDOWNLOAD)) {
				//hasAttachments = true;
				return getDownload(widget);
			}
			if (widget.getType().equals(WidgetType.BUTTON_SUBMIT)/* || widget.getType().equals(WidgetType.BUTTON_NEXT) || widget.getType().equals(WidgetType.BUTTON_PREVIOUS)*/) {
				Button component = new Button(widget.getLabel());
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
	public Object getComponentValue(){
		try {
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
		if(widget==null)
			return null;
		if (widget.getInitialValue() != null
				&& widget.getInitialValue().getExpression() != null) {
			if(GroovyExpression.isGroovyExpression(widget.getInitialValue().getExpression()))
				value = taskManager.evalGroovyExpression(widget.getInitialValue().getExpression());
			else
				return widget.getInitialValue().getExpression();

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
		} catch (GroovyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	private Component getDownload(Widget widget) {
/*
		Button b = new Button(widget.getLabel());
		b.setStyleName(Reindeer.BUTTON_LINK);

		// if there is no attached document then this button should be disabled
		// mode.
		String fileName = attachmentFileNames.get(widget.getInitialValue().getExpression());
		if (fileName == null)
			b.setEnabled(false);
		else {
			b.addListener(new Button.ClickListener() {

				public void buttonClick(ClickEvent event) {
					Widget w = getWidgets(event.getButton());
					byte[] bytes;
					try {
						String processUUID = taskInstance.getProcessInstanceUUID().toString();
						String fileName = attachmentFileNames.get(w.getInitialValue().getExpression());
						AttachmentInstance attachment = getBpmModule().getAttachment(processUUID,w.getVariableBound());
						Document document = getBpmModule().getDocument(attachment.getUUID());
						bytes = getBpmModule().getAttachmentBytes(attachment);
						ByteArraySource bas = new ByteArraySource(bytes);

						StreamResource streamResource = new StreamResource(bas,document.getContentFileName(), getApplication());
						streamResource.setCacheTime(50000); // no cache (<=0)
															// does not work
															// with IE8
						streamResource.setMIMEType("application/octet-stream");
						getWindow().getWindow().open(streamResource, "_blank");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		return b;*/
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	private ImmediateUpload getUpload(Widget widget) {
		/*ImmediateUpload component = null;
		String processUUID = null;
		String fileName = null;
		boolean hasFile = false;
		if (taskInstance != null) {
			processUUID = taskInstance.getProcessInstanceUUID().toString();
			fileName = attachmentFileNames.get(widget.getInitialValue().getExpression());

			LOGGER.debug("widget.getInitialValue().getExpression() = "+ widget.getInitialValue().getExpression());
			LOGGER.debug("fileName = " + fileName);
			if (fileName != null) {
				hasFile = true;
			}		}
		component = new ImmediateUpload(processUUID, widget.getLabel(), widget
				.getInitialValue().getExpression(), fileName, hasFile,
				widget.isReadonly(), ProcessbaseApplication.getCurrent()
						.getPbMessages());

		return component;*/
		return null;
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
			
			if(("field_"+this.getName()).equalsIgnoreCase(expression))
				this.addAction(action);
		}
	}
	
	public void setComponent(Component component) {
		this.component = component;
	}
	public void setName(String name) {
		this.name = name;
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
