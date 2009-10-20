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

    public TablePanel() {
        super();
        initUI();
    }

    public void initUI() {
        table.setSizeFull();
        table.setPageLength(19);
        horizontalLayout.addComponent(table);
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
