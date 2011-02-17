/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
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
package org.processbase.ui.generator;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.DoubleValidator;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.Reindeer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.GroovyExpression;
import org.processbase.bpm.forms.XMLActionDefinition;
import org.processbase.bpm.forms.XMLFormDefinition;
import org.processbase.bpm.forms.XMLProcessDefinition;
import org.processbase.bpm.forms.XMLWidgetsDefinition;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.HumanTaskWindow;
import org.processbase.ui.template.ImmediateUpload;
import org.processbase.ui.template.LongValidator;

/**
 *
 * @author marat
 */
public class GeneratedWindow extends HumanTaskWindow implements Button.ClickListener {

    private HashMap<Component, XMLWidgetsDefinition> components = new HashMap<Component, XMLWidgetsDefinition>();
    private XMLProcessDefinition xmlProcess;
    private ArrayList<GridLayout> pages = new ArrayList<GridLayout>();
    private ArrayList<XMLFormDefinition> forms;
    private int currentPage = 0;
    private boolean hasAttachments = false;
    private List<DLFileEntry> processFiles = null;
    private Map<String, Object> groovyScripts = new HashMap<String, Object>();

    public GeneratedWindow(String caption) {
        super(caption);
    }

    @Override
    public void initUI() {
        super.initUI();
        try {
            if (taskInstance != null && !taskInstance.getState().equals(ActivityState.FINISHED) && !taskInstance.getState().equals(ActivityState.ABORTED) && !taskInstance.getState().equals(ActivityState.CANCELLED)) {
                generateWindow();
            } else if (taskInstance == null) {
                generateWindow();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(GeneratedWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    protected void generateWindow() throws Exception {
        forms = taskInstance == null ? xmlProcess.getForms() : xmlProcess.getTasks().get(taskInstance.getActivityName()).getForms();
        prepareAttachments();
        prepareGroovyScripts();
        for (XMLFormDefinition form : forms) {
            GridLayout page = new GridLayout(form.getnColumn(), form.getnLine());
            page.setMargin(false, true, true, true);
            page.setSpacing(true);
            pages.add(page);

            ArrayList<XMLWidgetsDefinition> widgetsList = form.getWidgets();
            for (XMLWidgetsDefinition widgets : widgetsList) {
                Component component = getComponent(widgets);
                if (component != null) {
                    components.put(component, widgets);
                    page.addComponent(component, widgets.getColumn(), widgets.getLine(), widgets.getColumn() + (widgets.getHorizontalSpan() > 0 ? widgets.getHorizontalSpan() - 1 : 0), widgets.getLine());
                }
            }
        }
        taskPanel.setContent(pages.get(currentPage));
        taskPanel.setCaption(forms.get(currentPage).getLabel());
    }

    private Component getComponent(XMLWidgetsDefinition widgets) {
        Component component = null;
        Object value = null;
        DataFieldDefinition dfd = null;
        Collection options = null;
        try {
            // define lists
            if (widgets.getType().equals("form:SelectFormField")
                    || widgets.getType().equals("form:RadioFormField")
                    || widgets.getType().equals("form:CheckBoxMultipleFormField")
                    || widgets.getType().equals("form:ListFormField")
                    || widgets.getType().equals("form:SuggestBox")) {
                // set list options
                if ((widgets.getInputScript() == null || widgets.getInputScript().isEmpty())
                        && widgets.getEnum1() != null) {
                    if (taskInstance != null) {
                        String varName = xmlProcess.getDatas().get(widgets.getEnum1()).getName();
                        if (processDataFieldDefinitions.containsKey(varName)) {
                            dfd = processDataFieldDefinitions.get(varName);
                        } else if (activityDataFieldDefinitions.containsKey(varName)) {
                            dfd = activityDataFieldDefinitions.get(varName);
                        }
                        options = dfd.getEnumerationValues();

                        if (processInstanceVariables.containsKey(dfd.getName())) {
                            value = processInstanceVariables.get(dfd.getName());
                        } else if (activityInstanceVariables.containsKey(dfd.getName())) {
                            value = activityInstanceVariables.get(dfd.getName());
                        }
                    } else if (taskInstance == null) {
                        String varName = xmlProcess.getDatas().get(widgets.getEnum1()).getName();
                        if (processDataFieldDefinitions.containsKey(varName)) {
                            dfd = processDataFieldDefinitions.get(varName);
                        }
                        options = dfd.getEnumerationValues();
                        value = dfd.getInitialValue();
                    }
                } else if (GroovyExpression.isGroovyExpression(widgets.getInputScript())) {
                    if (taskInstance != null) {
                        options = (Collection) groovyScripts.get(widgets.getInputScript());
                    } else if (taskInstance == null) {
                        options = (Collection) groovyScripts.get(widgets.getInputScript());
                    }
                } else {
//                    options = widgets.getInputScript();
                }
                value = groovyScripts.get(widgets.getDefaultValue());

            } else if (widgets.getType().equals("form:Table") || widgets.getType().equals("form:DynamicTable")) {
                // define not lists
            } else {
                if (taskInstance != null && widgets.getInputScript() != null && !widgets.getInputScript().isEmpty()) {
                    value = groovyScripts.get(widgets.getInputScript());
                } else if (taskInstance != null && widgets.getDefaultValue() != null && !widgets.getDefaultValue().isEmpty()) {
                    value = groovyScripts.get(widgets.getDefaultValue());
                } else if (taskInstance == null && widgets.getInputScript() != null && !widgets.getInputScript().isEmpty()) {
                    value = groovyScripts.get(widgets.getInputScript());
                }
                if (widgets.getSetVarScript() != null
                        && !GroovyExpression.isGroovyExpression(widgets.getSetVarScript())) {
                    if (processDataFieldDefinitions.containsKey(widgets.getSetVarScript())) {
                        dfd = processDataFieldDefinitions.get(widgets.getSetVarScript());
                    } else if (activityDataFieldDefinitions.containsKey(widgets.getSetVarScript())) {
                        dfd = activityDataFieldDefinitions.get(widgets.getSetVarScript());
                    }
                }
            }
//            System.out.println(widgets.getName() + " " + (value != null ? (value + " " + value.getClass().getName()) : ""));

            // define UI components
            if (widgets.getType().equals("form:TextFormField")) {
                component = getTextField(widgets, value, dfd, false);
            }
            if (widgets.getType().equals("form:DateFormField")) {
                component = getPopupDateField(widgets, value);
            }
            if (widgets.getType().equals("form:TextAreaFormField")) {
                component = getTextArea(widgets, value, dfd, false);
            }
            if (widgets.getType().equals("form:RichTextAreaFormField")) {
                component = getRichTextArea(widgets, value);
            }
            if (widgets.getType().equals("form:TextInfo")) {
                component = getTextField(widgets, value, dfd, true);
            }
            if (widgets.getType().equals("form:PasswordFormField")) {
                component = getPasswordField(widgets, value, dfd, false);
            }
            if (widgets.getType().equals("form:MessageInfo")) {
                component = getLabel(widgets, value);
            }
            if (widgets.getType().equals("form:SelectFormField")) {
                component = getNativeSelect(widgets, options, value);
            }
            if (widgets.getType().equals("form:SuggestBox")) {
                component = getComboBox(widgets, options, value);
            }
            if (widgets.getType().equals("form:RadioFormField")) {
                component = getOptionGroup(widgets, options, value, false);
            }
            if (widgets.getType().equals("form:ListFormField")) {
                component = getListSelect(widgets, options, value);
            }
            if (widgets.getType().equals("form:CheckBoxSingleFormField")) {
                component = getCheckBox(widgets, value);
            }
            if (widgets.getType().equals("form:Table") || widgets.getType().equals("form:DynamicTable")) {
                component = new GeneratedTable(widgets, taskInstance, processDefinition);
            }
            if (widgets.getType().equals("form:CheckBoxMultipleFormField")) {
                component = getOptionGroup(widgets, options, value, true);
            }
            if (widgets.getType().equals("form:FileWidget")) {
                hasAttachments = true;
                component = getUpload(widgets);
            }
            if (widgets.getType().equals("form:SubmitFormButton")
                    || widgets.getType().equals("form:NextFormButton")
                    || widgets.getType().equals("form:PreviousFormButton")) {
                component = getButton(widgets);
            }

            component = (component != null) ? component : new Label("");
            // add general atrubutes
            // setWidth
            if (!(component instanceof Button)) {
                component.setWidth((widgets.getInputWidth() != null) ? widgets.getInputWidth() : "200px");
            } else if ((component instanceof Button) && widgets.getInputWidth() != null) {
                component.setWidth(widgets.getInputWidth());
            }
            // setHeght
            if (widgets.getInputHeight() != null) {
                component.setHeight(widgets.getInputHeight());
            }


            component.setReadOnly(widgets.getReadOnly() != null ? widgets.getReadOnly() : false);
            if (component instanceof AbstractField) {
                ((AbstractField) component).setRequired(widgets.getMandatory() != null ? widgets.getMandatory() : false);
                ((AbstractField) component).setRequiredError(widgets.getDisplayLabel() + PbPortlet.getCurrent().messages.getString("fieldRequired"));
                ((AbstractField) component).setDescription(widgets.getTooltip() != null ? widgets.getTooltip() : "");
                ((AbstractField) component).setInvalidCommitted(false);
                ((AbstractField) component).setWriteThrough(false);
            }

            return component;
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(GeneratedWindow.class.getName()).log(Level.SEVERE, "ERROR: " + ex.getMessage());
        }
        return new Label("");
    }

    private TextField getTextField(XMLWidgetsDefinition widgets, Object value, DataFieldDefinition dfd, boolean readOnly) {
        TextField component = new TextField(widgets.getDisplayLabel());
        if (widgets.getValidatorName() != null) {
            component.addValidator(new GeneratedValidator(widgets, taskInstance, processDefinition));
        } else if (dfd != null && dfd.getDataTypeClassName().equals("java.lang.Double")) {
            component.addValidator(
                    new DoubleValidator((widgets.getLabel() != null ? widgets.getLabel() : widgets.getName()) + " "
                    + PbPortlet.getCurrent().messages.getString("validatorDoubleError")));
        } else if (dfd != null && dfd.getDataTypeClassName().equals("java.lang.Long")) {
            component.addValidator(
                    new LongValidator((widgets.getLabel() != null ? widgets.getLabel() : widgets.getName()) + " "
                    + PbPortlet.getCurrent().messages.getString("validatorIntegerError")));
        }
        component.setValue(value);
//        System.out.println(widgets.getDisplayLabel()+" = " + (value!=null? component.getValue().getClass():""));
        component.setNullRepresentation("");
        component.setReadOnly(readOnly);
        return component;
    }

    private TextArea getTextArea(XMLWidgetsDefinition widgets, Object value, DataFieldDefinition dfd, boolean readOnly) {
        TextArea component = new TextArea(widgets.getDisplayLabel());
        if (widgets.getValidatorName() != null) {
            component.addValidator(new GeneratedValidator(widgets, taskInstance, processDefinition));
        }
        component.setValue(value);
        component.setNullRepresentation("");
        component.setReadOnly(readOnly);
        return component;
    }

    private TextField getPasswordField(XMLWidgetsDefinition widgets, Object value, DataFieldDefinition dfd, boolean readOnly) {
        TextField component = new TextField(widgets.getDisplayLabel());
        if (widgets.getValidatorName() != null) {
            component.addValidator(new GeneratedValidator(widgets, taskInstance, processDefinition));
        } else if (dfd != null && dfd.getDataTypeClassName().equals("java.lang.Double")) {
            component.addValidator(
                    new DoubleValidator((widgets.getLabel() != null ? widgets.getLabel() : widgets.getName()) + " "
                    + PbPortlet.getCurrent().messages.getString("validatorDoubleError")));
        } else if (dfd != null && dfd.getDataTypeClassName().equals("java.lang.Long")) {
            component.addValidator(
                    new LongValidator((widgets.getLabel() != null ? widgets.getLabel() : widgets.getName()) + " "
                    + PbPortlet.getCurrent().messages.getString("validatorIntegerError")));
        }
        component.setValue(value);
//        System.out.println(widgets.getDisplayLabel()+" = " + (value!=null? component.getValue().getClass():""));
        component.setNullRepresentation("");
        component.setReadOnly(readOnly);
        return component;
    }

    private Label getLabel(XMLWidgetsDefinition widgets, Object value) {
        Label component = new Label(value.toString());
        component.setContentMode(Label.CONTENT_XHTML);
        return component;
    }

    private PopupDateField getPopupDateField(XMLWidgetsDefinition widgets, Object value) {
        PopupDateField component = new PopupDateField(widgets.getDisplayLabel());
        component.setValue(value);
        component.setResolution(PopupDateField.RESOLUTION_DAY);
        return component;
    }

    private RichTextArea getRichTextArea(XMLWidgetsDefinition widgets, Object value) {
        RichTextArea component = new RichTextArea(widgets.getDisplayLabel());
        component.setValue(value);
        component.setNullRepresentation("");
        return component;
    }

    private NativeSelect getNativeSelect(XMLWidgetsDefinition widgets, Collection options, Object value) {
        NativeSelect component = new NativeSelect(widgets.getDisplayLabel(), options);
        component.setValue(value);
        return component;
    }

    private ComboBox getComboBox(XMLWidgetsDefinition widgets, Collection options, Object value) {
        ComboBox component = new ComboBox(widgets.getDisplayLabel(), options);
        component.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
        component.setValue(value);
        return component;
    }

    private OptionGroup getOptionGroup(XMLWidgetsDefinition widgets, Collection options, Object value, boolean multiselect) {
        OptionGroup component = new OptionGroup(widgets.getDisplayLabel(), options);
        component.setValue(value);
        component.setMultiSelect(multiselect);
        return component;
    }

    private ListSelect getListSelect(XMLWidgetsDefinition widgets, Collection options, Object value) {
        ListSelect component = new ListSelect(widgets.getDisplayLabel(), options);
        component.setMultiSelect(true);
        component.setValue(value);
        return component;
    }

    private CheckBox getCheckBox(XMLWidgetsDefinition widgets, Object value) {
        CheckBox component = new CheckBox(widgets.getDisplayLabel());
        component.setValue(value instanceof Boolean ? value : false);
        return component;
    }

    private ImmediateUpload getUpload(XMLWidgetsDefinition widgets) {
        DLFileEntry fe = null;
        String processUUID = null;
        if (taskInstance != null) {
            processUUID = taskInstance.getProcessInstanceUUID().toString();
            fe = findDLFileEntry(widgets.getInputScript());
        }
        ImmediateUpload component = new ImmediateUpload(processUUID, widgets, false, fe);
        return component;
    }

    private Button getButton(XMLWidgetsDefinition widgets) {
        Button component = new Button(widgets.getDisplayLabel());
        component.addListener((Button.ClickListener) this);
        if (widgets.isLabelBehavior()) {
            component.setStyleName(Reindeer.BUTTON_LINK);
        }
        return component;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        try {
            if (components.containsKey(event.getButton())) {
                Button btn = event.getButton();
                if (getWidgets(btn).getType().equals("form:SubmitFormButton")) {
                    commit();
                    setProcessVariables();
                    executeButtonActions(getWidgets(btn));
                    if (taskInstance == null) {
                        ProcessInstanceUUID piUUID = PbPortlet.getCurrent().bpmModule.startNewProcess(processDefinition.getUUID(), processInstanceVariables);
                        if (hasAttachments) {
                            saveAttachments(piUUID.toString());
                        }
                    } else {
                        PbPortlet.getCurrent().bpmModule.finishTask(taskInstance, true, processInstanceVariables, activityInstanceVariables);
                        if (hasAttachments) {
                            saveAttachments(taskInstance.getProcessInstanceUUID().toString());
                        }
                    }
                    close();

                } else if (getWidgets(btn).getType().equals("form:NextFormButton")) {
                    commitPage(pages.get(currentPage));
                    currentPage = (pages.size() > (currentPage + 1)) ? currentPage + 1 : currentPage;
                    taskPanel.setContent(pages.get(currentPage));
                    taskPanel.setCaption(forms.get(currentPage).getLabel());
                } else if (getWidgets(btn).getType().equals("form:PreviousFormButton")) {
                    commitPage(pages.get(currentPage));
                    currentPage = (currentPage != 0) ? currentPage - 1 : currentPage;
                    taskPanel.setContent(pages.get(currentPage));
                    taskPanel.setCaption(forms.get(currentPage).getLabel());
                }
            }
        } catch (InvalidValueException ex) {
            showMessage(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        } catch (Exception ex) {
            showMessage(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void commit() {
        for (GridLayout grid : pages) {
            commitPage(grid);
        }
    }

    private void commitPage(GridLayout page) {
        for (Iterator<Component> iterator = page.getComponentIterator(); iterator.hasNext();) {
            Component comp = iterator.next();
            if (comp instanceof AbstractField) {
                try {
                    ((AbstractField) comp).setComponentError(null);
                    ((AbstractField) comp).validate();
                } catch (InvalidValueException ex) {
                    if (ex instanceof EmptyValueException) {
                        ((AbstractField) comp).setComponentError(new UserError(((AbstractField) comp).getRequiredError()));
                    }
                    throw ex;
                }

            }
        }
    }

    private void setProcessVariables() throws Exception {
        Map<String, Object> piVariablesTemp = new HashMap<String, Object>();
        Map<String, Object> aiVariablesTemp = new HashMap<String, Object>();
        for (Component comp : components.keySet()) {
            XMLWidgetsDefinition widgets = components.get(comp);
            if (widgets.getSetVarScript() != null) {
                if (comp instanceof AbstractField) {
                    if (processDataFieldDefinitions.containsKey(widgets.getSetVarScript())) {
                        piVariablesTemp.put(widgets.getSetVarScript(), ((AbstractField) comp).getValue());
                    } else if (activityDataFieldDefinitions.containsKey(widgets.getSetVarScript())) {
                        aiVariablesTemp.put(widgets.getSetVarScript(), ((AbstractField) comp).getValue());
                    }
                }
                if (comp instanceof GeneratedTable) {
                    if (processDataFieldDefinitions.containsKey(widgets.getSetVarScript())) {
                        piVariablesTemp.put(widgets.getSetVarScript(), ((GeneratedTable) comp).getTableValue());
                    } else if (activityDataFieldDefinitions.containsKey(widgets.getSetVarScript())) {
                        aiVariablesTemp.put(widgets.getSetVarScript(), ((GeneratedTable) comp).getTableValue());
                    }
                }
                if (comp instanceof CheckBox) {
                    if (processDataFieldDefinitions.containsKey(widgets.getSetVarScript())) {
                        piVariablesTemp.put(widgets.getSetVarScript(), ((CheckBox) comp).booleanValue());
                    } else if (activityDataFieldDefinitions.containsKey(widgets.getSetVarScript())) {
                        aiVariablesTemp.put(widgets.getSetVarScript(), ((CheckBox) comp).booleanValue());
                    }
                }
            }
        }
        processInstanceVariables = piVariablesTemp;
        activityInstanceVariables = aiVariablesTemp;
    }

    private void saveAttachments(String processUUID) {
        for (Component comp : components.keySet()) {
            XMLWidgetsDefinition widgets = components.get(comp);
            if (widgets.getSetVarScript() != null) {
                if (widgets.getType().equals("form:FileWidget")) {
                    try {
                        ImmediateUpload ui = (ImmediateUpload) comp;
                        PbPortlet.getCurrent().documentLibraryUtil.addFile(PbPortlet.getCurrent().getPortalUser(), processUUID, widgets.getSetVarScript(), ui.getFileName(), ui.getFileName(), ui.getFileBody(), new String[]{});
                    } catch (PortalException ex) {
                        Logger.getLogger(GeneratedWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
                    } catch (SystemException ex) {
                        Logger.getLogger(GeneratedWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
                    }
                }
            }
        }
    }

    private void prepareAttachments() {
        if (taskInstance != null) {
            try {
                processFiles = PbPortlet.getCurrent().documentLibraryUtil.getProcessFiles(
                        PbPortlet.getCurrent().getPortalUser(),
                        taskInstance.getProcessInstanceUUID().toString());
            } catch (SystemException ex) {
                Logger.getLogger(GeneratedWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
    }

    private DLFileEntry findDLFileEntry(String name) {
        for (DLFileEntry file : processFiles) {
            if (file.getTitle().equals(name)) {
                return file;
            }
        }
        return null;
    }

    private XMLWidgetsDefinition getWidgets(Component c) {
        for (Component comp : components.keySet()) {
            if (comp.equals(c)) {
                return components.get(comp);
            }
        }
        return null;
    }

    public void setXMLProcess(XMLProcessDefinition xmlProcess) {
        this.xmlProcess = xmlProcess;
    }

    private void prepareGroovyScripts() throws Exception {
        HashMap<String, String> scripts = new HashMap<String, String>();

        for (XMLFormDefinition form : forms) {
            ArrayList<XMLWidgetsDefinition> widgetsList = form.getWidgets();
            for (XMLWidgetsDefinition widgets : widgetsList) {
                if (widgets.getInputScript() != null) {
                    scripts.put(widgets.getInputScript(), widgets.getInputScript());
                }
                if (widgets.getDefaultValue() != null) {
                    scripts.put(widgets.getDefaultValue(), widgets.getDefaultValue());
                }
            }
        }
        if (taskInstance != null) {
            groovyScripts = PbPortlet.getCurrent().bpmModule.evaluateGroovyExpressions(scripts, taskInstance.getUUID(), false, false);
        } else if (taskInstance == null) {
            groovyScripts = PbPortlet.getCurrent().bpmModule.evaluateGroovyExpressions(scripts, processDefinition.getUUID(), null, true);
        }
    }

    private String getPureScript(String script) {
        script = script.replace(GroovyExpression.START_DELIMITER, "");
        int end = script.indexOf(GroovyExpression.END_DELIMITER);
        return script.substring(0, end > 0 ? end : script.length());
    }

    private void executeButtonActions(XMLWidgetsDefinition button) throws Exception {
        for (XMLActionDefinition action : button.getActions()) {
            if (taskInstance != null) {
                if (processDataFieldDefinitions.containsKey(action.getSetVarScript())) {
                    processInstanceVariables.put(action.getSetVarScript(), PbPortlet.getCurrent().bpmModule.evaluateExpression(action.getExprScript(), taskInstance, true));
                } else if (activityDataFieldDefinitions.containsKey(action.getSetVarScript())) {
                    activityInstanceVariables.put(action.getSetVarScript(), PbPortlet.getCurrent().bpmModule.evaluateExpression(action.getExprScript(), taskInstance, true));
                }
            } else {
                if (processDataFieldDefinitions.containsKey(action.getSetVarScript())) {
                    processInstanceVariables.put(action.getSetVarScript(), PbPortlet.getCurrent().bpmModule.evaluateExpression(action.getExprScript(), processDefinition.getUUID()));

                }
            }

        }
    }
}
