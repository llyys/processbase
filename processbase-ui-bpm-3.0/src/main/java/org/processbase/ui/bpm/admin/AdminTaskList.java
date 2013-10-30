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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.IPbTable;
import org.processbase.ui.core.template.PagedTablePanel;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableExecButtonBar;
import org.processbase.ui.core.template.TableLinkButton;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 * 
 * @author mgubaidullin
 */
public class AdminTaskList extends PagedTablePanel implements
		Button.ClickListener, IPbTable {

	private CheckBox showFinished;

	private TextField additionalFilter = null;

	public AdminTaskList() {
		super();
	}

	@Override
	public void initUI() {
		super.initUI();
		table.addContainerProperty("processName", String.class, null,
				getText("tableCaptionProcedure"), null, null);

		table.addContainerProperty("label", TableLinkButton.class, null,
				getText("tableCaptionActivityName"), null, null);

		table.addContainerProperty("type", String.class, null,
				getText("tableCaptionType"), null, null);
		
		table.addContainerProperty("initiator", String.class, null, getText("tableCaptionInitiator"), null, null);
		table.setColumnWidth("initiator", 100);

		table.addContainerProperty("assigned", String.class, null,
				getText("taskAssignedTo"), null, null);

		table.addContainerProperty("lastUpdate", Date.class, null,
				getText("tableCaptionLastUpdatedDate"), null, null);
		table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
		table.setColumnWidth("lastUpdate", 100);

		table.addContainerProperty("state", String.class, null,
				getText("tableCaptionState"), null, null);
		table.setColumnWidth("state", 100);

		setInitialized(true);
	}

	@Override
	public int load(int startPosition, int maxResults) {

		table.removeAllItems();
		try {
			BPMModule bpmModule = ProcessbaseApplication.getCurrent()
					.getBpmModule();

			Set<LightActivityInstance> ais = bpmModule.getActivityInstances();

			Set<ActivityState> stateFilters = new HashSet<ActivityState>();
			if (showFinished == null || !(Boolean) showFinished.getValue()) {
				stateFilters.add(ActivityState.READY);
				stateFilters.add(ActivityState.EXECUTING);
				stateFilters.add(ActivityState.SUSPENDED);
			}

			// Filter words
			Set<String> filterWords = new HashSet<String>();
			if (additionalFilter != null && additionalFilter.getValue() != null
					&& StringUtils.isNotBlank(additionalFilter.getValue() + "")) {
				String[] words = StringUtils.splitByWholeSeparator(
						additionalFilter.getValue() + "", " ");
				for (int i = 0; i < words.length; i++) {
					filterWords.add(words[i]);
				}
			}

			// Filter
			List<LightActivityInstance> filtered = new ArrayList<LightActivityInstance>();
			for (LightActivityInstance ai : ais) {
				if (stateFilters.size() > 0
						&& !stateFilters.contains(ai.getState())) {
					continue;
				}

				if (filterWords.size() > 0) {
					String processName = ai.getProcessDefinitionUUID()
							.toString();

					StringBuilder link = new StringBuilder(
							ai.getActivityLabel() != null ? ai
									.getActivityLabel() : ai.getActivityName());

					if (ai.getDynamicLabel() != null
							&& ai.getDynamicDescription() != null) {
						link.append("(").append(ai.getDynamicLabel())
								.append(" - ")
								.append(ai.getDynamicDescription()).append(")");
					} else if (ai.getDynamicLabel() != null
							&& ai.getDynamicDescription() == null) {
						link.append("(").append(ai.getDynamicLabel())
								.append(")");
					} else if (ai.getDynamicLabel() != null
							&& ai.getDynamicDescription() != null) {
						link.append("(").append(ai.getDynamicDescription())
								.append(")");
					}
					
					String taskName = link.toString();

					boolean contains = true;
					for (String w : filterWords) {
						if (!StringUtils.containsIgnoreCase(processName, w) && 
								!StringUtils.containsIgnoreCase(taskName, w)) {
							contains = false;
							break;
						}
					}
					if (!contains) {
						continue;
					}
				}

				filtered.add(ai);
			}

			// Let sort list
			Collections.sort(filtered, new Comparator<LightActivityInstance>() {
				public int compare(LightActivityInstance o1,
						LightActivityInstance o2) {
					return o2.getLastUpdateDate().compareTo(
							o1.getLastUpdateDate());
				}
			});

			int from = startPosition < filtered.size() ? startPosition
					: filtered.size();
			int to = (startPosition + maxResults) < filtered.size() ? (startPosition + maxResults)
					: filtered.size();

			List<LightActivityInstance> page = filtered.subList(from, to);

			for (LightActivityInstance ai : page) {
				Item woItem = table.addItem(ai);

				LightProcessDefinition lpd = bpmModule
						.getLightProcessDefinition(ai
								.getProcessDefinitionUUID());

				String processName = lpd.getLabel() != null ? lpd.getLabel()
						: lpd.getName();
				String processInstanceUUID = ai.getProcessInstanceUUID()
						.toString();
				woItem.getItemProperty("processName").setValue(
						processName
								+ "  #"
								+ processInstanceUUID
										.substring(processInstanceUUID
												.lastIndexOf("--") + 2));

				StringBuilder link = new StringBuilder(
						ai.getActivityLabel() != null ? ai.getActivityLabel()
								: ai.getActivityName());

				if (ai.getDynamicLabel() != null
						&& ai.getDynamicDescription() != null) {
					link.append("(").append(ai.getDynamicLabel()).append(" - ")
							.append(ai.getDynamicDescription()).append(")");
				} else if (ai.getDynamicLabel() != null
						&& ai.getDynamicDescription() == null) {
					link.append("(").append(ai.getDynamicLabel()).append(")");
				} else if (ai.getDynamicLabel() != null
						&& ai.getDynamicDescription() != null) {
					link.append("(").append(ai.getDynamicDescription())
							.append(")");
				}

				TableLinkButton teb = new TableLinkButton(link.toString(),
						ai.getActivityDescription(), null, ai, this,
						Constants.ACTION_OPEN);
				woItem.getItemProperty("label").setValue(teb);
				woItem.getItemProperty("lastUpdate").setValue(
						ai.getLastUpdateDate());
				woItem.getItemProperty("state").setValue(getText(ai.getState().toString()));
				if (ai.isTask()) {
					woItem.getItemProperty("type").setValue(getText("task"));
				} else if (ai.isAutomatic()) {
					woItem.getItemProperty("type").setValue(
							getText("automatic"));
				} else if (ai.isTimer()) {
					woItem.getItemProperty("type").setValue(getText("timer"));
				} else if (ai.isSubflow()) {
					woItem.getItemProperty("type").setValue(getText("subflow"));
				}

				if (ai.getTask() != null && ai.getTask().isTaskAssigned()) {
					woItem.getItemProperty("assigned").setValue(
							ai.getTask().getTaskUser());
				}
				
				ProcessInstanceUUID processUuid = ai.getProcessInstanceUUID();
				if(ai.getRootInstanceUUID() != null){
					processUuid = ai.getRootInstanceUUID();
				}
				try{
					LightProcessInstance process = bpmModule.getLightProcessInstance(processUuid);
					woItem.getItemProperty("initiator").setValue(process.getStartedBy());
				}catch (Exception e) {
					e.printStackTrace();
				}

			}

			table.setSortContainerPropertyId("lastUpdate");
			table.setSortAscending(false);
			table.sort();

			return page.size();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public void buttonClick(ClickEvent event) {
		if (event.getButton() instanceof TableLinkButton) {
			TableLinkButton execBtn = (TableLinkButton) event.getButton();
			LightActivityInstance activity = (LightActivityInstance) execBtn
					.getTableValue();
			try {
				if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
					ActivityWindow activityWindow = new ActivityWindow(activity);
					getApplication().getMainWindow().addWindow(activityWindow);
					activityWindow.initUI();
					activityWindow.addListener(new Window.CloseListener() {

						public void windowClose(CloseEvent e) {
							refreshTable();
						}
					});
				} else if (execBtn.getAction().equals(Constants.ACTION_STOP)) {
					ProcessbaseApplication
							.getCurrent()
							.getBpmModule()
							.stopExecution(activity.getProcessInstanceUUID(),
									activity.getActivityName());
					Item woItem = table.getItem(activity);
					woItem.getItemProperty("state").setValue(
							ActivityState.CANCELLED);
					TableExecButtonBar tebb = new TableExecButtonBar();
					tebb.addButton(new TableLinkButton(getText("btnOpen"),
							"icons/document.png", activity, this,
							Constants.ACTION_OPEN));
					woItem.getItemProperty("actions").setValue(tebb);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}



	public void setAdditionalFilter(TextField additionalFilter) {
		this.additionalFilter = additionalFilter;
	}

	public void setShowFinished(CheckBox showFinished) {
		this.showFinished = showFinished;
	}

}
