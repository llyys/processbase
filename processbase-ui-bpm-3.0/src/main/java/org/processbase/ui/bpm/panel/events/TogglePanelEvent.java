package org.processbase.ui.bpm.panel.events;

import com.vaadin.ui.Button;

public class TogglePanelEvent {
	private Button clickedButton;

	public void setClickedButton(Button clickedButton) {
		this.clickedButton = clickedButton;
	}

	public Button getClickedButton() {
		return clickedButton;
	}
}
