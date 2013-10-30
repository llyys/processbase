package org.processbase.ui.bpm.admin;

import org.ow2.bonita.facade.identity.User;
import org.processbase.ui.core.template.PbWindow;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Users window.
 */
public class SelectUserWindow extends PbWindow {

	/** Serial version UID. */
	private static final long serialVersionUID = -6648408370161523491L;

	private SelectUsersPanel usersPanel;
	
	private SelectUsersPanel.UserSelectedListener userSelectedListener;

	public void initUI() {

		setWidth("600px");
		setHeight("400px");
		setModal(true);
		setResizable(false);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setStyleName(Reindeer.LAYOUT_WHITE);
		layout.setSizeFull();

		usersPanel = new SelectUsersPanel();
		usersPanel.initUI();
		usersPanel.refreshTable();
		usersPanel.setUserSelectedListener(new SelectUsersPanel.UserSelectedListener() {
			
			public void userSelected(User user) {
				close();
				if(userSelectedListener != null){
					userSelectedListener.userSelected(user);
				}
			}
		});
		
		layout.addComponent(usersPanel);

		setContent(layout);

	}

	public void setUserSelectedListener(
			SelectUsersPanel.UserSelectedListener userSelectedListener) {
		this.userSelectedListener = userSelectedListener;
	}

}
