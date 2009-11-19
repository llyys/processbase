/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.worklist;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.processbase.ui.template.TaskWindow;
import java.util.*;
import org.processbase.Constants;
import org.processbase.ProcessBase;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.processbase.MainWindow;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.util.db.HibernateUtil;
import org.processbase.util.db.PbProcessAcl;
import org.processbase.util.ldap.LdapUtils;
import org.processbase.util.ldap.User;

/**
 *
 * @author mgubaidullin
 */
public class ProcessesToStartPanel extends TablePanel implements Button.ClickListener {

    private HibernateUtil hutil = new HibernateUtil();
    protected BPMModule bpmModule = ((ProcessBase) getApplication()).getCurrent().getBpmModule();

    public ProcessesToStartPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionProcessName"), null, null);
        table.addContainerProperty("desc", Component.class, null, messages.getString("tableCaptionDescription"), null, null);
        table.addContainerProperty("version", String.class, null, messages.getString("tableCaptionVersion"), null, null);
//        table.addContainerProperty("author", String.class, null, messages.getString("tableCaptionAuthor"), null, null);
//        table.addContainerProperty("state", String.class, null, messages.getString("tableCaptionState"), null, null);
//        table.setColumnWidth("state", 75);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 75);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            User user = ((ProcessBase) getApplication()).getCurrent().getUser();
            LdapUtils ldapUtis = new LdapUtils(user.getUid(), null, user.getPassword());
            ldapUtis.updateUserGroups(user);
            ArrayList<PbProcessAcl> pbProcessAcls = hutil.findPbProcessAcl(user);
            for (PbProcessAcl pbProcessAcl : pbProcessAcls) {
                ProcessDefinitionUUID pdUUID = new ProcessDefinitionUUID(pbProcessAcl.getProccessUuid());
                try {
                    ProcessDefinition pd = bpmModule.getProcessDefinition(pdUUID);
                    Item woItem = table.addItem(pd);
                    woItem.getItemProperty("name").setValue(pd.getName());
                    woItem.getItemProperty("version").setValue(pd.getVersion());
//                    woItem.getItemProperty("author").setValue(pd.getAuthor());
                    woItem.getItemProperty("desc").setValue(new TableExecButton(pd.getDescription(), messages.getString("btnStart"), null, pd, this, Constants.ACTION_START));
//                    woItem.getItemProperty("state").setValue(pd.getState());
                    TableExecButtonBar tebb = new TableExecButtonBar();
                    tebb.addButton(new TableExecButton(messages.getString("btnStart"), "icons/start.png", pd, this, Constants.ACTION_START));
                    tebb.addButton(getExecBtn(messages.getString("btnHelp"), "icons/help.png", pd, Constants.ACTION_HELP));
                    woItem.getItemProperty("actions").setValue(tebb);
                } catch (ProcessNotFoundException ex) {
                    Logger.getLogger(ProcessesToStartPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                } catch (Exception ex) {
                    Logger.getLogger(ProcessesToStartPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            Logger.getLogger(ProcessesToStartPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            if (((TableExecButton) event.getButton()).getAction().equals(Constants.ACTION_START)) {
                try {
                    ProcessDefinition procd = (ProcessDefinition) ((TableExecButton) event.getButton()).getTableValue();
                    TaskWindow taskWindow = bpmModule.getStartWindow(procd, (ProcessBase) getApplication());
                    getApplication().getMainWindow().addWindow(taskWindow);
                } catch (Exception ex) {
                    Logger.getLogger(ProcessesToStartPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                    showError(ex.getMessage());
                }
            } else if (((TableExecButton) event.getButton()).getAction().equals(Constants.ACTION_HELP)) {
                ProcessDefinition pd = (ProcessDefinition) ((TableExecButton) event.getButton()).getTableValue();
                ((MainWindow) getWindow()).getWorkPanel().getHelpPanel().setHelp(pd.getUUID().toString());
            }
        }
    }
}
