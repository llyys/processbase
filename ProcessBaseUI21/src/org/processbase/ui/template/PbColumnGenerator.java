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

