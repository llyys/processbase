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

import com.vaadin.ui.TreeTable;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 *
 * @author mgubaidullin 
 */
public class TreeTablePanel extends WorkPanel implements Button.ClickListener, Window.CloseListener {

    protected TreeTable treeTable = new TreeTable();
    public int rowCount = 0;

    public TreeTablePanel() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        treeTable.setSizeFull();
        treeTable.setPageLength(15);
        treeTable.addStyleName("striped");
//        table.setSelectable(true);
//        table.setMultiSelect(false);
//        table.setImmediate(false);
        horizontalLayout.addComponent(treeTable, 0);
        horizontalLayout.setComponentAlignment(treeTable, Alignment.TOP_LEFT);
        horizontalLayout.setExpandRatio(treeTable, 1);
    }


    public void refreshTable() {
    }

    public TableLinkButton getExecBtn(String description, String iconName, Object t, String action) {
        return new TableLinkButton(description, iconName, t, this, action);
    }

    @Override
    public void buttonClick(ClickEvent event) {
    }

    @Override
    public void windowClose(CloseEvent e) {
        super.windowClose(e);
        refreshTable();
    }
}
