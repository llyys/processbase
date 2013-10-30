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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.caliburn.application.event.IHandle;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.ui.bpm.admin.ProcessInstanceWindow;
import org.processbase.ui.bpm.generator.view.OpenProcessWindow;
import org.processbase.ui.bpm.panel.events.TaskListEvent;
import org.processbase.ui.bpm.panel.events.TaskListEvent.ActionType;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.IPbTable;
import org.processbase.ui.core.template.PagedTablePanel;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

/**
 * Kasutaja poolt algatatud ja talle suunatud menetlused. (Nimekirjast avaneb
 * menetlusjuhtumi vaade - Joonis + läbitud sammud)
 * 
 * @author lauri
 */
public class UserCaseList extends PagedTablePanel implements IPbTable,
		Button.ClickListener, IHandle<TaskListEvent> {

	private Button menuBtn;
	private CheckBox showFinished;

	public UserCaseList() {
		super();
	}

	@Override
	public void initUI() {
		super.initUI();
		table.addContainerProperty("icon", ThemeResource.class, null);
		table.setColumnWidth("icon", 30);
		table.setItemIconPropertyId("icon");
		table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);

		table.addContainerProperty("state", String.class, null,
				getText("tableCaptionState"), null, null);
		table.setColumnWidth("state", 90);

		table.addContainerProperty("name", Component.class, null,
				getText("tableCaptionProcedure"), null, null);
		table.setColumnExpandRatio("name", 0.3F);

		table.addContainerProperty("started", Date.class, null,
				getText("tableCaptionStartedDate"), null, null);
		table.addGeneratedColumn("started", new PbColumnGenerator());
		table.setColumnWidth("started", 110);

		table.addContainerProperty("lastUpdate", Date.class, null,
				getText("tableCaptionLastUpdate"), null, null);
		table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
		table.setColumnWidth("lastUpdate", 110);

		table.setSortDisabled(false);

		table.setVisibleColumns(new Object[] { "name", "started", "lastUpdate",
				"state" });
		setInitialized(true);
	}

	@Override
	public int load(int startPosition, int maxResults) {

		int results = 0;

		table.removeAllItems();
		try {
			ProcessbaseApplication application = ProcessbaseApplication
					.getCurrent();

			// 1. load all tasks assigned to logged user
			Set<String> pids = new HashSet<String>();

			BPMModule bpmModule = ProcessbaseApplication.getCurrent()
					.getBpmModule();

			List<LightTaskInstance> tasks = new ArrayList<LightTaskInstance>();
			tasks.addAll(bpmModule.getUserLightTaskList(
					application.getUserName(), ActivityState.READY));
			tasks.addAll(bpmModule.getUserLightTaskList(
					application.getUserName(), ActivityState.EXECUTING));
			tasks.addAll(bpmModule.getUserLightTaskList(
					application.getUserName(), ActivityState.SUSPENDED));

			if (showFinished != null && (Boolean) showFinished.getValue()) {
				tasks.addAll(bpmModule.getUserLightTaskList(
						application.getUserName(), ActivityState.FINISHED));
				tasks.addAll(bpmModule.getUserLightTaskList(
						application.getUserName(), ActivityState.ABORTED));
				tasks.addAll(bpmModule.getUserLightTaskList(
						application.getUserName(), ActivityState.SKIPPED));
				tasks.addAll(bpmModule.getUserLightTaskList(
						application.getUserName(), ActivityState.CANCELLED));
			}

			List<LightProcessInstance> processes = new ArrayList<LightProcessInstance>();
			Map<LightProcessInstance, LightTaskInstance> processTask = new HashMap<LightProcessInstance, LightTaskInstance>();

			for (LightTaskInstance task : tasks) {
				if (task.isTask()
						&& task.isTaskAssigned()
						&& task.getTaskUser().equalsIgnoreCase(
								application.getUserName())) {
					try {
						LightProcessInstance process = bpmModule
								.getLightProcessInstance(task
										.getProcessInstanceUUID());

						// If not root process (#1491)
						if (process.getParentInstanceUUID() != null) {
							continue;
						}

						String pid = task.getProcessInstanceUUID().toString();
						if (!pids.contains(pid)) {
							pids.add(pid);// Add to set so we don't show
											// dublicate processes
							processes.add(process);
							processTask.put(process, task);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// 2. load all instances started by the logged user
			Set<LightProcessInstance> processInstances = application
					.getBpmModule().getLightUserInstances();
			for (LightProcessInstance process : processInstances) {

				// If not root process (#1491)
				if (process.getParentInstanceUUID() != null) {
					continue;
				}

				String pid = process.getProcessInstanceUUID().toString();
				if (process.getInstanceState() == InstanceState.STARTED
						&& !pids.contains(pid)) {
					processes.add(process);
				}
			}

			// Let sort list
			Collections.sort(processes, new Comparator<LightProcessInstance>() {
				public int compare(LightProcessInstance o1,
						LightProcessInstance o2) {
					return o2.getStartedDate().compareTo(o1.getStartedDate());
				}
			});

			int from = startPosition < processes.size() ? startPosition
					: processes.size();
			int to = (startPosition + maxResults) < processes.size() ? (startPosition + maxResults)
					: processes.size();

			List<LightProcessInstance> page = processes.subList(from, to);

			for (LightProcessInstance process : page) {
				if (processTask.containsKey(process)) {
					addTableRow(process, processTask.get(process));
				} else {
					addTableRow(process, null);
				}
			}

			results = page.size();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		table.setSortContainerPropertyId("lastUpdate");
		table.setSortAscending(false);
		table.sort();

		return results;
	}

	private void addTableRow(LightProcessInstance process,
			LightTaskInstance task) {

		Item woItem = table.addItem(process != null ? process : task);

		ProcessDefinitionUUID processDefinitionUUID = process
				.getProcessDefinitionUUID();

		ThemeResource icon = null;
		if (task != null) {
			icon = new ThemeResource("icons/document.png");
		} else {
			icon = new ThemeResource("icons/lock.png");
		}

		woItem.getItemProperty("icon").setValue(icon);

		BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
		
		
		
		String caption =  processDefinitionUUID.toString().split("--")[0];
		
		LightProcessDefinition lpd = null;
		try {
			lpd = bpmModule
				.getLightProcessDefinition(processDefinitionUUID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(lpd != null){
			caption = lpd.getLabel() != null ? lpd.getLabel() : lpd.getName();
		}
		
//		String caption = pdUUID.split("--")[0] + "  #" + process.getNb();
//		
//		if (task != null) {
//			String comment = ProcessbaseApplication.getCurrent().getBpmModule()
//					.getProcessComment(task.getProcessInstanceUUID());
//			if (StringUtils.isNotEmpty(comment))
//				caption = pdUUID.split("--")[0] + "  #" + comment;
//		}

		TableLinkButton teb = new TableLinkButton(caption, null, null, process,
				this, process != null ? "process" : "task");
		teb.setTableValue(process != null ? process : task);
		woItem.getItemProperty("name").setValue(teb);

		woItem.getItemProperty("started").setValue(process.getStartedDate());

		// Find last updated state
		try {
			List<LightTaskInstance> tasks = new ArrayList<LightTaskInstance>();
			tasks.addAll(bpmModule.getLightTasks(process
					.getProcessInstanceUUID()));

			Collections.sort(tasks, new Comparator<LightTaskInstance>() {

				public int compare(LightTaskInstance o2, LightTaskInstance o1) {
					return o1.getLastUpdateDate().compareTo(
							o2.getLastUpdateDate());
				}
			});

			if (!tasks.isEmpty()) {
				woItem.getItemProperty("lastUpdate").setValue(
						tasks.get(0).getLastUpdateDate());
			} else {
				woItem.getItemProperty("lastUpdate").setValue("");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Property stateColumn = woItem.getItemProperty("state");
		stateColumn.setValue(ProcessbaseApplication.getCurrent()
				.getPbMessages()
				.getString(process.getInstanceState().toString()));
	}

	public void buttonClick(ClickEvent event) {
		if (event.getButton() instanceof TableLinkButton) {
			try {
				TableLinkButton execBtn = (TableLinkButton) event.getButton();

				if (execBtn.getAction().equals("task")) {
					// will be able to execute the process
					LightTaskInstance process = (LightTaskInstance) ((TableLinkButton) execBtn)
							.getTableValue();
					OpenProcessWindow opw = new OpenProcessWindow();
					opw.initTask(process);
					opw.initUI();
					this.getWindow().addWindow(opw);
					opw.addListener(new Window.CloseListener() {

						public void windowClose(CloseEvent e) {
							refreshTable();
						}
					});
				} else {
					LightProcessInstance process = (LightProcessInstance) ((TableLinkButton) execBtn)
							.getTableValue();
					ProcessInstanceWindow window = new ProcessInstanceWindow(
							process, false);
					this.getWindow().addWindow(window);
					window.initUI();
					window.addListener(new Window.CloseListener() {

						public void windowClose(CloseEvent e) {
							refreshTable();
						}
					});
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void Handle(TaskListEvent message) {
		if (this.menuBtn == message.getButton()) {
			refreshTable();

			if (showFinished != null) {
				showFinished.setVisible(true);
			}
		} else if (message.getActionType() == ActionType.REFRESH) {
			if (!this.menuBtn.isEnabled())
				refreshTable();
		} else {
			this.menuBtn.setEnabled(true);
			this.menuBtn.setStyleName(Reindeer.BUTTON_LINK);

			if (showFinished != null) {
				showFinished.setVisible(false);
			}
		}
	}

	public void setButton(Button menuBtn) {
		this.menuBtn = menuBtn;
	}

	public void setShowFinished(CheckBox showFinished) {
		this.showFinished = showFinished;
	}

}
