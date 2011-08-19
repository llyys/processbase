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
package org.processbase.ui.bpm.generator;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.UserError;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
/*
 import ee.smartlink.esteid.EstEidComponent;
 import ee.smartlink.esteid.EstEidComponent.EstEidEvent;
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringEscapeUtils;
import org.h2.util.StringUtils;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.InitialAttachment;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightTaskInstance;
import org.ow2.bonita.util.GroovyException;
import org.ow2.bonita.util.GroovyExpression;
import org.ow2.bonita.util.GroovyUtil;
import org.processbase.ui.bpm.worklist.TaskList;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.ActionType;
import org.processbase.ui.core.bonita.forms.Actions.Action;
import org.processbase.ui.core.bonita.forms.Activities;
import org.processbase.ui.core.bonita.forms.Activities.Activity;
import org.processbase.ui.core.bonita.forms.FieldValue;
import org.processbase.ui.core.bonita.forms.FormsDefinition;
import org.processbase.ui.core.bonita.forms.FormsDefinition.Process;
import org.processbase.ui.core.bonita.forms.PageFlow;
import org.processbase.ui.core.bonita.forms.PageFlow.Pages.Page;
import org.processbase.ui.core.bonita.forms.SelectMode;
import org.processbase.ui.core.bonita.forms.ValuesList;
import org.processbase.ui.core.bonita.forms.VariableType;
import org.processbase.ui.core.bonita.forms.Widget;
import org.processbase.ui.core.bonita.forms.WidgetGroup;
import org.processbase.ui.core.bonita.forms.WidgetType;
import org.processbase.ui.core.bonita.forms.Widgets;
import org.processbase.ui.core.bonita.forms.XMLProcessDefinition;
import org.processbase.ui.core.template.ByteArraySource;
import org.processbase.ui.core.template.HumanTaskWindow;
import org.processbase.ui.core.template.ImmediateUpload;
import org.processbase.ui.core.util.YesNoDialog;

/**
 * 
 * @author marat
 */
