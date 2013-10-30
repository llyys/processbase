package org.processbase.ui.bpm.identity;

import java.util.List;

import org.ow2.bonita.facade.identity.User;
import org.processbase.ui.bpm.identity.sync.UserRolesSync;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.PagedTablePanel;
import org.processbase.ui.core.template.TableLinkButton;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

public class UsersPanel extends PagedTablePanel implements Button.ClickListener {

	private static final long serialVersionUID = -2633662219483920976L;

	public UsersPanel() {
		super();
	}

	@Override
	public void initUI() {
		super.initUI();

		table.addContainerProperty("username", TableLinkButton.class, null,
				getText("tableCaptionUsername"), null, null);
		table.addContainerProperty("lastname", String.class, null,
				getText("tableCaptionLastname"), null, null);
		table.addContainerProperty("firstname", String.class, null,
				getText("tableCaptionFirstname"), null, null);
		table.addContainerProperty("email", String.class, null,
				getText("tableCaptionEmail"), null, null);
		table.addContainerProperty("actions", TableLinkButton.class, null,
				getText("tableCaptionActions"), null, null);
		setInitialized(true);
	}

	@Override
	public int load(int startPosition, int maxResults) {
		int results = 0;

		try {
			table.removeAllItems();

			BPMModule module = ProcessbaseApplication.getCurrent()
					.getBpmModule();

			List<User> users = module.getUsers(startPosition, maxResults);
			results = users.size();

			for (User user : users) {
				Item woItem = table.addItem(user);
				TableLinkButton teb = new TableLinkButton(
						user.getUsername(), "", null, user, this,
						Constants.ACTION_OPEN);
				woItem.getItemProperty("username").setValue(teb);
				woItem.getItemProperty("lastname").setValue(
						user.getLastName());
				woItem.getItemProperty("firstname").setValue(
						user.getFirstName());
				woItem.getItemProperty("email").setValue(
						user.getProfessionalContactInfo() != null ? user
								.getProfessionalContactInfo().getEmail()
								: "");
				TableLinkButton tlb = new TableLinkButton(
						ProcessbaseApplication.getCurrent().getPbMessages()
								.getString("btnDelete"),
						"icons/cancel.png", user, this,
						Constants.ACTION_DELETE);
				woItem.getItemProperty("actions").setValue(tlb);
			}
			table.setSortContainerPropertyId("username");
			table.setSortAscending(false);
			table.sort();
		} catch (Exception ex) {
			ex.printStackTrace();
			showError(ex.getMessage());
		}
		
		return results;
	}

	public void buttonClick(ClickEvent event) {
		if (event.getButton() instanceof TableLinkButton) {
			TableLinkButton execBtn = (TableLinkButton) event.getButton();
			User user = (User) execBtn.getTableValue();
			if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
				try {
					removeUser(user);
				} catch (Exception ex) {
					ex.printStackTrace();
					showError(ex.getMessage());
				}
			} else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
				
				UserWindow nuw = new UserWindow(user);
				nuw.addListener((Window.CloseListener) this);
				getWindow().addWindow(nuw);
				nuw.initUI();
			}
		}
	}

	private void removeUser(final User user) {

		ConfirmDialog.show(getApplication().getMainWindow(),
				getText("windowCaptionConfirm"), getText("removeUser") + "?",
				getText("btnYes"), getText("btnNo"),
				new ConfirmDialog.Listener() {

					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							try {
								ProcessbaseApplication.getCurrent()
										.getBpmModule()
										.removeUserByUUID(user.getUUID());
								table.removeItem(user);
							} catch (Exception ex) {
								showError(ex.getMessage());
								ex.printStackTrace();
							}
						}
					}
				});
	}

}
