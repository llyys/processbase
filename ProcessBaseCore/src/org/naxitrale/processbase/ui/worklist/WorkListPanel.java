/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.worklist;

import org.naxitrale.processbase.ui.template.FirstLevelPanel;

/**
 *
 * @author mgubaidullin
 */
public class WorkListPanel extends FirstLevelPanel {

    public WorkListPanel() {
        initUI();
    }

    private void initUI() {
        tabSheet.addTab(new TasksToDoPanel(), messages.getString("tabCaptionTaskInbox"), null);
        tabSheet.addTab(new ProcessesToStartPanel(), messages.getString("tabCaptionTaskNew"), null);
        tabSheet.addTab(new TasksDonePanel(), messages.getString("tabCaptionTaskFinished"), null);
    }
}
