package org.processbase.ui.bpm.worklist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.caliburn.application.event.IHandle;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.ui.bpm.generator.view.OpenProcessWindow;
import org.processbase.ui.bpm.panel.events.TaskListEvent;
import org.processbase.ui.bpm.panel.events.TaskListEvent.ActionType;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.IPbTable;
import org.processbase.ui.core.template.PagedTablePanel;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;

import com.vaadin.data.Item;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

/**
 * Panel shows all processes that user has initialized, but now the process
 * needs some user interaction.
 * 
 * @author lauri
 */
public class UserTaskList extends PagedTablePanel implements IPbTable,
		Button.ClickListener, IHandle<TaskListEvent> {

	private Button menuBtn;
	
	private boolean initialized = false;

	@Override
	public void initUI() {
		if (initialized){
			return;
		}
		initialized = true;
		
		super.initUI();

		table.addContainerProperty("processName", Component.class, null,
				getText("tableCaptionProcedure"), null, null);
		table.setColumnExpandRatio("processName", 1);

		table.addContainerProperty("taskName", Label.class, null,
				getText("tableCaptionTask"), null, null);
		table.setColumnExpandRatio("taskName", 2);

		table.addContainerProperty("lastUpdate", Date.class, null,
				getText("tableCaptionLastUpdatedDate"), null, null);
		table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());

		table.addContainerProperty("expectedEndDate", Date.class, null,
				getText("tableCaptionExpectedEndDate"), null, null);
		table.addGeneratedColumn("expectedEndDate", new PbColumnGenerator());

		table.addContainerProperty("state", String.class, null,
				getText("tableCaptionState"), null, null);

		table.setVisibleColumns(new Object[] { "processName", "taskName",
				"lastUpdate", "expectedEndDate", "state" });
		setInitialized(true);
	}

	@Override
	public int load(int startPosition, int maxResults) {

		int results = 0;

		table.removeAllItems();
		try {
			BPMModule bpmModule = ProcessbaseApplication.getCurrent()
					.getBpmModule();
			
			List<LightTaskInstance> tasks = new ArrayList<LightTaskInstance>();
			tasks.addAll(bpmModule.getLightTaskList(ActivityState.READY));
			tasks.addAll(ProcessbaseApplication.getCurrent().getBpmModule()
					.getLightTaskList(ActivityState.EXECUTING));
			tasks.addAll(ProcessbaseApplication.getCurrent().getBpmModule()
					.getLightTaskList(ActivityState.SUSPENDED));

			// Filter tasks
			List<LightTaskInstance> filtered = new ArrayList<LightTaskInstance>();
			filtered.addAll(tasks);

			// Sort
			Collections.sort(filtered, new Comparator<LightTaskInstance>() {

				public int compare(LightTaskInstance o1, LightTaskInstance o2) {
					return o2.getLastUpdateDate().compareTo(
							o1.getLastUpdateDate());
				}
			});

			int from = startPosition < filtered.size() ? startPosition
					: filtered.size();
			int to = (startPosition + maxResults) < filtered.size() ? (startPosition + maxResults)
					: filtered.size();

			// Get page
			List<LightTaskInstance> page = filtered.subList(from, to);

			for (LightTaskInstance task : page) {
				addTableRow(task);
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

	private void addTableRow(LightTaskInstance task) throws InstanceNotFoundException,
			Exception {
		Item woItem = table.addItem(task);
		try {
			BPMModule bpmModule = ProcessbaseApplication.getCurrent()
					.getBpmModule();
			LightProcessDefinition lpd = bpmModule
					.getLightProcessDefinition(task.getProcessDefinitionUUID());

			String processName = lpd.getLabel() != null ? lpd.getLabel() : lpd.getName();
			
			TableLinkButton teb = new TableLinkButton(
					processName,
					lpd.getDescription(), null, task, this,
					Constants.ACTION_OPEN);
			woItem.getItemProperty("processName").setValue(teb);

			String taskTitle = task.getDynamicLabel() != null ? task
					.getDynamicLabel() : task.getActivityLabel();
			String taskDescription = task.getDynamicDescription() != null ? (" - " + task
					.getDynamicDescription()) : "";
			taskDescription = taskDescription
					+ bpmModule.getTaskComment(task.getUUID());

			woItem.getItemProperty("taskName").setValue(
					new Label("<b>" + taskTitle + "</b><i>" + taskDescription
							+ "</i>", Label.CONTENT_XHTML));
			woItem.getItemProperty("lastUpdate").setValue(
					task.getLastUpdateDate());
			woItem.getItemProperty("expectedEndDate").setValue(
					task.getExpectedEndDate());
			woItem.getItemProperty("state").setValue(
					getText(task.getState().toString()));
		} catch (Exception e) {
			e.printStackTrace();

			Label label = new Label(task.getActivityName());
			label.setComponentError(new UserError(e.getMessage()));
			woItem.getItemProperty("taskName").setValue(label);
		}

	}

	public void buttonClick(ClickEvent event) {
		if (event.getButton() instanceof TableLinkButton) {
			try {
				LightTaskInstance task = (LightTaskInstance) ((TableLinkButton) event
						.getButton()).getTableValue();
				openTaskPage(task);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void Handle(TaskListEvent message) {
		if (this.menuBtn == message.getButton()) {
			refreshTable();

		} else if (message.getActionType() == ActionType.REFRESH) {
			if (!this.menuBtn.isEnabled())
				refreshTable();
		} else {
			this.menuBtn.setEnabled(true);
			this.menuBtn.setStyleName(Reindeer.BUTTON_LINK);
		}
	}

	public void setButton(Button menuBtn) {
		this.menuBtn = menuBtn;
	}

	public void openTaskPage(LightTaskInstance task) {
		try {
			BPMModule bpmModule = ProcessbaseApplication.getCurrent()
					.getBpmModule();
			LightTaskInstance newTask = bpmModule.getTaskInstance(task
					.getUUID());
			if (newTask == null
					|| newTask.getState().equals(ActivityState.FINISHED)
					|| newTask.getState().equals(ActivityState.ABORTED)) {
				table.removeItem(task);
				return;
			}
			Map<String, String> processMetaData = bpmModule
					.getProcessMetaData(task.getProcessDefinitionUUID());
			String url = processMetaData.get(task.getActivityDefinitionUUID()
					.toString());
			if (url != null && !url.isEmpty() && url.length() > 0) {
				ProcessbaseApplication.getCurrent().removeSessionAttribute(
						"PROCESSINSTANCE");
				ProcessbaseApplication.getCurrent().removeSessionAttribute(
						"TASKINSTANCE");

				ProcessbaseApplication.getCurrent().setSessionAttribute(
						"TASKINSTANCE", task.getUUID().toString());
				this.getWindow().open(new ExternalResource(url));
			} else {
				OpenProcessWindow opw = new OpenProcessWindow();
				opw.initTask(newTask);
				opw.initUI();
				this.getApplication().getMainWindow().addWindow(opw);
				opw.addListener(new Window.CloseListener() {

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
