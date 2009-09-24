/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.portal;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 *
 * @author mgubaidullin
 */
public class MainPanel extends Panel {

    private GridLayout grid = new GridLayout(2, 2);
    private TopLeftPanel topLeftPanel = new TopLeftPanel();
    private Panel panel2 = new Panel();
    private Panel panel3 = new Panel();
    private Panel panel4 = new Panel();

    public MainPanel() {
        super();
        initUI();
    }

    public void initUI() {
        setStyleName("blue");
        this.setSizeFull();
        grid.setSizeFull();
        grid.setMargin(true);
        grid.setSpacing(true);
 
        panel2.addComponent(new Label("2222"));
        panel3.addComponent(new Label("3333"));
        panel4.addComponent(new Label("4444"));

        panel2.setSizeFull();
        panel3.setSizeFull();
        panel4.setSizeFull();
        grid.addComponent(topLeftPanel, 0, 0);
        grid.addComponent(panel2, 1, 0);
        grid.addComponent(panel3, 0, 1);
        grid.addComponent(panel4, 1, 1);
        setContent(grid);
    }
}
