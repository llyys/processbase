package org.processbase.ui.bpm.identity.sync;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Update candidates list task.
 * 
 * @author Margo
 */
public class UpdateCanditadeListsTask extends Thread {

	private static final Logger LOG = LoggerFactory
			.getLogger(UpdateCanditadeListsTask.class);

	private BPMModule bpmModule;

	private String user;
	private Set<String> changeAssigned;

	public UpdateCanditadeListsTask(String user, Set<String> changeAssigned) {
		super();
		this.user = user;
		this.changeAssigned = changeAssigned;
		bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
	}

	@Override
	public void run() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("UpdateCanditadeListsTask started");
		}

		try {
			bpmModule.initContext();
		} catch (Exception e) {
			LOG.warn("could not init context", e);
		}

		// Update candidates list
		QueryRuntimeAPI queryApi = bpmModule.getQueryRuntimeAPI();
		RuntimeAPI runtimeApi = bpmModule.getRuntimeAPI();

		List<ActivityState> states = new ArrayList<ActivityState>();
		states.add(ActivityState.READY);
		states.add(ActivityState.EXECUTING);

		// Find all active process instances
		Set<ProcessInstance> processInstances = queryApi
				.getProcessInstancesWithTaskState(states);

		for (ProcessInstance process : processInstances) {
			for (TaskInstance task : process.getTasks()) {
				if (ActivityState.READY.equals(task.getState())
						|| ActivityState.EXECUTING.equals(task.getState())) {

					// If task is assigned to user who started process skip
					// recalc
					if (task.getTaskUser() != null
							&& task.getTaskUser()
									.equals(process.getStartedBy())) {
						continue;
					}

					try {
						String assigned = null;
						if (task.isTaskAssigned()) {
							assigned = task.getTaskUser();
						}

						// Current candidates
						Set<String> current = new HashSet<String>();
						current.addAll(task.getTaskCandidates());

						// Find new candidates
						runtimeApi.assignTask(task.getUUID());

						// Get new candidates and add them to list
						TaskInstance t = queryApi.getTask(task.getUUID());

						Set<String> newCandidates = new HashSet<String>();
						newCandidates.addAll(t.getTaskCandidates());

						// Set candidates
						runtimeApi.assignTask(task.getUUID(), newCandidates);

						if (assigned != null) {
							// If currently assigned to user
							if (assigned.equals(user)) {

								// Still one of the candidates
								if (newCandidates.contains(current)) {
									if (LOG.isDebugEnabled()) {
										LOG.debug("assign back to " + assigned);
									}
									runtimeApi.assignTask(task.getUUID(),
											assigned);
								} else {
									// Not candidate any more
									if (LOG.isDebugEnabled()) {
										LOG.debug("remove assigned to " + user);
									}
								}
							} else {
								//If we need change assinged to
								if (changeAssigned.contains(assigned)) {
									if (LOG.isDebugEnabled()) {
										LOG.debug("assign from " + assigned
												+ " to " + user);
									}
									runtimeApi.assignTask(task.getUUID(),
											user);
								} else {
									if (LOG.isDebugEnabled()) {
										LOG.debug("assign back to " + assigned);
									}
									runtimeApi.assignTask(task.getUUID(),
											assigned);
								}
							}

						}

					} catch (Exception e) {
						LOG.warn("could not update candidate list", e);
					}
				}
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("UpdateCanditadeListsTask finished");
		}
	}

}
