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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;

/**
 *
 * @author mgubaidullin
 */
public class TableExecButtonBar extends HorizontalLayout{

    public TableExecButtonBar() {
        super();
    }

    public void addButton(TableExecButton teb) {
        this.addComponent(teb);
    }
    public void addButton(TableExecButton teb, Alignment alignment) {
        this.addComponent(teb);
        this.setComponentAlignment(teb, alignment);
    }
}
