package org.processbase.mobile.client;

import de.enough.polish.ui.Form;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.Locale;

/**
 *
 * @author mgubaidullin
 */
public class LoginForm extends Form {
    //#style forminput
    public TextField username = new TextField(Locale.get("loginform.username"), "", 20, TextField.ANY);
    //#style forminput
    public TextField password = new TextField(Locale.get("loginform.password"), "", 20, TextField.PASSWORD);
    //#style forminput
    public TextField url = new TextField(Locale.get("loginform.url"), "", 1000, TextField.ANY);

    public LoginForm() {
        //#style loginform
        super(Locale.get("loginform.title"));
        username.setText(ProcessBaseMobile.processBaseBO.getUsername());
        this.append(username);
        password.setString("admin");
        this.append(password);
//        if (ProcessBaseMobile.processBaseBO.getUrl() == null) {
            url.setText(ProcessBaseMobile.processBaseBO.getUrl());
            this.append(url);
//        }
        if (ProcessBaseMobile.processBaseBO.getUsername() != null) {
            this.focus(password);
        }
    }
}
