/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.admin;

import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import org.processbase.ui.template.FirstLevelPanel;

/**
 *
 * @author mgubaidullin
 */
public class AdminPanel extends FirstLevelPanel {

    public AdminPanel() {
        initUI();
    }

    private void initUI() {
        tabSheet.addTab(new UsersPanel(), messages.getString("tabCaptionACLUsers"), null);
        tabSheet.addTab(new GroupsPanel(), messages.getString("tabCaptionACLGroups"), null);
        tabSheet.addTab(new RolesPanel(), messages.getString("tabCaptionACLRoles"), null);
        tabSheet.addTab(new OrgUnitsPanel(), messages.getString("tabCaptionACLOrg"), null);
        tabSheet.addTab(new JarsPanel(), messages.getString("tabCaptionJars"), null);
//        tabSheet.addTab(new JobsPanel(), messages.getString("tabCaptionJobs"), null);
    }

    @Override
    public void selectedTabChange(SelectedTabChangeEvent event) {
        super.selectedTabChange(event);
        if (event.getTabSheet().getSelectedTab() instanceof OrgUnitsPanel) {
            ((OrgUnitsPanel) event.getTabSheet().getSelectedTab()).refreshTree();
        }
    }
}
