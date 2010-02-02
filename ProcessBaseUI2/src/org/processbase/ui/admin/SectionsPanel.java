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
package org.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import java.util.ArrayList;
import org.processbase.util.Constants;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.ui.template.TablePanel;
import org.processbase.util.db.HibernateUtil;
import org.processbase.util.db.PbSection;

/**
 *
 * @author mgubaidullin
 */
public class SectionsPanel extends TablePanel implements Button.ClickListener {

    protected HibernateUtil hutil = new HibernateUtil();
    protected Button newBtn = new Button(messages.getString("btnCreate"), this);

    public SectionsPanel() {
        super();
        buttonBar.addComponent(newBtn);
        buttonBar.setComponentAlignment(newBtn, Alignment.MIDDLE_LEFT);
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("id", Long.class, null, "id", null, null);
        table.setColumnWidth("id", 50);
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionSectionName"), null, null);
        table.setColumnWidth("name", 300);
        table.addContainerProperty("desc", String.class, null, messages.getString("tableCaptionSectionDesc"), null, null);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 50);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            ArrayList<PbSection> sections = hutil.findPbSections();
            for (PbSection section : sections) {
                Item woItem = table.addItem(section);
                woItem.getItemProperty("id").setValue(section.getId());
                woItem.getItemProperty("name").setValue(section.getSectionName());
                woItem.getItemProperty("desc").setValue(section.getSectionDesc());
                TableExecButtonBar tebb = new TableExecButtonBar();
//            tebb.addButton((TableExecButton) addResourceButton(pd));
                tebb.addButton(new TableExecButton(messages.getString("btnEdit"), "icons/edit.png", section, this, Constants.ACTION_EDIT));
                tebb.addButton(new TableExecButton(messages.getString("btnDelete"), "icons/cancel.png", section, this, Constants.ACTION_DELETE));
                woItem.getItemProperty("actions").setValue(tebb);
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            TableExecButton execBtn = (TableExecButton) event.getButton();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                try {
                    hutil.deletePbSection((PbSection) execBtn.getTableValue());
                    refreshTable();
                    showWarning(messages.getString("deletedSuccessfull"));
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_EDIT)) {
                try {
                    SectionWindow sectionWindow = new SectionWindow((PbSection) execBtn.getTableValue());
                    sectionWindow.exec();
                    sectionWindow.addListener(new Window.CloseListener() {

                        public void windowClose(CloseEvent e) {
                            refreshTable();
                        }
                    });
                    getApplication().getMainWindow().addWindow(sectionWindow);
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            }
        } else if (event.getButton().equals(newBtn)) {
            SectionWindow sectionWindow = new SectionWindow(null);
            sectionWindow.exec();
            sectionWindow.addListener(new Window.CloseListener() {

                public void windowClose(CloseEvent e) {
                    refreshTable();
                }
            });
            getApplication().getMainWindow().addWindow(sectionWindow);
        }
    }
}
