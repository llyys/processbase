/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase;

import org.processbase.ui.template.WorkPanel;

/**
 *
 * @author mgubaidullin
 */
public class PanelMenuItem {

    private String name;
    private Class panelClass;
    private WorkPanel workPanel;

    public PanelMenuItem(String name, Class panelClass, WorkPanel workPanel) {
        this.name = name;
        this.panelClass = panelClass;
        this.workPanel = workPanel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getPanelClass() {
        return panelClass;
    }

    public void setPanelClass(Class panelClass) {
        this.panelClass = panelClass;
    }

    public WorkPanel getWorkPanel() {
        return workPanel;
    }

    public void setWorkPanel(WorkPanel workPanel) {
        this.workPanel = workPanel;
    }

    

}
