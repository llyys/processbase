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
package org.processbase.ui.core.bonita.forms;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author marat
 */
public class XMLProcessDefinition {

    private String name;
    private String label;
    private XMLFormDefinition processStartForm;
    private ArrayList<XMLFormDefinition> forms = new ArrayList<XMLFormDefinition>();
    private HashMap<String, XMLTaskDefinition> tasks = new HashMap<String, XMLTaskDefinition>();
    private HashMap<String, XMLDataDefinition> datas = new HashMap<String, XMLDataDefinition>();
    private boolean byPassFormsGeneration = false;

    public XMLProcessDefinition(String name, String label) {
        this.name = name;
        this.label = label;
    }

    public XMLFormDefinition getProcessStartForm() {
        return processStartForm;
    }

    public ArrayList<XMLFormDefinition> getForms() {
        return forms;
    }

    public HashMap<String, XMLTaskDefinition> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<String, XMLTaskDefinition> tasks) {
        this.tasks = tasks;
    }

    public void addForms(ArrayList<XMLFormDefinition> forms) {
        this.forms.addAll(forms);
    }

    public void addTask(String stepName, XMLTaskDefinition task) {
        this.tasks.put(stepName, task);
    }

    public void addData(XMLDataDefinition data) {
        this.datas.put(data.getId(), data);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, XMLDataDefinition> getDatas() {
        return datas;
    }

    public boolean isByPassFormsGeneration() {
        return byPassFormsGeneration;
    }

    public void setByPassFormsGeneration(boolean byPassFormsGeneration) {
        this.byPassFormsGeneration = byPassFormsGeneration;
    }

}
