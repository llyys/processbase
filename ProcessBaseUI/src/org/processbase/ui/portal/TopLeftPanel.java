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

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 *
 * @author mgubaidullin
 */
public class TopLeftPanel extends Panel {

    public TopLeftPanel() {
        super();
        initUI();
    }

    public void initUI() {
        setStyleName("blue");
        this.setSizeFull();
        addComponent(new Label("PanelTopLeft"));
        
    }
}
