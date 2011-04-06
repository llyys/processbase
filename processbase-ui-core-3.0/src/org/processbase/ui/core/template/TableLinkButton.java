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
package org.processbase.ui.core.template;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

/**
 *
 * @author mgubaidullin
 */
public class TableLinkButton extends Button {

    private Object tableValue = null;
    private String action = null;

    public TableLinkButton(String caption, String description, String iconName, Object tv, ClickListener cl) {
        super();
        this.setCaption(caption);
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(BaseTheme.BUTTON_LINK);
    }

    public TableLinkButton(String description, String iconName, Object tv, ClickListener cl) {
        super();
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(BaseTheme.BUTTON_LINK);
        this.setIcon(new ThemeResource(iconName));
    }

    public TableLinkButton(String description, String iconName, Object tv, ClickListener cl, String action) {
        super();
        this.action = action;
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(BaseTheme.BUTTON_LINK);
        this.setIcon(new ThemeResource(iconName));
    }
    
    public TableLinkButton(String caption, String description, String iconName, Object tv, ClickListener cl, String action) {
        super();
        this.action = action;
        this.setCaption(caption);
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(BaseTheme.BUTTON_LINK);
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
