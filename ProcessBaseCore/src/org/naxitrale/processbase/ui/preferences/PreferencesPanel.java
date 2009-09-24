/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.preferences;

import org.naxitrale.processbase.ui.acl.*;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import java.util.Vector;
import org.naxitrale.processbase.ProcessBase;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pbuser;
import org.naxitrale.processbase.ui.portal.HelpPanel;
import org.naxitrale.processbase.ui.template.ACLFieldFactory;
import org.naxitrale.processbase.ui.template.FirstLevelPanel;

/**
 *
 * @author mgubaidullin
 */
public class PreferencesPanel extends FirstLevelPanel {

    public PreferencesPanel() {
        initUI();
    }

    public void initUI() {
        tabSheet.addTab(new CurrentUserPanel(), "Текущий пользователь", null);
    }
}
