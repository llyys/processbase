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
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.IPbTable;
import org.processbase.ui.core.template.PagedTablePanel;
import org.processbase.ui.core.template.PbColumnGenerator;
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
public class AdminCaseList extends PagedTablePanel implements
		Button.ClickListener, IPbTable {

	private CheckBox showFinished;

	private TextField additionalFilter = null;

	public AdminCaseList() {
		super();
	}

	@Override
	public void initUI() {
		super.initUI();
		table.addContainerProperty("name", TableLinkButton.class, null,
				getText("tableCaptionProcedure"), null, null);
		table.setColumnExpandRatio("name", 1);

		table.addContainerProperty("initiator", String.class, null,
				getText("tableCaptionInitiator"), null, null);
		table.setColumnWidth("initiator", 100);

		table.addContainerProperty("version", String.class, null,
				getText("tableCaptionVersion"), null, null);
		table.setColumnWidth("version", 100);

		table.addContainerProperty("startedDate", Date.class, null,
				getText("tableCaptionStartedDate"), null, null);
		table.addGeneratedColumn("startedDate", new PbColumnGenerator());
		table.setColumnWidth("startedDate", 100);

		table.addContainerProperty("lastUpdate", Date.class, null,
				getText("tableCaptionLastUpdate"), null, null);
		table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
		table.setColumnWidth("lastUpdate", 100);

		table.addContainerProperty("state", String.class, null,
				getText("tableCaptionState"), null, null);
		table.setColumnWidth("state", 90);

		table.setVisibleColumns(new Object[] { "name", "initiator", "version",
				"startedDate", "lastUpdate", "state" });

		setInitialized(true);
	}

	@Override
	public int load(int startPosition, int maxResults) {
		try {
			table.removeAllItems();

			Set<InstanceState> statesFilters = new HashSet<InstanceState>();
			if (showFinished == null || !((Boolean) showFinished.getValue())) {
				statesFilters.add(InstanceState.STARTED);
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

			BPMModule bpmModule = ProcessbaseApplication.getCurrent()
					.getBpmModule();

			List<LightProcessInstance> results = new ArrayList<LightProcessInstance>();
			results.addAll(bpmModule.getLightProcessInstances());

			// Filter process instances
			List<LightProcessInstance> filtered = new ArrayList<LightProcessInstance>();

			for (LightProcessInstance pi : results) {
				if (statesFilters.size() > 0
						&& !statesFilters.contains(pi.getInstanceState())) {
					continue;
				}

				if (filterWords.size() > 0) {

					String name = pi.getUUID().toString().split("--")[0]
							+ "  #" + pi.getNb();

					boolean contains = true;
					for (String w : filterWords) {
						if (!StringUtils.containsIgnoreCase(name, w)) {
							contains = false;
							break;
						}
					}
					if (!contains) {
						continue;
					}
				}

				filtered.add(pi);
			}

			// Let sort list
			Collections.sort(filtered, new Comparator<LightProcessInstance>() {
				public int compare(LightProcessInstance o1,
						LightProcessInstance o2) {
					return o2.getStartedDate().compareTo(o1.getStartedDate());
				}
			});

			int from = startPosition < filtered.size() ? startPosition
					: filtered.size();
			int to = (startPosition + maxResults) < filtered.size() ? (startPosition + maxResults)
					: filtered.size();

			List<LightProcessInstance> page = filtered.subList(from, to);

			for (LightProcessInstance pi : page) {

				Item woItem = table.addItem(pi);

				String pdUUID = pi.getProcessDefinitionUUID().toString();

				TableLinkButton teb = new TableLinkButton(pdUUID.split("--")[0]
						+ "  #" + pi.getNb(), null, null, pi, this,
						Constants.ACTION_OPEN);

				woItem.getItemProperty("name").setValue(teb);
				woItem.getItemProperty("initiator").setValue(pi.getStartedBy());
				woItem.getItemProperty("startedDate").setValue(
						pi.getStartedDate());
				woItem.getItemProperty("version").setValue(
						pdUUID.split("--")[1]);

				// Find all tasks
				List<LightTaskInstance> tasks = new ArrayList<LightTaskInstance>();
				try {
					tasks.addAll(bpmModule.getLightTasks(pi
							.getProcessInstanceUUID()));
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Sort tasks
				Collections.sort(tasks, new Comparator<LightTaskInstance>() {

					public int compare(LightTaskInstance o2,
							LightTaskInstance o1) {
						return o1.getLastUpdateDate().compareTo(
								o2.getLastUpdateDate());
					}
				});
				if (!tasks.isEmpty()) {
					woItem.getItemProperty("lastUpdate").setValue(
							tasks.get(0).getLastUpdateDate());
				} else {
					woItem.getItemProperty("lastUpdate").setValue(
							pi.getLastUpdate());
				}

				woItem.getItemProperty("state").setValue(getText(pi.getInstanceState().toString()));
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
			try {
				TableLinkButton execBtn = (TableLinkButton) event.getButton();
				LightProcessInstance process = (LightProcessInstance) ((TableLinkButton) event
						.getButton()).getTableValue();
				if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
					ProcessInstanceWindow window = new ProcessInstanceWindow(
							process, true);
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


	public void setShowFinished(CheckBox showFinished) {
		this.showFinished = showFinished;
	}

	public void setAdditionalFilter(TextField additionalFilter) {
		this.additionalFilter = additionalFilter;
	}

}
