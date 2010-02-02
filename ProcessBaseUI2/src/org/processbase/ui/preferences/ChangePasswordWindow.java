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
package org.processbase.ui.preferences;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.ui.template.PbWindow;
import org.processbase.util.ldap.LdapUtils;
import org.processbase.util.ldap.User;

/**
 *
 * @author mgubaidullin
 */
public class ChangePasswordWindow extends PbWindow implements ClickListener {

    private User user = null;
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button cancelBtn = new Button(messages.getString("btnCancel"), this);
    private Button applyBtn = new Button(messages.getString("btnOK"), this);
    private TextField currentPassword = new TextField(messages.getString("fieldCurrentPassword"));
    private TextField newPassword1 = new TextField(messages.getString("fieldNewPassword"));
    private TextField newPassword2 = new TextField(messages.getString("fieldConfirmPassword"));
    private FormLayout formLayout = new FormLayout();

    public ChangePasswordWindow(User user) {
        super();
        this.user = user;
    }

    public void exec() {
        try {
            setCaption(messages.getString("captionChangePassword"));
            setContent(formLayout);
            formLayout.setMargin(true);
            formLayout.setSpacing(true);
            formLayout.setSizeUndefined();
            formLayout.addComponent(currentPassword);
            formLayout.addComponent(newPassword1);
            formLayout.addComponent(newPassword2);

            currentPassword.setSecret(true);
            newPassword1.setSecret(true);
            newPassword2.setSecret(true);

            buttons.setSpacing(true);
            buttons.addComponent(applyBtn);
            buttons.addComponent(cancelBtn);
            formLayout.addComponent(buttons);
            setResizable(false);
            setModal(true);
        } catch (Exception ex) {
            Logger.getLogger(ChangePasswordWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }

    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(cancelBtn)) {
            } else {
                if (user.getPassword().equals(currentPassword.getValue().toString())) {
                    if (!currentPassword.getValue().toString().equals(newPassword1.getValue().toString())) {
                        if (newPassword1.getValue().toString().equals(newPassword2.getValue().toString())) {
                            LdapUtils ldapUtils = new LdapUtils(user.getUid(), null, user.getPassword());
                            ldapUtils.changePassword(user.getDn(), newPassword1.getValue().toString());
                            user.setPassword(newPassword1.getValue().toString());
                        } else {
                            throw new Exception(messages.getString("changePwdException1"));
                        }
                    } else {
                        throw new Exception(messages.getString("changePwdException2"));
                    }
                } else {
                    throw new Exception(messages.getString("changePwdException3"));
                }
            }
            close();
        } catch (Exception ex) {
            Logger.getLogger(ChangePasswordWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }

    }
}
