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
package org.processbase.ui.bpm.identity;

import java.util.List;

import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.identity.Role;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.PagedTablePanel;
import org.processbase.ui.core.template.TableLinkButton;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

/**
 * 
 * @author marat gubaidullin
 */
public class RolesPanel extends PagedTablePanel implements Button.ClickListener {

	/** Serial version UID. */
	private static final long serialVersionUID = -3686557275708610705L;

	public RolesPanel() {
		super();
	}

	@Override
	public void initUI() {
		super.initUI();
		table.addContainerProperty("name", TableLinkButton.class, null,
				getText("tableCaptionName"), null, null);
		table.setColumnExpandRatio("name", 1);
		table.addContainerProperty("label", String.class, null,
				getText("tableCaptionLabel"), null, null);
		table.addContainerProperty("description", String.class, null,
				getText("tableCaptionDescription"), null, null);
		table.addContainerProperty("actions", TableLinkButton.class, null,
				getText("tableCaptionActions"), null, null);
		table.setImmediate(true);

		setInitialized(true);
	}

	@Override
	public int load(int startPosition, int maxResults) {
		int results = 0;

		try {
			table.removeAllItems();
			List<Role> roles = ProcessbaseApplication.getCurrent()
					.getBpmModule().getRoles(startPosition, maxResults);
			results = roles.size();

			for (Role role : roles) {
				Item woItem = table.addItem(role);
				TableLinkButton teb = new TableLinkButton(role.getName(), "",
						null, role, this, Constants.ACTION_OPEN);
				woItem.getItemProperty("name").setValue(teb);
				woItem.getItemProperty("label").setValue(role.getLabel());
				woItem.getItemProperty("description").setValue(
						role.getDescription());
				if (!role.getName().equals(IdentityAPI.ADMIN_ROLE_NAME)
						&& !role.getName().equals(IdentityAPI.USER_ROLE_NAME)
						&& !role.getDescription().startsWith("AUTO IMPORTED")) {
					TableLinkButton tlb = new TableLinkButton(
							ProcessbaseApplication.getCurrent().getPbMessages()
									.getString("btnDelete"),
							"icons/cancel.png", role, this,
							Constants.ACTION_DELETE);
					woItem.getItemProperty("actions").setValue(tlb);
				}
			}
			table.setSortContainerPropertyId("name");
			table.setSortAscending(false);
			table.sort();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return results;
	}

	public void buttonClick(ClickEvent event) {
		if (event.getButton() instanceof TableLinkButton) {
			TableLinkButton execBtn = (TableLinkButton) event.getButton();
			Role role = (Role) execBtn.getTableValue();
			if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
				try {
					removeRole(role);
				} catch (Exception ex) {
					ex.printStackTrace();
					showError(ex.getMessage());
					throw new RuntimeException(ex);
				}
			} else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
				RoleWindow nrw = new RoleWindow(role);
				nrw.addListener((Window.CloseListener) this);
				getWindow().addWindow(nrw);
				nrw.initUI();
			}
		}
	}

	private void removeRole(final Role role) {
		ConfirmDialog.show(getApplication().getMainWindow(),
				getText("windowCaptionConfirm"), getText("removeRole") + "?",
				getText("btnYes"), getText("btnNo"),
				new ConfirmDialog.Listener() {

					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							try {
								ProcessbaseApplication.getCurrent()
										.getBpmModule()
										.removeRoleByUUID(role.getUUID());
								table.removeItem(role);
							} catch (Exception ex) {
								showError(ex.getMessage());
								ex.printStackTrace();
								throw new RuntimeException(ex);
							}
						}
					}
				});
	}
}
