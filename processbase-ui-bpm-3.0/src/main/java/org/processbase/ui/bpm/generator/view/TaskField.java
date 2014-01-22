package org.processbase.ui.bpm.generator.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.bonitasoft.forms.server.validator.RegexFieldValidator;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.util.GroovyException;
import org.ow2.bonita.util.GroovyExpression;
import org.ow2.bonita.util.GroovyUtil;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.Actions.Action;
import org.processbase.ui.core.bonita.forms.AvailableValues;
import org.processbase.ui.core.bonita.forms.SelectMode;
import org.processbase.ui.core.bonita.forms.Validators;
import org.processbase.ui.core.bonita.forms.ValuesList;
import org.processbase.ui.core.bonita.forms.Widget;
import org.processbase.ui.core.bonita.forms.WidgetType;
import org.processbase.ui.core.bonita.process.GeneratedTable;
import org.processbase.ui.core.template.ByteArraySource;
import org.processbase.ui.core.template.DownloadStreamResource;
import org.processbase.ui.core.template.ImmediateUpload;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.themes.Reindeer;

/**
 * For individual process data field value holder 
 * maps together component, form widget and it's datafield 
 * @author lauri
 *
 */
public class TaskField {
	
	private static final Logger LOG = Logger.getLogger(TaskField.class);
	
	private List<Action> actions;
	private Component component;
	private final TaskManager taskManager;	
	private Object value;
	private Widget widget;
	private boolean readOnly;
	
	
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
	
	
	