public class GeneratedWindow extends HumanTaskWindow implements
		Button.ClickListener {

	private static final String FIELD = "field_";
	private BarResource barResource;
	private FormsDefinition formsDefinition;
	private PageFlow pageFlow;
	private HashMap<Component, Widget> components = new HashMap<Component, Widget>();
	private HashMap<String, Component> fields = new HashMap<String, Component>();
	private ArrayList<GridLayout> pages = new ArrayList<GridLayout>();
	private int currentPage = 0;
	private boolean hasAttachments = false;
	private List<AttachmentInstance> attachmentInstances = null;
	private ArrayList<InitialAttachment> initialAttachments = new ArrayList<InitialAttachment>();
	private Map<AttachmentInstance, byte[]> attachments = new HashMap<AttachmentInstance, byte[]>();
	private Map<String, Object> groovyScripts = new HashMap<String, Object>();
	private Map<String, String> attachmentFileNames = new HashMap<String, String>();

	XMLProcessDefinition _xmlProcess;
	private GridLayout gridLayout;
	private boolean isDynamicForm;

	public GeneratedWindow(String caption) {
		super(caption);
	}

	public Component getComponentByWidgetId(String id) {
		if (fields.containsKey(FIELD + id))
			return fields.get(FIELD + id);
		return null;
	}
	
	public void addCustomComponent(String id, Component component){
		if(!fields.containsKey(id))
		{
			fields.put(FIELD + id, component);
		}
	}

	@Override
	public void initUI() {
		LOGGER.debug("Init UI");
		super.initUI();
		isDynamicForm = false;
		this.addListener(new Window.CloseListener() {
			// inline close-listener
			public void windowClose(CloseEvent e) {
				if (isTaskActive()) {
					getWindow()
							.addWindow(
									new YesNoDialog(
											"Do you want to cancel process?",
											"Please answer yes if you want to remove this process.",
											new YesNoDialog.Callback() {
												public void onDialogResult(
														boolean removeProcess) {
													if (removeProcess) {
														try {
															bpmModule.cancelProcessInstance(taskInstance.getProcessInstanceUUID());
														} catch (Exception e) {
															// TODO
															// Auto-generated
															// catch block
															throw new RuntimeException(e);
														}
													}
													close();
												}
											}));
				}

			}
		});

		setWidth("845px");
		setHeight("90%");
		setResizable(true);
		try {
			if (isTaskActive()) {

				pageFlow = getPageFlow(taskInstance.getActivityName());
			} else if (taskInstance == null) {
				pageFlow = getPageFlow();
			}
			if (pageFlow != null) {
				generateWindow();
			} else {
				showError(ProcessbaseApplication.getCurrent().getPbMessages()
						.getString("ERROR_UI_NOT_DEFINED"));
				close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();

			throw new RuntimeException(ex);
		}
	}

	protected void generateWindow() throws Exception {
		prepareAttachments();
		prepareGroovyScripts();
		// isDynamicForm = false;
		List<Page> pages2 = null;

		if (pageFlow == null) {
			showMessage("Form is not defined, generating dynamic form",
					Notification.TYPE_WARNING_MESSAGE);
			// return;
			pages2 = generateDynamicPage();
			isDynamicForm = true;
		} else {
			pages2 = pageFlow.getPages().getPages();
		}
		if (taskInstance != null)
			addDescription(taskInstance);

		for (Page page : pages2) {
			TableStyle ts = null;
			if (isDynamicForm) {
				gridLayout = new GridLayout(1, page.getWidgets()
						.getWidgetsAndGroups().size());
			} else {
				ts = barResource.getTableStyle(page);
				gridLayout = new GridLayout(ts.getColumns(), ts.getRows());
			}
			gridLayout.setMargin(false, true, true, true);
			gridLayout.setSpacing(true);

			int row1 = 0;
			int row2 = 0;

			for (Object wg : page.getWidgets().getWidgetsAndGroups()) {
				Component c = null;
				if (wg instanceof Widget) {
					Widget widget = (Widget) wg;
					c = getComponent(widget);

					if (c != null) {
						components.put(c, widget);
						fields.put(FIELD + widget.getId(), c);
						int col1 = 0;
						int col2 = 0;
						if (isDynamicForm == false) {
							ComponentStyle componentStyle = ts.getElements().get(widget.getId());
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
					//TODO WidgetGroup
				}
			}
			pages.add(gridLayout);
		}

		taskPanel.setContent(pages.get(currentPage));
		taskPanel.setSizeFull();

		String pageLabel = "";
		if (isDynamicForm)
			pageLabel = taskInstance.getActivityLabel();
		else
			pageLabel = pageFlow.getPages().getPages().get(currentPage)
					.getPageLabel();

		setCaption(pageLabel);
		taskPanel.setCaption(pageLabel);
	}

	public List<Page> generateDynamicPage() {
		List<Page> pages2;
		pages2 = new ArrayList<Page>(1);
		Page pg = new Page();
		pg.setPageLabel(taskInstance.getActivityLabel());
		pg.setWidgets(generateProcessWidgets(processDataFieldDefinitions));
		pages2.add(pg);
		return pages2;
	}

	private Widgets generateProcessWidgets( Map<String, DataFieldDefinition> processDataFieldDefinitions) {
		Widgets widgets = new Widgets();

		for (Entry<String, DataFieldDefinition> object : processDataFieldDefinitions.entrySet()) {
			Widget w = new Widget();
			w.setType(WidgetType.TEXT);
			DataFieldDefinition dataDefinition = object.getValue();

			w.setId(dataDefinition.getName());
			w.setReadonly(true);
			w.setAllowHtmlInField(false);
			w.setVariableBound("${" + dataDefinition.getName() + "}");
			w.setLabel(dataDefinition.getLabel());

			FieldValue value = new FieldValue();
			value.setExpression(w.getVariableBound());

			w.setInitialValue(value);
			widgets.getWidgetsAndGroups().add(w);
		}

		// append last submit button
		Widget submitWidget = new Widget();
		submitWidget.setType(WidgetType.BUTTON_SUBMIT);
		submitWidget.setId("submit");
		submitWidget.setLabelButton(false);
		submitWidget.setAllowHtmlInField(false);
		submitWidget.setLabel("Submit");
		widgets.getWidgetsAndGroups().add(submitWidget);

		return widgets;
	}

	private Component getComponent(Widget widget) {
		Component c = null;
		Object value = null;
		DataFieldDefinition dfd = null;
		Collection options = null;
		try {
			if (widget.getInitialValue() != null
					&& widget.getInitialValue().getExpression() != null) {
				value = groovyScripts.get(widget.getInitialValue()
						.getExpression());

				if (value instanceof Component)// if value is vaadin component return component instance
					return (Component) value;
			}
			if (widget.getAvailableValues() != null) {
				if (widget.getAvailableValues().getExpression() != null) {
					options = (Collection) groovyScripts.get(widget
							.getAvailableValues().getExpression());
				} else if (!widget.getAvailableValues().getValuesList()
						.getAvailableValues().isEmpty()) {
					options = new ArrayList<String>();
					for (ValuesList.AvailableValue avalue : widget
							.getAvailableValues().getValuesList()
							.getAvailableValues()) {
						options.add(avalue.getValue());
					}
				}
			}
			if (widget.getType().equals(WidgetType.MESSAGE)) {
				c = getLabel(widget, value);
			}
			if (widget.getType().equals(WidgetType.TEXTBOX)) {
				c = getTextField(widget);
			}
			if (widget.getType().equals(WidgetType.DATE)) {
				c = getPopupDateField(widget);
			}
			if (widget.getType().equals(WidgetType.TEXTAREA)) {
				c = getTextArea(widget);
			}
			if (widget.getType().equals(WidgetType.RICH_TEXTAREA)) {
				c = getRichTextArea(widget);
			}
			if (widget.getType().equals(WidgetType.TEXT)) {
				c = getTextField(widget);
			}
			if (widget.getType().equals(WidgetType.PASSWORD)) {
				c = getPasswordField(widget);
			}

			if (widget.getType().equals(WidgetType.LISTBOX_SIMPLE)) {
				c = getNativeSelect(widget, options);
			}
			if (widget.getType().equals(WidgetType.SUGGESTBOX)) {
				c = getComboBox(widget, options);
			}
			if (widget.getType().equals(WidgetType.RADIOBUTTON_GROUP)) {
				c = getOptionGroup(widget, options);
			}
			if (widget.getType().equals(WidgetType.LISTBOX_MULTIPLE)) {
				c = getListSelect(widget, options);
			}
			if (widget.getType().equals(WidgetType.CHECKBOX)) {
				c = getCheckBox(widget);
			}
			if (widget.getType().equals(WidgetType.EDITABLE_GRID)) {
				c = new GeneratedTable(widget, value, groovyScripts);
			}
			if (widget.getType().equals(WidgetType.CHECKBOX_GROUP)) {
				c = getOptionGroup(widget, options);
			}
			if (widget.getType().equals(WidgetType.FILEUPLOAD)) {
				hasAttachments = true;
				c = getUpload(widget);
			}
			if (widget.getType().equals(WidgetType.FILEDOWNLOAD)) {
				hasAttachments = true;
				c = getDownload(widget);
			}
			if (widget.getType().equals(WidgetType.BUTTON_SUBMIT)
					|| widget.getType().equals(WidgetType.BUTTON_NEXT)
					|| widget.getType().equals(WidgetType.BUTTON_PREVIOUS)) {
				c = getButton(widget);
			}

			c = (c != null) ? c : new Label("");
			// add general atrubutes
			// setWidth
			// if (!(component instanceof Button)) {
			// component.setWidth((widget.getInputWidth() != null) ?
			// widget.getInputWidth() : "200px");
			// } else if ((component instanceof Button) &&
			// widget.getInputWidth() != null) {
			// component.setWidth(widget.getInputWidth());
			// }
			// setHeght
			// if (widget.getInputHeight() != null) {
			// component.setHeight(widget.getInputHeight());
			// }

			if (!(c instanceof Button)
					&& c instanceof AbstractField) {
				if (value != null) {
					((AbstractField) c).setValue(value);
				}
				
				if (widget.isMandatory() != null) {
					((AbstractField) c).setRequired(widget.isMandatory());
				}
				((AbstractField) c).setRequiredError(widget.getLabel() + ProcessbaseApplication.getCurrent().getPbMessages().getString("fieldRequired"));
				((AbstractField) c).setDescription(widget.getTitle() != null ? widget.getTitle() : "");
				((AbstractField) c).setInvalidCommitted(false);
				((AbstractField) c).setWriteThrough(false);
			}
			if (widget.isReadonly() != null) {
				c.setReadOnly(widget.isReadonly());
			}
			return c;
		} catch (Exception ex) {
			ex.printStackTrace();
			
		}
		return new Label("");
	}

	private Component getDownload(Widget widget) {

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
					// TODO Auto-generated method stub
					Widget w = getWidgets(event.getButton());
					byte[] bytes;
					try {
						String processUUID = taskInstance.getProcessInstanceUUID().toString();
						String fileName = attachmentFileNames.get(w.getInitialValue().getExpression());
						bytes = getBpmModule().getAttachmentValue(processUUID,w.getVariableBound());
						ByteArraySource bas = new ByteArraySource(bytes);

						StreamResource streamResource = new StreamResource(bas,fileName, getApplication());
						streamResource.setCacheTime(50000); // no cache (<=0)
															// does not work
															// with IE8
						streamResource.setMIMEType("application/octet-stream");
						getWindow().getWindow().open(streamResource, "_new");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		return b;

	}

	private TextField getTextField(Widget widget) {
		TextField component = new TextField(widget.getLabel());
		// if (widget.getValidators() != null) {
		// component.addValidator(new GeneratedValidator(widget, taskInstance,
		// processDefinition, getApplication().getLocale(),
		// ProcessbaseApplication.getCurrent().getBpmModule()));
		// } else if (dfd != null &&
		// dfd.getDataTypeClassName().equals("java.lang.Double")) {
		// component.addValidator(
		// new DoubleValidator((widget.getLabel() != null ? widget.getLabel() :
		// widget.getName()) + " "
		// +
		// ProcessbaseApplication.getCurrent().getMessages().getString("validatorDoubleError")));
		// } else if (dfd != null &&
		// dfd.getDataTypeClassName().equals("java.lang.Long")) {
		// component.addValidator(
		// new LongValidator((widget.getLabel() != null ? widget.getLabel() :
		// widget.getName()) + " "
		// +
		// ProcessbaseApplication.getCurrent().getMessages().getString("validatorIntegerError")));
		// }
		component.setNullRepresentation("");
		component.setWidth("400px");
		return component;
	}

	private TextArea getTextArea(Widget widget) {
		TextArea component = new TextArea(widget.getLabel());
		// if (widget.getValidatorName() != null) {
		// component.addValidator(new GeneratedValidator(widget, taskInstance,
		// processDefinition, getApplication().getLocale(),
		// ProcessbaseApplication.getCurrent().getBpmModule()));
		// }
		component.setNullRepresentation("");
		return component;
	}

	private TextField getPasswordField(Widget widget) {
		TextField component = new TextField(widget.getLabel());
		component.setNullRepresentation("");
		return component;
	}

	private Label getLabel(Widget widget, Object value) {
		String escaped = StringEscapeUtils.unescapeHtml(value.toString());
		Label component = new Label(escaped);
		component.setContentMode(Label.CONTENT_XHTML);
		return component;
	}

	private PopupDateField getPopupDateField(Widget widget) {
		PopupDateField component = new PopupDateField(widget.getLabel());
		component.setResolution(PopupDateField.RESOLUTION_DAY);
		return component;
	}

	private RichTextArea getRichTextArea(Widget widget) {
		RichTextArea component = new RichTextArea(widget.getLabel());
		component.setNullRepresentation("");
		return component;
	}

	private NativeSelect getNativeSelect(Widget widget, Collection options) {
		NativeSelect component = new NativeSelect(widget.getLabel(), options);
		return component;
	}

	private ComboBox getComboBox(Widget widget, Collection options) {
		ComboBox component = new ComboBox(widget.getLabel(), options);
		component.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		return component;
	}

	private OptionGroup getOptionGroup(Widget widget, Collection options) {
		OptionGroup component = new OptionGroup(widget.getLabel(), options);
		if (widget.getSelectMode() != null
				&& widget.getSelectMode().equals(SelectMode.MULTIPLE)) {
			component.setMultiSelect(true);
		}
		return component;
	}

	private ListSelect getListSelect(Widget widget, Collection options) {
		ListSelect component = new ListSelect(widget.getLabel(), options);
		component.setMultiSelect(true);
		return component;
	}

	private CheckBox getCheckBox(Widget widget) {
		CheckBox component = new CheckBox(widget.getLabel());
		return component;
	}

	private Table getTable(Widget widget, Collection options) {
		Table component = new Table(widget.getLabel());
		component.setMultiSelect(true);
		return component;
	}

	private ImmediateUpload getUpload(Widget widget) {
		ImmediateUpload component = null;
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
			} else {

			}
			// ai =
			// findAttachmentInstance(widget.getInitialValue().getExpression());
		}
		component = new ImmediateUpload(processUUID, widget.getLabel(), widget.getInitialValue().getExpression(), fileName, hasFile, widget.isReadonly(), ProcessbaseApplication.getCurrent().getPbMessages());
		
		return component;
	}

	private Button getButton(Widget widget) {
		Button component = new Button(widget.getLabel());
		component.addListener((Button.ClickListener) this);
		if (widget.isLabelButton()) {
			component.setStyleName(Reindeer.BUTTON_LINK);
		}
		return component;
	}

	private TaskList taskList;

	@Override
	public void buttonClick(ClickEvent event) {
		super.buttonClick(event);
		try {
			if (components.containsKey(event.getButton())) {
				Button btn = event.getButton();

				if (getWidgets(btn).getType().equals(WidgetType.BUTTON_SUBMIT)) {
					submitPage();

				} else if (getWidgets(btn).getType().equals(
						WidgetType.BUTTON_NEXT)) {
					commitPage(pages.get(currentPage));
					currentPage = (pages.size() > (currentPage + 1)) ? currentPage + 1
							: currentPage;
					taskPanel.setContent(pages.get(currentPage));
					taskPanel.setCaption(pageFlow.getPages().getPages()
							.get(currentPage).getPageLabel());
				} else if (getWidgets(btn).getType().equals(
						WidgetType.BUTTON_PREVIOUS)) {
					commitPage(pages.get(currentPage));
					currentPage = (currentPage != 0) ? currentPage - 1
							: currentPage;
					taskPanel.setContent(pages.get(currentPage));
					taskPanel.setCaption(pageFlow.getPages().getPages()
							.get(currentPage).getPageLabel());
				}
			}
		} catch (InvalidValueException ex) {
			ex.printStackTrace();
			showMessage(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			throw new RuntimeException(ex);
		} catch (Exception ex) {
			ex.printStackTrace();
			showMessage(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			throw new RuntimeException(ex);
		}
	}

	public void submitPage() throws Exception, ProcessNotFoundException,
			VariableNotFoundException, TaskNotFoundException,
			IllegalTaskStateException, InstanceNotFoundException {
		commit();
		setProcessVariables();
		ProcessInstanceUUID piUUID = null;
		if (taskInstance == null) {
			if (hasAttachments) {
				prepareInitialAttachmentsToSave();
			}
			piUUID = getBpmModule().startNewProcess(processDefinition.getUUID(),processInstanceVariables);
			//if (ProcessbaseApplication.getCurrent().getApplicationType() == ProcessbaseApplication.LIFERAY_PORTAL) {
				saveAttachmentsToPortal(piUUID);
			//}

		} else {
			if (hasAttachments) {
				prepareAttachmentsToSave();
			}
			//if (ProcessbaseApplication.getCurrent().getApplicationType() == ProcessbaseApplication.LIFERAY_PORTAL) {
				saveAttachmentsToPortal(taskInstance.getProcessInstanceUUID());
			//}
			piUUID = taskInstance.getProcessInstanceUUID();
			getBpmModule().finishTask(taskInstance, true, processInstanceVariables, activityInstanceVariables, attachments);
		}
		if(pageFlow!=null)
		{
			String confirmationMessage = pageFlow.getConfirmationMessage();
			if (!StringUtils.isNullOrEmpty(confirmationMessage)) {
				showMessage(evalGroovyScript(confirmationMessage),
						Notification.TYPE_HUMANIZED_MESSAGE);
			}
		}
		taskInstance = bpmModule.nextUserTask(piUUID, getCurrentUser());
		if (taskInstance != null) // if there is a next task to be executed
		{
			pageFlow = getPageFlow(taskInstance.getActivityName());
			isDynamicForm = pageFlow == null;
			currentPage = 0;
			pages.clear();
			// if(pageFlow!=null)
			generateWindow();
			/*
			 * else { generatePageFlowBasedActivityData(taskInstance)
			 * showMessage(
			 * "Process design error, possibly no form attached to user process step"
			 * , Notification.TYPE_ERROR_MESSAGE); close(); }
			 */
		} else {
			if (taskList != null)
				taskList.refreshTable();
			close();
		}
	}

	private BPMModule getBpmModule() {
		return ProcessbaseApplication.getCurrent().getBpmModule();
	}

	private void commit() {
		for (GridLayout grid : pages) {
			commitPage(grid);
		}
	}

	private void commitPage(GridLayout page) {
		for (Iterator<Component> iterator = page.getComponentIterator(); iterator
				.hasNext();) {
			Component comp = iterator.next();
			if (comp instanceof AbstractField) {
				try {
					((AbstractField) comp).setComponentError(null);
					((AbstractField) comp).validate();
				} catch (InvalidValueException ex) {
					if (ex instanceof EmptyValueException) {
						((AbstractField) comp).setComponentError(new UserError(
								((AbstractField) comp).getRequiredError()));
					}
					throw ex;
				}

			}
		}
	}

	private void setProcessVariables() throws Exception {
		Map<String, Object> piVariablesTemp = new HashMap<String, Object>();
		Map<String, Object> aiVariablesTemp = new HashMap<String, Object>();
		if (isDynamicForm)
			return;

		for (Page page : pageFlow.getPages().getPages()) {
			if (page.getActions() != null) {
				for (Action action : page.getActions().getActions()) {
					if (action.getType().equals(ActionType.SET_VARIABLE)) {
						Component comp = null;
						Object value = null;
						if (action.getExpression().startsWith("field")) {
							comp = fields.get(action.getExpression());
							if (comp instanceof AbstractField) {
								value = ((AbstractField) comp).getValue();
							}
							if (comp instanceof GeneratedTable) {
								value = ((GeneratedTable) comp).getTableValue();
							}
							if (comp instanceof CheckBox) {
								value = ((CheckBox) comp).booleanValue();
							}
						} else {
							value = action.getExpression();
						}
						if (action.getVariableType().equals(
								VariableType.PROCESS_VARIABLE)) {
							piVariablesTemp.put(action.getVariable(), value);
						} else if (action.getVariableType().equals(
								VariableType.ACTIVITY_VARIABLE)) {
							aiVariablesTemp.put(action.getVariable(), value);
						}
					}
				}
			}
		}
		processInstanceVariables = piVariablesTemp;
		activityInstanceVariables = aiVariablesTemp;
	}

	private void prepareAttachmentsToSave() {
		// try {
		// attachments.clear();
		// for (Component comp : components.keySet()) {
		// XMLWidgetsDefinition widgets = components.get(comp);
		// if (widgets.getSetVarScript() != null) {
		// if (widgets.getType().equals("form:FileWidget") && ((ImmediateUpload)
		// comp).isNeedToSave()) {
		// ImmediateUpload ui = (ImmediateUpload) comp;
		// AttachmentInstanceImpl ai = new AttachmentInstanceImpl(
		// widgets.getInputScript(),
		// taskInstance.getProcessInstanceUUID(),
		// ProcessbaseApplication.getCurrent().getUserName(),
		// new Date());
		// ai.setFileName(ui.getFileName());
		// if (ProcessbaseApplication.getCurrent().getApplicationType() ==
		// Processbase.LIFERAY_PORTAL) {
		// attachments.put(ai, new byte[0]);
		// } else {
		// attachments.put(ai, ui.getFileBody());
		// }
		//
		// }
		// }
		// }
		// } catch (Exception ex) {
		// Logger.getLogger(GeneratedWindow2.class.getName()).log(Level.SEVERE,
		// ex.getMessage());
		// }
	}

	private void prepareInitialAttachmentsToSave() {
		// try {
		// attachments.clear();
		// for (Component comp : components.keySet()) {
		// XMLWidgetsDefinition widgets = components.get(comp);
		// if (widgets.getSetVarScript() != null) {
		// if (widgets.getType().equals("form:FileWidget") && ((ImmediateUpload)
		// comp).isNeedToSave()) {
		// ImmediateUpload ui = (ImmediateUpload) comp;
		// AttachmentInstanceImpl ai = new AttachmentInstanceImpl(
		// widgets.getInputScript(),
		// taskInstance.getProcessInstanceUUID(),
		// ProcessbaseApplication.getCurrent().getUserName(),
		// new Date());
		// ai.setFileName(ui.getFileName());
		// if (ProcessbaseApplication.getCurrent().getApplicationType() ==
		// Processbase.LIFERAY_PORTAL) {
		// attachments.put(ai, new byte[0]);
		// } else {
		// attachments.put(ai, ui.getFileBody());
		// }
		//
		// }
		// }
		// }
		// } catch (Exception ex) {
		// Logger.getLogger(GeneratedWindow2.class.getName()).log(Level.SEVERE,
		// ex.getMessage());
		// }
	}

	private void saveAttachmentsToPortal(ProcessInstanceUUID processUUID) {
		try {
			for (Component comp : components.keySet()) {
				Widget widget = components.get(comp);
				if (widget.getVariableBound() != null) {
					if (widget.getType().equals(WidgetType.FILEUPLOAD)
							&& ((ImmediateUpload) comp).isNeedToSave()) {
						ImmediateUpload ui = (ImmediateUpload) comp;
						getBpmModule().addAttachment(processUUID, widget.getVariableBound(), ui.getFileName(), ui.getMimeType(), ui.getFileBody());
						//ProcessbaseApplication.getCurrent().saveFile(processUUID, widget.getVariableBound(), ui.getFileName(), ui.getFileBody());
					}
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(GeneratedWindow.class.getName()).log(Level.SEVERE,
					ex.getMessage());
		}
	}

	private void prepareAttachments() {
		if (taskInstance != null) {
			try {
				if (ProcessbaseApplication.getCurrent().getApplicationType() == ProcessbaseApplication.STANDALONE) {
					
					ProcessInstance pi = getBpmModule().getProcessInstance(taskInstance.getProcessInstanceUUID());
					
					Set<String> names = new HashSet<String>();
					for (AttachmentInstance ai : pi.getAttachments()) {
						names.add(ai.getName());
					}
					attachmentInstances = getBpmModule().getLastAttachments(taskInstance.getProcessInstanceUUID(),names);

					for (AttachmentInstance ai : attachmentInstances) {
						attachmentFileNames.put(ai.getName(), ai.getFileName());
					}
				} else if (ProcessbaseApplication.getCurrent().getApplicationType() == ProcessbaseApplication.LIFERAY_PORTAL) {
					attachmentFileNames = ProcessbaseApplication.getCurrent().getFileList(taskInstance.getProcessInstanceUUID().toString());
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
		} else {
			// processDefinition.
		}
	}

	private Widget getWidgets(Component c) {
		for (Component comp : components.keySet()) {
			if (comp.equals(c)) {
				return components.get(comp);
			}
		}
		return null;
	}

	private void prepareGroovyScripts() throws Exception {
		if(taskInstance!=null)
		{
			processInstanceVariables.clear();
			processInstanceVariables.putAll(bpmModule.getProcessInstanceVariables(taskInstance.getProcessInstanceUUID()));
		}
		HashSet<String> expressions = new HashSet<String>();
		HashMap<String, String> scripts = new HashMap<String, String>();
		HashMap<String, String> strings = new HashMap<String, String>();
		List<Page> pages_ = null;
		if (isDynamicForm)
			pages_ = generateDynamicPage();
		else
			pages_ = pageFlow.getPages().getPages();

		for (Page page : pages_) {
			for (Object wg : page.getWidgets().getWidgetsAndGroups()) {
				if (wg instanceof Widget) {
					Widget widget = (Widget) wg;
					if (widget.getInitialValue() != null
							&& widget.getInitialValue().getExpression() != null) {
						expressions.add(widget.getInitialValue()
								.getExpression());
					}
					if (widget.getAvailableValues() != null
							&& widget.getAvailableValues().getExpression() != null) {
						expressions.add(widget.getAvailableValues()
								.getExpression());
					}
					if (widget.getHorizontalHeader() != null) {
						expressions.add(widget.getHorizontalHeader());
					}
				} else if (wg instanceof WidgetGroup) {
					// not yet implemented
				}
			}
		}
		for (String expression : expressions) {
			// System.out.println("expression = " + expression);
			if (expression != null && !expression.isEmpty()) {
				int begin = expression
						.indexOf(GroovyExpression.START_DELIMITER);
				int end = expression.indexOf(GroovyExpression.END_DELIMITER);
				if (begin >= end) {
					strings.put(expression, expression);
				} else {
					scripts.put(expression, expression);
				}
			}
		}
		Map<String, Object> context = new Hashtable<String, Object>();
		context.put("parent", this);// for groovy component hack

		if (taskInstance != null && !scripts.isEmpty()) {
			groovyScripts = getBpmModule()
					.evaluateGroovyExpressions(scripts, taskInstance.getUUID(),
							context, false, false);
		} else if (taskInstance == null && !scripts.isEmpty()) {
			groovyScripts = getBpmModule()
					.evaluateGroovyExpressions(scripts,
							processDefinition.getUUID(), context, true);
		}
		for (String string : strings.keySet()) {
			groovyScripts.put(string, strings.get(string));
		}
		//
		// for (String key : groovyScripts.keySet()){
		// System.out.println(key + " = " + groovyScripts.get(key) + " " +
		// groovyScripts.get(key).getClass().getCanonicalName());
		// }
	}

	private boolean hasGroovyScript(String expression) {
		int begin = expression.indexOf(GroovyExpression.START_DELIMITER);
		int end = expression.indexOf(GroovyExpression.END_DELIMITER);
		return begin <= end && begin != -1 && end != -1;
	}

	private String evalGroovyScript(String script) {
		int begin = script.indexOf(GroovyExpression.START_DELIMITER);
		int end = script.indexOf(GroovyExpression.END_DELIMITER);
		if (begin != -1 && end != -1) {
			HashMap<String, String> scripts = new HashMap<String, String>();
			scripts.put(script, script);
			Map<String, Object> resultGroovyScripts = new HashMap<String, Object>();
			try {
				Map<String, Object> context = new Hashtable<String, Object>();
				context.put("parent", this);// for groovy component hack

				if (taskInstance != null && !script.isEmpty()) {
					resultGroovyScripts = getBpmModule()
							.evaluateGroovyExpressions(scripts,
									taskInstance.getUUID(), context, false,
									false);
				} else if (taskInstance == null && !script.isEmpty()) {
					resultGroovyScripts = getBpmModule()
							.evaluateGroovyExpressions(scripts,
									processDefinition.getUUID(), context, true);
				}
				script = "";
				for (String iterable_element : resultGroovyScripts.keySet()) {
					script += resultGroovyScripts.get(iterable_element);
				}
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProcessNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GroovyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ActivityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return script;

	}

	private String getPureScript(String script) {
		script = script.replace(GroovyExpression.START_DELIMITER, "");
		int end = script.indexOf(GroovyExpression.END_DELIMITER);
		return script.substring(0, end > 0 ? end : script.length());
	}

	public void setBarResource(BarResource barResource) {
		this.barResource = barResource;
		formsDefinition = this.barResource.getFormsDefinition();
	}

	public void setXMLProcessDefinition(XMLProcessDefinition xmlProcess) {
		_xmlProcess = xmlProcess;
	}

	private PageFlow getPageFlow() {
		Process process = formsDefinition.getProcesses().get(0);

		PageFlow flow = process.getPageflow();
		if (flow == null) {
			for (Activity activity : process.getActivities().getActivities()) {
				if (activity.getPageflow() != null) {
					flow = activity.getPageflow();
					break;
				}
			}
		}
		if (flow.getPages() != null) // if there is no initial pageflow try
										// activity
			return flow;
		// try to find first activity if there is pageflow to execute
		// return process.getActivities().getActivities().get(0).getPageflow();
		ProcessInstanceUUID piUUID;
		try {
			piUUID = bpmModule.startNewProcess(processDefinition.getUUID());
			taskInstance = bpmModule.nextUserTask(piUUID, getCurrentUser());
			if (taskInstance != null) {
				return getPageFlow(taskInstance.getActivityName());
			}
		} catch (ProcessNotFoundException e) {
			showError(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (VariableNotFoundException e) {
			// TODO Auto-generated catch block
			showError(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			showError(e.getMessage());
			e.printStackTrace();
			return null;
		}

		return null;
	}

	private PageFlow getPageFlow(String activityName) {
		for (Activities.Activity a : formsDefinition.getProcesses().get(0)
				.getActivities().getActivities()) {
			if (a.getName().equals(activityName)) {
				return a.getPageflow();
			}
		}
		return null;
	}

	public void setTaskList(TaskList taskList) {
		this.taskList = taskList;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public Object getProcessValue(String key){
		
		if(processInstanceVariables.containsKey(key))
		{
			return processInstanceVariables.get(key);
		}
		return null;
	}
	
	private boolean isTaskActive() {
		return !(taskInstance == null
				|| taskInstance.getState().equals(ActivityState.FINISHED)
				|| taskInstance.getState().equals(ActivityState.ABORTED) || taskInstance
				.getState().equals(ActivityState.CANCELLED));
	}
}
