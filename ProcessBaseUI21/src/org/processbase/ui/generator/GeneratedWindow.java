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

import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import java.util.ArrayList;
import java.util.HashMap;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.processbase.bpm.forms.XMLFormDefinition;
import org.processbase.bpm.forms.XMLWidgetsDefinition;
import org.processbase.ui.template.HumanTaskWindow;

/**
 *
 * @author marat
 */
public class GeneratedWindow extends HumanTaskWindow implements Button.ClickListener {

    private HashMap<Component, XMLWidgetsDefinition> components = new HashMap<Component, XMLWidgetsDefinition>();
    private ArrayList<XMLFormDefinition> forms;
    private ArrayList<GridLayout> pages = new ArrayList<GridLayout>();

    public GeneratedWindow(String caption, PortletApplicationContext2 portletApplicationContext2) {
        super(caption, portletApplicationContext2);
    }

    @Override
    public void initUI() {
        super.initUI();
        generateWindow();
    }

    protected void generateWindow() {
        for (XMLFormDefinition form : forms) {
            GridLayout page = new GridLayout(form.getnColumn(), form.getnLine());
            page.setMargin(false, true, true, true);
            page.setSpacing(true);
//            page.setSizeFull();
//            System.out.println("GRID " + form.getnColumn() + " " + form.getnLine());
            page.setHeight(form.getHeight() != null ? (form.getHeight().contains("%") ? form.getHeight() : form.getHeight() + "px") : "100%");
            page.setWidth(form.getWidth() != null ? (form.getWidth().contains("%") ? form.getWidth() : form.getWidth() + "px") : "100%");
            pages.add(page);

            ArrayList<XMLWidgetsDefinition> widgetsList = form.getWidgets();
            for (XMLWidgetsDefinition widgets : widgetsList) {
//                System.out.println("ADD TO GRID " + widgets.getColumn() + " " + widgets.getLine() + " " + (widgets.getColumn() + (widgets.getHorizontalSpan() > 0 ? widgets.getHorizontalSpan() : 0)));
                Component component = getComponent(widgets);
                if (component != null) {
                    components.put(component, widgets);
                    page.addComponent(component, widgets.getColumn(), widgets.getLine(), widgets.getColumn() + (widgets.getHorizontalSpan() > 0 ? widgets.getHorizontalSpan() - 1 : 0), widgets.getLine());
                }
            }

        }
        taskPanel.setContent(pages.get(0));
        taskPanel.setCaption(forms.get(0).getLabel());
    }

    private Component getComponent(XMLWidgetsDefinition widgets) {
        Object value = null;
        DataFieldDefinition dfd = null;
        try {
            if (!initial && widgets.getSetVarScript() != null) {
                value = bpmModule.getProcessInstanceVariable(task.getProcessInstanceUUID(), widgets.getSetVarScript());
                dfd = bpmModule.getProcessDataField(task.getProcessDefinitionUUID(), widgets.getSetVarScript());
            } else {
                dfd = bpmModule.getProcessDataField(processDef.getUUID(), widgets.getSetVarScript());
            }
            System.out.println("dfd = " + dfd.getName() + "; dfd.scripting = " + dfd.getScriptingValue() +
                    "; dfd.initial = " + dfd.getInitialValue());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (widgets.getType().equals("form:TextFormField")) {
            return getTextField(widgets, dfd, value);
        }
        if (widgets.getType().equals("form:DateFormField")) {
            return getPopupDateField(widgets, dfd, value);
        }
        if (widgets.getType().equals("form:TextAreaFormField")) {
            return getTextField(widgets, dfd, value);
        }
        if (widgets.getType().equals("form:RichTextAreaFormField")) {
            return getRichTextArea(widgets, dfd, value);
        }
        if (widgets.getType().equals("form:SelectFormField")) {
            return getNativeSelect(widgets, dfd, value);
        }
        if (widgets.getType().equals("form:SubmitFormButton")) {
            return getButton(widgets, dfd, value);
        }
        return new Label("");
    }

    private TextField getTextField(XMLWidgetsDefinition widgets, DataFieldDefinition dfd, Object value) {
        TextField component = new TextField(widgets.getDisplayLabel());
        component.setValue(initial ? dfd.getInitialValue() : value);
        component.setNullRepresentation("");
        return component;
    }

    private PopupDateField getPopupDateField(XMLWidgetsDefinition widgets, DataFieldDefinition dfd, Object value) {
        PopupDateField component = new PopupDateField(widgets.getDisplayLabel());
        component.setValue(initial ? dfd.getInitialValue() : value);
        component.setResolution(PopupDateField.RESOLUTION_DAY);

        return component;
    }

    private RichTextArea getRichTextArea(XMLWidgetsDefinition widgets, DataFieldDefinition dfd, Object value) {
        RichTextArea component = new RichTextArea(widgets.getDisplayLabel());
        component.setValue(initial ? dfd.getInitialValue() : value);
        component.setNullRepresentation("");
        return component;
    }

    private NativeSelect getNativeSelect(XMLWidgetsDefinition widgets, DataFieldDefinition dfd, Object value) {
        NativeSelect component = new NativeSelect(widgets.getDisplayLabel());
        for (String key : dfd.getEnumerationValues()) {
            component.addItem(key);
        }
        component.setValue(initial ? dfd.getInitialValue() : value);
        return component;
    }

    private Button getButton(XMLWidgetsDefinition widgets, DataFieldDefinition dfd, Object value) {
        Button component = new Button(widgets.getDisplayLabel());
        component.addListener((Button.ClickListener) this);
        return component;
    }

    public void buttonClick(ClickEvent event) {
        if (components.containsKey(event.getButton())) {
            Button btn = event.getButton();
            if (getWidgets(btn).getType().equals("form:SubmitFormButton")) {
                try {
                    setProcessVariables();
                    if (initial) {
                        bpmModule.startNewProcess(processDef.getUUID(), procVariables);
                    } else {
                        bpmModule.finishTask(task, true, procVariables);
                    }
                    close();
                } catch (Exception ex) {
                    this.showNotification("Error", ex.getMessage().substring(250), Notification.TYPE_WARNING_MESSAGE);
                }
            }
        }
    }

    private void setProcessVariables() throws Exception {
        for (Component comp : components.keySet()) {
            XMLWidgetsDefinition widgets = components.get(comp);
            if (widgets.getType().equals("form:TextFormField")) {
                procVariables.put(widgets.getSetVarScript(), ((TextField) comp).getValue());
            }
            if (widgets.getType().equals("form:TextAreaFormField")) {
                procVariables.put(widgets.getSetVarScript(), ((TextField) comp).getValue());
            }
            if (widgets.getType().equals("form:RichTextAreaFormField")) {
                procVariables.put(widgets.getSetVarScript(), ((RichTextArea) comp).getValue());
            }
            if (widgets.getType().equals("form:SelectFormField")) {
                procVariables.put(widgets.getSetVarScript(), ((NativeSelect) comp).getValue());
            }
        }
    }

    private XMLWidgetsDefinition getWidgets(Component c) {
        for (Component comp : components.keySet()) {
            if (comp.equals(c)) {
                return components.get(comp);
            }
        }
        return null;
    }

    public void setForms(ArrayList<XMLFormDefinition> forms) {
        this.forms = forms;
    }
}
