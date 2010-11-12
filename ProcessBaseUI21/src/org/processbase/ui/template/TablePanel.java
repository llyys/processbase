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
package org.processbase.ui.template;

import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 *
 * @author mgubaidullin
 */
public class TablePanel extends WorkPanel implements Button.ClickListener, Window.CloseListener {

    protected Table table = new Table();

    public TablePanel(PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
        initUI();
    }

    public void initUI() {
        table.setSizeFull();
        table.setPageLength(15);
        table.addStyleName("striped");
        horizontalLayout.addComponent(table, 0);
        horizontalLayout.setComponentAlignment(table, Alignment.TOP_LEFT);
        horizontalLayout.setExpandRatio(table, 1);
    }

    public void initTableUI() {
    }

    public void refreshTable() {
    }

    public TableExecButton getExecBtn(String description, String iconName, Object t, String action) {
        return new TableExecButton(description, iconName, t, this, action);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton().equals(refreshBtn)) {
            refreshTable();
        }
    }

    @Override
    public void windowClose(CloseEvent e) {
        super.windowClose(e);
        refreshTable();
    }
}
