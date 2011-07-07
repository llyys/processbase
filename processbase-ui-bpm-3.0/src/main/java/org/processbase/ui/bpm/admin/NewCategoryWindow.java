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
package org.processbase.ui.bpm.admin;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class NewCategoryWindow extends PbWindow implements ClickListener {

    private ButtonBar buttons = new ButtonBar();
    private Button cancelBtn ;
    private Button applyBtn;
    private TextField categoryName;

    public NewCategoryWindow() {
        super();
    }

    public void initUI() {
        try {
            setCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("newCategory"));
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            categoryName = new TextField(ProcessbaseApplication.getCurrent().getPbMessages().getString("categoryName"));
            categoryName.setWidth("270px");
            addComponent(categoryName);

            cancelBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnCancel"), this);
            applyBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnSave"), this);
            
            buttons.addButton(applyBtn);
            buttons.setComponentAlignment(applyBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(applyBtn, 1);
            buttons.addButton(cancelBtn);
            buttons.setComponentAlignment(cancelBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(false);
            buttons.setHeight("30px");
            buttons.setWidth("100%");
            addComponent(buttons);
            setWidth("300px");
            setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                ProcessbaseApplication.getCurrent().getBpmModule().addCategory(categoryName.getValue().toString(), "", "", "");
                close();
            } else {
                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private void save() throws Exception {
    }
}
