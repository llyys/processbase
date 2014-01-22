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
package org.processbase.ui.bpm.worklist;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.caliburn.application.event.IHandle;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.runtime.Category;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.bpm.generator.view.NewProcessWindow;
import org.processbase.ui.bpm.panel.events.TaskListEvent;
import org.processbase.ui.bpm.panel.events.TaskListEvent.ActionType;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.IPbTable;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TreeTablePanel;
import org.processbase.ui.core.util.CategoryAndProcessDefinition;

import com.vaadin.data.Item;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

import ee.kovmen.data.LegislationData;
import ee.kovmen.entities.KovLegislation;

/**
 *
 * @author mgubaidullin
 */
public class NewProcesses extends TreeTablePanel implements IPbTable, Button.ClickListener , IHandle<TaskListEvent>{

    public NewProcesses() {
        super();
    }

    @Override
    public void initUI() {
    	
        super.initUI();
        treeTable.addContainerProperty("category", AbstractComponent.class, null, ProcessbaseApplication.getString("tableCaptionCategory"), null, null);
        //treeTable.addContainerProperty("processName", TableLinkButton.class, null, ProcessbaseApplication.getString("tableCaptionProcess"), null, null);
        //treeTable.setColumnExpandRatio("processName", 1);
        treeTable.addContainerProperty("processDescription", String.class, null, ProcessbaseApplication.getString("tableCaptionDescription"), null, null);
        treeTable.setColumnExpandRatio("processDescription", 2);
        //treeTable.addContainerProperty("version", String.class, null, ProcessbaseApplication.getString("tableCaptionVersion"), null, null);
        treeTable.addContainerProperty("processLegislations", Component.class, null, ProcessbaseApplication.getString("processLegislationInfo"), null, null);
        treeTable.setColumnExpandRatio("processLegislations", 1);
        treeTable.setVisibleColumns(new Object[]{"category", "processDescription", "processLegislations"/*, "version"*/});
        
    }

