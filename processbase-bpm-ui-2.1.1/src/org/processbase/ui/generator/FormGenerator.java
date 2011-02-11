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

import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.bpm.forms.XMLProcessDefinition;
import org.processbase.ui.portlet.PbPortlet;

/**
 *
 * @author marat
 */
public class FormGenerator {

    private GeneratedWindow genWindow;
    
    
    public FormGenerator(LightTaskInstance task, 
            XMLProcessDefinition xmlProcess
            ) throws Exception {
        genWindow = new GeneratedWindow(task.getActivityLabel());
        genWindow.setTask(PbPortlet.getCurrent().bpmModule.getTaskInstance(task.getUUID()));
        genWindow.setXMLProcess(xmlProcess);
        genWindow.initUI();
    }

    public FormGenerator(LightProcessDefinition process,
            XMLProcessDefinition xmlProcess) throws Exception {
        genWindow = new GeneratedWindow(process.getLabel());
        genWindow.setProcessDef(process);
        genWindow.setXMLProcess(xmlProcess);
        genWindow.initUI();
    }

    public GeneratedWindow getWindow() {
        return genWindow;
    }
}
