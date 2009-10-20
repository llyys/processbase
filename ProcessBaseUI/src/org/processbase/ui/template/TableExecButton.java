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

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;

/**
 *
 * @author mgubaidullin
 */
public class TableExecButton extends Button {

    private Object tableValue = null;
    private String action = null;

    public TableExecButton(String caption, String description, String iconName, Object tv, ClickListener cl) {
        super();
        this.setCaption(caption);
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(Button.STYLE_LINK);
    }

    public TableExecButton(String description, String iconName, Object tv, ClickListener cl) {
        super();
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(Button.STYLE_LINK);
        this.setIcon(new ThemeResource(iconName));
    }

    public TableExecButton(String description, String iconName, Object tv, ClickListener cl, String action) {
        super();
        this.action = action;
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(Button.STYLE_LINK);
        this.setIcon(new ThemeResource(iconName));
    }
    
    public TableExecButton(String caption, String description, String iconName, Object tv, ClickListener cl, String action) {
        super();
        this.action = action;
        this.setCaption(caption);
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(Button.STYLE_LINK);
    }

    public Object getTableValue() {
        return tableValue;
    }

    public void setTableValue(Object tableValue) {
        this.tableValue = tableValue;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    
}
