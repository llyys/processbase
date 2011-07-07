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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.ow2.bonita.facade.identity.Role;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class RoleWindow extends PbWindow implements ClickListener {

    private Role role = null;
    private ButtonBar buttons = new ButtonBar();
    private Button cancelBtn;
    private Button applyBtn;
    private TextField roleName;
    private TextField roleLabel;
    private TextArea roleDescription;

    public RoleWindow(Role role) {
        super();
        this.role = role;
    }

    public void initUI() {
        try {
            if (role == null) {
                setCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("newRole"));
            } else {
                setCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("role"));
            }
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            cancelBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnCancel"), this);
            applyBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnSave"), this);
            roleName = new TextField(ProcessbaseApplication.getCurrent().getPbMessages().getString("roleName"));
            roleLabel = new TextField(ProcessbaseApplication.getCurrent().getPbMessages().getString("roleLabel"));
            roleDescription = new TextArea(ProcessbaseApplication.getCurrent().getPbMessages().getString("roleDescription"));

            roleName.setWidth("270px");
            addComponent(roleName);
            roleLabel.setWidth("270px");
            addComponent(roleLabel);
            roleDescription.setWidth("270px");
            addComponent(roleDescription);

            if (role != null) {
                roleName.setValue(role.getName());
                roleLabel.setValue(role.getLabel());
                roleDescription.setValue(role.getDescription());
            }


            buttons.addButton(applyBtn);
            buttons.setComponentAlignment(applyBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(applyBtn, 1);
            buttons.addButton(cancelBtn);
            buttons.setComponentAlignment(cancelBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(false);
            buttons.setHeight("30px");
            buttons.setWidth("100%");
            addComponent(buttons);
            setWidth("310px");
            setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                if (role == null) {
                    ProcessbaseApplication.getCurrent().getBpmModule().addRole(roleName.getValue().toString(), roleLabel.getValue().toString(), roleDescription.getValue().toString());
                } else {
                    ProcessbaseApplication.getCurrent().getBpmModule().updateRoleByUUID(role.getUUID(), roleName.getValue().toString(), roleLabel.getValue().toString(), roleDescription.getValue().toString());
                }
                close();
            } else {
                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
