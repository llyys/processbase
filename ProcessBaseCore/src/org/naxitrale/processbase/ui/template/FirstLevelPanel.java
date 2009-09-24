/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.template;

import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import java.util.ResourceBundle;
import org.naxitrale.processbase.ProcessBase;

/**
 *
 * @author mgubaidullin
 */
public class FirstLevelPanel extends Panel implements TabSheet.SelectedTabChangeListener {

    protected TabSheet tabSheet = new TabSheet();
    protected ResourceBundle messages = ResourceBundle.getBundle("resources/MessagesBundle", ((ProcessBase) getApplication()).getCurrent().getLocale());

    public FirstLevelPanel() {
        setStyleName(Panel.STYLE_LIGHT);
        getContent().setStyleName("grey");
        setSizeFull();
//        getContent().setMargin(true);

        tabSheet.setStyleName("bar");
        tabSheet.setSizeFull();
//        tabSheet.setSizeUndefined();
        getContent().addComponent(tabSheet);
//        getContent().setExpandRatio(tabSheet, 1);
        tabSheet.addListener((TabSheet.SelectedTabChangeListener) this);
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        if (event.getTabSheet().getSelectedTab() instanceof TablePanel) {
            ((TablePanel) event.getTabSheet().getSelectedTab()).refreshTable();
        }

    }
}
