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

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import java.util.Date;

/**
 *
 * @author mgubaidullin
 */
public class PbColumnGenerator implements Table.ColumnGenerator {

    public Component generateCell(Table source, Object itemId, Object columnId) {
        Property prop = source.getItem(itemId).getItemProperty(columnId);
        Label label = new Label("");
        if (prop.getType().equals(Date.class)) {
            if (prop.getValue() != null) {
                label = new Label(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", new Object[]{(Date) prop.getValue()}));
            } 
            label.addStyleName("column-type-date");
            return label;
        }
        return label;
    }
}

