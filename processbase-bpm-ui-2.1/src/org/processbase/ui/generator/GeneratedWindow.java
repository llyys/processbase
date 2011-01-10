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
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.RichTextArea;
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
import org.processbase.bpm.forms.XMLFormDefinition;
import org.processbase.bpm.forms.XMLProcessDefinition;
import org.processbase.bpm.forms.XMLWidgetsDefinition;
import org.processbase.core.SyntaxChecker;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.HumanTaskWindow;
import org.processbase.ui.template.ImmediateUpload;

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
    private Map<String, Object> piVariables = new HashMap<String, Object>();
    private Map<String, Object> aiVariables = new HashMap<String, Object>();
    private Map<String, DataFieldDefinition> processDataFields = new HashMap<String, DataFieldDefinition>();
    private Map<String, DataFieldDefinition> activityDataFields = new HashMap<String, DataFieldDefinition>();

    public GeneratedWindow(String caption) {
        super(caption);
    }

    @Override
    public void initUI() {
        super.initUI();
        try {
            if (task != null && !task.getState().equals(ActivityState.FINISHED) && !task.getState().equals(ActivityState.ABORTED) && !task.getState().equals(ActivityState.CANCELLED)) {
                generateWindow();
            } else if (task == null) {
                generateWindow();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(GeneratedWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    protected void generateWindow() throws Exception {
        prepareAttachments();
        prepareVariables();

        forms = task == null ? xmlProcess.getForms() : xmlProcess.getTasks().get(task.getActivityName()).getForms();
        for (XMLFormDefinition form : forms) {
            GridLayout page = new GridLayout(form.getnColumn(), form.getnLine());
            page.setMargin(false, true, true, true);
            page.setSpacing(true);
            pages.add(page);

            ArrayList<XMLWidgetsDefinition> widgetsList = form.getWidgets();
            for (XMLWidgetsDefinition widgets : widgetsList) {
                Component component = getComponent(widgets);
                if (component != null) {
//                    Logger.getLogger(GeneratedWindow.class.getName()).log(Level.SEVERE, "component = " + component.getClass().getName());
                    components.put(component, widgets);
                    page.addComponent(component, widgets.getColumn(), widgets.getLine(), widgets.getColumn() + (widgets.getHorizontalSpan() > 0 ? widgets.getHorizontalSpan() - 1 : 0), widgets.getLine());
                }
            }

        }

        taskPanel.setContent(pages.get(currentPage));
        taskPanel.setCaption(forms.get(currentPage).getLabel());
    }

    private Component getComponent(XMLWidgetsDefinition widgets) {
//        Logger.getLogger(GeneratedWindow.class.getName()).log(Level.SEVERE, "widgets = " + widgets.getId() + " " + widgets.getName() + " widgets.getSetVarScript() = " + widgets.getSetVarScript());

        Component component = null;
        Object value = null;
        DataFieldDefinition dfd = null;
        Collection options = null;
        try {
            // define lists
            if (widgets.getType().equals("form:SelectFormField")
                    || widgets.getType().equals("form:RadioFormField")
                    || widgets.getType().equals("form:CheckBoxMultipleFormField")
                    || widgets.getType().equals("form:ListFormField")) {
                // set list options
                if ((widgets.getInputScript() == null || widgets.getInputScript().isEmpty())
                        && widgets.getEnum1() != null) {
                    if (task != null) {
                        String varName = xmlProcess.getDatas().get(widgets.getEnum1()).getName();
                        if (processDataFields.containsKey(varName)) {
                            dfd = processDataFields.get(varName);
                        } else if (activityDataFields.containsKey(varName)) {
                            dfd = activityDataFields.get(varName);
                        }
                        options = dfd.getEnumerationValues();

                        if (piVariables.containsKey(dfd.getName())) {
                            value = piVariables.get(dfd.getName());
                        } else if (aiVariables.containsKey(dfd.getName())) {
                            value = aiVariables.get(dfd.getName());
                        }
                    } else if (task == null) {
                        String varName = xmlProcess.getDatas().get(widgets.getEnum1()).getName();
                        if (processDataFields.containsKey(varName)) {
                            dfd = processDataFields.get(varName);
                        }
                        options = dfd.getEnumerationValues();
                        value = dfd.getInitialValue();
                    }
                } else if (SyntaxChecker.isScript(widgets.getInputScript())) {
                    if (task != null) {
                        options = (Collection) PbPortlet.getCurrent().bpmModule.evaluateGroovyExpression(widgets.getInputScript(), task, true);
                    } else if (task == null) {
                        options = (Collection) PbPortlet.getCurrent().bpmModule.evaluateGroovyExpression(widgets.getInputScript(), processDef.getUUID());
                    }
                } else {
//                    options = widgets.getInputScript();
                }
                // set list value
                if (task != null && widgets.getDefaultValue() != null && SyntaxChecker.isScript(widgets.getDefaultValue())) {
                    value = PbPortlet.getCurrent().bpmModule.evaluateGroovyExpression(widgets.getDefaultValue(), task, true);
                } else if (task == null && widgets.getDefaultValue() != null && SyntaxChecker.isScript(widgets.getDefaultValue())) {
                    value = PbPortlet.getCurrent().bpmModule.evaluateGroovyExpression(widgets.getDefaultValue(), processDef.getUUID());
                } else {
                    value = widgets.getDefaultValue();
                }
            } else if (widgets.getType().equals("form:Table") || widgets.getType().equals("form:DynamicTable")) {
                // define not lists
            } else {
                if (task != null && widgets.getInputScript() != null && !widgets.getInputScript().isEmpty()) {
                    if (SyntaxChecker.isScript(widgets.getInputScript())) {
                        value = PbPortlet.getCurrent().bpmModule.evaluateGroovyExpression(widgets.getInputScript(), task, true);
                    } else {
                        value = widgets.getInputScript();
                    }
                } else if (task != null && widgets.getDefaultValue() != null && !widgets.getDefaultValue().isEmpty()) {
                    if (SyntaxChecker.isScript(widgets.getDefaultValue())) {
                        value = PbPortlet.getCurrent().bpmModule.evaluateGroovyExpression(widgets.getDefaultValue(), task, true);
                    } else {
                        value = widgets.getDefaultValue();
                    }
                }
            }
            // define UI components
            if (widgets.getType().equals("form:TextFormField")) {
                component = getTextField(widgets, value, false, false);
            }
            if (widgets.getType().equals("form:DateFormField")) {
                component = getPopupDateField(widgets, value);
            }
            if (widgets.getType().equals("form:TextAreaFormField")) {
                component = getTextField(widgets, value, false, false);
            }
            if (widgets.getType().equals("form:RichTextAreaFormField")) {
                component = getRichTextArea(widgets, value);
            }
            if (widgets.getType().equals("form:TextInfo")) {
                component = getTextField(widgets, value, true, false);
            }
            if (widgets.getType().equals("form:PasswordFormField")) {
                component = getTextField(widgets, value, false, true);
            }
            if (widgets.getType().equals("form:MessageInfo")) {
                component = getLabel(widgets, value);
            }
            if (widgets.getType().equals("form:SelectFormField")) {
                component = getNativeSelect(widgets, options, value);
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
                component = new GeneratedTable(widgets, task, processDef);
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
                if (widgets.getValidatorName() != null) {
                    ((AbstractField) component).addValidator(new GeneratedValidator(widgets, task, processDef));
                }
            }
            return component;
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(GeneratedWindow.class.getName()).log(Level.SEVERE, "ERROR: " + ex.getMessage());
        }
        return new Label("");
    }

    private TextField getTextField(XMLWidgetsDefinition widgets, Object value, boolean readOnly, boolean secret) {
        TextField component = new TextField(widgets.getDisplayLabel());
        component.setValue(value);
        component.setNullRepresentation("");
        component.setReadOnly(readOnly);
        component.setSecret(secret);
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
        if (task != null) {
            processUUID = task.getProcessInstanceUUID().toString();
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
                    if (task == null) {
                        ProcessInstanceUUID piUUID = PbPortlet.getCurrent().bpmModule.startNewProcess(processDef.getUUID(), piVariables);
                        if (hasAttachments) {
                            saveAttachments(piUUID.toString());
                        }
                    } else {
                        PbPortlet.getCurrent().bpmModule.finishTask(task, true, piVariables, aiVariables);
                        if (hasAttachments) {
                            saveAttachments(task.getProcessInstanceUUID().toString());
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
            // do nothing
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
        for (Component comp : components.keySet()) {
            XMLWidgetsDefinition widgets = components.get(comp);
            if (widgets.getSetVarScript() != null) {
                if (widgets.getType().equals("form:TextFormField")) {
                    if (processDataFields.containsKey(widgets.getSetVarScript())) {
                        piVariables.put(widgets.getSetVarScript(), ((TextField) comp).getValue());
                    } else if (activityDataFields.containsKey(widgets.getSetVarScript())) {
                        aiVariables.put(widgets.getSetVarScript(), ((TextField) comp).getValue());
                    }
                }
                if (widgets.getType().equals("form:PasswordFormField")) {
                    if (processDataFields.containsKey(widgets.getSetVarScript())) {
                        piVariables.put(widgets.getSetVarScript(), ((TextField) comp).getValue());
                    } else if (activityDataFields.containsKey(widgets.getSetVarScript())) {
                        aiVariables.put(widgets.getSetVarScript(), ((TextField) comp).getValue());
                    }
                }
                if (widgets.getType().equals("form:TextAreaFormField")) {
                    if (processDataFields.containsKey(widgets.getSetVarScript())) {
                        piVariables.put(widgets.getSetVarScript(), ((TextField) comp).getValue());
                    } else if (activityDataFields.containsKey(widgets.getSetVarScript())) {
                        aiVariables.put(widgets.getSetVarScript(), ((TextField) comp).getValue());
                    }
                }
                if (widgets.getType().equals("form:RichTextAreaFormField")) {
                    if (processDataFields.containsKey(widgets.getSetVarScript())) {
                        piVariables.put(widgets.getSetVarScript(), ((RichTextArea) comp).getValue());
                    } else if (activityDataFields.containsKey(widgets.getSetVarScript())) {
                        aiVariables.put(widgets.getSetVarScript(), ((RichTextArea) comp).getValue());
                    }
                }
                if (widgets.getType().equals("form:SelectFormField")) {
                    if (processDataFields.containsKey(widgets.getSetVarScript())) {
                        piVariables.put(widgets.getSetVarScript(), ((NativeSelect) comp).getValue());
                    } else if (activityDataFields.containsKey(widgets.getSetVarScript())) {
                        aiVariables.put(widgets.getSetVarScript(), ((NativeSelect) comp).getValue());
                    }
                }
                if (widgets.getType().equals("form:RadioFormField")) {
                    if (processDataFields.containsKey(widgets.getSetVarScript())) {
                        piVariables.put(widgets.getSetVarScript(), ((OptionGroup) comp).getValue());
                    } else if (activityDataFields.containsKey(widgets.getSetVarScript())) {
                        aiVariables.put(widgets.getSetVarScript(), ((OptionGroup) comp).getValue());
                    }
                }
                if (widgets.getType().equals("form:DateFormField")) {
                    if (processDataFields.containsKey(widgets.getSetVarScript())) {
                        piVariables.put(widgets.getSetVarScript(), ((PopupDateField) comp).getValue());
                    } else if (activityDataFields.containsKey(widgets.getSetVarScript())) {
                        aiVariables.put(widgets.getSetVarScript(), ((PopupDateField) comp).getValue());
                    }
                }
                if (widgets.getType().equals("form:CheckBoxSingleFormField")) {
                    if (processDataFields.containsKey(widgets.getSetVarScript())) {
                        piVariables.put(widgets.getSetVarScript(), ((CheckBox) comp).booleanValue());
                    } else if (activityDataFields.containsKey(widgets.getSetVarScript())) {
                        aiVariables.put(widgets.getSetVarScript(), ((CheckBox) comp).booleanValue());
                    }
                }
                if (widgets.getType().equals("form:CheckBoxMultipleFormField")) {
                    if (processDataFields.containsKey(widgets.getSetVarScript())) {
                        piVariables.put(widgets.getSetVarScript(), ((OptionGroup) comp).getValue());
                    } else if (activityDataFields.containsKey(widgets.getSetVarScript())) {
                        aiVariables.put(widgets.getSetVarScript(), ((OptionGroup) comp).getValue());
                    }
                }
                if (widgets.getType().equals("form:ListFormField")) {
                    if (processDataFields.containsKey(widgets.getSetVarScript())) {
                        piVariables.put(widgets.getSetVarScript(), ((ListSelect) comp).getValue());
                    } else if (activityDataFields.containsKey(widgets.getSetVarScript())) {
                        aiVariables.put(widgets.getSetVarScript(), ((ListSelect) comp).getValue());
                    }

                }
                if (widgets.getType().equals("form:Table") || widgets.getType().equals("form:DynamicTable")) {
                    if (processDataFields.containsKey(widgets.getSetVarScript())) {
                        piVariables.put(widgets.getSetVarScript(), ((GeneratedTable) comp).getTableValue());
                    } else if (activityDataFields.containsKey(widgets.getSetVarScript())) {
                        aiVariables.put(widgets.getSetVarScript(), ((GeneratedTable) comp).getTableValue());
                    }
                }
            }
        }
    }

    private void saveAttachments(String processUUID) {
        for (Component comp : components.keySet()) {
            XMLWidgetsDefinition widgets = components.get(comp);
            if (widgets.getSetVarScript() != null) {
                if (widgets.getType().equals("form:FileWidget")) {
                    try {
                        ImmediateUpload ui = (ImmediateUpload) comp;
                        PbPortlet.getCurrent().documentLibraryUtil.addFile(processUUID, widgets.getSetVarScript(), ui.getFileName(), ui.getFileName(), ui.getFileBody(), new String[]{});
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
        if (task != null) {
            try {
                processFiles = PbPortlet.getCurrent().documentLibraryUtil.getProcessFiles(task.getProcessInstanceUUID().toString());
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

    private void prepareVariables() throws Exception {

        if (task != null) {
            for (DataFieldDefinition dfd : PbPortlet.getCurrent().bpmModule.getProcessDataFields(task.getProcessDefinitionUUID())) {
                processDataFields.put(dfd.getName(), dfd);
            }
            for (DataFieldDefinition dfd : PbPortlet.getCurrent().bpmModule.getActivityDataFields(task.getActivityDefinitionUUID())) {
                activityDataFields.put(dfd.getName(), dfd);
            }
            piVariables.putAll(PbPortlet.getCurrent().bpmModule.getProcessInstanceVariables(task.getProcessInstanceUUID()));
            aiVariables.putAll(PbPortlet.getCurrent().bpmModule.getActivityInstanceVariables(task.getUUID()));
        } else {
            for (DataFieldDefinition dfd : PbPortlet.getCurrent().bpmModule.getProcessDataFields(processDef.getUUID())) {
                processDataFields.put(dfd.getName(), dfd);
            }
        }
    }
}
