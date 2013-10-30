package org.processbase.ui.bpm.admin;

import java.util.List;

import org.apache.log4j.Logger;
import org.ow2.bonita.facade.identity.User;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

/**
 * Select user panel.
 */
public class SelectUsersPanel extends TablePanel implements
		Button.ClickListener, Window.CloseListener {

	/** Serial version UID. */
	private static final long serialVersionUID = 177192143551127066L;

	private static final Logger LOG = Logger.getLogger(SelectUsersPanel.class);

	private UserSelectedListener listener;

	public interface UserSelectedListener {
		void userSelected(User user);
	}

	public SelectUsersPanel() {
		super();
	}

	@Override
	public void initUI() {
		super.initUI();
		table.addContainerProperty(
				"username",
				TableLinkButton.class,
				null,
				ProcessbaseApplication.getCurrent().getPbMessages()
						.getString("tableCaptionUsername"), null, null);
		table.addContainerProperty(
				"lastname",
				String.class,
				null,
				ProcessbaseApplication.getCurrent().getPbMessages()
						.getString("tableCaptionLastname"), null, null);
		table.addContainerProperty(
				"firstname",
				String.class,
				null,
				ProcessbaseApplication.getCurrent().getPbMessages()
						.getString("tableCaptionFirstname"), null, null);
		table.addContainerProperty(
				"email",
				String.class,
				null,
				ProcessbaseApplication.getCurrent().getPbMessages()
						.getString("tableCaptionEmail"), null, null);
		table.setImmediate(true);
	}

	@Override
	public void refreshTable() {
		try {
			table.removeAllItems();

			List<User> users = ProcessbaseApplication.getCurrent()
					.getBpmModule().getAllUsers();

			for (User user : users) {
				Item woItem = table.addItem(user);
				TableLinkButton teb = new TableLinkButton(user.getUsername(),
						"", null, user, this, Constants.ACTION_OPEN);
				woItem.getItemProperty("username").setValue(teb);
				woItem.getItemProperty("lastname").setValue(user.getLastName());
				woItem.getItemProperty("firstname").setValue(
						user.getFirstName());
				woItem.getItemProperty("email").setValue(
						user.getProfessionalContactInfo() != null ? user
								.getProfessionalContactInfo().getEmail() : "");
			}

			table.setSortContainerPropertyId("username");
			table.setSortAscending(false);
			table.sort();

		} catch (Exception e) {
			LOG.warn("in refreshTable", e);
		}
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() instanceof TableLinkButton) {
			TableLinkButton execBtn = (TableLinkButton) event.getButton();
			User user = (User) execBtn.getTableValue();
			if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
				if (listener != null) {
					listener.userSelected(user);
				}
			}
		}
	}

	public void setUserSelectedListener(UserSelectedListener listener) {
		this.listener = listener;
	}

}