	private Component createComponent() throws Exception {
		
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
			((AbstractField) this.component).setRequiredError(widget.getLabel() + ProcessbaseApplication.getString("fieldRequired"));
			((AbstractField) this.component).setDescription(widget.getTitle() != null ? widget.getTitle() : "");
			((AbstractField) this.component).setInvalidCommitted(false);
			((AbstractField) this.component).setWriteThrough(false);
			
			if(widget.getValidators() != null){
				for (Validators.Validator v : widget.getValidators().getValidators()){
					if("org.bonitasoft.forms.server.validator.MailValidator".equals(v.getClassname())){
						((AbstractField) this.component).addValidator(new EmailValidator(
								ProcessbaseApplication.getString("errorInvalidEmail", "Invalid email address!")));
					}
				}
			}
		}else if (this.component instanceof CheckBox){
			if (value != null) {
				((CheckBox) this.component).setValue(value);
			}
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
	
	public Component getComponent() throws Exception {
		if(component==null)
			createComponent();
		return component;
	}
	
	private Component initComponent() throws Exception {
		try {
			//Object value = null;
			DataFieldDefinition dfd = null;
			Collection options = null;
			
				value=updateComponentValue();
				String label=widget.getLabel(); 
				if(label==null)
					label="";
				if(value!=null && value instanceof Component){//if this is a vaadin component
					return (Component) value;
				}
				WidgetType type = widget.getType();
				if (type.equals(WidgetType.MESSAGE)) {
					String escaped = StringEscapeUtils.unescapeHtml((value != null ? value.toString() :  ""));
					Label component = new Label(escaped);
					component.setWidth("100%");
					component.setContentMode(Label.CONTENT_XHTML);
					setReadOnly(true);
					return component;
				}
				if (type.equals(WidgetType.TEXT)) {
					String val = (value == null ? "" : value.toString());
					String escaped = StringEscapeUtils.unescapeHtml(val.toString());
					String content = label + "<br/>" + escaped;
					if("".equals(label))
						content=escaped;
					Label component = new Label(content);
					component.setWidth("100%");
					component.setContentMode(Label.CONTENT_XHTML);
					setReadOnly(true);
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
					//component.setWidth("100%");
					return component;
				}
				if (type.equals(WidgetType.DATE)) {
					PopupDateField component = new PopupDateField(label);
					component.setResolution(PopupDateField.RESOLUTION_DAY);
					return component;
				}
				if (type.equals(WidgetType.TEXTAREA)) {
					TextArea component = new TextArea(label);
					//component.setWidth("100%");
					//component.setHeight("100%");
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
					component.setValue(getInitialValue());
					//component.setWidth("100%");
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
					//component.setWidth("100%");
					
					return component;
				}
				if (type.equals(WidgetType.CHECKBOX)) {
					CheckBox c = new CheckBox(label);	
					c.setRequired(widget.isMandatory());
					return c;
				}
				if (type.equals(WidgetType.EDITABLE_GRID)) {
					
					//Find expressions to evaluate
					Set<String> expressions = new HashSet<String>();
					if (widget.getInitialValue() != null && widget.getInitialValue().getExpression() != null) {
                        expressions.add(widget.getInitialValue().getExpression());
                    }
                    if (widget.getAvailableValues() != null && widget.getAvailableValues().getExpression() != null) {
                        expressions.add(widget.getAvailableValues().getExpression());
                    }
                    if (widget.getHorizontalHeader() != null) {
                        expressions.add(widget.getHorizontalHeader());
                    }
                    if (widget.getVerticalHeader() != null) {
                        expressions.add(widget.getVerticalHeader());
                    }
                    if (widget.getMinRows() != null) {
                        expressions.add(widget.getMinRows());
                    }
                    if (widget.getMaxRows() != null) {
                        expressions.add(widget.getMaxRows());
                    }
                    if (widget.getMinColumns() != null) {
                        expressions.add(widget.getMinColumns());
                    }
                    if (widget.getMaxColumns() != null) {
                        expressions.add(widget.getMaxColumns());
                    }
					
					Map<String, Object> values = new HashMap<String, Object>();
					
					for (String expression : expressions) {
						Object value = expression;
						if (GroovyExpression.isGroovyExpression(expression)) {
							try {
								value = taskManager
										.evalGroovyExpression(expression);
							} catch (Exception e) {
								LOG.warn("could not evaluate " + expression, e);
							}
						}
						values.put(expression, value);
					}
					
					return new GeneratedTable(widget, value, values);
				}
				if (type.equals(WidgetType.TABLE)) {
					
					Set<String> expressions = new HashSet<String>();
					if (widget.getInitialValue() != null && widget.getInitialValue().getExpression() != null) {
                        expressions.add(widget.getInitialValue().getExpression());
                    }
                    if (widget.getAvailableValues() != null && widget.getAvailableValues().getExpression() != null) {
                        expressions.add(widget.getAvailableValues().getExpression());
                    }
                    if (widget.getHorizontalHeader() != null) {
                        expressions.add(widget.getHorizontalHeader());
                    }
                    if (widget.getVerticalHeader() != null) {
                        expressions.add(widget.getVerticalHeader());
                    }
                    if (widget.getValueColumnIndex() != null) {
                        expressions.add(widget.getValueColumnIndex());
                    }
                    if (widget.getVariableBound() != null) {
                        expressions.add(widget.getVariableBound());
                    }
                    
                    Map<String, Object> values = new HashMap<String, Object>();
                    
                    for (String expression : expressions) {
						Object value = expression;
						if (GroovyExpression.isGroovyExpression(expression)) {
							try {
								value = taskManager
										.evalGroovyExpression(expression);
							} catch (Exception e) {
								LOG.warn("could not evaluate " + expression, e);
							}
						}
						values.put(expression, value);
					}
				
                    Object availableValues = values.get(widget.getAvailableValues().getExpression());
					
					return new GeneratedTable(widget, availableValues, values);
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
					ImmediateUpload iu = getUpload((String)value);					
					return iu;
				}
				if (type.equals(WidgetType.FILEDOWNLOAD)) {
					//hasAttachments = true;
					setReadOnly(true);
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
							try {
								taskManager.onTaskFieldButtonClick(that, event);
							} catch (Exception e) {
								throw new RuntimeException("Task field button click", e);							
							}
						}
					});
					return component;
				}
		} catch (Exception e) {
			e.printStackTrace();
			return new Label("Error:"+widget.getLabel()+" - "+e.getMessage(), Label.CONTENT_XHTML);
		}			
			
		
		return new Label("");
	}
	
	public Object getInitialValue() throws Exception {

		if (widget.getInitialValue() != null
				&& widget.getInitialValue().getExpression() != null) {

			String expression = widget.getInitialValue().getExpression();
			if (GroovyExpression.isGroovyExpression(expression)) {
				try {
					return taskManager.evalGroovyExpression(expression);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				return expression;
			}
		}
		
		return null;
	}


	public Object updateComponentValue() throws Exception{
		if (widget.getInitialValue() != null 
				&& widget.getInitialValue().getExpression() != null) {
			
			String expression = widget.getInitialValue().getExpression();
			
			if(GroovyExpression.isGroovyExpression(expression))
				try {
					value = taskManager.evalGroovyExpression(expression);
				} catch (Exception e) {
					throw new Exception(expression, e);
				}
			else
				return expression;

			if (value instanceof Component)// if value is vaadin component return component instance
				return (Component) value;
			 
				
		}
		
		Collection options = null;
		AvailableValues availableValues = widget.getAvailableValues();
		if (availableValues != null) {
			if (availableValues.getExpression() != null) {
				options = (Collection) taskManager.evalGroovyExpression(availableValues.getExpression());
			} else if (!availableValues.getValuesList().getAvailableValues().isEmpty()) {
				options = new ArrayList<String>();
				for (ValuesList.AvailableValue avalue : availableValues.getValuesList()
						.getAvailableValues()) {
					options.add(avalue.getValue());
				}
			}
			return options;
		}		
		//return options;
		return value;
	}
	
	public Object getComponentValue(Action action){
		
		if(component!=null)
		{
			if (component instanceof CheckBox) {
				return ((CheckBox) component).booleanValue();
			}
			else if (component instanceof Button) {
				if(GroovyExpression.isGroovyExpression(action.getExpression())){
					ProcessManager processManager = taskManager.getProcessManager();
					Object result;
					try {
						result = taskManager.evalGroovyExpression(action.getExpression());
						return result;
					} catch (GroovyException e) {
						throw new RuntimeException(e);						
					} catch (Exception e) {
						return null;
					}
					
				}
				return action.getExpression();				
			} else if (component instanceof AbstractField) {
				return ((AbstractField) component).getValue();
			} else if (component instanceof GeneratedTable) {
				return ((GeneratedTable) component).getTableValue();
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
		
		ProcessManager processManager = taskManager.getProcessManager();
		String processUUID = processManager.getProcessInstanceUUID().toString();
		String fileVariable = widget.getVariableBound();
		
		//if(ProcessbaseApplication.getCurrent().getApplicationType()==ProcessbaseApplication.LIFERAY_PORTAL){
			//in liferay we cannot use simple link to download the file
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
							//AttachmentInstance attachment = bpmModule.getAttachment(processUUID,widget.getVariableBound());
							Document document = bpmModule.getDocument(processManager.getProcessInstanceUUID(),widget.getVariableBound());
							//Document document = bpmModule.getDocument(attachment.getUUID());
							bytes = bpmModule.getDocumentBytes(document);
							ByteArraySource bas = new ByteArraySource(bytes);

							DownloadStreamResource streamResource = new DownloadStreamResource(bas, 
			                		document.getContentFileName(), processManager.getApplication());
			                streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
			                
			                streamResource.setMIMEType("application/octet-stream");
			                streamResource.setParameter("Content-Disposition", "attachment; filename=\"" + document.getContentFileName()+"\"");
			                streamResource.setParameter("Cache-Control", "private, max-age=86400"); 
			                streamResource.setParameter("X-Content-Type-Options", "nosniff");
			                
							processManager.getApplication().getMainWindow().open(streamResource);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			return b;

//		}
//		else{
//			WebApplicationContext ctx = (WebApplicationContext) processManager.getApplication().getContext();
//			String path = ctx.getHttpSession().getServletContext().getContextPath();
//	        Link link = new Link(getName(), new ExternalResource(path + "/process_file?download="+processUUID+"&file="+fileVariable));
//	        return link;
//		}
       
        
	}
	
	public String getName() {
		return widget.getId();
	}
	
	private ImmediateUpload getUpload(String fileName) {
		
		String processUUID = null;
		//String fileName = null;
		boolean hasFile = false;
		final String boundVariable=widget.getVariableBound();
		final ProcessManager processManager = taskManager.getProcessManager();
		if (processManager.getTaskInstance() != null) {
			processUUID = processManager.getTaskInstance().getProcessInstanceUUID().toString();
			try {
                if(boundVariable==null)
                    throw new InvalidFormDefinitionException("process definition does not have bound variable for "+widget.getId());
				//AttachmentInstance attachment = processManager.getBpmModule().getAttachment(processUUID,boundVariable);
				Document document=processManager.getBpmModule().getDocument(processManager.getTaskInstance().getProcessInstanceUUID(), boundVariable);
				//fileName=attachment.getFileName();
				
				fileName=document.getContentFileName();
				if (document.getContentSize() > 0) {
					hasFile = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//fileName = attachmentFileNames.get(widget.getInitialValue().getExpression());
//			if (fileName != null) {
//				hasFile = true;
//			}	
			
		}
		
		String expression="";
		if(widget.getInitialValue()!=null)
			expression=widget.getInitialValue().getExpression();
		
		
		final ImmediateUpload component = new ImmediateUpload(processUUID, widget.getLabel()
				, boundVariable , fileName, hasFile
				, widget.isReadonly(), ProcessbaseApplication.getCurrent().getPbMessages());
		component.addListener(new Upload.FinishedListener(){

			public void uploadFinished(FinishedEvent event) {
				try {
					processManager.getBpmModule().addDocument(processManager.getTaskInstance().getProcessInstanceUUID(), widget.getVariableBound(), event.getFilename(), event.getMIMEType(), component.getFileBody());
				} catch (Exception e) {					
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				
			}
			
		});
		
		component.setMandatory(widget.isMandatory());
		component.setValidators(widget.getValidators());
		
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
		if(this.component!=null){
			if(component instanceof AbstractField){
				
				if(component instanceof CheckBox){
					CheckBox c = (CheckBox) component;
					if(c.isRequired() && !((Boolean)c.getValue())){
						String error = c.getCaption() + ProcessbaseApplication.getString("fieldRequired", " is required!");
						c.setComponentError(new UserError(error));
						errorMsg.append(error);
					}
				}
				
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
				
				if(component instanceof AbstractComponent){
					
					AbstractField c = (AbstractField) component;
					
					// Validators
					if (widget.getValidators() != null
							&& widget.getValidators().getValidators() != null) {
	
						for (Validators.Validator v : widget.getValidators().getValidators()) {
							if (v.getClassname().equals("org.bonitasoft.forms.server.validator.GroovyFieldValidator")
									&& v.getParameter() != null) {
								
								boolean valid = false;
								try {
									Map<String, Object> ctx = new HashMap<String, Object>();
									ctx.putAll(taskManager.getProcessManager().getGroovyContext());
									if(widget.getVariableBound() != null){
										ctx.put(TaskManager.stripGroovyExpression(widget.getVariableBound()), c.getValue());
									}
									ctx.put("field_" + widget.getId(), c.getValue());
									
									Object tmp = GroovyUtil.evaluate(GroovyExpression.START_DELIMITER + v.getParameter() 
											+ GroovyExpression.END_DELIMITER, ctx);
									valid = Boolean.parseBoolean(tmp.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								if(!valid){
									c.setComponentError(new UserError(v.getLabel()));
									errorMsg.append("\n").append(v.getLabel());
								}
							}
						}
						
					}
					
				}
				
				
			}else if(component instanceof ImmediateUpload){
				
				ImmediateUpload iu = (ImmediateUpload) component;
				if(iu.isMandatory() && StringUtils.isEmpty(iu.getFileName())){
					iu.setComponentError(new UserError(iu.getLabel() + 
							ProcessbaseApplication.getString("fieldRequired", " is required!")));
					errorMsg.append(iu.getLabel() + 
							ProcessbaseApplication.getString("fieldRequired", " is required!"));
					
				}else if(iu.getValidators() != null){
					for (Validators.Validator v : iu.getValidators().getValidators()) {
						
						//Regex validator
						if(v.getClassname().equals(RegexFieldValidator.class.getName()) 
								&& v.getLabel() != null && iu.getFileName() != null){
							try{
								boolean result = iu.getFileName().matches(v.getLabel());
								if(!result){
									String error = ProcessbaseApplication.getString("errorFileNotCorrect", "File is not correct!");
									iu.setComponentError(new UserError(error));
									errorMsg.append(error);
								}
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
						
					}
				}
			}
		}
		return errorMsg.length()==0?null:errorMsg.toString();
	}

	public String getVariableBound() {
		if(GroovyExpression.isGroovyExpression(widget.getVariableBound()))				
			return TaskManager.stripGroovyExpression(widget.getVariableBound());
		else
			return widget.getVariableBound();
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public boolean isReadOnly(){
		return this.readOnly;
	}
	
	
}
