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
}
