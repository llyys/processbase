/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.template;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

/**
 *
 * @author mgubaidullin
 */
public class ButtonBar extends HorizontalLayout {

    public ButtonBar() {
        super();
        initUI();
    }

    public void initUI() {
        setMargin(false, false, true, false);
        setSpacing(true);
//        setWidth("100%");
//        setSizeFull();
//        setStyleName("grey");
    }

    public void addButton(Button button){
        this.addComponent(button);
    }

    public void addButton(Button button,  int index){
        this.addComponent(button, index);
    }

    public void removeButton(Button button){
        this.removeComponent(button);
    }


}