    @Override
    public void refreshTable() {
    	if(!isInitialized())
    		initUI();
    	if(treeTable==null) return;
        treeTable.removeAllItems();
        try {
            Set<Category> categories = ProcessbaseApplication.getCurrent().getBpmModule().getAllCategories();
            Collection<LightProcessDefinition> processes = ProcessbaseApplication.getCurrent().getBpmModule().getAllowedLightProcessDefinitions(group);
           
            for (Category category : categories) {
                CategoryAndProcessDefinition capParent = new CategoryAndProcessDefinition(category, null);
                addTableRow(capParent, null);
                for (LightProcessDefinition process : processes) {
                	
                    if (process.getCategoryNames().contains(category.getName())
                    		|| (process.getCategoryNames().size()==0 && category.getName().equalsIgnoreCase("No Category"))//if there are multiple items that does not have any category then they won't be shown in tasklist Start processs list		
                    ) 
                    {
                        CategoryAndProcessDefinition cap = new CategoryAndProcessDefinition(category, process);
                        addTableRow(cap, capParent);
                    }
                }
            }
            for (Object id : treeTable.getItemIds()) {
                if (treeTable.getParent(id) == null && !treeTable.hasChildren(id)) {
                    treeTable.removeItem(id);
                }
            }
            this.rowCount = processes.size();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        treeTable.setSortContainerPropertyId("category");
        treeTable.setSortAscending(true);
        treeTable.sort();

    }

    private void addTableRow(CategoryAndProcessDefinition item, CategoryAndProcessDefinition parent) throws InstanceNotFoundException, Exception {

    	Item woItem = treeTable.addItem(item);
        if (parent == null) {
            treeTable.setChildrenAllowed(item, true);
            treeTable.setCollapsed(item, false);
            
            Button tableLinkButton = new Button(item.getCategory().getName());
            tableLinkButton.setStyleName(BaseTheme.BUTTON_LINK);

			woItem.getItemProperty("category").setValue(tableLinkButton);
        } else {
            treeTable.setChildrenAllowed(item, false);
            treeTable.setParent(item, parent);
            TableLinkButton teb = new TableLinkButton(item.getProcessDef().getLabel() != null ? item.getProcessDef().getLabel() : item.getProcessDef().getName(), item.getProcessDef().getDescription(), null, item.getProcessDef(), this, Constants.ACTION_OPEN);
            woItem.getItemProperty("category").setValue(teb);
            woItem.getItemProperty("processDescription").setValue(item.getProcessDef().getDescription());
           // woItem.getItemProperty("version").setValue(item.getProcessDef().getVersion());
            
            VerticalLayout list = new VerticalLayout();
            
			try {
				Session s = LegislationData.getCurrent().getSession();
				Transaction t = s.beginTransaction();

				Criteria c = s.createCriteria(KovLegislation.class);
				c.createAlias("processes", "p").add(
						Restrictions.eq("p.uuid", item.getProcessDef()
								.getUUID().toString()));

				for (KovLegislation legislation : (List<KovLegislation>) c
						.list()) {
					Link link = new Link(legislation.getName(),
							new ExternalResource(legislation.getUrl()));
					link.setTargetName("_blank");
					list.addComponent(link);
				}

				t.commit();
			} catch (Exception e) {
				LOGGER.error("could not find legislations", e);
			}
			
			woItem.getItemProperty("processLegislations").setValue(list);
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            try {
                LightProcessDefinition process = (LightProcessDefinition) ((TableLinkButton) event.getButton()).getTableValue();
                LightProcessDefinition refreshProcess = ProcessbaseApplication.getCurrent().getBpmModule().getLightProcessDefinition(process.getUUID());
                if (refreshProcess.getState() != ProcessState.ENABLED) {
                    treeTable.removeItem(process);
                } else {
                    openStartPage(process);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
                throw new RuntimeException(ex);
            }
        }
    }

    public void openStartPage(LightProcessDefinition process) {
        try {
            String url = ProcessbaseApplication.getCurrent().getBpmModule().getProcessMetaData(process.getUUID()).get(process.getUUID().toString());

            if (url != null && !url.isEmpty() && url.length() > 0) {
                ProcessbaseApplication.getCurrent().removeSessionAttribute("PROCESSINSTANCE");
                ProcessbaseApplication.getCurrent().removeSessionAttribute("TASKINSTANCE");
                ProcessbaseApplication.getCurrent().setSessionAttribute("PROCESSINSTANCE", process.getUUID().toString());
                this.getWindow().open(new ExternalResource(url));
            } else {
            	 NewProcessWindow ppanel=new NewProcessWindow();
            	 ppanel.initProcess(process);
            	 ppanel.initUI();
            	 this.getApplication().getMainWindow().addWindow(ppanel);
            	 ppanel.addListener(new Window.CloseListener() {
            			
						public void windowClose(CloseEvent e) {
							refreshTable();
						}
					});
            	/* BarResource barResource = BarResource.getBarResource(process.getUUID());
                 XMLProcessDefinition xmlProcess = barResource.getXmlProcessDefinition(process.getName());
                 if (!xmlProcess.isByPassFormsGeneration()) {
                     GeneratedWindow genWindow = new GeneratedWindow(process.getLabel());
                     genWindow.setProcessDef(process);
                     genWindow.setBarResource(barResource);
                     
                     
                     genWindow.initUI();
                 } else {
                     ProcessbaseApplication.getCurrent().getBpmModule().startNewProcess(process.getUUID());
                     showImportantInformation(ProcessbaseApplication.getString("processStarted"));
                 }*/
            }
        }catch (FileNotFoundException ex){ 
        	showError(ex.getMessage());
        }catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
    
    Group group;
	
    private Button processesBtn;
	
	public void setUserCurrentGroup(Group group) {
		this.group=group;
		refreshTable();
		
	}
	
	public void Handle(TaskListEvent message) {
		if(this.processesBtn==message.getButton())
		{
			refreshTable();
		}
		else if(message.getActionType()==ActionType.REFRESH){
			if(!this.processesBtn.isEnabled())
			refreshTable();			
		}
		else {
			this.processesBtn.setEnabled(true);
			this.processesBtn.setStyleName(Reindeer.BUTTON_LINK);
		}
	}

	public void setButton(Button processesBtn) {
		this.processesBtn = processesBtn;
		// TODO Auto-generated method stub
		
	}
}
