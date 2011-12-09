package org.processbase.ui.bpm.panel.events;

import org.processbase.ui.osgi.PbPanelModule;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

public class TaskListEvent {
	
	private ActionType _action;
	private Button button;
	private PbPanelModule parentContainer;

	public enum ActionType {
	    REFRESH, TOGGLE_PANEL 
	}
	
	public void setActionType(ActionType _action) {
		this._action = _action;
	}

	public ActionType getActionType() {
		return _action;
	}

	public void setButton(Button button) {
		this.button = button;
	}

	public void setParentContainer(PbPanelModule parentContainer) {
		this.parentContainer = parentContainer;
	}

	public Button getButton() {
		return this.button;
	}

	public PbPanelModule getParentContainer() {
		return this.parentContainer;
		
	}
}
