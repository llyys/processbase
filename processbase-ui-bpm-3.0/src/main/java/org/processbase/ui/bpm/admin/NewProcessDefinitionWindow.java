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
package org.processbase.ui.bpm.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.Role;
import org.ow2.bonita.facade.privilege.Rule;
import org.ow2.bonita.facade.privilege.Rule.RuleType;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class NewProcessDefinitionWindow extends PbWindow
        implements Upload.SucceededListener,
        Upload.FailedListener,
        Upload.Receiver {

    private Upload upload = new Upload("", (Upload.Receiver) this);
    private File file;
    private String filename;
    private String originalFilename;
    private CheckBox cbDisableOtherInstances;
    private String fileExt;
    public static String FILE_BAR = "FILE_BAR";
    public static String FILE_JAR = "FILE_JAR";
    private String fileType = null;

    public NewProcessDefinitionWindow() {
        super();
    }

    public void initUI() {
        try {
        	
            setCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("newProcessDefinition"));
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);
            cbDisableOtherInstances=new CheckBox("Leave old process active");
            
            // prepare upload button
            upload.setButtonCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnUpload"));
            upload.addListener((Upload.SucceededListener) this);
            upload.addListener((Upload.FailedListener) this);
            addComponent(upload);
            addComponent(cbDisableOtherInstances);
            
            
            setWidth("360px");
            setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

   
    public void uploadSucceeded(SucceededEvent event) {
        try {
            byte[] readData = new byte[new Long(event.getLength()).intValue()];
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            int i = fis.read(readData);
            fis.close();
            if (this.fileType.equals(FILE_BAR)) {
                System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema",
                        "com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory");
                BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(file);
                ProcessDefinition deployResult = getBpmModule().deploy(businessArchive, ProcessbaseApplication.getCurrent().getPbMessages().getString("emptyCategory"));
                
                if(cbDisableOtherInstances.booleanValue()==false)//disable other instances
                {
                	for (LightProcessDefinition process : getBpmModule().getLightProcessDefinitions()) {
						if(process.getName().equals(deployResult.getName())
								&& process.getUUID().equals(deployResult.getUUID())==false)
						{
							if(process.getState()==ProcessState.ENABLED)
								getBpmModule().disableProcessDefinitions(process.getUUID());
						}
					}	 
                }
                //Add default ENTITY_PROCESS_START rule for administrator
            	Role admin=getBpmModule().findRoleByName(IdentityAPI.ADMIN_ROLE_NAME);
            	Group defaultGroup = getBpmModule().findGroupByName(IdentityAPI.DEFAULT_GROUP_NAME);
            	if(admin!=null && defaultGroup!=null)
            	{
            		Membership membership = getBpmModule().getMembershipForRoleAndGroup(admin.getUUID(), defaultGroup.getUUID());
            		Set<String> membershipUUIDs = new HashSet<String>();
            		membershipUUIDs.add(membership.getUUID());
            		
            		Set<String> entityUUIDs = new HashSet<String>();
    				entityUUIDs.add(deployResult.getUUID().toString());
    				
            		Rule rule = getBpmModule().findRule(deployResult.getUUID().toString());
            		if(rule==null || rule.getType()!=RuleType.PROCESS_START)
            		{
            			rule=getBpmModule().createRule(deployResult.getUUID().toString(), "ENTITY_PROCESS_START", "Rule to start a process", RuleType.PROCESS_START);
            		}
    				getBpmModule().applyRuleToEntities(rule.getUUID(), null, null, null,membershipUUIDs, entityUUIDs);
            	}            	
            	
                showInformation(ProcessbaseApplication.getCurrent().getPbMessages().getString("processUploaded") + ": " + deployResult.getLabel());
            } else if (this.fileType.equals(FILE_JAR)) {
                getBpmModule().deployJar(originalFilename, readData);
                showWarning(ProcessbaseApplication.getCurrent().getPbMessages().getString("jarUploaded") + ": " + originalFilename);
            }
           
            close();
        }
        catch (org.ow2.bonita.facade.exception.DeploymentException ex){
        	showError(ex.getLocalizedMessage());
        }
        catch (org.ow2.bonita.facade.exception.DocumentAlreadyExistsException ex){
        	showError("Document already exists");
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        finally{
        	 file.delete();
        }
    }

	private BPMModule getBpmModule() {
		return ProcessbaseApplication.getCurrent().getBpmModule();
	}

    public void uploadFailed(FailedEvent event) {
        showError(event.getReason().getMessage());
    }

    public OutputStream receiveUpload(
            String filename, String MIMEType) {
        this.originalFilename = filename;
        this.filename = UUID.randomUUID().toString();
        String[] fileNameParts = originalFilename.split("\\.");
        this.fileExt = fileNameParts.length > 0 ? fileNameParts[fileNameParts.length - 1] : null;
        if (fileExt.equalsIgnoreCase("bar")) {
            this.fileType = FILE_BAR;
        } else if (fileExt.equalsIgnoreCase("jar")) {
            this.fileType = FILE_JAR;
        }
        FileOutputStream fos = null;
        file = new File(this.filename);
        try {
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException ex) {
            ex.printStackTrace();
            
            return null;
        }
        return fos;
    }
}
