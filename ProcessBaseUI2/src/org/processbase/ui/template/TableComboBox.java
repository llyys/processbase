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

import com.vaadin.data.Item;
import com.vaadin.ui.ComboBox;

/**
 *
 * @author mgubaidullin
 */
public class TableComboBox extends ComboBox {

    private Object tableValue = null;

    public TableComboBox(Object tv) {
        super();
        tableValue = tv;
    }

    public Object getTableValue() {
        return tableValue;
    }

    public void setTableValue(Object tableValue) {
        this.tableValue = tableValue;
    }

    void setContainerDataSource(String[] fileTypes) {
        this.addContainerProperty("id", String.class, null);
        this.addContainerProperty("name", String.class, null);
        for (String ft : fileTypes) {
            Item item = this.addItem(ft);
            item.getItemProperty("id").setValue(ft);
            item.getItemProperty("name").setValue(ft);
        }
    }
}
