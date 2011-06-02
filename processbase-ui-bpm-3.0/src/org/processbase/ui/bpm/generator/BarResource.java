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
package org.processbase.ui.bpm.generator;

import java.util.HashMap;
import java.util.Map;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.BonitaFormParcer;
import org.processbase.ui.core.bonita.forms.FormsDefinition;
import org.processbase.ui.core.bonita.forms.PageFlow.Pages.Page;
import org.processbase.ui.core.bonita.forms.XMLProcessDefinition;

/**
 *
 * @author marat
 */
public class BarResource {

    private ProcessDefinitionUUID puuid;
    private Map<String, byte[]> resource;
    private XMLProcessDefinition xmlProcessDefinition;
    private FormsDefinition formsDefinition;
    private byte[] proc = null;
    private byte[] form = null;
    private byte[] css = null;

    public BarResource(ProcessDefinitionUUID puuid) throws Exception {
        this.puuid = puuid;
        resource = ProcessbaseApplication.getCurrent().getBpmModule().getBusinessArchive(this.puuid);
        for (String key : resource.keySet()) {
            if (key.substring(key.length() - 4, key.length()).equals("proc")) {
                proc = resource.get(key);
            } else if (key.equals("forms/forms.xml")) {
                form = resource.get(key);
            } else if (key.equals("forms/resources/application/css/generatedcss.css")) {
                css = resource.get(key);
            }
        }
        BonitaFormParcer bfb = new BonitaFormParcer(proc);
        xmlProcessDefinition = bfb.getProcess();
//        formsDefinition = BonitaFormParcer.createFormsDefinition(new String(form, "UTF-8")); //If bar resource already is in UTF-8 encoding this caused a double encoding and thus resource was not available
        if(form==null)
        	throw new Exception("No form definitions found from process!");
        formsDefinition = BonitaFormParcer.createFormsDefinition(new String(form));
    }

    public TableStyle getTableStyle(Page page) {
        byte[] html = null;
        for (String key : resource.keySet()) {
            if (key.equals("forms/" + page.getPageTemplate())) {
                html = resource.get(key);
            }
        }
        TableStyle ts = new TableStyle(new String(html), new String(css));
        return ts;
    }

    public FormsDefinition getFormsDefinition() {
        return formsDefinition;
    }

    public void setFormsDefinition(FormsDefinition formsDefinition) {
        this.formsDefinition = formsDefinition;
    }

    public Map<String, byte[]> getResource() {
        return resource;
    }

    public void setResource(Map<String, byte[]> resource) {
        this.resource = resource;
    }

    public XMLProcessDefinition getXmlProcessDefinition() {
        return xmlProcessDefinition;
    }

    public void setXmlProcessDefinition(XMLProcessDefinition xmlProcessDefinition) {
        this.xmlProcessDefinition = xmlProcessDefinition;
    }
}
