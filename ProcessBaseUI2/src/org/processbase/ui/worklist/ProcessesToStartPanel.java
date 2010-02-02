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
import org.processbase.util.Constants;
import org.processbase.ProcessBase;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.processbase.MainWindow;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.util.ProcessBaseClassLoader;
import org.processbase.util.db.HibernateUtil;
import org.processbase.util.db.PbActivityUi;
import org.processbase.util.db.PbProcessAcl;
import org.processbase.util.ldap.LdapUtils;
import org.processbase.util.ldap.User;

/**
 *
 * @author mgubaidullin
 */
public class ProcessesToStartPanel extends TablePanel implements Button.ClickListener {

    private HibernateUtil hutil = new HibernateUtil();
    protected BPMModule bpmModule = ProcessBase.getCurrent().getBpmModule();

    public ProcessesToStartPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("label", Component.class, null, messages.getString("tableCaptionProcessName"), null, null);
        table.addContainerProperty("desc", String.class, null, messages.getString("tableCaptionDescription"), null, null);
        table.addContainerProperty("version", String.class, null, messages.getString("tableCaptionVersion"), null, null);
//        table.addContainerProperty("state", String.class, null, messages.getString("tableCaptionState"), null, null);
//        table.setColumnWidth("state", 75);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 75);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            User user = ProcessBase.getCurrent().getUser();
            LdapUtils ldapUtis = new LdapUtils(user.getUid(), null, user.getPassword());
            ldapUtis.updateUserGroups(user);
            ArrayList<PbProcessAcl> pbProcessAcls = hutil.findPbProcessAcl(user);
            for (PbProcessAcl pbProcessAcl : pbProcessAcls) {
                ProcessDefinitionUUID pdUUID = new ProcessDefinitionUUID(pbProcessAcl.getProccessUuid());
                try {
                    ProcessDefinition pd = bpmModule.getProcessDefinition(pdUUID);
                    Item woItem = table.addItem(pd);
                    woItem.getItemProperty("label").setValue(new TableExecButton(pd.getLabel(), messages.getString("btnStart"), null, pd, this, Constants.ACTION_START));
                    woItem.getItemProperty("version").setValue(pd.getVersion());
                    woItem.getItemProperty("desc").setValue(pd.getDescription());
//                    woItem.getItemProperty("state").setValue(pd.getState());
                    TableExecButtonBar tebb = new TableExecButtonBar();
                    tebb.addButton(new TableExecButton(messages.getString("btnStart"), "icons/start.png", pd, this, Constants.ACTION_START));
//                    tebb.addButton(getExecBtn(messages.getString("btnHelp"), "icons/help.png", pd, Constants.ACTION_HELP));
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
                    TaskWindow taskWindow = getStartWindow(procd);
                    getApplication().getMainWindow().addWindow(taskWindow);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (((TableExecButton) event.getButton()).getAction().equals(Constants.ACTION_HELP)) {
                ProcessDefinition pd = (ProcessDefinition) ((TableExecButton) event.getButton()).getTableValue();
                ((MainWindow) getWindow()).getWorkPanel().getHelpPanel().setHelp(pd.getUUID().toString());
            }
        }
    }

    public TaskWindow getStartWindow(ProcessDefinition procd) throws InstanceNotFoundException, ProcessNotFoundException {
        TaskWindow taskWindow = null;
        try {
            PbActivityUi pbActivityUi = hutil.findPbActivityUi(procd.getInitialActivities().values().iterator().next().getUUID().toString());
            Class b = ProcessBaseClassLoader.getCurrent().loadClass(pbActivityUi.getUiClass());
            taskWindow = (TaskWindow) b.newInstance();
            taskWindow.setTaskInfo(procd, null);
            return taskWindow;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (taskWindow == null) {
                taskWindow = new DefaultTaskWindow(procd, null);
            }
            taskWindow.exec();
            return taskWindow;
        }
    }
}
