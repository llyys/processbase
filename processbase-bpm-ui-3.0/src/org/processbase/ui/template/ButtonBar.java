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

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

/**
 *
 * @author mgubaidullin
 */
public class ButtonBar extends HorizontalLayout {

    public ButtonBar() {
        super();
        initUI();
    }

    public void initUI() {
        setMargin(true, true, true, true);
        setSpacing(true);
//        setWidth("100%");
//        setSizeFull();
        setStyleName("buttonbar");
    }

    public void addButton(Button button){
        this.addComponent(button);
    }

    public void addButton(Button button,  int index){
        this.addComponent(button, index);
    }

    public void removeButton(Button button){
        this.removeComponent(button);
    }


}
