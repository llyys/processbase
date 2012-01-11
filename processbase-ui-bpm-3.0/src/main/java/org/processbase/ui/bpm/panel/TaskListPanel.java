/**
 * Copyright (C) 2010 PROCESSBASE
 * PROCESSBASE Ltd, Almaty, Kazakhstan
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
package org.processbase.ui.bpm.panel;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.caliburn.application.event.IEventAggregator;
import org.caliburn.application.event.imp.DefaultEventAggregator;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.ProfileMetadata;
import org.ow2.bonita.facade.identity.User;
import org.processbase.ui.bpm.panel.events.TaskListEvent;
import org.processbase.ui.bpm.panel.events.TaskListEvent.ActionType;
import org.processbase.ui.bpm.worklist.NewProcesses;
import org.processbase.ui.bpm.worklist.UserCaseList;
import org.processbase.ui.bpm.worklist.TaskCompleted;
import org.processbase.ui.bpm.worklist.CandidateTaskList;
import org.processbase.ui.bpm.worklist.UserTaskList;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.ui.core.template.TreeTablePanel;
import org.processbase.ui.core.template.WorkPanel;
import org.processbase.ui.osgi.PbPanelModule;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 * @author mgubaidullin
 */
public class TaskListPanel extends PbPanelModule implements Button.ClickListener {

    private ButtonBar buttonBar = new ButtonBar();
    private CandidateTaskList pnlTaskList;
    //private TaskCompleted taskCompletedPanel; 
    private UserCaseList pnlRoleProcesses; 
    private NewProcesses pnlNewProcesses;
    private Button refreshBtn = null;
    private Button btnTaskList = null;
    //private Button myTaskCompletedBtn = null;
    private Button btnRoleProcesses = null;
    private Button btnNewProcess = null;
    
	private com.vaadin.ui.ComboBox roleCombo;
	private Button myTaskBtn;
	private IEventAggregator events;
	private Button btnUserTaskList;
	private UserTaskList pnlUserTaskList;

    public void initUI(){
    	if(isInitialized())
    		return;
        setMargin(true);
        
       setInitialized(true);
        pnlNewProcesses = new NewProcesses();
        
        if(ProcessbaseApplication.getCurrent().getUserName()==BPMModule.USER_GUEST)
        {
        	addComponent(pnlNewProcesses, 0);
        	setExpandRatio(pnlNewProcesses, 1);
        	pnlNewProcesses.initUI();
        	pnlNewProcesses.refreshTable();        	
        	return;
        }
        
        addComponent(buttonBar);
        buttonBar.setWidth("100%");
//Teenuste nimekiri
        
                
        btnNewProcess = new Button(ProcessbaseApplication.getString("myNewProcessesBtn"), this);
        btnNewProcess.setStyleName(Reindeer.BUTTON_LINK);
        btnNewProcess.setData(pnlNewProcesses);
        btnNewProcess.setEnabled(false);//make active
        btnNewProcess.setStyleName("special");
        
        buttonBar.addComponent(btnNewProcess);
        buttonBar.setComponentAlignment(btnNewProcess, Alignment.MIDDLE_LEFT);
        
        pnlNewProcesses.setButton(btnNewProcess);        
        addComponent(pnlNewProcesses, 1);
        setExpandRatio(pnlNewProcesses, 1);      
        pnlNewProcesses.initUI();
    	pnlNewProcesses.refreshTable();
    	
//Menetluste nimekiri
        
    	btnTaskList = new Button(ProcessbaseApplication.getString("myTaskListBtn"), this);
    	btnTaskList.setStyleName(Reindeer.BUTTON_LINK);
    	pnlTaskList = new CandidateTaskList();
        pnlTaskList.setButton(btnTaskList);
        
                
        btnTaskList.setData(pnlTaskList);
        btnTaskList.setEnabled(true);        
        buttonBar.addComponent(btnTaskList);
        buttonBar.setComponentAlignment(btnTaskList, Alignment.MIDDLE_LEFT);

//kasutajale suunatud menetlused
        
        btnUserTaskList = new Button(ProcessbaseApplication.getString("userTaskListBtn"), this);
        btnUserTaskList.setStyleName(Reindeer.BUTTON_LINK);
        
        pnlUserTaskList = new UserTaskList();
        
        btnUserTaskList.setData(pnlUserTaskList);
        pnlUserTaskList.setButton(btnUserTaskList);
        buttonBar.addComponent(btnUserTaskList);
        buttonBar.setComponentAlignment(btnUserTaskList, Alignment.MIDDLE_LEFT);
        
// Kasutaja rollile suuantud menetlused
        
        btnRoleProcesses = new Button(ProcessbaseApplication.getString("myProcessesBtn"), this);
        buttonBar.addComponent(btnRoleProcesses);
        buttonBar.setComponentAlignment(btnRoleProcesses, Alignment.MIDDLE_LEFT);
        btnRoleProcesses.setStyleName(Reindeer.BUTTON_LINK);
        btnRoleProcesses.setEnabled(true);
        
        pnlRoleProcesses=new UserCaseList();        
        pnlRoleProcesses.setButton(btnRoleProcesses);
        btnRoleProcesses.setData(pnlRoleProcesses);        
                
/*
// Minu taskid
        myTaskBtn = new Button(ProcessbaseApplication.getString("myTaskBtn", "My task"), this);
        myTaskBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(myTaskBtn);
        myTaskBtn.setData("myTaskBtn");        
        buttonBar.setComponentAlignment(myTaskBtn, Alignment.MIDDLE_LEFT);
*/
        
// prepare refresh button
        refreshBtn = new Button(ProcessbaseApplication.getString("btnRefresh"));        
        buttonBar.addComponent(refreshBtn); 
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);        
        buttonBar.setExpandRatio(refreshBtn, 1);
        
        //Register event agrigator
        events = new DefaultEventAggregator();
        
        events.Subscribe(pnlRoleProcesses);
        events.Subscribe(pnlNewProcesses);
        events.Subscribe(pnlUserTaskList);
        events.Subscribe(pnlTaskList);
        
        refreshBtn.addListener(new Button.ClickListener() {			
			public void buttonClick(ClickEvent event) {
				TaskListEvent message = new TaskListEvent();
				message.setButton(event.getButton());
				message.setActionType(ActionType.REFRESH);
				events.Publish(message);
			}
		});
        
                
    }

   
    public void buttonClick(ClickEvent event) {
    	
    	Button button = event.getButton();
		Component component = (Component)button.getData();
		
		Component component2 = getComponent(1);
		replaceComponent(component2, component);
    	setExpandRatio(component, 1);
    	
    	button.setEnabled(false);//button that is clicked will be disabled now
    	button.setStyleName("special");
    	TaskListEvent message = new TaskListEvent();
		message.setButton(event.getButton());
		message.setActionType(ActionType.TOGGLE_PANEL);
		message.setParentContainer(this);
		
		events.Publish(message);    	
    }

    
    @Override
    public String getTitle(Locale locale) {
        ResourceBundle rb = ResourceBundle.getBundle("MessagesBundle", locale);
        return rb.getString("bpmTasklist");
    }
}
