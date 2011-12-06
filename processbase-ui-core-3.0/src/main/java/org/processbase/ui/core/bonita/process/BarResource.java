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
package org.processbase.ui.core.bonita.process;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;

import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.BonitaFormParcer;
import org.processbase.ui.core.bonita.forms.FormsDefinition;
import org.processbase.ui.core.bonita.forms.PageFlow.Pages.Page;
import org.processbase.ui.core.bonita.forms.XMLProcessDefinition;
import org.processbase.ui.core.util.CacheUtil;
import org.processbase.ui.core.util.ICacheDelegate;

/**
 *
 * @author marat
 */
public class BarResource implements Serializable {

    private ProcessDefinitionUUID puuid;
    private Map<String, byte[]> resource;
    private Map<String, XMLProcessDefinition> xmlProcessDefinition;
    private FormsDefinition formsDefinition;
    private byte[] proc = null;
    private byte[] form = null;
    private byte[] css = null;
	

    
    public static BarResource getBarResource(final ProcessDefinitionUUID puuid) throws Exception {
    	return CacheUtil.getOrCache("BAR_RESOURCE", puuid, new ICacheDelegate<BarResource>() {
			public BarResource execute() throws Exception {
				// TODO Auto-generated method stub
				return new BarResource(puuid);
			}
		});
    }
    
    private BarResource(ProcessDefinitionUUID puuid) throws Exception{
        this.puuid = puuid;
        byte[] process_def = null;
        resource = ProcessbaseApplication.getCurrent().getBpmModule().getBusinessArchive(this.puuid);
        for (String key : resource.keySet()) {
            if (key.substring(key.length() - 4, key.length()).equals("proc")) {
                proc = resource.get(key);
            } else if (key.equals("forms/forms.xml")) {
                form = resource.get(key);
            } else if (key.equals("forms/resources/application/css/generatedcss.css")) {
                css = resource.get(key);
            } else if (key.equals("process-def.xml")){
            	process_def=resource.get(key);
            }
        } 
        BonitaFormParcer bfb = new BonitaFormParcer(proc);
        xmlProcessDefinition = bfb.getProcess();
//        formsDefinition = BonitaFormParcer.createFormsDefinition(new String(form, "UTF-8")); //If bar resource already is in UTF-8 encoding this caused a double encoding and thus resource was not available
        if(form==null)
        	throw new Exception("No form definitions found from process!");
        
        String old=new String(form, "UTF-8");
       // String data=String.copyValueOf(old.toCharArray());
        formsDefinition = BonitaFormParcer.createFormsDefinition(old);
        
        processDefinition = new ProcessDefinition(process_def);
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

    public XMLProcessDefinition getXmlProcessDefinition(String name) {
        return xmlProcessDefinition.get(name);
    }

    public void setXmlProcessDefinition(String name, XMLProcessDefinition xmlProcessDefinition) {
    	if(this.xmlProcessDefinition==null)
    		this.xmlProcessDefinition=new Hashtable<String, XMLProcessDefinition>();
        this.xmlProcessDefinition.put(name, xmlProcessDefinition);
    }
    List<String> processRoles=null;
	private ProcessDefinition processDefinition;
	public List<ProcessParticipant> getProcessRoles() {
		if(processDefinition!=null);
		{
			return processDefinition.getProcesses().get(0).getParticipants();
		}
	}
	List<String> processGroups=null;
	public List<ProcessParticipant> getProcessGroups() {
		if(processDefinition!=null);
		{
			return processDefinition.getProcesses().get(0).getParticipants();
		}
	}

	public byte[] getAttachment(String attachmentName) {
		//return null;
		// TODO Auto-generated method stub
		return ProcessbaseApplication.getCurrent().getBpmModule().getLargeDataRepositoryAttachment(puuid, attachmentName);
	}
	
}
