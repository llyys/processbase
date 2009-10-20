/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.portal;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.net.URLClassLoader;
import org.processbase.util.ProcessBaseClassLoader;

/**
 *
 * @author mgubaidullin
 */
public class MainPanel extends Panel implements Button.ClickListener {

    Button x = new Button("load", this);
    Button y = new Button("all", this);
    TextField className = new TextField("classname");
    Label label = new Label();
    VerticalLayout layout = null;

    public MainPanel() {
        super();
        initUI();
    }

    public void initUI() {
        setStyleName("blue");
        this.setSizeFull();
        layout = (VerticalLayout) this.getContent();
        layout.addComponent(className);
        layout.addComponent(x);
        layout.addComponent(y);
        layout.addComponent(label);

    }

    public void buttonClick(ClickEvent event) {
    }
}
