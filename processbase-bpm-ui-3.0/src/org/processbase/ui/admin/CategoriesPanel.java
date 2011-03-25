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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.util.Set;
import org.processbase.core.Constants;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.Category;
import org.processbase.ui.Processbase;

/**
 *
 * @author marat gubaidullin
 */
public class CategoriesPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public CategoriesPanel() {
        super();
    }


    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("name", TableLinkButton.class, null, ((Processbase)getApplication()).getMessages().getString("tableCaptionCategory"), null, null);
        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("uuid", String.class, null, ((Processbase)getApplication()).getMessages().getString("tableCaptionUUID"), null, null);
//        table.setColumnWidth("uuid", 50);
//        table.addContainerProperty("deployedBy", String.class, null, ProcessbasePortlet.getCurrent().messages.getString("tableCaptionDeployedBy"), null, null);
//        table.addContainerProperty("deployedDate", Date.class, null, ProcessbasePortlet.getCurrent().messages.getString("tableCaptionDeployedDate"), null, null);
//        table.addGeneratedColumn("deployedDate", new PbColumnGenerator());
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            Set<Category> categories = ((Processbase)getApplication()).getBpmModule().getAllCategories();

            for (Category category : categories) {
                Item woItem = table.addItem(category);
                TableLinkButton teb = new TableLinkButton(category.getName(), category.getName(), null, category, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("uuid").setValue(category.getUUID());
//                woItem.getItemProperty("deployedBy").setValue(pd.getDeployedBy());
//                woItem.getItemProperty("deployedDate").setValue(pd.getDeployedDate());
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            TableLinkButton execBtn = (TableLinkButton) event.getButton();
                if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                try {
                    CategoryWindow categoryWindow = new CategoryWindow((Category) execBtn.getTableValue());
                    getApplication().getMainWindow().addWindow(categoryWindow);
                    categoryWindow.initUI();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            }
        }
    }

    
}
